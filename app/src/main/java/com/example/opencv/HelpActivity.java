package com.example.opencv;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/*
 *  HelpActivity displays information about how to use the app
 *  and source for the resources used in the app.
 */
public class HelpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        setTextInTextBox();
    }

    private void setTextInTextBox() {
        TextView helpContent = (TextView) findViewById(R.id.txtHelpContent);
        helpContent.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static Intent makeIntent(Context context) {
        Intent intent = new Intent(context, HelpActivity.class);
        return intent;
    }
}