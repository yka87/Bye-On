package com.example.opencv.utils;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;

import com.example.opencv.R;

/*
  This class creates notification channel to create notification received from the server
  Code based on [https://developer.android.com/training/notify-user/channels] [https://youtu.be/s0Q2QKZ4OP8]
 */
public class CreateChannel extends Application {
    public static final String CHANNEL_1_ID = "channel_1";

    public void onCreate(){
        super.onCreate();
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel1 = new NotificationChannel
                    (getResources().getString(R.string.notification_channel_id),
                            getResources().getString(R.string.notification_channel_name),
                    NotificationManager.IMPORTANCE_HIGH);
            channel1.setDescription("This is channel 1");
            //setsounds, lock screen, vibration... many things are possible
            channel1.setLightColor(Color.TRANSPARENT);
            channel1.enableVibration(true);
            channel1.setVibrationPattern(new long[]{100, 200, 300});
            channel1.enableLights(true);
            channel1.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }
}
