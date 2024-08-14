package com.jws.jwsapi.feature.formulador.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.feature.formulador.models.Form_Model_Ingredientes;
import com.jws.jwsapi.R;
import com.jws.jwsapi.feature.formulador.ui.interfaces.AdapterIngredientesInterface;
import java.util.ArrayList;
import java.util.List;

public class Form_Adapter_Ingredientes extends RecyclerView.Adapter<Form_Adapter_Ingredientes.ViewHolder> {

    private int selectedPos = RecyclerView.NO_POSITION;
    private List<Form_Model_Ingredientes> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int lastPosition = -1;
    Boolean altaybaja;
    AdapterIngredientesInterface ingredientesInterface;
    Context context;

    public Form_Adapter_Ingredientes(Context context, List<Form_Model_Ingredientes> data,Boolean altaybaja,AdapterIngredientesInterface ingredientesInterface) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context=context;
        this.altaybaja = altaybaja;
        this.ingredientesInterface=ingredientesInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.prog_formulador_adapter_ingredientes, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        int posi=position;
        holder.tv_codigo.setText(mData.get(position).getCodigo());
        holder.tv_nombre.setText(mData.get(position).getNombre());
        if(!altaybaja){
            holder.ln_eliminar.setVisibility(View.GONE);
            holder.ln_print.setVisibility(View.GONE);
        }
        holder.ln_eliminar.setOnClickListener(view -> {
            ingredientesInterface.eliminarIngrediente(mData,posi);
        });
        holder.ln_print.setOnClickListener(view -> {
            ingredientesInterface.imprimirEtiqueta(mData.get(posi).getCodigo(),mData.get(posi).getNombre());

        });
        setAnimation(holder.itemView, position);
        holder.itemView.setSelected(selectedPos == position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(context, R.anim.pivot);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }



    // total number of rows
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
        TextView tv_nombre,tv_codigo;
        LinearLayout ln_eliminar,ln_print;


        ViewHolder(View itemView) {
            super(itemView);
            tv_nombre = itemView.findViewById(R.id.tv_descripcioningrediente);
            tv_codigo = itemView.findViewById(R.id.tv_codigoingrediente);
            ln_eliminar = itemView.findViewById(R.id.ln_editar);
            ln_print=itemView.findViewById(R.id.ln_print);
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
    public void filterList(ArrayList<Form_Model_Ingredientes> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }
    public void refrescarList(List<Form_Model_Ingredientes> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }
}