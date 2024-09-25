package com.jws.jwsapi.core.user;

import static com.jws.jwsapi.dialog.DialogUtil.keyboard;
import static com.jws.jwsapi.dialog.DialogUtil.keyboardInt;
import static com.jws.jwsapi.dialog.DialogUtil.keyboardPassword;
import static com.jws.jwsapi.utils.SpinnerHelper.setupSpinner;

import android.app.AlertDialog;
import android.content.Context;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;

import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.DialogoUsuarioBinding;
import com.jws.jwsapi.utils.ToastHelper;

import java.util.Arrays;

public class UserCreateDialog {
    private final Context context;
    private final UserCreateInterface userCreateInterface;

    public UserCreateDialog(Context context, UserCreateInterface userCreateInterface) {
        this.context = context;
        this.userCreateInterface = userCreateInterface;
    }

    public void showDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        DialogoUsuarioBinding binding = DialogoUsuarioBinding.inflate(LayoutInflater.from(context));

        setupSpinner(binding.spinnertipo, context, Arrays.asList(context.getResources().getStringArray(R.array.tipoUsuarios)));
        handleCreateDialogListeners(binding);
        mBuilder.setView(binding.getRoot());
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        binding.buttons.setOnClickListener(view -> userCreateInterface.handleCreateUserButton(binding, dialog));
        binding.buttonc.setOnClickListener(view -> {
            resetDialogTexts(binding);
            dialog.cancel();
        });
    }

    public void handleCreateDialogListeners(DialogoUsuarioBinding binding) {
        binding.tvnContrasena.setTransformationMethod(new AsteriskPasswordTransformationMethod());
        binding.tvnContrasena.setOnClickListener(v -> keyboardPassword(binding.tvnContrasena, context.getString(R.string.input_password), context, true, null, PasswordTransformationMethod.getInstance()));
        binding.tvnNombre.setOnClickListener(v -> keyboard(binding.tvnNombre, context.getString(R.string.input_name), context, null));
        binding.tvnUsuario.setOnClickListener(v -> keyboard(binding.tvnUsuario, context.getString(R.string.input_user), context, texto -> {
            if (userCreateInterface.searchUserOrCode(usuario -> usuario.getUser().equals(texto))) {
                ToastHelper.message(context.getString(R.string.user_error_user_exist), R.layout.item_customtoasterror, context);
                binding.tvnUsuario.setText("");
            }
        }));
        binding.tvcodigo.setOnClickListener(v -> keyboardInt(binding.tvcodigo, context.getString(R.string.input_code), context, texto -> {
            if (userCreateInterface.searchUserOrCode(usuario -> usuario.getCode().equals(texto))) {
                ToastHelper.message(context.getString(R.string.user_error_code_exist), R.layout.item_customtoasterror, context);
                binding.tvcodigo.setText("");
            }
        }));
    }

    private void resetDialogTexts(DialogoUsuarioBinding binding) {
        binding.tvcodigo.setText("");
        binding.tvnUsuario.setText("");
        binding.tvnNombre.setText("");
    }
}
