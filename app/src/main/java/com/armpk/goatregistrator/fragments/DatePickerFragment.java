package com.armpk.goatregistrator.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.armpk.goatregistrator.R;

import java.util.Calendar;
import java.util.Locale;


public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private OnDatePickerFragmentInteractionListener mListener;

    public DatePickerFragment(){

    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mListener = (OnDatePickerFragmentInteractionListener) getActivity();

        // Use the current date as the default date in the picker
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and return it
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    public void onDateSet(DatePicker view, int year, int month, int day) {
        // Do something with the date chosen by the user

        Calendar c = Calendar.getInstance();
        c.set(year, month, day);
        if (mListener != null) {
            mListener.onDateSet(c);
        }
        /*buttonBirthdate.setText(
                day+" "+
                        c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())+" "+
                        year);*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDatePickerFragmentInteractionListener) {
            mListener = (OnDatePickerFragmentInteractionListener) context;
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
    public interface OnDatePickerFragmentInteractionListener {
        // TODO: Update argument type and name
        void onDateSet(Calendar calendar);
    }
}
