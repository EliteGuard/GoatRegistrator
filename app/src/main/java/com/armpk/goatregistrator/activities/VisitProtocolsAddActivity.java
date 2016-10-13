package com.armpk.goatregistrator.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.adapters.FarmAutocompleteAdapter;
import com.armpk.goatregistrator.adapters.UserAutocompleteAdapter;
import com.armpk.goatregistrator.database.DatabaseHelper;
import com.armpk.goatregistrator.database.Farm;
import com.armpk.goatregistrator.database.User;
import com.armpk.goatregistrator.database.VisitActivity;
import com.armpk.goatregistrator.database.VisitProtocol;
import com.armpk.goatregistrator.database.mobile.LocalVisitProtocol;
import com.armpk.goatregistrator.database.mobile.LocalVisitProtocolVisitActivity;
import com.armpk.goatregistrator.fragments.DatePickerFragment;
import com.armpk.goatregistrator.utilities.Globals;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;


public class VisitProtocolsAddActivity extends AppCompatActivity implements DatePickerFragment.OnDatePickerFragmentInteractionListener{

    private DatabaseHelper dbHelper;
    private SharedPreferences mSharedPreferences;
    private StringBuffer mErrorMessage;
    private LocalVisitProtocol mLocalVisitProtocol;

    private AutoCompleteTextView mAutocompleteTextFarm;
    private AutoCompleteTextView mAutocompleteTextEmployee1;
    private AutoCompleteTextView mAutocompleteTextEmployee2;
    private Button mButtonVisitDate;
    private EditText mEditTextNotes;

    Farm mFarmSelected;
    User mEmployee1Selected;
    User mEmployee2Selected;

    private FarmAutocompleteAdapter mFarmAdapter;
    private UserAutocompleteAdapter mUser1Adapter;
    private UserAutocompleteAdapter mUser2Adapter;

    private Date mDateSelected;

    private Set<String> mVisitActivitiesIds;

    private static final String ARG_FARM = "farm";
    private static final String ARG_VISIT_PROTOCOL = "visit_protocol";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DatabaseHelper(this);
        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        mErrorMessage = new StringBuffer();

        setContentView(R.layout.activity_visit_protocol_add);


        mAutocompleteTextFarm = (AutoCompleteTextView)findViewById(R.id.edit_farm_name);
        mAutocompleteTextEmployee1 = (AutoCompleteTextView)findViewById(R.id.edit_employee_1);
        mAutocompleteTextEmployee2 = (AutoCompleteTextView)findViewById(R.id.edit_employee_2);

        InitActivity ia = new InitActivity(this);
        ia.execute((Void) null);
        /*try {
            mAutocompleteTextFarm.setAdapter(new FarmAutocompleteAdapter(this, dbHelper.getDaoFarm().queryForAll()));
            mAutocompleteTextEmployee1.setAdapter(new UserAutocompleteAdapter(this, dbHelper.getDaoUser().queryForAll()));
            mAutocompleteTextEmployee2.setAdapter(new UserAutocompleteAdapter(this, dbHelper.getDaoUser().queryForAll()));
        } catch (SQLException e) {
            e.printStackTrace();
        }*/



        mButtonVisitDate = (Button)findViewById(R.id.button_visit_date_pick);
        mButtonVisitDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showVisitDatePickerDialog();
            }
        });

        mEditTextNotes = (EditText)findViewById(R.id.edit_text_notes);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mLocalVisitProtocol = new LocalVisitProtocol();
                    //mVisitProtocol.setId((long) 0);
                    try {
                        if(validateAllFields()){
                            //dbHelper.createVisitProtocol(mVisitProtocol);
                            //saveProtocol(mVisitProtocol);
                            continueToReadGoats(mLocalVisitProtocol);
                            //Log.d("TEMPORARY PROTOCOL", mSharedPreferences.getString(Globals.TEMPORARY_VISIT_PROTOCOL, null));
                            //finish();
                        }else{
                            mLocalVisitProtocol = null;
                            Toast error = Toast.makeText(getApplicationContext(), mErrorMessage, Toast.LENGTH_LONG);
                            mErrorMessage.setLength(0);
                            error.show();
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

        initActivities();
        /*try {
            loadProtocol();
        } catch (JSONException e) {
            e.printStackTrace();
        }*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            OpenHelperManager.releaseHelper();
            dbHelper = null;
        }
    }

    /*private void loadProtocol() throws JSONException {
        String vpS = mSharedPreferences.getString(Globals.TEMPORARY_VISIT_PROTOCOL, "");
        if(vpS.length()>0){
            JSONObject jsonVP = new JSONObject(vpS);
            //JSONObject t = new JSONObject("{\"farm\":{\"herdbookVolumeNumber\":\"1\",\"corespondenceAddress\":{\"id\":11,\"region\":\"\",\"addressTitle\":\"Адрес за кореспонденция\",\"email\":\"\",\"dateLastUpdated\":1471955561235,\"street\":\"\",\"lastUpdatedByUser\":{\"id\":1,\"dateRegistered\":1471437881273,\"username\":\"admin\",\"dateLastUpdated\":1471437881273,\"activeStatus\":true,\"familyName\":\"Супер\",\"userRole\":{\"id\":1},\"firstName\":\"Админ\",\"password\":\"827ccb0eea8a706c4c34a16891f84e7b\"},\"dateAddedToSystem\":1471437306083,\"notes\":\"\",\"postcode\":\"\"},\"lst_contactPhones\":[0],\"dateAddedToSystem\":1471437306083,\"eik\":6507285326,\"companyName\":\"Живко Костадинов Тодоров\",\"bulstat\":\"\",\"mol\":\"Живко Костадинов Тодоров\",\"breedingPlaceNumber\":\"7092-0364\",\"id\":4,\"breedingPlaceAddress\":{\"id\":10,\"region\":\"\",\"addressTitle\":\"Адрес на Животн. Обект\",\"email\":\"\",\"dateLastUpdated\":1471955561235,\"street\":\"\",\"lastUpdatedByUser\":{\"id\":1,\"dateRegistered\":1471437881273,\"username\":\"admin\",\"dateLastUpdated\":1471437881273,\"activeStatus\":true,\"familyName\":\"Супер\",\"userRole\":{\"id\":1},\"firstName\":\"Админ\",\"password\":\"827ccb0eea8a706c4c34a16891f84e7b\"},\"dateAddedToSystem\":1471437306099,\"notes\":\"\",\"postcode\":\"\",\"city\":{\"id\":20914,\"area\":\"РУСЕ\",\"dateLastUpdated\":1471435336016,\"locationType\":\"VILLAGE\",\"name\":\"ТРЪСТЕНИК\",\"lastUpdatedByUser\":{\"id\":1},\"municipality\":\"ИВАНОВО\",\"dateAddedToSystem\":1471435336016,\"postcode\":\"7092\",\"regionalCenter\":\"СЦРЦККРД\"}},\"managementAddress\":{\"id\":12,\"region\":\"\",\"addressTitle\":\"Адрес на управление\",\"email\":\"\",\"dateLastUpdated\":1471955561219,\"street\":\"\",\"lastUpdatedByUser\":{\"id\":1,\"dateRegistered\":1471437881273,\"username\":\"admin\",\"dateLastUpdated\":1471437881273,\"activeStatus\":true,\"familyName\":\"Супер\",\"userRole\":{\"id\":1},\"firstName\":\"Админ\",\"password\":\"827ccb0eea8a706c4c34a16891f84e7b\"},\"dateAddedToSystem\":1471437306083,\"notes\":\"\",\"postcode\":\"\"},\"dateLastUpdated\":1471955561219,\"lastUpdatedByUser\":{\"id\":1,\"dateRegistered\":1471437881273,\"username\":\"admin\",\"dateLastUpdated\":1471437881273,\"activeStatus\":true,\"familyName\":\"Супер\",\"userRole\":{\"id\":1,\"codeName\":\"admin\",\"dateAddedToSystem\":1469307600000,\"displayName\":\"Администратор\",\"description\":\"Администратор\",\"dateLastUpdated\":1469307600000},\"firstName\":\"Админ\",\"password\":\"827ccb0eea8a706c4c34a16891f84e7b\"}},\"id\":0,\"dateLastUpdated\":1472077426767,\"lastUpdatedByUser\":{\"id\":1,\"dateRegistered\":1471437881273,\"username\":\"admin\",\"dateLastUpdated\":1471437881273,\"activeStatus\":true,\"familyName\":\"Супер\",\"userRole\":{\"id\":1,\"codeName\":\"admin\",\"dateAddedToSystem\":1469307600000,\"displayName\":\"Администратор\",\"description\":\"Администратор\",\"dateLastUpdated\":1469307600000},\"firstName\":\"Админ\",\"password\":\"827ccb0eea8a706c4c34a16891f84e7b\"},\"employFirst\":{\"id\":1,\"dateRegistered\":1471437881273,\"username\":\"admin\",\"dateLastUpdated\":1471437881273,\"activeStatus\":true,\"familyName\":\"Супер\",\"userRole\":{\"id\":1,\"codeName\":\"admin\",\"dateAddedToSystem\":1469307600000,\"displayName\":\"Администратор\",\"description\":\"Администратор\",\"dateLastUpdated\":1469307600000},\"firstName\":\"Админ\",\"password\":\"827ccb0eea8a706c4c34a16891f84e7b\"},\"employSecond\":{\"id\":1,\"dateRegistered\":1471437881273,\"username\":\"admin\",\"dateLastUpdated\":1471437881273,\"activeStatus\":true,\"familyName\":\"Супер\",\"userRole\":{\"id\":1,\"codeName\":\"admin\",\"dateAddedToSystem\":1469307600000,\"displayName\":\"Администратор\",\"description\":\"Администратор\",\"dateLastUpdated\":1469307600000},\"firstName\":\"Админ\",\"password\":\"827ccb0eea8a706c4c34a16891f84e7b\"},\"dateAddedToSystem\":1472077426767,\"visitDate\":1472077425157}");
            VisitProtocol vp = Globals.jsonToObject(jsonVP, VisitProtocol.class);
            if(vp.getFarm()!=null) {
                mAutocompleteTextFarm.setText(vp.getFarm().getCompanyName());
                mFarmSelected = vp.getFarm();
            }
            if(vp.getEmployFirst()!=null){
                mAutocompleteTextEmployee1.setText(getString(R.string.placeholder_two_names,
                        vp.getEmployFirst().getFirstName(),
                        vp.getEmployFirst().getFamilyName()));
                mEmployee1Selected = vp.getEmployFirst();
            }
            if(vp.getEmploySecond()!=null){
                mAutocompleteTextEmployee2.setText(getString(R.string.placeholder_two_names,
                        vp.getEmploySecond().getFirstName(),
                        vp.getEmploySecond().getFamilyName()));
                mEmployee2Selected = vp.getEmploySecond();
            }
            if(vp.getNotes()!=null){
                mEditTextNotes.setText(vp.getNotes());
            }
            if(vp.getVisitDate()!=null) {
                mButtonVisitDate.setText(Globals.getDateShort(vp.getVisitDate()));
                mDateSelected = vp.getVisitDate();
            }
        }
    }*/

    private void initActivities(){
        final LinearLayout linearMain = (LinearLayout) findViewById(R.id.linearMain);
        mVisitActivitiesIds = new HashSet<>();
        try {
            for(final VisitActivity va : dbHelper.getDaoVisitActivity().queryForAll()){
                final CheckBox cb = new CheckBox(this);
                cb.setTag(va);
                cb.setText(va.getName());
                cb.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(cb.isChecked()){
                            mVisitActivitiesIds.add(String.valueOf(va.getId()));
                        }else{
                            mVisitActivitiesIds.remove(String.valueOf(va.getId()));
                        }
                    }
                });
                if (linearMain != null) {
                    linearMain.addView(cb);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void continueToReadGoats(LocalVisitProtocol vp){
        Intent intent = new Intent(this, GoatAddReaderActivity.class);
        Bundle args = new Bundle();
        args.putSerializable(ARG_VISIT_PROTOCOL, vp);
        args.putSerializable(ARG_FARM, vp.getFarm());
        intent.putExtras(args);
        try {
            //Globals.savePreferences(Globals.TEMPORARY_VISIT_PROTOCOL, Globals.objectToJson(vp).toString(), this);
            /*Set<String> tempProtocols = mSharedPreferences.getStringSet(Globals.TEMPORARY_VISIT_PROTOCOLS, new HashSet<String>());
            tempProtocols.add(Globals.objectToJson(vp).toString());
            Globals.savePreferences(Globals.TEMPORARY_VISIT_PROTOCOLS, tempProtocols, this);
            String key = Globals.TEMPORARY_ACTIVITIES_FOR_PROTOCOL+String.valueOf(vp.getFarm().getId())+"_"+String.valueOf(vp.getDateAddedToSystem().getTime());
            Globals.savePreferences(key, mVisitActivitiesIds, this);*/

            dbHelper.getDaoLocalVisitProtocol().create(vp);
            for(String s : mVisitActivitiesIds){
                VisitActivity va = dbHelper.getDaoVisitActivity().queryForId(Integer.valueOf(s));
                LocalVisitProtocolVisitActivity lvpva = new LocalVisitProtocolVisitActivity();

                lvpva.setVisitActivity(va);
                lvpva.setLocalVisitProtocol(vp);

                QueryBuilder<LocalVisitProtocolVisitActivity, Long> qb = dbHelper.getDaoLocalVisitProtocolVisitActivity().queryBuilder();
                long rows = qb.where().eq("localVisitProtocol_id", lvpva.getLocalVisitProtocol().getId())
                        .and()
                        .eq("visitActivity_id", lvpva.getVisitActivity().getId())
                        .countOf();
                if(rows<1){
                    dbHelper.getDaoLocalVisitProtocolVisitActivity().create(lvpva);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        startActivity(intent);
        finish();
    }

    public boolean validateAllFields() throws SQLException {

        boolean valid = true;

        mAutocompleteTextFarm.setError(null);
        mAutocompleteTextEmployee1.setError(null);
        mAutocompleteTextEmployee2.setError(null);
        View focusView = null;

        if(mFarmSelected==null || dbHelper.getDaoFarm().queryForId(mFarmSelected.getId())==null){
            mAutocompleteTextFarm.setError("Несъществуващо име на ферма!");
            valid = false;
            focusView = mAutocompleteTextFarm;
        }else {
            mFarmSelected.setLst_visitProtocol(null);
            mFarmSelected.setLst_contactPhones(null);
            mLocalVisitProtocol.setFarm(mFarmSelected);
        }

        if(mEmployee1Selected==null || dbHelper.getDaoUser().queryForId(mEmployee1Selected.getId())==null){
            mAutocompleteTextEmployee1.setError("Несъществуващо име на служител!");
            valid = false;
            focusView = mAutocompleteTextEmployee1;
        }else {
            mLocalVisitProtocol.setEmployFirst(mEmployee1Selected);
        }

        if(mEmployee2Selected==null || dbHelper.getDaoUser().queryForId(mEmployee2Selected.getId())==null){
            mAutocompleteTextEmployee2.setError("Несъществуващо име на служител!");
            valid = false;
            focusView = mAutocompleteTextEmployee2;
        }else {
            mLocalVisitProtocol.setEmploySecond(mEmployee2Selected);
        }

        if(mDateSelected!=null){
            mLocalVisitProtocol.setVisitDate(mDateSelected);
            mLocalVisitProtocol.setDateAddedToSystem(new Date(System.currentTimeMillis()));
            mLocalVisitProtocol.setDateLastUpdated(new Date(System.currentTimeMillis()));
            try {
                mLocalVisitProtocol.setLastUpdatedByUser(
                        dbHelper.getDaoUser().queryForId(mSharedPreferences.getLong(Globals.SETTING_ACTIVE_USER_ID, 0))
                );
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            valid = false;
            focusView = mButtonVisitDate;
            mErrorMessage.append("Не е избрана ДАТА НА ПОСЕЩЕНИЕ\n");
        }

        if(mEditTextNotes.getText().length()>0){
            mLocalVisitProtocol.setNotes(mEditTextNotes.getText().toString());
        }

        if(!valid) {
            focusView.requestFocus();
        }

        return valid;
    }

    public void showVisitDatePickerDialog(){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    public void onDateSet(Calendar calendar) {
        mDateSelected = calendar.getTime();
        mButtonVisitDate.setText( getString(R.string.placeholder_date,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("bg","BG")),
                calendar.get(Calendar.YEAR)));
    }

    private class InitActivity extends AsyncTask<Void, Integer, Boolean> {
        private Context mContext;
        private ProgressDialog mProgressDialog;
        public InitActivity(Context ctx){
            mContext = ctx;
            mProgressDialog = new ProgressDialog(mContext);
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.setMessage("Зареждане на данни");
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mProgressDialog.setIndeterminate(true);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setCanceledOnTouchOutside(false);
            //mProgressDialog.setProgress(0);
            mProgressDialog.show();
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            final boolean[] success = {false};


            try {
                mFarmAdapter = new FarmAutocompleteAdapter(VisitProtocolsAddActivity.this, dbHelper.getDaoFarm().queryForAll());
                mUser1Adapter = new UserAutocompleteAdapter(VisitProtocolsAddActivity.this, dbHelper.getDaoUser().queryForAll());
                mUser2Adapter = new UserAutocompleteAdapter(VisitProtocolsAddActivity.this, dbHelper.getDaoUser().queryForAll());
            } catch (SQLException e) {
                e.printStackTrace();
            }


            return success[0];
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            mProgressDialog.incrementProgressBy(1);
        }

        @Override
        protected void onPostExecute(Boolean success) {

            mAutocompleteTextFarm.setAdapter(mFarmAdapter);
            mAutocompleteTextEmployee1.setAdapter(mUser1Adapter);
            mAutocompleteTextEmployee2.setAdapter(mUser2Adapter);

            mAutocompleteTextFarm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mAutocompleteTextFarm.setText(
                            ((TextView)view.findViewById(R.id.text_name)).getText().toString()
                    );
                    mFarmSelected = (Farm) adapterView.getItemAtPosition(i);
                    mAutocompleteTextEmployee1.requestFocus();
                }
            });
            mAutocompleteTextEmployee1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mAutocompleteTextEmployee1.setText(
                            ((TextView)view.findViewById(R.id.text_name)).getText().toString()
                    );
                    mEmployee1Selected = (User) adapterView.getItemAtPosition(i);
                    mAutocompleteTextEmployee2.requestFocus();
                }
            });
            mAutocompleteTextEmployee2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    mAutocompleteTextEmployee2.setText(
                            ((TextView)view.findViewById(R.id.text_name)).getText().toString()
                    );
                    mEmployee2Selected = (User) adapterView.getItemAtPosition(i);
                }
            });

            if(mProgressDialog.isShowing()){
                mProgressDialog.cancel();
            }
        }
    }
}
