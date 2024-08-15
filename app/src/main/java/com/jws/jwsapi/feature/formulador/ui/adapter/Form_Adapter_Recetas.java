package com.jws.jwsapi.feature.formulador.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.base.activities.MainActivity;
import com.jws.jwsapi.feature.formulador.di.RecetaManager;
import com.jws.jwsapi.feature.formulador.models.Form_Model_Receta;
import com.jws.jwsapi.R;
import com.jws.jwsapi.feature.formulador.ui.interfaces.AdapterRecetasInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Form_Adapter_Recetas extends RecyclerView.Adapter<Form_Adapter_Recetas.ViewHolder> {

    RecetaManager recetaManager;
    private final int selectedPos = RecyclerView.NO_POSITION;
    private List<Form_Model_Receta> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    Context context;
    public String recetaelegida = "";
    private int lastPositionAdapter = -1;
    AdapterRecetasInterface recetasInterface;
    String unidad;

    // data is passed into the constructor
    public Form_Adapter_Recetas(Context context, List<Form_Model_Receta> data, String recetaelegida,RecetaManager recetaManager,AdapterRecetasInterface recetasInterface,String unidad) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.recetaelegida = recetaelegida;
        this.recetaManager = recetaManager;
        this.recetasInterface=recetasInterface;
        this.context=context;
        this.unidad=unidad;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.prog_formulador_adapter_receta, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //ExampleItem currentItem = mExampleList.get(position);
        int posi = position;
        holder.tv_codigo.setText(mData.get(position).getCodigo_ing());
        holder.tv_nombre.setText(mData.get(position).getDescrip_ing());
        holder.tv_kilos.setText(mData.get(position).getKilos_ing() +unidad);
        holder.tv_paso.setText(String.valueOf(position + 1));
        holder.tv_tolerancia.setText(mData.get(position).getTolerancia_ing());
        if (recetaManager.ejecutando) {
            holder.ln_editar.setVisibility(View.GONE);
            holder.ln_borrar.setVisibility(View.GONE);
            holder.ln_agregar.setVisibility(View.GONE);
        }else{
            holder.im_campo.setVisibility(View.GONE);
            holder.tv_pesoreal.setVisibility(View.GONE);
            holder.tv_tolerancia.setVisibility(View.GONE);
        }
        holder.ln_editar.setOnClickListener(view -> recetasInterface.modificarPaso(mData,posi));
        holder.ln_borrar.setOnClickListener(view -> recetasInterface.eliminarPaso(mData,posi));
        holder.ln_agregar.setOnClickListener(view -> recetasInterface.agregarPaso(mData,posi));
        if (Objects.equals(mData.get(position).getKilos_reales_ing(), "NO")) {
            if (recetaManager.ejecutando) {
                holder.im_campo.setBackgroundResource(R.drawable.unchecked);
            }
            holder.tv_pesoreal.setVisibility(View.GONE);
        } else {
            if (recetaManager.ejecutando) {
                holder.im_campo.setBackgroundResource(R.drawable.checked);
            }
            holder.tv_pesoreal.setText(mData.get(position).getKilos_reales_ing() + unidad);
        }
        if (selectedPos == position) {
            holder.itemView.setBackgroundResource(R.drawable.fondolineainferiornegratranslucido);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.fondolineainferiornegra2);
        }
        setAnimation(holder.itemView, position);
        holder.itemView.setSelected(selectedPos == position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        if (position > lastPositionAdapter) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPositionAdapter = position;
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

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_nombre, tv_codigo, tv_kilos, tv_pesoreal, tv_paso, tv_tolerancia;
        ImageView im_campo;
        LinearLayout ln_editar, ln_borrar, ln_agregar;

        ViewHolder(View itemView) {
            super(itemView);
            tv_nombre = itemView.findViewById(R.id.tv_descripcioningrediente);
            tv_codigo = itemView.findViewById(R.id.tv_codigoingrediente);
            tv_kilos = itemView.findViewById(R.id.tv_kilos);
            tv_pesoreal = itemView.findViewById(R.id.tv_pesoreal);
            im_campo = itemView.findViewById(R.id.im_campo);
            tv_paso = itemView.findViewById(R.id.tv_paso);
            tv_tolerancia = itemView.findViewById(R.id.tv_tolerancia);
            ln_editar = itemView.findViewById(R.id.ln_editar);
            ln_borrar = itemView.findViewById(R.id.ln_borrar);
            ln_agregar = itemView.findViewById(R.id.ln_agregar);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            /*if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);*/
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

    public void filterList(ArrayList<Form_Model_Receta> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }

    public void refrescarList(List<Form_Model_Receta> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }
}