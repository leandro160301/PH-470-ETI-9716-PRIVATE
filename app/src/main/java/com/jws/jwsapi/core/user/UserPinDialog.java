package com.jws.jwsapi.core.user;

import static com.jws.jwsapi.core.user.UserUtils.getNewPin;
import static com.jws.jwsapi.dialog.DialogUtil.keyboardInt;
import static com.jws.jwsapi.utils.Utils.randomNumber;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;

import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.DialogoPinBinding;

public class UserPinDialog {
    private final Context context;
    private final UserPinInterface userPinInterface;

    public UserPinDialog(Context context, UserPinInterface userPinInterface) {
        this.context = context;
        this.userPinInterface = userPinInterface;
    }

    public void showDialog() {
        final String[] newPin = {"error"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        DialogoPinBinding binding = DialogoPinBinding.inflate(LayoutInflater.from(context));

        mBuilder.setView(binding.getRoot());
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        binding.tvpin.setOnClickListener(view -> keyboardInt(binding.tvpin, null, context, null));
        binding.btGenerar.setOnClickListener(view -> {
            int Codigo = randomNumber();
            newPin[0] = getNewPin(Codigo);
            binding.tvCodigo.setText(String.valueOf(Codigo));
        });

        binding.buttonc.setOnClickListener(view -> dialog.cancel());
        binding.buttons.setOnClickListener(view -> {
            String pinFromTv = binding.tvpin.getText().toString();
            if(userPinInterface.setupPin(newPin[0], pinFromTv)) dialog.cancel();
        });
    }



}
