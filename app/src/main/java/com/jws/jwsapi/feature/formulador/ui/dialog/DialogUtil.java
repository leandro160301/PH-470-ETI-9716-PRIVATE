package com.jws.jwsapi.feature.formulador.ui.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.jws.jwsapi.databinding.DialogoDosopcionesBinding;
import com.jws.jwsapi.databinding.DialogoDosopcionespuntosBinding;
import com.jws.jwsapi.databinding.DialogoDossinetBinding;
import com.jws.jwsapi.utils.Utils;

public class DialogUtil {
    public static void Teclado(TextView view, String texto, Context context, DialogInputInterface dialogInterface) {
        LayoutInflater inflater = LayoutInflater.from(context);
        DialogoDosopcionesBinding dialogBinding = DialogoDosopcionesBinding.inflate(inflater);

        final EditText userInput = dialogBinding.etDatos;
        TextView textView = dialogBinding.textViewt;
        Button guardar = dialogBinding.buttons;
        Button cancelar = dialogBinding.buttonc;
        LinearLayout deleteText = dialogBinding.lndeleteText;

        textView.setText(texto);
        userInput.setOnLongClickListener(v -> true);
        userInput.requestFocus();
        userInput.setText(view.getText().toString());
        if (!view.getText().toString().equals("")) {
            userInput.setSelection(userInput.getText().length());
        }
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setView(dialogBinding.getRoot());
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        guardar.setOnClickListener(v -> {
            String input = userInput.getText().toString();
            view.setText(input);
            dialogInterface.textoIngresado(input);
            dialog.cancel();
        });
        cancelar.setOnClickListener(v -> dialog.cancel());
        deleteText.setOnClickListener(v -> userInput.setText(""));
    }

    public static void TecladoFlotante(TextView view, String texto, Context context, DialogInputInterface dialogInterface) {
        LayoutInflater inflater = LayoutInflater.from(context);
        DialogoDosopcionespuntosBinding dialogBinding = DialogoDosopcionespuntosBinding.inflate(inflater);

        final EditText userInput = dialogBinding.etDatos;
        TextView textView = dialogBinding.textViewt;
        Button guardar = dialogBinding.buttons;
        Button cancelar = dialogBinding.buttonc;
        LinearLayout deleteText = dialogBinding.lndeleteText;

        textView.setText(texto);
        userInput.setOnLongClickListener(v -> true);
        userInput.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
        userInput.setKeyListener(DigitsKeyListener.getInstance(".0123456789"));
        userInput.setText(view.getText().toString());
        userInput.requestFocus();
        if (!view.getText().toString().equals("")) {
            userInput.setSelection(userInput.getText().length());
        }
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setView(dialogBinding.getRoot());
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        guardar.setOnClickListener(v -> {
            String input = userInput.getText().toString();
            if(Utils.isNumeric(input)) {
                view.setText(input);
                dialogInterface.textoIngresado(input);
            }
            dialog.cancel();
        });

        cancelar.setOnClickListener(v -> dialog.cancel());
        deleteText.setOnClickListener(v -> userInput.setText(""));
    }

    public static void TecladoEntero(TextView view, String texto, Context context, DialogInputInterface dialogInterface) {
        LayoutInflater inflater = LayoutInflater.from(context);
        DialogoDosopcionespuntosBinding dialogBinding = DialogoDosopcionespuntosBinding.inflate(inflater);

        final EditText userInput = dialogBinding.etDatos;
        TextView textView = dialogBinding.textViewt;
        Button guardar = dialogBinding.buttons;
        Button cancelar = dialogBinding.buttonc;
        LinearLayout deleteText = dialogBinding.lndeleteText;

        textView.setText(texto);
        userInput.setOnLongClickListener(v -> true);
        userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        userInput.setText(view.getText().toString());
        userInput.requestFocus();
        if (!view.getText().toString().equals("")) {
            userInput.setSelection(userInput.getText().length());
        }
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setView(dialogBinding.getRoot());
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        guardar.setOnClickListener(v -> {
            String input = userInput.getText().toString();
            if(Utils.isNumeric(input)) {
                view.setText(input);
                dialogInterface.textoIngresado(input);
            }
            dialog.cancel();
        });

        cancelar.setOnClickListener(v -> dialog.cancel());
        deleteText.setOnClickListener(v -> userInput.setText(""));
    }

    public static void dialogoTexto(Context context, String texto, String textoBoton, DialogButtonInterface dialogInterface) {
        LayoutInflater inflater = LayoutInflater.from(context);
        DialogoDossinetBinding dialogBinding = DialogoDossinetBinding.inflate(inflater);

        dialogBinding.textViewt.setText(texto);
        dialogBinding.buttons.setText(textoBoton);

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setView(dialogBinding.getRoot());
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        dialogBinding.buttons.setOnClickListener(view -> {
            dialogInterface.buttonClick();
            dialog.cancel();
        });
        dialogBinding.buttonc.setOnClickListener(view -> dialog.cancel());
    }

}
