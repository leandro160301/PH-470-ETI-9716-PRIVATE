package com.jws.jwsapi.pallet;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.databinding.ItemPalletBinding;

import java.util.List;

public class PalletAdapter extends RecyclerView.Adapter<PalletViewHolder> {

    private List<Pallet> palletList;
    private final PalletButtonClickListener listener;
    private final String unit;

    public PalletAdapter(List<Pallet> palletList, PalletButtonClickListener listener, String unit) {
        this.palletList = palletList;
        this.listener = listener;
        this.unit = unit;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateData(List<Pallet> newPalletList) {
        this.palletList = newPalletList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PalletViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemPalletBinding binding = ItemPalletBinding.inflate(inflater, parent, false);
        return new PalletViewHolder(binding, listener, unit);
    }

    @Override
    public void onBindViewHolder(@NonNull PalletViewHolder holder, int position) {
        Pallet pallet = palletList.get(position);
        holder.bind(pallet);
    }

    @Override
    public int getItemCount() {
        return palletList.size();
    }
}