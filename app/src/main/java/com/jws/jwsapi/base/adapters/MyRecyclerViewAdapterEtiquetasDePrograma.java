package com.jws.jwsapi.base.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.base.activities.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.base.models.EtiquetasDeProgramaModel;
import java.util.ArrayList;
import java.util.List;

public class MyRecyclerViewAdapterEtiquetasDePrograma extends RecyclerView.Adapter<MyRecyclerViewAdapterEtiquetasDePrograma.ViewHolder> {

    private int selectedPos = RecyclerView.NO_POSITION;
    private List<EtiquetasDeProgramaModel> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private MainActivity mainActivity;
    private int lastPosition = -1;
    List<String>etiquetas ;

    // data is passed into the constructor
    public MyRecyclerViewAdapterEtiquetasDePrograma(Context context, List<EtiquetasDeProgramaModel> data, MainActivity mainActivity,List<String>etiquetas) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mainActivity=mainActivity;
        this.etiquetas= etiquetas;

    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.standar_adapter_etiquetadeprograma, parent, false);

        return new ViewHolder(view);
    }
    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //ExampleItem currentItem = mExampleList.get(position);
        int posi=position;
        holder.tv_campo.setText(mData.get(position).nombrecampo);
        holder.spCampo.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(
                mainActivity,
                R.layout.item_spinner,
                etiquetas
        );

        holder.spCampo.setAdapter(adapter2);
        int index= etiquetas.indexOf(mainActivity.mainClass.preferencesManager.getEtiqueta(posi));
        if (index>-1){
            holder.spCampo.setSelection(index);
        }

        holder.spCampo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                mainActivity.mainClass.preferencesManager.setEtiqueta(etiquetas.get(i),posi);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        setAnimation(holder.itemView, position);
        //holder.itemView.setBackgroundColor(Color.YELLOW);
        holder.itemView.setSelected(selectedPos == position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mainActivity.getApplicationContext(), R.anim.pivot);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
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
    public int getItemViewType(int position)
    {
        return position;
    }

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_campo;
        Spinner spCampo;

        ViewHolder(View itemView) {
            super(itemView);
            tv_campo = itemView.findViewById(R.id.tv_campo);
            spCampo = itemView.findViewById(R.id.spCampo);
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
    //public String getItem(int id) {return mData.get(id);}

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
    public void filterList(ArrayList<EtiquetasDeProgramaModel> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }
    public void refrescarList(List<EtiquetasDeProgramaModel> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }
}