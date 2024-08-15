package com.jws.jwsapi.feature.formulador.ui.fragment;

import static com.jws.jwsapi.utils.DialogUtil.TecladoEntero;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.jws.jwsapi.base.ui.activities.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.ProgFormuladorPantallaConfiguracionturnosBinding;
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
        binding.tvTurno1.setOnClickListener(view1 -> TecladoEntero(binding.tvTurno1, "Ingrese la hora cuando comienza el turno 1", mainActivity, texto -> mainActivity.mainClass.preferencesManager.setTurno1(Integer.parseInt(texto))));
        binding.tvTurno2.setOnClickListener(view1 -> TecladoEntero(binding.tvTurno2, "Ingrese la hora cuando comienza el turno 2", mainActivity, texto -> mainActivity.mainClass.preferencesManager.setTurno2(Integer.parseInt(texto))));
        binding.tvTurno3.setOnClickListener(view1 -> TecladoEntero(binding.tvTurno3, "Ingrese la hora cuando comienza el turno 3", mainActivity, texto -> mainActivity.mainClass.preferencesManager.setTurno3(Integer.parseInt(texto))));
        binding.tvTurno4.setOnClickListener(view1 -> TecladoEntero(binding.tvTurno4, "Ingrese la hora cuando comienza el turno 4", mainActivity, texto -> mainActivity.mainClass.preferencesManager.setTurno4(Integer.parseInt(texto))));

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


