package com.jws.jwsapi.core.user;

import static com.jws.jwsapi.dialog.DialogUtil.keyboardPassword;
import static com.jws.jwsapi.utils.SpinnerHelper.setupSpinner;

import android.app.AlertDialog;
import android.content.Context;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.DialogoLogeoBinding;

public class UserLoginDialog {

    private static void setupDialogButtons(UserLoginInterface loginInterface, DialogoLogeoBinding binding, AlertDialog dialog) {
        binding.buttond.setOnClickListener(view -> {
            loginInterface.logout();
            dialog.cancel();
        });
        binding.buttons.setOnClickListener(view -> {
            if (loginInterface.login(binding.tvnContrasena.getText().toString(), binding.spinner.getSelectedItem().toString())) {
                dialog.cancel();
            }
        });
        binding.buttonc.setOnClickListener(view -> dialog.cancel());
    }

    private static void setupUiEvents(Context context, DialogoLogeoBinding binding) {
        binding.spinner.setSelection(0);
        binding.tvnContrasena.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        binding.tvnContrasena.setOnClickListener(view -> showPasswordKeyboard(binding.tvnContrasena, context));
        binding.buttons.setText(context.getString(R.string.dialog_login));
    }

    private static void showPasswordKeyboard(TextView textView, Context context) {
        keyboardPassword(textView, "", context, true, text -> {
        }, PasswordTransformationMethod.getInstance());
    }

    public void showDialog(Context context, UserLoginInterface loginInterface) {
        DialogoLogeoBinding binding = DialogoLogeoBinding.inflate(LayoutInflater.from(context));
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        setupSpinner(binding.spinner, context, loginInterface.getUsersSpinner());
        setupUiEvents(context, binding);
        mBuilder.setView(binding.getRoot());
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        setupDialogButtons(loginInterface, binding, dialog);
    }
}
