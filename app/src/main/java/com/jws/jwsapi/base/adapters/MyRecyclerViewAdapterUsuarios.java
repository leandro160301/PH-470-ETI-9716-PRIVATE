package com.jws.jwsapi.base.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.base.activities.MainActivity;
import com.jws.jwsapi.base.data.sql.Usuarios_SQL_db;
import com.jws.jwsapi.R;
import com.jws.jwsapi.base.models.UsuariosModel;
import com.jws.jwsapi.utils.Utils;

import java.util.List;
import java.util.Objects;

public class MyRecyclerViewAdapterUsuarios extends RecyclerView.Adapter<MyRecyclerViewAdapterUsuarios.ViewHolder> {


    private int selectedPos = RecyclerView.NO_POSITION;
    List<UsuariosModel> ListElementsArrayList2;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    MainActivity mainActivity;

    // data is passed into the constructor
    public MyRecyclerViewAdapterUsuarios(Context context, List<UsuariosModel> data,MainActivity mainActivity) {
        this.mInflater = LayoutInflater.from(context);
        this.ListElementsArrayList2 = data;
        this.mainActivity=mainActivity;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.item_usuarios_recycler, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int posi=position;
        holder.tv_nombre.setText( ListElementsArrayList2.get(position).nombre);
        holder.tv_tipo.setText( ListElementsArrayList2.get(position).tipo);
        holder.tv_id.setText(String.valueOf(ListElementsArrayList2.get(position).id));
        holder.tv_codigo.setText(ListElementsArrayList2.get(position).codigo);

        holder.ln_eliminar.setOnClickListener(view -> DialogoEliminarUsuario(posi));

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


           /* itemView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        // run scale animation and make it bigger
                        Animation anim = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.scaleintv);
                        itemView.startAnimation(anim);
                        anim.setFillAfter(true);
                    } else {
                        // run scale animation and make it smaller
                        Animation anim = AnimationUtils.loadAnimation(itemView.getContext(), R.anim.scaleouttv);
                        itemView.startAnimation(anim);
                        anim.setFillAfter(true);
                    }
                }
            });*/

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
    //public String getItem(int id) { return ListElementsArrayList2.get(id); }

    // allows clicks events to be caught
    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public void DialogoEliminarUsuario(int posicion) {
        if(mainActivity.getNivelUsuario()>2){
            if (ListElementsArrayList2.size() > 0) {
                if(!ListElementsArrayList2.get(posicion).nombre.equals(mainActivity.getUsuarioActual())){
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(mainActivity,R.style.AlertDialogCustom);
                    View mView = mainActivity.getLayoutInflater().inflate(R.layout.dialogo_dossinet, null);
                    TextView textView = mView.findViewById(R.id.textViewt);
                    textView.setText("Quiere eliminar el usuario " + ListElementsArrayList2.get(posicion).nombre + "?");

                    Button Guardar = mView.findViewById(R.id.buttons);
                    Button Cancelar = mView.findViewById(R.id.buttonc);

                    Guardar.setText("ELIMINAR");
                    mBuilder.setView(mView);
                    final AlertDialog dialog = mBuilder.create();
                    dialog.show();

                    Guardar.setOnClickListener(view -> {

                        try (Usuarios_SQL_db dbHelper = new Usuarios_SQL_db(mainActivity, MainActivity.DB_USERS_NAME, null, MainActivity.DB_USERS_VERSION)) {
                            dbHelper.eliminarUsuario(ListElementsArrayList2.get(posicion).usuario);
                        }
                        ListElementsArrayList2.remove(posicion);
                        filterList(ListElementsArrayList2);


                        dialog.cancel();

                    });
                    Cancelar.setOnClickListener(view -> dialog.cancel());
                }
                else {

                    Utils.Mensaje("No puedes eliminar el usuario actual",R.layout.item_customtoasterror,mainActivity);

                }
            }
            else {

                Utils.Mensaje("No puedes eliminar mas usuarios",R.layout.item_customtoasterror,mainActivity);

            }
        }else{
            Utils.Mensaje("El usuario logeado no puede modificar otros usuarios",R.layout.item_customtoasterror,mainActivity);
        }

    }

    // parent activity will implement this method to respond to click events
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