package com.jws.jwsapi.core.gpio;

import static com.jws.jwsapi.core.gpio.GpioConstants.OFF;
import static com.jws.jwsapi.core.gpio.GpioConstants.ON;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.FragmentGpioBinding;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class GpioFragment extends Fragment {

    private FragmentGpioBinding binding;
    private ButtonProvider buttonProvider;
    private MainActivity mainActivity;
    private GpioViewModel gpioViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = FragmentGpioBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();

        initViewModels();

        observeViewModels();

        setupButtons();

    }

    private void initViewModels() {
        gpioViewModel = new ViewModelProvider(this).get(GpioViewModel.class);
    }

    private void observeViewModels() {
        gpioViewModel.getGpioValue1().observe(getViewLifecycleOwner(), value -> updateGpioBackground(binding.input1, value));
        gpioViewModel.getGpioValue2().observe(getViewLifecycleOwner(), value -> updateGpioBackground(binding.input2, value));
        gpioViewModel.getGpioValue3().observe(getViewLifecycleOwner(), value -> updateGpioBackground(binding.input3, value));
        gpioViewModel.getGpioValue4().observe(getViewLifecycleOwner(), value -> updateGpioBackground(binding.input4, value));
        gpioViewModel.getGpioValue5().observe(getViewLifecycleOwner(), value -> updateGpioBackground(binding.input5, value));
        gpioViewModel.getGpioValue6().observe(getViewLifecycleOwner(), value -> updateGpioBackground(binding.input6, value));
        gpioViewModel.getGpioValue7().observe(getViewLifecycleOwner(), value -> updateGpioBackground(binding.input7, value));
        gpioViewModel.getGpioValue8().observe(getViewLifecycleOwner(), value -> updateGpioBackground(binding.input8, value));
    }

    private void updateGpioBackground(TextView textView, Integer value) {
        if (value != null) {
            if (value == ON) {
                textView.setBackgroundResource(R.drawable.square_background_on);
            } else if (value == OFF) {
                textView.setBackgroundResource(R.drawable.square_background_off);
            }
        }
    }

    private void setupButtons() {
        if (buttonProvider != null) {
            buttonProvider.getButton1().setVisibility(View.INVISIBLE);
            buttonProvider.getButton2().setVisibility(View.INVISIBLE);
            buttonProvider.getButton3().setVisibility(View.INVISIBLE);
            buttonProvider.getButton4().setVisibility(View.INVISIBLE);
            buttonProvider.getButton5().setVisibility(View.INVISIBLE);
            buttonProvider.getButton6().setVisibility(View.INVISIBLE);
            buttonProvider.getButtonHome().setOnClickListener(v -> mainActivity.mainClass.openFragmentPrincipal());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        gpioViewModel.startPolling();
    }

}