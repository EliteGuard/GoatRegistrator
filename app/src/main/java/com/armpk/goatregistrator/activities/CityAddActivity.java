package com.armpk.goatregistrator.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.database.City;
import com.armpk.goatregistrator.database.DatabaseHelper;
import com.armpk.goatregistrator.database.enums.LocationType;
import com.j256.ormlite.android.apptools.OpenHelperManager;

import java.sql.SQLException;

public class CityAddActivity extends AppCompatActivity {

    private City mCity;
    private DatabaseHelper dbHelper;
    private StringBuffer mErrorMessage;

    private EditText mEditTextCityName;
    private RadioGroup mRadioGroupLocationType;
    private AutoCompleteTextView mAutocompleteTextAreaName;
    private AutoCompleteTextView mAutocompleteTextMunicipalityName;
    private EditText mEditTextPostCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbHelper = new DatabaseHelper(this);
        mErrorMessage = new StringBuffer();

        setContentView(R.layout.activity_city_add);

        mEditTextCityName = (EditText)findViewById(R.id.edit_city_name);

        mRadioGroupLocationType = (RadioGroup)findViewById(R.id.radioGroupLocationType);

        mAutocompleteTextAreaName = (AutoCompleteTextView) findViewById(R.id.edit_area_name);
        ArrayAdapter<String> adapterAreaNames = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
        try {
            for (City c : dbHelper.getDaoCity().queryBuilder().groupByRaw("area").query()) {
                if(c.getArea()!=null) adapterAreaNames.add(c.getArea());
            }
            adapterAreaNames.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            mAutocompleteTextAreaName.setAdapter(adapterAreaNames);
        }catch (SQLException e) {
            e.printStackTrace();
        }

        mAutocompleteTextMunicipalityName = (AutoCompleteTextView) findViewById(R.id.edit_municipality_name);
        ArrayAdapter<String> adapterMunicipalityNames = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item);
        try {
            for (City c : dbHelper.getDaoCity().queryBuilder().groupByRaw("municipality").query()) {
                if(c.getMunicipality()!=null) adapterMunicipalityNames.add(c.getMunicipality());
            }
            adapterMunicipalityNames.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
            mAutocompleteTextMunicipalityName.setAdapter(adapterMunicipalityNames);
        }catch (SQLException e) {
            e.printStackTrace();
        }

        mEditTextPostCode = (EditText)findViewById(R.id.edit_post_code);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mCity = new City();
                    if(validateAllFields()){
                        dbHelper.insertCity(mCity);
                        finish();
                    }else{
                        mCity = null;
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

        mEditTextCityName.setError(null);
        mAutocompleteTextAreaName.setError(null);
        mAutocompleteTextMunicipalityName.setError(null);
        mEditTextPostCode.setError(null);
        View focusView = null;

        if(mEditTextCityName.getText().length()<3){
            mEditTextCityName.setError("Невалидно име");
            valid = false;
            focusView = mEditTextCityName;
        }else {
            mCity.setName(mEditTextCityName.getText().toString());
        }

        switch(mRadioGroupLocationType.getCheckedRadioButtonId()) {
            case R.id.radio_city:
                mCity.setLocationType(LocationType.CITY);
                break;
            case R.id.radio_village:
                mCity.setLocationType(LocationType.VILLAGE);
                break;
            default:
                mErrorMessage.append("Не е избран ТИП\n");
                valid = false;
                break;
        }

        if(mAutocompleteTextAreaName.getText().length()<3){
            mAutocompleteTextAreaName.setError("Невалидно име на област");
            valid = false;
        }else {
            mCity.setArea(mAutocompleteTextAreaName.getText().toString());
        }

        if(mAutocompleteTextMunicipalityName.getText().length()<3){
            mAutocompleteTextMunicipalityName.setError("Невалидно име на община");
            valid = false;
        }else {
            mCity.setMunicipality(mAutocompleteTextMunicipalityName.getText().toString());
        }

        if(mEditTextPostCode.getText().length()<4){
            mEditTextPostCode.setError("Невалиден пощенски код");
            valid = false;
        }else {
            mCity.setPostcode(mEditTextPostCode.getText().toString());
        }

        if(!valid) {
            focusView.requestFocus();
        }

        return valid;

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            OpenHelperManager.releaseHelper();
            dbHelper = null;
        }
    }
}
