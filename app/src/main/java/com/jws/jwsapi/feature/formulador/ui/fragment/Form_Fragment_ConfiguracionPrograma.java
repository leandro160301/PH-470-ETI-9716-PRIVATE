package com.jws.jwsapi.feature.formulador.ui.fragment;

import static com.jws.jwsapi.utils.DialogUtil.TecladoEntero;
import static com.jws.jwsapi.utils.DialogUtil.TecladoFlotante;
import static com.jws.jwsapi.helpers.SpinnerHelper.configurarSpinner;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.jws.jwsapi.base.ui.activities.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.ProgFormuladorPantallaConfiguracionpBinding;
import com.jws.jwsapi.feature.formulador.di.RecetaManager;
import com.jws.jwsapi.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import java.util.ArrayList;
import java.util.Arrays;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Form_Fragment_ConfiguracionPrograma extends Fragment  {

    @Inject
    RecetaManager recetaManager;
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    int modoreceta=0;
    int modouso=0;
    ProgFormuladorPantallaConfiguracionpBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = ProgFormuladorPantallaConfiguracionpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        configuracionBotones();

        mainActivity=(MainActivity)getActivity();

        configurarSpinner(binding.spReceta,getContext(), Arrays.asList(getResources().getStringArray(R.array.Receta)));
        modoreceta=mainActivity.mainClass.preferencesManager.getModoReceta();
        binding.spReceta.setSelection(modoreceta);

        binding.spReceta.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!recetaManager.ejecutando){
                    mainActivity.mainClass.preferencesManager.setModoReceta(i);
                    if(i!=modoreceta){
                        resetearValores();
                    }
                }else{
                    Utils.Mensaje("No puede la cambiar la configuracion mientras hay una receta en ejecucion",R.layout.item_customtoasterror,mainActivity);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        configurarSpinner(binding.spMododeuso,getContext(), Arrays.asList(getResources().getStringArray(R.array.Mododeuso)));
        modouso=mainActivity.mainClass.preferencesManager.getModoUso();
        binding.spMododeuso.setSelection(modouso);

        binding.spMododeuso.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!recetaManager.ejecutando){
                    mainActivity.mainClass.preferencesManager.setModoUso(i);
                    if(i!=modouso){
                        resetearValores();
                    }
                }else{
                    Utils.Mensaje("No puede la cambiar la configuracion mientras hay una receta en ejecucion",R.layout.item_customtoasterror,mainActivity);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        if(mainActivity.mainClass.preferencesManager.getRecipientexPaso()){
            binding.toggle.check(R.id.btON);
        }
        else {
            binding.toggle.check(R.id.btOFF);
        }
        if(mainActivity.mainClass.preferencesManager.getContinuarFueraRango()){
            binding.toggle2.check(R.id.btON2);
        }
        else {
            binding.toggle2.check(R.id.btOFF2);
        }
        if(mainActivity.mainClass.preferencesManager.getEtiquetaxPaso()){
            binding.toggle3.check(R.id.btON3);
        }
        else {
            binding.toggle3.check(R.id.btOFF3);
        }

        binding.toggle.setOnCheckedChangeListener((radioGroup, i) -> {
            mainActivity.mainClass.preferencesManager.setRecipientexPaso(binding.toggle.getCheckedRadioButtonId() == R.id.btON);
        });
        binding.toggle2.setOnCheckedChangeListener((radioGroup, i) -> {
            mainActivity.mainClass.preferencesManager.setContinuarFueraRango(binding.toggle2.getCheckedRadioButtonId() == R.id.btON2);
        });
        binding.toggle3.setOnCheckedChangeListener((radioGroup, i) -> {
            mainActivity.mainClass.preferencesManager.setEtiquetaxPaso(binding.toggle3.getCheckedRadioButtonId() == R.id.btON3);
        });
        binding.tvTolerancia.setText(mainActivity.mainClass.preferencesManager.getTolerancia());
        binding.tvBza1.setText(mainActivity.mainClass.preferencesManager.getBza1Limite());
        binding.tvBza2.setText(mainActivity.mainClass.preferencesManager.getBza2Limite());
        binding.tvBza3.setText(mainActivity.mainClass.preferencesManager.getBza3Limite());
        binding.tvTolerancia.setOnClickListener(view12 -> TecladoEntero(binding.tvTolerancia, "Ingrese la tolerancia", mainActivity, texto -> mainActivity.mainClass.preferencesManager.setTolerancia(texto)));
        binding.tvBza1.setOnClickListener(view13 -> TecladoFlotante(binding.tvBza1, "Ingrese el limite de la balanza 1", mainActivity, texto -> mainActivity.mainClass.preferencesManager.setBza1Limite(texto)));
        binding.tvBza2.setOnClickListener(view13 -> TecladoFlotante(binding.tvBza2, "Ingrese el limite de la balanza 2", mainActivity, texto -> mainActivity.mainClass.preferencesManager.setBza2Limite(texto)));
        binding.tvBza3.setOnClickListener(view13 -> TecladoFlotante(binding.tvBza3, "Ingrese el limite de la balanza 3", mainActivity, texto -> mainActivity.mainClass.preferencesManager.setBza3Limite(texto)));


    }

    private void resetearValores() {
        mainActivity.mainClass.preferencesManager.setRecetaactual("");
        recetaManager.recetaActual ="";
        mainActivity.mainClass.preferencesManager.setCodigoRecetaactual("");
        recetaManager.codigoReceta ="";
        mainActivity.mainClass.preferencesManager.setNombreRecetaactual("");
        recetaManager.nombreReceta ="";
        recetaManager.listRecetaActual =new ArrayList<>();
        mainActivity.mainClass.preferencesManager.setPasosRecetaActual(new ArrayList<>());
        mainActivity.mainClass.preferencesManager.setCantidad(1);
        recetaManager.cantidad.setValue(1);
        mainActivity.mainClass.preferencesManager.setRealizadas(0);
        recetaManager.realizadas.setValue(0);
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
            buttonProvider.getTitulo().setText("CONFIGURACION DE PROGRAMA");

            bt_1.setBackgroundResource(R.drawable.boton_atras_i);
            bt_1.setVisibility(View.INVISIBLE);
            bt_2.setVisibility(View.INVISIBLE);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);

            bt_home.setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());

        }
    }

}


