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
import com.jws.jwsapi.core.data.local.PreferencesManager;
import com.jws.jwsapi.core.label.LabelManager;
import com.jws.jwsapi.core.printer.preferences.PreferencesPrinterManager;
import com.jws.jwsapi.core.user.UserManager;
import com.jws.jwsapi.databinding.StandarImpresorasBinding;
import com.jws.jwsapi.utils.ToastHelper;
import com.jws.jwsapi.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.util.Arrays;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class PrinterFragment extends Fragment{
    @Inject
    PreferencesManager preferencesManager;
    @Inject
    LabelManager labelManager;
    @Inject
    UserManager userManager;
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    PrinterManager imprimirStandar;
    PreferencesPrinterManager preferencesPrinterManager;
    StandarImpresorasBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = StandarImpresorasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        setupButtons();

        initPrinter();

        initTextView();

        initSpinner();


    }

    private void initPrinter() {
        imprimirStandar=new PrinterManager(getContext(),mainActivity, userManager,preferencesManager,labelManager);
        preferencesPrinterManager= new PreferencesPrinterManager(mainActivity);
    }

    private void initTextView() {
        binding.tvIpimpresora.setText(preferencesPrinterManager.consultaIP());
        binding.tvIpimpresora.setOnClickListener(v -> keyboardIpAdress(binding.tvIpimpresora, "Ingrese IP de Impresora", requireContext(), this::setupIpHandler));
    }

    private void initSpinner() {
        setupSpinner(binding.spImpresora, requireContext(), Arrays.asList(getResources().getStringArray(R.array.ImpresoraModo)));
        binding.spImpresora.setSelection(preferencesPrinterManager.consultaModo());
        binding.spImpresora.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i!=2){
                    preferencesPrinterManager.setModo(i);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setupIpHandler(String ip) {
        if(Utils.isIP(ip)) {
            preferencesPrinterManager.setIP(ip);
        } else {
            ToastHelper.message(getString(R.string.error_ip_adress_invalid),R.layout.item_customtoasterror,requireContext());
            binding.tvIpimpresora.setText("");
        }
    }

    private void setupButtons() {
        if (buttonProvider != null) {
            buttonProvider.getTitulo().setText(R.string.title_fragment_printer);

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


