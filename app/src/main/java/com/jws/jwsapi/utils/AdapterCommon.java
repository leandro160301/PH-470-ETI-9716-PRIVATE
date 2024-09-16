package com.jws.jwsapi.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.R;

import java.util.List;

public class AdapterCommon extends RecyclerView.Adapter<AdapterCommon.ViewHolder> {

    private int selectedPos = RecyclerView.NO_POSITION;
    private List<String> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int lastPositionAdapter = -1;
    Context context;

    public AdapterCommon(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context= context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_list_black_tex2t, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String animal = mData.get(position);
        holder.myTextView.setText(animal);
        if (selectedPos == position){
            holder.itemView.setBackgroundResource(R.drawable.botoneraprincipal);
            holder.myTextView.setTextColor(Color.WHITE);
        }
        else{
            holder.itemView.setBackgroundResource(R.drawable.fondoinfoprincipal);
            holder.myTextView.setTextColor(Color.DKGRAY);
        }
        lastPositionAdapter= AdapterHelper.setAnimationPivot(holder.itemView,position,lastPositionAdapter,context);
        holder.itemView.setSelected(selectedPos == position);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView myTextView;

        ViewHolder(View itemView) {
            super(itemView);
            myTextView = itemView.findViewById(R.id.tvAnimalName);
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


    public String getItem(int id) {
        return mData.get(id);
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
    public void filterList(List<String> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }
}