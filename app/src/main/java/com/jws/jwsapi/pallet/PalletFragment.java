package com.jws.jwsapi.pallet;

import static com.jws.jwsapi.general.dialog.DialogUtil.dialogText;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.FragmentPalletBinding;
import com.jws.jwsapi.general.utils.ToastHelper;
import com.jws.jwsapi.shared.WeighRepository;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PalletFragment extends Fragment implements PalletButtonClickListener {

    private PalletAdapter palletAdapter;
    private FragmentPalletBinding binding;
    private PalletViewModel palletViewModel;
    private ButtonProvider buttonProvider;
    MainActivity mainActivity;
    @Inject
    WeighRepository repository;

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

        tabLayoutListener();

        setupButtons();

        setupRecycler();

        observeViewModel();
    }

    private void observeViewModel() {
        palletViewModel.getPallets().observe(getViewLifecycleOwner(), this::handlePalletsUpdate);

        palletViewModel.getPalletCloseResponse().observe(getViewLifecycleOwner(), palletCloseResponse -> {
            if (palletCloseResponse == null) return;
            int toastLayout = palletCloseResponse.getStatus() ? R.layout.item_customtoastok : R.layout.item_customtoasterror;
            String message = palletCloseResponse.getStatus() ? requireContext().getString(R.string.toast_message_pallet_closed) : palletCloseResponse.getError();
            ToastHelper.message(message, toastLayout, getContext());
        });

        palletViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading!=null){
                binding.loadingPanel.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });

        palletViewModel.getError().observe(getViewLifecycleOwner(), error
                -> showMessage(error != null, error, R.layout.item_customtoasterror));
    }

    private void tabLayoutListener() {
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                int position = tab.getPosition();
                switch (position) {
                    case 0:
                        palletViewModel.setupPalletOpen();
                        break;
                    case 1:
                        palletViewModel.setupPalletClose();
                        break;
                }
            }
            @Override
            public void onTabUnselected(@NonNull TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(@NonNull TabLayout.Tab tab) {}
        });
    }


    private void setupRecycler() {
        String unit = repository.getUnit().getValue();
        if(unit!=null) {
            binding.recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            palletAdapter = new PalletAdapter(new ArrayList<>(),this,unit);
            binding.recycler.setAdapter(palletAdapter);
        }

    }

    private void showMessage(boolean message, String toastMessage, int layout) {
        if (message) {
            ToastHelper.message(toastMessage, layout,getContext());
        }
    }

    private void handlePalletsUpdate(List<Pallet> pallets) {
        if (pallets !=null) {
            palletAdapter.updateData(pallets);
        }
    }

    private void setupButtons() {
        if (buttonProvider != null) {
            buttonProvider.getButtonHome().setOnClickListener(view -> openHome());
            buttonProvider.getButton1().setVisibility(View.INVISIBLE);
            buttonProvider.getButton2().setVisibility(View.INVISIBLE);
            buttonProvider.getButton3().setVisibility(View.INVISIBLE);
            buttonProvider.getButton4().setVisibility(View.INVISIBLE);
            buttonProvider.getButton5().setVisibility(View.INVISIBLE);
            buttonProvider.getButton6().setVisibility(View.INVISIBLE);
            buttonProvider.getTitulo().setText(requireContext().getString(R.string.title_pallets));
        }
    }

    private void openHome() {
        mainActivity.mainClass.openFragmentPrincipal();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void deletePallet(Pallet pallet) {
        dialogText(getContext(), requireContext().getString(R.string.dialog_delete_pallet), requireContext().getString(R.string.dialog_button_delete_pallet), ()
                -> palletViewModel.deletePallet(pallet.getSerialNumber()));
    }

    @Override
    public void selectPallet(Pallet pallet) {
        palletViewModel.setCurrentPallet(pallet);
        openHome();
    }

    @Override
    public void closePallet(Pallet pallet) {
        dialogText(getContext(), requireContext().getString(R.string.dialog_close_pallet), requireContext().getString(R.string.dialog_button_close_pallet), ()
                -> palletViewModel.closePallet(pallet.getSerialNumber()));
    }
}