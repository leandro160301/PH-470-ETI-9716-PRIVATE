package com.jws.jwsapi.weighing;

import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.ItemWeighingBinding;

import java.util.Locale;

public class WeighingViewHolder extends RecyclerView.ViewHolder {
    private final ItemWeighingBinding binding;

    public WeighingViewHolder(ItemWeighingBinding binding) {
        super(binding.getRoot());
        this.binding = binding;
    }

    public void bind(Weighing weighing) {
        binding.lnExpand.setVisibility(View.GONE);
        binding.btExpand.setBackgroundResource(R.drawable.boton_mas_i);

        binding.tvWeighingName.setText(weighing.getProduct());
        binding.tvNet.setText(weighFormat(weighing.getNet(), weighing.getUnit()));
        binding.tvWeighingGross.setText(weighFormat(weighing.getGross(), weighing.getUnit()));
        binding.tvTareBox.setText(weighFormat(weighing.getBoxTare(), weighing.getUnit()));
        binding.tvTareIce.setText(weighFormat(weighing.getIceTare(), weighing.getUnit()));
        binding.tvTareParts.setText(weighFormat(weighing.getPartsTare(), weighing.getUnit()));
        binding.tvTareTop.setText(weighFormat(weighing.getTopTare(), weighing.getUnit()));
        binding.tvWeighingOperator.setText(weighing.getOperator());
        binding.tvBatch.setText(weighing.getBatch());
        binding.tvDestinatation.setText(String.valueOf(weighing.getDestination()));
        binding.tvCaliber.setText(String.valueOf(weighing.getCaliber()));
        binding.tvDate.setText(String.format(Locale.US, "%s %s", weighing.getDate(), weighing.getHour()));
        binding.tvLine.setText(weighing.getLine());
        binding.btExpand.setOnClickListener(v -> {
            boolean isGone = (binding.lnExpand.getVisibility() == View.GONE);
            binding.lnExpand.setVisibility(isGone ? View.VISIBLE : View.GONE);
            binding.btExpand.setBackgroundResource(isGone ? R.drawable.boton_menos_i : R.drawable.boton_mas_i);
        });
    }

    public String weighFormat(String weight, String unit) {
        return String.format(Locale.US, "%s%s", weight, unit);
    }
}
