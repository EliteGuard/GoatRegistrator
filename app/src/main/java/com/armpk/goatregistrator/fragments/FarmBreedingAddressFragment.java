package com.armpk.goatregistrator.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.activities.FarmAddActivity;
import com.armpk.goatregistrator.adapters.AreaCursorAdapter;
import com.armpk.goatregistrator.adapters.CityCursorAdapter;
import com.armpk.goatregistrator.adapters.MunicipalityCursorAdapter;
import com.armpk.goatregistrator.adapters.PostCodeCursorAdapter;
import com.armpk.goatregistrator.adapters.StreetCursorAdapter;
import com.armpk.goatregistrator.database.Address;
import com.armpk.goatregistrator.database.City;
import com.armpk.goatregistrator.database.User;

import java.sql.SQLException;
import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FarmBreedingAddressFragment.OnFarmBreedingAddressFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FarmBreedingAddressFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FarmBreedingAddressFragment extends Fragment {

    private HashMap<String, City> hashMapCities;

    private AutoCompleteTextView mAutocompleteArea;
    private AutoCompleteTextView mAutocompleteMunicipality;
    private AutoCompleteTextView mAutocompleteCity;
    private AutoCompleteTextView mAutocompleteStreetName;
    private AutoCompleteTextView mAutocompletePostCode;
    private AutoCompleteTextView mAutocompleteEmail;
    private EditText mEditTextNotes;

    private AreaCursorAdapter areaCursorAdapter;
    private MunicipalityCursorAdapter municipalityCursorAdapter;
    private CityCursorAdapter cityCursorAdapter;
    private StreetCursorAdapter streetCursorAdapter;
    private PostCodeCursorAdapter postCodeCursorAdapter;

    private City mCity;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFarmBreedingAddressFragmentInteractionListener mListener;

    public FarmBreedingAddressFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FarmBreedingAddressFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FarmBreedingAddressFragment newInstance(String param1, String param2) {
        FarmBreedingAddressFragment fragment = new FarmBreedingAddressFragment();
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
        View rootView = inflater.inflate(R.layout.fragment_farm_management_address, container, false);

        mAutocompleteArea = (AutoCompleteTextView)rootView.findViewById(R.id.autocomplete_area);
        areaCursorAdapter = new AreaCursorAdapter(getActivity(), null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER,((FarmAddActivity)getActivity()).getDatabaseHelper());
        mAutocompleteArea.setAdapter(areaCursorAdapter);
        mAutocompleteArea.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor c = ((Cursor)adapterView.getItemAtPosition(i));
                municipalityCursorAdapter.setArea(c.getString(c.getColumnIndex("area")));
                cityCursorAdapter.setArea(c.getString(c.getColumnIndex("area")));
                mAutocompleteMunicipality.requestFocus();
            }
        });

        mAutocompleteMunicipality = (AutoCompleteTextView)rootView.findViewById(R.id.autocomplete_municipality);
        municipalityCursorAdapter = new MunicipalityCursorAdapter(getActivity(), null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, ((FarmAddActivity)getActivity()).getDatabaseHelper());
        mAutocompleteMunicipality.setAdapter(municipalityCursorAdapter);
        mAutocompleteMunicipality.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor c = ((Cursor)adapterView.getItemAtPosition(i));
                cityCursorAdapter.setMunicipality(c.getString(c.getColumnIndex("municipality")));
                mAutocompleteCity.requestFocus();
            }
        });

        mAutocompleteCity = (AutoCompleteTextView)rootView.findViewById(R.id.autocomplete_city);
        cityCursorAdapter = new CityCursorAdapter(getActivity(), null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, ((FarmAddActivity)getActivity()).getDatabaseHelper());
        mAutocompleteCity.setAdapter(cityCursorAdapter);
        mAutocompleteCity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Cursor c = ((Cursor)adapterView.getItemAtPosition(i));
                try {
                    mCity = ((FarmAddActivity)getActivity()).getDatabaseHelper().getDaoCity().queryForId(c.getLong(c.getColumnIndex("_id")));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                mAutocompleteStreetName.requestFocus();
            }
        });

        mAutocompleteStreetName = (AutoCompleteTextView)rootView.findViewById(R.id.autocomplete_street_name);
        streetCursorAdapter = new StreetCursorAdapter(getActivity(), null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, ((FarmAddActivity)getActivity()).getDatabaseHelper());
        mAutocompleteStreetName.setAdapter(streetCursorAdapter);
        mAutocompleteStreetName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mAutocompletePostCode.requestFocus();
            }
        });

        mAutocompletePostCode = (AutoCompleteTextView)rootView.findViewById(R.id.autocomplete_post_code);
        postCodeCursorAdapter = new PostCodeCursorAdapter(getActivity(), null, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER, ((FarmAddActivity)getActivity()).getDatabaseHelper());
        mAutocompletePostCode.setAdapter(postCodeCursorAdapter);
        mAutocompletePostCode.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mAutocompleteEmail.requestFocus();
            }
        });

        mAutocompleteEmail = (AutoCompleteTextView)rootView.findViewById(R.id.autocomplete_email);

        mEditTextNotes = (EditText)rootView.findViewById(R.id.edit_text_notes);

        return rootView;
    }

    public String getArea(){
        return mAutocompleteArea.getText().toString();
    }

    public void setErrorArea(String msg){
        mAutocompleteArea.setError(msg);
    }

    public String getMunicipality(){
        return mAutocompleteMunicipality.getText().toString();
    }

    public void setErrorMunicipality(String msg){
        mAutocompleteMunicipality.setError(msg);
    }

    public City getCity(){
        return mCity;
    }

    public void setErrorCity(String msg){
        mAutocompleteCity.setError(msg);
    }

    public String getStreetName(){
        return mAutocompleteStreetName.getText().toString();
    }

    public void setErrorStreetName(String msg){
        mAutocompleteStreetName.setError(msg);
    }

    public String getPostCode(){
        return mAutocompletePostCode.getText().toString();
    }

    public void setErrorPostCode(String msg){
        mAutocompletePostCode.setError(msg);
    }

    public String getEmail(){
        return mAutocompleteEmail.getText().toString();
    }

    public void setErrorEmail(String msg){
        mAutocompleteEmail.setError(msg);
    }

    public String getNotes(){
        return mEditTextNotes.getText().toString();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFarmBreedingAddressFragmentInteraction();
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFarmBreedingAddressFragmentInteractionListener) {
            mListener = (OnFarmBreedingAddressFragmentInteractionListener) context;
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFarmBreedingAddressFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFarmBreedingAddressFragmentInteraction();
    }
}
