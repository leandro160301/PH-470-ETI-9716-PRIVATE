package com.jws.jwsapi.feature.formulador.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.databinding.ProgFormuladorAdapterIngredientesBinding;
import com.jws.jwsapi.feature.formulador.models.FormModelIngredientes;
import com.jws.jwsapi.helpers.AdapterHelper;
import com.jws.jwsapi.feature.formulador.ui.interfaces.AdapterIngredientesInterface;
import java.util.ArrayList;
import java.util.List;

public class FormAdapterIngredientes extends RecyclerView.Adapter<FormAdapterIngredientes.ViewHolder> {

    private int selectedPos = RecyclerView.NO_POSITION;
    private List<FormModelIngredientes> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int lastPositionAdapter = -1;
    Boolean altayBaja;
    AdapterIngredientesInterface ingredientesInterface;
    Context context;

    public FormAdapterIngredientes(Context context, List<FormModelIngredientes> data, Boolean altaybaja, AdapterIngredientesInterface ingredientesInterface) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.altayBaja = altaybaja;
        this.ingredientesInterface = ingredientesInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ProgFormuladorAdapterIngredientesBinding binding = ProgFormuladorAdapterIngredientesBinding.inflate(mInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FormModelIngredientes item = mData.get(position);
        holder.binding.tvCodigoingrediente.setText(item.getCodigo());
        holder.binding.tvDescripcioningrediente.setText(item.getNombre());
        if(item.getSalida()==0){
            holder.binding.tvSalida.setText("MANUAL");
        }else{
            holder.binding.tvSalida.setText("SALIDA "+ item.getSalida());
        }

        if (!altayBaja) {
            holder.binding.lnEditar.setVisibility(View.GONE);
            holder.binding.lnPrint.setVisibility(View.GONE);
        }
        holder.binding.lnEditar.setOnClickListener(view -> {
            ingredientesInterface.eliminarIngrediente(mData, position);
        });
        holder.binding.lnPrint.setOnClickListener(view -> {
            ingredientesInterface.imprimirEtiqueta(item.getCodigo(), item.getNombre());
        });
        lastPositionAdapter = AdapterHelper.setAnimationPivot(holder.itemView, position, lastPositionAdapter, context);
        holder.itemView.setSelected(selectedPos == position);
    }

    @Override
    public int getItemCount() {return mData.size();}

    @Override
    public long getItemId(int position) {return position;}

    @Override
    public int getItemViewType(int position) {return position;}

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ProgFormuladorAdapterIngredientesBinding binding;

        ViewHolder(ProgFormuladorAdapterIngredientesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
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

    public void filterList(ArrayList<FormModelIngredientes> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }

    public void refrescarList(List<FormModelIngredientes> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }
}