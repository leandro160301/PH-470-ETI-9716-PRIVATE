package com.jws.jwsapi.general.pallet;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.databinding.ItemPalletBinding;
import java.util.List;
import java.util.Locale;

public class PalletAdapter extends RecyclerView.Adapter<PalletAdapter.PalletViewHolder> {

    private List<Pallet> palletList;
    private final PalletButtonClickListener listener;

    public PalletAdapter(List<Pallet> palletList, PalletButtonClickListener listener) {
        this.palletList = palletList;
        this.listener = listener;
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
        return new PalletViewHolder(binding);
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

    class PalletViewHolder extends RecyclerView.ViewHolder {
        private final ItemPalletBinding binding;

        public PalletViewHolder(ItemPalletBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Pallet pallet) {
            binding.tvPalletSerialNumber.setText(pallet.getCode());
            binding.tvPalletDestination.setText(pallet.getName());
            binding.tvPalletTotal.setText(pallet.getTotalNet());
            String text = String.format(Locale.US, "%d/%d", pallet.getDone(), pallet.getQuantity());
            binding.tvPalletQuantity.setText(text);
            binding.lnDelete.setOnClickListener(v -> listener.deletePallet(pallet));
            binding.lnSelect.setOnClickListener(v -> listener.selectPallet(pallet));
            binding.lnClose.setOnClickListener(v -> listener.closePallet(pallet));
            if(pallet.isClosed()){
                binding.lnDelete.setVisibility(View.GONE);
                binding.lnSelect.setVisibility(View.GONE);
                binding.lnClose.setVisibility(View.GONE);
                binding.tvPalletTotal.setVisibility(View.VISIBLE);
                binding.tvPalletTotalHeader.setVisibility(View.VISIBLE);
            }
        }
    }
}