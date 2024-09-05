package com.jws.jwsapi.general.pallet;

import static com.jws.jwsapi.general.dialog.DialogUtil.keyboard;
import static com.jws.jwsapi.general.dialog.DialogUtil.keyboardInt;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.jws.jwsapi.R;
import com.jws.jwsapi.base.ui.activities.MainActivity;
import com.jws.jwsapi.databinding.FragmentPalletBinding;
import com.jws.jwsapi.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PalletFragment extends Fragment {

    private PalletViewModel palletViewModel;
    private FragmentPalletBinding binding;
    private ButtonProvider buttonProvider;
    MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = FragmentPalletBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mainActivity=(MainActivity)getActivity();
        palletViewModel = new ViewModelProvider(this).get(PalletViewModel.class);
        setupButtons();
        setOnClickListeners();

        palletViewModel.getPalletResponse().observe(getViewLifecycleOwner(), palletResponse -> {
            if (palletResponse != null) {
                Utils.message(requireContext().getString(R.string.toast_message_pallet_created),R.layout.item_customtoastok,getContext());
            }
        });
        palletViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> binding.loadingPanel.setVisibility(isLoading ? View.VISIBLE : View.GONE));

        palletViewModel.getError().observe(getViewLifecycleOwner(), error -> {
            if (error != null) {
                Utils.message(error,R.layout.item_customtoasterror,getContext());
            }
        });
    }

    private void setOnClickListeners() {
        binding.tvScale.setOnClickListener(v -> keyboardInt(binding.tvScale, requireContext().getString(R.string.dialog_scale_input), getContext(), texto -> palletViewModel.setScale(Integer.parseInt(texto))));
        binding.tvPalletOrigin.setOnClickListener(v -> keyboard(binding.tvPalletOrigin, requireContext().getString(R.string.dialog_palleto_input), getContext(), texto -> palletViewModel.setPalletOrigin(texto)));
        binding.tvPalletDestination.setOnClickListener(v -> keyboard(binding.tvPalletDestination, requireContext().getString(R.string.dialog_palletd_input), getContext(), texto -> palletViewModel.setPalletDestination(texto)));
    }

    private void setupButtons() {
        if (buttonProvider != null) {
            Button bt_home = buttonProvider.getButtonHome();
            Button bt_1 = buttonProvider.getButton1();
            Button bt_2 = buttonProvider.getButton2();
            Button bt_3 = buttonProvider.getButton3();
            Button bt_4 = buttonProvider.getButton4();
            Button bt_5 = buttonProvider.getButton5();
            Button bt_6 = buttonProvider.getButton6();
            buttonProvider.getTitulo().setText(requireContext().getString(R.string.title_new_pallet));
            bt_1.setBackgroundResource(R.drawable.i_boton_add);
            bt_2.setVisibility(View.INVISIBLE);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);
            bt_home.setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());
            bt_1.setOnClickListener(view ->  palletViewModel.createPallet());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}