package com.jws.jwsapi.base.ui.fragments;

import static com.jws.jwsapi.helpers.PrinterHelper.getCamposEtiqueta;
import static com.jws.jwsapi.common.storage.Storage.getArchivosExtension;
import static com.jws.jwsapi.common.storage.Storage.openAndReadFile;
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
import com.jws.jwsapi.base.ui.adapters.AdapterMultimedia;
import com.jws.jwsapi.R;
import com.jws.jwsapi.base.models.EtiquetasModel;
import com.jws.jwsapi.base.ui.adapters.AdapterEtiquetas;
import com.jws.jwsapi.feature.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.feature.formulador.di.LabelManager;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EtiquetasFragment extends Fragment implements AdapterMultimedia.ItemClickListener {

    @Inject
    PreferencesManager preferencesManager;
    @Inject
    LabelManager labelManager;
    Button bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6;
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    List<EtiquetasModel> listaCampos =new ArrayList<>();
    List<String> listaEtiquetas=new ArrayList<>();
    RecyclerView recyclerEtiquetas, recyclerCampos;
    AdapterMultimedia adapterEtiquetas;
    AdapterEtiquetas adapterCampos;
    public int posicion =0;
    String etiquetaNombre ="";


    @Override
    public void onItemClick(View view, int position) {
        posicion=position;
        String etiqueta =openAndReadFile(listaEtiquetas.get(position),mainActivity);
        if(etiqueta!=null&& !etiqueta.equals("")){
            listaCampos=getCamposEtiqueta(etiqueta);
            etiquetaNombre = listaEtiquetas.get(position);
            setupRecyclerCampos(listaCampos,posicion);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standar_impresoras_etiquetas,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        configuracionBotones();
        recyclerEtiquetas =view.findViewById(R.id.listview);
        recyclerCampos =view.findViewById(R.id.recyclerEtiqueta);

        listaEtiquetas= getArchivosExtension(".prn");
        setupRecycler(listaEtiquetas);

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
            buttonProvider.getTitulo().setText("CONFIGURACION ETIQUETAS");

            bt_1.setBackgroundResource(R.drawable.boton_guardado_i);
            bt_2.setBackgroundResource(R.drawable.boton_impresora_i);
            bt_3.setBackgroundResource(R.drawable.boton_pdf_i);
            bt_4.setBackgroundResource(R.drawable.boton_video_i);
            bt_5.setBackgroundResource(R.drawable.boton_camara_i);

            bt_2.setVisibility(View.INVISIBLE);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);

            bt_1.setOnClickListener(view -> {
                if(adapterCampos !=null){
                    if(adapterCampos.ListElementsInt!=null){
                        preferencesManager.saveListSpinner(adapterCampos.ListElementsInternaInt, adapterCampos.etiqueta);
                    }
                    if(adapterCampos.ListElementsInternaFijo!=null){
                        preferencesManager.saveListFijo(adapterCampos.ListElementsInternaFijo, adapterCampos.etiqueta);
                    }

                }
            });

            bt_home.setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());

        }
    }


    public void setupRecycler(List<String> lista) {
        recyclerEtiquetas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterEtiquetas = new AdapterMultimedia(getContext(), lista);
        adapterEtiquetas.setClickListener(this);
        recyclerEtiquetas.setAdapter(adapterEtiquetas);

    }
    private void setupRecyclerCampos(List<EtiquetasModel> lista,int posi) {
        recyclerCampos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterCampos = new AdapterEtiquetas(getContext(), lista,mainActivity, etiquetaNombre,posi,labelManager,preferencesManager);
        recyclerCampos.setAdapter(adapterCampos);
    }

}


