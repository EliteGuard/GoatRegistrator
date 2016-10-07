package com.armpk.goatregistrator.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.database.City;
import com.armpk.goatregistrator.database.DatabaseHelper;
import com.armpk.goatregistrator.database.GoatStatus;
import com.armpk.goatregistrator.database.User;
import com.armpk.goatregistrator.database.UserRole;
import com.armpk.goatregistrator.fragments.DatePickerFragment;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class UserAddActivity extends AppCompatActivity implements DatePickerFragment.OnDatePickerFragmentInteractionListener{

    private User mUser;
    private DatabaseHelper dbHelper;
    private StringBuffer mErrorMessage;

    private HashMap<String, UserRole> hashMapUserRoles;
    private HashMap<String, City> hashMapCities;

    private Spinner spinnerUserRoles;

    private EditText mEditTextName;
    private EditText mEditTextSurName;
    private EditText mEditTextFamilyName;
    private AutoCompleteTextView mAutocompleteTextCity;
    private AutoCompleteTextView mAutocompleteTextPhone;
    private AutoCompleteTextView mAutocompleteTextEmail;
    private Button mButtonRegisterDate;
    private Date mDateSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DatabaseHelper(this);
        mErrorMessage = new StringBuffer();

        setContentView(R.layout.activity_user_add);

        hashMapUserRoles = new HashMap<String, UserRole>();
        spinnerUserRoles = (Spinner)findViewById(R.id.spinner_user_role);
        ArrayAdapter<String> adapterUserRoles = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item
        );
        try {
            for(UserRole ur : dbHelper.getDaoUserRole().queryForAll()){
                adapterUserRoles.add(ur.getDisplayName());
                hashMapUserRoles.put(ur.getDisplayName(), ur);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        adapterUserRoles.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerUserRoles.setAdapter(adapterUserRoles);

        mEditTextName = (EditText)findViewById(R.id.editTextFirstName);
        mEditTextSurName = (EditText)findViewById(R.id.editTextSurName);
        mEditTextFamilyName = (EditText)findViewById(R.id.editTextFamilyName);

        mAutocompleteTextCity = (AutoCompleteTextView)findViewById(R.id.autocomplete_text_city);
        hashMapCities = new HashMap<String, City>();
        ArrayAdapter<String> adapterCities = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_spinner_item);
        try {
            for (City c : dbHelper.getDaoCity().queryBuilder().groupByRaw("name").query()) {
                if(c.getName()!=null) adapterCities.add(c.getName());
                hashMapCities.put(c.getName(), c);
            }
        }catch (SQLException e) {
            e.printStackTrace();
        }
        adapterCities.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        mAutocompleteTextCity.setAdapter(adapterCities);

        mAutocompleteTextPhone = (AutoCompleteTextView)findViewById(R.id.autocomplete_text_phone);
        ArrayAdapter<String> adapterPhones = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
        try {
            for (User u : dbHelper.getDaoUser().queryBuilder().groupByRaw("phone").query()) {
                if(u.getPhone()!=null) adapterPhones.add(u.getPhone());
            }
            adapterPhones.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            mAutocompleteTextPhone.setAdapter(adapterPhones);
        }catch (SQLException e) {
            e.printStackTrace();
        }

        mAutocompleteTextEmail = (AutoCompleteTextView)findViewById(R.id.autocomplete_text_email);
        ArrayAdapter<String> adapterEmails = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
        try {
            for (User u : dbHelper.getDaoUser().queryBuilder().groupByRaw("email").query()) {
                if(u.getPhone()!=null) adapterEmails.add(u.getPhone());
            }
            adapterEmails.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            mAutocompleteTextEmail.setAdapter(adapterEmails);
        }catch (SQLException e) {
            e.printStackTrace();
        }

        mButtonRegisterDate = (Button)findViewById(R.id.buttonRegisterDatePick);
        mButtonRegisterDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRegisterDatePickerDialog();
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mUser = new User();
                    if(validateAllFields()){
                        //dbHelper.insertUser(mUser);
                        finish();
                    }else{
                        mUser = null;
                        Toast error = Toast.makeText(getApplicationContext(), mErrorMessage, Toast.LENGTH_LONG);
                        mErrorMessage.setLength(0);
                        error.show();
                    }

                }
            });
        }
    }

    public boolean validateAllFields() {

        boolean valid = true;

        mEditTextName.setError(null);
        mEditTextSurName.setError(null);
        mEditTextFamilyName.setError(null);
        mAutocompleteTextCity.setError(null);
        mAutocompleteTextPhone.setError(null);
        mAutocompleteTextEmail.setError(null);
        View focusView = null;

        mUser.setUserRole(hashMapUserRoles.get(spinnerUserRoles.getSelectedItem().toString()));

        if(mEditTextName.getText().length()<2){
            mEditTextName.setError("Невалидно име");
            valid = false;
            focusView = mEditTextName;
        }else {
            mUser.setFirstName(mEditTextName.getText().toString());
        }

        if(mEditTextSurName.getText().length()<2){
            mEditTextSurName.setError("Невалидно презиме");
            valid = false;
            focusView = mEditTextSurName;
        }else {
            mUser.setSurName(mEditTextSurName.getText().toString());
        }

        if(mEditTextFamilyName.getText().length()<2){
            mEditTextFamilyName.setError("Невалидна фамилия");
            valid = false;
            focusView = mEditTextFamilyName;
        }else {
            mUser.setFamilyName(mEditTextFamilyName.getText().toString());
        }

        if(mAutocompleteTextCity.getText().length()<2 || !hashMapCities.containsKey(mAutocompleteTextCity.getText().toString())){
            mAutocompleteTextCity.setError("Невалидно име на град/село");
            valid = false;
            focusView = mAutocompleteTextCity;
        }else {
            mUser.setCity(hashMapCities.get(mAutocompleteTextCity.getText().toString()));
        }

        if(mAutocompleteTextPhone.getText().length()<7){
            mAutocompleteTextPhone.setError("Невалиден телефонен номер");
            valid = false;
            focusView = mAutocompleteTextPhone;
        }else {
            mUser.setPhone(mAutocompleteTextPhone.getText().toString());
        }

        if(mAutocompleteTextEmail.getText().length()>0){
            mUser.setEmail(mAutocompleteTextEmail.getText().toString());
        }else{
            mUser.setEmail("Няма въведен имейл");
        }

        if(mDateSelected!=null){
            mUser.setDateRegistered(mDateSelected);
            mUser.setDateLastUpdated(mDateSelected);
        }else{
            valid = false;
            focusView = mButtonRegisterDate;
            mErrorMessage.append("Не е избрана ДАТА НА РЕГИСТРАЦИЯ\n");
        }

        if(!valid) {
            focusView.requestFocus();
        }

        return valid;
    }

    public void showRegisterDatePickerDialog(){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            OpenHelperManager.releaseHelper();
            dbHelper = null;
        }
    }

    @Override
    public void onDateSet(Calendar calendar) {
        mDateSelected = calendar.getTime();
        mButtonRegisterDate.setText(calendar.get(Calendar.DAY_OF_MONTH)+" "+
                calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())+" "+
                calendar.get(Calendar.YEAR));
    }
}
