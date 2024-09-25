package com.jws.jwsapi.core.label;

import static com.jws.jwsapi.core.printer.PrinterHelper.removeLastSeparator;
import static com.jws.jwsapi.utils.SpinnerHelper.setupSpinner;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jws.jwsapi.R;
import com.jws.jwsapi.core.printer.PrinterPreferences;
import com.jws.jwsapi.databinding.DialogoEtiquetaconcatenarBinding;
import com.jws.jwsapi.utils.AdapterCommon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LabelConcatDialog {
    private final Context context;
    private final PrinterPreferences printerPreferences;
    private int posicionConcat = -1;
    private List<Integer> elementsConcatFormat;
    private int selected = 0;

    public LabelConcatDialog(Context context, PrinterPreferences printerPreferences) {
        this.context = context;
        this.printerPreferences = printerPreferences;
    }

    @SuppressLint("NotifyDataSetChanged")
    private static void updateAdapter(AdapterCommon adapter, List<String> ListElementsArrayConcat) {
        if (adapter != null) {
            adapter.filterList(ListElementsArrayConcat);
            adapter.notifyDataSetChanged();
        }
    }

    public void showDialog(String label, int posicion, TextView textView, List<String> varList) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        DialogoEtiquetaconcatenarBinding binding = DialogoEtiquetaconcatenarBinding.inflate(LayoutInflater.from(context));

        setupSpinner(binding.spCampo, context.getApplicationContext(), varList);

        elementsConcatFormat = printerPreferences.getListConcat(label, posicion);
        if (elementsConcatFormat == null) elementsConcatFormat = new ArrayList<>();

        setupListView(varList, binding);

        String separator = printerPreferences.getSeparator(label, posicion);
        setupSeparatorOptions(binding, separator);
        setupOptionsClickListener(binding);

        mBuilder.setView(binding.getRoot());
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        binding.buttons.setOnClickListener(view -> handleCancelButton(label, posicion, textView, varList, dialog));
        binding.buttonc.setOnClickListener(view -> dialog.cancel());

    }

    private void handleCancelButton(String label, int posicion, TextView textView, List<String> varList, AlertDialog dialog) {
        printerPreferences.setListConcat(elementsConcatFormat, label, posicion);
        String separated = "";
        separated = updateSeparator(1, ",", label, posicion, separated);
        separated = updateSeparator(2, ":", label, posicion, separated);
        separated = updateSeparator(3, ";", label, posicion, separated);
        separated = updateSeparator(4, "|", label, posicion, separated);

        textView.setText(removeLastSeparator(separated, calculeConcatText(varList, separated)));
        dialog.cancel();
    }

    @NonNull
    private String calculeConcatText(List<String> varList, String separated) {
        String concat = "";
        if (elementsConcatFormat != null) {
            for (Integer concatValue : elementsConcatFormat) {
                if (varList.size() > concatValue) {
                    concat = concat.concat(varList.get(concatValue) + separated);
                }
            }
        }
        return concat;
    }

    private void setupOptionsClickListener(DialogoEtiquetaconcatenarBinding binding) {
        Button[] buttons = {binding.btComa, binding.btDospuntos, binding.btPuntoycoma, binding.btBarra};
        binding.btComa.setOnClickListener(view -> updateButtonStates(1, buttons));
        binding.btDospuntos.setOnClickListener(view -> updateButtonStates(2, buttons));
        binding.btPuntoycoma.setOnClickListener(view -> updateButtonStates(3, buttons));
        binding.btBarra.setOnClickListener(view -> updateButtonStates(4, buttons));
    }

    private void setupSeparatorOptions(DialogoEtiquetaconcatenarBinding binding, String separator) {
        setupOption(separator, binding.btComa, 1);
        setupOption(separator, binding.btDospuntos, 2);
        setupOption(separator, binding.btPuntoycoma, 3);
        setupOption(separator, binding.btBarra, 4);
    }

    private void setupListView(List<String> varList, DialogoEtiquetaconcatenarBinding binding) {
        List<String> ListElementsArrayConcat = getElementsConcat(varList);
        AdapterCommon adapter = new AdapterCommon(context, ListElementsArrayConcat);
        adapter.setClickListener((view, position) -> posicionConcat = position);
        binding.listview.setLayoutManager(new LinearLayoutManager(context));
        binding.listview.setAdapter(adapter);
        binding.btAdd.setOnClickListener(view -> handleAdd(binding.spCampo, adapter, ListElementsArrayConcat, varList));
        binding.btborrar.setOnClickListener(view -> handleDelete(adapter, ListElementsArrayConcat));
    }

    @NonNull
    private List<String> getElementsConcat(List<String> varList) {
        List<String> ListElementsArrayConcat = new ArrayList<>();
        for (Integer concatValue : elementsConcatFormat) {
            if (concatValue < varList.size()) {
                ListElementsArrayConcat.add(varList.get(concatValue));
            }
        }
        return ListElementsArrayConcat;
    }

    private String updateSeparator(int x, String separador, String etiqueta, int posicion, String separated) {
        if (selected == x) {
            printerPreferences.setSeparator(separador, etiqueta, posicion);
            separated = separador;
        }
        return separated;
    }

    private void setupOption(String separacion, Button button, int selected) {
        if (Objects.equals(separacion, button.getText().toString())) {
            button.setBackgroundResource(R.drawable.botoneraprincipal_selectorgris);
            button.setTextColor(Color.WHITE);
            this.selected = selected;
        }
    }

    private void updateButtonStates(int selectedButtonId, Button[] buttons) {
        int[] buttonIds = {1, 2, 3, 4};
        for (int i = 0; i < buttons.length; i++) {
            Button button = buttons[i];
            button.setBackgroundResource(buttonIds[i] == selectedButtonId ? R.drawable.botoneraprincipal_selectorgris : R.drawable.stylekeycor4);
            button.setTextColor(buttonIds[i] == selectedButtonId ? Color.WHITE : Color.BLACK);
        }
        selected = selectedButtonId;
    }

    private void handleDelete(AdapterCommon adapter, List<String> ListElementsArrayConcat) {
        if (posicionConcat < ListElementsArrayConcat.size() && posicionConcat != -1) {
            elementsConcatFormat.remove(posicionConcat);
            ListElementsArrayConcat.remove(posicionConcat);
            updateAdapter(adapter, ListElementsArrayConcat);
            posicionConcat = -1;
        }
    }

    private void handleAdd(Spinner spCampo, AdapterCommon adapter, List<String> ListElementsArrayConcat, List<String> listaVariables) {
        if (spCampo.getSelectedItemPosition() > 0 && spCampo.getSelectedItemPosition() < listaVariables.size()) {
            if (elementsConcatFormat != null) {
                elementsConcatFormat.add(spCampo.getSelectedItemPosition());
                ListElementsArrayConcat.add(listaVariables.get(spCampo.getSelectedItemPosition()));
                updateAdapter(adapter, ListElementsArrayConcat);
            }
            spCampo.setSelection(0);
            posicionConcat = -1;
        }
    }

}
