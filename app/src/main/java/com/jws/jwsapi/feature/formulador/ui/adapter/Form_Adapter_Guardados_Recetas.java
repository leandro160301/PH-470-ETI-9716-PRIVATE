package com.jws.jwsapi.feature.formulador.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.feature.formulador.models.Form_Model_RecetaDB;
import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.helpers.AdapterHelper;

import java.util.ArrayList;
import java.util.List;

public class Form_Adapter_Guardados_Recetas extends RecyclerView.Adapter<Form_Adapter_Guardados_Recetas.ViewHolder> {

    private int selectedPos = RecyclerView.NO_POSITION;
    private List<Form_Model_RecetaDB> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private ButtonClickListener mButtonClickListener;
    private boolean permitirClic = true;
    private int lastPositionAdapter = -1;
    Context context;
    // data is passed into the constructor
    public Form_Adapter_Guardados_Recetas(Context context, List<Form_Model_RecetaDB> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context=context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.prog_formulador_adapter_guardados_recetas, parent, false);
        return new ViewHolder(view);
    }
    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tv_descripcion.setText(mData.get(position).getDescripcionReceta());
        holder.tv_codigo.setText(mData.get(position).getCodigoReceta());
        holder.tv_lote.setText(mData.get(position).getLote());
        holder.tv_neto.setText(mData.get(position).getNeto());
        holder.tv_fecha.setText(mData.get(position).getFecha());
        holder.tv_hora.setText(mData.get(position).getHora());
        holder.tv_vencimiento.setText(mData.get(position).getVencimiento());
        holder.tv_bruto.setText(mData.get(position).getBruto());
        holder.tv_id.setText(String.valueOf(mData.get(position).getId()));
        holder.tv_tara.setText(mData.get(position).getTara());
        holder.tv_kilosaproducir.setText(mData.get(position).getKilosAProducir());
        holder.tv_turno.setText(mData.get(position).getTurno());
        holder.tv_campo1.setText(mData.get(position).getCampo1());
        holder.tv_campo2.setText(mData.get(position).getCampo2());
        holder.tv_campo3.setText(mData.get(position).getCampo3());
        holder.tv_campo4.setText(mData.get(position).getCampo4());
        holder.tv_campo5.setText(mData.get(position).getCampo5());
        holder.tv_campo1valor.setText(mData.get(position).getCampo1Valor());
        holder.tv_campo2valor.setText(mData.get(position).getCampo2Valor());
        holder.tv_campo3valor.setText(mData.get(position).getCampo3Valor());
        holder.tv_campo4valor.setText(mData.get(position).getCampo4Valor());
        holder.tv_campo5valor.setText(mData.get(position).getCampo5Valor());
        holder.tv_operador.setText(mData.get(position).getOperador());

        holder.bt_editar.setOnClickListener(view -> {
            if(holder.ln_expandir.getVisibility()==View.GONE){
                holder.ln_expandir.setVisibility(View.VISIBLE);
                holder.bt_editar.setBackgroundResource(R.drawable.boton_menos_i);
            }else{
                holder.ln_expandir.setVisibility(View.GONE);
                holder.bt_editar.setBackgroundResource(R.drawable.boton_mas_i);
            }

        });
        holder.bt_pasos.setOnClickListener(view -> {
            if (mButtonClickListener != null) {
                mButtonClickListener.onButtonClick(view, position);
            }
        });
        if (selectedPos == position) {
            //holder.itemView.setBackgroundResource(R.drawable.fondolineainferiornegratranslucido);
        } else {
           // holder.itemView.setBackgroundResource(R.drawable.fondolineainferiornegra2);
        }

        lastPositionAdapter= AdapterHelper.setAnimationPivot(holder.itemView,position,lastPositionAdapter,context);
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
        TextView  tv_codigo,tv_neto,tv_fecha,tv_hora, tv_lote, tv_descripcion,tv_vencimiento;
        TextView tv_id,tv_turno,tv_kilosaproducir,tv_operador,tv_bruto,tv_tara,tv_campo1,tv_campo2,tv_campo3,tv_campo4,tv_campo5;
        TextView tv_campo1valor,tv_campo2valor,tv_campo3valor,tv_campo4valor,tv_campo5valor;
        LinearLayout ln_expandir;
        Button bt_editar,bt_pasos;

        ViewHolder(View itemView) {
            super(itemView);

            tv_lote = itemView.findViewById(R.id.tv_lote);
            tv_codigo= itemView.findViewById(R.id.tv_codigoingrediente);
            tv_neto= itemView.findViewById(R.id.tv_neto);
            tv_fecha= itemView.findViewById(R.id.tv_fecha);
            tv_hora= itemView.findViewById(R.id.tv_hora);
            tv_descripcion = itemView.findViewById(R.id.tv_descripcioningrediente);
            tv_vencimiento= itemView.findViewById(R.id.tv_vencimiento);
            tv_id= itemView.findViewById(R.id.tv_id);
            tv_turno= itemView.findViewById(R.id.tv_turno);
            tv_kilosaproducir= itemView.findViewById(R.id.tv_setpoint);
            tv_operador= itemView.findViewById(R.id.tv_operador);
            tv_bruto= itemView.findViewById(R.id.tv_bruto);
            tv_tara= itemView.findViewById(R.id.tv_tara);
            tv_campo1= itemView.findViewById(R.id.tv_campo1);
            tv_campo2= itemView.findViewById(R.id.tv_campo2);
            tv_campo3= itemView.findViewById(R.id.tv_campo3);
            tv_campo4= itemView.findViewById(R.id.tv_campo4);
            tv_campo5= itemView.findViewById(R.id.tv_campo5);
            tv_campo1valor= itemView.findViewById(R.id.tv_campo1valor);
            tv_campo2valor= itemView.findViewById(R.id.tv_campo2valor);
            tv_campo3valor= itemView.findViewById(R.id.tv_campo3valor);
            tv_campo4valor= itemView.findViewById(R.id.tv_campo4valor);
            tv_campo5valor= itemView.findViewById(R.id.tv_campo5valor);
            ln_expandir= itemView.findViewById(R.id.ln_expandir);
            bt_editar= itemView.findViewById(R.id.bt_editar);
            bt_pasos= itemView.findViewById(R.id.bt_pasos);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            if(permitirClic){
                if (mClickListener != null)
                    mClickListener.onItemClick(view, getAdapterPosition());
                notifyItemChanged(selectedPos);
                selectedPos = getLayoutPosition();
                notifyItemChanged(selectedPos);
                permitirClic = false;
                view.postDelayed(() -> permitirClic = true, 2000);
            }

        }
    }

    // convenience method for getting data at click position
    //public String getItem(int id) {return mData.get(id);}

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }
    public interface ButtonClickListener {
        void onButtonClick(View view, int position);
    }
    public void setButtonClickListener(ButtonClickListener buttonClickListener) {
        this.mButtonClickListener = buttonClickListener;
    }
    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public void removeAt(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }
    public void filterList(ArrayList<Form_Model_RecetaDB> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }
}