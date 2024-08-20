package com.jws.jwsapi.feature.formulador.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.databinding.ProgFormuladorAdapterGuardadosRecetasBinding;
import com.jws.jwsapi.feature.formulador.models.FormModelRecetaDB;
import com.jws.jwsapi.R;
import com.jws.jwsapi.helpers.AdapterHelper;
import java.util.ArrayList;
import java.util.List;

public class FormAdapterGuardadosRecetas extends RecyclerView.Adapter<FormAdapterGuardadosRecetas.ViewHolder> {

    private int selectedPos = RecyclerView.NO_POSITION;
    private List<FormModelRecetaDB> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ButtonClickListener mButtonClickListener;
    private boolean permitirClic = true;
    private int lastPositionAdapter = -1;
    Context context;
    // data is passed into the constructor
    public FormAdapterGuardadosRecetas(Context context, List<FormModelRecetaDB> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ProgFormuladorAdapterGuardadosRecetasBinding binding = ProgFormuladorAdapterGuardadosRecetasBinding.inflate(mInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position){
        FormModelRecetaDB receta = mData.get(position);

        holder.binding.tvDescripcioningrediente.setText(receta.getDescripcionReceta());
        holder.binding.tvCodigoingrediente.setText(receta.getCodigoReceta());
        holder.binding.tvLote.setText(receta.getLote());
        holder.binding.tvNeto.setText(receta.getNeto());
        holder.binding.tvFecha.setText(receta.getFecha());
        holder.binding.tvHora.setText(receta.getHora());
        holder.binding.tvVencimiento.setText(receta.getVencimiento());
        holder.binding.tvBruto.setText(receta.getBruto());
        holder.binding.tvId.setText(String.valueOf(receta.getId()));
        holder.binding.tvTara.setText(receta.getTara());
        holder.binding.tvSetpoint.setText(receta.getKilosAProducir());
        holder.binding.tvTurno.setText(receta.getTurno());
        holder.binding.tvCampo1.setText(receta.getCampo1());
        holder.binding.tvCampo2.setText(receta.getCampo2());
        holder.binding.tvCampo3.setText(receta.getCampo3());
        holder.binding.tvCampo4.setText(receta.getCampo4());
        holder.binding.tvCampo5.setText(receta.getCampo5());
        holder.binding.tvCampo1valor.setText(receta.getCampo1Valor());
        holder.binding.tvCampo2valor.setText(receta.getCampo2Valor());
        holder.binding.tvCampo3valor.setText(receta.getCampo3Valor());
        holder.binding.tvCampo4valor.setText(receta.getCampo4Valor());
        holder.binding.tvCampo5valor.setText(receta.getCampo5Valor());
        holder.binding.tvOperador.setText(receta.getOperador());

        holder.binding.btEditar.setOnClickListener(view -> {
            if (holder.binding.lnExpandir.getVisibility() == View.GONE) {
                holder.binding.lnExpandir.setVisibility(View.VISIBLE);
                holder.binding.btEditar.setBackgroundResource(R.drawable.boton_menos_i);
            } else {
                holder.binding.lnExpandir.setVisibility(View.GONE);
                holder.binding.btEditar.setBackgroundResource(R.drawable.boton_mas_i);
            }
        });

        holder.binding.btPasos.setOnClickListener(view -> {
            if (mButtonClickListener != null) {
                mButtonClickListener.onButtonClick(view, position);
            }
        });

        lastPositionAdapter = AdapterHelper.setAnimationPivot(holder.itemView, position, lastPositionAdapter, context);
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


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ProgFormuladorAdapterGuardadosRecetasBinding binding;

        ViewHolder(ProgFormuladorAdapterGuardadosRecetasBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (permitirClic) {
                if (mClickListener != null) {
                    mClickListener.onItemClick(view, getAdapterPosition());
                }
                notifyItemChanged(selectedPos);
                selectedPos = getLayoutPosition();
                notifyItemChanged(selectedPos);
                permitirClic = false;
                view.postDelayed(() -> permitirClic = true, 2000);
            }
        }
    }
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
    public interface ButtonClickListener {
        void onButtonClick(View view, int position);
    }
    public void setButtonClickListener(ButtonClickListener buttonClickListener) {
        this.mButtonClickListener = buttonClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public void removeAt(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }
    public void filterList(ArrayList<FormModelRecetaDB> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }
}