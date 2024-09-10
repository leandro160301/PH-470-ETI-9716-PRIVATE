package com.jws.jwsapi.general.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.jws.jwsapi.R;
import com.jws.jwsapi.base.containers.clases.ButtonProviderSingletonPrincipal;
import com.jws.jwsapi.base.containers.interfaces.ButtonProvider_Principal;
import com.jws.jwsapi.base.ui.activities.MainActivity;
import com.jws.jwsapi.common.users.UsersManager;
import com.jws.jwsapi.databinding.HomeFragmentBinding;
import com.jws.jwsapi.general.formulador.ui.fragment.FormFragmentRecetas;
import com.jws.jwsapi.general.pallet.Pallet;
import com.jws.jwsapi.general.pallet.PalletCreateFragment;
import com.jws.jwsapi.general.pallet.PalletFragment;
import com.jws.jwsapi.general.weighing.WeighingViewModel;
import com.jws.jwsapi.utils.Utils;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment{

    private HomeFragmentBinding binding;
    private ButtonProvider_Principal buttonProvider;
    MainActivity mainActivity;
    WeighingViewModel weighingViewModel;
    HomeViewModel homeViewModel;

    @Inject
    UsersManager usersManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingletonPrincipal.getInstance().getButtonProvider();
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        weighingViewModel = new ViewModelProvider(this).get(WeighingViewModel.class);
        HomeViewModelFactory factory = new HomeViewModelFactory(mainActivity.mainClass.bza);
        homeViewModel = new ViewModelProvider(this, factory).get(HomeViewModel.class);

        setupButtons();

        observeViewModels();

    }

    private void observeViewModels() {
        weighingViewModel.getCurrentPallet().observe(getViewLifecycleOwner(), pallet -> {
            if (pallet!=null) {
                updateUI(pallet);
            }
        });

        weighingViewModel.getError().observe(getViewLifecycleOwner(), this::messageError);

        weighingViewModel.getErrorRequest().observe(getViewLifecycleOwner(), this::messageError);

        weighingViewModel.getWeighingResponse().observe(getViewLifecycleOwner(), weighingResponse -> {
            if(weighingResponse!=null){
                if(weighingResponse.getStatus()){
                    Utils.message(requireContext().getString(R.string.toast_message_weighing_created),R.layout.item_customtoastok,getContext());
                }else{
                    Utils.message(weighingResponse.getError(),R.layout.item_customtoasterror,getContext());
                }
            }
        });

        homeViewModel.getNet().observe(getViewLifecycleOwner(), net -> {
            if(net!=null){
                binding.tvNet.setText(net);
            }
        });

        homeViewModel.getGross().observe(getViewLifecycleOwner(), gross -> {
            if(gross!=null){
                binding.tvGross.setText(gross);
            }
        });

        homeViewModel.getUnit().observe(getViewLifecycleOwner(), unit -> {
            binding.tvTotalNetUnit.setText(unit);
            binding.tvGrossUnit.setText(unit);
            binding.tvNetUnit.setText(unit);
        });
    }

    private void messageError(String error) {
        if(error !=null){
            Utils.message(error,R.layout.item_customtoasterror,getContext());
        }
    }

    private void updateUI(Pallet pallet) {
        binding.tvCantidad.setText(String.valueOf(pallet.getQuantity()));
        binding.tvDone.setText(String.valueOf(pallet.getDone()));
        binding.tvProduct.setText(pallet.getName());
        binding.tvPalletOrigin.setText(pallet.getOriginPallet());
        binding.tvPalletDestination.setText(pallet.getDestinationPallet());
        binding.tvScale.setText(String.valueOf(pallet.getScaleNumber()));
        binding.tvTotalNet.setText(pallet.getTotalNet());
    }

    private void setupButtons() {
        if (buttonProvider != null) {
            Button bt1 = buttonProvider.getButton1();
            Button bt2 = buttonProvider.getButton2();
            Button bt3 = buttonProvider.getButton3();
            Button bt4 = buttonProvider.getButton4();
            Button bt5 = buttonProvider.getButton5();
            bt1.setText(requireContext().getString(R.string.button_text_1));
            bt2.setText(requireContext().getString(R.string.button_text_2));
            bt3.setText(requireContext().getString(R.string.button_text_3));
            bt4.setText(requireContext().getString(R.string.button_text_4));
            bt5.setText(requireContext().getString(R.string.button_text_5));
            bt2.setOnClickListener(v -> weighingViewModel.createWeighing());
            bt3.setOnClickListener(view -> mainActivity.mainClass.openFragment(new PalletFragment()));
            bt4.setOnClickListener(view -> mainActivity.mainClass.openFragment(new FormFragmentRecetas()));
            bt5.setOnClickListener(view -> mainActivity.mainClass.openFragment(new PalletCreateFragment()));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}