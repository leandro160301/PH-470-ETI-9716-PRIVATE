package com.jws.jwsapi.feature.formulador.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.jws.jwsapi.base.activities.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.ProgFormuladorPantallaDatosBinding;
import com.jws.jwsapi.feature.formulador.di.RecetaManager;
import com.jws.jwsapi.feature.formulador.viewmodel.Form_Fragment_DatosViewModel;
import com.jws.jwsapi.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Form_Fragment_Datos extends Fragment{
    @Inject
    RecetaManager recetaManager;
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    private ProgFormuladorPantallaDatosBinding binding;
    private Form_Fragment_DatosViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = ProgFormuladorPantallaDatosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        initializeViewModel();
        observeViewModel();
        configuracionBotones();
        setOnClickListeners();
        setupSpinnersWithDefaults();

    }

    private void observeViewModel() {
        viewModel.getCampo1().observe(getViewLifecycleOwner(), campo1 -> updateTextCampoBackground(campo1,binding.tvCampo1,binding.imCampo1));
        viewModel.getCampo2().observe(getViewLifecycleOwner(), campo2 -> updateTextCampoBackground(campo2,binding.tvCampo2,binding.imCampo2));
        viewModel.getCampo3().observe(getViewLifecycleOwner(), campo3 -> updateTextCampoBackground(campo3,binding.tvCampo3,binding.imCampo3));
        viewModel.getCampo4().observe(getViewLifecycleOwner(), campo4 -> updateTextCampoBackground(campo4,binding.tvCampo4,binding.imCampo4));
        viewModel.getCampo5().observe(getViewLifecycleOwner(), campo5 -> updateTextCampoBackground(campo5,binding.tvCampo5,binding.imCampo5));
    }

    private void setupSpinnersWithDefaults() {
        String[] Vencimiento_arr = getResources().getStringArray(R.array.Vencimiento);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(requireContext(),R.layout.item_spinner,Vencimiento_arr);
        adapter2.setDropDownViewResource(R.layout.item_spinner);
        binding.spVencimiento.setAdapter(adapter2);
        binding.spVencimiento.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        int modovenc=mainActivity.mainClass.preferencesManager.getModoVencimiento();
        binding.spVencimiento.setSelection(modovenc);
        binding.spVencimiento.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!recetaManager.ejecutando){
                    if(i!=modovenc){
                        mainActivity.mainClass.preferencesManager.setModoVencimiento(i);
                        mainActivity.mainClass.preferencesManager.setVencimiento("");
                        mainActivity.mainClass.ovenci.value="";
                    }
                }else{
                    Utils.Mensaje("No puede la cambiar la configuracion mientras hay una receta en ejecucion",R.layout.item_customtoasterror,mainActivity);
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        String[] Lote_arr = getResources().getStringArray(R.array.Lote);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(requireContext(),R.layout.item_spinner,Lote_arr);
        adapter3.setDropDownViewResource(R.layout.item_spinner);
        binding.spLote.setAdapter(adapter3);
        binding.spLote.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        int modolote=mainActivity.mainClass.preferencesManager.getModoLote();
        binding.spLote.setSelection(mainActivity.mainClass.preferencesManager.getModoLote());
        binding.spLote.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                if(!recetaManager.ejecutando){
                    if(i!=modolote){
                        mainActivity.mainClass.preferencesManager.setLote("");
                        mainActivity.mainClass.olote.value="";
                        mainActivity.mainClass.preferencesManager.setModoLote(i);
                    }

                }else{
                    Utils.Mensaje("No puede la cambiar la configuracion mientras hay una receta en ejecucion",R.layout.item_customtoasterror,mainActivity);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setOnClickListeners() {
        binding.tvCampo1.setOnClickListener(view117 -> Teclado(binding.tvCampo1,"Ingrese el campo"));
        binding.tvCampo2.setOnClickListener(view116 -> Teclado(binding.tvCampo2,"Ingrese el campo"));
        binding.tvCampo3.setOnClickListener(view115 -> Teclado(binding.tvCampo3,"Ingrese el campo"));
        binding.tvCampo4.setOnClickListener(view114 -> Teclado(binding.tvCampo4,"Ingrese el campo"));
        binding.tvCampo5.setOnClickListener(view113 -> Teclado(binding.tvCampo5,"Ingrese el campo"));

        binding.imCampo1.setOnClickListener(view112 -> viewModel.disableCampo1());
        binding.imCampo2.setOnClickListener(view111 -> viewModel.disableCampo2());
        binding.imCampo3.setOnClickListener(view110 -> viewModel.disableCampo3());
        binding.imCampo4.setOnClickListener(view19 -> viewModel.disableCampo4());
        binding.imCampo5.setOnClickListener(view18 -> viewModel.disableCampo5());
        binding.lnEditarlote.setOnClickListener(view17 -> DialogoReset(binding.lnEditarlote,"CERRAR LOTE","NUNCA","CUANDO FINALIZA LA CANTIDAD A REALIZAR","POR RECETA/PEDIDO"));
        binding.lnEditarvencimiento.setOnClickListener(view16 -> DialogoReset(binding.lnEditarvencimiento,"RESTAURAR VENCIMIENTO","NUNCA","CUANDO FINALIZA LA CANTIDAD A REALIZAR","POR RECETA/PEDIDO"));
        binding.lnEditar1.setOnClickListener(view15 -> DialogoReset(binding.lnEditar1,"RESTAURAR","NUNCA","CUANDO FINALIZA LA CANTIDAD A REALIZAR","POR RECETA/PEDIDO"));
        binding.lnEditar2.setOnClickListener(view14 -> DialogoReset(binding.lnEditar2,"RESTAURAR","NUNCA","CUANDO FINALIZA LA CANTIDAD A REALIZAR","POR RECETA/PEDIDO"));
        binding.lnEditar3.setOnClickListener(view13 -> DialogoReset(binding.lnEditar3,"RESTAURAR","NUNCA","CUANDO FINALIZA LA CANTIDAD A REALIZAR","POR RECETA/PEDIDO"));
        binding.lnEditar4.setOnClickListener(view12 -> DialogoReset(binding.lnEditar4,"RESTAURAR","NUNCA","CUANDO FINALIZA LA CANTIDAD A REALIZAR","POR RECETA/PEDIDO"));
        binding.lnEditar5.setOnClickListener(view1 -> DialogoReset(binding.lnEditar5,"RESTAURAR","NUNCA","CUANDO FINALIZA LA CANTIDAD A REALIZAR","POR RECETA/PEDIDO"));

    }

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(this).get(Form_Fragment_DatosViewModel.class);
    }

    private void updateTextCampoBackground(String campo, TextView tvCampo, ImageView imCampo) {
        tvCampo.setText(campo);
        if (campo.isEmpty()) {
            imCampo.setBackgroundResource(R.drawable.unchecked);
        } else {
            imCampo.setBackgroundResource(R.drawable.checked);
        }
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
            buttonProvider.getTitulo().setText("DATOS");

            bt_1.setVisibility(View.INVISIBLE);
            bt_2.setVisibility(View.INVISIBLE);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);
            bt_home.setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());

        }
    }
    public void Teclado(TextView View,String texto){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialogo_dosopciones, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        TextView textView=mView.findViewById(R.id.textViewt);
        final LinearLayout delete_text= mView.findViewById(R.id.lndelete_text);
        delete_text.setOnClickListener(view -> userInput.setText(""));
        textView.setText(texto);
        userInput.setOnLongClickListener(v -> true);
        userInput.requestFocus();
        userInput.setText(View.getText().toString());
        if(!View.getText().toString().equals("") && !View.getText().toString().equals("-")){
            userInput.setSelection(userInput.getText().length());
        }
        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {
            String input = userInput.getText().toString();
            if (!input.isEmpty()) {
                if (View == binding.tvCampo1) viewModel.setCampo1(input);
                if (View == binding.tvCampo2) viewModel.setCampo2(input);
                if (View == binding.tvCampo3) viewModel.setCampo3(input);
                if (View == binding.tvCampo4) viewModel.setCampo4(input);
                if (View == binding.tvCampo5) viewModel.setCampo5(input);
            } else {
                if (View == binding.tvCampo1) viewModel.disableCampo1();
                if (View == binding.tvCampo2) viewModel.disableCampo2();
                if (View == binding.tvCampo3) viewModel.disableCampo3();
                if (View == binding.tvCampo4) viewModel.disableCampo4();
                if (View == binding.tvCampo5) viewModel.disableCampo5();
            }
            dialog.cancel();
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());

    }
    public void DialogoReset(LinearLayout linearLayout,String titulo,String o1,String o2,String o3){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(),R.style.AlertDialogCustom);
        View mView = getLayoutInflater().inflate(R.layout.dialogo_temas, null);
        TextView textView=mView.findViewById(R.id.textViewt);
        Button Cancelar =  mView.findViewById(R.id.buttonc);
        TextView tvopcion1 =  mView.findViewById(R.id.tvTema1);
        TextView tvopcion2 =  mView.findViewById(R.id.tvTema2);
        TextView tvopcion3 =  mView.findViewById(R.id.tvTema3);
        textView.setText(titulo);
        tvopcion1.setText(o1);
        tvopcion2.setText(o2);
        tvopcion3.setText(o3);
        if (linearLayout == binding.lnEditarlote) {
            actualizarFondo(mainActivity.mainClass.preferencesManager.getResetLote(),tvopcion1,tvopcion2,tvopcion3);
        } else if (linearLayout == binding.lnEditarvencimiento) {
            actualizarFondo(mainActivity.mainClass.preferencesManager.getResetVencimiento(),tvopcion1,tvopcion2,tvopcion3);
        } else if (linearLayout == binding.lnEditar1) {
            actualizarFondo(mainActivity.mainClass.preferencesManager.getResetCampo1(),tvopcion1,tvopcion2,tvopcion3);
        } else if (linearLayout == binding.lnEditar2) {
            actualizarFondo(mainActivity.mainClass.preferencesManager.getResetCampo2(),tvopcion1,tvopcion2,tvopcion3);
        } else if (linearLayout == binding.lnEditar3) {
            actualizarFondo(mainActivity.mainClass.preferencesManager.getResetCampo3(),tvopcion1,tvopcion2,tvopcion3);
        } else if (linearLayout == binding.lnEditar4) {
            actualizarFondo(mainActivity.mainClass.preferencesManager.getResetCampo4(),tvopcion1,tvopcion2,tvopcion3);
        } else if (linearLayout == binding.lnEditar5) {
            actualizarFondo(mainActivity.mainClass.preferencesManager.getResetCampo5(),tvopcion1,tvopcion2,tvopcion3);
        }
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        tvopcion1.setOnClickListener(view -> {
            tvopcion1.setBackgroundResource(R.drawable.fondoinfoprincipal);
            tvopcion2.setBackgroundResource(R.drawable.stylekeycor3);
            tvopcion3.setBackgroundResource(R.drawable.stylekeycor3);
            setearResets(linearLayout,0);
            dialog.cancel();

        });
        tvopcion2.setOnClickListener(view -> {
            tvopcion2.setBackgroundResource(R.drawable.fondoinfoprincipal);
            tvopcion1.setBackgroundResource(R.drawable.stylekeycor3);
            tvopcion3.setBackgroundResource(R.drawable.stylekeycor3);
            setearResets(linearLayout,1);
            dialog.cancel();
        });
        tvopcion3.setOnClickListener(view -> {
            tvopcion3.setBackgroundResource(R.drawable.fondoinfoprincipal);
            tvopcion2.setBackgroundResource(R.drawable.stylekeycor3);
            tvopcion1.setBackgroundResource(R.drawable.stylekeycor3);
            setearResets(linearLayout,2);
            dialog.cancel();
        });

        Cancelar.setOnClickListener(view -> dialog.cancel());

    }

    public void setearResets(LinearLayout linearLayout,int posi){
        if(linearLayout==binding.lnEditarlote){
            mainActivity.mainClass.preferencesManager.setResetLote(posi);
        }
        if(linearLayout==binding.lnEditarvencimiento){
            mainActivity.mainClass.preferencesManager.setResetVencimiento(posi);
        }
        if(linearLayout==binding.lnEditar1){
            mainActivity.mainClass.preferencesManager.setResetCampo1(posi);
        }
        if(linearLayout==binding.lnEditar2){
            mainActivity.mainClass.preferencesManager.setResetCampo2(posi);
        }
        if(linearLayout==binding.lnEditar3){
            mainActivity.mainClass.preferencesManager.setResetCampo3(posi);
        }
        if(linearLayout==binding.lnEditar4){
            mainActivity.mainClass.preferencesManager.setResetCampo4(posi);
        }
        if(linearLayout==binding.lnEditar5){
            mainActivity.mainClass.preferencesManager.setResetCampo5(posi);
        }
    }
    void actualizarFondo(int opcion,TextView tvopcion1,TextView tvopcion2,TextView tvopcion3) {
        if (opcion == 0) {
            tvopcion1.setBackgroundResource(R.drawable.fondoinfoprincipal);
        } else if (opcion == 1) {
            tvopcion2.setBackgroundResource(R.drawable.fondoinfoprincipal);
        } else if (opcion == 2) {
            tvopcion3.setBackgroundResource(R.drawable.fondoinfoprincipal);
        }
    }
}


