package com.armpk.goatregistrator.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;

import com.armpk.goatregistrator.R;
import com.armpk.goatregistrator.database.Phone;
import com.armpk.goatregistrator.utilities.Globals;

import java.util.Date;

public class AddPhoneFragment extends DialogFragment {

    private Phone mPhone;
    private OnAddPhoneFragmentInteractionListener mListener;

    private AutoCompleteTextView mAutocompleteTextPhone;
    private AutoCompleteTextView mAutocompleteTextPersonToContact;
    private AutoCompleteTextView mAutocompleteTextEmail;
    private EditText mEditTextNotes;

    public AddPhoneFragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        //mListener = (OnAddPhoneFragmentInteractionListener) this;

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View rootView = inflater.inflate(R.layout.fragment_add_phone, null);

        mAutocompleteTextPhone = (AutoCompleteTextView)rootView.findViewById(R.id.autocomplete_text_phone_number);
        mAutocompleteTextPersonToContact = (AutoCompleteTextView)rootView.findViewById(R.id.autocomplete_text_name);
        mAutocompleteTextEmail = (AutoCompleteTextView)rootView.findViewById(R.id.autocomplete_text_email);
        mEditTextNotes = (EditText)rootView.findViewById(R.id.edit_text_notes);

        mPhone = new Phone();

        builder.setView(rootView)
                .setTitle(R.string.text_add_phone)
                .setPositiveButton(R.string.text_save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        if (mListener != null && validateFields()) {
                            mListener.onAddPhoneFragmentPositiveClick(mPhone);
                        }
                    }
                })
                .setNegativeButton(R.string.text_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (mListener != null) {
                            mListener.onAddPhoneFragmentNegativeClick();
                        }
                    }
                });
        return builder.create();
    }

    private boolean validateFields(){
        boolean valid = true;

        mAutocompleteTextPhone.setError(null);
        mAutocompleteTextPersonToContact.setError(null);
        mAutocompleteTextEmail.setError(null);
        View focusView = null;

        if(mAutocompleteTextPhone.getText().length()<7){
            mAutocompleteTextPhone.setError("Невалиден телефонен номер");
            valid = false;
            focusView = mAutocompleteTextPhone;
        }else{
            mPhone.setPhoneNumber(mAutocompleteTextPhone.getText().toString());
        }

        if(mAutocompleteTextPersonToContact.getText().length()<3){
            mAutocompleteTextPersonToContact.setError("Прекалено кратко име");
            valid = false;
            focusView = mAutocompleteTextPersonToContact;
        }else{
            mPhone.setContactPerson(mAutocompleteTextPersonToContact.getText().toString());
        }

        if(mAutocompleteTextEmail.getText().length()<1){
            mAutocompleteTextEmail.setError("Няма въведен имейл");
            valid = false;
            focusView = mAutocompleteTextEmail;
        }else{
            mPhone.setEmail(mAutocompleteTextEmail.getText().toString());
        }

        mPhone.setNotes(mEditTextNotes.getText().toString());

        if(!valid) {
            focusView.requestFocus();
        }

        return  valid;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnAddPhoneFragmentInteractionListener) activity;
        } catch (ClassCastException e){
            throw new RuntimeException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnAddPhoneFragmentInteractionListener {
        void onAddPhoneFragmentPositiveClick(Phone phone);
        void onAddPhoneFragmentNegativeClick();
    }
}
