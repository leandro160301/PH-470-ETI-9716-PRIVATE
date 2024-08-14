package com.jws.jwsapi.feature.formulador.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.jws.jwsapi.base.activities.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.ProgFormuladorPantallaIngresodatosBinding;
import com.jws.jwsapi.utils.DatePickerDialogFragment;
import com.jws.jwsapi.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Form_Fragment_IngresoDatos extends Fragment implements  DatePickerDialogFragment.DatePickerListener{

    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    Boolean reset1=false,reset2=false,reset3=false,reset4=false;
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
        binding.tvLote.setOnClickListener(view1 -> Teclado(binding.tvLote,"Ingrese Lote"));
        binding.tvVencimiento.setOnClickListener(view12 -> {
            int modo=mainActivity.mainClass.preferencesManager.getModoVencimiento();
            if(modo==0){
                showDatePicker();
            }
            if(modo==1){
                Teclado(binding.tvVencimiento,"Ingrese Vencimiento en dias");
            }
        });
        binding.tvTurno.setOnClickListener(view13 -> Teclado(binding.tvTurno,"Ingrese Turno"));
        binding.tvCampo1.setOnClickListener(view14 -> Teclado(binding.tvCampo1,"Ingrese "+campo1));
        binding.tvCampo2.setOnClickListener(view15 -> Teclado(binding.tvCampo2,"Ingrese "+campo2));
        binding.tvCampo3.setOnClickListener(view16 -> Teclado(binding.tvCampo3,"Ingrese "+campo3));
        binding.tvCampo4.setOnClickListener(view17 -> Teclado(binding.tvCampo4,"Ingrese "+campo4));
        binding.tvCampo5.setOnClickListener(view18 -> Teclado(binding.tvCampo5,"Ingrese "+campo5));


    }

    private void setupViews() {
        int modolote=mainActivity.mainClass.preferencesManager.getModoLote();
        if(modolote==0){
            binding.lnLote.setVisibility(View.VISIBLE);
        }else{
            binding.lnLote.setVisibility(View.GONE);
        }
        binding.tv1.setText(campo1);
        binding.tv2.setText(campo2);
        binding.tv3.setText(campo3);
        binding.tv4.setText(campo4);
        binding.tv5.setText(campo5);
        binding.tvLote.setText(mainActivity.mainClass.olote.value.toString());
        binding.tvVencimiento.setText(mainActivity.mainClass.ovenci.value.toString());
        binding.tvTurno.setText(mainActivity.mainClass.oturno.value.toString());
        binding.tvCampo1.setText(mainActivity.mainClass.ocampo1.value.toString());
        binding.tvCampo2.setText(mainActivity.mainClass.ocampo2.value.toString());
        binding.tvCampo3.setText(mainActivity.mainClass.ocampo3.value.toString());
        binding.tvCampo4.setText(mainActivity.mainClass.ocampo4.value.toString());
        binding.tvCampo5.setText(mainActivity.mainClass.ocampo5.value.toString());

        if(campo1.equals("")){
            binding.lnCampo1.setVisibility(View.GONE);
        }
        if(campo2.equals("")){
            binding.lnCampo2.setVisibility(View.GONE);
        }
        if(campo3.equals("")){
            binding.lnCampo3.setVisibility(View.GONE);
        }
        if(campo4.equals("")){
            binding.lnCampo4.setVisibility(View.GONE);
        }
        if(campo5.equals("")){
            binding.lnCampo5.setVisibility(View.GONE);
        }
    }

    private void initializatePreferences() {
        campo1=mainActivity.mainClass.preferencesManager.getCampo1();
        campo2=mainActivity.mainClass.preferencesManager.getCampo2();
        campo3=mainActivity.mainClass.preferencesManager.getCampo3();
        campo4=mainActivity.mainClass.preferencesManager.getCampo4();
        campo5=mainActivity.mainClass.preferencesManager.getCampo5();
        reset1=mainActivity.mainClass.preferencesManager.getReset1();
        reset2=mainActivity.mainClass.preferencesManager.getReset2();
        reset3=mainActivity.mainClass.preferencesManager.getReset3();
        reset4=mainActivity.mainClass.preferencesManager.getReset4();
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
            bt_home.setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());

        }
    }
    public void showDatePicker(){
        DatePickerDialogFragment newFragment = new DatePickerDialogFragment();
        newFragment.setDatePickerListener(this);
        newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
    }
    public void Teclado(TextView View,String texto){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialogo_dosopciones, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        TextView textView=mView.findViewById(R.id.textViewt);
        final LinearLayout delete_text= mView.findViewById(R.id.lndelete_text);
        delete_text.setOnClickListener(view -> userInput.setText(""));
        textView.setText(texto);
        if(View==binding.tvVencimiento){
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
        userInput.setOnLongClickListener(v -> true);
        userInput.requestFocus();
        if(View!=binding.tvVencimiento){
            userInput.setText(View.getText().toString());
        }
        if(!View.getText().toString().equals("") && !View.getText().toString().equals("-")){
            userInput.setSelection(userInput.getText().length());
        }
        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {
            if(View==binding.tvLote){
                binding.tvLote.setText(userInput.getText().toString());
                mainActivity.mainClass.olote.value=userInput.getText().toString();
                mainActivity.mainClass.preferencesManager.setLote(userInput.getText().toString());
            }
            if(View==binding.tvVencimiento&& Utils.isNumeric(userInput.getText().toString())){
                Calendar calendar = Calendar.getInstance();
                int numeroDias = Integer.parseInt(userInput.getText().toString());
                calendar.add(Calendar.DAY_OF_YEAR, numeroDias);
                Date nuevaFecha = calendar.getTime();
                SimpleDateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                String fechaFormateada = formatoFecha.format(nuevaFecha);
                binding.tvVencimiento.setText(fechaFormateada);
                mainActivity.mainClass.ovenci.value=fechaFormateada;
                mainActivity.mainClass.preferencesManager.setVencimiento(fechaFormateada);
            }
            if(View==binding.tvTurno){
                binding.tvTurno.setText(userInput.getText().toString());
                mainActivity.mainClass.oturno.value=userInput.getText().toString();
            }
            if(View==binding.tvCampo1){
                binding.tvCampo1.setText(userInput.getText().toString());
                mainActivity.mainClass.ocampo1.value=userInput.getText().toString();
                mainActivity.mainClass.preferencesManager.setCampo1Valor(userInput.getText().toString());
            }
            if(View==binding.tvCampo2){
                binding.tvCampo2.setText(userInput.getText().toString());
                mainActivity.mainClass.ocampo2.value=userInput.getText().toString();
                mainActivity.mainClass.preferencesManager.setCampo2Valor(userInput.getText().toString());
            }
            if(View==binding.tvCampo3){
                binding.tvCampo3.setText(userInput.getText().toString());
                mainActivity.mainClass.ocampo3.value=userInput.getText().toString();
                mainActivity.mainClass.preferencesManager.setCampo3Valor(userInput.getText().toString());
            }
            if(View==binding.tvCampo4){
                binding.tvCampo4.setText(userInput.getText().toString());
                mainActivity.mainClass.ocampo4.value=userInput.getText().toString();
                mainActivity.mainClass.preferencesManager.setCampo4Valor(userInput.getText().toString());
            }
            if(View==binding.tvCampo5){
                binding.tvCampo5.setText(userInput.getText().toString());
                mainActivity.mainClass.ocampo5.value=userInput.getText().toString();
                mainActivity.mainClass.preferencesManager.setCampo5Valor(userInput.getText().toString());
            }
            dialog.cancel();
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());

    }

    @Override
    public void onDateSelected(String selectedDate) {
        binding.tvVencimiento.setText(selectedDate);
        mainActivity.mainClass.ovenci.value=selectedDate;
        mainActivity.mainClass.preferencesManager.setVencimiento(selectedDate);
    }
}


