package com.jws.jwsapi.feature.formulador.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.base.activities.MainActivity;
import com.jws.jwsapi.feature.formulador.di.RecetaManager;
import com.jws.jwsapi.feature.formulador.models.Form_Model_Ingredientes;
import com.jws.jwsapi.feature.formulador.models.Form_Model_Receta;
import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.Utils;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Form_Adapter_Recetas extends RecyclerView.Adapter<Form_Adapter_Recetas.ViewHolder> {

    RecetaManager recetaManager;
    private final int selectedPos = RecyclerView.NO_POSITION;
    private List<Form_Model_Receta> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private final MainActivity mainActivity;
    public String recetaelegida = "";
    private int lastPosition = -1;
    int lastVisiblePosition2 = -1, lastVisiblePosition3 = -1;
    Boolean filtro = false;
    public Form_Adapter_Ingredientes adapter;
    ArrayList<Form_Model_Ingredientes> filteredList = new ArrayList<>();
    ArrayList<Integer> filteredListNumeric = new ArrayList<>();


    // data is passed into the constructor
    public Form_Adapter_Recetas(Context context, List<Form_Model_Receta> data, MainActivity mainActivity, String recetaelegida,RecetaManager recetaManager) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.mainActivity = mainActivity;
        this.recetaelegida = recetaelegida;
        this.recetaManager = recetaManager;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.prog_formulador_adapter_receta, parent, false);

        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //ExampleItem currentItem = mExampleList.get(position);
        int posi = position;
        holder.tv_codigo.setText(mData.get(position).getCodigo_ing());
        holder.tv_nombre.setText(mData.get(position).getDescrip_ing());
        holder.tv_kilos.setText(mData.get(position).getKilos_ing() + mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA));
        holder.tv_paso.setText(String.valueOf(position + 1));
        holder.tv_tolerancia.setText(mData.get(position).getTolerancia_ing());
        if (recetaManager.ejecutando) {
            holder.ln_editar.setVisibility(View.GONE);
            holder.ln_borrar.setVisibility(View.GONE);
            holder.ln_agregar.setVisibility(View.GONE);
        }
        holder.ln_editar.setOnClickListener(view -> {
            if(mainActivity.modificarDatos()){
                dialogoModificarPaso(posi);
            }else{
                Utils.Mensaje("No esta habilitado para modificar datos",R.layout.item_customtoasterror,mainActivity);
            }

        });
        holder.ln_borrar.setOnClickListener(view -> {
            if (mData.size() > 1) {
                if(mainActivity.modificarDatos()){
                    dialogoEliminarPaso(posi);
                }else{
                    Utils.Mensaje("No esta habilitado para modificar datos",R.layout.item_customtoasterror,mainActivity);
                }
            } else {
                Utils.Mensaje("No puede eliminar mas pasos", R.layout.item_customtoasterror,mainActivity);
            }
        });
        holder.ln_agregar.setOnClickListener(view -> {
            if(mainActivity.modificarDatos()){
                dialogoAgregarPaso(posi);
            }else{
                Utils.Mensaje("No esta habilitado para modificar datos",R.layout.item_customtoasterror,mainActivity);
            }


        });
        if (!recetaManager.ejecutando) {
            holder.im_campo.setVisibility(View.GONE);
            holder.tv_pesoreal.setVisibility(View.GONE);
            holder.tv_tolerancia.setVisibility(View.GONE);

        }
        if (Objects.equals(mData.get(position).getKilos_reales_ing(), "NO")) {
            if (recetaManager.ejecutando) {
                holder.im_campo.setBackgroundResource(R.drawable.unchecked);
            }

            holder.tv_pesoreal.setVisibility(View.GONE);
        } else {
            if (recetaManager.ejecutando) {
                holder.im_campo.setBackgroundResource(R.drawable.checked);
            }

            holder.tv_pesoreal.setText(mData.get(position).getKilos_reales_ing() + mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA));
        }


        if (selectedPos == position) {
            holder.itemView.setBackgroundResource(R.drawable.fondolineainferiornegratranslucido);
        } else {
            holder.itemView.setBackgroundResource(R.drawable.fondolineainferiornegra2);
        }

        setAnimation(holder.itemView, position);
        //holder.itemView.setBackgroundColor(Color.YELLOW);
        holder.itemView.setSelected(selectedPos == position);
    }

    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mainActivity.getApplicationContext(), android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    private void dialogoEliminarPaso(int posicion) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mainActivity,R.style.AlertDialogCustom);

        View mView = mainActivity.getLayoutInflater().inflate(R.layout.dialogo_dossinet, null);
        TextView textView = mView.findViewById(R.id.textViewt);
        textView.setText("¿Quiere eliminar el paso " + (posicion + 1) + "?");

        Button Guardar = mView.findViewById(R.id.buttons);
        Button Cancelar = mView.findViewById(R.id.buttonc);
        Guardar.setText("ELIMINAR");

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {
            mData.remove(posicion);
            try {
                mainActivity.mainClass.setReceta(recetaelegida, mData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            refrescarList(mData);
            dialog.cancel();
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());
    }


    private void dialogoModificarPaso(int posicion) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mainActivity,R.style.AlertDialogCustom);

        View mView = mainActivity.getLayoutInflater().inflate(R.layout.dialogo_pasoreceta, null);
        TextView tv_codigo = mView.findViewById(R.id.tv_codigoingrediente);
        TextView tv_descripcion = mView.findViewById(R.id.tv_descripcioningrediente);
        TextView tv_kilos = mView.findViewById(R.id.tv_kilos);

        Button Guardar = mView.findViewById(R.id.buttons);
        Button Cancelar = mView.findViewById(R.id.buttonc);
        Guardar.setText("EDITAR");


        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        if (mData.size() > posicion) {
            tv_descripcion.setText(mData.get(posicion).getDescrip_ing());
            tv_codigo.setText(mData.get(posicion).getCodigo_ing());
            tv_kilos.setText(mData.get(posicion).getKilos_ing());
        }

        tv_codigo.setOnClickListener(view -> Buscador(tv_codigo, tv_descripcion));
        tv_descripcion.setOnClickListener(view -> Buscador(tv_codigo, tv_descripcion));
        tv_kilos.setOnClickListener(view -> Teclado(tv_kilos, "Ingrese los kilos del paso"));

        Guardar.setOnClickListener(view -> {
            if (!tv_codigo.getText().toString().equals("") && !tv_descripcion.getText().toString().equals("") && !tv_kilos.getText().toString().equals("") && Utils.isNumeric(tv_kilos.getText().toString())) {
                if (mData.size() > posicion) {
                    mData.get(posicion).setDescrip_ing(tv_descripcion.getText().toString());
                    mData.get(posicion).setCodigo_ing(tv_codigo.getText().toString());
                    mData.get(posicion).setKilos_ing(tv_kilos.getText().toString());
                    try {
                        mainActivity.mainClass.setReceta(recetaelegida, mData);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    refrescarList(mData);
                }

                dialog.cancel();
            }

        });
        Cancelar.setOnClickListener(view -> dialog.cancel());
    }

    private void dialogoAgregarPaso(int posicion) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mainActivity,R.style.AlertDialogCustom);

        View mView = mainActivity.getLayoutInflater().inflate(R.layout.dialogo_pasoreceta, null);
        TextView tv_codigo = mView.findViewById(R.id.tv_codigoingrediente);
        TextView tv_descripcion = mView.findViewById(R.id.tv_descripcioningrediente);
        TextView tv_kilos = mView.findViewById(R.id.tv_kilos);

        Button Guardar = mView.findViewById(R.id.buttons);
        Button Cancelar = mView.findViewById(R.id.buttonc);
        Guardar.setText("AGREGAR");


        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        tv_codigo.setOnClickListener(view -> Buscador(tv_codigo, tv_descripcion));
        tv_descripcion.setOnClickListener(view -> Buscador(tv_codigo, tv_descripcion));
        tv_kilos.setOnClickListener(view -> Teclado(tv_kilos, "Ingrese los kilos a realizar del nuevo paso"));

        Guardar.setOnClickListener(view -> {
            if (!tv_codigo.getText().toString().equals("") && !tv_descripcion.getText().toString().equals("") && !tv_kilos.getText().toString().equals("") && Utils.isNumeric(tv_kilos.getText().toString())) {
                if (posicion < mData.size() - 1) {
                    mData.add(posicion + 1, new Form_Model_Receta(mData.get(posicion).getCodigo(), mData.get(posicion).getNombre(), mData.get(posicion).getKilos_totales(),
                            tv_codigo.getText().toString(), tv_descripcion.getText().toString(), tv_kilos.getText().toString(), "NO", ""));
                } else {
                    mData.add(new Form_Model_Receta(mData.get(posicion).getCodigo(), mData.get(posicion).getNombre(), mData.get(posicion).getKilos_totales(),
                            tv_codigo.getText().toString(), tv_descripcion.getText().toString(), tv_kilos.getText().toString(), "NO", ""));
                }

                try {
                    mainActivity.mainClass.setReceta(recetaelegida, mData);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                refrescarList(mData);
                mainActivity.mainClass.lista_recetas.smoothScrollToPosition(mData.size() - 1);
                dialog.cancel();
            }

        });
        Cancelar.setOnClickListener(view -> dialog.cancel());
    }

    public void Teclado(TextView View, String texto) {

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mainActivity);

        View mView = mainActivity.getLayoutInflater().inflate(R.layout.dialogo_dosopcionespuntos, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        TextView textView = mView.findViewById(R.id.textViewt);
        LinearLayout lndelete_text = mView.findViewById(R.id.lndelete_text);
        textView.setText(texto);
        userInput.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);
        userInput.setKeyListener(DigitsKeyListener.getInstance(".0123456789"));
        userInput.setOnLongClickListener(v -> true);
        userInput.requestFocus();

        lndelete_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                userInput.setText("");
            }
        });
        Button Guardar = mView.findViewById(R.id.buttons);
        Button Cancelar = mView.findViewById(R.id.buttonc);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {
            if (Utils.isNumeric(userInput.getText().toString())) {
                View.setText(userInput.getText().toString());
            }
            dialog.cancel();
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());

    }

    public void Buscador(TextView tv_codigo, TextView tv_des) {
        filtro = false;

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mainActivity,R.style.AlertDialogCustom);

        View mView = mainActivity.getLayoutInflater().inflate(R.layout.dialogo_buscador, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        RecyclerView listview = mView.findViewById(R.id.listview);
        ImageView im_buscador = mView.findViewById(R.id.im_buscador);

        Button Cancelar = mView.findViewById(R.id.buttonc);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        List<Form_Model_Ingredientes> ingredien = mainActivity.mainClass.getIngredientes();
        listview.setLayoutManager(new LinearLayoutManager(mainActivity));
        adapter = new Form_Adapter_Ingredientes(mainActivity, ingredien, false,null);
        adapter.setClickListener((view, position) -> {
            tv_codigo.setText(ingredien.get(position).getCodigo());
            tv_des.setText(ingredien.get(position).getNombre());

            dialog.cancel();
        });

        listview.setAdapter(adapter);
        userInput.setOnLongClickListener(v -> true);
        userInput.setHint(" Buscar");
        userInput.setOnClickListener(view -> userInput.getText().clear());

        userInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                filtro = true;
                filter(editable.toString(), listview, dialog, ingredien, tv_codigo, tv_des);
            }
        });

        im_buscador.setOnClickListener(view -> {
            if (mainActivity != null) {
                InputMethodManager imm = (InputMethodManager) mainActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(userInput.getWindowToken(), 0);
                }
            }
        });

        Cancelar.setOnClickListener(view -> dialog.cancel());

    }

    private void filter(String text, RecyclerView listview, AlertDialog dialog, List<Form_Model_Ingredientes> inge, TextView tv_codigo, TextView tv_des) {
        // List<String>Filtro = new ArrayList<>();
        filteredList = new ArrayList<>();
        filteredListNumeric = new ArrayList<>();
        int i;
        for (i = 0; i < inge.size(); i++) {
            if (inge.get(i).getNombre().toLowerCase().contains(text.toLowerCase()) || inge.get(i).getCodigo().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(new Form_Model_Ingredientes(inge.get(i).getCodigo(), inge.get(i).getNombre()));
                filteredListNumeric.add(i);
                // Filtro.add(Productos.get(i));
            }
        }
        adapter.filterList(filteredList);

        adapter.setClickListener((view, position) -> {
            tv_codigo.setText(filteredList.get(position).getCodigo());
            tv_des.setText(filteredList.get(position).getNombre());
            dialog.cancel();
        });
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

    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_nombre, tv_codigo, tv_kilos, tv_pesoreal, tv_paso, tv_tolerancia;
        ImageView im_campo;
        LinearLayout ln_editar, ln_borrar, ln_agregar;


        ViewHolder(View itemView) {
            super(itemView);
            tv_nombre = itemView.findViewById(R.id.tv_descripcioningrediente);
            tv_codigo = itemView.findViewById(R.id.tv_codigoingrediente);
            tv_kilos = itemView.findViewById(R.id.tv_kilos);
            tv_pesoreal = itemView.findViewById(R.id.tv_pesoreal);
            im_campo = itemView.findViewById(R.id.im_campo);
            tv_paso = itemView.findViewById(R.id.tv_paso);
            tv_tolerancia = itemView.findViewById(R.id.tv_tolerancia);
            ln_editar = itemView.findViewById(R.id.ln_editar);
            ln_borrar = itemView.findViewById(R.id.ln_borrar);
            ln_agregar = itemView.findViewById(R.id.ln_agregar);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
            /*if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);*/
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

    public void filterList(ArrayList<Form_Model_Receta> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }

    public void refrescarList(List<Form_Model_Receta> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }
}