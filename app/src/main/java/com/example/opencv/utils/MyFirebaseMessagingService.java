package com.example.opencv.utils;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import androidx.core.app.NotificationCompat;

import com.example.opencv.NotifyActivity;
import com.example.opencv.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import static com.example.opencv.NotifyActivity.notificationManager;
import static com.example.opencv.utils.CreateChannel.CHANNEL_1_ID;

/*
    This class manages to send push notification to the device, by getting data payload from python XmlRpc server
 */
//Code based on [https://youtu.be/s0Q2QKZ4OP8]
//[https://medium.com/@abhilashbehera88/firebase-image-notifications-android-2c16348bd5ac]
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "PUSH Received";

    /**
     * Called if InstanceID token is updated. This may occur if the security of
     * the previous token had been compromised. Note that this is called when the InstanceID token
     * is initially generated so this is where you would retrieve the token.
     */
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
       sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        Log.e(TAG, "here ! sendRegistrationToServer! token is " + token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // parse remoteMessage object
        String notificationTitle = remoteMessage.getNotification().getTitle();
        String imgUrl = remoteMessage.getNotification().getBody();
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Log.d(TAG, "Message body: " + imgUrl);
        // create a notification with these data
        if (imgUrl != null) {
            createNotification(notificationTitle, imgUrl);
        }
    }

    private void createNotification(String notificationTitle, String notificationContent){
        // create a notification builder object
        Intent activityIntent = new Intent(this, NotifyActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(this,
                0, activityIntent, 0);
        Intent fullScreenIntent = new Intent(this, MyFirebaseMessagingService.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(this, 0,
                fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Bitmap bitmap = getBitmapfromUrl(notificationContent);
        Log.d(TAG, "image parsed well: " );

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_1_ID)
                .setContentTitle(notificationTitle)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setSound(defaultSoundUri)
                .setAutoCancel(true)
                .setLargeIcon(bitmap)
                .setStyle(new NotificationCompat.BigPictureStyle()
                        .bigPicture(bitmap)
                        .bigLargeIcon(null))
                .setContentIntent(contentIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_CALL)
                .setFullScreenIntent(fullScreenPendingIntent, true)
                .build();
//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(1, notification);
        Log.d(TAG, "noti created: " );

    }

    public Bitmap getBitmapfromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            Log.e("BitmapParsing", "Error in getting notification image: " + e.getLocalizedMessage());
            return null;
        }
    }
}

