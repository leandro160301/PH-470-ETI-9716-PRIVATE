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
    private final LabelActions labelActions;
    TextView tvField, tvStaticText, tvConcatenatedText;
    LinearLayout lnStaticText, lnConcatenatedText, lnEdit;
    Spinner spCampo;
    private int lastPositionAdapter = -1;

    public LabelViewHolder(@NonNull View itemView, LabelActions labelActions) {
        super(itemView);
        this.labelActions = labelActions;
        tvField = itemView.findViewById(R.id.tv_campo);
        tvStaticText = itemView.findViewById(R.id.tv_textofijo);
        spCampo = itemView.findViewById(R.id.spCampo);
        tvConcatenatedText = itemView.findViewById(R.id.tv_textoconcatenado);
        lnStaticText = itemView.findViewById(R.id.ln_textofijo);
        lnConcatenatedText = itemView.findViewById(R.id.ln_textoconcatenado);
        lnEdit = itemView.findViewById(R.id.ln_editar);
    }

    public static void handleVisibility(LinearLayout holder, int gone, int gone1, LinearLayout holder1) {
        holder.setVisibility(gone);
        holder1.setVisibility(gone1);
    }

    public static void handleVisibilityElements(LabelViewHolder holder, int visible, LinearLayout holder1, LinearLayout holder2, int gone) {
        handleVisibility(holder.lnConcatenatedText, visible, View.VISIBLE, holder1);
        holder2.setVisibility(gone);
    }

    void bind(@NonNull LabelViewHolder holder, int position, Context context, List<String> variableList, List<LabelModel> mData) {
        try {
            setupSpinner(holder.spCampo, context.getApplicationContext(), variableList);
            holder.tvField.setText(mData.get(position).getFieldName());
            labelActions.updateViews(holder, position);
        } catch (Exception e) {
            e.printStackTrace();
            ToastHelper.message("Ocurrió un error:" + e.getMessage(), R.layout.item_customtoasterror, context);
        }

        holder.spCampo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                labelActions.onSpinnerSelection(i, position, holder);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        holder.lnEdit.setOnClickListener(view -> labelActions.onEditClick(holder, position));

        lastPositionAdapter = AdapterHelper.setAnimationSlideInLeft(holder.itemView, position, lastPositionAdapter, context);
    }

}
