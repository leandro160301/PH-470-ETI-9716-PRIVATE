package com.jws.jwsapi.pallet;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.databinding.ItemPalletBinding;

import java.util.Locale;

public class PalletViewHolder extends RecyclerView.ViewHolder {

    private final ItemPalletBinding binding;
    private final PalletButtonClickListener listener;
    private final String unit;

    public PalletViewHolder(ItemPalletBinding binding, PalletButtonClickListener listener, String unit) {
        super(binding.getRoot());
        this.binding = binding;
        this.listener = listener;
        this.unit = unit;
    }

    public void bind(Pallet pallet) {
        binding.tvState.setText(pallet.isClosed() ? "PALLET CERRADO" : "PALLET ABIERTO");
        binding.lnDelete.setVisibility(View.VISIBLE);
        binding.lnSelect.setVisibility(View.VISIBLE);
        binding.lnClose.setVisibility(View.VISIBLE);
        binding.tvPalletTotal.setVisibility(View.GONE);
        binding.tvPalletTotalHeader.setVisibility(View.GONE);

        binding.tvPalletCode.setText(pallet.getCode());
        binding.tvPalletName.setText(pallet.getName());
        binding.tvOrigin.setText(pallet.getOriginPallet());
        binding.tvDestination.setText(pallet.getDestinationPallet());
        String totalNet = String.format(Locale.US, "%s%s", pallet.getTotalNet(), unit);
        binding.tvPalletTotal.setText(totalNet);
        String done = String.format(Locale.US, "%d/%d", pallet.getDone(), pallet.getQuantity());
        binding.tvPalletQuantity.setText(done);

        binding.lnDelete.setOnClickListener(v -> listener.deletePallet(pallet));
        binding.lnSelect.setOnClickListener(v -> listener.selectPallet(pallet));
        binding.lnClose.setOnClickListener(v -> listener.closePallet(pallet));

        if (pallet.isClosed()) {
            binding.lnDelete.setVisibility(View.GONE);
            binding.lnSelect.setVisibility(View.GONE);
            binding.lnClose.setVisibility(View.GONE);
            binding.tvPalletTotal.setVisibility(View.VISIBLE);
            binding.tvPalletTotalHeader.setVisibility(View.VISIBLE);
        }
    }
}