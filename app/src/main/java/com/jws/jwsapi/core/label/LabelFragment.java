package com.jws.jwsapi.core.label;

import static com.jws.jwsapi.utils.PrinterHelper.getFieldsFromLabel;
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
import com.jws.jwsapi.databinding.StandarImpresorasEtiquetasBinding;

@AndroidEntryPoint
public class LabelFragment extends Fragment implements AdapterCommon.ItemClickListener {

    @Inject
    PrinterPreferences printerPreferences;
    @Inject
    LabelManager labelManager;
    private MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    private List<String> labelList = new ArrayList<>();
    private LabelAdapter fieldAdapter;
    private StandarImpresorasEtiquetasBinding binding;

    @Override
    public void onItemClick(View view, int position) {
        handleItemClick(position);
    }

    private void handleItemClick(int position) {
        String label =openAndReadFile(labelList.get(position),mainActivity);
        if(label!=null&& !label.isEmpty()){
            setupFieldRecycler(getFieldsFromLabel(label), position, labelList.get(position));
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = StandarImpresorasEtiquetasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        setupButtons();

        labelList = getFilesExtension(".prn");
        setupLabelRecycler(labelList);

    }

    private void setupButtons() {
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

            buttonProvider.getButton1().setOnClickListener(view -> saveSettings());
            buttonProvider.getButtonHome().setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());

        }
    }

    private void saveSettings() {
        if(fieldAdapter !=null){
            if(fieldAdapter.ListElementsInt!=null){
                printerPreferences.saveListSpinner(fieldAdapter.ListElementsInternaInt, fieldAdapter.etiqueta);
            }
            if(fieldAdapter.ListElementsInternaFijo!=null){
                printerPreferences.saveListFijo(fieldAdapter.ListElementsInternaFijo, fieldAdapter.etiqueta);
            }
        }
    }

    public void setupLabelRecycler(List<String> lista) {
        binding.recyclerLabel.setLayoutManager(new LinearLayoutManager(getContext()));
        AdapterCommon labelAdapter = new AdapterCommon(getContext(), lista);
        labelAdapter.setClickListener(this);
        binding.recyclerLabel.setAdapter(labelAdapter);

    }
    private void setupFieldRecycler(List<LabelModel> lista, int posi, String name) {
        binding.recyclerField.setLayoutManager(new LinearLayoutManager(getContext()));
        fieldAdapter = new LabelAdapter(getContext(), lista,mainActivity, name,posi,labelManager, printerPreferences);
        binding.recyclerField.setAdapter(fieldAdapter);
    }

}


