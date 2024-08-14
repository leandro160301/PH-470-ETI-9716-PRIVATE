package com.jws.jwsapi.feature.formulador.ui.fragment;

import static android.view.View.GONE;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.base.activities.MainActivity;
import com.jws.jwsapi.databinding.ProgFormuladorPantallaRecetasBinding;
import com.jws.jwsapi.feature.formulador.ui.adapter.Form_Adapter_Ingredientes;
import com.jws.jwsapi.feature.formulador.ui.adapter.Form_Adapter_Recetas;
import com.jws.jwsapi.feature.formulador.di.RecetaManager;
import com.jws.jwsapi.feature.formulador.models.Form_Model_Ingredientes;
import com.jws.jwsapi.feature.formulador.models.Form_Model_Receta;
import com.jws.jwsapi.R;
import com.jws.jwsapi.feature.formulador.ui.adapter.Form_Adapter_Encabezado;
import com.jws.jwsapi.feature.formulador.ui.interfaces.AdapterRecetasInterface;
import com.jws.jwsapi.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import au.com.bytecode.opencsv.CSVWriter;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Form_Fragment_Recetas extends Fragment implements Form_Adapter_Encabezado.ItemClickListener, AdapterRecetasInterface {

    @Inject
    RecetaManager recetaManager;
    Button bt_1,bt_2;
    TextView titulo;
    TextView tv_codigo,tv_descripcion;
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    Form_Adapter_Encabezado adapter;
    List<String> archivos= new ArrayList<>();
    String recetaelegida="";
    int posicion_recycler=-1;
    String codigoDialogo="";
    String descripcionDialogo="";
    List<Form_Model_Receta> recetaseleccionada= new ArrayList<>();
    List<Integer> posiciones= new ArrayList<>();
    Boolean filtro=false;
    ProgFormuladorPantallaRecetasBinding binding;


    Boolean filtroAdapter = false;
    public Form_Adapter_Ingredientes adapterIngredientes;
    ArrayList<Form_Model_Ingredientes> filteredListAdapterIngredientes = new ArrayList<>();
    ArrayList<Integer> filteredListAdapterNumeric = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = ProgFormuladorPantallaRecetasBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        archivos=getArchivos();
        configuracionBotones();
        mainActivity.mainClass.lista_recetas =view.findViewById(R.id.lista_recetas);
        recetaelegida=recetaManager.recetaActual;
        cargarRecyclerView();


    }

    public void SeleccionarItem(int numero){
        mainActivity.mainClass.lista_recetas.post(() -> {
            try {
                Objects.requireNonNull(mainActivity.mainClass.lista_recetas.findViewHolderForAdapterPosition(numero)).itemView.performClick();
                binding.lnLista.setVisibility(GONE);
                if(recetaManager.listRecetaActual.size()>0){
                    String nomb= recetaManager.listRecetaActual.get(0).getNombre();
                    if(nomb.length()>20){
                        nomb=nomb.substring(0,20).concat("...");
                    }
                    if(titulo!=null){
                        titulo.setText(recetaManager.listRecetaActual.get(0).getCodigo() +" | "+nomb+
                                " | "+ recetaManager.listRecetaActual.get(0).getKilos_totales() +mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA));
                    }
                }
                if (buttonProvider != null) {
                    bt_1.setVisibility(GONE);
                    bt_2.setVisibility(GONE);
                }
            } catch (Exception exception) {
                System.out.println("error item click:" + exception.getMessage());
                exception.printStackTrace();
            }
        });
        mainActivity.mainClass.lista_recetas.smoothScrollToPosition(numero);
    }
    public void SeleccionarItem2(int numero){
        mainActivity.mainClass.lista_recetas.post(() -> {
            try {
                Objects.requireNonNull(mainActivity.mainClass.lista_recetas.findViewHolderForAdapterPosition(numero)).itemView.performClick();
                if(recetaManager.listRecetaActual.size()>0){
                    String nomb= recetaManager.listRecetaActual.get(0).getNombre();
                    if(nomb.length()>20){
                        nomb=nomb.substring(0,20).concat("...");
                    }
                    if(titulo!=null){
                        titulo.setText(recetaManager.listRecetaActual.get(0).getCodigo() +" | "+nomb+
                                " | "+ recetaManager.listRecetaActual.get(0).getKilos_totales() +mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA));
                    }
                }
            } catch (Exception exception) {
                System.out.println("error item click:" + exception.getMessage());
                exception.printStackTrace();
            }
        });
        mainActivity.mainClass.lista_recetas.smoothScrollToPosition(numero);
    }

    private void cargarRecyclerView(){
        mainActivity.mainClass.lista_recetas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Form_Adapter_Encabezado(getContext(), archivos);
        adapter.setClickListener(this);
        mainActivity.mainClass.lista_recetas.setAdapter(adapter);

        if(recetaManager.ejecutando){
            int item=archivos.indexOf(recetaManager.recetaActual);
            if(item>-1&&item<archivos.size()){
                SeleccionarItem(item);
            }

        }

    }

    private void cargarRecyclerViewReceta(String archivo){
        if(recetaManager.ejecutando){
            binding.receta.setLayoutManager(new LinearLayoutManager(getContext()));
            mainActivity.mainClass.adapter_recetas = new Form_Adapter_Recetas(getContext(), recetaManager.listRecetaActual,archivo,recetaManager,this,mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA));
            binding.receta.setAdapter(mainActivity.mainClass.adapter_recetas);
        }else{
            recetaseleccionada=mainActivity.mainClass.getReceta(archivo);
            binding.receta.setLayoutManager(new LinearLayoutManager(getContext()));
            mainActivity.mainClass.adapter_recetas = new Form_Adapter_Recetas(getContext(), recetaseleccionada,archivo,recetaManager,this,mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA));
            binding.receta.setAdapter(mainActivity.mainClass.adapter_recetas);
            String[]arr= archivo.split("_");
            if(arr.length==3&&recetaseleccionada.size()>0){
                String nomb=arr[2].replace("_","");
                if(nomb.length()>20){
                    nomb=nomb.substring(0,20).concat("...");
                }
                if(titulo!=null){
                    titulo.setText(arr[1].replace("_","")+" | "+nomb+
                            " | "+ recetaseleccionada.get(0).getKilos_totales() +mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA));
                }

            }

        }

        /*int resId = R.anim.recycler1;
        LayoutAnimationController animation = AnimationUtils.loadLayoutAnimation(getContext(), resId);
        receta.setLayoutAnimation(animation);
        adapter_recetas.notifyDataSetChanged();*/

    }

    public List<String> getArchivos() {
        String[] List = new String[]{};
        List<String> Lista=new ArrayList<>(Arrays.asList(List));
        File[] filearrays4;
        File root2 = new File(Environment.getExternalStorageDirectory().toString()+"/Memoria");

        if(root2.exists()){
            filearrays4 = root2.listFiles((dir, filename) -> filename.toLowerCase().endsWith(".csv"));
            StringBuilder f = new StringBuilder();

            if(filearrays4.length>0){
                f = new StringBuilder();
                for(int i=0; i < filearrays4.length; i++){
                    f.append(filearrays4[i].getName());
                    String archivo=f.toString();
                    if(archivo.startsWith("Receta_")){
                        Lista.add(archivo.replace(".csv",""));
                    }

                    f = new StringBuilder();

                }
            }

        }
        return Lista;
    }

    public void DialogoSeteoVariables(TextView textViewelegido,String texto){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialogo_dosopciones, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        final LinearLayout delete_text= mView.findViewById(R.id.lndelete_text);
        userInput.setOnLongClickListener(v -> true);
        delete_text.setOnClickListener(view -> userInput.setText(""));
        TextView textView=mView.findViewById(R.id.textViewt);
        textView.setText(texto);

        if(textViewelegido==tv_codigo){
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {
            if(textViewelegido== tv_codigo){
                tv_codigo.setText(userInput.getText().toString());
                codigoDialogo=userInput.getText().toString();
            }
            if(textViewelegido== tv_descripcion){
                tv_descripcion.setText(userInput.getText().toString());
                descripcionDialogo=userInput.getText().toString();
            }
            dialog.cancel();
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());

    }

    public void DialogoNuevaReceta(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
        View mView = getLayoutInflater().inflate(R.layout.dialogo_nuevareceta, null);
        CheckBox checkBox = ((CheckBox) mView.findViewById(R.id.checkBox));

        if(recetaseleccionada!=null&&recetaseleccionada.size()>0){
            checkBox.setVisibility(View.VISIBLE);
        }else{
            checkBox.setVisibility(View.GONE);
        }

        tv_codigo.setOnClickListener(view -> DialogoSeteoVariables(tv_codigo,"Ingrese el codigo de la receta"));
        tv_descripcion.setOnClickListener(view -> DialogoSeteoVariables(tv_descripcion,"Ingrese la descripcion de la receta"));

        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        Guardar.setText("CREAR");
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {

            if(!Objects.equals(codigoDialogo, "") && !Objects.equals(descripcionDialogo, "")){
                List<String> recetascargadas=getArchivos();
                List<String> codigoscargados=new ArrayList<>();
                for(int i=0;i<recetascargadas.size();i++){
                    String[]arr= recetascargadas.get(i).split("_");
                    if(arr.length==3){
                        codigoscargados.add(arr[1].replace("_",""));
                    }
                }

                int index=codigoscargados.indexOf(codigoDialogo);
                if(index==-1){
                    if(checkBox.isChecked()&&recetaseleccionada!=null&&recetaseleccionada.size()>0){
                        //chequear que el codigo no exista
                        File filePath = new File(Environment.getExternalStorageDirectory() + "/Memoria/Receta_"+codigoDialogo+"_"+descripcionDialogo+".csv");
                        char separador= ',';
                        CSVWriter writer;
                        try {

                            writer = new CSVWriter(new FileWriter(filePath.getAbsolutePath(), true), separador);
                            for(int i=0;i<recetaseleccionada.size();i++){
                                writer.writeNext(new String[]{recetaseleccionada.get(i).getCodigo_ing(), recetaseleccionada.get(i).getDescrip_ing(), recetaseleccionada.get(i).getKilos_ing()});
                            }

                            writer.close();

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        archivos.add("Receta_"+codigoDialogo+"_"+descripcionDialogo);
                        adapter.filterList(archivos);
                        binding.receta.smoothScrollToPosition(archivos.size()-1);
                        codigoDialogo="";
                        descripcionDialogo="";
                    }else{
                        //chequear que el codigo no exista
                        File filePath = new File(Environment.getExternalStorageDirectory() + "/Memoria/Receta_"+codigoDialogo+"_"+descripcionDialogo+".csv");
                        char separador= ',';
                        CSVWriter writer;
                        List<Form_Model_Ingredientes> ing=mainActivity.mainClass.getIngredientes();
                        String cod_nueva="001";
                        String des_nueva="Ingrediente 1";
                        if(ing.size()>0){
                            cod_nueva=ing.get(0).getCodigo();
                            des_nueva=ing.get(0).getNombre();
                        }
                        try {
                            writer = new CSVWriter(new FileWriter(filePath.getAbsolutePath(), true), separador);
                            writer.writeNext(new String[]{cod_nueva,des_nueva,"1.5"});
                            writer.close();

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        archivos.add("Receta_"+codigoDialogo+"_"+descripcionDialogo);
                        adapter.filterList(archivos);
                        adapter.notifyDataSetChanged();
                        codigoDialogo="";
                        descripcionDialogo="";
                    }
                }else{
                    Utils.Mensaje("Ya existe el codigo de receta ingresado",R.layout.item_customtoasterror,mainActivity);
                }

            }else{
                Utils.Mensaje("Los valores ingresados no son validos",R.layout.item_customtoasterror,mainActivity);
            }
            dialog.cancel();

        });
        Cancelar.setOnClickListener(view -> {
            codigoDialogo="";
            descripcionDialogo="";
            dialog.cancel();
        });
    }

    private void ElimarReceta(String receta,int posicion_recycler) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(),R.style.AlertDialogCustom);

        View mView = getLayoutInflater().inflate(R.layout.dialogo_dossinet, null);
        TextView textView=mView.findViewById(R.id.textViewt);
        textView.setText("¿Esta seguro de eliminar la receta "+receta+" ?");

        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);
        Guardar.setText("ELIMINAR");

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {
            File file= new File("/storage/emulated/0/Memoria/"+receta+".csv");
            if (file != null && file.exists()) {
                Boolean eliminacion=file.delete();
                if(eliminacion){
                    Utils.Mensaje("Receta eliminada", R.layout.item_customtoastok,mainActivity);
                    recetaManager.recetaActual ="";
                    mainActivity.mainClass.preferencesManager.setRecetaactual("");
                    mainActivity.mainClass.preferencesManager.setCodigoRecetaactual("");
                    mainActivity.mainClass.ocodigoreceta.value="";
                    recetaManager.codigoReceta ="";
                    mainActivity.mainClass.preferencesManager.setNombreRecetaactual("");
                    mainActivity.mainClass.oreceta.value="";
                    recetaManager.nombreReceta ="";
                    archivos.remove(posicion_recycler);
                    adapter.filterList(archivos);
                    adapter.notifyDataSetChanged();

                }else{
                    Utils.Mensaje("La receta no se pudo borrar", R.layout.item_customtoasterror,mainActivity);
                }
            }else{
                Utils.Mensaje("La receta no esta disponible", R.layout.item_customtoasterror,mainActivity);
            }
            dialog.cancel();
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());
    }

    private void configuracionBotones() {
        if (buttonProvider != null) {
            Button bt_home = buttonProvider.getButtonHome();
            bt_1 = buttonProvider.getButton1();
            bt_2 = buttonProvider.getButton2();
            Button bt_3 = buttonProvider.getButton3();
            Button bt_4 = buttonProvider.getButton4();
            Button bt_5 = buttonProvider.getButton5();
            Button bt_6 = buttonProvider.getButton6();
            titulo=buttonProvider.getTitulo();
            bt_1.setBackgroundResource(R.drawable.boton_buscar_i);
            bt_2.setBackgroundResource(R.drawable.boton_add_i);
            bt_3.setBackgroundResource(R.drawable.boton_eliminar_i);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);
            bt_home.setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());

            bt_3.setOnClickListener(view -> {
                if(mainActivity.modificarDatos()){
                    if(posicion_recycler!=-1&&posicion_recycler<archivos.size()){
                        ElimarReceta(archivos.get(posicion_recycler),posicion_recycler);
                    }
                }else{
                    Utils.Mensaje("No esta habilitado para modificar datos",R.layout.item_customtoasterror,mainActivity);
                }

            });

            bt_2.setOnClickListener(view -> {
                if(mainActivity.modificarDatos()){
                    DialogoNuevaReceta();
                }else{
                    Utils.Mensaje("No esta habilitado para modificar datos",R.layout.item_customtoasterror,mainActivity);
                }
            });

            bt_1.setOnClickListener(view ->Buscador());

            if(recetaManager.ejecutando){
                int item=archivos.indexOf(recetaManager.recetaActual);
                if(item>-1&&item<archivos.size()){
                    bt_1.setVisibility(GONE);
                    bt_2.setVisibility(GONE);
                    bt_3.setVisibility(GONE);
                }
            }

        }
    }

    public void Buscador() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mainActivity,R.style.AlertDialogCustom);
        View mView = mainActivity.getLayoutInflater().inflate(R.layout.dialogo_buscador, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        RecyclerView listview = mView.findViewById(R.id.listview);
        ImageView im_buscador = mView.findViewById(R.id.im_buscador);
        Button Cancelar = mView.findViewById(R.id.buttonc);
        Cancelar.setText("CERRAR");
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        List<String>ListfilteredList = getArchivos();
        filtro=false;
        listview.setLayoutManager(new LinearLayoutManager(mainActivity));
        Form_Adapter_Encabezado adapter2 = new Form_Adapter_Encabezado(mainActivity, ListfilteredList);
        adapter2.setClickListener((view, position) -> {
            if(!filtro){
                SeleccionarItem2(position);
            }else{
                if(posiciones.size()>position){
                    SeleccionarItem2(posiciones.get(position));
                }
            }

            dialog.cancel();
        });
        listview.setAdapter(adapter2);
        userInput.setOnLongClickListener(v -> true);
        userInput.setHint(" Buscar");
        userInput.setOnClickListener(view -> userInput.getText().clear());
        userInput.requestFocus();

        userInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString(),adapter2, ListfilteredList);
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
    private void filter(String text, Form_Adapter_Encabezado adapter2, List<String> inge) {
        filtro=true;
        posiciones=new ArrayList<>();
        List<String> ListfilteredList = new ArrayList<>();
        for (int i = 0; i < inge.size(); i++) {
            if (inge.get(i).toLowerCase().contains(text.toLowerCase())) {
                ListfilteredList .add(inge.get(i));
                posiciones.add(i);
            }
        }
        adapter2.filterList(ListfilteredList);
    }


    @Override
    public void onItemClick(View view, int position) {
        posicion_recycler=position;
        cargarRecyclerViewReceta(archivos.get(position));
        recetaManager.recetaActual =archivos.get(posicion_recycler);
        mainActivity.mainClass.preferencesManager.setRecetaactual(archivos.get(posicion_recycler));

        if(!Objects.equals(recetaManager.recetaActual, "")){
            String[]arr= recetaManager.recetaActual.split("_");
            if(arr.length==3){
                recetaManager.codigoReceta =arr[1].replace("_","");
                recetaManager.nombreReceta =arr[2].replace("_","");
                mainActivity.mainClass.ocodigoreceta.value=recetaManager.codigoReceta;
                mainActivity.mainClass.oreceta.value=recetaManager.nombreReceta;
                mainActivity.mainClass.preferencesManager.setCodigoRecetaactual(recetaManager.codigoReceta);
                mainActivity.mainClass.preferencesManager.setNombreRecetaactual(recetaManager.nombreReceta);
            }
        }

    }

    private void dialogoEliminarPaso(List<Form_Model_Receta> mData, int posicion) {
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
            mainActivity.mainClass.adapter_recetas.refrescarList(mData);
            dialog.cancel();
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());
    }

    public void BuscadorAdapter(TextView tv_codigo, TextView tv_des) {
        filtroAdapter = false;

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
        adapterIngredientes = new Form_Adapter_Ingredientes(mainActivity, ingredien, false,null);
        adapterIngredientes.setClickListener((view, position) -> {
            tv_codigo.setText(ingredien.get(position).getCodigo());
            tv_des.setText(ingredien.get(position).getNombre());

            dialog.cancel();
        });

        listview.setAdapter(adapterIngredientes);
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
                filtroAdapter = true;
                filterAdapter(editable.toString(), dialog, ingredien, tv_codigo, tv_des);
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

    private void filterAdapter(String text, AlertDialog dialog, List<Form_Model_Ingredientes> inge, TextView tv_codigo, TextView tv_des) {
        filteredListAdapterIngredientes = new ArrayList<>();
        filteredListAdapterNumeric = new ArrayList<>();
        for (int i= 0; i < inge.size(); i++) {
            if (inge.get(i).getNombre().toLowerCase().contains(text.toLowerCase()) || inge.get(i).getCodigo().toLowerCase().contains(text.toLowerCase())) {
                filteredListAdapterIngredientes.add(new Form_Model_Ingredientes(inge.get(i).getCodigo(), inge.get(i).getNombre()));
                filteredListAdapterNumeric.add(i);
            }
        }
        adapterIngredientes.filterList(filteredListAdapterIngredientes);
        adapterIngredientes.setClickListener((view, position) -> {
            tv_codigo.setText(filteredListAdapterIngredientes.get(position).getCodigo());
            tv_des.setText(filteredListAdapterIngredientes.get(position).getNombre());
            dialog.cancel();
        });
    }


    private void dialogoAgregarPaso(List<Form_Model_Receta> mData, int posicion) {
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

        tv_codigo.setOnClickListener(view -> BuscadorAdapter(tv_codigo, tv_descripcion));
        tv_descripcion.setOnClickListener(view -> BuscadorAdapter(tv_codigo, tv_descripcion));
        tv_kilos.setOnClickListener(view -> TecladoAdapter(tv_kilos, "Ingrese los kilos a realizar del nuevo paso"));

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
                mainActivity.mainClass.adapter_recetas.refrescarList(mData);
                mainActivity.mainClass.lista_recetas.smoothScrollToPosition(mData.size() - 1);
                dialog.cancel();
            }

        });
        Cancelar.setOnClickListener(view -> dialog.cancel());
    }

    private void dialogoModificarPaso(List<Form_Model_Receta> mData, int posicion) {
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

        tv_codigo.setOnClickListener(view -> BuscadorAdapter(tv_codigo, tv_descripcion));
        tv_descripcion.setOnClickListener(view -> BuscadorAdapter(tv_codigo, tv_descripcion));
        tv_kilos.setOnClickListener(view -> TecladoAdapter(tv_kilos, "Ingrese los kilos del paso"));

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
                    mainActivity.mainClass.adapter_recetas.refrescarList(mData);
                }

                dialog.cancel();
            }

        });
        Cancelar.setOnClickListener(view -> dialog.cancel());
    }


    public void TecladoAdapter(TextView View, String texto) {

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

        lndelete_text.setOnClickListener(view -> userInput.setText(""));
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

    @Override
    public void eliminarPaso(List<Form_Model_Receta> mData, int posicion) {
        if (mData.size() > 1) {
            if(mainActivity.modificarDatos()){
                dialogoEliminarPaso(mData,posicion);
            }else{
                Utils.Mensaje("No esta habilitado para modificar datos",R.layout.item_customtoasterror,mainActivity);
            }
        } else {
            Utils.Mensaje("No puede eliminar mas pasos", R.layout.item_customtoasterror,mainActivity);
        }
    }

    @Override
    public void agregarPaso(List<Form_Model_Receta> mData, int posicion) {
        if(mainActivity.modificarDatos()){
            dialogoAgregarPaso(mData,posicion);
        }else{
            Utils.Mensaje("No esta habilitado para modificar datos",R.layout.item_customtoasterror,mainActivity);
        }
    }

    @Override
    public void modificarPaso(List<Form_Model_Receta> mData, int posicion) {
        if(mainActivity.modificarDatos()){
            dialogoModificarPaso(mData,posicion);
        }else{
            Utils.Mensaje("No esta habilitado para modificar datos",R.layout.item_customtoasterror,mainActivity);
        }

    }
}


