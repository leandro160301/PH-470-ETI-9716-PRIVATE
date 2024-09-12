package com.jws.jwsapi.general.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.text.method.KeyListener;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.DialogoDosopcionesBinding;
import com.jws.jwsapi.databinding.DialogoDosopcionescheckboxBinding;
import com.jws.jwsapi.databinding.DialogoDossinetBinding;
import com.jws.jwsapi.general.utils.Utils;

public class DialogUtil {

    private static void showDialog(TextView view, String texto, Context context, DialogInputInterface dialogInterface, boolean numeric, Integer inputType, KeyListener key, DialogButtonInterface dialogButtonInterface, String textoCancelar , PasswordTransformationMethod passwordTransformationMethod){
        LayoutInflater inflater = LayoutInflater.from(context);
        DialogoDosopcionesBinding dialogBinding = DialogoDosopcionesBinding.inflate(inflater);
        final EditText userInput = dialogBinding.etDatos;
        dialogBinding.textViewt.setText(texto);
        if(textoCancelar!=null)dialogBinding.buttonc.setText(textoCancelar);
        userInput.setOnLongClickListener(v -> true);
        if(inputType!=null)userInput.setInputType(inputType);
        if(key!=null)userInput.setKeyListener(key);
        if(passwordTransformationMethod!=null)userInput.setTransformationMethod(passwordTransformationMethod);
        userInput.requestFocus();
        if(view!=null)userInput.setText(view.getText().toString());
        if(view!=null&&!view.getText().toString().equals("")) userInput.setSelection(userInput.getText().length());
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setView(dialogBinding.getRoot());
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        dialogBinding.buttons.setOnClickListener(v -> {
            String input = userInput.getText().toString();
            if(numeric){
                if(Utils.isNumeric(input)) {
                    if(view!=null)view.setText(input);
                    if(dialogInterface!=null)dialogInterface.textoIngresado(input);
                    dialog.cancel();
                }
            }else{
                if(view!=null)view.setText(input);
                if(dialogInterface!=null)dialogInterface.textoIngresado(input);
                dialog.cancel();
            }

        });
        dialogBinding.buttonc.setOnClickListener(view1 -> {
            if(dialogButtonInterface!=null)dialogButtonInterface.buttonClick(); // si quiere tener acceso a el click cancelar
            dialog.cancel();
        });
        dialogBinding.lndeleteText.setOnClickListener(v -> userInput.setText(""));
    }

    private static void showDialogCheckbox(TextView view, String texto, Context context, DialogCheckboxInterface dialogInterface, boolean numeric, Integer inputType, KeyListener key, String textoCheckbox, Integer visible, boolean checkboxState){
        LayoutInflater inflater = LayoutInflater.from(context);
        DialogoDosopcionescheckboxBinding dialogBinding = DialogoDosopcionescheckboxBinding.inflate(inflater);
        final EditText userInput = dialogBinding.etDatos;
        if(visible!=null)dialogBinding.checkBox.setVisibility(visible);
        dialogBinding.checkBox.setChecked(checkboxState);
        dialogBinding.checkBox.setText(textoCheckbox);
        dialogBinding.textViewt.setText(texto);
        userInput.setOnLongClickListener(v -> true);
        if(inputType!=null)userInput.setInputType(inputType);
        if(key!=null)userInput.setKeyListener(key);
        userInput.requestFocus();
        if(view!=null)userInput.setText(view.getText().toString());
        if(view!=null&&!view.getText().toString().equals("")) userInput.setSelection(userInput.getText().length());
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
        mBuilder.setView(dialogBinding.getRoot());
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        dialogBinding.buttons.setOnClickListener(v -> {
            String input = userInput.getText().toString();
            if(numeric){
                if(Utils.isNumeric(input)) {
                    if(view!=null)view.setText(input);
                    if(dialogInterface!=null)dialogInterface.textoIngresadoCheckbox(input,dialogBinding.checkBox.isChecked());
                    dialog.cancel();
                }
            }else{
                if(view!=null)view.setText(input);
                if(dialogInterface!=null)dialogInterface.textoIngresadoCheckbox(input,dialogBinding.checkBox.isChecked());
                dialog.cancel();
            }

        });
        dialogBinding.buttonc.setOnClickListener(view1 -> dialog.cancel());
        dialogBinding.lndeleteText.setOnClickListener(v -> userInput.setText(""));
    }

    public static AlertDialog dialogLoading(Context context, String texto){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context,R.style.AlertDialogCustom);
        View mView = LayoutInflater.from(context).inflate(R.layout.dialogo_transferenciaarchivo, null);
        TextView textView = mView.findViewById(R.id.textView);
        textView.setText(texto);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        return dialog;
    }

    private static void showDialogText(Context context, String texto, String textoBoton, DialogButtonInterface botonDerechoInterface, DialogButtonInterface botonIzquierdoInterface){
        LayoutInflater inflater = LayoutInflater.from(context);
        DialogoDossinetBinding dialogBinding = DialogoDossinetBinding.inflate(inflater);
        dialogBinding.textViewt.setText(texto);
        dialogBinding.buttons.setText(textoBoton);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context,R.style.AlertDialogCustom);
        mBuilder.setView(dialogBinding.getRoot());
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        dialogBinding.buttons.setOnClickListener(view -> {
            if(botonDerechoInterface!=null)botonDerechoInterface.buttonClick();
            dialog.cancel();
        });
        dialogBinding.buttonc.setOnClickListener(view -> {
            if(botonIzquierdoInterface!=null)botonIzquierdoInterface.buttonClick();
            dialog.cancel();
        });
    }


    public static void keyboard(TextView view, String texto, Context context, DialogInputInterface dialogInterface) {
        showDialog(view,texto,context,dialogInterface,false,null,null,null, null,null);
    }

    public static void keyboardPassword(TextView view, String texto, Context context, DialogInputInterface dialogInterface, PasswordTransformationMethod passwordTransformationMethod) {
        showDialog(view,texto,context,dialogInterface,false,InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS| InputType.TYPE_CLASS_NUMBER| InputType.TYPE_TEXT_VARIATION_PASSWORD,null,null, null,passwordTransformationMethod);
    }

    public static void keyboardFloat(TextView view, String texto, Context context, DialogInputInterface dialogInterface) {
        showDialog(view,texto,context,dialogInterface,true,InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL,DigitsKeyListener.getInstance(".0123456789"),null, null,null);
    }

    public static void keyboardInt(TextView view, String texto, Context context, DialogInputInterface dialogInterface) {
        showDialog(view,texto,context,dialogInterface,true,InputType.TYPE_CLASS_NUMBER,null,null, null,null);
    }

    public static void keyboardFloatCancel(TextView view, String texto, Context context, DialogInputInterface dialogInterface, DialogButtonInterface buttonInterface, String textoCancelar) {
        showDialog(view,texto,context,dialogInterface,true,InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL,DigitsKeyListener.getInstance(".0123456789"),buttonInterface,textoCancelar,null);
    }

    public static void dialogText(Context context, String texto, String textoBoton, DialogButtonInterface dialogInterface) {
        showDialogText(context,texto,textoBoton,dialogInterface,null);
    }

    public static void dialogTextCancel(Context context, String texto, String textoBoton, DialogButtonInterface botonDerechoInterface, DialogButtonInterface botonIzquierdoInterface) {
        showDialogText(context,texto,textoBoton,botonDerechoInterface,botonIzquierdoInterface);
    }

    public static void dialogCheckboxVisibility(TextView view, String texto, Context context, DialogCheckboxInterface dialogInterface, boolean isKeyboardNumeric, Integer inputType, KeyListener key, String textoCheckbox, Integer visible, boolean checkboxState){
        showDialogCheckbox(view,texto,context,dialogInterface,isKeyboardNumeric,inputType,key,textoCheckbox,visible,checkboxState);
    }



}
