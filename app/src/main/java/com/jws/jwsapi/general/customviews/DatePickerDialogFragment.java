package com.jws.jwsapi.general.customviews;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import com.jws.jwsapi.R;

import java.util.Calendar;

public class DatePickerDialogFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    String Fecha="";
    private DatePickerListener datePickerListener;
    int year=0;
    int month=0;
    int day=0;
    public interface DatePickerListener {
        void onDateSelected(String selectedDate);
    }

    public void setDatePickerListener(DatePickerListener listener) {
        datePickerListener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());

        View mView = getLayoutInflater().inflate(R.layout.dialogo_fecha, null);
        DatePicker datePicker = mView.findViewById(R.id.datePicker2);

        final Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);

        // Configurar el DatePicker y establecer la fecha actual
        datePicker.init(year, month, day, null);

        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();



        Guardar.setOnClickListener(view -> {
            year=datePicker.getYear();
            day=datePicker.getDayOfMonth();
            month=datePicker.getMonth();
            int month2= month+1;
            String fm=""+month2;
            String fd=""+day;

            if(month<10){
                fm ="0"+month2;
            }
            if (day<10){
                fd="0"+day;
            }

            String selectedDate = fd + "/" + fm + "/" + year;
            if (datePickerListener != null) {
                datePickerListener.onDateSelected(selectedDate);
            }


            dialog.cancel();
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());




        return dialog;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        // Aquí puedes realizar acciones con la fecha seleccionada
        // Por ejemplo, puedes mostrarla en un TextView
        int month2= month+1;
        String fm=""+month2;
        String fd=""+dayOfMonth;

        if(month<10){
            fm ="0"+month2;
        }
        if (dayOfMonth<10){
            fd="0"+dayOfMonth;
        }

        String selectedDate = fd + "/" + fm + "/" + year;

        if (datePickerListener != null) {
            datePickerListener.onDateSelected(selectedDate);
        }
        Fecha=selectedDate;
        //textView.setText(selectedDate);
    }
    public String getFecha(){
        return Fecha;
    }
}