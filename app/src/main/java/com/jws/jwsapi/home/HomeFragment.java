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
import com.jws.jwsapi.core.container.ContainerButtonProvider;
import com.jws.jwsapi.core.container.ContainerButtonProviderSingleton;
import com.jws.jwsapi.core.user.UserManager;
import com.jws.jwsapi.databinding.HomeFragmentBinding;
import com.jws.jwsapi.pallet.Pallet;
import com.jws.jwsapi.pallet.PalletCreateFragment;
import com.jws.jwsapi.pallet.PalletFragment;
import com.jws.jwsapi.pallet.PalletViewModel;
import com.jws.jwsapi.service.ServiceViewModel;
import com.jws.jwsapi.service.ServiceViewModelFactory;
import com.jws.jwsapi.shared.PalletRepository;
import com.jws.jwsapi.shared.WeighRepository;
import com.jws.jwsapi.utils.ToastHelper;
import com.jws.jwsapi.utils.Utils;
import com.jws.jwsapi.weighing.WeighingFragment;
import com.jws.jwsapi.weighing.WeighingResponse;
import com.jws.jwsapi.weighing.WeighingViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    public static int OPERATION_BUTTONS = 0;
    public static int SCALE_BUTTONS = 1;
    @Inject
    UserManager userManager;
    @Inject
    WeighRepository repository;
    @Inject
    HomeService homeService;
    @Inject
    PalletRepository palletRepository;
    private HomeFragmentBinding binding;
    private ContainerButtonProvider buttonProvider;
    private MainActivity mainActivity;
    private WeighingViewModel weighingViewModel;
    private ServiceViewModel serviceViewModel;
    private PalletViewModel palletViewModel;
    private int buttons = OPERATION_BUTTONS;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        buttonProvider = ContainerButtonProviderSingleton.getInstance().getButtonProvider();
        binding = HomeFragmentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();

        initViewModels();

        setupButtons();

        observeViewModels();

        binding.lnFondolayout.setOnClickListener(v -> changeMode());

    }

    private void initViewModels() {
        weighingViewModel = new ViewModelProvider(this).get(WeighingViewModel.class);
        ServiceViewModelFactory factory = new ServiceViewModelFactory(mainActivity.mainClass.bza, repository);
        serviceViewModel = new ViewModelProvider(requireActivity(), factory).get(ServiceViewModel.class);
        palletViewModel = new ViewModelProvider(this).get(PalletViewModel.class);
    }

    private void observeViewModels() {
        repository.getNet().observe(getViewLifecycleOwner(), net -> handleWeighUpdate(net, binding.tvNet));

        repository.getGross().observe(getViewLifecycleOwner(), gross -> handleWeighUpdate(gross, binding.tvGross));

        repository.getStable().observe(getViewLifecycleOwner(), stable -> binding.imEstable.setVisibility(stable ? View.VISIBLE : View.INVISIBLE));

        repository.getTare().observe(getViewLifecycleOwner(), tare -> binding.imTare.setVisibility(Utils.isNumeric(tare) && Float.parseFloat(tare) > 0 ? View.VISIBLE : View.INVISIBLE));

        repository.getUnit().observe(getViewLifecycleOwner(), unit -> {
            binding.tvTotalNetUnit.setText(unit);
            binding.tvGrossUnit.setText(unit);
            binding.tvNetUnit.setText(unit);
        });

        handleObservePallet();

        handleObserveWeighing();
    }

    private void handleObserveWeighing() {
        weighingViewModel.getCurrentPallet().observe(getViewLifecycleOwner(), this::updateUi);

        weighingViewModel.getLoading().observe(getViewLifecycleOwner(), this::handleLoadingUpdate);

        weighingViewModel.getError().observe(getViewLifecycleOwner(), this::messageError);

        weighingViewModel.getErrorRequest().observe(getViewLifecycleOwner(), this::messageError);

        weighingViewModel.getWeighingResponse().observe(getViewLifecycleOwner(), this::handleWeighingResponse);

    }

    private void handleObservePallet() {
        palletViewModel.getPalletCloseResponse().observe(getViewLifecycleOwner(), palletCloseResponse -> {
            if (palletCloseResponse == null) return;
            int toastLayout = palletCloseResponse.getStatus() ? R.layout.item_customtoastok : R.layout.item_customtoasterror;
            String message = palletCloseResponse.getStatus() ? requireContext().getString(R.string.toast_message_pallet_closed) : palletCloseResponse.getError();
            showMessage(message, toastLayout);
        });

        palletViewModel.getLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.loadingPanel.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }
        });
        palletViewModel.getError().observe(getViewLifecycleOwner(), this::handleErrorPallet);
    }

    private void handleErrorPallet(String error) {
        if (error != null) {
            showMessage(error, R.layout.item_customtoasterror);
        }
    }

    private void showMessage(String error, int layout) {
        ToastHelper.message(error, layout, getContext());
    }

    private void handleWeighUpdate(String net, TextView textView) {
        if (net != null) {
            textView.setText(net);
        }
    }

    private void handleWeighingResponse(WeighingResponse weighingResponse) {
        if (weighingResponse != null) {
            if (weighingResponse.getStatus()) {
                showMessage(requireContext().getString(R.string.toast_message_weighing_created), R.layout.item_customtoastok);
                homeService.print(mainActivity, serviceViewModel.getScaleService().getSerialPort(repository.getScaleNumber()));
                mainActivity.mainClass.bza.setTaraDigital(repository.getScaleNumber(), mainActivity.mainClass.bza.getBruto(repository.getScaleNumber()));
            } else {
                showMessage(weighingResponse.getError(), R.layout.item_customtoasterror);
            }
        }
    }

    private void handleLoadingUpdate(Boolean isLoading) {
        if (isLoading != null) {
            binding.loadingPanel.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        }
    }

    private void messageError(String error) {
        if (error != null) {
            showMessage(error, R.layout.item_customtoasterror);
        }
    }

    private void changeMode() {
        if (buttons == OPERATION_BUTTONS) {
            buttons = SCALE_BUTTONS;
            setupButtonsScale();
        } else {
            buttons = OPERATION_BUTTONS;
            setupButtons();
        }
    }

    private void setupButtonsScale() {
        if (buttonProvider != null) {
            binding.lnFondolayout.setBackgroundResource(R.drawable.boton_selector_balanza_seleccionado);
            setupButton(buttonProvider.getButton1(), R.string.button_scale_zero,
                    view -> mainActivity.mainClass.bza.setCero(repository.getScaleNumber()), View.VISIBLE);
            setupButton(buttonProvider.getButton2(), R.string.button_scale_tare,
                    view -> mainActivity.mainClass.bza.setTaraDigital(repository.getScaleNumber(), mainActivity.mainClass.bza.getBruto(repository.getScaleNumber())), View.VISIBLE);
            setupButton(buttonProvider.getButton3(), R.string.button_scale_print,
                    view -> homeService.printMemory(mainActivity, serviceViewModel.getScaleService().getSerialPort(repository.getScaleNumber())), View.VISIBLE);
            setupButton(buttonProvider.getButton4(), null, null, View.INVISIBLE);
            setupButton(buttonProvider.getButton5(), null, null, View.INVISIBLE);
        }
    }

    private void setupButtons() {
        if (buttonProvider != null) {
            setupButton(buttonProvider.getButton1(), R.string.button_text_2,
                    v -> createWeighing(), View.VISIBLE);
            setupButton(buttonProvider.getButton2(), R.string.button_text_3,
                    v -> mainActivity.mainClass.openFragment(new PalletFragment()), View.VISIBLE);
            setupButton(buttonProvider.getButton3(), R.string.button_text_4,
                    v -> mainActivity.mainClass.openFragment(new WeighingFragment()), View.VISIBLE);
            setupButton(buttonProvider.getButton4(), R.string.button_text_6, v -> closePallet(), View.VISIBLE);
            setupButton(buttonProvider.getButton5(), R.string.button_text_5,
                    v -> mainActivity.mainClass.openFragment(new PalletCreateFragment()), View.VISIBLE);
            binding.lnFondolayout.setBackgroundResource(R.drawable.boton_selector_balanza);
        }
    }


    private void updateUi(Pallet pallet) {
        binding.tvCantidad.setText(pallet != null ? String.valueOf(pallet.getQuantity()) : "");
        binding.tvDone.setText(pallet != null ? String.valueOf(pallet.getDone()) : "");
        binding.tvProduct.setText(pallet != null ? pallet.getName() : "");
        binding.tvPalletOrigin.setText(pallet != null ? pallet.getOriginPallet() : "");
        binding.tvPalletDestination.setText(pallet != null ? pallet.getDestinationPallet() : "");
        binding.tvScale.setText(pallet != null ? String.valueOf(pallet.getScaleNumber()) : "");
        binding.tvTotalNet.setText(pallet != null ? pallet.getTotalNet() : "");
    }

    private void closePallet() {
        Pallet currentPallet = palletRepository.getCurrentPallet().getValue();
        if (currentPallet != null) {
            palletViewModel.closePallet(currentPallet.getSerialNumber());
        } else {
            messageError(getString(R.string.toast_error_close_pallet));
        }

    }

    private void createWeighing() {
        String unit = repository.getUnit().getValue();
        String tare = repository.getTare().getValue();
        String net = repository.getNet().getValue();
        String gross = repository.getGross().getValue();
        if (unit != null && tare != null && net != null && gross != null) {
            weighingViewModel.createWeighing(gross, net, tare, unit);
        }
    }

    private void setupButton(Button button, Integer textResId, View.OnClickListener onClickListener, Integer visibility) {
        if (textResId != null) button.setText(requireContext().getString(textResId));
        if (onClickListener != null) button.setOnClickListener(onClickListener);
        if (visibility != null) button.setVisibility(visibility);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}