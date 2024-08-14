package com.jws.jwsapi.feature.formulador.ui.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.jws.jwsapi.base.activities.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.ProgFormuladorPantallaConfiguracionbalanzaBinding;
import com.jws.jwsapi.feature.formulador.di.RecetaManager;
import com.jws.jwsapi.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Form_Fragment_ConfiguracionBalanza extends Fragment  {

    @Inject
    RecetaManager recetaManager;
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    ProgFormuladorPantallaConfiguracionbalanzaBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = ProgFormuladorPantallaConfiguracionbalanzaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        configuracionBotones();
        mainActivity=(MainActivity)getActivity();
        initializateViews();
        setOnClickListeners();

    }

    private void setOnClickListeners() {
        binding.tvBza1.setOnClickListener(view1 -> Teclado(binding.tvBza1,"Ingrese el limite de la balanza 1"));
        binding.tvBza2.setOnClickListener(view1 -> Teclado(binding.tvBza2,"Ingrese el limite de la balanza 2"));
        binding.tvBza3.setOnClickListener(view1 -> Teclado(binding.tvBza3,"Ingrese el limite de la balanza 3"));
    }

    private void initializateViews() {
        if(mainActivity.getNivelUsuario()<4){
            binding.ln17.setVisibility(View.GONE);
            binding.ln18.setVisibility(View.GONE);
        }
        String[] Balanzas_arr = getResources().getStringArray(R.array.balanzas);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(requireContext(),R.layout.item_spinner,Balanzas_arr);
        adapter4.setDropDownViewResource(R.layout.item_spinner);
        binding.spBalanzas.setAdapter(adapter4);
        binding.spBalanzas.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        binding.spBalanzas.setSelection(mainActivity.mainClass.preferencesManager.getModoBalanza());
        binding.spBalanzas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!recetaManager.ejecutando){
                    mainActivity.mainClass.preferencesManager.setModoBalanza(i);
                }else{
                    Utils.Mensaje("No puede la cambiar la configuracion mientras hay una receta en ejecucion",R.layout.item_customtoasterror,mainActivity);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        binding.tvBza1.setText(mainActivity.mainClass.preferencesManager.getBza1Limite());
        binding.tvBza2.setText(mainActivity.mainClass.preferencesManager.getBza2Limite());
        binding.tvBza3.setText(mainActivity.mainClass.preferencesManager.getBza3Limite());

    }

    public void Teclado(TextView View,String texto){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialogo_dosopcionespuntos, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        TextView textView=mView.findViewById(R.id.textViewt);
        LinearLayout lndelete_text=mView.findViewById(R.id.lndelete_text);
        textView.setText(texto);
        if(View==binding.tvBza1||View==binding.tvBza2||View==binding.tvBza3){
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_FLAG_DECIMAL);
            userInput.setKeyListener(DigitsKeyListener.getInstance(".0123456789"));
        }
        userInput.setOnLongClickListener(v -> true);
        userInput.setText(View.getText().toString());
        userInput.requestFocus();

        if(!View.getText().toString().equals("") && !View.getText().toString().equals("-")){
            userInput.setSelection(userInput.getText().length());
        }
        lndelete_text.setOnClickListener(view -> userInput.setText(""));
        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {
            if(Utils.isNumeric(userInput.getText().toString())){
                if(View==binding.tvBza1){mainActivity.mainClass.preferencesManager.setBza1Limite(userInput.getText().toString());}
                if(View==binding.tvBza2){mainActivity.mainClass.preferencesManager.setBza2Limite(userInput.getText().toString());}
                if(View==binding.tvBza3){
                    mainActivity.mainClass.preferencesManager.setBza3Limite(userInput.getText().toString());
                }
                View.setText(userInput.getText().toString());
            }
            dialog.cancel();
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());

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


