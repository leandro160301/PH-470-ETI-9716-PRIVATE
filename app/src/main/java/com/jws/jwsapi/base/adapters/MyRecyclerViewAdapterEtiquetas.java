package com.jws.jwsapi.base.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.base.activities.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.base.models.EtiquetasModel;
import com.jws.jwsapi.utils.Utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MyRecyclerViewAdapterEtiquetas extends RecyclerView.Adapter<MyRecyclerViewAdapterEtiquetas.ViewHolder> {


    private int selectedPos = RecyclerView.NO_POSITION;
    private List<EtiquetasModel> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private String[] ListElements2 = new String[] {};
    private List<String> ListElementsArrayList2=new ArrayList<>(Arrays.asList(ListElements2));
    public List<Integer> ListElementsInt,ListElementsInternaInt,ListElementsInternalConcat,ListElementsPosicionesTipo,ListElementsArrayConcatFormat;
    public List<String> ListElementsFijo,ListElementsInternaFijo;
    RecyclerView recyclerView;
    private final Context context;
    private static MainActivity mainActivity;
    public String etiqueta="";
    int Selected=0;
    int posicionconcat=-1;
    public int numposicion=0;
    private int lastPosition = -1;

    public MyRecyclerViewAdapterEtiquetas(Context context, List<EtiquetasModel> data, MainActivity mainActivity,String etiqueta,RecyclerView recyclerView,int numposicion) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
        this.context=context;
        this.mainActivity=mainActivity;
        this.etiqueta=etiqueta;
        this.recyclerView=recyclerView;
        this.numposicion=numposicion;
        ListElementsArrayList2=new ArrayList<>();
        for(int i = 0; i<mainActivity.mainClass.imprimiblesPredefinidas.size(); i++){
            ListElementsArrayList2.add(mainActivity.mainClass.imprimiblesPredefinidas.get(i));
        }
        for(int i = 0; i<mainActivity.mainClass.variablesImprimibles.size(); i++){
            ListElementsArrayList2.add(mainActivity.mainClass.variablesImprimibles.get(i).descripcion);
        }

        ListElementsInt=mainActivity.mainClass.preferencesManager.getListSpinner(etiqueta);
        if(ListElementsInt==null){
            ListElementsInt=new ArrayList<>();
            for(int i=0;i<data.size();i++){
                ListElementsInt.add(0);

            }
        }
        if(ListElementsInt.size()<data.size()){// esto se agrego para el caso de que carguen una actualizacion de la etiqueta con mas campos
            while(ListElementsInt.size()<data.size())   {
                ListElementsInt.add(0);
            }
        }
        ListElementsPosicionesTipo=new ArrayList<>();
        for(int i=0;i<ListElementsArrayList2.size();i++){
            ListElementsPosicionesTipo.add(0);

        }
        ListElementsInternaInt=ListElementsInt;

        ListElementsFijo=mainActivity.mainClass.preferencesManager.getListFijo(etiqueta);
        if(ListElementsFijo==null){
            ListElementsFijo=new ArrayList<>();
            for(int i=0;i<data.size();i++){
                ListElementsFijo.add("");
            }
        }
        if(ListElementsFijo.size()<data.size()){// esto se agrego para el caso de que carguen una actualizacion de la etiqueta con mas campos
            while(ListElementsFijo.size()<data.size())   {
                ListElementsFijo.add("");
            }
        }
        ListElementsInternaFijo=ListElementsFijo;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.standar_adapter_etiqueta, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        int posi=position;
        try {
            holder.spCampo.setPopupBackgroundResource(R.drawable.campollenarclickeable);
            ArrayAdapter<String> adapter2 = new ArrayAdapter<>(
                    context.getApplicationContext(),
                    R.layout.item_spinner,
                    ListElementsArrayList2
            );

            holder.spCampo.setAdapter(adapter2);
            holder.tv_campo.setText(mData.get(posi).nombrecampo);

            if(ListElementsInt!=null&&ListElementsInt.size()>posi){
                holder.spCampo.setSelection(ListElementsInt.get(posi));
                if(ListElementsInt.get(posi)==mainActivity.mainClass.imprimiblesPredefinidas.size()-1){
                    holder.tv_textoconcatenado.setText("A,SD,AS,D");
                    if(ListElementsPosicionesTipo.size()>posi){
                        ListElementsPosicionesTipo.set(posi,2);
                    }

                    holder.ln_textoconcatenado.setVisibility(View.VISIBLE);
                    holder.ln_textofijo.setVisibility(View.GONE);
                    holder.ln_editar.setVisibility(View.VISIBLE);
                }else{
                    holder.ln_textoconcatenado.setVisibility(View.GONE);
                    holder.ln_editar.setVisibility(View.GONE);
                }
                if(ListElementsInt.get(posi)!=mainActivity.mainClass.imprimiblesPredefinidas.size()-1){
                    if(ListElementsInt.get(posi)==mainActivity.mainClass.imprimiblesPredefinidas.size()-2){
                        holder.ln_textoconcatenado.setVisibility(View.GONE);
                        if(ListElementsPosicionesTipo.size()>posi){
                            ListElementsPosicionesTipo.set(posi,1);
                        }
                        holder.ln_editar.setVisibility(View.VISIBLE);
                        if(ListElementsFijo!=null&&ListElementsFijo.size()>posi){
                            holder.tv_textofijo.setText(ListElementsFijo.get(posi));
                        }
                    }else{
                        holder.ln_textofijo.setVisibility(View.GONE);
                        holder.ln_editar.setVisibility(View.GONE);
                    }
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.Mensaje("Ocurrió un error:"+e.getMessage(), R.layout.item_customtoasterror,mainActivity);
        }

        holder.spCampo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                try {
                        if(ListElementsInt!=null&&ListElementsInt.size()>posi){
                            ListElementsInternaInt.set(posi,i);

                            if(i==mainActivity.mainClass.imprimiblesPredefinidas.size()-1){
                                ListElementsInternalConcat=mainActivity.mainClass.preferencesManager.getListConcat(etiqueta,posi);
                                String concat="";
                                String sepa=mainActivity.mainClass.preferencesManager.getSeparador(etiqueta,posi);
                                if(ListElementsInternalConcat!=null){
                                    for(int j=0;j<ListElementsInternalConcat.size();j++){
                                        if(ListElementsArrayList2.size()>ListElementsInternalConcat.get(j)){
                                            concat=concat.concat(ListElementsArrayList2.get(ListElementsInternalConcat.get(j))+sepa);
                                        }
                                    }
                                }
                                StringBuilder stringBuilder = new StringBuilder(concat);
                                int lastCommaIndex = stringBuilder.lastIndexOf(sepa);
                                if (lastCommaIndex >= 0) {
                                    stringBuilder.deleteCharAt(lastCommaIndex);
                                }
                                String resultado = stringBuilder.toString();
                                holder.tv_textoconcatenado.setText(resultado);
                                holder.ln_textoconcatenado.setVisibility(View.VISIBLE);
                                holder.ln_editar.setVisibility(View.VISIBLE);
                                holder.ln_textofijo.setVisibility(View.GONE);
                                if(ListElementsPosicionesTipo.size()>posi){
                                    ListElementsPosicionesTipo.set(posi,2);
                                }
                            }else{
                                holder.ln_textoconcatenado.setVisibility(View.GONE);
                                holder.ln_editar.setVisibility(View.GONE);
                            }
                            if(i!=mainActivity.mainClass.imprimiblesPredefinidas.size()-1){
                                if(i==mainActivity.mainClass.imprimiblesPredefinidas.size()-2){
                                    if(ListElementsFijo!=null&&ListElementsFijo.size()>posi){
                                        holder.tv_textofijo.setText(ListElementsFijo.get(posi));
                                    }
                                    if(ListElementsPosicionesTipo.size()>posi){
                                        ListElementsPosicionesTipo.set(posi,1);
                                    }
                                    holder.ln_textoconcatenado.setVisibility(View.GONE);
                                    holder.ln_textofijo.setVisibility(View.VISIBLE);
                                    holder.ln_editar.setVisibility(View.VISIBLE);
                                }else{
                                    holder.ln_textofijo.setVisibility(View.GONE);
                                    holder.ln_editar.setVisibility(View.GONE);
                                }
                            }

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Utils.Mensaje("Ocurrió un error:"+e.getMessage(), R.layout.item_customtoasterror,mainActivity);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        holder.ln_editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ListElementsPosicionesTipo.size()>posi){
                    if(ListElementsPosicionesTipo.get(posi)==2){
                        DialogoConcatenar(etiqueta,posi,holder.tv_textoconcatenado);
                    }
                    if(ListElementsPosicionesTipo.get(posi)==1){
                        //mainActivity.Mensaje("texto fijo",R.layout.item_customtoastok);
                        Teclado(holder.tv_textofijo,"Ingrese el texto fijo",posi);
                    }
                }


            }
        });

        setAnimation(holder.itemView, position);
        holder.itemView.setSelected(selectedPos == posi);
    }
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mainActivity.getApplicationContext(), android.R.anim.slide_in_left);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public void Teclado(TextView View,String texto,int posicion){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);

        View mView = mainActivity.getLayoutInflater().inflate(R.layout.dialogo_dosopciones, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        final LinearLayout delete_text= mView.findViewById(R.id.lndelete_text);
        delete_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {
                userInput.setText("");
            }
        });

        TextView textView=mView.findViewById(R.id.textViewt);
        textView.setText(texto);

        userInput.setText(View.getText().toString());

       /* if(View==tv_analisis||View==tv_empresa){
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        }*/

        userInput.setOnLongClickListener(v -> true);

        userInput.requestFocus();

        if(!View.getText().toString().equals("") && !View.getText().toString().equals("-")){
            userInput.setSelection(userInput.getText().length());
        }
        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {
            View.setText(userInput.getText().toString());
            if(ListElementsInternaFijo!=null&&ListElementsInternaFijo.size()>posicion){
                ListElementsInternaFijo.set(posicion,userInput.getText().toString());
            }
            dialog.cancel();
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());

    }


    public void DialogoConcatenar(String etiqueta,int posicion,TextView textView){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context,R.style.AlertDialogCustom);

        posicionconcat=-1;
        View mView = mainActivity.getLayoutInflater().inflate(R.layout.dialogo_etiquetaconcatenar, null);
        final Spinner spCampo = mView.findViewById(R.id.spCampo);
        final AppCompatButton bt_add= mView.findViewById(R.id.bt_add);
        final AppCompatButton bt_coma= mView.findViewById(R.id.bt_coma);
        final AppCompatButton bt_dospuntos= mView.findViewById(R.id.bt_dospuntos);
        final AppCompatButton bt_puntoycoma= mView.findViewById(R.id.bt_puntoycoma);
        final AppCompatButton bt_barra= mView.findViewById(R.id.bt_barra);
        final RecyclerView listview= mView.findViewById(R.id.listview);
        final AppCompatButton btborrar= mView.findViewById(R.id.btborrar);
        spCampo.setPopupBackgroundResource(R.drawable.stylekeycor6);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(
                context.getApplicationContext(),
                R.layout.item_spinner,
                ListElementsArrayList2
        );

        MyRecyclerViewAdapterMultimedia adapter;
        listview.setLayoutManager(new LinearLayoutManager(context));
        ListElementsArrayConcatFormat=mainActivity.mainClass.preferencesManager.getListConcat(etiqueta,posicion);
        List<String> ListElementsArrayConcat=new ArrayList<>();
        if(ListElementsArrayConcatFormat==null){
            ListElementsArrayConcatFormat=new ArrayList<>();
        }
        for(int i=0;i<ListElementsArrayConcatFormat.size();i++){
          /*  if(ListElementsArrayConcatFormat.get(i)<mainActivity.mainClass.imprimiblesPredefinidas.size()){
                ListElementsArrayConcat.add(mainActivity.mainClass.imprimiblesPredefinidas.get(ListElementsArrayConcatFormat.get(i)));
            }else if(ListElementsArrayConcatFormat.get(i)<mainActivity.mainClass.variablesImprimibles.size()+mainActivity.mainClass.imprimiblesPredefinidas.size()){
                ListElementsArrayConcat.add(mainActivity.mainClass.variablesImprimibles.get(ListElementsArrayConcatFormat.get(i)).descripcion);
            }*/
            if(ListElementsArrayConcatFormat.get(i)<ListElementsArrayList2.size()){
                ListElementsArrayConcat.add(ListElementsArrayList2.get(ListElementsArrayConcatFormat.get(i)));
            }

        }

        adapter = new MyRecyclerViewAdapterMultimedia(context, ListElementsArrayConcat,mainActivity);
        adapter.setClickListener(new MyRecyclerViewAdapterMultimedia.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                posicionconcat=position;
            }
        });
        listview.setAdapter(adapter);

        spCampo.setAdapter(adapter2);

        Selected=0;
        String separacion= mainActivity.mainClass.preferencesManager.getSeparador(etiqueta,posicion);
        if(Objects.equals(separacion, bt_coma.getText().toString())){
            bt_coma.setBackgroundResource(R.drawable.botoneraprincipal_selectorgris);
            bt_coma.setTextColor(Color.WHITE);
            Selected=1;
        }
        if(Objects.equals(separacion, bt_dospuntos.getText().toString())){
            bt_dospuntos.setBackgroundResource(R.drawable.botoneraprincipal_selectorgris);
            bt_dospuntos.setTextColor(Color.WHITE);
            Selected=2;
        }
        if(Objects.equals(separacion, bt_puntoycoma.getText().toString())){
            bt_puntoycoma.setBackgroundResource(R.drawable.botoneraprincipal_selectorgris);
            bt_puntoycoma.setTextColor(Color.WHITE);
            Selected=3;
        }
        if(Objects.equals(separacion, bt_barra.getText().toString())){
            bt_barra.setBackgroundResource(R.drawable.botoneraprincipal_selectorgris);
            bt_barra.setTextColor(Color.WHITE);
            Selected=4;
        }
        bt_coma.setOnClickListener(view -> {
            if(Selected!=1){
                bt_coma.setBackgroundResource(R.drawable.botoneraprincipal_selectorgris);
                bt_coma.setTextColor(Color.WHITE);
                bt_dospuntos.setBackgroundResource(R.drawable.stylekeycor4);
                bt_puntoycoma.setBackgroundResource(R.drawable.stylekeycor4);
                bt_barra.setBackgroundResource(R.drawable.stylekeycor4);
                bt_dospuntos.setTextColor(Color.BLACK);
                bt_puntoycoma.setTextColor(Color.BLACK);
                bt_barra.setTextColor(Color.BLACK);
                Selected=1;
            }
        });

        bt_dospuntos.setOnClickListener(view -> {
            if(Selected!=2){
                bt_dospuntos.setBackgroundResource(R.drawable.botoneraprincipal_selectorgris);
                bt_dospuntos.setTextColor(Color.WHITE);
                bt_coma.setBackgroundResource(R.drawable.stylekeycor4);
                bt_puntoycoma.setBackgroundResource(R.drawable.stylekeycor4);
                bt_barra.setBackgroundResource(R.drawable.stylekeycor4);
                bt_coma.setTextColor(Color.BLACK);
                bt_puntoycoma.setTextColor(Color.BLACK);
                bt_barra.setTextColor(Color.BLACK);
                Selected=2;
            }
        });

        bt_puntoycoma.setOnClickListener(view -> {
            if(Selected!=3){
                bt_puntoycoma.setBackgroundResource(R.drawable.botoneraprincipal_selectorgris);
                bt_puntoycoma.setTextColor(Color.WHITE);
                bt_coma.setBackgroundResource(R.drawable.stylekeycor4);
                bt_dospuntos.setBackgroundResource(R.drawable.stylekeycor4);
                bt_barra.setBackgroundResource(R.drawable.stylekeycor4);
                bt_coma.setTextColor(Color.BLACK);
                bt_dospuntos.setTextColor(Color.BLACK);
                bt_barra.setTextColor(Color.BLACK);
                Selected=3;
            }
        });
        bt_barra.setOnClickListener(view -> {
            if(Selected!=4){
                bt_barra.setBackgroundResource(R.drawable.botoneraprincipal_selectorgris);
                bt_barra.setTextColor(Color.WHITE);
                bt_coma.setBackgroundResource(R.drawable.stylekeycor4);
                bt_dospuntos.setBackgroundResource(R.drawable.stylekeycor4);
                bt_puntoycoma.setBackgroundResource(R.drawable.stylekeycor4);
                bt_coma.setTextColor(Color.BLACK);
                bt_dospuntos.setTextColor(Color.BLACK);
                bt_puntoycoma.setTextColor(Color.BLACK);
                Selected=4;
            }
        });

        bt_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(spCampo.getSelectedItemPosition()>0&&spCampo.getSelectedItemPosition()<ListElementsArrayList2.size()){
                    if(ListElementsArrayConcatFormat!=null){
                        ListElementsArrayConcatFormat.add(spCampo.getSelectedItemPosition());
                        ListElementsArrayConcat.add(ListElementsArrayList2.get(spCampo.getSelectedItemPosition()));
                        if(adapter!=null){
                            adapter.filterList(ListElementsArrayConcat);
                            adapter.notifyDataSetChanged();
                        }
                    }
                    spCampo.setSelection(0);
                    posicionconcat=-1;
                }

            }
        });

        btborrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if(posicionconcat<ListElementsArrayConcat.size()&&posicionconcat!=-1){
                   ListElementsArrayConcatFormat.remove(posicionconcat);
                   ListElementsArrayConcat.remove(posicionconcat);
                   if(adapter!=null){
                       adapter.filterList(ListElementsArrayConcat);
                       adapter.notifyDataSetChanged();
                   }
                   posicionconcat=-1;
               }
            }
        });




        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();


        Guardar.setOnClickListener(view -> {
            mainActivity.mainClass.preferencesManager.saveListConcat(ListElementsArrayConcatFormat,etiqueta,posicion);
            String separated="";
            if(Selected==1){
                mainActivity.mainClass.preferencesManager.setSeparador(",",etiqueta,posicion);
                separated=",";

            }
            if(Selected==2){
                mainActivity.mainClass.preferencesManager.setSeparador(":",etiqueta,posicion);
                separated=":";
            }
            if(Selected==3){
                mainActivity.mainClass.preferencesManager.setSeparador(";",etiqueta,posicion);
                separated=";";
            }
            if(Selected==4){
                mainActivity.mainClass.preferencesManager.setSeparador("|",etiqueta,posicion);
                separated="|";
            }

            String concat="";
            if(ListElementsArrayConcatFormat!=null){
                for(int j=0;j<ListElementsArrayConcatFormat.size();j++){
                    if(ListElementsArrayList2.size()>ListElementsArrayConcatFormat.get(j)){
                        concat=concat.concat(ListElementsArrayList2.get(ListElementsArrayConcatFormat.get(j))+separated);
                    }
                }
            }
            StringBuilder stringBuilder = new StringBuilder(concat);
            int lastCommaIndex = stringBuilder.lastIndexOf(separated);
            if (lastCommaIndex >= 0) {
                stringBuilder.deleteCharAt(lastCommaIndex);
            }
            String resultado = stringBuilder.toString();

            textView.setText(resultado);
           // SeleccionarItem(numposicion);
            dialog.cancel();
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());

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
    public int getItemViewType(int position) {
        return position;
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tv_campo,tv_textofijo,tv_textoconcatenado;
        LinearLayout ln_textofijo,ln_textoconcatenado,ln_editar;
        Spinner spCampo;


        ViewHolder(View itemView) {
            super(itemView);
            tv_campo = itemView.findViewById(R.id.tv_campo);
            tv_textofijo = itemView.findViewById(R.id.tv_textofijo);
            spCampo = itemView.findViewById(R.id.spCampo);
            tv_textoconcatenado = itemView.findViewById(R.id.tv_textoconcatenado);
            ln_textofijo = itemView.findViewById(R.id.ln_textofijo);
            ln_textoconcatenado = itemView.findViewById(R.id.ln_textoconcatenado);
            ln_editar = itemView.findViewById(R.id.ln_editar);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {
           /* if (mClickListener != null) mClickListener.onItemClick(view, getAdapterPosition());
            notifyItemChanged(selectedPos);
            selectedPos = getLayoutPosition();
            notifyItemChanged(selectedPos);*/
        }
    }


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
    public void filterList(ArrayList<EtiquetasModel> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }

    public void SeleccionarItem(int numero) {
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                try {
                    RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(numero);
                    if (viewHolder != null) {
                        viewHolder.itemView.performClick();
                    } else {
                        recyclerView.scrollToPosition(numero);
                    }
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        });
    }






}