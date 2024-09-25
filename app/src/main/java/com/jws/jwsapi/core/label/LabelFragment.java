package com.jws.jwsapi.core.label;

import static com.jws.jwsapi.core.printer.PrinterHelper.getFieldsFromLabel;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.core.printer.PrinterPreferences;
import com.jws.jwsapi.databinding.StandarImpresorasEtiquetasBinding;
import com.jws.jwsapi.utils.AdapterCommon;
import com.jws.jwsapi.utils.file.FileExtensionUtils;
import com.jws.jwsapi.utils.file.FileUtils;
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
        String label = FileUtils.openAndReadFile(labelList.get(position), mainActivity);
        if (label != null && !label.isEmpty()) {
            setupFieldRecycler(getFieldsFromLabel(label), labelList.get(position));
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

        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        setupButtons();

        labelList = FileExtensionUtils.getFilesExtension(".prn");
        setupLabelRecycler(labelList);

    }

    private void setupButtons() {
        if (buttonProvider != null) {
            buttonProvider.getTitle().setText(R.string.title_fragment_label);
            buttonProvider.getButton1().setOnClickListener(view -> saveSettings());
            setupButton(buttonProvider.getButton1(), R.drawable.boton_guardado_i, View.VISIBLE);
            setupButton(buttonProvider.getButton2(), R.drawable.boton_impresora_i, View.INVISIBLE);
            setupButton(buttonProvider.getButton3(), R.drawable.boton_pdf_i, View.INVISIBLE);
            setupButton(buttonProvider.getButton4(), R.drawable.boton_video_i, View.INVISIBLE);
            setupButton(buttonProvider.getButton5(), R.drawable.boton_camara_i, View.INVISIBLE);
            setupButton(buttonProvider.getButton6(), null, View.INVISIBLE);

            buttonProvider.getButtonHome().setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());

        }
    }

    private void setupButton(Button button, Integer resid, Integer visibility) {
        if (resid != null) button.setBackgroundResource(resid);
        if (visibility != null) button.setVisibility(visibility);
    }

    private void saveSettings() {
        if (fieldAdapter == null) return;

        if (fieldAdapter.intElements != null) {
            printerPreferences.setListSpinner(fieldAdapter.intInternalElements, fieldAdapter.label);
        }
        if (fieldAdapter.staticInternalElements != null) {
            printerPreferences.setListFijo(fieldAdapter.staticInternalElements, fieldAdapter.label);
        }
    }

    public void setupLabelRecycler(List<String> lista) {
        binding.recyclerLabel.setLayoutManager(new LinearLayoutManager(getContext()));
        AdapterCommon labelAdapter = new AdapterCommon(getContext(), lista);
        labelAdapter.setClickListener(this);
        binding.recyclerLabel.setAdapter(labelAdapter);

    }

    private void setupFieldRecycler(List<LabelModel> lista, String name) {
        binding.recyclerField.setLayoutManager(new LinearLayoutManager(getContext()));
        fieldAdapter = new LabelAdapter(getContext(), lista, name, labelManager, printerPreferences);
        binding.recyclerField.setAdapter(fieldAdapter);
    }

}


