package com.armpk.goatregistrator.activities;

import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.database.Breed;
import com.armpk.goatregistrator.database.DatabaseHelper;
import com.armpk.goatregistrator.database.Goat;
import com.armpk.goatregistrator.database.enums.Maturity;
import com.armpk.goatregistrator.database.enums.Sex;
import com.armpk.goatregistrator.fragments.GoatLactationFragment;
import com.armpk.goatregistrator.fragments.GoatMainInfoFragment;
import com.armpk.goatregistrator.fragments.GoatPedigreeFragment;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class GoatAddManuallyActivity extends AppCompatActivity implements
    GoatMainInfoFragment.OnMainInfoFragmentInteractionListener,
    GoatPedigreeFragment.OnPedigreeFragmentInteractionListener,
    GoatLactationFragment.OnLactationsFragmentInteractionListener{

    private StringBuffer mErrorMessage;
    private DatabaseHelper dbHelper;
    private Goat mGoat;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    GoatMainInfoFragment fragmentGoatMainInfo;
    GoatPedigreeFragment fragmentGoatPedigree;
    GoatLactationFragment fragmentGoatLactation;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goat_add_manually);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Добавяне на коза");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mGoat = new Goat();
                    if(validateAllFragmentFields()){
                        dbHelper.insertGoat(mGoat);
                        finish();
                    }else{
                        mGoat = null;
                        Toast error = Toast.makeText(getApplicationContext(), mErrorMessage, Toast.LENGTH_LONG);
                        mErrorMessage.setLength(0);
                        error.show();
                    }

                }
            });
        }

        dbHelper = new DatabaseHelper(this);

        mErrorMessage = new StringBuffer();
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
        if(fragmentGoatMainValidation()){
            return true;
        }
        return false;
    }

    public boolean fragmentGoatMainValidation(){

        boolean valid = true;

        if(fragmentGoatMainInfo!=null){

            if(fragmentGoatMainInfo.getVetCode1().length()<5){
                mErrorMessage.append("Невалиден ВЕТЕРИНАРЕН номер\n");
                valid = false;
            }else {
                mGoat.setFirstVeterinaryNumber(fragmentGoatMainInfo.getVetCode1());
            }

            if(fragmentGoatMainInfo.getBreedingCode1().length()<5){
                mErrorMessage.append("Невалиден РАЗВЪДЕН номер\n");
                valid = false;
            }else{
                mGoat.setFirstBreedingNumber(fragmentGoatMainInfo.getBreedingCode1());
            }

            switch (fragmentGoatMainInfo.getButtonGenderId()){
                case R.id.radio_male:
                    mGoat.setSex(Sex.MALE);
                    break;
                case R.id.radio_female:
                    mGoat.setSex(Sex.FEMALE);
                    break;
                default:
                    mErrorMessage.append("Не е избран пол на козата\n");
                    valid = false;
                    break;
            }

            switch (fragmentGoatMainInfo.getButtonMaturityId()){
                case R.id.radio_adult:
                    mGoat.setMaturity(Maturity.ADULT);
                    break;
                case R.id.radio_kid:
                    mGoat.setMaturity(Maturity.KID);
                    break;
                default:
                    mErrorMessage.append("Не е избрана зрялост на козата\n");
                    valid = false;
                    break;
            }

        }

        return valid;
    }

    /*@Override
    public void onMainInfoFragmentButtonContinue() {
        mViewPager.setCurrentItem(1);
    }*/

    @Override
    public void onMainInfoFragmentButtonContinue(Goat goat) {

    }

    @Override
    public void onMainInfoFragmentFoundGoat(GoatMainInfoFragment.GoatMainInfoContext context, Goat goat) {

    }

    @Override
    public void onPedigreeFragmentButtonContinue() {
        mViewPager.setCurrentItem(2);
    }

    @Override
    public void onLactationsFragmentInteraction(Uri uri) {

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentStatePagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {
            switch (index) {
                case 0:
                    fragmentGoatMainInfo = GoatMainInfoFragment.newInstance(GoatMainInfoFragment.GoatMainInfoContext.ADD, null, null);
                    return fragmentGoatMainInfo;
                case 1:
                    fragmentGoatPedigree = GoatPedigreeFragment.newInstance("", "");
                    return fragmentGoatPedigree;
                case 2:
                    fragmentGoatLactation = GoatLactationFragment.newInstance("", "");
                    return fragmentGoatLactation;
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Основна";
                case 1:
                    return "Родословие";
                case 2:
                    return "Лактации";
            }
            return null;
        }
    }
}
