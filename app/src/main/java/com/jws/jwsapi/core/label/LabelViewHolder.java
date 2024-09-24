package com.jws.jwsapi.core.label;

import static com.jws.jwsapi.utils.SpinnerHelper.setupSpinner;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.AdapterHelper;
import com.jws.jwsapi.utils.ToastHelper;

import java.util.List;

public class LabelViewHolder extends RecyclerView.ViewHolder {
    TextView tv_campo,tv_textofijo,tv_textoconcatenado;
    LinearLayout ln_textofijo,ln_textoconcatenado,ln_editar;
    Spinner spCampo;
    private int lastPositionAdapter = -1;
    private final LabelInterface labelInterface;

    public LabelViewHolder(@NonNull View itemView, LabelInterface labelInterface) {
        super(itemView);
        this.labelInterface = labelInterface;
        tv_campo = itemView.findViewById(R.id.tv_campo);
        tv_textofijo = itemView.findViewById(R.id.tv_textofijo);
        spCampo = itemView.findViewById(R.id.spCampo);
        tv_textoconcatenado = itemView.findViewById(R.id.tv_textoconcatenado);
        ln_textofijo = itemView.findViewById(R.id.ln_textofijo);
        ln_textoconcatenado = itemView.findViewById(R.id.ln_textoconcatenado);
        ln_editar = itemView.findViewById(R.id.ln_editar);
    }

    void bind(@NonNull LabelViewHolder holder, int position, Context context, List<String> listaVariables,List<LabelModel> mData) {
        try {
            setupSpinner(holder.spCampo,context.getApplicationContext(),listaVariables);
            holder.tv_campo.setText(mData.get(position).getFieldName());
            labelInterface.updateViews(holder, position);
        } catch (Exception e) {
            e.printStackTrace();
            ToastHelper.message("Ocurrió un error:"+e.getMessage(), R.layout.item_customtoasterror,context);
        }

        holder.spCampo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                labelInterface.spinnerSelection(i, position, holder);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        holder.ln_editar.setOnClickListener(view -> labelInterface.editClick(holder, position));

        lastPositionAdapter = AdapterHelper.setAnimationSlideInLeft(holder.itemView, position, lastPositionAdapter, context);
    }

    public static void handleVisibility(LinearLayout holder, int gone, int gone1, LinearLayout holder1) {
        holder.setVisibility(gone);
        holder1.setVisibility(gone1);
    }

    public static void handleVisibilityElements(LabelViewHolder holder, int visible, LinearLayout holder1, LinearLayout holder2, int gone) {
        handleVisibility(holder.ln_textoconcatenado, visible, View.VISIBLE, holder1);
        holder2.setVisibility(gone);
    }

}
