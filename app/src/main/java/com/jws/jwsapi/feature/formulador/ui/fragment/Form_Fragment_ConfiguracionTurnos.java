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
import com.jws.jwsapi.base.ui.activities.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.ProgFormuladorPantallaConfiguracionturnosBinding;
import com.jws.jwsapi.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

public class Form_Fragment_ConfiguracionTurnos extends Fragment  {

    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    ProgFormuladorPantallaConfiguracionturnosBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = ProgFormuladorPantallaConfiguracionturnosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        configuracionBotones();

        mainActivity=(MainActivity)getActivity() ;

        binding.tvTurno1.setText(String.valueOf(mainActivity.mainClass.preferencesManager.getTurno1()));
        binding.tvTurno2.setText(String.valueOf(mainActivity.mainClass.preferencesManager.getTurno2()));
        binding.tvTurno3.setText(String.valueOf(mainActivity.mainClass.preferencesManager.getTurno3()));
        binding.tvTurno4.setText(String.valueOf(mainActivity.mainClass.preferencesManager.getTurno4()));
        binding.tvTurno1.setOnClickListener(view12 -> Teclado(binding.tvTurno1,"Ingrese la hora cuando comienza el turno 1"));
        binding.tvTurno2.setOnClickListener(view13 -> Teclado(binding.tvTurno2,"Ingrese la hora cuando comienza el turno 2"));
        binding.tvTurno3.setOnClickListener(view14 -> Teclado(binding.tvTurno3,"Ingrese la hora cuando comienza el turno 3"));
        binding.tvTurno4.setOnClickListener(view15 -> Teclado(binding.tvTurno4,"Ingrese la hora cuando comienza el turno 4"));

    }

    public void Teclado(TextView View,String texto){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());

        View mView = getLayoutInflater().inflate(R.layout.dialogo_dosopcionespuntos, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        TextView textView=mView.findViewById(R.id.textViewt);
        LinearLayout lndelete_text=mView.findViewById(R.id.lndelete_text);
        textView.setText(texto);
        if(View==binding.tvTurno1||View==binding.tvTurno2||View==binding.tvTurno3||View==binding.tvTurno4){
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
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
                if(View==binding.tvTurno1){
                    mainActivity.mainClass.preferencesManager.setTurno1(Integer.parseInt(userInput.getText().toString()));
                }
                if(View==binding.tvTurno2){
                    mainActivity.mainClass.preferencesManager.setTurno2(Integer.parseInt(userInput.getText().toString()));
                }
                if(View==binding.tvTurno3){
                    mainActivity.mainClass.preferencesManager.setTurno3(Integer.parseInt(userInput.getText().toString()));
                }
                if(View==binding.tvTurno4){
                    mainActivity.mainClass.preferencesManager.setTurno4(Integer.parseInt(userInput.getText().toString()));
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
            buttonProvider.getTitulo().setText("TURNOS");

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


