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

//        binding.tvWeighingCode.setText(weighing.getCode());
//        binding.tvWeighingName.setText(weighing.getName());
//        binding.tvWeighingNet.setText(weighFormat(weighing.getNet(), weighing.getUnit()));
//        binding.tvWeighingGross.setText(weighFormat(weighing.getGross(), weighing.getUnit()));
//        binding.tvWeighingTare.setText(weighFormat(weighing.getTare(), weighing.getUnit()));
//        binding.tvWeighingOperator.setText(weighing.getOperator());
//        binding.tvWeighingSerialNumber.setText(weighing.getSerialNumber());
//        binding.tvWeighingScale.setText(String.valueOf(weighing.getScaleNumber()));
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
