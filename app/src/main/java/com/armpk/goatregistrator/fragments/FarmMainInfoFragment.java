package com.armpk.goatregistrator.fragments;

import android.app.DialogFragment;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.activities.FarmAddActivity;
import com.armpk.goatregistrator.database.Farm;
import com.armpk.goatregistrator.database.Phone;
import com.armpk.goatregistrator.database.User;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FarmMainInfoFragment.OnFarmMainInfoFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FarmMainInfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FarmMainInfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LinearLayout mLinearMain;
    private EditText mEditTextCompanyName;
    private AutoCompleteTextView mAutocompleteMol;
    private EditText mEditTextEik;
    private EditText mEditTextBulstat;
    private AutoCompleteTextView mAutocompleteFarmer;
    private EditText mEditTextBreedingPlaceNumber;
    private EditText mEditTextTotalAnimals;
    private AutoCompleteTextView mAutocompleteHerdbookVolumeNumber;
    private Button mButtonAddPhone;

    private OnFarmMainInfoFragmentInteractionListener mListener;

    private HashMap<String, User> hashMapFarmers;

    public FarmMainInfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FarmMainInfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FarmMainInfoFragment newInstance(String param1, String param2) {
        FarmMainInfoFragment fragment = new FarmMainInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
                View rootView = inflater.inflate(R.layout.fragment_farm_main_info, container, false);

        mLinearMain = (LinearLayout)rootView.findViewById(R.id.linear_main);

        mEditTextCompanyName = (EditText)rootView.findViewById(R.id.editTextCompanyName);

        mAutocompleteMol = (AutoCompleteTextView)rootView.findViewById(R.id.autocompleteMol);
        /*ArrayAdapter<String> adapterMol = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item);
        try {
            for (Farm f : ((FarmAddActivity)getActivity()).getDatabaseHelper().getDaoFarm().queryBuilder().groupByRaw("mol").query()) {
                adapterMol.add(f.getMol());
            }
            for (User u : ((FarmAddActivity)getActivity()).getDatabaseHelper().getDaoUser().queryForAll()) {
                String name = u.getFirstName()+" "+u.getSurName()+" "+u.getFamilyName();
                int a = adapterMol.getPosition(name);
                if(adapterMol.getPosition(name)<0){
                    adapterMol.add(name);
                }
            }
            adapterMol.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mAutocompleteMol.setAdapter(adapterMol);
        }catch (SQLException e) {
            e.printStackTrace();
        }*/

        mEditTextEik = (EditText)rootView.findViewById(R.id.editTextEik);

        mEditTextBulstat = (EditText)rootView.findViewById(R.id.editTextBulstat);

        mAutocompleteFarmer = (AutoCompleteTextView)rootView.findViewById(R.id.autocompleteFarmer);
        /*hashMapFarmers = new HashMap<String, User>();
        ArrayAdapter<String> adapterFarmer = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item);
        try {
            for (User u : ((FarmAddActivity)getActivity()).getDatabaseHelper().getDaoUser().queryForAll()) {
                String name = u.getFirstName()+" "+u.getSurName()+" "+u.getFamilyName();
                adapterFarmer.add(name);
                hashMapFarmers.put(name, u);
            }
            adapterFarmer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mAutocompleteFarmer.setAdapter(adapterFarmer);
        }catch (SQLException e) {
            e.printStackTrace();
        }*/

        mEditTextBreedingPlaceNumber = (EditText)rootView.findViewById(R.id.editTextBreedingPlaceNumber);

        mEditTextTotalAnimals = (EditText)rootView.findViewById(R.id.editTextTotalAnimals);

        mAutocompleteHerdbookVolumeNumber = (AutoCompleteTextView)rootView.findViewById(R.id.autocompleteHerdbookVolumeNumber);
        /*ArrayAdapter<String> adapterHerdbookVolumeNumber = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item);
        try {
            for (Farm f : ((FarmAddActivity)getActivity()).getDatabaseHelper().getDaoFarm().queryBuilder().groupBy("herdbookVolumeNumber").query()) {
                adapterHerdbookVolumeNumber.add(f.getHerdbookVolumeNumber());
            }
            adapterHerdbookVolumeNumber.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mAutocompleteHerdbookVolumeNumber.setAdapter(adapterHerdbookVolumeNumber);
        }catch (SQLException e) {
            e.printStackTrace();
        }*/

        mButtonAddPhone = (Button)rootView.findViewById(R.id.button_add_phone);
        mButtonAddPhone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialog = new AddPhoneFragment();
                dialog.show(getActivity().getFragmentManager(), "AddPhoneDialogFragment");
            }
        });

        return rootView;
    }

    private void viewContactPhones(){

    }

    public void addContactButton(Phone phone){
        Button b = new Button(getActivity());
        b.setText(phone.getPhoneNumber()+" - "+phone.getContactPerson());
        b.setTag(phone);
        mLinearMain.addView(b, mLinearMain.indexOfChild(mButtonAddPhone),
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
    }

    public String getCompanyName(){
        return mEditTextCompanyName.getText().toString();
    }

    public String getMol(){
        return mAutocompleteMol.getText().toString();
    }

    public String getEik(){
        return mEditTextEik.getText().toString();
    }

    public String getBulstat(){
        return mEditTextBulstat.getText().toString();
    }

    public User getFarmer(){
        return hashMapFarmers.get(mAutocompleteFarmer.getText().toString());
    }

    public String getBreedingPlaceNumber(){
        return mEditTextBreedingPlaceNumber.getText().toString();
    }

    public String getTotalAnimals(){
        return mEditTextTotalAnimals.getText().toString();
    }

    public String getHerdbookVolumeNumber(){
        return mAutocompleteHerdbookVolumeNumber.getText().toString();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFarmMainInfoFragmentInteraction();;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFarmMainInfoFragmentInteractionListener) {
            mListener = (OnFarmMainInfoFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFarmMainInfoFragmentInteractionListener {
        void onFarmMainInfoFragmentInteraction();
    }
}
