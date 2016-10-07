package com.armpk.goatregistrator.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.armpk.goatregistrator.activities.LoginActivity;
import com.armpk.goatregistrator.activities.MainMenuActivity;
import com.armpk.goatregistrator.database.DatabaseHelper;
import com.armpk.goatregistrator.utilities.Globals;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class MainEntryActivity extends Activity {

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            createOrmLiteDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(getSharedPreferences(getPackageName(), Context.MODE_PRIVATE).getBoolean(Globals.SETTING_SIGNED_IN, false)){
            Intent intent = new Intent(this, MainMenuActivity.class);
            startActivity(intent);
        }else{
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }

        finish();
    }

    private void createOrmLiteDatabase() throws SQLException {
        databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
		/*
		 * You'll need this in your class to release the helper when done.
		 */
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }
}
