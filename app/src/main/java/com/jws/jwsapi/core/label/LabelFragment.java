package com.jws.jwsapi.core.label;

import static com.jws.jwsapi.utils.PrinterHelper.getCamposEtiqueta;
import static com.jws.jwsapi.core.storage.Storage.getFilesExtension;
import static com.jws.jwsapi.core.storage.Storage.openAndReadFile;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.AdapterCommon;
import com.jws.jwsapi.core.printer.PrinterPreferences;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class LabelFragment extends Fragment implements AdapterCommon.ItemClickListener {

    @Inject
    PrinterPreferences printerPreferences;
    @Inject
    LabelManager labelManager;
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    List<LabelModel> listaCampos =new ArrayList<>();
    List<String> listaEtiquetas=new ArrayList<>();
    RecyclerView recyclerEtiquetas, recyclerCampos;
    AdapterCommon adapterEtiquetas;
    LabelAdapter adapterCampos;
    String etiquetaNombre ="";

    @Override
    public void onItemClick(View view, int position) {
        handleItemClick(position);
    }

    private void handleItemClick(int position) {
        String label =openAndReadFile(listaEtiquetas.get(position),mainActivity);
        if(label!=null&& !label.isEmpty()){
            listaCampos=getCamposEtiqueta(label);
            etiquetaNombre = listaEtiquetas.get(position);
            setupRecyclerCampos(listaCampos, position);
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

        listaEtiquetas= getFilesExtension(".prn");
        setupRecycler(listaEtiquetas);

    }


    private void configuracionBotones() {
        if (buttonProvider != null) {
            buttonProvider.getTitulo().setText(R.string.title_fragment_label);

            buttonProvider.getButton1().setBackgroundResource(R.drawable.boton_guardado_i);
            buttonProvider.getButton2().setBackgroundResource(R.drawable.boton_impresora_i);
            buttonProvider.getButton3().setBackgroundResource(R.drawable.boton_pdf_i);
            buttonProvider.getButton4().setBackgroundResource(R.drawable.boton_video_i);
            buttonProvider.getButton5().setBackgroundResource(R.drawable.boton_camara_i);

            buttonProvider.getButton2().setVisibility(View.INVISIBLE);
            buttonProvider.getButton3().setVisibility(View.INVISIBLE);
            buttonProvider.getButton4().setVisibility(View.INVISIBLE);
            buttonProvider.getButton5().setVisibility(View.INVISIBLE);
            buttonProvider.getButton6().setVisibility(View.INVISIBLE);

            buttonProvider.getButton1().setOnClickListener(view -> {
                if(adapterCampos !=null){
                    if(adapterCampos.ListElementsInt!=null){
                        printerPreferences.saveListSpinner(adapterCampos.ListElementsInternaInt, adapterCampos.etiqueta);
                    }
                    if(adapterCampos.ListElementsInternaFijo!=null){
                        printerPreferences.saveListFijo(adapterCampos.ListElementsInternaFijo, adapterCampos.etiqueta);
                    }

                }
            });

            buttonProvider.getButtonHome().setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());

        }
    }

    public void setupRecycler(List<String> lista) {
        recyclerEtiquetas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterEtiquetas = new AdapterCommon(getContext(), lista);
        adapterEtiquetas.setClickListener(this);
        recyclerEtiquetas.setAdapter(adapterEtiquetas);

    }
    private void setupRecyclerCampos(List<LabelModel> lista, int posi) {
        recyclerCampos.setLayoutManager(new LinearLayoutManager(getContext()));
        adapterCampos = new LabelAdapter(getContext(), lista,mainActivity, etiquetaNombre,posi,labelManager, printerPreferences);
        recyclerCampos.setAdapter(adapterCampos);
    }

}


