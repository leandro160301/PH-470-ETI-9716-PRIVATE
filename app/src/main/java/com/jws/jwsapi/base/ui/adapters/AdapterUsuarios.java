package com.jws.jwsapi.base.ui.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.R;
import com.jws.jwsapi.base.models.UsuariosModel;
import com.jws.jwsapi.base.ui.fragments.AdapterUsuariosInterface;
import java.util.List;
import java.util.Objects;

public class AdapterUsuarios extends RecyclerView.Adapter<AdapterUsuarios.ViewHolder> {

    private int selectedPos = RecyclerView.NO_POSITION;
    List<UsuariosModel> ListElementsArrayList2;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    AdapterUsuariosInterface adapterUsuariosInterface;

    // data is passed into the constructor
    public AdapterUsuarios(Context context, List<UsuariosModel> data, AdapterUsuariosInterface adapterUsuariosInterface) {
        this.mInflater = LayoutInflater.from(context);
        this.ListElementsArrayList2 = data;
        this.adapterUsuariosInterface=adapterUsuariosInterface;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_usuarios_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv_nombre.setText( ListElementsArrayList2.get(position).nombre);
        holder.tv_tipo.setText( ListElementsArrayList2.get(position).tipo);
        holder.tv_id.setText(String.valueOf(ListElementsArrayList2.get(position).id));
        holder.tv_codigo.setText(ListElementsArrayList2.get(position).codigo);
        holder.ln_eliminar.setOnClickListener(view -> adapterUsuariosInterface.eliminarUsuario(ListElementsArrayList2, position));

        if(Objects.equals(ListElementsArrayList2.get(position).tipo, "Supervisor")){
            holder.idIVCourseImage.setImageResource(R.drawable.icono_supervisor_negro);
        }
        if(Objects.equals(ListElementsArrayList2.get(position).tipo, "Operador")){
            holder.idIVCourseImage.setImageResource(R.drawable.usuario_icono_negro);
        }

        if (selectedPos == position){
            holder.tv_nombre.setTextColor(Color.BLACK);
        }
        else{
            holder.tv_nombre.setTextColor(Color.BLACK);
        }

        //holder.itemView.setBackgroundColor(Color.YELLOW);
        holder.itemView.setSelected(selectedPos == position);



    }

    @Override
    public int getItemCount() {
        return ListElementsArrayList2.size();
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
        TextView tv_nombre, tv_id, tv_tipo, tv_codigo;
        ImageView idIVCourseImage;
        LinearLayout ln_eliminar;



        ViewHolder(View itemView) {
            super(itemView);
            tv_nombre = itemView.findViewById(R.id.tvUsuario);
            tv_id = itemView.findViewById(R.id.tvTipo);
            tv_tipo = itemView.findViewById(R.id.tvDocumento);
            tv_codigo = itemView.findViewById(R.id.tvNumero);
            idIVCourseImage = itemView.findViewById(R.id.idIVCourseImage);
            ln_eliminar= itemView.findViewById(R.id.ln_eliminar);

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

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public void removeAt(int position) {
        ListElementsArrayList2.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, ListElementsArrayList2.size());
    }
    public void filterList(List<UsuariosModel> filteredList) {
        ListElementsArrayList2 = filteredList;
        notifyDataSetChanged();
    }
}