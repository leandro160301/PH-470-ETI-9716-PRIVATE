package com.jws.jwsapi.feature.formulador.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.databinding.ProgFormuladorAdapterRecetaBinding;
import com.jws.jwsapi.feature.formulador.di.RecetaManager;
import com.jws.jwsapi.feature.formulador.models.FormModelReceta;
import com.jws.jwsapi.R;
import com.jws.jwsapi.helpers.AdapterHelper;
import com.jws.jwsapi.feature.formulador.ui.interfaces.AdapterRecetasInterface;
import java.util.List;
import java.util.Objects;

public class FormAdapterRecetas extends RecyclerView.Adapter<FormAdapterRecetas.ViewHolder> {

    RecetaManager recetaManager;
    private final int selectedPos = RecyclerView.NO_POSITION;
    private List<FormModelReceta> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;
    public String recetaelegida = "";
    private int lastPositionAdapter = -1;
    AdapterRecetasInterface recetasInterface;
    String unidad;

    public FormAdapterRecetas(Context context, List<FormModelReceta> data, String recetaelegida, RecetaManager recetaManager, AdapterRecetasInterface recetasInterface, String unidad) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.recetaelegida = recetaelegida;
        this.recetaManager = recetaManager;
        this.recetasInterface = recetasInterface;
        this.context = context;
        this.unidad = unidad;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ProgFormuladorAdapterRecetaBinding binding = ProgFormuladorAdapterRecetaBinding.inflate(mInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FormModelReceta item = mData.get(position);
        holder.binding.tvCodigoingrediente.setText(item.getCodigoIng());
        holder.binding.tvDescripcioningrediente.setText(item.getDescIng());
        holder.binding.tvKilos.setText(item.getKilosIng() + unidad);
        holder.binding.tvPaso.setText(String.valueOf(position + 1));
        holder.binding.tvTolerancia.setText(item.getToleranciaIng());

        if (isEjecutando()) {
            holder.binding.lnEditar.setVisibility(View.GONE);
            holder.binding.lnBorrar.setVisibility(View.GONE);
            holder.binding.lnAgregar.setVisibility(View.GONE);
        } else {
            holder.binding.imCampo.setVisibility(View.GONE);
            holder.binding.tvPesoreal.setVisibility(View.GONE);
            holder.binding.tvTolerancia.setVisibility(View.GONE);
        }

        holder.binding.lnEditar.setOnClickListener(view -> recetasInterface.modificarPaso(mData, position));
        holder.binding.lnBorrar.setOnClickListener(view -> recetasInterface.eliminarPaso(mData, position));
        holder.binding.lnAgregar.setOnClickListener(view -> recetasInterface.agregarPaso(mData, position));

        if (Objects.equals(item.getKilosRealesIng(), "NO")) {
            if (isEjecutando()) holder.binding.imCampo.setBackgroundResource(R.drawable.unchecked);
            holder.binding.tvPesoreal.setVisibility(View.GONE);
        } else {
            if (isEjecutando()) holder.binding.imCampo.setBackgroundResource(R.drawable.checked);
            holder.binding.tvPesoreal.setText(item.getKilosRealesIng() + unidad);
        }

        if (selectedPos == position) {
            holder.itemView.setBackgroundResource(R.drawable.fondolineainferiornegratranslucido);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.fondolineainferiornegra2);
        }

        lastPositionAdapter = AdapterHelper.setAnimationSlideInLeft(holder.itemView, position, lastPositionAdapter, context);
        holder.itemView.setSelected(selectedPos == position);
    }
    private boolean isEjecutando() {
        return recetaManager.ejecutando.getValue() != null && recetaManager.ejecutando.getValue();
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ProgFormuladorAdapterRecetaBinding binding;

        ViewHolder(ProgFormuladorAdapterRecetaBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
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

    public void filterList(List<FormModelReceta> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }

    public void refrescarList(List<FormModelReceta> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }

}