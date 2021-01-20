package com.example.bigowlapp.fragments;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class DatePickerDialogFragment extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    public interface DatePickedListener {
        void onDatePicked(int year, int month, int day);
    }

    private DatePickedListener datePickedListener;

    public static DatePickerDialogFragment newInstance(DatePickedListener listener) {
        DatePickerDialogFragment fragment = new DatePickerDialogFragment();
        fragment.setDatePickedListener(listener);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        datePickedListener.onDatePicked(year, month, dayOfMonth);
    }

    public void setDatePickedListener(DatePickedListener datePickedListener) {
        this.datePickedListener = datePickedListener;
    }
}
