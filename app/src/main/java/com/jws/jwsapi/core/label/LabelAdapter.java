package com.jws.jwsapi.core.label;

import static com.jws.jwsapi.core.label.LabelViewHolder.handleVisibility;
import static com.jws.jwsapi.core.label.LabelViewHolder.handleVisibilityElements;
import static com.jws.jwsapi.core.printer.PrinterHelper.removeLastSeparator;
import static com.jws.jwsapi.dialog.DialogUtil.keyboard;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.R;
import com.jws.jwsapi.core.printer.PrinterPreferences;
import com.jws.jwsapi.utils.ToastHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LabelAdapter extends RecyclerView.Adapter<LabelViewHolder> implements LabelActions {

    public final String label;
    private final List<LabelModel> mData;
    private final Context context;
    public List<Integer> intElements, intInternalElements, positionsElements;
    public List<String> staticElements, staticInternalElements;
    LabelManager labelManager;
    PrinterPreferences printerPreferences;
    private List<String> varElements;

    public LabelAdapter(Context context, List<LabelModel> mData, String label, LabelManager labelManager, PrinterPreferences printerPreferences) {
        this.mData = mData;
        this.context = context;
        this.label = label;
        this.labelManager = labelManager;
        this.printerPreferences = printerPreferences;

        setupVariablesList();
        setupSpinnerList();
        positionsElements = new ArrayList<>(Collections.nCopies(varElements.size(), 0));
        setupStaticList();

    }

    private static boolean isStaticTextOption(int selectedItem, int constantsSize) {
        return isStaticPosition(selectedItem, constantsSize - 2);
    }

    private static boolean isConcatenatedOption(int selectedItem, int constantsSize) {
        return isStaticPosition(selectedItem, constantsSize - 1);
    }

    private static boolean isStaticPosition(Integer positionsElements, int x) {
        return positionsElements == x;
    }

    private void setupVariablesList() {
        varElements = new ArrayList<>(labelManager.constantPrinterList);
        labelManager.varPrinterList.forEach(var -> varElements.add(var.getDescription()));
    }

    private void setupSpinnerList() {
        intElements = printerPreferences.getListSpinner(label);
        initializateList(intElements == null);
        initializateList(intElements.size() < mData.size());
        intInternalElements = intElements;
    }

    private void initializateList(boolean condition) {
        if (condition) {
            intElements = new ArrayList<>(Collections.nCopies(mData.size(), 0));
        }
    }

    private void setupStaticList() {
        staticElements = printerPreferences.getListFijo(label);
        initializateListString(staticElements == null);
        initializateListString(staticElements.size() < mData.size());
        staticInternalElements = staticElements;
    }

    private void initializateListString(boolean condition) {
        if (condition) {
            staticElements = new ArrayList<>(Collections.nCopies(mData.size(), ""));
        }
    }

    @NonNull
    @Override
    public LabelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.standar_adapter_etiqueta, parent, false);
        return new LabelViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelViewHolder holder, int position) {
        holder.bind(holder, position, context, varElements, mData);
    }

    @Override
    public void onEditClick(LabelViewHolder holder, int position) {
        if (positionsElements.size() <= position) return;

        if (isConcatenatedPosition(position)) {
            new LabelConcatDialog(context, printerPreferences).showDialog(label, position, holder.tv_textoconcatenado, varElements);
        } else if (isStaticPosition(positionsElements.get(position), 1)) {
            keyboard(holder.tv_textofijo, "Ingrese el texto fijo", context, texto -> handleInputText(position, texto));
        }
    }

    private void handleInputText(int posi, String texto) {
        if (staticInternalElements == null || staticInternalElements.size() <= posi) return;
        staticInternalElements.set(posi, texto);
    }

    @Override
    public void updateViews(LabelViewHolder holder, int position) {
        if (intElements == null || intElements.size() <= position) return;

        int selectedItem = intElements.get(position);
        int constantsSize = labelManager.constantPrinterList.size();

        holder.spCampo.setSelection(selectedItem);

        if (isConcatenatedOption(selectedItem, constantsSize)) {
            setupPositionList(position, 2);
            handleVisibilityElements(holder, View.VISIBLE, holder.ln_editar, holder.ln_textofijo, View.GONE);
            return;
        } else {
            handleVisibility(holder.ln_textoconcatenado, View.GONE, View.GONE, holder.ln_editar);
        }
        if (isStaticTextOption(selectedItem, constantsSize)) {
            handleVisibility(holder.ln_editar, View.VISIBLE, View.GONE, holder.ln_textoconcatenado);
            setupPositionList(position, 1);
            setupTextPosition(position, holder);
        } else {
            handleVisibility(holder.ln_textofijo, View.GONE, View.GONE, holder.ln_editar);
        }

    }

    private boolean isStaticText(int i) {
        return isStaticTextOption(i, labelManager.constantPrinterList.size());
    }

    private boolean isConcatenatedValue(int i) {
        return isConcatenatedOption(i, labelManager.constantPrinterList.size());
    }

    private boolean isConcatenatedPosition(int position) {
        return isStaticPosition(positionsElements.get(position), 2);
    }

    @Override
    public void onSpinnerSelection(int i, int position, LabelViewHolder holder) {
        try {
            if (intElements == null || intElements.size() <= position) return;

            intInternalElements.set(position, i);
            if (isConcatenatedValue(i)) {
                holder.tv_textoconcatenado.setText(getConcatenatedValue(position));
                handleVisibilityElements(holder, View.VISIBLE, holder.ln_editar, holder.ln_textofijo, View.GONE);
                setupPositionList(position, 2);
                return;
            }
            if (isStaticText(i)) {
                handleVisibility(holder.ln_textoconcatenado, View.GONE, View.GONE, holder.ln_editar);
                setupTextPosition(position, holder);
                setupPositionList(position, 1);
                handleVisibilityElements(holder, View.GONE, holder.ln_textofijo, holder.ln_editar, View.VISIBLE);
            } else {
                handleVisibility(holder.ln_textofijo, View.GONE, View.GONE, holder.ln_editar);
            }
        } catch (Exception e) {
            e.printStackTrace();
            ToastHelper.message("Ocurrió un error:" + e.getMessage(), R.layout.item_customtoasterror, context);
        }
    }

    @NonNull
    private String getConcatenatedValue(int position) {
        List<Integer> concatInternalElements = printerPreferences.getListConcat(label, position);
        final String[] concat = {""};
        String separator = printerPreferences.getSeparator(label, position);
        if (concatInternalElements == null) return concat[0];

        concatInternalElements.forEach(integer -> {
            if (varElements.size() > integer) {
                concat[0] = concat[0].concat(varElements.get(integer) + separator);
            }
        });
        return removeLastSeparator(separator, concat[0]);
    }

    private void setupTextPosition(int position, LabelViewHolder holder) {
        if (staticElements != null && staticElements.size() > position) {
            holder.tv_textofijo.setText(staticElements.get(position));
        }
    }

    private void setupPositionList(int position, int value) {
        if (positionsElements.size() > position) {
            positionsElements.set(position, value);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

}