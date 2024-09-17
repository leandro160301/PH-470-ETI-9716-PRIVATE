package com.jws.jwsapi.core.user;

import static com.jws.jwsapi.dialog.DialogUtil.keyboardPassword;
import static com.jws.jwsapi.utils.SpinnerHelper.setupSpinner;

import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.widget.TextView;

import com.jws.jwsapi.R;
import com.jws.jwsapi.core.data.local.PreferencesManagerBase;
import com.jws.jwsapi.databinding.DialogoLogeoBinding;


public class UserDialogHandler {

    public void showDialog(Context context, UserManager userManager, Application application){
        PreferencesManagerBase preferencesManagerBase=new PreferencesManagerBase(application);
        DialogoLogeoBinding binding = DialogoLogeoBinding.inflate(LayoutInflater.from(context));
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        setupSpinner(binding.spinner,application,userManager.getUsersSpinner());
        binding.spinner.setSelection(0);
        binding.tvnContrasena.setOnClickListener(view -> showPasswordKeyboard(binding.tvnContrasena,context));
        binding.buttons.setText(context.getString(R.string.dialog_login));

        mBuilder.setView(binding.getRoot());
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        binding.buttond.setOnClickListener(view -> {
            userManager.logout();
            dialog.cancel();
        });
        binding.buttons.setOnClickListener(view -> userManager.login(preferencesManagerBase, binding.tvnContrasena, binding.spinner, dialog));
        binding.buttonc.setOnClickListener(view -> dialog.cancel());
    }

    private static void showPasswordKeyboard(TextView textView, Context context){
        keyboardPassword(null, "", context, true ,texto -> {
            String copia = texto;
            copia=copia.replaceAll("(?s).", "*");
            textView.setText(copia);
        }, PasswordTransformationMethod.getInstance());
    }
}
