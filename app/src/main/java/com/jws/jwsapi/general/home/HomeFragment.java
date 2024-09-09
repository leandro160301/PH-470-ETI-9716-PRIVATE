package com.jws.jwsapi.general.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
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
import com.jws.jwsapi.general.pallet.PalletService;
import com.jws.jwsapi.general.weighing.Weighing;
import com.jws.jwsapi.general.weighing.WeighingViewModel;

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
    PalletService palletService;

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
        HomeViewModelFactory factory = new HomeViewModelFactory(palletService, mainActivity.mainClass.bza);
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
            bt2.setOnClickListener(v -> newWeighing());
            bt3.setOnClickListener(view -> mainActivity.mainClass.openFragment(new PalletFragment()));
            bt4.setOnClickListener(view -> mainActivity.mainClass.openFragment(new FormFragmentRecetas()));
            bt5.setOnClickListener(view -> mainActivity.mainClass.openFragment(new PalletCreateFragment()));
        }
    }

    private void newWeighing() {
        Weighing weighing= new Weighing();
        Pallet pallet= weighingViewModel.getCurrentPallet().getValue();
        if(pallet!=null) {
            weighing.setCode(pallet.getCode());
            /*weighing.setGross(homeViewModel.getGross().getValue());
            weighing.setTare(homeViewModel.getTare().getValue());
            weighing.setNet(homeViewModel.getNet().getValue());*/
            weighing.setGross("10.00");
            weighing.setTare("8.00");
            weighing.setNet("2.00");
            weighing.setName(pallet.getName());
            weighing.setOperator(usersManager.getUsuarioActual());
            weighing.setIdPallet(pallet.getId());
            weighing.setScaleNumber(pallet.getScaleNumber());
            weighing.setQuantity(pallet.getQuantity());
            weighing.setSerialNumber(pallet.getSerialNumber());
            weighingViewModel.createWeighing(weighing);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}