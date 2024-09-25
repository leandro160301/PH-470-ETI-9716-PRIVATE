package com.jws.jwsapi.utils.date;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.jws.jwsapi.R;

public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private DatePickerListener datePickerListener;

    public void setDatePickerListener(DatePickerListener listener) {
        this.datePickerListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.dialogo_fecha, null);

        return DateDialogController.createDatePickerDialog(requireContext(), view, datePickerListener);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String selectedDate = DateUtils.formatDate(dayOfMonth, month + 1, year);
        if (datePickerListener != null) {
            datePickerListener.onDateSelected(selectedDate);
        }
    }
}