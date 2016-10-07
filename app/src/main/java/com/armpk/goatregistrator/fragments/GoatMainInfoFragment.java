package com.armpk.goatregistrator.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.activities.GoatAddReaderActivity;
import com.armpk.goatregistrator.adapters.FarmAutocompleteAdapter;
import com.armpk.goatregistrator.adapters.HerdAutocompleteAdapter;
import com.armpk.goatregistrator.database.Breed;
import com.armpk.goatregistrator.database.Farm;
import com.armpk.goatregistrator.database.Goat;
import com.armpk.goatregistrator.database.GoatStatus;
import com.armpk.goatregistrator.database.Herd;
import com.armpk.goatregistrator.database.VisitProtocol;
import com.armpk.goatregistrator.utilities.Globals;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link GoatMainInfoFragment.OnMainInfoFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link GoatMainInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class GoatMainInfoFragment extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_FARM = "farm";
    private static final String ARG_GOAT = "goat";
    private static final String ARG_VISIT_PROTOCOL = "visit_protocol";
    private static final String ARG_GOAT_MAIN_INFO_CONTEXT = "goat_main_info_context";

    // TODO: Rename and change types of parameters
    private VisitProtocol mVisitProtocol;
    private Farm mFarm;

    public enum GoatMainInfoContext{
        ADD,
        VIEW
    }

    private GoatMainInfoContext mGoatMainInfoContext;

    private Goat mGoatFound;
    private Goat mGoatRead;

    private AutoCompleteTextView mAutocompleteTextFarm;
    private Farm mFarmSelected;

    private AutoCompleteTextView mAutocompleteTextHerd;
    private Herd mHerdSelected;

    private EditText mEditTextVetCode1;
    //private EditText mEditTextVetCode2;
    //private EditText mEditTextVetCode3;
    private EditText mEditTextBreedingCode1;
    //private EditText mEditTextBreedingCode2;
    //private EditText mEditTextBreedingCode3;
    private EditText mEditTextHerdbookNumber;
    private EditText mEditTextHerdbookVolume;

    private RadioGroup radioGroupGender;
    private RadioGroup radioGroupMaturity;

    Button buttonBirthdate;
    private Date mDateBirthSelected;

    private List<Breed> listBreeds;
    private List<GoatStatus> listGoatStatuses;

    AutoCompleteTextView textViewBirthplace;
    ArrayAdapter<String> adapterBirthplace;

    EditText editTextNotes;

    private StringBuffer mErrorMessage;

    private OnMainInfoFragmentInteractionListener mListener;

    public GoatMainInfoFragment() {

    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param visitProtocol Parameter 1.
     * @return A new instance of fragment GoatMainInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static GoatMainInfoFragment newInstance(GoatMainInfoContext fragmentContext, VisitProtocol visitProtocol, Farm farm) {
        GoatMainInfoFragment fragment = new GoatMainInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_GOAT_MAIN_INFO_CONTEXT, fragmentContext);
        args.putSerializable(ARG_VISIT_PROTOCOL, visitProtocol);
        args.putSerializable(ARG_FARM, farm);
        fragment.setArguments(args);
        return fragment;
    }

    public static GoatMainInfoFragment newInstance(GoatMainInfoContext fragmentContext, VisitProtocol visitProtocol, Farm farm, Goat goat) {
        GoatMainInfoFragment fragment = new GoatMainInfoFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_GOAT_MAIN_INFO_CONTEXT, fragmentContext);
        args.putSerializable(ARG_VISIT_PROTOCOL, visitProtocol);
        args.putSerializable(ARG_FARM, farm);
        args.putSerializable(ARG_GOAT, goat);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mGoatMainInfoContext = (GoatMainInfoContext) getArguments().getSerializable(ARG_GOAT_MAIN_INFO_CONTEXT);
            mVisitProtocol = (VisitProtocol) getArguments().getSerializable(ARG_VISIT_PROTOCOL);
            mFarmSelected = (Farm) getArguments().getSerializable(ARG_FARM);
            if(mGoatMainInfoContext == GoatMainInfoContext.VIEW && (Goat)getArguments().getSerializable(ARG_GOAT)!=null ){
                mGoatFound = (Goat)getArguments().getSerializable(ARG_GOAT);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_goat_main_info, container, false);

        mAutocompleteTextFarm = (AutoCompleteTextView)rootView.findViewById(R.id.autocompleteFarm);
        if(mFarmSelected!=null){
            mAutocompleteTextFarm.setText(mFarmSelected.getCompanyName());
        }
        try {
            mAutocompleteTextFarm.setAdapter(new FarmAutocompleteAdapter(getActivity(), ((GoatAddReaderActivity)getActivity()).getDatabaseHelper().getDaoFarm().queryForAll()) );
        } catch (SQLException e) {
            e.printStackTrace();
        }
        mAutocompleteTextFarm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mAutocompleteTextFarm.setText(
                        ((TextView)view.findViewById(R.id.text_name)).getText().toString()
                );
                mFarmSelected = (Farm) adapterView.getItemAtPosition(i);
                initHerdAutocomplete();
            }
        });

        mAutocompleteTextHerd = (AutoCompleteTextView)rootView.findViewById(R.id.autocompleteHerd);
        initHerdAutocomplete();

        mAutocompleteTextHerd.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mAutocompleteTextHerd.setText(
                        ((TextView)view.findViewById(R.id.text_name)).getText().toString()
                );
                mHerdSelected = (Herd) adapterView.getItemAtPosition(i);
            }
        });

        mEditTextVetCode1 = (EditText)rootView.findViewById(R.id.editTextVetCode1);
        if(mGoatMainInfoContext == GoatMainInfoContext.ADD) {
            mEditTextVetCode1.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    try {
                        Goat g = ((GoatAddReaderActivity) getActivity()).getDatabaseHelper().getDaoGoat().queryBuilder().where().eq("firstVeterinaryNumber", s).queryForFirst();
                        if (g != null) {
                            mListener.onMainInfoFragmentFoundGoat(GoatMainInfoContext.ADD, g);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {
                    /*try {
                        Goat g = ((GoatAddReaderActivity)getActivity()).getDatabaseHelper().getDaoGoat().queryBuilder().where().eq("firstVeterinaryNumber", s).query().get(0);
                        if(g!=null){
                            mListener.onMainInfoFragmentFoundGoat(GoatMainInfoContext.ADD, g);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }*/
                }
            });
        }


















        //mEditTextVetCode2 = (EditText)rootView.findViewById(R.id.editTextVetCode2);
        //mEditTextVetCode3 = (EditText)rootView.findViewById(R.id.editTextVetCode3);
        mEditTextBreedingCode1 = (EditText)rootView.findViewById(R.id.editTextBreedingCode1);
        //mEditTextBreedingCode2 = (EditText)rootView.findViewById(R.id.editTextBreedingCode2);
        //mEditTextBreedingCode3 = (EditText)rootView.findViewById(R.id.editTextBreedingCode3);
        mEditTextHerdbookNumber = (EditText)rootView.findViewById(R.id.editTextHerdbookEntryNumber);
        mEditTextHerdbookVolume = (EditText)rootView.findViewById(R.id.editTextHerdbookVolume);

        /*radioGroupGender = (RadioGroup)rootView.findViewById(R.id.radioGroupGender);
        radioGroupGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
        radioGroupMaturity = (RadioGroup)rootView.findViewById(R.id.radioGroupMaturity);
        radioGroupMaturity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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

        try {
            listGoatStatuses = ((GoatAddManuallyActivity)getActivity()).getDatabaseHelper().getDaoGoatStatus().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Spinner spinnerGoatStatus = (Spinner)rootView.findViewById(R.id.spinner_status);
        ArrayAdapter<String> adapterGoatStatus = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_item
        );
        adapterGoatStatus.add("Избери");
        for(GoatStatus gs : listGoatStatuses){
            adapterGoatStatus.add(gs.getStatusName());
        }
        adapterGoatStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoatStatus.setAdapter(adapterGoatStatus);



        try {
            listBreeds = ((GoatAddManuallyActivity)getActivity()).getDatabaseHelper().getDaoBreed().queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Spinner spinnerGoatBreed = (Spinner)rootView.findViewById(R.id.spinner_breed);
        ArrayAdapter<String> adapterGoatBreed = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_spinner_item
        );
        adapterGoatBreed.add("Избери");
        for(Breed b : listBreeds){
            adapterGoatBreed.add(b.getBreedName());
        }
        adapterGoatBreed.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGoatBreed.setAdapter(adapterGoatBreed);

        AutoCompleteTextView textViewColor = (AutoCompleteTextView)rootView.findViewById(R.id.autocompleteColor);*/

        buttonBirthdate = (Button)rootView.findViewById(R.id.buttonBirthdatePick);
        buttonBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBirthdatePickerDialog();
            }
        });

       /* AutoCompleteTextView textViewBirthplace = (AutoCompleteTextView)rootView.findViewById(R.id.autocompleteBirthplace);

        editTextNotes = (EditText)rootView.findViewById(R.id.editTextNotes);*/

        Button buttonContinue = (Button)rootView.findViewById(R.id.buttonContinue);
        if(mGoatMainInfoContext==GoatMainInfoContext.ADD){
            buttonContinue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mGoatRead = new Goat();
                    if (mListener != null && validateFields()) {
                        mListener.onMainInfoFragmentButtonContinue(mGoatRead);
                    }
                }
            });
        }else if(mGoatMainInfoContext==GoatMainInfoContext.VIEW){
            buttonContinue.setVisibility(View.GONE);
        }

        if(mGoatFound!=null){
            loadGoat(mGoatFound);
        }
        mErrorMessage = new StringBuffer();
        return rootView;
    }

    private void loadGoat(Goat goat){
        mEditTextVetCode1.setText(goat.getFirstVeterinaryNumber());
        mEditTextBreedingCode1.setText(goat.getFirstBreedingNumber());
    }

    private boolean validateFields(){
        boolean valid = true;

        mEditTextVetCode1.setError(null);
        mEditTextBreedingCode1.setError(null);
        View focusView = null;

        if(mEditTextVetCode1.getText().length()<15){
            mErrorMessage.append("Невалиден ВЕТЕРИНАРЕН номер\n");
            valid = false;
            focusView = mEditTextVetCode1;
        }else {
            mGoatRead.setFirstVeterinaryNumber(mEditTextVetCode1.getText().toString());
        }

        if(mEditTextBreedingCode1.getText().length()<6){
            mErrorMessage.append("Невалиден РАЗВЪДЕН номер\n");
            valid = false;
            focusView = mEditTextBreedingCode1;
        }else{
            mGoatRead.setFirstBreedingNumber(mEditTextBreedingCode1.getText().toString());
        }

        if(!valid) {
            focusView.requestFocus();
        }

        return valid;
    }

    public String getVetCode1(){
        return mEditTextVetCode1.getText().toString();
    }

    /*public String getVetCode2(){
        return mEditTextVetCode2.getText().toString();
    }

    public String getVetCode3(){
        return mEditTextVetCode3.getText().toString();
    }*/

    public String getBreedingCode1(){
        return mEditTextBreedingCode1.getText().toString();
    }

    /*public String getBreedingCode2(){
        return mEditTextBreedingCode2.getText().toString();
    }

    public String getBreedingCode3(){
        return mEditTextBreedingCode3.getText().toString();
    }*/

    public String getHerbookNumber(){
        return mEditTextHerdbookNumber.getText().toString();
    }

    public String getHerdBookVolume(){
        return mEditTextHerdbookVolume.getText().toString();
    }

    public int getButtonGenderId(){
        return radioGroupGender.getCheckedRadioButtonId();
    }

    public int getButtonMaturityId(){
        return radioGroupMaturity.getCheckedRadioButtonId();
    }

    public void setVetNum1(String text){
        mEditTextVetCode1.setText(text);
    }

    public void setBreedNum1(String text){
        mEditTextBreedingCode1.setText(text);
    }

    public void setButtonBirthdateText(int ages){

        Calendar d = Calendar.getInstance();
        d.set(d.get(Calendar.YEAR)-ages, 0, 1);
        buttonBirthdate.setText(Globals.getDateShort(d.getTime()));
    }

    public void setButtonBirthdateText(Calendar calendar){
        buttonBirthdate.setText(getString(R.string.placeholder_date,
                calendar.get(Calendar.DAY_OF_MONTH),
                calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, new Locale("bg","BG")),
                calendar.get(Calendar.YEAR)));
    }

    public void showBirthdatePickerDialog(){
        DatePickerFragmentFromFragment newFragment = DatePickerFragmentFromFragment.newInstance(mGoatMainInfoContext);
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }

    public void onRadioButtonGenderClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_male:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radio_female:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }

    public void onRadioButtonMaturityClicked(View view){
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();
        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radio_adult:
                if (checked)
                    // Pirates are the best
                    break;
            case R.id.radio_kid:
                if (checked)
                    // Ninjas rule
                    break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMainInfoFragmentInteractionListener) {
            mListener = (OnMainInfoFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void initHerdAutocomplete(){
        try {
            mAutocompleteTextHerd.setAdapter(new HerdAutocompleteAdapter(getActivity(), ((GoatAddReaderActivity)getActivity()).getDatabaseHelper().getHerdsForFarm(mFarmSelected)) );
            if(mAutocompleteTextHerd.getAdapter().getCount()==1){
                mAutocompleteTextHerd.setEnabled(false);
                mHerdSelected = ((HerdAutocompleteAdapter)mAutocompleteTextHerd.getAdapter()).getFirst();
                mAutocompleteTextHerd.setText(String.valueOf(mHerdSelected.getHerdNumber()));
            }/*else{
                mAutocompleteTextHerd.setEnabled(true);
                mHerdSelected = ((HerdAutocompleteAdapter)mAutocompleteTextHerd.getAdapter()).getLast();
                mAutocompleteTextHerd.setText(String.valueOf(mHerdSelected.getHerdNumber()));
            }*/
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnMainInfoFragmentInteractionListener {
        // TODO: Update argument type and name
        void onMainInfoFragmentButtonContinue(Goat goat);
        void onMainInfoFragmentFoundGoat(GoatMainInfoFragment.GoatMainInfoContext context, Goat goat);
    }


}
