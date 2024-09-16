package com.jws.jwsapi.core.navigation;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.AdapterHelper;

import java.util.ArrayList;
import java.util.List;

public class NavigationDynamicAdapter extends RecyclerView.Adapter<NavigationDynamicAdapter.ViewHolder> {


    private int selectedPos = RecyclerView.NO_POSITION;
    private List<String> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int lastPositionAdapter = -1;
    Context context;
    // data is passed into the constructor
    public NavigationDynamicAdapter(Context context, List<String> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context=context;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_list_black_tex4t, parent, false);

        return new ViewHolder(view);
    }
    // binds the data to the TextView in each row
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
        lastPositionAdapter = AdapterHelper.setAnimationSlideInLeft(holder.itemView, position, lastPositionAdapter, context);
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
    public int getItemViewType(int position) {
        return position;
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

    public String getItem(int id) {
        return mData.get(id);
    }

    // allows clicks events to be caught
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
    public void filterList(ArrayList<String> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }
}