package com.jws.jwsapi.core.label;

import static com.jws.jwsapi.utils.AdapterHelper.setAnimationPivot;
import static com.jws.jwsapi.utils.SpinnerHelper.setupSpinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.R;
import com.jws.jwsapi.core.printer.PrinterPreferences;

import java.util.ArrayList;
import java.util.List;

public class LabelProgramAdapter extends RecyclerView.Adapter<LabelProgramAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    List<String> etiquetas;
    Context context;
    PrinterPreferences printerPreferences;
    private int selectedPos = RecyclerView.NO_POSITION;
    private List<LabelProgramModel> mData;
    private ItemClickListener mClickListener;
    private int lastPositionAdapter = -1;

    public LabelProgramAdapter(Context context, List<LabelProgramModel> data, List<String> etiquetas, PrinterPreferences printerPreferences) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.etiquetas = etiquetas;
        this.context = context;
        this.printerPreferences = printerPreferences;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.standar_adapter_etiquetadeprograma, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int posi = position;
        holder.tv_campo.setText(mData.get(position).getName());
        setupSpinner(holder.spCampo, context, etiquetas);
        int index = etiquetas.indexOf(printerPreferences.getLabel(posi));
        if (index > -1) {
            holder.spCampo.setSelection(index);
        }
        holder.spCampo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                printerPreferences.setLabel(etiquetas.get(i), posi);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        lastPositionAdapter = setAnimationPivot(holder.itemView, posi, lastPositionAdapter, context);
        holder.itemView.setSelected(selectedPos == position);
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

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void removeAt(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }

    public void filterList(ArrayList<LabelProgramModel> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }

    public void refrescarList(List<LabelProgramModel> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_campo;
        Spinner spCampo;

        ViewHolder(View itemView) {
            super(itemView);
            tv_campo = itemView.findViewById(R.id.tv_campo);
            spCampo = itemView.findViewById(R.id.spCampo);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);
        }
    }
}