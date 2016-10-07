package com.armpk.goatregistrator.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.utilities.Globals;

public class MainMenuActivity extends AppCompatActivity {

    private Intent intentSecondaryMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        intentSecondaryMenu = new Intent(this, SecondaryMenuActivity.class);

        Button buttonFarm = (Button)findViewById(R.id.buttonFarm);
        if (buttonFarm != null) {
            buttonFarm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intentSecondaryMenu.putExtra(Globals.EXTRA_SECONDARY_MENU_CONTEXT, Globals.MenuContext.FARM);
                    startActivity(intentSecondaryMenu);
                }
            });
        }

        Button buttonUser = (Button)findViewById(R.id.buttonUser);
        if (buttonUser != null) {
            buttonUser.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intentSecondaryMenu.putExtra(Globals.EXTRA_SECONDARY_MENU_CONTEXT, Globals.MenuContext.USER);
                    startActivity(intentSecondaryMenu);
                }
            });
        }

        Button buttonGoat = (Button)findViewById(R.id.buttonGoat);
        if (buttonGoat != null) {
            buttonGoat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intentSecondaryMenu.putExtra(Globals.EXTRA_SECONDARY_MENU_CONTEXT, Globals.MenuContext.GOAT);
                    startActivity(intentSecondaryMenu);
                }
            });
        }

        Button buttonVisitProtocol = (Button)findViewById(R.id.buttonVisitProtocol);
        if(buttonVisitProtocol != null){
            buttonVisitProtocol.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent visitProtocols = new Intent(MainMenuActivity.this, VisitProtocolsActivity.class);
                    startActivity(visitProtocols);
                }
            });
        }

        Button buttonSettings = (Button)findViewById(R.id.buttonSettings);
        if (buttonSettings != null) {
            buttonSettings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainMenuActivity.this, SettingsActivity.class);
                    startActivity(intent);
                }
            });
        }

    }
}
