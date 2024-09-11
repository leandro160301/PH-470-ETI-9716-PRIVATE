package com.jws.jwsapi.general.weighing;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.ItemWeighingBinding;
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
        ItemWeighingBinding binding = ItemWeighingBinding.inflate(inflater, parent, false);
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

    static class WeighingViewHolder extends RecyclerView.ViewHolder {
        private final ItemWeighingBinding binding;

        public WeighingViewHolder(ItemWeighingBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Weighing weighing) {
            binding.tvWeighingCode.setText(weighing.getCode());
            binding.tvWeighingName.setText(weighing.getName());
            binding.tvWeighingNet.setText(weighFormat(weighing.getNet(),weighing));
            binding.tvWeighingGross.setText(weighFormat(weighing.getGross(),weighing));
            binding.tvWeighingTare.setText(weighFormat(weighing.getTare(),weighing));
            binding.tvWeighingOperator.setText(weighing.getOperator());
            binding.tvWeighingSerialNumber.setText(weighing.getSerialNumber());
            binding.tvWeighingScale.setText(String.valueOf(weighing.getScaleNumber()));
            binding.btExpand.setOnClickListener(v -> {
                boolean isGone = (binding.lnExpand.getVisibility() == View.GONE);
                binding.lnExpand.setVisibility(isGone ? View.VISIBLE : View.GONE);
                binding.btExpand.setBackgroundResource(isGone ? R.drawable.boton_menos_i : R.drawable.boton_mas_i);
            });
        }

        public String weighFormat(String weight,Weighing weighing) {
            return String.format(Locale.US, "%s%s", weight, weighing.getUnit());
        }
    }
}