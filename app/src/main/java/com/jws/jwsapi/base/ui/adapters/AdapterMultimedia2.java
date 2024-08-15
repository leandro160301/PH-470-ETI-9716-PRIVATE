package com.jws.jwsapi.base.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.R;

import java.util.ArrayList;
import java.util.List;

public class AdapterMultimedia2 extends RecyclerView.Adapter<AdapterMultimedia2.ViewHolder> {


    private int selectedPos = RecyclerView.NO_POSITION;
    private List<String> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;

    // data is passed into the constructor
    public AdapterMultimedia2(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_list_black_tex2t, parent, false);

        return new ViewHolder(view);
    }
    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //ExampleItem currentItem = mExampleList.get(position);
        String animal = mData.get(position);
        holder.myTextView.setText(animal);

        if (selectedPos == position){
            // holder.itemView.setBackgroundColor(Color.parseColor("#FF2C2C"));
            holder.itemView.setBackgroundColor(Color.parseColor("#940000"));
            holder.myTextView.setTextColor(Color.WHITE);
            //Animation anim = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.scaleintv);
            //  holder.itemView.startAnimation(anim);
            // anim.setFillAfter(true);
        }
        else{
            holder.itemView.setBackgroundResource(R.drawable.fondoinfoprincipal);
            // holder.itemView.setBackgroundColor(Color.parseColor("#41FFFFFF"));
            holder.myTextView.setTextColor(Color.DKGRAY);
            // Animation anim = AnimationUtils.loadAnimation(holder.itemView.getContext(), R.anim.scaleouttv);
            //  holder.itemView.startAnimation(anim);
            // anim.setFillAfter(true);
        }

        //holder.itemView.setBackgroundColor(Color.YELLOW);
        holder.itemView.setSelected(selectedPos == position);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
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

    // convenience method for getting data at click position
    public String getItem(int id) {
        return mData.get(id);
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
    public void filterList(ArrayList<String> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }
}