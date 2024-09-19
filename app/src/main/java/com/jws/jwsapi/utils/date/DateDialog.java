package com.jws.jwsapi.utils.date;

import static com.jws.jwsapi.dialog.DialogUtil.keyboardInt;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.DialogoFechayhoraBinding;

public class DateDialog {
    private final DateInterface dateInterface;
    private final Context context;

    public DateDialog(DateInterface dateInterface, Context context) {
        this.dateInterface = dateInterface;
        this.context = context;
    }

    public void showDialog(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        DialogoFechayhoraBinding binding = DialogoFechayhoraBinding.inflate(LayoutInflater.from(context));
        View mView = binding.getRoot();

        binding.tvMinutos.setOnLongClickListener(view -> {
            dateInterface.setRemoteFix();
            return false;
        });
        binding.tvMinutos.setOnClickListener(view -> keyboardInt(binding.tvMinutos, "Ingrese los minutos", context, minutes -> checkMinutes(minutes,binding.tvMinutos)));
        binding.tvHora.setOnClickListener(view -> keyboardInt(binding.tvHora, "Ingrese la hora", context, hour -> checkHour(hour,binding.tvHora)));
        binding.tvDia.setOnClickListener(view -> keyboardInt(binding.tvDia, "Ingrese el dia", context, day -> checkDay(day,binding.tvDia)));
        binding.tvMes.setOnClickListener(view -> keyboardInt(binding.tvMes, "Ingrese el mes", context, month -> checkMonth(month,binding.tvMes)));
        binding.tvAno.setOnClickListener(view -> keyboardInt(binding.tvAno, "Ingrese el año", context, year -> checkYear(year,binding.tvAno)));

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        binding.buttons.setOnClickListener(view -> {
            String day=binding.tvDia.getText().toString();
            String hour=binding.tvHora.getText().toString();
            String minutes=binding.tvMinutos.getText().toString();
            String month=binding.tvMes.getText().toString();
            String year=binding.tvAno.getText().toString();
            if(dateInterface.setDate(day, hour, minutes, month, year)) {
                dialog.cancel();
            }

        });
        binding.buttonc.setOnClickListener(view -> dialog.cancel());

    }

    private void checkMinutes(String userInput, TextView textView) {
        if (Integer.parseInt(userInput) > 59) {
            textView.setText("");
        }
    }

    private void checkYear(String userInput, TextView textView) {
        if (Integer.parseInt(userInput) >= 2200) {
            textView.setText("");
        }
    }

    private void checkMonth(String userInput, TextView textView) {
        if (Integer.parseInt(userInput) > 12) {
            textView.setText("");
        }
    }

    private void checkDay(String userInput, TextView textView) {
        if (Integer.parseInt(userInput) > 31) {
            textView.setText("");
        }
    }

    private void checkHour(String userInput, TextView textView) {
        if (Integer.parseInt(userInput) > 24) {
            textView.setText("");
        }
    }
}
