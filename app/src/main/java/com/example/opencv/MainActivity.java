package com.example.opencv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

/*
 *  MainActivity sets activities of buttons that navigate
 *  to upload image, change notification settings, and get help.
 */
public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_OPTION = 6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupHelpButton();
        setupOptionButton();
        setupImageButton();
    }

    private void setupImageButton() {
        Button btn = findViewById(R.id.btn_img_upload);
        btn.setOnClickListener(new View.OnClickListener(){
            public void onClick (View v){

                Intent intent = ImageActivity.makeIntent(MainActivity.this);
                startActivity(intent);
            }
        });
    }

    private void setupOptionButton() {
        Button btn = (Button) findViewById(R.id.btn_option);
        btn.setOnClickListener(new View.OnClickListener(){

            public void onClick (View v){
                Intent intent = NotifyActivity.makeIntent(MainActivity.this);
                startActivityForResult(intent, REQUEST_CODE_OPTION);
            }

        });
    }

    private void setupHelpButton() {
        Button btnHelp = (Button) findViewById(R.id.btn_help);
        btnHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = AboutScreen.makeIntent(MainActivity.this);
                startActivity(intent);
            }
        });
    }

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        return intent;
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        ImageView menuCat = (ImageView) findViewById(R.id.menu_cat);
        YoYo.with(Techniques.RubberBand).duration(8000).repeat(5).playOn(menuCat);
        Button btnPlay = (Button) findViewById(R.id.btn_img_upload);
        YoYo.with(Techniques.Bounce).duration(2000).repeat(3).playOn(btnPlay);
    }
}