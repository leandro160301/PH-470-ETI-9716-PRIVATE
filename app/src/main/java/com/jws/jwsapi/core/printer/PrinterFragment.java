package com.jws.jwsapi.core.printer;

import static com.jws.jwsapi.dialog.DialogUtil.keyboardIpAdress;
import static com.jws.jwsapi.utils.SpinnerHelper.setupSpinner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.core.label.LabelManager;
import com.jws.jwsapi.databinding.StandarImpresorasBinding;
import com.jws.jwsapi.shared.UserRepository;
import com.jws.jwsapi.shared.WeighRepository;
import com.jws.jwsapi.utils.NetworkUtils;
import com.jws.jwsapi.utils.ToastHelper;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.util.Arrays;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PrinterFragment extends Fragment {

    @Inject
    PrinterPreferences printerPreferences;
    @Inject
    LabelManager labelManager;
    @Inject
    UserRepository userRepository;
    @Inject
    WeighRepository weighRepository;
    MainActivity mainActivity;
    PrinterManager printerManager;
    StandarImpresorasBinding binding;
    private ButtonProvider buttonProvider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = StandarImpresorasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        setupButtons();

        initPrinter();

        initTextView();

        initSpinner();

    }

    private void initPrinter() {
        printerManager = new PrinterManager(getContext(), mainActivity, userRepository, printerPreferences, labelManager, weighRepository);
    }

    private void initTextView() {
        binding.tvPrinterIp.setText(printerPreferences.getIp());
        binding.tvPrinterIp.setOnClickListener(v -> keyboardIpAdress(binding.tvPrinterIp, "Ingrese IP de Impresora", requireContext(), this::setupIpHandler));
    }

    private void initSpinner() {
        setupSpinner(binding.spPrinter, requireContext(), Arrays.asList(getResources().getStringArray(R.array.ImpresoraModo)));
        binding.spPrinter.setSelection(printerPreferences.getMode());
        binding.spPrinter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i != 2) {
                    printerPreferences.setMode(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setupIpHandler(String ip) {
        if (NetworkUtils.isValidIp(ip)) {
            printerPreferences.setIp(ip);
        } else {
            ToastHelper.message(getString(R.string.error_ip_adress_invalid), R.layout.item_customtoasterror, requireContext());
            binding.tvPrinterIp.setText("");
        }
    }

    private void setupButtons() {
        if (buttonProvider != null) {
            buttonProvider.getTitle().setText(R.string.title_fragment_printer);

            buttonProvider.getButton1().setVisibility(View.INVISIBLE);
            buttonProvider.getButton2().setVisibility(View.INVISIBLE);
            buttonProvider.getButton3().setVisibility(View.INVISIBLE);
            buttonProvider.getButton4().setVisibility(View.INVISIBLE);
            buttonProvider.getButton5().setVisibility(View.INVISIBLE);
            buttonProvider.getButton6().setVisibility(View.INVISIBLE);
            buttonProvider.getButtonHome().setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());

        }
    }
}


