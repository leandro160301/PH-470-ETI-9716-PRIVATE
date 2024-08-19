package com.jws.jwsapi.feature.formulador.ui.fragment;

import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.Teclado;
import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.TecladoEntero;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.jws.jwsapi.base.ui.activities.MainActivity;
import com.jws.jwsapi.databinding.ProgFormuladorPantallaIngresodatosBinding;
import com.jws.jwsapi.base.ui.fragments.DatePickerDialogFragment;
import com.jws.jwsapi.feature.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.feature.formulador.di.LabelManager;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FormFragmentIngresoDatos extends Fragment implements  DatePickerDialogFragment.DatePickerListener{

    @Inject
    PreferencesManager preferencesManager;
    @Inject
    LabelManager labelManager;
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    public String campo1="";
    public String campo2="";
    public String campo3="";
    public String campo4="";
    public String campo5="";
    ProgFormuladorPantallaIngresodatosBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = ProgFormuladorPantallaIngresodatosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        configuracionBotones();
        initializatePreferences();
        setupViews();
        setOnClickListeners();

    }

    private void setOnClickListeners() {
        binding.tvLote.setOnClickListener(view -> Teclado(binding.tvLote, "Ingrese Lote", mainActivity, this::setearLote));
        binding.tvVencimiento.setOnClickListener(view12 -> setVencimiento());
        binding.tvCampo1.setOnClickListener(view -> Teclado(binding.tvCampo1, "Ingrese "+campo1, mainActivity, this::setearCampo1));
        binding.tvCampo2.setOnClickListener(view -> Teclado(binding.tvCampo2, "Ingrese "+campo2, mainActivity, this::setearCampo2));
        binding.tvCampo3.setOnClickListener(view -> Teclado(binding.tvCampo3, "Ingrese "+campo3, mainActivity, this::setearCampo3));
        binding.tvCampo4.setOnClickListener(view -> Teclado(binding.tvCampo4, "Ingrese "+campo4, mainActivity, this::setearCampo4));
        binding.tvCampo5.setOnClickListener(view -> Teclado(binding.tvCampo5, "Ingrese "+campo5, mainActivity, this::setearCampo5));

    }

    private void setearLote(String texto) {
        labelManager.olote.value=texto;
        preferencesManager.setLote(texto);
    }

    private void setearCampo1(String texto) {
        labelManager.ocampo1.value=texto;
        preferencesManager.setCampo1Valor(texto);
    }

    private void setearCampo2(String texto) {
        labelManager.ocampo2.value=texto;
        preferencesManager.setCampo2Valor(texto);
    }

    private void setearCampo3(String texto) {
        labelManager.ocampo3.value=texto;
        preferencesManager.setCampo3Valor(texto);
    }

    private void setearCampo4(String texto) {
        labelManager.ocampo4.value=texto;
        preferencesManager.setCampo4Valor(texto);
    }

    private void setearCampo5(String texto) {
        labelManager.ocampo5.value=texto;
        preferencesManager.setCampo5Valor(texto);
    }

    private void setVencimiento() {
        int modo=preferencesManager.getModoVencimiento();
        if(modo==0){
            showDatePicker();
        }
        if(modo==1){
            TecladoEntero(binding.tvVencimiento, "Ingrese Vencimiento en dias", mainActivity, this::setearVencimientoDias);
        }
    }

    private void setearVencimientoDias(String texto) {
        Calendar calendar = Calendar.getInstance();
        int numeroDias = Integer.parseInt(texto);
        calendar.add(Calendar.DAY_OF_YEAR, numeroDias);
        Date nuevaFecha = calendar.getTime();
        SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String fechaFormateada = formatoFecha.format(nuevaFecha);
        binding.tvVencimiento.setText(fechaFormateada);
        labelManager.ovenci.value=fechaFormateada;
        preferencesManager.setVencimiento(fechaFormateada);
    }

    private void setupViews() {
        int modolote=preferencesManager.getModoLote();
        setearVisibilidadLote(modolote);
        binding.tv1.setText(campo1);
        binding.tv2.setText(campo2);
        binding.tv3.setText(campo3);
        binding.tv4.setText(campo4);
        binding.tv5.setText(campo5);
        binding.tvLote.setText(labelManager.olote.value.toString());
        binding.tvVencimiento.setText(labelManager.ovenci.value.toString());
        binding.tvCampo1.setText(labelManager.ocampo1.value.toString());
        binding.tvCampo2.setText(labelManager.ocampo2.value.toString());
        binding.tvCampo3.setText(labelManager.ocampo3.value.toString());
        binding.tvCampo4.setText(labelManager.ocampo4.value.toString());
        binding.tvCampo5.setText(labelManager.ocampo5.value.toString());
        deshabilitarCampos();

    }

    private void setearVisibilidadLote(int modolote) {
        if(modolote==0){
            binding.lnLote.setVisibility(View.VISIBLE);
        }else{
            binding.lnLote.setVisibility(View.GONE);
        }
    }

    private void deshabilitarCampos() {
        if(campo1.isEmpty())binding.lnCampo1.setVisibility(View.GONE);
        if(campo2.isEmpty())binding.lnCampo2.setVisibility(View.GONE);
        if(campo3.isEmpty())binding.lnCampo3.setVisibility(View.GONE);
        if(campo4.isEmpty())binding.lnCampo4.setVisibility(View.GONE);
        if(campo5.isEmpty())binding.lnCampo5.setVisibility(View.GONE);
    }

    private void initializatePreferences() {
        campo1=preferencesManager.getCampo1();
        campo2=preferencesManager.getCampo2();
        campo3=preferencesManager.getCampo3();
        campo4=preferencesManager.getCampo4();
        campo5=preferencesManager.getCampo5();
    }

    private void configuracionBotones() {
        if (buttonProvider != null) {
            Button bt_home = buttonProvider.getButtonHome();
            Button bt_1 = buttonProvider.getButton1();
            Button bt_2 = buttonProvider.getButton2();
            Button bt_3 = buttonProvider.getButton3();
            Button bt_4 = buttonProvider.getButton4();
            Button bt_5 = buttonProvider.getButton5();
            Button bt_6 = buttonProvider.getButton6();
            buttonProvider.getTitulo().setText("INGRESO DE DATOS");
            bt_1.setVisibility(View.INVISIBLE);
            bt_2.setVisibility(View.INVISIBLE);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);
            bt_home.setOnClickListener(view -> mainActivity.openFragmentPrincipal());

        }
    }
    public void showDatePicker(){
        DatePickerDialogFragment newFragment = new DatePickerDialogFragment();
        newFragment.setDatePickerListener(this);
        newFragment.show(requireActivity().getSupportFragmentManager(), "datePicker");
    }


    @Override
    public void onDateSelected(String selectedDate) {
        binding.tvVencimiento.setText(selectedDate);
        labelManager.ovenci.value=selectedDate;
        preferencesManager.setVencimiento(selectedDate);
    }
}


