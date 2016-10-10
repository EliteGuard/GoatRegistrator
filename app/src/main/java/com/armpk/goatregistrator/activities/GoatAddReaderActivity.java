package com.armpk.goatregistrator.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.AsyncTaskLoader;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.os.Vibrator;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.adapters.FarmAutocompleteAdapter;
import com.armpk.goatregistrator.database.Breed;
import com.armpk.goatregistrator.database.DatabaseHelper;
import com.armpk.goatregistrator.database.Farm;
import com.armpk.goatregistrator.database.FarmGoat;
import com.armpk.goatregistrator.database.Goat;
import com.armpk.goatregistrator.database.GoatFromVetIs;
import com.armpk.goatregistrator.database.GoatMeasurement;
import com.armpk.goatregistrator.database.GoatStatus;
import com.armpk.goatregistrator.database.Herd;
import com.armpk.goatregistrator.database.Measurement;
import com.armpk.goatregistrator.database.VisitProtocol;
import com.armpk.goatregistrator.database.enums.Maturity;
import com.armpk.goatregistrator.database.enums.MeasurementType;
import com.armpk.goatregistrator.database.enums.Sex;
import com.armpk.goatregistrator.fragments.DatePickerFragment;
import com.armpk.goatregistrator.services.BluetoothService;
import com.armpk.goatregistrator.utilities.Globals;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

public class GoatAddReaderActivity extends AppCompatActivity
        implements DatePickerFragment.OnDatePickerFragmentInteractionListener{

    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> BTArrayAdapter;
    private BluetoothService mChatService = null;
    private String mConnectedDeviceName = null;

    private StringBuffer mErrorMessage;
    private DatabaseHelper dbHelper;

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE_SECURE = 1;
    private static final int REQUEST_CONNECT_DEVICE_INSECURE = 2;
    private static final int REQUEST_ENABLE_BT = 3;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    private static final String ARG_FARM = "farm";
    private static final String ARG_VISIT_PROTOCOL = "visit_protocol";

    private VisitProtocol mVisitProtocol;

    GoatStatus goatStatus = null;
    GoatStatus tempGoatStatus = null;

    private String GOATS_KEY;
    private Set<String> GOATS_SET;
    private ArrayList<Goat> GOATS_ARRAY;
    private int currentIndex = -1;

    private Menu mMainMenu;
    private MenuItem mMenuPrevious;
    private MenuItem mMenuNext;

    private Farm mFarmRead;
    private Farm mFarmRead2;
    private Farm mFarmFound;

    private Farm mFarmByGoat;

    private Herd mHerdRead;
    private Herd mHerdFind;

    public boolean isFirstGoat = true;
    private GoatFromVetIs mGoatVetIs;
    private Goat mGoatFound;
    private List<GoatMeasurement> mGoatMeasurementsFound;
    private List<Measurement> measBonitirovka;

    private SharedPreferences mSharedPreferences;

    NestedScrollView mScrollMain;

    private AutoCompleteTextView mAutocompleteTextFarm;
    List<Farm> mFarmsRead;

    private Spinner mSpinnerHerd;
    ArrayAdapter<String> adapterHerdsRead;

    private AutoCompleteTextView mAutocompleteTextFarm2;
    private Spinner mSpinnerHerd2;
    private Button mButtonShowFarm2;

    private EditText mEditTextVetCode1;
    private EditText mEditTextBreedingCode1;
    private RadioGroup mRadioGroupGender;
    private Spinner mSpinnerBreed;
    ArrayAdapter<String> adapterBreeds;

    private SeekBar mSeekbarYears;
    private TextView mTextYears;
    private LinearLayout mLinearScale;
    private Button mButtonBirthdate;
    private Date mDateBirthSelected;
    private Date mDateBirthFound;

    private RadioGroup mRadioGroupMaturity;
    private EditText mEditTextNotes;

    private LinearLayout mLinearBinitirovka1;
    private TextView mTextBonititirovka1;
    private SeekBar mSeekBonitirovka1;
    private LinearLayout mLinearBinitirovka2;
    private TextView mTextBonititirovka2;
    private SeekBar mSeekBonitirovka2;
    private LinearLayout mLinearBinitirovka3;
    private TextView mTextBonititirovka3;
    private SeekBar mSeekBonitirovka3;
    private LinearLayout mLinearBinitirovka4;
    private TextView mTextBonititirovka4;
    private SeekBar mSeekBonitirovka4;
    private LinearLayout mLinearBinitirovka5;
    private TextView mTextBonititirovka5;
    private SeekBar mSeekBonitirovka5;

    private Button mButtonAdd;
    private Button mButtonSave;


    private AutoCompleteTextView mAutocompleteTextFarmFound;
    private Spinner mSpinnerHerdFound;
    ArrayAdapter<String> adapterHerdsFound;
    private EditText mEditTextVetCode1Found;
    private EditText mEditTextVetCode2Found;
    private EditText mEditTextBreedingCode1Found;
    private EditText mEditTextBreedingCode2Found;
    private TextView mTextGenderBook;
    private TextView mTextGenderVetIS;
    private TextView mTextBreedBook;
    private TextView mTextBreedVetIS;
    private Button mButtonBirthdateBook;
    private TextView mTextAppliedForSelectionControlYear;
    private TextView mTextSelectionControlVetis;
    private TextView mTextInclusionStatus;
    private TextView mTextNumberInCert;
    private TextView mTextCertificate;
    private TextView mTextNumberInWastageProtocol;
    private TextView mTextWastageProtocol;
    private TextView mTextDateDeregistered;

    private GoatLoader mGoatLoaderFromVet;
    private GoatLoader mGoatLoaderFromBreed;

    private RelativeLayout mRelativeProgress;

    private Button mButtonMoveToFV2;
    private Button mButtonMoveToFB2;

    private Queue<String> readContentQueue = new LinkedList<String>();
    //Vibrator vibrator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /*if(getResources().getBoolean(R.bool.portrait_only)){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }*/


        dbHelper = new DatabaseHelper(this);
        mErrorMessage = new StringBuffer();
        mSharedPreferences = getSharedPreferences(getPackageName(), Context.MODE_PRIVATE);
        //vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        BTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothDevice.ACTION_UUID);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(bReceiver, filter);
        //this.registerReceiver(bReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

        if(mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(),"Your device does not support Bluetooth", Toast.LENGTH_LONG).show();
        } else {
            turnBluetoothOn();
        }

        setContentView(R.layout.activity_goat_add_reader);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Прочитане на коза");
        mScrollMain = (NestedScrollView)findViewById(R.id.scrollMain);

        mAutocompleteTextFarm = (AutoCompleteTextView)findViewById(R.id.autocompleteFarm);

        mSpinnerHerd = (Spinner)findViewById(R.id.spinnerHerd);
        adapterHerdsRead = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapterHerdsRead.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerHerd.setAdapter(adapterHerdsRead);

        mAutocompleteTextFarm2 = (AutoCompleteTextView)findViewById(R.id.autocompleteFarm2);
        mSpinnerHerd2 = (Spinner)findViewById(R.id.spinnerHerd2);
        mSpinnerHerd2.setAdapter(adapterHerdsRead);
        mButtonShowFarm2 = (Button)findViewById(R.id.buttonShowFarm2);

        mEditTextVetCode1 = (EditText)findViewById(R.id.editTextVetCode1);
        mEditTextBreedingCode1 = (EditText)findViewById(R.id.editTextBreedingCode1);
        mRadioGroupGender = (RadioGroup)findViewById(R.id.radioGroupGender);
        mSpinnerBreed = (Spinner)findViewById(R.id.spinnerBreed);
        adapterBreeds = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);

        mSeekbarYears = (SeekBar)findViewById(R.id.seekbarYears);
        mTextYears = (TextView)findViewById(R.id.textYears);
        mLinearScale = (LinearLayout)findViewById(R.id.linearScale);
        mButtonBirthdate = (Button)findViewById(R.id.buttonBirthdatePick);
        mRadioGroupMaturity = (RadioGroup)findViewById(R.id.radioGroupMaturity);
        mEditTextNotes = (EditText)findViewById(R.id.editTextNotes);
        mLinearBinitirovka1 = (LinearLayout)findViewById(R.id.linearBonitirovka1);
        mTextBonititirovka1 = (TextView)findViewById(R.id.textBonitorvka1);
        mSeekBonitirovka1 = (SeekBar)findViewById(R.id.seekBarBonitirovka1);
        mLinearBinitirovka2 = (LinearLayout)findViewById(R.id.linearBonitirovka2);
        mTextBonititirovka2 = (TextView)findViewById(R.id.textBonitorvka2);
        mSeekBonitirovka2 = (SeekBar)findViewById(R.id.seekBarBonitirovka2);
        mLinearBinitirovka3 = (LinearLayout)findViewById(R.id.linearBonitirovka3);
        mTextBonititirovka3 = (TextView)findViewById(R.id.textBonitorvka3);
        mSeekBonitirovka3 = (SeekBar)findViewById(R.id.seekBarBonitirovka3);
        mLinearBinitirovka4 = (LinearLayout)findViewById(R.id.linearBonitirovka4);
        mTextBonititirovka4 = (TextView)findViewById(R.id.textBonitorvka4);
        mSeekBonitirovka4 = (SeekBar)findViewById(R.id.seekBarBonitirovka4);
        mLinearBinitirovka5 = (LinearLayout)findViewById(R.id.linearBonitirovka5);
        mTextBonititirovka5 = (TextView)findViewById(R.id.textBonitorvka5);
        mSeekBonitirovka5 = (SeekBar)findViewById(R.id.seekBarBonitirovka5);
        mButtonAdd = (Button)findViewById(R.id.buttonAdd);
        mButtonSave = (Button)findViewById(R.id.buttonSave);


        mAutocompleteTextFarmFound = (AutoCompleteTextView)findViewById(R.id.autocompleteFarmFound);

        mSpinnerHerdFound = (Spinner)findViewById(R.id.spinnerHerdFound);
        adapterHerdsFound = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adapterHerdsFound.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerHerdFound.setAdapter(adapterHerdsFound);

        mEditTextVetCode1Found = (EditText)findViewById(R.id.editTextVetCode1Found);
        mEditTextVetCode2Found = (EditText)findViewById(R.id.editTextVetCode2Found);
        mEditTextBreedingCode1Found = (EditText)findViewById(R.id.editTextBreedingCode1Found);
        mEditTextBreedingCode2Found = (EditText)findViewById(R.id.editTextBreedingCode2Found);
        mTextGenderBook = (TextView)findViewById(R.id.textGenderBook);
        mTextGenderVetIS = (TextView)findViewById(R.id.textGenderVetIS);
        mTextBreedBook = (TextView)findViewById(R.id.textBreedBook);
        mTextBreedVetIS = (TextView)findViewById(R.id.textBreedVetIS);
        mButtonBirthdateBook = (Button)findViewById(R.id.buttonBirthdateBook);
        mButtonBirthdateBook.setEnabled(false);
        mTextAppliedForSelectionControlYear = (TextView)findViewById(R.id.textAppliedForSelectionControlYear);
        mTextSelectionControlVetis = (TextView)findViewById(R.id.textSelectionControlVetis);
        mTextInclusionStatus = (TextView)findViewById(R.id.textInclusionStatus);
        mTextInclusionStatus.setText(getString(R.string.text_status_2016_sc, ""));
        mTextNumberInCert = (TextView)findViewById(R.id.textNumberInCert);
        mTextCertificate = (TextView)findViewById(R.id.textCertificate);
        mTextNumberInWastageProtocol = (TextView)findViewById(R.id.textNumberInWastageProtocol);
        mTextWastageProtocol = (TextView)findViewById(R.id.textWastageProtocol);
        mTextDateDeregistered = (TextView)findViewById(R.id.textDateDeregistered);

        mRelativeProgress = (RelativeLayout)findViewById(R.id.relativeProgress);

        mButtonMoveToFV2 = (Button)findViewById(R.id.buttonMoveToFV2);
        mButtonMoveToFB2 = (Button)findViewById(R.id.buttonMoveToFB2);


        if (getIntent().getExtras()!=null) {
            mVisitProtocol = (VisitProtocol) getIntent().getExtras().getSerializable(ARG_VISIT_PROTOCOL);
            mFarmRead = mFarmRead2 = (Farm) getIntent().getExtras().getSerializable(ARG_FARM);
            //mFarmFound = (Farm) getIntent().getExtras().getSerializable(ARG_FARM);
            /*initFarmRead();
            initHerdRead();*/
            InitActivity ia = new InitActivity(this);
            ia.execute((Void) null);
            try {
                initData();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else{
            finish();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_goat_add_reader, menu);
        mMainMenu = menu;

        mMenuPrevious = menu.findItem(R.id.action_previous);
        mMenuNext = menu.findItem(R.id.action_next);

        if(mMenuPrevious!=null && mMenuNext!=null){
            if(GOATS_SET.size()>0){
                mMenuPrevious.setEnabled(true);
            }else{
                mMenuPrevious.setEnabled(false);
            }
            mMenuNext.setEnabled(false);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_previous:
                loadPreviousRecord();
                return true;
            case R.id.action_next:
                loadNextRecord();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    private void initData() throws SQLException {
        initFarmRead();
        initFarmSearch();
        initHerdRead();
        initHerdSearch();
        initBreeds();
        initRadioGroups();
        initYears();
        mButtonBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBirthdatePickerDialog();
            }
        });
        initBonitirovka();
        initAddSaveButtons();

        initVetCode1();
        initBreedCode1();

        initMoveButtons();

        /*readContentQueue.add("VetNum: 999999999999999 BreedNum: 00009 Years: 0");
        readContentQueue.add("VetNum: 111112222233333 BreedNum: 11111 Years: 1");
        readContentQueue.add("VetNum: 111112222244444 BreedNum: 22222 Years: 2");
        readContentQueue.add("VetNum: 111112222255555 BreedNum: 33333 Years: 3");
        readContentQueue.add("VetNum: 111112222266666 BreedNum: 44444 Years: 4");
        readContentQueue.add("VetNum: 111112222277777 BreedNum: 55555 Years: 5");*/
        getNextFromBuffer();
    }

    private void initYears() {
        for(int i=0; i<10; i++){
            TextView tv = new TextView(this);
            tv.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT, 1f));
            tv.setPadding(0, 0, 0, 0);
            tv.setText(String.valueOf(i));
            mLinearScale.addView(tv);
        }
        mTextYears.setText(getString(R.string.placeholder_years_sc, 0));
        mSeekbarYears.setProgress(0);
        mSeekbarYears.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                mTextYears.setText(getString(R.string.placeholder_years_sc, i));

                Calendar c = Calendar.getInstance();
                Random r = new Random();
                int y = c.get(Calendar.YEAR) - i;
                int m = r.nextInt(3);
                int d = r.nextInt(28);

                c.set(y, m, d);
                mButtonBirthdate.setText(Globals.getDateShort(c.getTime()));
                mDateBirthSelected = c.getTime();

                RadioButton rbAdult = (RadioButton) findViewById(R.id.radio_adult);
                RadioButton rbKid = (RadioButton) findViewById(R.id.radio_kid);
                Calendar yearBack = Calendar.getInstance();
                yearBack.set(Calendar.YEAR, yearBack.get(Calendar.YEAR)-1);
                if(mDateBirthSelected.before(yearBack.getTime())){
                    rbAdult.setChecked(true);
                    rbKid.setChecked(false);
                }else{
                    rbAdult.setChecked(false);
                    rbKid.setChecked(true);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void initFarmRead(){
        FarmLoader fl = new FarmLoader(this);
        fl.startLoading();

        if(mFarmRead!=null){
            mAutocompleteTextFarm.setText(mFarmRead.getCompanyName());
        }
        mAutocompleteTextFarm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mAutocompleteTextFarm.setText(
                        ((TextView)view.findViewById(R.id.text_name)).getText().toString()
                );
                mFarmRead = (Farm) adapterView.getItemAtPosition(i);
                try {
                    initHerdRead();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        mButtonShowFarm2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mButtonShowFarm2.setVisibility(View.GONE);
            }
        });

        /*if(mFarmRead2!=null){
            mAutocompleteTextFarm2.setText(mFarmRead2.getCompanyName());
        }*/
        mAutocompleteTextFarm2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mAutocompleteTextFarm2.setText(
                        ((TextView)view.findViewById(R.id.text_name)).getText().toString()
                );
                mFarmRead2 = (Farm) adapterView.getItemAtPosition(i);
                try {
                    initHerdRead();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    private void initFarmSearch(){
        if(mFarmFound!=null){
            mAutocompleteTextFarmFound.setText(mFarmFound.getCompanyName());
        }
        mAutocompleteTextFarmFound.setAdapter(new FarmAutocompleteAdapter(this, mFarmsRead));
        mAutocompleteTextFarmFound.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mAutocompleteTextFarmFound.setText(
                        ((TextView)view.findViewById(R.id.text_name)).getText().toString()
                );
                mFarmFound = (Farm) adapterView.getItemAtPosition(i);
                initHerdSearch();
            }
        });
    }

    private void initHerdRead() throws SQLException {
        adapterHerdsRead.clear();
        for(Herd h : dbHelper.getHerdsForFarm(mFarmRead)){
            adapterHerdsRead.add(String.valueOf(h.getHerdNumber()));
        }
        adapterHerdsRead.notifyDataSetChanged();
    }

    private void initHerdSearch(){
        adapterHerdsFound.clear();
        if(mFarmFound!=null) {
            try {
                for (Herd h : dbHelper.getHerdsForFarm(mFarmFound)) {
                    adapterHerdsFound.add(String.valueOf(h.getHerdNumber()));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        adapterHerdsFound.notifyDataSetChanged();
    }

    private void initBreeds() throws SQLException {
        adapterBreeds.add("Непосочена");
        for(Breed b : dbHelper.getDaoBreed().queryBuilder().selectColumns("_id", "breedName", "codeName").query()){
            adapterBreeds.add(String.valueOf(b.getCodeName()));
        }
        adapterBreeds.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mSpinnerBreed.setAdapter(adapterBreeds);
    }

    private void initRadioGroups(){
        mRadioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(group.getCheckedRadioButtonId()) {
                    case R.id.radio_male:
                        //if (checked)
                        // Pirates are the best
                        break;
                    case R.id.radio_female:
                        //if (checked)
                        // Ninjas rule
                        break;
                }
            }
        });
        mRadioGroupMaturity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(group.getCheckedRadioButtonId()) {
                    case R.id.radio_adult:
                        //if (checked)
                        // Pirates are the best
                        break;
                    case R.id.radio_kid:
                        //if (checked)
                        // Ninjas rule
                        break;
                }
            }
        });
        RadioButton rbKid = (RadioButton) findViewById(R.id.radio_kid);
        if(rbKid!=null) rbKid.setChecked(true);
    }

    private void initBonitirovka(){
        try {
            measBonitirovka = dbHelper.getDaoMeasurement().queryBuilder()
                    .selectColumns("_id", "name", "codeName", "minValue", "maxValue", "allowedValues", "description").orderBy("name", true)
                    .where().eq("type", MeasurementType.BONITIROVKA).query();

            if(measBonitirovka.get(0)!=null) setBonitirovka(mLinearBinitirovka1, mTextBonititirovka1, mSeekBonitirovka1, measBonitirovka.get(0));
            if(measBonitirovka.get(1)!=null) setBonitirovka(mLinearBinitirovka2, mTextBonititirovka2, mSeekBonitirovka2, measBonitirovka.get(1));
            if(measBonitirovka.get(2)!=null) setBonitirovka(mLinearBinitirovka3, mTextBonititirovka3, mSeekBonitirovka3, measBonitirovka.get(2));
            if(measBonitirovka.get(3)!=null) setBonitirovka(mLinearBinitirovka4, mTextBonititirovka4, mSeekBonitirovka4, measBonitirovka.get(3));
            if(measBonitirovka.get(4)!=null) setBonitirovka(mLinearBinitirovka5, mTextBonititirovka5, mSeekBonitirovka5, measBonitirovka.get(4));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void setBonitirovka(LinearLayout linear, final TextView text, SeekBar seekBar, final Measurement meas){
        linear.setVisibility(View.VISIBLE);
        text.setText(getString(R.string.text_two_values_with_sc_delimiter, meas.getName(), String.valueOf(0)));
        seekBar.setMax(5);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                text.setText(getString(R.string.text_two_values_with_sc_delimiter, meas.getName(), String.valueOf(i)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void resetBonitorvka(){
        mSeekBonitirovka1.setProgress(0);
        mTextBonititirovka1.setText(getString(R.string.text_two_values_with_sc_delimiter, measBonitirovka.get(0).getName(), String.valueOf(0)));

        mSeekBonitirovka2.setProgress(0);
        mTextBonititirovka2.setText(getString(R.string.text_two_values_with_sc_delimiter, measBonitirovka.get(1).getName(), String.valueOf(0)));

        mSeekBonitirovka3.setProgress(0);
        mTextBonititirovka3.setText(getString(R.string.text_two_values_with_sc_delimiter, measBonitirovka.get(2).getName(), String.valueOf(0)));

        mSeekBonitirovka4.setProgress(0);
        mTextBonititirovka4.setText(getString(R.string.text_two_values_with_sc_delimiter, measBonitirovka.get(3).getName(), String.valueOf(0)));

        mSeekBonitirovka5.setProgress(0);
        mTextBonititirovka5.setText(getString(R.string.text_two_values_with_sc_delimiter, measBonitirovka.get(4).getName(), String.valueOf(0)));
    }



    private void initAddSaveButtons(){

        try {
            goatStatus = dbHelper.getDaoGoatStatus().queryBuilder()
                    .where().like("statusName", "%налич%").queryForFirst();

            tempGoatStatus = new GoatStatus();
            tempGoatStatus.setStatusName("Налична");
            tempGoatStatus.setCodeName("Налична");
            tempGoatStatus.setDateAddedToSystem(new Date(System.currentTimeMillis()));
            tempGoatStatus.setDateLastUpdated(new Date(System.currentTimeMillis()));
            tempGoatStatus.setLastUpdatedByUser(dbHelper.getDaoUser().queryForId(mSharedPreferences.getLong(Globals.SETTING_ACTIVE_USER_ID, 1)));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        mButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(validateAge() && validateBreed() && validateGender()) {
                    //String key = Globals.TEMPORARY_GOATS_FOR_PROTOCOL + String.valueOf(mVisitProtocol.getFarm().getId()) + "_" + String.valueOf(mVisitProtocol.getDateAddedToSystem().getTime());
                    //Set<String> goats = mSharedPreferences.getStringSet(key, new HashSet<String>());

                    Goat goat = new Goat();
                    if(goatStatus!=null) goat.setStatus(goatStatus);
                    else goat.setStatus(tempGoatStatus);
                    goat.setFirstVeterinaryNumber(mEditTextVetCode1.getText().toString());
                    goat.setFirstBreedingNumber(mEditTextBreedingCode1.getText().toString());
                    goat.setHerd(mHerdRead);
                    switch (mRadioGroupGender.getCheckedRadioButtonId()) {
                        case R.id.radio_male:
                            goat.setSex(Sex.MALE);
                            break;
                        case R.id.radio_female:
                            goat.setSex(Sex.FEMALE);
                            break;
                    }
                    try {
                        goat.setBreed(dbHelper.getDaoBreed().queryBuilder().where().eq("codeName", adapterBreeds.getItem(mSpinnerBreed.getSelectedItemPosition())).queryForFirst());


                        if (mDateBirthSelected != null) {
                            goat.setBirthDate(mDateBirthSelected);
                        }
                        switch (mRadioGroupMaturity.getCheckedRadioButtonId()) {
                            case R.id.radio_adult:
                                goat.setMaturity(Maturity.ADULT);
                                break;
                            case R.id.radio_kid:
                                goat.setMaturity(Maturity.KID);
                                break;
                        }
                        goat.setNotes(mEditTextNotes.getText().toString());
                        goat.setDateAddedToSystem(new Date(System.currentTimeMillis()));
                        goat.setDateLastUpdated(new Date(System.currentTimeMillis()));
                        goat.setLastUpdatedByUser(dbHelper.getDaoUser().queryForId(mSharedPreferences.getLong(Globals.SETTING_ACTIVE_USER_ID, 1)));

                        saveFarmForGoat(goat, mFarmFound);
                        saveTemporaryBonitirovka(goat);

                        GOATS_ARRAY.add(goat);
                        GOATS_SET.add(Globals.objectToJson(goat).toString());
                        Globals.savePreferences(GOATS_KEY, GOATS_SET, getApplicationContext());
                        loadNextRecord();

                        updateVisitProtocolModified();

                        clearOnAdd();
                        clearSearchResult();
                        resetBonitorvka();

                        getNextFromBuffer();
                    } catch (SQLException | JSONException e) {
                        e.printStackTrace();
                    }
                }else{
                    Toast error = Toast.makeText(getApplicationContext(), mErrorMessage, Toast.LENGTH_LONG);
                    mErrorMessage.setLength(0);
                    error.show();
                }
            }
        });

        mButtonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentIndex>=GOATS_ARRAY.size()) {
                    if (validateAge() && validateBreed() && validateGender()) {
                        /*String key = Globals.TEMPORARY_GOATS_FOR_PROTOCOL + String.valueOf(mVisitProtocol.getFarm().getId()) + "_" + String.valueOf(mVisitProtocol.getDateAddedToSystem().getTime());
                        Set<String> goats = mSharedPreferences.getStringSet(key, new HashSet<String>());*/

                        Goat goat = mGoatFound;
                        if (goat != null) {
                            if(goatStatus!=null) goat.setStatus(goatStatus);
                            else goat.setStatus(tempGoatStatus);
                            goat.setFirstVeterinaryNumber(mEditTextVetCode1Found.getText().toString());
                            goat.setSecondVeterinaryNumber(mEditTextVetCode2Found.getText().toString());
                            goat.setFirstBreedingNumber(mEditTextBreedingCode1Found.getText().toString());

                            goat.setSecondBreedingNumber(mEditTextBreedingCode2Found.getText().toString());
                            switch (mRadioGroupGender.getCheckedRadioButtonId()) {
                                case R.id.radio_male:
                                    goat.setSex(Sex.MALE);
                                    break;
                                case R.id.radio_female:
                                    goat.setSex(Sex.FEMALE);
                                    break;
                            }
                            try {
                                goat.setBreed(dbHelper.getDaoBreed().queryBuilder().where().eq("codeName", adapterBreeds.getItem(mSpinnerBreed.getSelectedItemPosition())).queryForFirst());
                                if (mDateBirthSelected != null) {
                                    goat.setBirthDate(mDateBirthSelected);
                                }
                                switch (mRadioGroupMaturity.getCheckedRadioButtonId()) {
                                    case R.id.radio_adult:
                                        goat.setMaturity(Maturity.ADULT);
                                        break;
                                    case R.id.radio_kid:
                                        goat.setMaturity(Maturity.KID);
                                        break;
                                }
                                goat.setNotes(mEditTextNotes.getText().toString());
                                goat.setDateAddedToSystem(new Date(System.currentTimeMillis()));
                                goat.setDateLastUpdated(new Date(System.currentTimeMillis()));
                                goat.setLastUpdatedByUser(dbHelper.getDaoUser().queryForId(mSharedPreferences.getLong(Globals.SETTING_ACTIVE_USER_ID, 1)));

                                saveFarmForGoat(goat, mFarmFound);
                                saveTemporaryBonitirovka(goat);

                                GOATS_ARRAY.add(goat);
                                GOATS_SET.add(Globals.objectToJson(goat).toString());
                                Globals.savePreferences(GOATS_KEY, GOATS_SET, getApplicationContext());
                                loadNextRecord();

                                updateVisitProtocolModified();

                                clearOnAdd();
                                clearSearchResult();
                                resetBonitorvka();

                                getNextFromBuffer();
                            } catch (SQLException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Toast error = Toast.makeText(getApplicationContext(), mErrorMessage, Toast.LENGTH_LONG);
                        mErrorMessage.setLength(0);
                        error.show();
                    }
                }else{
                    Goat goat = GOATS_ARRAY.get(currentIndex);
                    String markedGoat = null;
                    try {
                        markedGoat = Globals.objectToJson(GOATS_ARRAY.get(currentIndex)).toString();
                        saveEditedRecord(goat);
                        if(GOATS_SET.contains(markedGoat)){
                            if(GOATS_SET.remove(markedGoat)){
                                GOATS_SET.add(Globals.objectToJson(goat).toString());
                                Globals.savePreferences(GOATS_KEY, GOATS_SET, getApplicationContext());
                                saveTemporaryBonitirovka(goat);
                                loadNextRecord();
                                updateVisitProtocolModified();
                            }
                        }
                    } catch (JSONException | SQLException e){
                        e.printStackTrace();
                    }


                }
            }
        });
    }

    private boolean validateAge(){
        boolean success = false;

        if(mDateBirthSelected!=null){
            success = true;
        }else{
            mErrorMessage.append("Непосочена ВЪЗРАСТ!\n");
        }

        return success;
    }

    private boolean validateBreed(){
        boolean success = false;

        if(!adapterBreeds.getItem(mSpinnerBreed.getSelectedItemPosition()).equals("Непосочена")){
            success = true;
        }else{
            mErrorMessage.append("Непосочена ПОРОДА!\n");
        }

        return success;
    }

    private boolean validateGender(){
        boolean success = false;

        RadioButton rbMale = (RadioButton) findViewById(R.id.radio_male);
        RadioButton rbFemale = (RadioButton) findViewById(R.id.radio_female);

        if(rbMale.isChecked() || rbFemale.isChecked()){
            success = true;
        }else{
            mErrorMessage.append("Непосочен ПОЛ!\n");
        }

        return success;
    }


    private void saveTemporaryBonitirovka(Goat goat){

        GoatMeasurement goatBonit1 = new GoatMeasurement();
        goatBonit1.setMeasurement(measBonitirovka.get(0));
        goatBonit1.setGoat(goat);
        goatBonit1.setValue(mSeekBonitirovka1.getProgress());

        GoatMeasurement goatBonit2 = new GoatMeasurement();
        goatBonit2.setMeasurement(measBonitirovka.get(1));
        goatBonit2.setGoat(goat);
        goatBonit2.setValue(mSeekBonitirovka2.getProgress());

        GoatMeasurement goatBonit3 = new GoatMeasurement();
        goatBonit3.setMeasurement(measBonitirovka.get(2));
        goatBonit3.setGoat(goat);
        goatBonit3.setValue(mSeekBonitirovka3.getProgress());

        GoatMeasurement goatBonit4 = new GoatMeasurement();
        goatBonit4.setMeasurement(measBonitirovka.get(3));
        goatBonit4.setGoat(goat);
        goatBonit4.setValue(mSeekBonitirovka4.getProgress());

        GoatMeasurement goatBonit5 = new GoatMeasurement();
        goatBonit5.setMeasurement(measBonitirovka.get(4));
        goatBonit5.setGoat(goat);
        goatBonit5.setValue(mSeekBonitirovka5.getProgress());

        JSONArray measurements = new JSONArray();
        try {
            measurements.put(Globals.objectToJson(goatBonit1));
            measurements.put(Globals.objectToJson(goatBonit2));
            measurements.put(Globals.objectToJson(goatBonit3));
            measurements.put(Globals.objectToJson(goatBonit4));
            measurements.put(Globals.objectToJson(goatBonit5));

            StringBuffer key = new StringBuffer();
            if(goat.getFirstVeterinaryNumber()!=null) key.append(goat.getFirstVeterinaryNumber());
            if(goat.getSecondVeterinaryNumber()!=null) key.append(goat.getSecondVeterinaryNumber());
            if(goat.getFirstBreedingNumber()!=null) key.append(goat.getFirstBreedingNumber());
            if(goat.getSecondBreedingNumber()!=null) key.append(goat.getSecondBreedingNumber());
            Globals.savePreferences(key.toString(), measurements.toString(), this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadTemporaryBonitirovka(Goat keygoat){
        try{
            StringBuffer key = new StringBuffer();
            if(keygoat.getFirstVeterinaryNumber()!=null) key.append(keygoat.getFirstVeterinaryNumber());
            if(keygoat.getSecondVeterinaryNumber()!=null) key.append(keygoat.getSecondVeterinaryNumber());
            if(keygoat.getFirstBreedingNumber()!=null) key.append(keygoat.getFirstBreedingNumber());
            if(keygoat.getSecondBreedingNumber()!=null) key.append(keygoat.getSecondBreedingNumber());
            if(mSharedPreferences.getString(key.toString(), "").length()>0){
                JSONArray measurements = new JSONArray(mSharedPreferences.getString(key.toString(), ""));

                GoatMeasurement measurement1 = Globals.jsonToObject(measurements.getJSONObject(0), GoatMeasurement.class);
                mTextBonititirovka1.setText(getString(R.string.text_two_values_with_sc_delimiter,
                        measurement1.getMeasurement().getName(), String.valueOf(measurement1.getValue())));
                mSeekBonitirovka1.setProgress(measurement1.getValue());

                GoatMeasurement measurement2 = Globals.jsonToObject(measurements.getJSONObject(1), GoatMeasurement.class);
                mTextBonititirovka2.setText(getString(R.string.text_two_values_with_sc_delimiter,
                        measurement2.getMeasurement().getName(), String.valueOf(measurement2.getValue())));
                mSeekBonitirovka2.setProgress(measurement2.getValue());

                GoatMeasurement measurement3 = Globals.jsonToObject(measurements.getJSONObject(2), GoatMeasurement.class);
                mTextBonititirovka3.setText(getString(R.string.text_two_values_with_sc_delimiter,
                        measurement3.getMeasurement().getName(), String.valueOf(measurement3.getValue())));
                mSeekBonitirovka3.setProgress(measurement3.getValue());

                GoatMeasurement measurement4 = Globals.jsonToObject(measurements.getJSONObject(3), GoatMeasurement.class);
                mTextBonititirovka4.setText(getString(R.string.text_two_values_with_sc_delimiter,
                        measurement4.getMeasurement().getName(), String.valueOf(measurement4.getValue())));
                mSeekBonitirovka4.setProgress(measurement4.getValue());

                GoatMeasurement measurement5 = Globals.jsonToObject(measurements.getJSONObject(4), GoatMeasurement.class);
                mTextBonititirovka5.setText(getString(R.string.text_two_values_with_sc_delimiter,
                        measurement5.getMeasurement().getName(), String.valueOf(measurement5.getValue())));
                mSeekBonitirovka5.setProgress(measurement5.getValue());

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateVisitProtocolModified() throws SQLException, JSONException {
        Set<String> tempProtocols = mSharedPreferences.getStringSet(Globals.TEMPORARY_VISIT_PROTOCOLS, new HashSet<String>());

        if(tempProtocols.contains(Globals.objectToJson(mVisitProtocol).toString())) {
            if(tempProtocols.remove(Globals.objectToJson(mVisitProtocol).toString())){
                mVisitProtocol.setLastUpdatedByUser(dbHelper.getDaoUser().queryForId(mSharedPreferences.getLong(Globals.SETTING_ACTIVE_USER_ID, 1)));
                mVisitProtocol.setDateLastUpdated(new Date(System.currentTimeMillis()));
                Set<String> newPs = new HashSet<String>();
                newPs.addAll(tempProtocols);
                newPs.add(Globals.objectToJson(mVisitProtocol).toString());
                Globals.savePreferences(Globals.TEMPORARY_VISIT_PROTOCOLS, newPs, this);
            }
        }
    }

    private void initMoveButtons(){

        mButtonMoveToFV2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditTextVetCode1Found.getText().length()>0){
                    mEditTextVetCode2Found.setText(mEditTextVetCode1Found.getText().toString());
                    mEditTextVetCode1Found.setText(mEditTextVetCode1.getText().toString());
                }
            }
        });

        mButtonMoveToFB2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mEditTextBreedingCode1Found.getText().length()>0){
                    mEditTextBreedingCode2Found.setText(mEditTextBreedingCode1Found.getText().toString());
                    mEditTextBreedingCode1Found.setText(mEditTextBreedingCode1.getText().toString());
                }
            }
        });
    }

    private void getNextFromBuffer(){
        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(1000);
        MediaPlayer mp = MediaPlayer.create(this, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
        if(mp!=null) mp.start();
        // -------------- RECEIVED MESSAGE
        if(!readContentQueue.isEmpty()){
            String readMessage = readContentQueue.remove();
            if (readMessage.contains("VetNum:")) {
                mEditTextVetCode1.setText(readMessage.substring(
                        readMessage.indexOf("VetNum:") + 8,
                        readMessage.indexOf("VetNum:") + 23
                ));
            }
            /*if (readMessage.contains("BreedNum:")) {
                Integer bn = Integer.parseInt(readMessage.substring(
                        readMessage.indexOf("BreedNum:") + 10,
                        readMessage.indexOf("BreedNum") + 16
                ));
                mEditTextBreedingCode1.setText(String.valueOf(bn));
            }*/
            if (readMessage.contains("Years:")) {
                int ages = Integer.parseInt(readMessage.substring(
                        readMessage.indexOf("Years:") + 7,
                        readMessage.indexOf("Years:") + 8
                ));
                Calendar c = Calendar.getInstance();
                Random r = new Random();
                int y = c.get(Calendar.YEAR) - ages;
                int m = r.nextInt(3);
                int d = r.nextInt(28);

                c.set(y, m, d);
                mButtonBirthdate.setText(Globals.getDateShort(c.getTime()));
                mDateBirthSelected = c.getTime();

                RadioButton rbAdult = (RadioButton) findViewById(R.id.radio_adult);
                RadioButton rbKid = (RadioButton) findViewById(R.id.radio_kid);

                Calendar yearBack = Calendar.getInstance();
                yearBack.set(Calendar.YEAR, yearBack.get(Calendar.YEAR)-1);
                if(mDateBirthSelected.before(yearBack.getTime())){
                    rbAdult.setChecked(true);
                    rbKid.setChecked(false);
                }else{
                    rbAdult.setChecked(false);
                    rbKid.setChecked(true);
                }
                /*switch (mGoatFound.getMaturity()) {
                    case ADULT:
                        rbAdult.setChecked(true);
                        rbKid.setChecked(false);
                        break;
                    case KID:
                        rbAdult.setChecked(false);
                        rbKid.setChecked(true);
                        break;
                };*/
            }
        }
    }

    private void loadPreviousRecord(){
        if(currentIndex>0) {
            currentIndex -= 1;

            getSupportActionBar().setTitle("Коза "+(currentIndex+1)+"/"+GOATS_ARRAY.size());

            Goat goat = GOATS_ARRAY.get(currentIndex);

            if (goat.getFirstVeterinaryNumber() != null) {
                mEditTextVetCode1.setText(goat.getFirstVeterinaryNumber());
            } else {
                mEditTextVetCode1.setText("НЯМА МАРКА");
            }

            if (goat.getFirstBreedingNumber() != null) {
                mEditTextBreedingCode1.setText(goat.getFirstBreedingNumber());
            } else {
                mEditTextBreedingCode1.setText("НЯМА МАРКА");
            }

            if (goat.getSex() != null) {
                switch (goat.getSex()) {
                    case MALE:
                        RadioButton rbMale = (RadioButton) findViewById(R.id.radio_male);
                        rbMale.setChecked(true);
                        break;
                    case FEMALE:
                        RadioButton rbFemale = (RadioButton) findViewById(R.id.radio_female);
                        rbFemale.setChecked(true);
                        break;
                };
            }

            if (goat.getBreed() != null && goat.getBreed().getCodeName() != null) {
                mSpinnerBreed.setSelection(adapterBreeds.getPosition(goat.getBreed().getCodeName()));
            }

            if (goat.getBirthDate() != null) {
                mDateBirthSelected = goat.getBirthDate();
                mButtonBirthdate.setText(Globals.getDateShort(mDateBirthSelected));

                RadioButton rbAdult = (RadioButton) findViewById(R.id.radio_adult);
                RadioButton rbKid = (RadioButton) findViewById(R.id.radio_kid);
                Calendar yearBack = Calendar.getInstance();
                yearBack.set(Calendar.YEAR, yearBack.get(Calendar.YEAR) - 1);
                if (mDateBirthSelected.before(yearBack.getTime())) {
                    rbAdult.setChecked(true);
                    rbKid.setChecked(false);
                } else {
                    rbAdult.setChecked(false);
                    rbKid.setChecked(true);
                }
                mSeekbarYears.setProgress(Globals.getDiffYears(mDateBirthSelected, new Date(System.currentTimeMillis())));
            }

            //resetBonitorvka();
            loadTemporaryBonitirovka(goat);

            if(currentIndex==0){
                mMenuNext.setEnabled(true);
                mMenuPrevious.setEnabled(false);
                mButtonAdd.setEnabled(false);
            }else{
                mMenuNext.setEnabled(true);
                mMenuPrevious.setEnabled(true);
                mButtonAdd.setEnabled(false);
            }
        }


    }

    private void loadNextRecord(){
        currentIndex += 1;
        if(currentIndex >= GOATS_ARRAY.size()){

            mSeekbarYears.setProgress(0);
            clearOnAdd();
            clearSearchResult();
            resetBonitorvka();
            mMenuNext.setEnabled(false);
            mMenuPrevious.setEnabled(true);
            getSupportActionBar().setTitle("Коза "+(GOATS_ARRAY.size()+1)+" по ред");
            mButtonAdd.setEnabled(true);
        }else{
            getSupportActionBar().setTitle("Коза "+(currentIndex+1)+"/"+GOATS_ARRAY.size());
            mMenuNext.setEnabled(true);
            mMenuPrevious.setEnabled(true);
            mButtonAdd.setEnabled(false);

            Goat goat = GOATS_ARRAY.get(currentIndex);

            if(goat.getFirstVeterinaryNumber()!=null){
                mEditTextVetCode1.setText(goat.getFirstVeterinaryNumber());
            }else{
                mEditTextVetCode1.setText("НЯМА МАРКА");
            }

            if(goat.getFirstBreedingNumber()!=null){
                mEditTextBreedingCode1.setText(goat.getFirstBreedingNumber());
            }else{
                mEditTextBreedingCode1.setText("НЯМА МАРКА");
            }

            if(goat.getSex()!=null) {
                switch (goat.getSex()) {
                    case MALE:
                        RadioButton rbMale = (RadioButton) findViewById(R.id.radio_male);
                        rbMale.setChecked(true);
                        break;
                    case FEMALE:
                        RadioButton rbFemale = (RadioButton) findViewById(R.id.radio_female);
                        rbFemale.setChecked(true);
                        break;
                };
            }

            if(goat.getBreed()!=null && goat.getBreed().getCodeName()!=null ) {
                mSpinnerBreed.setSelection(adapterBreeds.getPosition(goat.getBreed().getCodeName()));
            }

            if(goat.getBirthDate()!=null){
                mDateBirthSelected = goat.getBirthDate();
                mButtonBirthdate.setText(Globals.getDateShort(mDateBirthSelected));

                RadioButton rbAdult = (RadioButton) findViewById(R.id.radio_adult);
                RadioButton rbKid = (RadioButton) findViewById(R.id.radio_kid);
                Calendar yearBack = Calendar.getInstance();
                yearBack.set(Calendar.YEAR, yearBack.get(Calendar.YEAR)-1);
                if(mDateBirthSelected.before(yearBack.getTime())){
                    rbAdult.setChecked(true);
                    rbKid.setChecked(false);
                }else{
                    rbAdult.setChecked(false);
                    rbKid.setChecked(true);
                }
                mSeekbarYears.setProgress(Globals.getDiffYears(mDateBirthSelected, new Date(System.currentTimeMillis())));
            }

            //resetBonitorvka();
            loadTemporaryBonitirovka(goat);
        }
    }

    private void saveFarmForGoat(Goat goat, Farm farm){
        try {
            JSONObject farmJson = null;

            if(farm!=null) {
                farm.setLst_visitProtocol(null);
                farmJson = Globals.objectToJson(farm);
            }
            else farmJson = Globals.objectToJson(mFarmRead);

            StringBuffer key = new StringBuffer();
            if(goat.getFirstVeterinaryNumber()!=null) key.append(goat.getFirstVeterinaryNumber());
            if(goat.getSecondVeterinaryNumber()!=null) key.append(goat.getSecondVeterinaryNumber());
            if(goat.getFirstBreedingNumber()!=null) key.append(goat.getFirstBreedingNumber());
            if(goat.getSecondBreedingNumber()!=null) key.append(goat.getSecondBreedingNumber());
            key.append("_farm");
            Globals.savePreferences(key.toString(), farmJson.toString(), this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void saveEditedRecord(Goat goat){
        goat.setFirstVeterinaryNumber(mEditTextVetCode1.getText().toString());
        goat.setFirstBreedingNumber(mEditTextBreedingCode1.getText().toString());
        goat.setHerd(mHerdRead);
        switch (mRadioGroupGender.getCheckedRadioButtonId()) {
            case R.id.radio_male:
                goat.setSex(Sex.MALE);
                break;
            case R.id.radio_female:
                goat.setSex(Sex.FEMALE);
                break;
        }
        try {
            goat.setBreed(dbHelper.getDaoBreed().queryBuilder().where().eq("codeName", adapterBreeds.getItem(mSpinnerBreed.getSelectedItemPosition())).queryForFirst());


            if (mDateBirthSelected != null) {
                goat.setBirthDate(mDateBirthSelected);
            }
            switch (mRadioGroupMaturity.getCheckedRadioButtonId()) {
                case R.id.radio_adult:
                    goat.setMaturity(Maturity.ADULT);
                    break;
                case R.id.radio_kid:
                    goat.setMaturity(Maturity.KID);
                    break;
            }
            goat.setNotes(mEditTextNotes.getText().toString());
            goat.setDateAddedToSystem(new Date(System.currentTimeMillis()));
            goat.setDateLastUpdated(new Date(System.currentTimeMillis()));
            goat.setLastUpdatedByUser(dbHelper.getDaoUser().queryForId(mSharedPreferences.getLong(Globals.SETTING_ACTIVE_USER_ID, 1)));

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void clearOnAdd(){
        mEditTextVetCode1.setText("");
        mEditTextVetCode1.setTextColor(Color.BLACK);
        mEditTextBreedingCode1.setText("");
        mEditTextBreedingCode1.setTextColor(Color.BLACK);
        mRadioGroupGender.clearCheck();//setSelected(false);
        mRadioGroupMaturity.clearCheck();//setSelected(false);
        mButtonBirthdate.setText("ИЗБЕРИ ДАТА");
        mDateBirthSelected = null;
        mEditTextNotes.setText("");
        mScrollMain.scrollTo(0,0);
    }

    private void clearSearchResult() {

        mAutocompleteTextFarm.setTextColor(Color.BLACK);
        mAutocompleteTextFarm2.setTextColor(Color.BLACK);
        mAutocompleteTextFarmFound.setTextColor(Color.BLACK);

        if(mEditTextVetCode1.getText().toString().length()<15) mEditTextVetCode1.setTextColor(Color.BLACK);
        else mEditTextVetCode1.setTextColor(Color.RED);
        mEditTextBreedingCode1.setTextColor(Color.BLACK);

        mSpinnerBreed.setSelection(0);

        mEditTextVetCode1Found.setText("");
        mEditTextVetCode1Found.setTextColor(Color.BLACK);
        mEditTextVetCode1Found.setError(null);
        mEditTextVetCode2Found.setText("");
        mEditTextVetCode2Found.setTextColor(Color.BLACK);
        mEditTextBreedingCode1Found.setText("");
        mEditTextBreedingCode1Found.setTextColor(Color.BLACK);
        mEditTextBreedingCode2Found.setText("");
        mEditTextBreedingCode2Found.setTextColor(Color.BLACK);
        mButtonBirthdateBook.setText("ИЗБЕРИ ДАТА");
        mTextGenderBook.setBackgroundColor(Color.WHITE);
        mTextGenderBook.setText("");
        mTextGenderVetIS.setBackgroundColor(Color.WHITE);
        mTextGenderVetIS.setText("");
        mTextBreedBook.setBackgroundColor(Color.WHITE);
        mTextBreedBook.setText("");
        mTextBreedVetIS.setBackgroundColor(Color.WHITE);
        mTextBreedVetIS.setText("");
        mTextAppliedForSelectionControlYear.setBackgroundColor(Color.WHITE);
        mTextAppliedForSelectionControlYear.setText("");
        mTextSelectionControlVetis.setBackgroundColor(Color.WHITE);
        mTextSelectionControlVetis.setText("");
        mTextInclusionStatus.setText(getString(R.string.text_status_2016_sc, ""));

        mTextNumberInCert.setVisibility(View.GONE);
        mTextCertificate.setVisibility(View.GONE);
        mTextNumberInWastageProtocol.setVisibility(View.GONE);
        mTextWastageProtocol.setVisibility(View.GONE);
        mTextDateDeregistered.setVisibility(View.GONE);
        mTextNumberInCert.setText(getString(R.string.text_number_in_certificate_sc, ""));
        mTextCertificate.setText(getString(R.string.text_number_in_certificate_sc, ""));
        mTextNumberInWastageProtocol.setText(getString(R.string.text_number_in_certificate_sc, ""));
        mTextWastageProtocol.setText(getString(R.string.text_number_in_certificate_sc, ""));
        mTextDateDeregistered.setText(getString(R.string.text_number_in_certificate_sc, ""));
    }

    private void initVetCode1(){
        mGoatLoaderFromVet = new GoatLoader(this, true);
        mEditTextVetCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>9 && currentIndex==GOATS_ARRAY.size()){// || s.length()==6){
                    if(!mGoatLoaderFromVet.isStarted()){
                        mGoatLoaderFromVet.startLoading();
                    }else{
                        mGoatLoaderFromVet.forceLoad();
                    }
                    mRelativeProgress.setVisibility(View.VISIBLE);
                }/*else if(s.length()<15){// || s.length()!=6){
                    mGoatLoaderFromVet.cancelLoadInBackground();
                    mEditTextVetCode1.setTextColor(Color.BLACK);
                    mRelativeProgress.setVisibility(View.INVISIBLE);
                }*/
            }
        });
    }

    private void initBreedCode1(){
        mGoatLoaderFromBreed = new GoatLoader(this, false);
        mEditTextBreedingCode1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                /*if(start>1 && before==0 && count==1 && mEditTextVetCode1.getText().toString().length()<15){
                    if(!mGoatLoaderFromBreed.isStarted()){
                        mGoatLoaderFromBreed.startLoading();
                    }else{
                        mGoatLoaderFromBreed.forceLoad();
                    }
                    mRelativeProgress.setVisibility(View.VISIBLE);
                }else if(before==1 && count==0&& mGoatLoaderFromBreed.isStarted()){
                    mGoatLoaderFromBreed.cancelLoadInBackground();
                    mRelativeProgress.setVisibility(View.INVISIBLE);
                }
                if(s.length()<2){
                    mEditTextBreedingCode1.setTextColor(Color.BLACK);
                }else if(s.length()>1 && mGoatFound==null){
                    mEditTextBreedingCode1.setTextColor(Color.RED);
                }*/
            }
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length()>1){// && mEditTextVetCode1.getText().toString().length()<15){
                    if(!mGoatLoaderFromBreed.isStarted()){
                        mGoatLoaderFromBreed.startLoading();
                    }else{
                        mGoatLoaderFromBreed.forceLoad();
                    }
                    mRelativeProgress.setVisibility(View.VISIBLE);
                }/*else if(before==1 && count==0&& mGoatLoaderFromBreed.isStarted()){
                    mGoatLoaderFromBreed.cancelLoadInBackground();
                    mRelativeProgress.setVisibility(View.INVISIBLE);
                }*/
                /*if(s.length()<2){
                    mEditTextBreedingCode1.setTextColor(Color.BLACK);
                }else if(s.length()>1 && mGoatFound==null){
                    mEditTextBreedingCode1.setTextColor(Color.RED);
                }*/
            }
        });
    }

    private void fillInGoatFound(){

        if(mFarmByGoat!=null){
            mFarmFound = mFarmByGoat;
            mAutocompleteTextFarmFound.setText(mFarmFound.getCompanyName());
            mAutocompleteTextFarmFound.setTextColor(Color.BLACK);

            if(mFarmFound.getId().equals(mFarmRead.getId())){
                mAutocompleteTextFarm.setTextColor(Color.GREEN);
                mAutocompleteTextFarm2.setTextColor(Color.BLACK);
            }else{
                mAutocompleteTextFarm.setTextColor(Color.BLACK);
            }
            if(mFarmFound.getId().equals(mFarmRead2.getId()) && !mFarmRead.getId().equals(mFarmRead2.getId())){
                mAutocompleteTextFarm2.setTextColor(Color.GREEN);
                mAutocompleteTextFarm.setTextColor(Color.BLACK);
            }else{
                mAutocompleteTextFarm2.setTextColor(Color.BLACK);
            }
        }else{
            mAutocompleteTextFarm.setTextColor(Color.BLACK);
            mAutocompleteTextFarm2.setTextColor(Color.BLACK);
            mFarmFound = mFarmRead;
            mAutocompleteTextFarmFound.setText("НЕНАМЕРЕНА в посочените ферми");
            mAutocompleteTextFarmFound.setTextColor(Color.RED);
        }

        initHerdSearch();
        if(mGoatFound.getHerd()!=null) {
            int spinnerPos = adapterHerdsFound.getPosition(String.valueOf(mGoatFound.getHerd().getHerdNumber()));
            if (spinnerPos > -1) mSpinnerHerdFound.setSelection(spinnerPos);

            if(mGoatFound.getHerd().getHerdNumber().toString().equals(mSpinnerHerd.getSelectedItem().toString())){
                mSpinnerHerdFound.setBackgroundColor(Color.GREEN);
            }else{
                mSpinnerHerdFound.setBackgroundColor(Color.RED);
            }
        }

        if(mGoatFound.getFirstVeterinaryNumber()!=null){
            mEditTextVetCode1Found.setText(mGoatFound.getFirstVeterinaryNumber());
        }else{
            mEditTextVetCode1Found.setText("НЯМА МАРКА");
        }
        if(!mGoatFound.getFirstVeterinaryNumber().equals(mEditTextVetCode1.getText().toString())){// &&
                //mEditTextVetCode1.getText().toString().length()!=6){
            mEditTextVetCode1.setTextColor(Color.RED);
        }else
            mEditTextVetCode1.setTextColor(Color.GREEN);
        if(mGoatFound.getSecondVeterinaryNumber()!=null) mEditTextVetCode2Found.setText(mGoatFound.getSecondVeterinaryNumber());



        if(mGoatFound.getFirstBreedingNumber()!=null){
            mEditTextBreedingCode1Found.setText(mGoatFound.getFirstBreedingNumber());
        }else{
            mEditTextBreedingCode1Found.setText("НЯМА МАРКА");
        }
        if(!mGoatFound.getFirstBreedingNumber().equals(mEditTextBreedingCode1.getText().toString())){
            mEditTextBreedingCode1.setTextColor(Color.RED);
        }else
            mEditTextBreedingCode1.setTextColor(Color.GREEN);
        if(mGoatFound.getSecondBreedingNumber()!=null) mEditTextBreedingCode2Found.setText(mGoatFound.getSecondBreedingNumber());

        if(mGoatFound.getSex()!=null) {
            RadioButton rbMale = (RadioButton) findViewById(R.id.radio_male);
            RadioButton rbFemale = (RadioButton) findViewById(R.id.radio_female);
            switch (mGoatFound.getSex()) {
                case MALE:
                    mTextGenderBook.setText("Мъжки");
                    rbMale.setChecked(true);
                    break;
                case FEMALE:
                    mTextGenderBook.setText("Женски");
                    rbFemale.setChecked(true);
                    break;
                default:

                    break;
            };
        }
        if(mGoatFound.getBreed()!=null && mGoatFound.getBreed().getCodeName()!=null ) {
            mSpinnerBreed.setSelection(adapterBreeds.getPosition(mGoatFound.getBreed().getCodeName()));
        }
        /*if(mGoatFound.getBirthDate()!=null){
            //mButtonBirthdateBook.setEnabled(false);
            mButtonBirthdateBook.setText(Globals.getDateShort(mGoatFound.getBirthDate()));

        }*/
        if(mGoatFound.getBirthDate()!=null){
            mDateBirthFound = mGoatFound.getBirthDate();
            mDateBirthSelected = mGoatFound.getBirthDate();
            mButtonBirthdate.setText(Globals.getDateShort(mDateBirthSelected));
            mButtonBirthdateBook.setText(Globals.getDateShort(mDateBirthFound));

            RadioButton rbAdult = (RadioButton) findViewById(R.id.radio_adult);
            RadioButton rbKid = (RadioButton) findViewById(R.id.radio_kid);
            Calendar yearBack = Calendar.getInstance();
            yearBack.set(Calendar.YEAR, yearBack.get(Calendar.YEAR)-1);
            if(mDateBirthSelected.before(yearBack.getTime())){
                rbAdult.setChecked(true);
                rbKid.setChecked(false);
            }else{
                rbAdult.setChecked(false);
                rbKid.setChecked(true);
            }
            mSeekbarYears.setProgress(Globals.getDiffYears(mDateBirthFound, new Date(System.currentTimeMillis())));
        }
        if(mGoatFound.getBreed()!=null) mTextBreedBook.setText(mGoatFound.getBreed().getCodeName());
        if(mGoatFound.getAppliedForSelectionControlYear()!=null){
            mTextAppliedForSelectionControlYear.setText("Да");
            mTextAppliedForSelectionControlYear.setBackgroundColor(Color.GREEN);
        }else{
            mTextAppliedForSelectionControlYear.setBackgroundColor(Color.RED);
            mTextAppliedForSelectionControlYear.setText("Не");
        }
        if(mGoatFound.getInclusionStatusString()!=null) mTextInclusionStatus.setText(getString(R.string.text_status_2016_sc,mGoatFound.getInclusionStatusString()));

        if(mGoatFound.getNumberInCertificate() != null){
            mTextNumberInCert.setVisibility(View.VISIBLE);
            mTextNumberInCert.setText(getString(R.string.text_number_in_certificate_sc, String.valueOf(mGoatFound.getNumberInCertificate())));
        }else{
            mTextNumberInCert.setVisibility(View.GONE);
            mTextNumberInCert.setText(getString(R.string.text_number_in_certificate_sc, ""));
        }

        if(mGoatFound.getCertificateNumber() != null){
            mTextCertificate.setVisibility(View.VISIBLE);
            mTextCertificate.setText(getString(R.string.text_certificate_sc, mGoatFound.getCertificateNumber()));
        }else{
            mTextCertificate.setVisibility(View.GONE);
            mTextCertificate.setText(getString(R.string.text_number_in_certificate_sc, ""));
        }

        if(mGoatFound.getNumberInWastageProtocol() != null){
            mTextNumberInWastageProtocol.setVisibility(View.VISIBLE);
            mTextNumberInWastageProtocol.setText(getString(R.string.text_number_in_wastage_protocol_sc, String.valueOf(mGoatFound.getNumberInWastageProtocol())));
        }else{
            mTextNumberInWastageProtocol.setVisibility(View.GONE);
            mTextNumberInWastageProtocol.setText(getString(R.string.text_number_in_certificate_sc, ""));
        }

        if(mGoatFound.getWastageProtocolNumber() != null){
            mTextWastageProtocol.setVisibility(View.VISIBLE);
            mTextWastageProtocol.setText(getString(R.string.text_wastage_protocol_sc, mGoatFound.getWastageProtocolNumber()));
        }else{
            mTextWastageProtocol.setVisibility(View.GONE);
            mTextWastageProtocol.setText(getString(R.string.text_number_in_certificate_sc, ""));
        }

        if(mGoatFound.getDateDeregistered() != null){
            mTextDateDeregistered.setVisibility(View.VISIBLE);
            mTextDateDeregistered.setText(getString(R.string.text_date_deregistered_sc, Globals.getDateShort(mGoatFound.getDateDeregistered())));
        }else{
            mTextDateDeregistered.setVisibility(View.GONE);
            mTextDateDeregistered.setText(getString(R.string.text_number_in_certificate_sc, ""));
        }

        if(mGoatMeasurementsFound!=null && mGoatMeasurementsFound.size()>4){
            //mLinearBinitirovka1.setVisibility(View.VISIBLE);
            mTextBonititirovka1.setText(getString(R.string.text_two_values_with_sc_delimiter,
                    mGoatMeasurementsFound.get(0).getMeasurement().getName(), String.valueOf(mGoatMeasurementsFound.get(0).getValue())));
            mSeekBonitirovka1.setProgress(mGoatMeasurementsFound.get(0).getValue());

            //mLinearBinitirovka2.setVisibility(View.VISIBLE);
            mTextBonititirovka2.setText(getString(R.string.text_two_values_with_sc_delimiter,
                    mGoatMeasurementsFound.get(1).getMeasurement().getName(), String.valueOf(mGoatMeasurementsFound.get(1).getValue())));
            mSeekBonitirovka2.setProgress(mGoatMeasurementsFound.get(1).getValue());

            //mLinearBinitirovka3.setVisibility(View.VISIBLE);
            mTextBonititirovka3.setText(getString(R.string.text_two_values_with_sc_delimiter,
                    mGoatMeasurementsFound.get(2).getMeasurement().getName(), String.valueOf(mGoatMeasurementsFound.get(2).getValue())));
            mSeekBonitirovka3.setProgress(mGoatMeasurementsFound.get(2).getValue());

            //mLinearBinitirovka4.setVisibility(View.VISIBLE);
            mTextBonititirovka4.setText(getString(R.string.text_two_values_with_sc_delimiter,
                    mGoatMeasurementsFound.get(3).getMeasurement().getName(), String.valueOf(mGoatMeasurementsFound.get(3).getValue())));
            mSeekBonitirovka4.setProgress(mGoatMeasurementsFound.get(3).getValue());

            //mLinearBinitirovka5.setVisibility(View.VISIBLE);
            mTextBonititirovka5.setText(getString(R.string.text_two_values_with_sc_delimiter,
                    mGoatMeasurementsFound.get(4).getMeasurement().getName(), String.valueOf(mGoatMeasurementsFound.get(4).getValue())));
            mSeekBonitirovka5.setProgress(mGoatMeasurementsFound.get(4).getValue());
        }else{
            resetBonitorvka();
        }

    }
    private void fillInGoatVetIS(){
        if(mGoatVetIs!=null) {
            if(mGoatVetIs.getFirstVeterinaryNumber() != null && mGoatVetIs.getFirstVeterinaryNumber().equalsIgnoreCase(mGoatFound.getFirstVeterinaryNumber())){
                mEditTextVetCode1Found.setTextColor(Color.GREEN);
            }else if(mGoatVetIs.getFirstVeterinaryNumber() != null && !mGoatVetIs.getFirstVeterinaryNumber().equalsIgnoreCase(mGoatFound.getFirstVeterinaryNumber())){
                mEditTextVetCode1Found.setTextColor(Color.RED);
                mEditTextVetCode1Found.setError("Не намерен във ВетИС");
            }

            if(mGoatVetIs.getSecondVeterinaryNumber()!=null  &&
                    mGoatVetIs.getSecondVeterinaryNumber().equalsIgnoreCase(mGoatFound.getSecondVeterinaryNumber()))
                    mEditTextVetCode2Found.setTextColor(Color.GREEN);

            if(mGoatVetIs.getSex()!=null && mGoatVetIs.getSex()==mGoatFound.getSex()){
                mTextGenderBook.setBackgroundColor(Color.GREEN);
                mTextGenderVetIS.setBackgroundColor(Color.GREEN);
                switch (mGoatVetIs.getSex()){
                    case MALE:
                        mTextGenderVetIS.setText("Мъжки");
                        break;
                    case FEMALE:
                        mTextGenderVetIS.setText("Женски");
                        break;
                };
            }else if(mGoatVetIs.getSex()!=null && mGoatVetIs.getSex()!=mGoatFound.getSex()){
                mTextGenderBook.setBackgroundColor(Color.RED);
                mTextGenderVetIS.setBackgroundColor(Color.RED);
                switch (mGoatVetIs.getSex()){
                    case MALE:
                        mTextGenderVetIS.setText("Мъжки");
                        break;
                    case FEMALE:
                        mTextGenderVetIS.setText("Женски");
                        break;
                };
            }

            if(mGoatVetIs.getBreed()!=null && mGoatVetIs.getBreed().equalsIgnoreCase(mGoatFound.getBreed().getBreedName()) ||
                    mGoatVetIs.getBreed().equalsIgnoreCase(mGoatFound.getBreed().getCodeName())){
                mTextBreedBook.setBackgroundColor(Color.GREEN);
                mTextBreedVetIS.setBackgroundColor(Color.GREEN);
                mTextBreedVetIS.setText(mGoatVetIs.getBreed());
            }else if(mGoatVetIs.getBreed()!=null && (!mGoatVetIs.getBreed().equalsIgnoreCase(mGoatFound.getBreed().getBreedName()) ||
                    !mGoatVetIs.getBreed().equalsIgnoreCase(mGoatFound.getBreed().getCodeName())) ){
                mTextBreedBook.setBackgroundColor(Color.RED);
                mTextBreedVetIS.setBackgroundColor(Color.RED);
                mTextBreedVetIS.setText(mGoatVetIs.getBreed());
            }

            if(mGoatVetIs.getSelectionControl()){
                mTextSelectionControlVetis.setBackgroundColor(Color.GREEN);
                mTextSelectionControlVetis.setText("Да");
            }else{
                mTextSelectionControlVetis.setBackgroundColor(Color.RED);
                mTextSelectionControlVetis.setText("Не");
            }

        }else{
            mEditTextVetCode1Found.setTextColor(Color.RED);
        }
    }

    private void showBirthdatePickerDialog(){
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public DatabaseHelper getDatabaseHelper(){
        return dbHelper;
    }

    private final BroadcastReceiver bReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                //discovery starts, we can show progress dialog or perform other tasks
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)) {
                //discovery finishes, dismis progress dialog
            } else if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if(device!=null && device.getName().toLowerCase().contains("ges3s")){
                    mBluetoothAdapter.cancelDiscovery();
                    Intent newIntent = intent.putExtra(EXTRA_DEVICE_ADDRESS, device.getAddress());
                    connectDevice(newIntent, false);
                }
            }else if (BluetoothDevice.ACTION_UUID.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                Parcelable[] uuidExtra = intent.getParcelableArrayExtra(BluetoothDevice.EXTRA_UUID);
                for (int i=0; i<uuidExtra.length; i++) {
                    //out.append("\n  Device: " + device.getName() + ", " + device + ", Service: " + uuidExtra[i].toString());
                    Log.d("UUID", uuidExtra[i].toString());
                    /*BTArrayAdapter.add(uuidExtra[i].toString());
                    BTArrayAdapter.notifyDataSetChanged();*/
                }
            }
        }
    };

    void turnBluetoothOn(){
        if (!mBluetoothAdapter.isEnabled()) {
            Intent turnOnIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOnIntent, REQUEST_ENABLE_BT);
            /*Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            startActivity(discoverableIntent);*/
            Toast.makeText(getApplicationContext(),"Bluetooth turned on" , Toast.LENGTH_LONG).show();
        }
        else{
            /*Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 0);
            startActivity(discoverableIntent);*/
            Toast.makeText(getApplicationContext(),"Bluetooth is already on", Toast.LENGTH_LONG).show();
        }

        /*Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            // Loop through paired devices
            for (BluetoothDevice device : pairedDevices) {
                // Add the name and address to an array adapter to show in a ListView
                BTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                BTArrayAdapter.notifyDataSetChanged();
            }
        }*/
        findBluetoothDevices();
    }

    void findBluetoothDevices(){
        if (mBluetoothAdapter.isDiscovering()) {
            // the button is pressed when it discovers, so cancel the discovery
            mBluetoothAdapter.cancelDiscovery();
        }
        else {
            BTArrayAdapter.clear();
            mBluetoothAdapter.startDiscovery();
        }
    }


    private void setupConnection() {
        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothService(this, mHandler);

    }

    private void connectDevice(Intent data, boolean secure) {
        // Get the device MAC address
        String address = data.getExtras().getString(EXTRA_DEVICE_ADDRESS);
        // Get the BluetoothDevice object
        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        mChatService.connect(device, secure);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE_SECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, true);
                }
                break;
            case REQUEST_CONNECT_DEVICE_INSECURE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    connectDevice(data, false);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupConnection();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    Toast.makeText(getApplicationContext(),"Bluetooth turned off" , Toast.LENGTH_LONG).show();
                    finish();
                }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else if (mChatService == null) {
            setupConnection();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(bReceiver!=null) {
            this.unregisterReceiver(bReceiver);
        }
        if (mChatService != null) {
            mChatService.stop();
        }
        if (dbHelper != null) {
            OpenHelperManager.releaseHelper();
            dbHelper = null;
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // Performing this check in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Globals.MESSAGE_STATE_CHANGE:
                    switch (msg.arg1) {
                        case BluetoothService.STATE_CONNECTED:
                            //mEditIdMark.setEnabled(true);
                            break;
                        case BluetoothService.STATE_CONNECTING:

                            break;
                        case BluetoothService.STATE_LISTEN:
                        case BluetoothService.STATE_NONE:
                            //mEditIdMark.setEnabled(false);
                            break;
                    }
                    break;
                case Globals.MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    break;
                case Globals.MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String rm = new String(readBuf, 0, msg.arg1);
                    readContentQueue.add(rm);

                    currentIndex = GOATS_ARRAY.size()-1;
                    loadNextRecord();

                    getNextFromBuffer();

                    // Vibrate for 500 milliseconds

                    break;
                case Globals.MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(Globals.DEVICE_NAME);
                    if (null != this) {
                        Toast.makeText(getApplicationContext(), "Connected to "
                                + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    }
                    break;
                case Globals.MESSAGE_TOAST:
                    if (null != this) {
                        Toast.makeText(getApplicationContext(), msg.getData().getString(Globals.TOAST),
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        }
    };

    @Override
    public void onDateSet(Calendar calendar) {
        mDateBirthSelected = calendar.getTime();
        mButtonBirthdate.setText( getString(R.string.placeholder_date,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("bg","BG")),
                calendar.get(Calendar.YEAR)));
    }

    private void sortSingleListByLastUpdated(List<Goat> list){
        Collections.sort(list, new Comparator<Goat>() {

            @Override
            public int compare(Goat goat, Goat goat2) {

                long lut1 = 0;
                long lut2 = 0;
                if(goat.getDateLastUpdated()!=null) lut1 = goat.getDateLastUpdated().getTime();
                if(goat2.getDateLastUpdated()!=null) lut2 = goat2.getDateLastUpdated().getTime();

                int result = 0;
                result = (int) (lut1 - lut2);

                return result;
            }
        });
    }

    private class InitActivity extends AsyncTask<Void, Integer, Boolean>{
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

            GOATS_KEY = Globals.TEMPORARY_GOATS_FOR_PROTOCOL + String.valueOf(mVisitProtocol.getFarm().getId()) + "_" + String.valueOf(mVisitProtocol.getDateAddedToSystem().getTime());
            GOATS_SET = mSharedPreferences.getStringSet(GOATS_KEY, new HashSet<String>());

            GOATS_ARRAY = new ArrayList<Goat>();
            Iterator iter = GOATS_SET.iterator();
            try {
                while (iter.hasNext()) {
                    GOATS_ARRAY.add(Globals.jsonToObject(new JSONObject(iter.next().toString()), Goat.class));
                }
            }catch (JSONException e){
                e.printStackTrace();
            }
            sortSingleListByLastUpdated(GOATS_ARRAY);
            currentIndex = GOATS_ARRAY.size();

            /*if(mMenuPrevious!=null && mMenuNext!=null){
                if(GOATS_SET.size()>0){
                    mMenuPrevious.setEnabled(true);
                }else{
                    mMenuPrevious.setEnabled(false);
                }
                mMenuNext.setEnabled(false);
            }*/


            return success[0];
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            mProgressDialog.incrementProgressBy(1);
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if(mProgressDialog.isShowing()){
                mProgressDialog.cancel();
            }
            getSupportActionBar().setTitle("Коза "+(GOATS_ARRAY.size()+1)+" по ред");
        }
    }


    private class FarmLoader extends AsyncTaskLoader<List<Farm>>{
        //private List<Farm> mData;

        public FarmLoader(Context ctx){
            super(ctx);
        }

        @Override
        public List<Farm> loadInBackground() {
            return dbHelper.getFarms();
        }
        @Override
        public void deliverResult(List<Farm> farms) {
            if (isReset()) {
                return;
            }
            //mFarmsRead = farms;
            mAutocompleteTextFarm.setAdapter(new FarmAutocompleteAdapter(GoatAddReaderActivity.this, farms));
            mAutocompleteTextFarm2.setAdapter(new FarmAutocompleteAdapter(GoatAddReaderActivity.this, farms));
            mAutocompleteTextFarmFound.setAdapter(new FarmAutocompleteAdapter(GoatAddReaderActivity.this, farms));
            //adapterFarm.notifyDataSetChanged();
            if (isStarted()) {
                super.deliverResult(farms);
            }
        }
        @Override
        protected void onStartLoading() {
            if (mFarmsRead != null) {
                deliverResult(mFarmsRead);
            }
            if (takeContentChanged() || mFarmsRead == null){
                forceLoad();
            }
        }
        @Override
        protected void onStopLoading() {
            cancelLoad();
        }
        @Override
        protected void onReset() {
            super.onReset();
            onStopLoading();
            if (mFarmsRead != null) {
                mFarmsRead = null;
            }
        }
    }

    private class GoatLoader extends AsyncTaskLoader<Goat> {
        private boolean vet;

        public GoatLoader(Context ctx, boolean vOrB) {
            super(ctx);
            vet = vOrB;
        }

        @Override
        public Goat loadInBackground() {
            try {
                mGoatFound = null;
                QueryBuilder<Goat, Long> gqb;
                QueryBuilder<FarmGoat, Long> fgqb = dbHelper.getDaoFarmGoat().queryBuilder();
                fgqb.where()
                        .eq("farm_id", mFarmRead.getId())
                        .or()
                        .eq("farm_id", mFarmRead2.getId());
                gqb = dbHelper.getDaoGoat().queryBuilder();
                gqb.selectColumns("firstVeterinaryNumber", "secondVeterinaryNumber",
                        "firstBreedingNumber", "secondBreedingNumber",
                        "sex", "birthDate", "breed_id",
                        "appliedForSelectionControlYear", "inclusionStatusString",
                        "numberInCertificate", "certificateNumber", "numberInWastageProtocol", "wastageProtocolNumber", "dateDeregistered",
                        "dateLastUpdated", "lastUpdatedByUser_id", "lastModifiedBy");
                if(vet) {
                    gqb.where()
                            .eq("firstVeterinaryNumber", mEditTextVetCode1.getText().toString())
                            .or()
                            .eq("secondVeterinaryNumber", mEditTextVetCode1.getText().toString())
                    .or().like("firstVeterinaryNumber", "%"+mEditTextVetCode1.getText().toString())
                    .or().like("secondVeterinaryNumber", "%"+mEditTextVetCode1.getText().toString());
                    gqb.join(fgqb);
                    if(gqb.query().size()>0) mGoatFound = gqb.query().get(0);
                    if(mGoatFound==null){
                        QueryBuilder<Goat, Long> gqb2 = dbHelper.getDaoGoat().queryBuilder();
                        gqb2.selectColumns("firstVeterinaryNumber", "secondVeterinaryNumber",
                                "firstBreedingNumber", "secondBreedingNumber",
                                "sex", "birthDate", "breed_id", "herd_id",
                                "appliedForSelectionControlYear", "inclusionStatusString",
                                "numberInCertificate", "certificateNumber", "numberInWastageProtocol", "wastageProtocolNumber", "dateDeregistered",
                                "dateLastUpdated", "lastUpdatedByUser_id", "lastModifiedBy");
                        gqb2.where()
                                .eq("firstBreedingNumber", mEditTextBreedingCode1.getText().toString());
                        gqb2.join(fgqb);
                        if(gqb2.query().size()==1) mGoatFound = gqb2.query().get(0);
                    }

                }else{
                    //QueryBuilder<FarmGoat, Long> fgqb = dbHelper.getDaoFarmGoat().queryBuilder();
                    //fgqb.where().eq("farm_id", mFarmFound.getId());
                    gqb = dbHelper.getDaoGoat().queryBuilder();
                    gqb.where()
                            .eq("firstBreedingNumber", mEditTextBreedingCode1.getText().toString())
                            .or()
                            .eq("secondBreedingNumber", mEditTextBreedingCode1.getText().toString());
                            //.queryForFirst();
                    gqb.join(fgqb);
                    if(gqb.query().size()==1) mGoatFound = gqb.query().get(0);
                    //mGoatFound = gqb.queryForFirst();
                }
                if (mGoatFound != null) {
                    //find in which farm it is
                    QueryBuilder<FarmGoat, Long> farmbygoat = dbHelper.getDaoFarmGoat().queryBuilder();
                    farmbygoat.selectColumns("farm_id")
                            .where().eq("goat_id", mGoatFound.getId());
                    if(farmbygoat.query().size()==1) mFarmByGoat = dbHelper.getDaoFarm().queryForId(farmbygoat.query().get(0).getFarm().getId());


                    //find in vetIS
                    QueryBuilder<GoatFromVetIs, Long> gvQB = dbHelper.getDaoGoatFromVetIs().queryBuilder();
                    gvQB
                            .selectColumns()
                            .where()
                            .eq("farmID", mFarmRead.getId())
                            .or()
                            .eq("farmID", mFarmRead2.getId())
                            .and()
                            .eq("firstVeterinaryNumber", mGoatFound.getFirstVeterinaryNumber());
                            /*.or()
                            .eq("firstVeterinaryNumber", mGoatFound.getSecondVeterinaryNumber())
                            .or()
                            .eq("secondVeterinaryNumber", mGoatFound.getFirstVeterinaryNumber())
                            .or()
                            .eq("secondVeterinaryNumber", mGoatFound.getSecondVeterinaryNumber())*/
                            //.or().like("firstVeterinaryNumber", "%"+mGoatFound.getFirstVeterinaryNumber());
                            /*.or().like("firstVeterinaryNumber", "%"+mGoatFound.getSecondVeterinaryNumber())
                            .or().like("secondVeterinaryNumber", "%"+mGoatFound.getFirstVeterinaryNumber())
                            .or().like("secondVeterinaryNumber", "%"+mGoatFound.getSecondVeterinaryNumber())*/
                            //.queryForFirst();
                    if(gvQB.query().size()==1) mGoatVetIs = gvQB.query().get(0);

                    QueryBuilder<Measurement, Integer> mqb = dbHelper.getDaoMeasurement().queryBuilder();
                    mqb.selectColumns("_id", "type", "name").orderBy("name", true)
                            .where().eq("type", MeasurementType.BONITIROVKA);
                    QueryBuilder<GoatMeasurement, Long> gmqb = dbHelper.getDaoGoatMeasurement().queryBuilder();
                    gmqb.join(mqb);
                    gmqb.selectColumns("value", "measurement_id")
                            .where()
                            .eq("goat_id", mGoatFound.getId());
                    mGoatMeasurementsFound = gmqb.query();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return mGoatFound;
        }

        @Override
        public void deliverResult(Goat g) {
            if (isReset()) {
                return;
            }
            if (isStarted()) {
                super.deliverResult(g);
            }
            if(mGoatFound!=null) {
                fillInGoatFound();
                if(mGoatVetIs!=null){
                    fillInGoatVetIS();
                }
            }else{
                clearSearchResult();
            }

            mRelativeProgress.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onStartLoading() {
            mGoatMeasurementsFound = null;
            mFarmByGoat = null;
            mGoatVetIs = null;
            if (takeContentChanged() || mGoatFound == null) {
                forceLoad();
            }
        }

        @Override
        public void onCanceled(Goat g) {
            super.onCanceled(g);
            mGoatMeasurementsFound = null;
            mFarmByGoat = null;
            mGoatVetIs = null;
            mRelativeProgress.setVisibility(View.INVISIBLE);
        }

        @Override
        protected void onStopLoading() {
            mRelativeProgress.setVisibility(View.INVISIBLE);
            cancelLoad();
        }

        @Override
        protected void onReset() {
            super.onReset();
            onStopLoading();
            if (mGoatFound != null) {
                mGoatFound = null;
                mGoatMeasurementsFound = null;
                mFarmByGoat = null;
                mGoatVetIs = null;
            }
        }
    }

}
