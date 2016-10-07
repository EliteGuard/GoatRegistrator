package com.armpk.goatregistrator.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.utilities.Globals;

import java.util.HashSet;
import java.util.Set;

public class VisitProtocolsActivity extends AppCompatActivity {

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visit_protocols);

        setTitle(R.string.text_visit_protocol);

        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        Button buttonAdd = (Button)findViewById(R.id.buttonAdd);
        if (buttonAdd != null) {
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent add = new Intent(VisitProtocolsActivity.this, VisitProtocolsAddActivity.class);
                    startActivity(add);
                }
            });
        }

        Button buttonSearch = (Button)findViewById(R.id.buttonSearch);
        if (buttonSearch != null) {
            buttonSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent search = new Intent(VisitProtocolsActivity.this, VisitProtocolSearchActivity.class);
                    startActivity(search);
                }
            });
        }

        Button buttonNotSynced = (Button)findViewById(R.id.buttonNotSynced);
        if (buttonNotSynced != null) {
            Set<String> ps = mSharedPreferences.getStringSet(Globals.TEMPORARY_VISIT_PROTOCOLS, new HashSet<String>());
            if(ps.size() > 0){
                buttonNotSynced.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent notSynced = new Intent(VisitProtocolsActivity.this, VisitProtocolsNotSyncedActivity.class);
                        startActivity(notSynced);
                    }
                });
                buttonNotSynced.setEnabled(true);
            }else{
                buttonNotSynced.setEnabled(false);
            }

        }
    }
}
