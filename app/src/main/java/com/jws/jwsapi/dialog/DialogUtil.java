package com.jws.jwsapi.dialog;

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
import com.jws.jwsapi.utils.Utils;

public class DialogUtil {

    private static void showDialog(TextView view, String text, Context context, DialogInputInterface dialogInterface, boolean numeric, Integer inputType, KeyListener key, DialogButtonInterface dialogButtonInterface, String cancelText , PasswordTransformationMethod passwordTransformationMethod){
        LayoutInflater inflater = LayoutInflater.from(context);
        DialogoDosopcionesBinding dialogBinding = DialogoDosopcionesBinding.inflate(inflater);
        final EditText userInput = dialogBinding.etDatos;
        dialogBinding.textViewt.setText(text);
        if(cancelText!=null)dialogBinding.buttonc.setText(cancelText);
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

    private static void showDialogCheckbox(TextView view, String text, Context context, DialogCheckboxInterface dialogInterface, boolean numeric, Integer inputType, KeyListener key, String textoCheckbox, Integer visible, boolean checkboxState){
        LayoutInflater inflater = LayoutInflater.from(context);
        DialogoDosopcionescheckboxBinding dialogBinding = DialogoDosopcionescheckboxBinding.inflate(inflater);
        final EditText userInput = dialogBinding.etDatos;
        if(visible!=null)dialogBinding.checkBox.setVisibility(visible);
        dialogBinding.checkBox.setChecked(checkboxState);
        dialogBinding.checkBox.setText(textoCheckbox);
        dialogBinding.textViewt.setText(text);
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

    public static AlertDialog dialogLoading(Context context, String text){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context,R.style.AlertDialogCustom);
        View mView = LayoutInflater.from(context).inflate(R.layout.dialogo_transferenciaarchivo, null);
        TextView textView = mView.findViewById(R.id.textView);
        textView.setText(text);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        return dialog;
    }

    private static void showDialogText(Context context, String text, String buttonText, DialogButtonInterface rightButtonClick, DialogButtonInterface leftButtonClick){
        LayoutInflater inflater = LayoutInflater.from(context);
        DialogoDossinetBinding dialogBinding = DialogoDossinetBinding.inflate(inflater);
        dialogBinding.textViewt.setText(text);
        dialogBinding.buttons.setText(buttonText);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context,R.style.AlertDialogCustom);
        mBuilder.setView(dialogBinding.getRoot());
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        dialogBinding.buttons.setOnClickListener(view -> {
            if(rightButtonClick!=null)rightButtonClick.buttonClick();
            dialog.cancel();
        });
        dialogBinding.buttonc.setOnClickListener(view -> {
            if(leftButtonClick!=null)leftButtonClick.buttonClick();
            dialog.cancel();
        });
    }


    public static void keyboard(TextView view, String text, Context context, DialogInputInterface dialogInterface) {
        showDialog(view,text,context,dialogInterface,false,null,null,null, null,null);
    }

    public static void keyboardPassword(TextView view, String text, Context context, boolean numeric, DialogInputInterface dialogInterface, PasswordTransformationMethod passwordTransformationMethod) {
        showDialog(view,text,context,dialogInterface,false,(numeric? InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS| InputType.TYPE_CLASS_NUMBER| InputType.TYPE_TEXT_VARIATION_PASSWORD : InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS| InputType.TYPE_TEXT_VARIATION_PASSWORD),null,null, null,passwordTransformationMethod);
    }

    public static void keyboardIpAdress(TextView view, String text, Context context, DialogInputInterface dialogInterface) {
        showDialog(view,text,context,dialogInterface,false,InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL,DigitsKeyListener.getInstance(".0123456789"),null, null,null);
    }

    public static void keyboardFloat(TextView view, String text, Context context, DialogInputInterface dialogInterface) {
        showDialog(view,text,context,dialogInterface,true,InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL,null,null, null,null);
    }

    public static void keyboardInt(TextView view, String text, Context context, DialogInputInterface dialogInterface) {
        showDialog(view,text,context,dialogInterface,true,InputType.TYPE_CLASS_NUMBER,null,null, null,null);
    }

    public static void keyboardFloatCancel(TextView view, String text, Context context, DialogInputInterface dialogInterface, DialogButtonInterface buttonInterface, String textoCancelar) {
        showDialog(view,text,context,dialogInterface,true,InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL,null,buttonInterface,textoCancelar,null);
    }

    public static void dialogText(Context context, String text, String textoBoton, DialogButtonInterface dialogInterface) {
        showDialogText(context,text,textoBoton,dialogInterface,null);
    }

    public static void dialogTextCancel(Context context, String text, String buttonText, DialogButtonInterface rightButtonClick, DialogButtonInterface leftButtonClick) {
        showDialogText(context,text,buttonText,rightButtonClick,leftButtonClick);
    }

    public static void dialogCheckboxVisibility(TextView view, String text, Context context, DialogCheckboxInterface dialogInterface, boolean isKeyboardNumeric, Integer inputType, KeyListener key, String textoCheckbox, Integer visible, boolean checkboxState){
        showDialogCheckbox(view,text,context,dialogInterface,isKeyboardNumeric,inputType,key,textoCheckbox,visible,checkboxState);
    }



}
