package com.example.opencv;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/*
 *  AboutScreenActivity displays information about how to use the app
 *  and source for the resources used in the app.
 */
public class AboutScreen extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        setTextInTextBox();
    }

    private void setTextInTextBox() {
        TextView helpContent = (TextView) findViewById(R.id.txtHelpContent);
        helpContent.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, AboutScreen.class);
        return intent;
    }
}