package com.jws.jwsapi.utils.date;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.jws.jwsapi.R;

public class DateDialogController {

    public static AlertDialog createDatePickerDialog(Context context, View view, DatePickerDialogFragment.DatePickerListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        final AlertDialog dialog = builder.create();

        Button buttonGuardar = view.findViewById(R.id.buttons);
        Button buttonCancelar = view.findViewById(R.id.buttonc);

        DatePicker datePicker = view.findViewById(R.id.datePicker2);
        buttonGuardar.setOnClickListener(v -> {
            int selectedYear = datePicker.getYear();
            int selectedMonth = datePicker.getMonth();
            int selectedDay = datePicker.getDayOfMonth();
            String selectedDate = DateUtils.formatDate(selectedDay, selectedMonth + 1, selectedYear);
            if (listener != null) {
                listener.onDateSelected(selectedDate);
            }
            dialog.dismiss();
        });

        buttonCancelar.setOnClickListener(v -> dialog.dismiss());

        return dialog;
    }
}