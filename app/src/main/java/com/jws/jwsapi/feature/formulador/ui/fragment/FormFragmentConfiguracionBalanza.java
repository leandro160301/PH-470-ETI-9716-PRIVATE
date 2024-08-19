package com.jws.jwsapi.feature.formulador.ui.fragment;

import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.TecladoFlotante;
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
import com.jws.jwsapi.common.users.UsersManager;
import com.jws.jwsapi.databinding.ProgFormuladorPantallaConfiguracionbalanzaBinding;
import com.jws.jwsapi.feature.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.feature.formulador.di.RecetaManager;
import com.jws.jwsapi.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import java.util.Arrays;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FormFragmentConfiguracionBalanza extends Fragment  {

    @Inject
    RecetaManager recetaManager;
    @Inject
    UsersManager usersManager;
    @Inject
    PreferencesManager preferencesManager;
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
        binding.tvBza1.setOnClickListener(view -> TecladoFlotante(binding.tvBza1, "Ingrese el limite de la balanza 1", mainActivity, texto -> preferencesManager.setBza1Limite(texto)));
        binding.tvBza2.setOnClickListener(view -> TecladoFlotante(binding.tvBza2, "Ingrese el limite de la balanza 2", mainActivity, texto -> preferencesManager.setBza2Limite(texto)));
        binding.tvBza3.setOnClickListener(view -> TecladoFlotante(binding.tvBza3, "Ingrese el limite de la balanza 3", mainActivity, texto -> preferencesManager.setBza3Limite(texto)));
    }

    private void initializateViews() {
        if(usersManager.getNivelUsuario()<4){
            binding.ln17.setVisibility(View.GONE);
            binding.ln18.setVisibility(View.GONE);
        }
        configurarSpinner(binding.spBalanzas,getContext(), Arrays.asList(getResources().getStringArray(R.array.balanzas)));
        binding.spBalanzas.setSelection(preferencesManager.getModoBalanza());
        binding.spBalanzas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(!recetaManager.ejecutando){
                    preferencesManager.setModoBalanza(i);
                }else{
                    Utils.Mensaje("No puede la cambiar la configuracion mientras hay una receta en ejecucion",R.layout.item_customtoasterror,mainActivity);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        binding.tvBza1.setText(preferencesManager.getBza1Limite());
        binding.tvBza2.setText(preferencesManager.getBza2Limite());
        binding.tvBza3.setText(preferencesManager.getBza3Limite());

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

            bt_home.setOnClickListener(view -> mainActivity.openFragmentPrincipal());

        }
    }

}


