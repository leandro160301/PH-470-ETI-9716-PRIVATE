package com.jws.jwsapi.core.label;

import static com.jws.jwsapi.dialog.DialogUtil.keyboard;
import static com.jws.jwsapi.utils.SpinnerHelper.setupSpinner;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.AdapterCommon;
import com.jws.jwsapi.core.printer.PrinterPreferences;
import com.jws.jwsapi.utils.AdapterHelper;
import com.jws.jwsapi.utils.ToastHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LabelAdapter extends RecyclerView.Adapter<LabelAdapter.ViewHolder> {

    private int selectedPos = RecyclerView.NO_POSITION;
    private List<LabelModel> mData;
    private final LayoutInflater mInflater;
    private ItemClickListener mClickListener;
    private List<String> listaVariables;
    public List<Integer> ListElementsInt,ListElementsInternaInt,ListElementsInternalConcat,ListElementsPosicionesTipo,ListElementsArrayConcatFormat;
    public List<String> ListElementsFijo,ListElementsInternaFijo;
    private final Context context;
    private static MainActivity mainActivity;
    public String etiqueta;
    int Selected=0;
    int posicionconcat=-1;
    public int numposicion;
    private int lastPositionAdapter = -1;
    LabelManager labelManager;
    PrinterPreferences printerPreferences;

    public LabelAdapter(Context context, List<LabelModel> mData, MainActivity mainActivity, String etiqueta, int numposicion, LabelManager labelManager, PrinterPreferences printerPreferences) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = mData;
        this.context=context;
        this.mainActivity=mainActivity;
        this.etiqueta=etiqueta;
        this.numposicion=numposicion;
        this.labelManager=labelManager;
        this.printerPreferences = printerPreferences;

        setupVariablesList();
        setupSpinnerList();
        ListElementsPosicionesTipo = new ArrayList<>(Collections.nCopies(listaVariables.size(), 0));//inicia en cero con la cantidad de listavariables
        setupTextoFijo();

    }

    private void setupVariablesList() {
        listaVariables =new ArrayList<>();
        for(int i = 0; i<labelManager.constantPrinterList.size(); i++){
            listaVariables.add(labelManager.constantPrinterList.get(i));
        }
        for(int i = 0; i<labelManager.varPrinterList.size(); i++){
            listaVariables.add(labelManager.varPrinterList.get(i).descripcion);
        }
    }

    private void setupSpinnerList() {
        ListElementsInt= printerPreferences.getListSpinner(etiqueta);
        if(ListElementsInt==null){
            ListElementsInt = new ArrayList<>(Collections.nCopies(mData.size(), 0));
        }
        if(ListElementsInt.size()<mData.size()){// esto se agrego para el caso de que carguen una actualizacion de la etiqueta con mas campos
            ListElementsInt = new ArrayList<>(Collections.nCopies(mData.size(), 0));
        }
        ListElementsInternaInt=ListElementsInt;
    }

    private void setupTextoFijo() {
        ListElementsFijo= printerPreferences.getListFijo(etiqueta);
        if(ListElementsFijo==null){
            ListElementsFijo = new ArrayList<>(Collections.nCopies(mData.size(), ""));
        }
        if(ListElementsFijo.size()<mData.size()){// esto se agrego para el caso de que carguen una actualizacion de la etiqueta con mas campos
            ListElementsFijo = new ArrayList<>(Collections.nCopies(mData.size(), ""));
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
            setupSpinner(holder.spCampo,context.getApplicationContext(),listaVariables);
            holder.tv_campo.setText(mData.get(posi).getFieldName());
            updateViews(holder, posi);
        } catch (Exception e) {
            e.printStackTrace();
            ToastHelper.message("Ocurrió un error:"+e.getMessage(), R.layout.item_customtoasterror,mainActivity);
        }

        holder.spCampo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                spinnerSelection(i, posi, holder);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
        holder.ln_editar.setOnClickListener(view -> editClick(holder, posi));

        lastPositionAdapter = AdapterHelper.setAnimationSlideInLeft(holder.itemView, position, lastPositionAdapter, context);
        holder.itemView.setSelected(selectedPos == posi);
    }

    private void updateViews(ViewHolder holder, int posi) {
        if(ListElementsInt!=null&&ListElementsInt.size()> posi){
            holder.spCampo.setSelection(ListElementsInt.get(posi));
            if(ListElementsInt.get(posi)==labelManager.constantPrinterList.size()-1){
                holder.tv_textoconcatenado.setText("A,SD,AS,D");
                if(ListElementsPosicionesTipo.size()> posi){
                    ListElementsPosicionesTipo.set(posi,2);
                }
                holder.ln_textoconcatenado.setVisibility(View.VISIBLE);
                holder.ln_editar.setVisibility(View.VISIBLE);
                holder.ln_textofijo.setVisibility(View.GONE);
            }else{
                holder.ln_textoconcatenado.setVisibility(View.GONE);
                holder.ln_editar.setVisibility(View.GONE);
            }
            if(ListElementsInt.get(posi)!=labelManager.constantPrinterList.size()-1){
                if(ListElementsInt.get(posi)==labelManager.constantPrinterList.size()-2){
                    holder.ln_textoconcatenado.setVisibility(View.GONE);
                    if(ListElementsPosicionesTipo.size()> posi){
                        ListElementsPosicionesTipo.set(posi,1);
                    }
                    holder.ln_editar.setVisibility(View.VISIBLE);
                    if(ListElementsFijo!=null&&ListElementsFijo.size()> posi){
                        holder.tv_textofijo.setText(ListElementsFijo.get(posi));
                    }
                }else{
                    holder.ln_textofijo.setVisibility(View.GONE);
                    holder.ln_editar.setVisibility(View.GONE);
                }
            }

        }
    }

    private void editClick(ViewHolder holder, int posi) {
        if(ListElementsPosicionesTipo.size()> posi){
            if(ListElementsPosicionesTipo.get(posi)==2){
                DialogoConcatenar(etiqueta, posi, holder.tv_textoconcatenado);
            }
            if(ListElementsPosicionesTipo.get(posi)==1){
                keyboard(holder.tv_textofijo, "Ingrese el texto fijo", mainActivity, texto -> {
                    if(ListElementsInternaFijo!=null&&ListElementsInternaFijo.size()>posi){
                        ListElementsInternaFijo.set(posi,texto);
                    }
                });
            }
        }
    }

    private void spinnerSelection(int i, int posi, ViewHolder holder) {
        try {
             if(ListElementsInt!=null&&ListElementsInt.size()> posi){
                 ListElementsInternaInt.set(posi, i);
                 if(i ==labelManager.constantPrinterList.size()-1){
                     ListElementsInternalConcat= printerPreferences.getListConcat(etiqueta, posi);
                     String concat="";
                     String sepa= printerPreferences.getSeparador(etiqueta, posi);
                     if(ListElementsInternalConcat!=null){
                         for(int j=0;j<ListElementsInternalConcat.size();j++){
                             if(listaVariables.size()>ListElementsInternalConcat.get(j)){
                                 concat=concat.concat(listaVariables.get(ListElementsInternalConcat.get(j))+sepa);
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
                     if(ListElementsPosicionesTipo.size()> posi){
                         ListElementsPosicionesTipo.set(posi,2);
                     }
                 }else{
                     holder.ln_textoconcatenado.setVisibility(View.GONE);
                     holder.ln_editar.setVisibility(View.GONE);
                 }
                 if(i !=labelManager.constantPrinterList.size()-1){
                     if(i ==labelManager.constantPrinterList.size()-2){
                         if(ListElementsFijo!=null&&ListElementsFijo.size()> posi){
                             holder.tv_textofijo.setText(ListElementsFijo.get(posi));
                         }
                         if(ListElementsPosicionesTipo.size()> posi){
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
            ToastHelper.message("Ocurrió un error:"+e.getMessage(), R.layout.item_customtoasterror,mainActivity);
        }
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
        setupSpinner(spCampo,context.getApplicationContext(),listaVariables);

        AdapterCommon adapter;
        listview.setLayoutManager(new LinearLayoutManager(context));
        ListElementsArrayConcatFormat= printerPreferences.getListConcat(etiqueta,posicion);
        List<String> ListElementsArrayConcat=new ArrayList<>();
        if(ListElementsArrayConcatFormat==null){
            ListElementsArrayConcatFormat=new ArrayList<>();
        }
        for(int i=0;i<ListElementsArrayConcatFormat.size();i++){
            if(ListElementsArrayConcatFormat.get(i)< listaVariables.size()){
                ListElementsArrayConcat.add(listaVariables.get(ListElementsArrayConcatFormat.get(i)));
            }
        }
        adapter = new AdapterCommon(context, ListElementsArrayConcat);
        adapter.setClickListener((view, position) -> posicionconcat=position);
        listview.setAdapter(adapter);


        Selected=0;
        String separacion= printerPreferences.getSeparador(etiqueta,posicion);
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
        Button[] buttons = {bt_coma, bt_dospuntos, bt_puntoycoma, bt_barra};
        bt_coma.setOnClickListener(view -> updateButtonStates(1,buttons));
        bt_dospuntos.setOnClickListener(view -> updateButtonStates(2,buttons));
        bt_puntoycoma.setOnClickListener(view -> updateButtonStates(3,buttons));
        bt_barra.setOnClickListener(view -> updateButtonStates(4,buttons));

        bt_add.setOnClickListener(view -> btAddClick(spCampo, adapter, ListElementsArrayConcat));
        btborrar.setOnClickListener(view -> btBorrarClick(adapter, ListElementsArrayConcat));

        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {
            printerPreferences.saveListConcat(ListElementsArrayConcatFormat,etiqueta,posicion);
            String separated="";
            if(Selected==1){
                printerPreferences.setSeparador(",",etiqueta,posicion);
                separated=",";
            }
            if(Selected==2){
                printerPreferences.setSeparador(":",etiqueta,posicion);
                separated=":";
            }
            if(Selected==3){
                printerPreferences.setSeparador(";",etiqueta,posicion);
                separated=";";
            }
            if(Selected==4){
                printerPreferences.setSeparador("|",etiqueta,posicion);
                separated="|";
            }

            String concat="";
            if(ListElementsArrayConcatFormat!=null){
                for(int j=0;j<ListElementsArrayConcatFormat.size();j++){
                    if(listaVariables.size()>ListElementsArrayConcatFormat.get(j)){
                        concat=concat.concat(listaVariables.get(ListElementsArrayConcatFormat.get(j))+separated);
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
            dialog.cancel();
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());

    }

    private void btBorrarClick(AdapterCommon adapter, List<String> ListElementsArrayConcat) {
        if(posicionconcat< ListElementsArrayConcat.size()&&posicionconcat!=-1){
            ListElementsArrayConcatFormat.remove(posicionconcat);
            ListElementsArrayConcat.remove(posicionconcat);
            if(adapter !=null){
                adapter.filterList(ListElementsArrayConcat);
                adapter.notifyDataSetChanged();
            }
            posicionconcat=-1;
        }
    }

    private void btAddClick(Spinner spCampo, AdapterCommon adapter, List<String> ListElementsArrayConcat) {
        if(spCampo.getSelectedItemPosition()>0&& spCampo.getSelectedItemPosition()< listaVariables.size()){
            if(ListElementsArrayConcatFormat!=null){
                ListElementsArrayConcatFormat.add(spCampo.getSelectedItemPosition());
                ListElementsArrayConcat.add(listaVariables.get(spCampo.getSelectedItemPosition()));
                if(adapter !=null){
                    adapter.filterList(ListElementsArrayConcat);
                    adapter.notifyDataSetChanged();
                }
            }
            spCampo.setSelection(0);
            posicionconcat=-1;
        }
    }

    private void updateButtonStates(int selectedButtonId, Button[] buttons) {
        int[] buttonIds = {1, 2, 3, 4};

        for (int i = 0; i < buttons.length; i++) {
            Button button = buttons[i];
            if (buttonIds[i] == selectedButtonId) {
                button.setBackgroundResource(R.drawable.botoneraprincipal_selectorgris);
                button.setTextColor(Color.WHITE);
            } else {
                button.setBackgroundResource(R.drawable.stylekeycor4);
                button.setTextColor(Color.BLACK);
            }
        }
        Selected = selectedButtonId;
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
        }
    }

    public void setClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    public interface ItemClickListener {
        void onItemClick(View view, int position);
    }
    public void removeAt(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, mData.size());
    }
    public void filterList(ArrayList<LabelModel> filteredList) {
        mData = filteredList;
        notifyDataSetChanged();
    }

}