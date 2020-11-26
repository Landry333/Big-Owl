package com.example.bigowlapp.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;

public class TimePickerDialogFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    public interface TimePickedListener {
        void onTimePicked(int hour, int minute);
    }

    public static TimePickerDialogFragment newInstance(TimePickedListener listener) {
        TimePickerDialogFragment fragment = new TimePickerDialogFragment();
        fragment.setTimePickedListener(listener);
        return fragment;
    }

    private TimePickedListener timePickedListener;

    public void setTimePickedListener(TimePickedListener timePickedListener) {
        this.timePickedListener = timePickedListener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        return new TimePickerDialog(getActivity(), this, hour, minute, true);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        timePickedListener.onTimePicked(hourOfDay, minute);
    }
}
