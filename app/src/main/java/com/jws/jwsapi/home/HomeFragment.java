package com.jws.jwsapi.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.HomeFragmentBinding;
import com.jws.jwsapi.core.container.ContainerButtonProvider;
import com.jws.jwsapi.core.container.ContainerButtonProviderSingleton;
import com.jws.jwsapi.core.user.UserManager;
import com.jws.jwsapi.service.ServiceViewModel;
import com.jws.jwsapi.service.ServiceViewModelFactory;
import com.jws.jwsapi.utils.ToastHelper;
import com.jws.jwsapi.utils.Utils;
import com.jws.jwsapi.pallet.Pallet;
import com.jws.jwsapi.pallet.PalletCreateFragment;
import com.jws.jwsapi.pallet.PalletFragment;
import com.jws.jwsapi.shared.WeighRepository;
import com.jws.jwsapi.weighing.WeighingFragment;
import com.jws.jwsapi.weighing.WeighingResponse;
import com.jws.jwsapi.weighing.WeighingViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment{

    private HomeFragmentBinding binding;
    private ContainerButtonProvider buttonProvider;
    MainActivity mainActivity;
    WeighingViewModel weighingViewModel;
    ServiceViewModel serviceViewModel;

    @Inject
    UserManager userManager;
    @Inject
    WeighRepository repository;
    @Inject
    HomeService homeService;



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        buttonProvider = ContainerButtonProviderSingleton.getInstance().getButtonProvider();
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        weighingViewModel = new ViewModelProvider(this).get(WeighingViewModel.class);
        ServiceViewModelFactory factory = new ServiceViewModelFactory(mainActivity.mainClass.bza, repository);
        serviceViewModel = new ViewModelProvider(requireActivity(), factory).get(ServiceViewModel.class);

        setupButtons();

        observeViewModels();

    }

    private void observeViewModels() {
        weighingViewModel.getCurrentPallet().observe(getViewLifecycleOwner(), this::handlePalletUpdate);

        weighingViewModel.getLoading().observe(getViewLifecycleOwner(), this::handleLoadingUpdate);

        weighingViewModel.getError().observe(getViewLifecycleOwner(), this::messageError);

        weighingViewModel.getErrorRequest().observe(getViewLifecycleOwner(), this::messageError);

        weighingViewModel.getWeighingResponse().observe(getViewLifecycleOwner(), this::handleWeighingResponse);

        repository.getNet().observe(getViewLifecycleOwner(), net -> handleWeighUpdate(net, binding.tvNet));

        repository.getGross().observe(getViewLifecycleOwner(), gross -> handleWeighUpdate(gross, binding.tvGross));

        repository.getStable().observe(getViewLifecycleOwner(), stable -> binding.imEstable.setVisibility(stable ? View.VISIBLE : View.INVISIBLE));

        repository.getTare().observe(getViewLifecycleOwner(), tare -> binding.imTare.setVisibility(Utils.isNumeric(tare) && Float.parseFloat(tare) > 0 ? View.VISIBLE : View.INVISIBLE));

        repository.getUnit().observe(getViewLifecycleOwner(), unit -> {
            binding.tvTotalNetUnit.setText(unit);
            binding.tvGrossUnit.setText(unit);
            binding.tvNetUnit.setText(unit);
        });
    }

    private void handleWeighUpdate(String net, TextView binding) {
        if(net !=null){
            binding.setText(net);
        }
    }

    private void handleWeighingResponse(WeighingResponse weighingResponse) {
        if(weighingResponse !=null){
            if(weighingResponse.getStatus()){
                ToastHelper.message(requireContext().getString(R.string.toast_message_weighing_created),R.layout.item_customtoastok,getContext());
                homeService.print(mainActivity, serviceViewModel.getScaleService().getSerialPort(repository.getScaleNumber()));
            }else{
                ToastHelper.message(weighingResponse.getError(),R.layout.item_customtoasterror,getContext());
            }
        }
    }

    private void handleLoadingUpdate(Boolean isLoading) {
        if (isLoading !=null){
            binding.loadingPanel.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void handlePalletUpdate(Pallet pallet) {
        if (pallet !=null) {
            updateUi(pallet);
        }
    }

    private void messageError(String error) {
        if(error !=null){
            ToastHelper.message(error,R.layout.item_customtoasterror,getContext());
        }
    }

    private void updateUi(Pallet pallet) {
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
            setupButton(buttonProvider.getButton1(), R.string.button_text_1,null);
            setupButton(buttonProvider.getButton2(), R.string.button_text_2,
                    v -> btCreateWeighing());
            setupButton(buttonProvider.getButton3(), R.string.button_text_3,
                    v -> mainActivity.mainClass.openFragment(new PalletFragment()));
            setupButton(buttonProvider.getButton4(), R.string.button_text_4,
                    v -> mainActivity.mainClass.openFragment(new WeighingFragment()));
            setupButton(buttonProvider.getButton5(), R.string.button_text_5,
                    v -> mainActivity.mainClass.openFragment(new PalletCreateFragment()));
        }
    }

    private void btCreateWeighing() {
        String unit = repository.getUnit().getValue();
        String tare = repository.getTare().getValue();
        String net = repository.getNet().getValue();
        String gross = repository.getGross().getValue();
        if(unit!=null&&tare!=null&&net!=null&&gross!=null) {
            weighingViewModel.createWeighing(gross, net, tare,unit);
        }
    }

    private void setupButton(Button button, int textResId, View.OnClickListener onClickListener) {
        button.setText(requireContext().getString(textResId));
        if (onClickListener != null) {
            button.setOnClickListener(onClickListener);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}