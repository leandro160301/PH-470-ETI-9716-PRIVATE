package com.jws.jwsapi.feature.formulador.ui.fragment;

import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.dialogoTexto;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.tabs.TabLayout;
import com.jws.jwsapi.base.ui.activities.MainActivity;
import com.jws.jwsapi.common.users.UsersManager;
import com.jws.jwsapi.databinding.ProgFormuladorPantallaGuardadosBinding;
import com.jws.jwsapi.feature.formulador.models.FormModelPesadasDB;
import com.jws.jwsapi.feature.formulador.models.FormModelRecetaDB;
import com.jws.jwsapi.feature.formulador.ui.adapter.FormAdapterGuardadosPesadas;
import com.jws.jwsapi.feature.formulador.ui.adapter.FormAdapterGuardadosRecetas;
import com.jws.jwsapi.R;
import com.jws.jwsapi.feature.formulador.ui.viewmodel.FormFragmentGuardadosViewModel;
import com.jws.jwsapi.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.util.List;

import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FormFragmentGuardados extends Fragment {


    @Inject
    UsersManager usersManager;
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    private FormFragmentGuardadosViewModel viewModel;
    private ProgFormuladorPantallaGuardadosBinding binding;
    private FormAdapterGuardadosPesadas adapterPesadas;
    private FormAdapterGuardadosRecetas adapterRecetas;
    private int menu = 0;
    private static int maxPesadas = 50;
    private static int maxRecetas = 10;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = ProgFormuladorPantallaGuardadosBinding.inflate(inflater, container, false);
        viewModel = new ViewModelProvider(this).get(FormFragmentGuardadosViewModel.class);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        configuracionBotones();
        tabLayoutListener();
        viewModel.getPesadas().observe(getViewLifecycleOwner(), pesadas -> {
            List<FormModelPesadasDB> limitedPesadas=  pesadas.size() > maxPesadas ? pesadas.subList(0, maxPesadas) : pesadas;
            adapterPesadas = new FormAdapterGuardadosPesadas(getContext(), limitedPesadas);
            binding.listaacumulados.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.listaacumulados.setAdapter(adapterPesadas);
        });

        viewModel.getRecetas().observe(getViewLifecycleOwner(), recetas -> {
            List<FormModelRecetaDB> limitedRecetas=  recetas.size() > maxRecetas ? recetas.subList(0, maxRecetas) : recetas;
            adapterRecetas = new FormAdapterGuardadosRecetas(getContext(), limitedRecetas);
            adapterRecetas.setButtonClickListener((v, position) ->
                    viewModel.cargarPesadasConId(getContext(), String.valueOf(limitedRecetas.get(position).getId()), true));
            binding.listaacumulados.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.listaacumulados.setAdapter(adapterRecetas);
        });

        viewModel.getPedidos().observe(getViewLifecycleOwner(), pedidos -> {
            List<FormModelRecetaDB> limitedRecetas=  pedidos.size() > maxRecetas ? pedidos.subList(0, maxRecetas) : pedidos;
            adapterRecetas = new FormAdapterGuardadosRecetas(getContext(), limitedRecetas);
            adapterRecetas.setButtonClickListener((v, position) ->
                    viewModel.cargarPesadasConId(getContext(), String.valueOf(pedidos.get(position).getId()), false));
            binding.listaacumulados.setLayoutManager(new LinearLayoutManager(getContext()));
            binding.listaacumulados.setAdapter(adapterRecetas);
        });

        viewModel.cargarPesadas(getContext());


    }

    private void tabLayoutListener() {
        binding.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                int position = tab.getPosition();
                menu = position;
                switch (position) {
                    case 0:
                        viewModel.cargarPesadas(getContext());
                        break;
                    case 1:
                        viewModel.cargarRecetas(getContext());
                        break;
                    case 2:
                        viewModel.cargarPedidos(getContext());
                        break;
                }
            }
            @Override
            public void onTabUnselected(@NonNull TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(@NonNull TabLayout.Tab tab) {}
        });
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
            buttonProvider.getTitulo().setText("GUARDADOS");
            bt_2.setBackgroundResource(R.drawable.boton_excel_i);
            bt_2.setVisibility(View.INVISIBLE);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);
            bt_home.setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());
            bt_2.setOnClickListener(view -> crearExcel());
            bt_1.setOnClickListener(view -> eliminarDatos());

        }
    }

    private void crearExcel() {
        if(viewModel.getPesadas().getValue() !=null&&viewModel.getPesadas().getValue().size()>0){
            int registros=mainActivity.storage.cantidadRegistros();
            if(registros>=50 && registros<60){
                Utils.Mensaje("El indicador alcanzo el limite recomendado de 50 registros. Quedan "+ (60 - registros) +" registros disponibles",R.layout.item_customtoasterror,mainActivity);
                //mainActivity.Dialogo_excel(guardado_pesadas);
            }
            if(registros<50){
                // mainActivity.Dialogo_excel(guardado_pesadas);
            }
            if(registros>=60){
                Utils.Mensaje("Error el indicador alcanzo el limite de registros excel",R.layout.item_customtoasterror,mainActivity);
            }
        }
    }

    private void eliminarDatos() {
        if (usersManager.getNivelUsuario() > 1) {
            String texto = obtenerMensajeConfirmacion(menu);
            if (texto != null) {
                dialogoTexto(getContext(), texto,"ELIMINAR",() -> viewModel.eliminarDatos(getContext(), menu));
            }
        } else {
            Utils.Mensaje("Debe ingresar la clave para acceder a esta configuración", R.layout.item_customtoasterror, mainActivity);
        }
    }

    private String obtenerMensajeConfirmacion(int menu) {
        switch (menu) {
            case 0:
                return "¿Está seguro de eliminar todos los datos de pesadas?";
            case 1:
                return "¿Está seguro de eliminar todos los datos de recetas?";
            case 2:
                return "¿Está seguro de eliminar todos los datos de pedidos?";
            default:
                return null;
        }
    }

}