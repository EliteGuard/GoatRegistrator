package com.armpk.goatregistrator.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.util.Calendar;


public class DatePickerFragmentFromFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final String ARG_GOAT_MAIN_INFO_CONTEXT = "goat_main_info_context";

    private OnDatePickerFragmentFromFragmentInteractionListener mListener;

    private GoatMainInfoFragment.GoatMainInfoContext mGoatMainInfoContext;

    public DatePickerFragmentFromFragment(){

    }

    public static DatePickerFragmentFromFragment newInstance(GoatMainInfoFragment.GoatMainInfoContext fragmentContext) {
        DatePickerFragmentFromFragment fragment = new DatePickerFragmentFromFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_GOAT_MAIN_INFO_CONTEXT, fragmentContext);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        if (getArguments() != null) {
            mGoatMainInfoContext = (GoatMainInfoFragment.GoatMainInfoContext) getArguments().getSerializable(ARG_GOAT_MAIN_INFO_CONTEXT);
        }

        mListener = (OnDatePickerFragmentFromFragmentInteractionListener) getActivity();

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
            mListener.onDateSetFromFragment(c, mGoatMainInfoContext);
        }
        /*buttonBirthdate.setText(
                day+" "+
                        c.getDisplayName(Calendar.MONTH, Calendar.LONG, Locale.getDefault())+" "+
                        year);*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnDatePickerFragmentFromFragmentInteractionListener) {
            mListener = (OnDatePickerFragmentFromFragmentInteractionListener) context;
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
    public interface OnDatePickerFragmentFromFragmentInteractionListener {
        // TODO: Update argument type and name
        void onDateSetFromFragment(Calendar calendar, GoatMainInfoFragment.GoatMainInfoContext context);
    }
}
