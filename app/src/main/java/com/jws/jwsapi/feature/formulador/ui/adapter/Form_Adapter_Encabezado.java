package com.jws.jwsapi.feature.formulador.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.helpers.AdapterHelper;

import java.util.List;

public class Form_Adapter_Encabezado extends RecyclerView.Adapter<Form_Adapter_Encabezado.ViewHolder> {

    private int selectedPos = RecyclerView.NO_POSITION;
    private List<String> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int lastPositionAdapter = -1;
    private Context context;

    public Form_Adapter_Encabezado(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context=context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_encabezado, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String animal = mData.get(position);
        String[]arr= animal.split("_");
        if(arr.length==3){
            holder.myTextView.setText(arr[1].replace("_",""));
            holder.tvdes.setText(arr[2].replace("_",""));
        }else{
            holder.myTextView.setText(mData.get(position));
            holder.tvdes.setText("");
            holder.tvdes.setVisibility(View.GONE);
        }
        if (selectedPos == position){
            holder.itemView.setBackgroundResource(R.drawable.botoneraprincipal);
            holder.myTextView.setTextColor(Color.WHITE);
            holder.imageView8.setVisibility(View.VISIBLE);
            holder.tvdes.setTextColor(Color.WHITE);
        }
        else{
            holder.itemView.setBackgroundResource(R.drawable.fondoinfoprincipal);
            holder.imageView8.setVisibility(View.GONE);
            holder.tvdes.setTextColor(Color.DKGRAY);
            holder.myTextView.setTextColor(Color.DKGRAY);

        }
        lastPositionAdapter= AdapterHelper.setAnimationPivot(holder.itemView,position,lastPositionAdapter,context);
        holder.itemView.setSelected(selectedPos == position);
    }

    @Override
    public int getItemCount() {return mData.size();}

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView,tvdes;
        ImageView imageView8;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvAnimalName2);
            tvdes = itemView.findViewById(R.id.tvdes);
            imageView8= itemView.findViewById(R.id.imageView8);
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

    public String getItem(int id) {return mData.get(id);}
    public void setClickListener(ItemClickListener itemClickListener) {this.mClickListener = itemClickListener;}

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public void removeAt(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }
    public void filterList(List<String> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }
}