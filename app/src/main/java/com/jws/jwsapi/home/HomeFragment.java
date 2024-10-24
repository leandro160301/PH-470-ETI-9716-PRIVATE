package com.jws.jwsapi.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.caliber.CaliberFragment;
import com.jws.jwsapi.core.container.ContainerButtonProvider;
import com.jws.jwsapi.core.container.ContainerButtonProviderSingleton;
import com.jws.jwsapi.core.gpio.GpioHighListener;
import com.jws.jwsapi.core.gpio.GpioManager;
import com.jws.jwsapi.databinding.HomeFragmentBinding;
import com.jws.jwsapi.productionline.ProductionLineFragment;
import com.jws.jwsapi.productionline.ProductionLineViewModel;
import com.jws.jwsapi.scale.ScaleViewModel;
import com.jws.jwsapi.shared.WeighRepository;
import com.jws.jwsapi.utils.ToastHelper;
import com.jws.jwsapi.utils.Utils;
import com.jws.jwsapi.weighing.WeighingFragment;
import com.jws.jwsapi.weighing.WeighingPrintAction;
import com.jws.jwsapi.weighing.WeighingViewModel;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment implements GpioHighListener, WeighingPrintAction {

    @Inject
    WeighRepository repository;
    @Inject
    HomeService homeService;
    @Inject
    ScaleViewModel.Factory viewModelFactory;
    @Inject
    GpioManager gpioManager;
    private HomeFragmentBinding binding;
    private ContainerButtonProvider buttonProvider;
    private MainActivity mainActivity;
    private WeighingViewModel weighingViewModel;
    private ScaleViewModel serviceScaleViewModel;
    private ProductionLineViewModel productionLineViewModel;
    private boolean isScaleMode = false;

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

        binding.lnLine.setOnClickListener(v -> productionLineViewModel.changeCurrentLine());

    }

    @SuppressWarnings("unchecked")
    private void initViewModels() {
        weighingViewModel = new ViewModelProvider(this).get(WeighingViewModel.class);
        productionLineViewModel = new ViewModelProvider(this).get(ProductionLineViewModel.class);
        serviceScaleViewModel = new ViewModelProvider(requireActivity(), new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends androidx.lifecycle.ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) viewModelFactory.create(mainActivity.mainClass.bza);
            }
        }).get(ScaleViewModel.class);

        gpioManager.setHighListener(this);
    }

    private void observeViewModels() {
        repository.getNetStr().observe(getViewLifecycleOwner(), net -> updateWeightDisplay(net, binding.tvNet));

        repository.getGrossStr().observe(getViewLifecycleOwner(), gross -> updateWeightDisplay(gross, binding.tvGross));

        repository.getStable().observe(getViewLifecycleOwner(), stable -> binding.imEstable.setVisibility(stable ? View.VISIBLE : View.INVISIBLE));

        repository.getTare().observe(getViewLifecycleOwner(), tare -> binding.imTare.setVisibility(Utils.isNumeric(tare) && Float.parseFloat(tare) > 0 ? View.VISIBLE : View.INVISIBLE));

        repository.getUnit().observe(getViewLifecycleOwner(), unit -> {
            binding.tvGrossUnit.setText(unit);
            binding.tvNetUnit.setText(unit);
        });

        productionLineViewModel.getProduct().observe(getViewLifecycleOwner(), product ->
                animateAndSetText(binding.tvProduct, binding.shimmerViewProduct, product));
        productionLineViewModel.getBatch().observe(getViewLifecycleOwner(), batch ->
                animateAndSetText(binding.tvBatch, binding.shimmerViewBatch, batch));
        productionLineViewModel.getDestination().observe(getViewLifecycleOwner(), destination ->
                animateAndSetText(binding.tvDestination, binding.shimmerViewDestination, destination));
        productionLineViewModel.getExpirationDate().observe(getViewLifecycleOwner(), expiration ->
                animateAndSetText(binding.tvExpirationDate, binding.shimmerViewExpirationDate, expiration));
        productionLineViewModel.getCaliber().observe(getViewLifecycleOwner(), caliber ->
                animateAndSetText(binding.tvCaliber, binding.shimmerLine, caliber));
        productionLineViewModel.getLineNumber().observe(getViewLifecycleOwner(), number ->
                animateAndSetText(binding.tvLine, binding.shimmerLine, String.valueOf(number)));

        productionLineViewModel.getState().observe(getViewLifecycleOwner(), state -> {
            switch (state) {
                case INIT:
                    setupLinearState(binding.lnState1, binding.lnState2, binding.lnState3, binding.lnState4);
                    break;
                case BOX:
                    setupLinearState(null, binding.lnState2, binding.lnState3, binding.lnState4);
                    binding.lnState1.setBackgroundResource(R.drawable.botoneraprincipalverde);
                    break;
                case PARTS:
                    setupLinearState(binding.lnState1, null, binding.lnState3, binding.lnState4);
                    binding.lnState2.setBackgroundResource(R.drawable.botoneraprincipalverde);
                    break;
                case ICE:
                    setupLinearState(binding.lnState1, binding.lnState2, null, binding.lnState4);
                    binding.lnState3.setBackgroundResource(R.drawable.botoneraprincipalverde);
                    break;
                case TOP:
                    setupLinearState(binding.lnState1, binding.lnState2, binding.lnState3, null);
                    binding.lnState4.setBackgroundResource(R.drawable.botoneraprincipalverde);
                    break;
            }
        });

        handleObserveWeighing();
    }

    private void handleObserveWeighing() {
        weighingViewModel.getError().observe(getViewLifecycleOwner(), this::messageError);
        weighingViewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                ToastHelper.message(message, R.layout.item_customtoastok, requireContext());
            }
        });
        productionLineViewModel.getError().observe(getViewLifecycleOwner(), this::messageError);
        productionLineViewModel.getMessage().observe(getViewLifecycleOwner(), message -> {
            if (message != null) {
                ToastHelper.message(message, R.layout.item_customtoastok, requireContext());
            }
        });
    }

    private void setupLinearState(LinearLayout ln1, LinearLayout ln2, LinearLayout ln3, LinearLayout ln4) {
        if (ln1 != null)
            ln1.setBackgroundResource(R.drawable.campollenarclickeableceropadding_selector);
        if (ln2 != null)
            ln2.setBackgroundResource(R.drawable.campollenarclickeableceropadding_selector);
        if (ln3 != null)
            ln3.setBackgroundResource(R.drawable.campollenarclickeableceropadding_selector);
        if (ln4 != null)
            ln4.setBackgroundResource(R.drawable.campollenarclickeableceropadding_selector);
    }

    private void showMessage(String error, int layout) {
        ToastHelper.message(error, layout, getContext());
    }

    private void updateWeightDisplay(String weight, TextView textView) {
        if (weight != null) {
            textView.setText(weight);
        }
    }

    private void messageError(String error) {
        if (error != null) showMessage(error, R.layout.item_customtoasterror);
    }

    private void changeMode() {
        if (isScaleMode) {
            setupButtons();
        } else {
            setupButtonsScale();
        }
        isScaleMode = !isScaleMode;
    }

    private void setupButtonsScale() {
        if (buttonProvider != null) {
            binding.lnFondolayout.setBackgroundResource(R.drawable.boton_selector_balanza_seleccionado);
            setupButton(buttonProvider.getButton1(), R.string.button_scale_zero,
                    view -> repository.setZero(), View.VISIBLE);
            setupButton(buttonProvider.getButton2(), R.string.button_scale_tare,
                    view -> repository.setTare(), View.VISIBLE);
            setupButton(buttonProvider.getButton3(), R.string.button_scale_print,
                    view -> homeService.printMemory(mainActivity, serviceScaleViewModel.getScaleService().getSerialPort(repository.getScaleNumber())), View.VISIBLE);
            setupButton(buttonProvider.getButton4(), null, null, View.INVISIBLE);
            setupButton(buttonProvider.getButton5(), null, null, View.INVISIBLE);
        }
    }

    private void setupButtons() {
        if (buttonProvider != null) {
            setupButton(buttonProvider.getButton1(), R.string.button_text_2,
                    v -> mainActivity.mainClass.openFragment(new ProductionLineFragment()), View.VISIBLE);
            setupButton(buttonProvider.getButton2(), R.string.button_text_3,
                    v -> mainActivity.mainClass.openFragment(new CaliberFragment()), View.VISIBLE);
            setupButton(buttonProvider.getButton3(), R.string.button_text_4,
                    v -> mainActivity.mainClass.openFragment(new WeighingFragment()), View.VISIBLE);
            setupButton(buttonProvider.getButton4(), R.string.button_text_6, v -> tareButton(), View.VISIBLE);
            setupButton(buttonProvider.getButton5(), R.string.button_text_5,
                    v -> printButton(), View.VISIBLE);
            binding.lnFondolayout.setBackgroundResource(R.drawable.boton_selector_balanza);
        }
    }

    private void setupButton(Button button, Integer textResId, View.OnClickListener onClickListener, Integer visibility) {
        if (textResId != null) button.setText(requireContext().getString(textResId));
        if (onClickListener != null) button.setOnClickListener(onClickListener);
        if (visibility != null) button.setVisibility(visibility);
    }

    private void animateAndSetText(TextView textView, ShimmerFrameLayout shimmerLayout, String newText) {
        shimmerLayout.startShimmer();
        textView.postDelayed(() -> {
            shimmerLayout.stopShimmer();
            textView.setText(newText);

            if (getActivity() != null && textView == binding.tvLine) {
                if (Utils.isNumeric(newText)) {
                    if (Integer.parseInt(newText) == 1) {
                        binding.lnLine.setBackgroundResource(R.drawable.line_1);
                    } else {
                        binding.lnLine.setBackgroundResource(R.drawable.line_2);
                    }
                }
            }
        }, 2000);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        productionLineViewModel.stopPolling();
        binding = null;
    }

    @Override
    public void onInputHigh(int input) {
        if (getActivity() == null) return;

        if (input == 0) {
            tareButton();
        }
        if (input == 1) {
            printButton();
        }
        if (input == 2) {
            getActivity().runOnUiThread(() -> productionLineViewModel.changeCurrentLine());
        }

    }

    private void tareButton() {
        getActivity().runOnUiThread(() -> productionLineViewModel.tareButton());
    }

    private void printButton() {
        getActivity().runOnUiThread(() -> weighingViewModel.createWeighing(HomeFragment.this));
    }

    @Override
    public void print() {
        homeService.print(requireContext(), serviceScaleViewModel.getScaleService().getSerialPort(repository.getScaleNumber()));
    }
}