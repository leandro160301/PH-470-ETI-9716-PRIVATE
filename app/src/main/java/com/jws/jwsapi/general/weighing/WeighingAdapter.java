package com.jws.jwsapi.general.weighing;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.databinding.ItemPalletBinding;
import com.jws.jwsapi.general.pallet.Pallet;
import com.jws.jwsapi.general.pallet.PalletButtonClickListener;

import java.util.List;
import java.util.Locale;

public class WeighingAdapter extends RecyclerView.Adapter<WeighingAdapter.WeighingViewHolder> {

    private List<Weighing> weighingList;

    public WeighingAdapter(List<Weighing> weighingList) {
        this.weighingList = weighingList;
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
        ItemPalletBinding binding = ItemPalletBinding.inflate(inflater, parent, false);
        return new WeighingViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WeighingViewHolder holder, int position) {
        Weighing weighing = weighingList.get(position);
        holder.bind(weighing);
    }

    @Override
    public int getItemCount() {
        return weighingList.size();
    }

    class WeighingViewHolder extends RecyclerView.ViewHolder {
        private final ItemPalletBinding binding;

        public WeighingViewHolder(ItemPalletBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Weighing weighing) {

        }
    }
}