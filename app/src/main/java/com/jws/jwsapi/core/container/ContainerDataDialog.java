package com.jws.jwsapi.core.container;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.DialogoInformacionBinding;

public class ContainerDataDialog {
    private final ContainerData containerData;
    private final Context context;

    public ContainerDataDialog(ContainerData containerData, Context context) {
        this.containerData = containerData;
        this.context = context;
    }

    public void showDialog(){
        DialogoInformacionBinding binding = DialogoInformacionBinding.inflate(LayoutInflater.from(context));
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        if(!containerData.getStorageState()){
            binding.buttons.setVisibility(View.INVISIBLE);
        }
        binding.tvIP.setText(containerData.getIp());
        binding.tvVersion.setText(containerData.getVersion());
        mBuilder.setView(binding.getRoot());
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        binding.buttonc.setOnClickListener(view -> dialog.cancel());
        binding.buttons.setOnClickListener(view -> {
            containerData.openStorage();
            dialog.cancel();
        });



    }
}
