package com.armpk.goatregistrator.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.armpk.goatregistrator.R;

public class GoatAddChoiceActivity extends AppCompatActivity {

    private Intent intentRead;
    private Intent intentManually;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goat_add_choice);

        Button buttonByReader = (Button)findViewById(R.id.buttonByReader);
        intentRead = new Intent(this, GoatAddReaderActivity.class);
        buttonByReader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentRead);
            }
        });

        Button buttonManually = (Button)findViewById(R.id.buttonManually);
        intentManually = new Intent(this, GoatAddManuallyActivity.class);
        buttonManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(intentManually);
            }
        });

    }
}
