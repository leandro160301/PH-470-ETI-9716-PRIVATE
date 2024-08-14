package com.jws.jwsapi.base.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.R;
import com.jws.jwsapi.base.models.StatsModel;

import java.util.List;
import java.util.Objects;

public class MyRecyclerViewAdapterStats extends RecyclerView.Adapter<MyRecyclerViewAdapterStats.ViewHolder> {


    private int selectedPos = RecyclerView.NO_POSITION;
    private final List<StatsModel> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public MyRecyclerViewAdapterStats(Context context, List<StatsModel> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_estadisticas_recycler, parent, false);






        return new ViewHolder(view);
    }
    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //ExampleItem currentItem = mExampleList.get(position);

        if(Objects.equals(mData.get(position).Rango, "ALTO")){
            holder.card_view.setCardBackgroundColor(Color.RED);
            holder.tv_Tolerancia.setTextColor(Color.WHITE);
            holder.tv_Neto.setTextColor(Color.WHITE);
            holder.tv_Bruto.setTextColor(Color.WHITE);
            holder.tv_Tara.setTextColor(Color.WHITE);
            holder.tv_TaraDigital.setTextColor(Color.WHITE);
            holder.tv_Contenedor.setTextColor(Color.WHITE);
            holder.tv_Transporte.setTextColor(Color.WHITE);
            holder.tv_Destinatario.setTextColor(Color.WHITE);
            holder.tv_Fecha.setTextColor(Color.WHITE);
            holder.tv_Hora.setTextColor(Color.WHITE);
            holder.tv_PesoTeorico.setTextColor(Color.WHITE);
            holder.tv_11.setTextColor(Color.WHITE);
            holder.tv_22.setTextColor(Color.WHITE);
            holder.tv_33.setTextColor(Color.WHITE);
            holder.tv_44.setTextColor(Color.WHITE);
            holder.tv_55.setTextColor(Color.WHITE);
            holder.tv_66.setTextColor(Color.WHITE);
            holder.tv_77.setTextColor(Color.WHITE);
            holder.tv_88.setTextColor(Color.WHITE);
            holder.tv_99.setTextColor(Color.WHITE);
            holder.tv_100.setTextColor(Color.WHITE);
            holder.tv_110.setTextColor(Color.WHITE);

        }
        else if(Objects.equals(mData.get(position).Rango, "BAJO")){
            holder.card_view.setCardBackgroundColor(Color.RED);
            holder.tv_Tolerancia.setTextColor(Color.WHITE);
            holder.tv_Neto.setTextColor(Color.WHITE);
            holder.tv_Bruto.setTextColor(Color.WHITE);
            holder.tv_Tara.setTextColor(Color.WHITE);
            holder.tv_TaraDigital.setTextColor(Color.WHITE);
            holder.tv_Contenedor.setTextColor(Color.WHITE);
            holder.tv_Transporte.setTextColor(Color.WHITE);
            holder.tv_Destinatario.setTextColor(Color.WHITE);
            holder.tv_Fecha.setTextColor(Color.WHITE);
            holder.tv_Hora.setTextColor(Color.WHITE);
            holder.tv_PesoTeorico.setTextColor(Color.WHITE);
            holder.tv_11.setTextColor(Color.WHITE);
            holder.tv_22.setTextColor(Color.WHITE);
            holder.tv_33.setTextColor(Color.WHITE);
            holder.tv_44.setTextColor(Color.WHITE);
            holder.tv_55.setTextColor(Color.WHITE);
            holder.tv_66.setTextColor(Color.WHITE);
            holder.tv_77.setTextColor(Color.WHITE);
            holder.tv_88.setTextColor(Color.WHITE);
            holder.tv_99.setTextColor(Color.WHITE);
            holder.tv_100.setTextColor(Color.WHITE);
            holder.tv_110.setTextColor(Color.WHITE);
        }
        else if (Objects.equals(mData.get(position).Rango, "ACEPTO")){
            holder.card_view.setCardBackgroundColor(Color.WHITE);
            holder.tv_Tolerancia.setTextColor(Color.BLACK);
            holder.tv_Neto.setTextColor(Color.BLACK);
            holder.tv_Bruto.setTextColor(Color.BLACK);
            holder.tv_Tara.setTextColor(Color.BLACK);
            holder.tv_TaraDigital.setTextColor(Color.BLACK);
            holder.tv_Contenedor.setTextColor(Color.BLACK);
            holder.tv_Transporte.setTextColor(Color.BLACK);
            holder.tv_Destinatario.setTextColor(Color.BLACK);
            holder.tv_Fecha.setTextColor(Color.BLACK);
            holder.tv_Hora.setTextColor(Color.BLACK);
            holder.tv_PesoTeorico.setTextColor(Color.BLACK);
            holder.tv_11.setTextColor(Color.BLACK);
            holder.tv_22.setTextColor(Color.BLACK);
            holder.tv_33.setTextColor(Color.BLACK);
            holder.tv_44.setTextColor(Color.BLACK);
            holder.tv_55.setTextColor(Color.BLACK);
            holder.tv_66.setTextColor(Color.BLACK);
            holder.tv_77.setTextColor(Color.BLACK);
            holder.tv_88.setTextColor(Color.BLACK);
            holder.tv_99.setTextColor(Color.BLACK);
            holder.tv_100.setTextColor(Color.BLACK);
            holder.tv_110.setTextColor(Color.BLACK);
        }


        holder.tv_Tolerancia.setText(mData.get(position).Tolerancia);
        holder.tv_Neto.setText(mData.get(position).Neto);
        holder.tv_Bruto.setText(mData.get(position).Bruto);
        holder.tv_Tara.setText(mData.get(position).Tara);
        holder.tv_TaraDigital.setText(mData.get(position).TaraDigital);
        holder.tv_Contenedor.setText(mData.get(position).Contenedor);
        holder.tv_Transporte.setText(mData.get(position).Transporte);
        holder.tv_Destinatario.setText(mData.get(position).Destinatario);
        holder.tv_Fecha.setText(mData.get(position).Fecha);
        holder.tv_Hora.setText(mData.get(position).Hora);
        holder.tv_PesoTeorico.setText(mData.get(position).PesoTeorico);

        //holder.itemView.setBackgroundColor(Color.YELLOW);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_Tolerancia, tv_Neto,tv_Bruto,tv_Tara,tv_TaraDigital,tv_Contenedor,tv_Transporte,tv_Destinatario,tv_Fecha,tv_Hora,tv_PesoTeorico;
        TextView tv_11,tv_22,tv_33,tv_44,tv_55,tv_66,tv_77,tv_88,tv_99,tv_100,tv_110;
        CardView card_view;

        ViewHolder(View itemView) {
            super(itemView);
            tv_Tolerancia= itemView.findViewById(R.id.tvTolerancia);
            tv_Neto= itemView.findViewById(R.id.tvNeto);
            tv_Bruto= itemView.findViewById(R.id.tvBruto);
            tv_Tara= itemView.findViewById(R.id.tvTara);
            tv_TaraDigital= itemView.findViewById(R.id.tvTaradigital);
            tv_Contenedor= itemView.findViewById(R.id.tvContenedor);
            tv_Transporte= itemView.findViewById(R.id.tvTransporte);
            tv_Destinatario= itemView.findViewById(R.id.tvDestinatario);
            tv_Fecha= itemView.findViewById(R.id.tvfecha);
            tv_Hora= itemView.findViewById(R.id.tvhora);
            tv_Hora= itemView.findViewById(R.id.tvhora);
            tv_PesoTeorico= itemView.findViewById(R.id.tvPesoTeorico);
            card_view= itemView.findViewById(R.id.card_view);
            tv_11= itemView.findViewById(R.id.tv_11);
            tv_22= itemView.findViewById(R.id.tv_22);
            tv_33= itemView.findViewById(R.id.tv_33);
            tv_44= itemView.findViewById(R.id.tv_44);
            tv_55= itemView.findViewById(R.id.tv_55);
            tv_66= itemView.findViewById(R.id.tv_66);
            tv_77= itemView.findViewById(R.id.tv_77);
            tv_88= itemView.findViewById(R.id.tv_88);
            tv_99= itemView.findViewById(R.id.tv_99);
            tv_100= itemView.findViewById(R.id.tv_100);
            tv_110= itemView.findViewById(R.id.tv_110);


            itemView.setOnClickListener(this);

           /* itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        // run scale animation and make it bigger
                        Animation anim = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.scaleintv);
                        itemView.startAnimation(anim);
                        anim.setFillAfter(true);
                    } else {
                        // run scale animation and make it smaller
                        Animation anim = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.scaleouttv);
                        itemView.startAnimation(anim);
                        anim.setFillAfter(true);
                    }
                }
            });*/
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);
        }
    }

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mData.get(id).toString();
    }

    // allows clicks events to be caught
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

}