package com.jws.jwsapi.general.printer;

import static com.jws.jwsapi.general.dialog.DialogUtil.keyboardIpAdress;
import static com.jws.jwsapi.general.utils.SpinnerHelper.setupSpinner;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.general.data.local.PreferencesManager;
import com.jws.jwsapi.general.label.LabelManager;
import com.jws.jwsapi.general.printer.preferences.PreferencesPrinterManager;
import com.jws.jwsapi.general.user.UserManager;
import com.jws.jwsapi.general.utils.ToastHelper;
import com.jws.jwsapi.general.utils.Utils;
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
    TextView tv_ipimpresora;
    Spinner sp_impresora;
    PrinterManager imprimirStandar;
    PreferencesPrinterManager preferencesPrinterManager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standar_impresoras,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        setupButtons();
        tv_ipimpresora=view.findViewById(R.id.tv_ipimpresora);
        sp_impresora= view.findViewById(R.id.sp_impresora);
        initPrinter();

        initTextView();

        initSpinner();


    }

    private void initPrinter() {
        imprimirStandar=new PrinterManager(getContext(),mainActivity, userManager,preferencesManager,labelManager);
        preferencesPrinterManager= new PreferencesPrinterManager(mainActivity);
    }

    private void initTextView() {
        tv_ipimpresora.setText(preferencesPrinterManager.consultaIP());
        tv_ipimpresora.setOnClickListener(v -> keyboardIpAdress(tv_ipimpresora, "Ingrese IP de Impresora", requireContext(), this::setupIpHandler));
    }

    private void initSpinner() {
        setupSpinner(sp_impresora, requireContext(), Arrays.asList(getResources().getStringArray(R.array.ImpresoraModo)));
        sp_impresora.setSelection(preferencesPrinterManager.consultaModo());
        sp_impresora.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
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
            tv_ipimpresora.setText("");
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


