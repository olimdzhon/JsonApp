package tj.sodiq.jsonapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.apollographql.apollo.ApolloClient;
import com.apollographql.apollo.response.CustomTypeAdapter;
import com.apollographql.apollo.response.CustomTypeValue;
import com.apollographql.apollo.subscription.WebSocketSubscriptionTransport;

import java.io.IOException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import java.util.Date;

import java.util.HashSet;


import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import okhttp3.logging.HttpLoggingInterceptor;
import tj.sodiq.jsonapp.type.CustomType;

import static android.content.Context.MODE_PRIVATE;

public class MyApolloClient {

    public static final String BASE_URL = "http://10.0.3.2:4001/graphql";
    private static ApolloClient myApolloClient;

    public static ApolloClient getMyApolloClient(final Context context) {

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(loggingInterceptor);
        builder.addInterceptor(new Interceptor() {

            @Override
            public Response intercept(Chain chain) throws IOException {

                SharedPreferences pref1 = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);
                String cookies1 = pref1.getString("cookies", "No cookie");

                final Request request = chain.request()
                        .newBuilder()
                        .addHeader("Cookie", cookies1)
                        .build();

                Response originalResponse = chain.proceed(request);

                if (!originalResponse.headers("Set-Cookie").isEmpty()) {
                    HashSet<String> cookies = new HashSet<>();

                    for (String header : originalResponse.headers("Set-Cookie")) {

                        SharedPreferences pref = context.getSharedPreferences(context.getString(R.string.preference_file_key), MODE_PRIVATE);

                        if (pref != null) {

                            SharedPreferences.Editor editor = pref.edit();
                            header = header.substring(0, header.indexOf(";"));
                            editor.putString("cookie", header);
                            editor.apply();

                        }

                        cookies.add(header);

                        Log.e("cookies", cookies.toString());
                    }

                }

                return originalResponse;
            }
        });

        OkHttpClient okHttpClient = builder.build();

        myApolloClient = ApolloClient.builder()
                .serverUrl(BASE_URL)
                .okHttpClient(okHttpClient)
                .subscriptionTransportFactory(new WebSocketSubscriptionTransport.Factory("ws://10.0.3.2:4001/subscriptions", okHttpClient))
                .addCustomTypeAdapter(CustomType.DATE, new CustomTypeAdapter<Date>() {

                    @Override
                    public Date decode(CustomTypeValue value) {
                        try {
                            String dateString = value.value.toString();
                            String specialPrefix = "$Date(";
                            if (dateString.substring(0, specialPrefix.length()).equals(specialPrefix)) {
                                dateString = dateString.substring(specialPrefix.length(), dateString.length() - 1);
                            }
                            DateFormat javascriptDateFormat = new SimpleDateFormat("EE MMM d y H:m:s 'GMT'Z (zz)");
                            return javascriptDateFormat.parse(dateString);

                        } catch (ParseException e) {
                            e.printStackTrace();
                            return new Date();
//                            throw new RuntimeException(e);
                        }
                    }

                    @Override
                    public CustomTypeValue encode(Date value) {
                        return new CustomTypeValue.GraphQLString("$Date(" + value.toString() + ")");
                    }
                })
                .build();
        return myApolloClient;
    }
}
