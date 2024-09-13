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
import com.jws.jwsapi.R;
import com.jws.jwsapi.general.container.HomeButtonProviderSingleton;
import com.jws.jwsapi.general.container.HomeButtonProvider;
import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.general.user.UserManager;
import com.jws.jwsapi.databinding.HomeFragmentBinding;
import com.jws.jwsapi.pallet.Pallet;
import com.jws.jwsapi.pallet.PalletCreateFragment;
import com.jws.jwsapi.pallet.PalletFragment;
import com.jws.jwsapi.general.utils.ToastHelper;
import com.jws.jwsapi.shared.WeighRepository;
import com.jws.jwsapi.weighing.WeighingFragment;
import com.jws.jwsapi.weighing.WeighingResponse;
import com.jws.jwsapi.weighing.WeighingViewModel;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment{

    private HomeFragmentBinding binding;
    private HomeButtonProvider buttonProvider;
    MainActivity mainActivity;
    WeighingViewModel weighingViewModel;
    HomeViewModel homeViewModel;

    @Inject
    UserManager userManager;
    @Inject
    WeighRepository repository;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        buttonProvider = HomeButtonProviderSingleton.getInstance().getButtonProvider();
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        weighingViewModel = new ViewModelProvider(this).get(WeighingViewModel.class);
        HomeViewModelFactory factory = new HomeViewModelFactory(mainActivity.mainClass.bza, repository);
        homeViewModel = new ViewModelProvider(requireActivity(), factory).get(HomeViewModel.class);

        setupButtons();

        observeViewModels();

    }

    private void observeViewModels() {
        weighingViewModel.getCurrentPallet().observe(getViewLifecycleOwner(), this::handlePalletUpdate);

        weighingViewModel.getLoading().observe(getViewLifecycleOwner(), this::handleLoadingUpdate);

        weighingViewModel.getError().observe(getViewLifecycleOwner(), this::messageError);

        weighingViewModel.getErrorRequest().observe(getViewLifecycleOwner(), this::messageError);

        weighingViewModel.getWeighingResponse().observe(getViewLifecycleOwner(), this::handleWeighingResponse);

        homeViewModel.getNet().observe(getViewLifecycleOwner(), net -> handleWeighUpdate(net, binding.tvNet));

        homeViewModel.getGross().observe(getViewLifecycleOwner(), gross -> handleWeighUpdate(gross, binding.tvGross));

        homeViewModel.getUnit().observe(getViewLifecycleOwner(), unit -> {
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
            setupButton(buttonProvider.getButton1(), R.string.button_text_1, null);
            setupButton(buttonProvider.getButton2(), R.string.button_text_2,
                    v -> weighingViewModel.createWeighing("10", "5", "5","kg"));
            setupButton(buttonProvider.getButton3(), R.string.button_text_3,
                    v -> mainActivity.mainClass.openFragment(new PalletFragment()));
            setupButton(buttonProvider.getButton4(), R.string.button_text_4,
                    v -> mainActivity.mainClass.openFragment(new WeighingFragment()));
            setupButton(buttonProvider.getButton5(), R.string.button_text_5,
                    v -> mainActivity.mainClass.openFragment(new PalletCreateFragment()));
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