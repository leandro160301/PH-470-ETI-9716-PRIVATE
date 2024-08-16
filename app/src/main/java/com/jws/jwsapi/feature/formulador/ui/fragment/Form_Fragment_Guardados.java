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
import androidx.recyclerview.widget.LinearLayoutManager;
import com.google.android.material.tabs.TabLayout;
import com.jws.jwsapi.base.ui.activities.MainActivity;
import com.jws.jwsapi.databinding.ProgFormuladorPantallaGuardadosBinding;
import com.jws.jwsapi.feature.formulador.ui.adapter.Form_Adapter_Guardados_Pesadas;
import com.jws.jwsapi.feature.formulador.ui.adapter.Form_Adapter_Guardados_Recetas;
import com.jws.jwsapi.feature.formulador.MainFormClass;
import com.jws.jwsapi.feature.formulador.models.Form_Model_PesadasDB;
import com.jws.jwsapi.feature.formulador.models.Form_Model_RecetaDB;
import com.jws.jwsapi.feature.formulador.data.sql.Form_SQL_db;
import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import java.util.ArrayList;
import java.util.List;

public class Form_Fragment_Guardados extends Fragment {

    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    Form_Adapter_Guardados_Pesadas adapter_pesadas;
    Form_Adapter_Guardados_Recetas adapter_recetas;
    List<Form_Model_PesadasDB> guardado_pesadas = new ArrayList<>();
    List<Form_Model_RecetaDB> guardado_recetasypedidos = new ArrayList<>();
    int menu=0;
    ProgFormuladorPantallaGuardadosBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = ProgFormuladorPantallaGuardadosBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        configuracionBotones();
        tabLayoutListener();
        cargarRecyclerViewPesadas();


    }

    private void tabLayoutListener() {
        binding.tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                int position = tab.getPosition();
                menu=position;
                switch (position) {
                    case 0:
                        cargarRecyclerViewPesadas();
                        break;
                    case 1:
                        cargarRecyclerViewRecetas();
                        break;
                    case 2:
                        cargarRecyclerViewPedidos();
                        break;
                }
            }
            @Override
            public void onTabUnselected(@NonNull TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(@NonNull TabLayout.Tab tab) {
            }
        });
    }

    private void cargarRecyclerViewPesadas(){
        try (Form_SQL_db guardadosSQL = new Form_SQL_db(getContext(), MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
            guardado_pesadas = guardadosSQL.getPesadasSQL(null);
        }
        binding.listaacumulados.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter_pesadas = new Form_Adapter_Guardados_Pesadas(getContext(), guardado_pesadas);
        binding.listaacumulados.setAdapter(adapter_pesadas);
    }

    private void cargarRecyclerViewPesadasconId(String id,Boolean receta){
        try (Form_SQL_db guardadosSQL = new Form_SQL_db(getContext(), MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
            guardado_pesadas = guardadosSQL.getPesadasSQLconId(id,receta);
        }
        binding.listaacumulados.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter_pesadas = new Form_Adapter_Guardados_Pesadas(getContext(), guardado_pesadas);
        binding.listaacumulados.setAdapter(adapter_pesadas);
    }

    private void cargarRecyclerViewRecetas(){
        try (Form_SQL_db guardadosSQL = new Form_SQL_db(getContext(), MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
            guardado_recetasypedidos = guardadosSQL.getRecetasSQL(null);
        }
        binding.listaacumulados.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter_recetas = new Form_Adapter_Guardados_Recetas(getContext(), guardado_recetasypedidos);
        adapter_recetas.setButtonClickListener((view, position) -> cargarRecyclerViewPesadasconId(String.valueOf(guardado_recetasypedidos.get(position).getId()),true));
        binding.listaacumulados.setAdapter(adapter_recetas);
    }

    private void cargarRecyclerViewPedidos(){
        try (Form_SQL_db guardadosSQL = new Form_SQL_db(getContext(), MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
            guardado_recetasypedidos = guardadosSQL.getPedidosSQL(null);
        }
        binding.listaacumulados.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter_recetas = new Form_Adapter_Guardados_Recetas(getContext(), guardado_recetasypedidos);
        adapter_recetas.setButtonClickListener((view, position) -> cargarRecyclerViewPesadasconId(String.valueOf(guardado_recetasypedidos.get(position).getId()),false));
        binding.listaacumulados.setAdapter(adapter_recetas);
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
        if(guardado_pesadas !=null){
            if(guardado_pesadas.size()>0){
                int registros=mainActivity.storage.cantidadRegistros();
                if(registros>=50 && registros<60){
                    Utils.Mensaje("El indicador alcanzo el limite recomendado de 50 registros. Quedan "+String.valueOf(60-registros)+" registros disponibles",R.layout.item_customtoasterror,mainActivity);
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
    }

    private void eliminarDatos() {
        if (mainActivity.getNivelUsuario() > 1) {
            String texto = obtenerMensajeConfirmacion(menu);
            if (texto != null) {
                dialogoTexto(mainActivity, texto,"ELIMINAR",() -> {
                    try (Form_SQL_db productosSQL = new Form_SQL_db(getContext(), MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
                        switch (menu) {
                            case 0:
                                productosSQL.eliminarPesadas();
                                cargarRecyclerViewPesadas();
                                break;
                            case 1:
                                productosSQL.eliminarRecetas();
                                cargarRecyclerViewRecetas();
                                break;
                            case 2:
                                productosSQL.eliminarPedidos();
                                cargarRecyclerViewPedidos();
                                break;
                            default:
                                Utils.Mensaje("Opción de menú no válida", R.layout.item_customtoasterror, mainActivity);
                        }
                    } catch (Exception e) {
                        Utils.Mensaje("Error al eliminar datos: " + e.getMessage(), R.layout.item_customtoasterror, mainActivity);
                    }
                });
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


