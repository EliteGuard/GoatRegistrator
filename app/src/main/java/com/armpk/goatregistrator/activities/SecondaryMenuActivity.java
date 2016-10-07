package com.armpk.goatregistrator.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.utilities.Globals;

public class SecondaryMenuActivity extends AppCompatActivity {

    private Globals.MenuContext menuContext;
    private Intent intentAddForm;
    private Intent intentSearchForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondary_menu);

        Intent intent = getIntent();
        menuContext = (Globals.MenuContext) intent.getExtras().get(Globals.EXTRA_SECONDARY_MENU_CONTEXT);

        switch (menuContext){
            case FARM:
                setTitle(R.string.text_farm);
                intentAddForm = new Intent(this, FarmAddActivity.class);
                intentSearchForm = new Intent(this, FarmSearchActivity.class);
                break;
            case USER:
                setTitle(R.string.text_user);
                intentAddForm = new Intent(this, UserAddActivity.class);
                intentSearchForm = new Intent(this, UserSearchActivity.class);
                break;
            case GOAT:
                setTitle(R.string.text_goat);
                intentAddForm = new Intent(this, GoatAddChoiceActivity.class);
                intentSearchForm = new Intent(this, GoatSearchActivity.class);
                break;
            /*case VISIT_PROTOCOL:
                setTitle(R.string.text_visit_protocol);
                intentAddForm = new Intent(this, VisitProtocolsAddActivity.class);
                intentSearchForm = new Intent(this, VisitProtocolSearchActivity.class);
                break;*/
            default:
                finish();
                break;
        }

        Button buttonAdd = (Button)findViewById(R.id.buttonAdd);
        if (buttonAdd != null) {
            buttonAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(intentAddForm);
                }
            });
        }

        Button buttonSearch = (Button)findViewById(R.id.buttonSearch);
        if (buttonSearch != null) {
            buttonSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(intentSearchForm);
                }
            });
        }


    }
}
