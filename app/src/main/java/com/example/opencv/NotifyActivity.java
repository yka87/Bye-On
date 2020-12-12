package com.example.opencv;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationManagerCompat;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.opencv.utils.CallServerWorker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.InstanceIdResult;

import java.util.concurrent.TimeUnit;

import static com.google.firebase.iid.FirebaseInstanceId.getInstance;

// This class manages user selected notification time interval setting,
// and manage the work to be executed background even when the app is not active.
// Notification schedule is set by calling python XmlRpc server by Java client from utls.CallServerWorker.class.
// Code based on [https://developer.android.com/topic/libraries/architecture/workmanager/how-to/managing-work]
// [https://developer.android.com/training/notify-user/expanded]
public class NotifyActivity extends AppCompatActivity { //implements TimePickerDialog.OnTimeSetListener{

    private int selectedTime;
    public static NotificationManagerCompat notificationManager;
    private final String TAG = "firebase token";
    public static String token;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (!task.isSuccessful()) {
                    Log.w(TAG, "getInstanceId failed", task.getException());
                    return;
                }
                // Get new Instance ID token
                token = task.getResult().getToken();
                Log.d(TAG, token);
            }
        });

        notificationManager = NotificationManagerCompat.from(this);
        setTimeInterval();
        setButtons();
    }

    public static Intent makeTokenIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("Token", token);
        return intent;
    }

    private void setButtons() {
        Button scheduleJobButton = findViewById(R.id.scheduleJobButton);
        Button sendNowButton = findViewById(R.id.sendNowButton);
        Button cancelJobButton = findViewById(R.id.cancelJobsButton);

        scheduleJobButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                executeWorkRequest();
            }
        });
        sendNowButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                onetimeRequest();
            }
        });
        cancelJobButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelRequest();
            }
        });
    }

    private void setTimeInterval() {
        RadioGroup intervalOptions = (RadioGroup) findViewById(R.id.interval_options);
        selectedTime = intervalOptions.getCheckedRadioButtonId();
        switch (selectedTime) {
            case R.id.one_hr:
                selectedTime = 1;
                break;
            case R.id.four_hr:
                selectedTime = 4;
                break;
            case R.id.eight_hr:
                selectedTime = 8;
                break;
            case R.id.once_day:
                selectedTime = 24;
                break;
        }
    }

    // [https://developer.android.com/topic/libraries/architecture/workmanager/how-to/define-work]
    private void executeWorkRequest(){
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest callServerPeriodicRequest =
                new PeriodicWorkRequest.Builder(CallServerWorker.class, selectedTime, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .addTag("getNoti")
                        .build();

        WorkManager.getInstance(this).enqueue(callServerPeriodicRequest);
    }

    private void onetimeRequest(){
        WorkRequest callServerRequest =
                new OneTimeWorkRequest.Builder(CallServerWorker.class)
                        .build();
        WorkManager.getInstance(this).enqueue(callServerRequest);
    }

    private void cancelRequest(){
        WorkManager.getInstance(this).cancelAllWorkByTag("getNoti");
    }

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, NotifyActivity.class);
        return intent;
    }

//    private void openNotificationSettings() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
//            intent.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
//            startActivity(intent);
//        } else {
//            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
//            intent.setData(Uri.parse("package:" + getPackageName()));
//            startActivity(intent);
//        }
//    }
}
