package com.jws.jwsapi.core.gpio;

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

    private final static int ON = 0;
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
        gpioViewModel.getInputValue1().observe(getViewLifecycleOwner(), value -> updateGpioBackground(binding.input1, value));
        gpioViewModel.getInputValue2().observe(getViewLifecycleOwner(), value -> updateGpioBackground(binding.input2, value));
        gpioViewModel.getInputValue3().observe(getViewLifecycleOwner(), value -> updateGpioBackground(binding.input3, value));
        gpioViewModel.getInputValue4().observe(getViewLifecycleOwner(), value -> updateGpioBackground(binding.input4, value));
        gpioViewModel.getInputValue5().observe(getViewLifecycleOwner(), value -> updateGpioBackground(binding.input5, value));
        gpioViewModel.getInputValue6().observe(getViewLifecycleOwner(), value -> updateGpioBackground(binding.input6, value));
        gpioViewModel.getInputValue7().observe(getViewLifecycleOwner(), value -> updateGpioBackground(binding.input7, value));
        gpioViewModel.getInputValue8().observe(getViewLifecycleOwner(), value -> updateGpioBackground(binding.input8, value));

        /*binding.switch1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gpioViewModel.setOutputValueOn1();
            } else {
                gpioViewModel.setOutputValueOff1();
            }
        });

        binding.switch2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gpioViewModel.setOutputValueOn2();
            } else {
                gpioViewModel.setOutputValueOff2();
            }
        });

        binding.switch3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gpioViewModel.setOutputValueOff3();
            } else {
                gpioViewModel.setOutputValueOn3();
            }
        });

        binding.switch4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gpioViewModel.setOutputValueOff4();
            } else {
                gpioViewModel.setOutputValueOn4();
            }
        });

        binding.switch5.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gpioViewModel.setOutputValueOff5();
            } else {
                gpioViewModel.setOutputValueOn5();
            }
        });

        binding.switch6.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gpioViewModel.setOutputValueOff6();
            } else {
                gpioViewModel.setOutputValueOn6();
            }
        });

        binding.switch7.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gpioViewModel.setOutputValueOff7();
            } else {
                gpioViewModel.setOutputValueOn7();
            }
        });

        binding.switch8.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                gpioViewModel.setOutputValueOff8();
            } else {
                gpioViewModel.setOutputValueOn8();
            }
        });*/

    }

    private void updateGpioBackground(TextView textView, Integer value) {
        if (value != null && value == ON) {
            textView.setBackgroundResource(R.drawable.square_background_on);
        } else {
            textView.setBackgroundResource(R.drawable.square_background_off);
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