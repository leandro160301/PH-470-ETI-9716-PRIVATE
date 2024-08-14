package com.jws.jwsapi.base.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.base.activities.MainActivity;
import com.jws.jwsapi.R;

import java.util.ArrayList;
import java.util.List;

public class MyRecyclerViewAdapterConfigDinamico extends RecyclerView.Adapter<MyRecyclerViewAdapterConfigDinamico.ViewHolder> {


    private int selectedPos = RecyclerView.NO_POSITION;
    private List<String> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private int lastPosition = -1;
    private final MainActivity mainActivity;
    // data is passed into the constructor
    public MyRecyclerViewAdapterConfigDinamico(Context context, List<String> data, MainActivity mainActivity) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mainActivity = mainActivity;
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
        //ExampleItem currentItem = mExampleList.get(position);
        String animal = mData.get(position);
        holder.myTextView.setText(animal);

        if (selectedPos == position){
            // holder.itemView.setBackgroundColor(Color.parseColor("#FF2C2C"));
            holder.itemView.setBackgroundResource(R.drawable.botoneraprincipal);
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
        setAnimation(holder.itemView, position);

        //holder.itemView.setBackgroundColor(Color.YELLOW);
        holder.itemView.setSelected(selectedPos == position);
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
    public int getItemViewType(int position) {
        return position;
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mainActivity.getApplicationContext(), android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
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