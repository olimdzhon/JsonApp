package tj.sodiq.jsonapp;

import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.widget.Toast;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import tj.sodiq.jsonapp.type.Crop;

public class UploadService extends Service {

    private static final String TAG = UploadService.class.getSimpleName();

    public static final String CHANNEL_2_ID = "channel2";

    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String uri = intent.getStringExtra("resultUri");
        Uri uriParse = Uri.parse(uri);
        someTask(uriParse);



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

    void someTask(final Uri resultUri){

        final int progressMax = 100;

        Intent intent = new Intent(this, Main2Activity.class);
        intent.putExtra("From", "notifyFrag");

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        final NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANNEL_2_ID)
                .setSmallIcon(R.drawable.ic_one)
                .setContentTitle("Upload")
                .setContentText("Upload in progress")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setAutoCancel(true)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setProgress(progressMax, 0, false);

        final NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getApplicationContext());
        notificationManager.notify(1, notification.build());

        new Thread(new Runnable() {
            @Override
            public void run() {
                String responseFilename = uploadImage(resultUri.getPath());

                Crop crop = Crop
                        .builder()
                        .attachedTo("user")
                        .attachFileName(responseFilename)
                        .height("400")
                        .rotate("0")
                        .scaleX("1")
                        .scaleY("1")
                        .width("400")
                        .x("0")
                        .y("0")
                        .build();

                ProfileCropMutation mutation = ProfileCropMutation
                        .builder()
                        .crop(crop)
                        .build();

                MyApolloClient
                        .getMyApolloClient(getApplicationContext())
                        .mutate(mutation)
                        .enqueue(new ApolloCall.Callback<ProfileCropMutation.Data>() {
                            @Override
                            public void onResponse(@NotNull final Response<ProfileCropMutation.Data> response) {

                                Log.e("Added successfully", "Added successfully");

//
//                                private void runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//
//                                        if (response.errors().size() > 0) {
//                                            Toast.makeText(getApplicationContext(), "Ошибка сохранения", Toast.LENGTH_SHORT).show();
//                                            Log.e("Ошибка сохранения", "Ошибка сохранения");
//                                            return;
//                                        }
//
//                                        Toast.makeText(getApplicationContext(), "Added successfully", Toast.LENGTH_SHORT).show();
//                                        Log.e("Added successfully", "Added successfully");
//
//                                    }
//                                });
                            }

                            @Override
                            public void onFailure(@NotNull ApolloException e) {

                                Log.e("Add Failure", "Add Failure");

                                Toast.makeText(getApplicationContext(), "Add Failure", Toast.LENGTH_SHORT).show();

                            }
                        });

                        notification.setContentText("Upload finished")
                                .setProgress(0,0,false)
                                .setOngoing(false);
                        notificationManager.notify(1, notification.build());

            stopSelf();
            }
        }).start();

    }

    public static String uploadImage(String sourceImageFile) {

        try {

            File sourceFile = new File(sourceImageFile);

            Log.e(TAG, "File...::::" + sourceFile + " : " + sourceFile.exists());

            final MediaType MEDIA_TYPE = sourceImageFile.endsWith("png") ?
                    MediaType.parse("image/png") : MediaType.parse("image/jpeg");

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("file", sourceFile.getName(), RequestBody.create(MEDIA_TYPE, sourceFile))
                    .build();

            Request request = new Request.Builder()
                    .url("http://10.0.3.2:4001/api/upload?type=attach-file")
                    .post(requestBody)
                    .build();

            Log.e("request", "request: " + request);

            OkHttpClient client = new OkHttpClient();
            okhttp3.Response response = client.newCall(request).execute();

            Log.e("response", "response: " + response);

            String respBody = response.body().string();

            Log.e("respBody", "respBody: " + respBody);

            return respBody;

        } catch (UnknownHostException | UnsupportedEncodingException e) {
            Log.e(TAG, "Error: " + e.toString());
        } catch (Exception e) {
            Log.e(TAG, "Other Error: " + e.toString());
        }
        return null;
    }
}
