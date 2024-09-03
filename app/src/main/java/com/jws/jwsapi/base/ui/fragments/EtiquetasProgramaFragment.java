package com.jws.jwsapi.base.ui.fragments;

import static com.jws.jwsapi.common.storage.Storage.getArchivosExtension;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.base.ui.activities.MainActivity;
import com.jws.jwsapi.base.ui.adapters.AdapterEtiquetasDePrograma;
import com.jws.jwsapi.R;
import com.jws.jwsapi.base.models.EtiquetasDeProgramaModel;
import com.jws.jwsapi.general.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.general.formulador.di.LabelManager;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EtiquetasProgramaFragment extends Fragment implements AdapterEtiquetasDePrograma.ItemClickListener {

    @Inject
    PreferencesManager preferencesManager;
    @Inject
    LabelManager labelManager;
    Button bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6;
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    AdapterEtiquetasDePrograma adapter;
    public RecyclerView rc_lista_ingredientes;
    List<EtiquetasDeProgramaModel> lista_ingredientes;
    int posicion_recycler=-1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standar_etiquetasdeprograma,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        rc_lista_ingredientes =view.findViewById(R.id.lista_ingredientes);
        List<String> nombreetiquetas=labelManager.nombreEtiquetas;
        lista_ingredientes= new ArrayList<>();
        for(int i=0;i<nombreetiquetas.size();i++){
            lista_ingredientes.add(new EtiquetasDeProgramaModel(nombreetiquetas.get(i),preferencesManager.getEtiqueta(i)));
        }
        configuracionBotones();
        cargarRecyclerView();

    }


    private void cargarRecyclerView(){
        rc_lista_ingredientes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdapterEtiquetasDePrograma(getContext(),lista_ingredientes, getArchivosExtension(".prn"),preferencesManager);
        adapter.setClickListener(this);
        rc_lista_ingredientes.setAdapter(adapter);

    }


    private void configuracionBotones() {
        if (buttonProvider != null) {
            bt_home = buttonProvider.getButtonHome();
            bt_1 = buttonProvider.getButton1();
            bt_2 = buttonProvider.getButton2();
            bt_3 = buttonProvider.getButton3();
            bt_4 = buttonProvider.getButton4();
            bt_5 = buttonProvider.getButton5();
            bt_6 = buttonProvider.getButton6();
            buttonProvider.getTitulo().setText("ETIQUETAS DE PROGRAMA");

            bt_1.setVisibility(View.INVISIBLE);
            bt_2.setVisibility(View.INVISIBLE);
            bt_1.setBackgroundResource(R.drawable.boton_buscar_i);
            bt_2.setBackgroundResource(R.drawable.boton_add_i);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);
            bt_home.setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());



        }
    }


    @Override
    public void onItemClick(View view, int position) {
        posicion_recycler=position;
    }

}


