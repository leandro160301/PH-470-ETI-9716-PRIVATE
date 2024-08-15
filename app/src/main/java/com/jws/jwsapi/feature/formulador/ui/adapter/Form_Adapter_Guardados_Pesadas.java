package com.jws.jwsapi.feature.formulador.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.feature.formulador.models.Form_Model_PesadasDB;
import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.helpers.AdapterHelper;

import java.util.ArrayList;
import java.util.List;

public class Form_Adapter_Guardados_Pesadas extends RecyclerView.Adapter<Form_Adapter_Guardados_Pesadas.ViewHolder> {


    private int selectedPos = RecyclerView.NO_POSITION;
    private List<Form_Model_PesadasDB> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private boolean permitirClic = true;
    private int lastPositionAdapter = -1;
    Context context;
    // data is passed into the constructor
    public Form_Adapter_Guardados_Pesadas(Context context, List<Form_Model_PesadasDB> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context=context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.prog_formulador_adapter_guardados_pesadas, parent, false);
        return new ViewHolder(view);
    }
    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        holder.tv_lote.setText(mData.get(position).getLote());
        holder.tv_neto.setText(mData.get(position).getNeto());
        holder.tv_bruto.setText(mData.get(position).getBruto());
        holder.tv_tara.setText(mData.get(position).getTara());
        holder.tv_fecha.setText(mData.get(position).getFecha());
        holder.tv_hora.setText(mData.get(position).getHora());
        holder.tv_vencimiento.setText(mData.get(position).getVencimiento());
        holder.tv_codigoingrediente.setText(mData.get(position).getCodigoIngrediente());
        holder.tv_codigoreceta.setText(mData.get(position).getCodigoReceta());
        holder.tv_descripcionreceta.setText(mData.get(position).getDescripcionReceta());
        holder.tv_descripcioningrediente.setText(mData.get(position).getDescripcionIngrediente());
        holder.tv_idpedido.setText(mData.get(position).getIdPedido());
        holder.tv_idreceta.setText(mData.get(position).getIdReceta());
        holder.tv_idpesada.setText(String.valueOf(mData.get(position).getId()));
        holder.tv_operador.setText(mData.get(position).getOperador());
        holder.tv_setpoint.setText(mData.get(position).getSetPoint());
        holder.tv_turno.setText(mData.get(position).getTurno());
        holder.tv_reales.setText(mData.get(position).getReales());
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

        if (selectedPos == position) {
            holder.itemView.setBackgroundResource(R.drawable.fondolineainferiornegratranslucido);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.fondolineainferiornegra2);
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
        TextView tv_codigoingrediente,tv_neto,tv_bruto,tv_tara,tv_fecha,tv_hora, tv_lote,tv_vencimiento,tv_turno,
                tv_operador,tv_setpoint,tv_reales,
                tv_descripcioningrediente,tv_codigoreceta,tv_descripcionreceta,tv_idpesada,tv_idreceta,tv_idpedido;
        TextView tv_campo1,tv_campo2,tv_campo3,tv_campo4,tv_campo5;
        TextView tv_campo1valor,tv_campo2valor,tv_campo3valor,tv_campo4valor,tv_campo5valor;

        ViewHolder(View itemView) {
            super(itemView);

            tv_codigoingrediente= itemView.findViewById(R.id.tv_codigoingrediente);
            tv_descripcioningrediente= itemView.findViewById(R.id.tv_descripcioningrediente);
            tv_turno=itemView.findViewById(R.id.tv_turno);
            tv_codigoreceta= itemView.findViewById(R.id.tv_codigoreceta);
            tv_descripcionreceta= itemView.findViewById(R.id.tv_descripcionreceta);
            tv_idpesada= itemView.findViewById(R.id.tv_id);
            tv_idreceta= itemView.findViewById(R.id.tv_idreceta);
            tv_idpedido= itemView.findViewById(R.id.tv_idpedido);
            tv_neto= itemView.findViewById(R.id.tv_neto);
            tv_bruto= itemView.findViewById(R.id.tv_bruto);
            tv_tara= itemView.findViewById(R.id.tv_tara);
            tv_fecha= itemView.findViewById(R.id.tv_fecha);
            tv_hora= itemView.findViewById(R.id.tv_hora);
            tv_lote = itemView.findViewById(R.id.tv_lote);
            tv_vencimiento= itemView.findViewById(R.id.tv_vencimiento);
            tv_operador= itemView.findViewById(R.id.tv_operador);
            tv_setpoint= itemView.findViewById(R.id.tv_setpoint);
            tv_reales= itemView.findViewById(R.id.tv_reales);
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

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
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
    public void filterList(ArrayList<Form_Model_PesadasDB> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }
}