package com.armpk.goatregistrator.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.database.Address;
import com.armpk.goatregistrator.database.DatabaseHelper;
import com.armpk.goatregistrator.database.Farm;
import com.armpk.goatregistrator.database.Goat;
import com.armpk.goatregistrator.database.Phone;
import com.armpk.goatregistrator.fragments.AddPhoneFragment;
import com.armpk.goatregistrator.fragments.FarmBreedingAddressFragment;
import com.armpk.goatregistrator.fragments.FarmCorrespondenceAddressFragment;
import com.armpk.goatregistrator.fragments.FarmMainInfoFragment;
import com.armpk.goatregistrator.fragments.FarmManagementAddressFragment;
import com.armpk.goatregistrator.fragments.FarmPaymentsFragment;
import com.armpk.goatregistrator.utilities.Globals;
import com.armpk.goatregistrator.utilities.RestConnection;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.ForeignCollection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FarmAddActivity extends AppCompatActivity implements
    FarmMainInfoFragment.OnFarmMainInfoFragmentInteractionListener,
    FarmManagementAddressFragment.OnFarmManagementAddressFragmentInteractionListener,
    FarmCorrespondenceAddressFragment.OnFarmCorrespondenceAddressFragmentInteractionListener,
    FarmBreedingAddressFragment.OnFarmBreedingAddressFragmentInteractionListener,
    FarmPaymentsFragment.OnFarmPaymentsFragmentInteractionListener,
        AddPhoneFragment.OnAddPhoneFragmentInteractionListener, RestConnection.OnConnectionCompleted {

    private StringBuffer mErrorMessage;
    private DatabaseHelper dbHelper;
    private Farm mFarm;
    private List<Phone> mListPhonesToAdd;

    private SectionsPagerAdapter mSectionsPagerAdapter;

    FarmMainInfoFragment fragmentFarmMainInfo;
    FarmManagementAddressFragment fragmentFarmManagementAddress;
    FarmCorrespondenceAddressFragment fragmentFarmCorrespondenceAddress;
    FarmBreedingAddressFragment fragmentFarmBreedingAddress;
    //FarmPaymentsFragment fragmentFarmPayments;

    Address addressManagement;
    Address addressCorrespondence;
    Address addressBreeding;

    public static final String FRAGMENT_TAG_MAIN_INFO = "fragment_farm_main_info";
    public static final String FRAGMENT_TAG_MANAGEMENT_ADDRESS = "fragment_farm_management_address";
    public static final String FRAGMENT_TAG_CORRESPONDENCE_ADDRESS = "fragment_farm_correspondence_address";
    public static final String FRAGMENT_TAG_BREEDING_ADDRESS = "fragment_farm_breeding_address";
    public static final String FRAGMENT_TAG_PAYMENTS = "fragment_farm_payments";

    private ViewPager mViewPager;

    private SharedPreferences mSharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_farm_add);


        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Добавяне на ферма");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOffscreenPageLimit(4);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mFarm = new Farm();
                    if(validateAllFragmentFields()){

                        RestConnection mRestFarmAdd = new RestConnection(FarmAddActivity.this, RestConnection.DataType.FARM_NEW, FarmAddActivity.this);
                        mRestFarmAdd.setAction(RestConnection.Action.POST);
                        try {
                            mFarm.setDateAddedToSystem(new Date(System.currentTimeMillis()));
                            mFarm.setDateLastUpdated(new Date(System.currentTimeMillis()));
                            mFarm.setLastUpdatedByUser(dbHelper.getDaoUser().queryForId(mSharedPreferences.getLong(Globals.SETTING_ACTIVE_USER_ID, 0)));

                            addressManagement.setDateAddedToSystem(new Date(System.currentTimeMillis()));
                            addressManagement.setDateLastUpdated(new Date(System.currentTimeMillis()));
                            addressManagement.setLastUpdatedByUser(dbHelper.getDaoUser().queryForId(mSharedPreferences.getLong(Globals.SETTING_ACTIVE_USER_ID, 0)));

                            addressCorrespondence.setDateAddedToSystem(new Date(System.currentTimeMillis()));
                            addressCorrespondence.setDateLastUpdated(new Date(System.currentTimeMillis()));
                            addressCorrespondence.setLastUpdatedByUser(dbHelper.getDaoUser().queryForId(mSharedPreferences.getLong(Globals.SETTING_ACTIVE_USER_ID, 0)));

                            addressBreeding.setDateAddedToSystem(new Date(System.currentTimeMillis()));
                            addressBreeding.setDateLastUpdated(new Date(System.currentTimeMillis()));
                            addressBreeding.setLastUpdatedByUser(dbHelper.getDaoUser().queryForId(mSharedPreferences.getLong(Globals.SETTING_ACTIVE_USER_ID, 0)));

                            mFarm.setManagementAddress(addressManagement);
                            mFarm.setCorespondenceAddress(addressCorrespondence);
                            mFarm.setBreedingPlaceAddress(addressBreeding);

                            JSONObject data = Globals.objectToJson(mFarm);

                            JSONArray localPhones = new JSONArray();
                            for(Phone p : mListPhonesToAdd) {
                                p.setDateAddedToSystem(new Date(System.currentTimeMillis()));
                                p.setDateLastUpdated(new Date(System.currentTimeMillis()));
                                p.setLastUpdatedByUser(dbHelper.getDaoUser().queryForId(mSharedPreferences.getLong(Globals.SETTING_ACTIVE_USER_ID, 0)));
                                p.setFarm(mFarm);
                                localPhones.put(Globals.objectToJson(p));
                            }
                            data.put("lst_contactPhones", localPhones);

                            mRestFarmAdd.setJSONData(data);
                        } catch (JSONException | SQLException e) {
                            e.printStackTrace();
                        }
                        mRestFarmAdd.execute((Void) null);
                    }else{
                        mFarm = null;
                        addressManagement = null;
                        addressCorrespondence = null;
                        addressBreeding = null;
                        Toast error = Toast.makeText(getApplicationContext(), mErrorMessage, Toast.LENGTH_LONG);
                        mErrorMessage.setLength(0);
                        error.show();
                    }

                }
            });
        }

        dbHelper = new DatabaseHelper(this);

        mErrorMessage = new StringBuffer();

        mListPhonesToAdd = new ArrayList<Phone>();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            OpenHelperManager.releaseHelper();
            dbHelper = null;
        }
    }

    public DatabaseHelper getDatabaseHelper(){
        return dbHelper;
    }

    public boolean validateAllFragmentFields(){

        boolean valid = true;

        if(!fragmentFarmMainInfoValidation()){
            mErrorMessage.append("Моля, проверете ОСНОВНА ИНФОРМАЦИЯ за грешки\n");
            valid = false;
        }
        if(!fragmentFarmManagementAddressValidation()){
            mErrorMessage.append("Моля, проверете АДРЕС НА УПРАВЛЕНИЕ за грешки\n");
            valid = false;
        }
        if(!fragmentFarmCorrespondenceAddressValidation()){
            mErrorMessage.append("Моля, проверете АДРЕС ЗА КОРЕСПОНДЕНЦИЯ за грешки\n");
            valid = false;
        }
        if(!fragmentFarmBreedingAddressValidation()){
            mErrorMessage.append("Моля, проверете АДРЕС ЖИВОТНОВЪДЕН ОБЕКТ за грешки\n");
            valid = false;
        }


        return valid;
    }

    public boolean fragmentFarmMainInfoValidation() {

        boolean valid = true;

        FarmMainInfoFragment fragment = fragmentFarmMainInfo;
        //FarmMainInfoFragment fragment = (FarmMainInfoFragment) mSectionsPagerAdapter.getItem(0);

        if ( fragment != null ) {

            if(fragment.getCompanyName().length()<4){
                mErrorMessage.append("Невалидно име на ФИРМА\n");
                valid = false;
            }else{
                mFarm.setCompanyName(fragment.getCompanyName());
            }

            if(fragment.getMol().length()<8){
                mErrorMessage.append("Невалидно име на МОЛ\n");
                valid = false;
            }else{
                mFarm.setMol(fragment.getMol());
            }

            if(fragment.getEik().length()<10){
                mErrorMessage.append("Невалиден ЕИК номер\n");
                valid = false;
            }else{
                mFarm.setEik(Long.parseLong(fragment.getEik()));
            }

            if(fragment.getBulstat().length()<9){
                mErrorMessage.append("Невалиден БУЛСТАТ номер\n");
                valid = false;
            }else{
                mFarm.setBulstat(fragment.getBulstat());
            }

            /*if(fragment.getFarmer()==null){
                mErrorMessage.append("Невалидно име на ФЕРМЕР\n");
                valid = false;
            }else{
                try {
                    mFarm.setFarmer(dbHelper.getDaoUser().queryForSameId(fragment.getFarmer()));
                } catch (SQLException e) {
                    e.printStackTrace();
                    valid = false;
                }
            }*/

            if(fragment.getBreedingPlaceNumber().length()<1){
                mErrorMessage.append("Невалиден НОМЕР на ЖИВОТНОВЪДЕН ОБЕКТ\n");
                valid = false;
            }else{
                mFarm.setBreedingPlaceNumber(fragment.getBreedingPlaceNumber());
            }

            /*if(fragment.getHerdbookVolumeNumber().length()<1){
                mErrorMessage.append("Невалиден НОМЕР на РОДОСЛОВНА КНИГА\n");
                valid = false;
            }else{
                mFarm.setHerdbookVolumeNumber(fragment.getHerdbookVolumeNumber());
            }*/
        }else{
            valid = false;
        }

        return valid;
    }

    public boolean fragmentFarmManagementAddressValidation() {
        boolean valid = true;

        FarmManagementAddressFragment fragment = fragmentFarmManagementAddress;
        //FarmManagementAddressFragment fragment = (FarmManagementAddressFragment) mSectionsPagerAdapter.getItem(1);

        fragment.setErrorArea(null);
        fragment.setErrorMunicipality(null);
        fragment.setErrorCity(null);
        fragment.setErrorStreetName(null);
        fragment.setErrorPostCode(null);
        //fragment.setErrorEmail(null);

        if ( fragment != null ) {
            addressManagement = new Address();

            if(fragment.getArea().length()<3){
                mErrorMessage.append("Невалидно име на ОБЛАСТ\n");
                fragment.setErrorArea("Невалидно име на ОБЛАСТ\n");
                valid = false;
            }else{
                addressManagement.setRegion(fragment.getArea());
            }

            if(fragment.getMunicipality().length()<3){
                mErrorMessage.append("Невалидно име на ОБЩИНА\n");
                fragment.setErrorArea("Невалидно име на ОБЩИНА\n");
                valid = false;
            }else{
                addressManagement.setRegion(fragment.getArea());
            }

            if(fragment.getCity() == null){
                mErrorMessage.append("Невалидно име на ГРАД/СЕЛО\n");
                fragment.setErrorCity("Невалидно име на ГРАД/СЕЛО\n");
                valid = false;
            }else{
                addressManagement.setCity(fragment.getCity());
            }

            if(fragment.getStreetName().length()<3){
                mErrorMessage.append("Невалидно име на УЛИЦА\n");
                fragment.setErrorStreetName("Невалидно име на УЛИЦА\n");
                valid = false;
            }else{
                addressManagement.setStreet(fragment.getStreetName());
            }

            if(fragment.getPostCode().length()<4){
                mErrorMessage.append("Невалиден ПОЩЕНСКИ КОД\n");
                fragment.setErrorPostCode("Невалиден ПОЩЕНСКИ КОД\n");
                valid = false;
            }else{
                addressManagement.setPostcode(fragment.getPostCode());
            }

            /*if(fragment.getEmail().length()<2){
                mErrorMessage.append("Невалиден ИМЕЙЛ\n");
                fragment.setErrorEmail("Невалиден ИМЕЙЛ\n");
                valid = false;
            }else{
                addressManagement.setEmail(fragment.getEmail());
            }*/
            addressManagement.setEmail(fragment.getEmail());
            addressManagement.setNotes(fragment.getNotes());

            //mFarm.setManagementAddress(addressManagement);
        }else{
            valid = false;
        }
        return valid;
    }

    public boolean fragmentFarmCorrespondenceAddressValidation(){
        boolean valid = true;

        FarmCorrespondenceAddressFragment fragment = fragmentFarmCorrespondenceAddress;
        //FarmCorrespondenceAddressFragment fragment = (FarmCorrespondenceAddressFragment) mSectionsPagerAdapter.getItem(2);

        fragment.setErrorArea(null);
        fragment.setErrorMunicipality(null);
        fragment.setErrorCity(null);
        fragment.setErrorStreetName(null);
        fragment.setErrorPostCode(null);
        //fragment.setErrorEmail(null);

        if ( fragment != null ) {
            addressCorrespondence = new Address();

            if(fragment.getArea().length()<3){
                mErrorMessage.append("Невалидно име на ОБЛАСТ\n");
                fragment.setErrorArea("Невалидно име на ОБЛАСТ\n");
                valid = false;
            }else{
                addressCorrespondence.setRegion(fragment.getArea());
            }

            if(fragment.getMunicipality().length()<3){
                mErrorMessage.append("Невалидно име на ОБЩИНА\n");
                fragment.setErrorArea("Невалидно име на ОБЩИНА\n");
                valid = false;
            }else{
                addressCorrespondence.setRegion(fragment.getArea());
            }

            if(fragment.getCity() == null){
                mErrorMessage.append("Невалидно име на ГРАД/СЕЛО\n");
                fragment.setErrorCity("Невалидно име на ГРАД/СЕЛО\n");
                valid = false;
            }else{
                addressCorrespondence.setCity(fragment.getCity());
            }

            if(fragment.getStreetName().length()<3){
                mErrorMessage.append("Невалидно име на УЛИЦА\n");
                fragment.setErrorStreetName("Невалидно име на УЛИЦА\n");
                valid = false;
            }else{
                addressCorrespondence.setStreet(fragment.getStreetName());
            }

            if(fragment.getPostCode().length()<4){
                mErrorMessage.append("Невалиден ПОЩЕНСКИ КОД\n");
                fragment.setErrorPostCode("Невалиден ПОЩЕНСКИ КОД\n");
                valid = false;
            }else{
                addressCorrespondence.setPostcode(fragment.getPostCode());
            }

            addressCorrespondence.setEmail(fragment.getEmail());
            addressCorrespondence.setNotes(fragment.getNotes());
        }else{
            valid = false;
        }
        return valid;
    }

    public boolean fragmentFarmBreedingAddressValidation(){
        boolean valid = true;

        FarmBreedingAddressFragment fragment = fragmentFarmBreedingAddress;
        //FarmBreedingAddressFragment fragment = (FarmBreedingAddressFragment) mSectionsPagerAdapter.getItem(3);

        fragment.setErrorArea(null);
        fragment.setErrorMunicipality(null);
        fragment.setErrorCity(null);
        fragment.setErrorStreetName(null);
        fragment.setErrorPostCode(null);
        //fragment.setErrorEmail(null);

        if ( fragment != null ) {
            addressBreeding = new Address();

            if(fragment.getArea().length()<3){
                mErrorMessage.append("Невалидно име на ОБЛАСТ\n");
                fragment.setErrorArea("Невалидно име на ОБЛАСТ\n");
                valid = false;
            }else{
                addressBreeding.setRegion(fragment.getArea());
            }

            if(fragment.getMunicipality().length()<3){
                mErrorMessage.append("Невалидно име на ОБЩИНА\n");
                fragment.setErrorArea("Невалидно име на ОБЩИНА\n");
                valid = false;
            }else{
                addressBreeding.setRegion(fragment.getArea());
            }

            if(fragment.getCity() == null){
                mErrorMessage.append("Невалидно име на ГРАД/СЕЛО\n");
                fragment.setErrorCity("Невалидно име на ГРАД/СЕЛО\n");
                valid = false;
            }else{
                addressBreeding.setCity(fragment.getCity());
            }

            if(fragment.getStreetName().length()<3){
                mErrorMessage.append("Невалидно име на УЛИЦА\n");
                fragment.setErrorStreetName("Невалидно име на УЛИЦА\n");
                valid = false;
            }else{
                addressBreeding.setStreet(fragment.getStreetName());
            }

            if(fragment.getPostCode().length()<4){
                mErrorMessage.append("Невалиден ПОЩЕНСКИ КОД\n");
                fragment.setErrorPostCode("Невалиден ПОЩЕНСКИ КОД\n");
                valid = false;
            }else{
                addressBreeding.setPostcode(fragment.getPostCode());
            }

            addressBreeding.setEmail(fragment.getEmail());
            addressBreeding.setNotes(fragment.getNotes());
        }else{
            valid = false;
        }
        return valid;
    }

    @Override
    public void onFarmMainInfoFragmentInteraction() {

    }

    @Override
    public void onFarmManagementAddressFragmentInteraction() {

    }

    @Override
    public void onFarmCorrespondenceAddressFragmentInteraction() {

    }

    @Override
    public void onFarmBreedingAddressFragmentInteraction() {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onAddPhoneFragmentPositiveClick(Phone phone) {
        mListPhonesToAdd.add(phone);
        if(fragmentFarmMainInfo!=null){
            fragmentFarmMainInfo.addContactButton(phone);
        }
    }

    @Override
    public void onAddPhoneFragmentNegativeClick() {

    }

    @Override
    public void onResultReceived(RestConnection.Action action, RestConnection.DataType dataType, String result) {
        if(result!=null){
            try {
                JSONObject data = new JSONObject(result);
                JSONArray phonesArray = new JSONArray(data.getString("lst_contactPhones"));
                data.remove("lst_contactPhones");
                Farm f = Globals.jsonToObject(data, Farm.class);
                for (int i =0; i<phonesArray.length(); i++) {
                    Phone p = Globals.jsonToObject(new JSONObject(phonesArray.get(i).toString()), Phone.class);
                    p.setFarm(f);
                    dbHelper.updatePhoneByDate(p);
                }

                if(dbHelper.updateFarmByDate(f)){
                    finish();
                }else{
                    Toast error = Toast.makeText(getApplicationContext(), "Възникна грешка при запис в устройството!\nМоля, опитайте отново", Toast.LENGTH_LONG);
                    error.show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        RestConnection.closeProgressDialog();
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        HashMap<Integer, Fragment> mPageReferenceMap;

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
            mPageReferenceMap = new HashMap<Integer, Fragment>();
        }

        public Fragment getFragment(int key) {
            return mPageReferenceMap.get(key);
        }

        @Override
        public Fragment getItem(int index) {
            switch (index) {
                case 0:
                    fragmentFarmMainInfo = FarmMainInfoFragment.newInstance("", "");
                    mPageReferenceMap.put(index, fragmentFarmMainInfo);
                    return fragmentFarmMainInfo;
                case 1:
                    fragmentFarmManagementAddress = FarmManagementAddressFragment.newInstance("", "");
                    mPageReferenceMap.put(index, fragmentFarmManagementAddress);
                    return fragmentFarmManagementAddress;
                case 2:
                    fragmentFarmCorrespondenceAddress = FarmCorrespondenceAddressFragment.newInstance("", "");
                    mPageReferenceMap.put(index, fragmentFarmCorrespondenceAddress);
                    return fragmentFarmCorrespondenceAddress;
                case 3:
                    fragmentFarmBreedingAddress = FarmBreedingAddressFragment.newInstance("", "");
                    mPageReferenceMap.put(index, fragmentFarmBreedingAddress);
                    return fragmentFarmBreedingAddress;
                /*case 4:
                    fragmentFarmPayments = FarmPaymentsFragment.newInstance("", "");
                    mPageReferenceMap.put(index, fragmentFarmPayments);
                    return fragmentFarmPayments;*/
            }
            return null;
        }

        public void destroyItem(View container, int position, Object object) {
            super.destroyItem(container, position, object);
            mPageReferenceMap.remove(position);
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Основна Информация";
                case 1:
                    return "Адрес управление";
                case 2:
                    return "Адрес кореспонденция";
                case 3:
                    return "Адрес животн. обект";
                /*case 4:
                    return "Плащания";*/
            }
            return null;
        }
    }
}
