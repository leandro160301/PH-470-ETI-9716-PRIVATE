package com.jws.jwsapi.weighing;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.databinding.ItemWeighingBinding;

import java.util.List;

public class WeighingAdapter extends RecyclerView.Adapter<WeighingViewHolder> {

    private final WeighingDeleteClick weighingDeleteClick;
    private List<Weighing> weighingList;

    public WeighingAdapter(List<Weighing> weighingList,
                           WeighingDeleteClick weighingDeleteClick) {
        this.weighingList = weighingList;
        this.weighingDeleteClick = weighingDeleteClick;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Weighing> newPalletList) {
        this.weighingList = newPalletList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WeighingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemWeighingBinding binding = ItemWeighingBinding.inflate(inflater, parent, false);
        return new WeighingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WeighingViewHolder holder, int position) {
        Weighing weighing = weighingList.get(position);
        holder.bind(weighing, weighingDeleteClick);
    }

    @Override
    public int getItemCount() {
        return weighingList.size();
    }


}