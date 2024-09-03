package com.jws.jwsapi.general.pallet;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.Observer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.jws.jwsapi.R;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PalletFragment extends Fragment {

    private PalletViewModel palletViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pallet, container, false);
        TextView tv= view.findViewById(R.id.tvtituaslo2);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PalletRequest palletRequest = new PalletRequest(1, "Lote 3", "2");
                palletViewModel.createPallet(palletRequest);
            }
        });

        palletViewModel = new ViewModelProvider(this).get(PalletViewModel.class);

        palletViewModel.getPalletResponse().observe(getViewLifecycleOwner(), new Observer<PalletResponse>() {
            @Override
            public void onChanged(PalletResponse palletResponse) {
                Toast.makeText(getContext(), "Pallet created: " + palletResponse.getCode(), Toast.LENGTH_SHORT).show();
            }
        });

        palletViewModel.getLoading().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isLoading) {
                // Mostrar o esconder el indicador de carga
                // e.g., showLoadingIndicator(isLoading);
            }
        });

        palletViewModel.getError().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String error) {
                // Manejar errores
                Toast.makeText(getContext(), "Error: " + error, Toast.LENGTH_SHORT).show();
            }
        });



        return view;
    }
}