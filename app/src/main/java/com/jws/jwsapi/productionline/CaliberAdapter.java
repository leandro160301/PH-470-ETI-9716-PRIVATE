package com.jws.jwsapi.productionline;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.databinding.CaliberAdapterBinding;
import com.jws.jwsapi.utils.AdapterHelper;

import java.util.List;

public class CaliberAdapter extends RecyclerView.Adapter<CaliberAdapter.ViewHolder> {

    private final LayoutInflater mInflater;
    CaliberInterface caliberInterface;
    Context context;
    private int selectedPos = RecyclerView.NO_POSITION;
    private List<String> mData;
    private ItemClickListener mClickListener;
    private int lastPositionAdapter = -1;

    public CaliberAdapter(Context context, List<String> data, CaliberInterface caliberInterface) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context = context;
        this.caliberInterface = caliberInterface;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CaliberAdapterBinding binding = CaliberAdapterBinding.inflate(mInflater, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String item = mData.get(position);
        holder.binding.tvDescripcioningrediente.setText(item);

        holder.binding.lnEditar.setOnClickListener(view -> {
            caliberInterface.deleteElement(mData, position);
        });
        lastPositionAdapter = AdapterHelper.setAnimationPivot(holder.itemView, position, lastPositionAdapter, context);
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

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void removeAt(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }

    @SuppressLint("NotifyDataSetChanged")
    public void refreshList(List<String> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final CaliberAdapterBinding binding;

        ViewHolder(CaliberAdapterBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);
        }
    }
}