package com.jws.jwsapi.general.pallet;

import static com.jws.jwsapi.general.dialog.DialogUtil.keyboard;
import static com.jws.jwsapi.general.dialog.DialogUtil.keyboardInt;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jws.jwsapi.R;
import com.jws.jwsapi.base.ui.activities.MainActivity;
import com.jws.jwsapi.databinding.FragmentPalletCreateBinding;
import com.jws.jwsapi.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PalletCreateFragment extends Fragment {

    private PalletViewModel palletViewModel;
    private FragmentPalletCreateBinding binding;
    private ButtonProvider buttonProvider;
    MainActivity mainActivity;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = FragmentPalletCreateBinding.inflate(inflater, container, false);
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
        binding.btNewPaller.setOnClickListener(v -> palletViewModel.createPallet());
    }

    private void setupButtons() {
        if (buttonProvider != null) {
            buttonProvider.getButtonHome().setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());
            buttonProvider.getButton1().setVisibility(View.INVISIBLE);
            buttonProvider.getButton2().setVisibility(View.INVISIBLE);
            buttonProvider.getButton3().setVisibility(View.INVISIBLE);
            buttonProvider.getButton4().setVisibility(View.INVISIBLE);
            buttonProvider.getButton5().setVisibility(View.INVISIBLE);
            buttonProvider.getButton6().setVisibility(View.INVISIBLE);
            buttonProvider.getTitulo().setText(requireContext().getString(R.string.title_new_pallet));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}