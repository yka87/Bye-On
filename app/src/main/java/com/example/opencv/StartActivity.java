package com.example.opencv;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

/*
 * This class shows welcome screen to user and advances to the main menu page.
 */
public class StartActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        setupSkipButton();
        changeActivity();
    }

    // Set animation activity
    // Code found at [https://developer.android.com/guide/topics/graphics/drawable-animation]
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ImageView catImage = (ImageView) findViewById(R.id.cat_animation);
        ImageView catImage1 = (ImageView) findViewById(R.id.cat1);
        ImageView catImage2 = (ImageView) findViewById(R.id.cat2);
        YoYo.with(Techniques.RubberBand).duration(2500).repeat(3).playOn(catImage1);
        YoYo.with(Techniques.Landing).duration(2500).repeat(3).playOn(catImage2);
        YoYo.with(Techniques.Bounce).duration(2500).repeat(3).playOn(catImage);
    }

    // Advance activity automatically to the main menu at the completion of welcome screen
    // Code found at [https://stackoverflow.com/questions/12160319/how-to-move-to-another-activity-in-a-few-seconds]
    private void changeActivity() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(StartActivity.this, MainActivity.class);
                StartActivity.this.startActivity(intent);
                StartActivity.this.finish();
            }
        }, 15000);
    }

    private void setupSkipButton() {
        Button btn = (Button) findViewById(R.id.btn_skp);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = MainActivity.makeIntent(StartActivity.this);
                startActivity(intent);
            }
        });
    }
}