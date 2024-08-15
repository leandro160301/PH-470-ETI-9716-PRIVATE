package com.jws.jwsapi.base.ui.adapters;

import static com.jws.jwsapi.utils.helpers.AdapterHelper.setAnimationPivot;
import static com.jws.jwsapi.utils.helpers.SpinnerHelper.configurarSpinner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.R;
import com.jws.jwsapi.base.models.EtiquetasDeProgramaModel;
import com.jws.jwsapi.feature.formulador.data.preferences.PreferencesManager;
import java.util.ArrayList;
import java.util.List;

public class AdapterEtiquetasDePrograma extends RecyclerView.Adapter<AdapterEtiquetasDePrograma.ViewHolder> {

    private int selectedPos = RecyclerView.NO_POSITION;
    private List<EtiquetasDeProgramaModel> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int lastPositionAdapter = -1;
    List<String>etiquetas ;
    Context context;
    PreferencesManager preferencesManager;

    public AdapterEtiquetasDePrograma(Context context, List<EtiquetasDeProgramaModel> data, List<String>etiquetas) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.etiquetas= etiquetas;
        this.context=context;
        this.preferencesManager= new PreferencesManager(context);

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.standar_adapter_etiquetadeprograma, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int posi=position;
        holder.tv_campo.setText(mData.get(position).nombrecampo);
        configurarSpinner(holder.spCampo,context,etiquetas);
        int index= etiquetas.indexOf(preferencesManager.getEtiqueta(posi));
        if (index>-1){
            holder.spCampo.setSelection(index);
        }
        holder.spCampo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                preferencesManager.setEtiqueta(etiquetas.get(i),posi);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });
        lastPositionAdapter =setAnimationPivot(holder.itemView,posi, lastPositionAdapter,context);
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
    public int getItemViewType(int position)
    {
        return position;
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

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public void removeAt(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }
    public void filterList(ArrayList<EtiquetasDeProgramaModel> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }
    public void refrescarList(List<EtiquetasDeProgramaModel> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }
}