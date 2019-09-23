package tj.sodiq.jsonapp.services;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.apollographql.apollo.ApolloSubscriptionCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import tj.sodiq.jsonapp.MessageSentSubscription;
import tj.sodiq.jsonapp.MessageViewActivity;
import tj.sodiq.jsonapp.MyApolloClient;
import tj.sodiq.jsonapp.R;

public class SubscriptionService extends Service {

    private static final String TAG = SubscriptionService.class.getSimpleName();

    private static final String CHANNEL_3_ID = "channel3";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.e(TAG, "Subscription onStartCommand: ");

        subscriptionListener();

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    void subscriptionListener (){

        final Intent intent = new Intent(SubscriptionService.this, MessageViewActivity.class);

        new Thread(new Runnable() {
            @Override
            public void run() {

                MessageSentSubscription subscription = MessageSentSubscription
                        .builder()
                        .build();

                MyApolloClient
                        .getMyApolloClient(getApplicationContext())
                        .subscribe(subscription)
                        .execute(new ApolloSubscriptionCall.Callback<MessageSentSubscription.Data>() {
                            @Override
                            public void onResponse(@NotNull final Response<MessageSentSubscription.Data> response) {

                                if (response.errors().size() > 0){
                                    Log.e("Ошибка сабскрипшна", response.errors().toString());
                                    return;
                                }

                                int idSub = response.data().messageSent().ownerId();

                                intent.putExtra("idSub", idSub);

                                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0,intent,PendingIntent.FLAG_ONE_SHOT);

                                Log.e("Subscription", "Added successfully");

                                NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_3_ID)
                                        .setSmallIcon(R.drawable.ic_message)
                                        .setContentTitle("Message")
                                        .setContentText("You have new message")
                                        .setPriority(NotificationCompat.PRIORITY_MAX)
                                        .setAutoCancel(true)
                                        .setOngoing(true)
                                        .setContentIntent(pendingIntent);

                                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
                                notificationManager.notify(2, notification.build());

                                MessageViewActivity.RequestReceiver requestReceiver = new MessageViewActivity.RequestReceiver();

                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {
                                Log.e("Apollo Subscription", "Failure");
                                e.printStackTrace();
                            }

                            @Override
                            public void onCompleted() {
                                Log.e("Apollo subscription", "Completed");
                            }
                        });

            }
        }).start();

    }
}
