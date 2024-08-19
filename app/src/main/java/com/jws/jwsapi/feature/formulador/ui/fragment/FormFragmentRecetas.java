package com.jws.jwsapi.feature.formulador.ui.fragment;

import static android.view.View.GONE;
import static com.jws.jwsapi.common.storage.Storage.getArchivosExtension;
import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.Teclado;
import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.TecladoEntero;
import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.TecladoFlotante;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.base.ui.activities.MainActivity;
import com.jws.jwsapi.common.users.UsersManager;
import com.jws.jwsapi.databinding.ProgFormuladorPantallaRecetasBinding;
import com.jws.jwsapi.feature.formulador.ui.adapter.FormAdapterIngredientes;
import com.jws.jwsapi.feature.formulador.ui.adapter.FormAdapterRecetas;
import com.jws.jwsapi.feature.formulador.di.RecetaManager;
import com.jws.jwsapi.feature.formulador.models.FormModelIngredientes;
import com.jws.jwsapi.feature.formulador.models.FormModelReceta;
import com.jws.jwsapi.R;
import com.jws.jwsapi.feature.formulador.ui.adapter.FormAdapterEncabezado;
import com.jws.jwsapi.feature.formulador.ui.interfaces.AdapterRecetasInterface;
import com.jws.jwsapi.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import au.com.bytecode.opencsv.CSVWriter;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FormFragmentRecetas extends Fragment implements FormAdapterEncabezado.ItemClickListener, AdapterRecetasInterface {

    @Inject
    RecetaManager recetaManager;
    @Inject
    UsersManager usersManager;
    Button bt_1,bt_2;
    TextView titulo;
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    FormAdapterEncabezado adapter;
    List<String> archivos= new ArrayList<>();
    String recetaelegida="";
    int posicion_recycler=-1;
    List<FormModelReceta> recetaseleccionada= new ArrayList<>();
    List<Integer> posiciones= new ArrayList<>();
    Boolean filtro=false;
    Boolean filtroAdapter = false;
    ProgFormuladorPantallaRecetasBinding binding;
    public RecyclerView lista_recetas;
    public FormAdapterIngredientes adapterIngredientes;
    ArrayList<FormModelIngredientes> filteredListAdapterIngredientes = new ArrayList<>();
    ArrayList<Integer> filteredListAdapterNumeric = new ArrayList<>();
    public FormAdapterRecetas adapter_recetas;

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
        archivos=filtrarRecetasEnArchivos(getArchivosExtension(".csv"));
        configuracionBotones();
        lista_recetas =view.findViewById(R.id.lista_recetas);
        recetaelegida=recetaManager.recetaActual;
        cargarRecyclerView();


    }

    public void SeleccionarItem(int numero){
       lista_recetas.post(() -> {
            try {
                Objects.requireNonNull(lista_recetas.findViewHolderForAdapterPosition(numero)).itemView.performClick();
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
        lista_recetas.smoothScrollToPosition(numero);
    }
    public void SeleccionarItem2(int numero){
        lista_recetas.post(() -> {
            try {
                Objects.requireNonNull(lista_recetas.findViewHolderForAdapterPosition(numero)).itemView.performClick();
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
        lista_recetas.smoothScrollToPosition(numero);
    }

    private void cargarRecyclerView(){
        lista_recetas.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FormAdapterEncabezado(getContext(), archivos);
        adapter.setClickListener(this);
        lista_recetas.setAdapter(adapter);

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
            adapter_recetas = new FormAdapterRecetas(getContext(), recetaManager.listRecetaActual,archivo,recetaManager,this,mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA));
            binding.receta.setAdapter(adapter_recetas);
        }else{
            recetaseleccionada=mainActivity.mainClass.getReceta(archivo);
            binding.receta.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter_recetas = new FormAdapterRecetas(getContext(), recetaseleccionada,archivo,recetaManager,this,mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA));
            binding.receta.setAdapter(adapter_recetas);
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

    }

    public void DialogoNuevaReceta(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
        View mView = getLayoutInflater().inflate(R.layout.dialogo_nuevareceta, null);
        CheckBox checkBox = ((CheckBox) mView.findViewById(R.id.checkBox));
        TextView tv_codigo=mView.findViewById(R.id.tv_codigoingrediente);
        TextView tv_descripcion=mView.findViewById(R.id.tv_descripcioningrediente);
        if(recetaseleccionada!=null&&recetaseleccionada.size()>0){
            checkBox.setVisibility(View.VISIBLE);
        }else{
            checkBox.setVisibility(View.GONE);
        }
        tv_codigo.setOnClickListener(view -> TecladoEntero(tv_codigo, "Ingrese el codigo de la receta", getContext(),null));
        tv_descripcion.setOnClickListener(view -> Teclado(tv_descripcion, "Ingrese la descripcion de la receta", getContext(), null));

        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        Guardar.setText("CREAR");
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {
            String codigoDialogo=tv_codigo.getText().toString();
            String descripcionDialogo=tv_descripcion.getText().toString();
            if(!codigoDialogo.isEmpty() && !descripcionDialogo.isEmpty()){
                agregarNuevaReceta(checkBox.isChecked(),codigoDialogo,descripcionDialogo);
            }else{
                Utils.Mensaje("Los valores ingresados no son validos",R.layout.item_customtoasterror,mainActivity);
            }
            dialog.cancel();

        });
        Cancelar.setOnClickListener(view -> dialog.cancel());
    }

    private void agregarNuevaReceta(boolean checked,String codigo, String descripcion) {
        List<String> recetascargadas=filtrarRecetasEnArchivos(getArchivosExtension(".csv"));
        List<String> codigoscargados=new ArrayList<>();
        for(int i=0;i<recetascargadas.size();i++){
            String[]arr= recetascargadas.get(i).split("_");
            if(arr.length==3){
                codigoscargados.add(arr[1].replace("_",""));
            }
        }
        int index=codigoscargados.indexOf(codigo);
        if(index==-1){
            if(checked&&recetaseleccionada!=null&&recetaseleccionada.size()>0){
                //chequear que el codigo no exista
                crearNuevaRecetaConCopia(codigo,descripcion);
            }else{
                crearNuevaRecetaBase(codigo,descripcion);
            }
        }else{
            Utils.Mensaje("Ya existe el codigo de receta ingresado",R.layout.item_customtoasterror,mainActivity);
        }
    }

    private void crearNuevaRecetaBase(String codigo, String descripcion) {
        File filePath = new File(Environment.getExternalStorageDirectory() + "/Memoria/Receta_"+codigo+"_"+descripcion+".csv");
        char separador= ',';
        CSVWriter writer;
        List<FormModelIngredientes> ing=mainActivity.mainClass.getIngredientes();
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
        archivos.add("Receta_"+codigo+"_"+descripcion);
        adapter.filterList(archivos);
        adapter.notifyDataSetChanged();
    }

    private void crearNuevaRecetaConCopia(String codigo, String descripcion) {
        File filePath = new File(Environment.getExternalStorageDirectory() + "/Memoria/Receta_"+codigo+"_"+descripcion+".csv");
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
        archivos.add("Receta_"+codigo+"_"+descripcion);
        adapter.filterList(archivos);
        binding.receta.smoothScrollToPosition(archivos.size()-1);
    }


    private List<String> filtrarRecetasEnArchivos(List<String> archivos){
        List<String> lista= new ArrayList<>();
        for(String Archivo:archivos){
            if(Archivo.startsWith("Receta_")){
                lista.add(Archivo.replace(".csv",""));
            }
        }
        return lista;
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
            if (file.exists()) {
                boolean eliminacion=file.delete();
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

            bt_3.setOnClickListener(view -> eliminarReceta());
            bt_2.setOnClickListener(view -> nuevaReceta());
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

    private void nuevaReceta() {
        if(usersManager.modificarDatos()){
            DialogoNuevaReceta();
        }else{
            Utils.Mensaje("No esta habilitado para modificar datos",R.layout.item_customtoasterror,mainActivity);
        }
    }

    private void eliminarReceta() {
        if(usersManager.modificarDatos()){
            if(posicion_recycler!=-1&&posicion_recycler<archivos.size()){
                ElimarReceta(archivos.get(posicion_recycler),posicion_recycler);
            }
        }else{
            Utils.Mensaje("No esta habilitado para modificar datos",R.layout.item_customtoasterror,mainActivity);
        }
    }

    public void Buscador() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mainActivity,R.style.AlertDialogCustom);
        View mView = mainActivity.getLayoutInflater().inflate(R.layout.dialogo_buscador, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        RecyclerView recycler = mView.findViewById(R.id.listview);
        ImageView im_buscador = mView.findViewById(R.id.im_buscador);
        Button Cancelar = mView.findViewById(R.id.buttonc);
        Cancelar.setText("CERRAR");
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        List<String>ListfilteredList =filtrarRecetasEnArchivos(getArchivosExtension(".csv"));
        filtro=false;
        recycler.setLayoutManager(new LinearLayoutManager(mainActivity));
        FormAdapterEncabezado adapter2 = new FormAdapterEncabezado(mainActivity, ListfilteredList);
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
        recycler.setAdapter(adapter2);
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
    private void filter(String text, FormAdapterEncabezado adapter2, List<String> inge) {
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

    private void dialogoEliminarPaso(List<FormModelReceta> mData, int posicion) {
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
            adapter_recetas.refrescarList(mData);
            dialog.cancel();
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());
    }

    public void BuscadorAdapter(TextView tv_codigoIngrediente, TextView tv_des) {
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
        List<FormModelIngredientes> ingredien = mainActivity.mainClass.getIngredientes();
        listview.setLayoutManager(new LinearLayoutManager(mainActivity));
        adapterIngredientes = new FormAdapterIngredientes(mainActivity, ingredien, false,null);
        adapterIngredientes.setClickListener((view, position) -> {
            tv_codigoIngrediente.setText(ingredien.get(position).getCodigo());
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
                filterAdapter(editable.toString(), dialog, ingredien, tv_codigoIngrediente, tv_des);
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

    private void filterAdapter(String text, AlertDialog dialog, List<FormModelIngredientes> inge, TextView tv_codigo, TextView tv_des) {
        filteredListAdapterIngredientes = new ArrayList<>();
        filteredListAdapterNumeric = new ArrayList<>();
        for (int i= 0; i < inge.size(); i++) {
            if (inge.get(i).getNombre().toLowerCase().contains(text.toLowerCase()) || inge.get(i).getCodigo().toLowerCase().contains(text.toLowerCase())) {
                filteredListAdapterIngredientes.add(new FormModelIngredientes(inge.get(i).getCodigo(), inge.get(i).getNombre()));
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


    private void dialogoAgregarPaso(List<FormModelReceta> mData, int posicion) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mainActivity,R.style.AlertDialogCustom);
        View mView = mainActivity.getLayoutInflater().inflate(R.layout.dialogo_pasoreceta, null);
        TextView tv_codigoIngrediente = mView.findViewById(R.id.tv_codigoingrediente);
        TextView tv_descripcionIngrediente = mView.findViewById(R.id.tv_descripcioningrediente);
        TextView tv_kilos = mView.findViewById(R.id.tv_kilos);

        Button Guardar = mView.findViewById(R.id.buttons);
        Button Cancelar = mView.findViewById(R.id.buttonc);
        Guardar.setText("AGREGAR");

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        tv_codigoIngrediente.setOnClickListener(view -> BuscadorAdapter(tv_codigoIngrediente, tv_descripcionIngrediente));
        tv_descripcionIngrediente.setOnClickListener(view -> BuscadorAdapter(tv_codigoIngrediente, tv_descripcionIngrediente));
        tv_kilos.setOnClickListener(view -> TecladoFlotante(tv_kilos, "Ingrese los kilos a realizar del nuevo paso",mainActivity,null));

        Guardar.setOnClickListener(view -> {
            String codigo=tv_codigoIngrediente.getText().toString();
            String descripcion=tv_descripcionIngrediente.getText().toString();
            String kilos=tv_kilos.getText().toString();
            if (!codigo.equals("") && !descripcion.equals("") && !kilos.equals("") && Utils.isNumeric(kilos)) {
                if (posicion < mData.size() - 1) {
                    mData.add(posicion + 1, new FormModelReceta(mData.get(posicion).getCodigo(), mData.get(posicion).getNombre(), mData.get(posicion).getKilos_totales(),
                            codigo, descripcion, kilos, "NO", ""));
                } else {
                    mData.add(new FormModelReceta(mData.get(posicion).getCodigo(), mData.get(posicion).getNombre(), mData.get(posicion).getKilos_totales(),
                            codigo, descripcion, kilos, "NO", ""));
                }
                try {
                    mainActivity.mainClass.setReceta(recetaelegida, mData);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                adapter_recetas.refrescarList(mData);
                lista_recetas.smoothScrollToPosition(mData.size() - 1);
                dialog.cancel();
            }

        });
        Cancelar.setOnClickListener(view -> dialog.cancel());
    }

    private void dialogoModificarPaso(List<FormModelReceta> mData, int posicion) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mainActivity,R.style.AlertDialogCustom);
        View mView = mainActivity.getLayoutInflater().inflate(R.layout.dialogo_pasoreceta, null);
        TextView tv_codigoIngrediente = mView.findViewById(R.id.tv_codigoingrediente);
        TextView tv_descripcionIngrediente = mView.findViewById(R.id.tv_descripcioningrediente);
        TextView tv_kilos = mView.findViewById(R.id.tv_kilos);

        Button Guardar = mView.findViewById(R.id.buttons);
        Button Cancelar = mView.findViewById(R.id.buttonc);
        Guardar.setText("EDITAR");

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        if (mData.size() > posicion) {
            tv_descripcionIngrediente.setText(mData.get(posicion).getDescrip_ing());
            tv_codigoIngrediente.setText(mData.get(posicion).getCodigo_ing());
            tv_kilos.setText(mData.get(posicion).getKilos_ing());
        }

        tv_codigoIngrediente.setOnClickListener(view -> BuscadorAdapter(tv_codigoIngrediente, tv_descripcionIngrediente));
        tv_descripcionIngrediente.setOnClickListener(view -> BuscadorAdapter(tv_codigoIngrediente, tv_descripcionIngrediente));
        tv_kilos.setOnClickListener(view -> TecladoFlotante(tv_kilos, "Ingrese los kilos del paso",mainActivity,null));
        Guardar.setOnClickListener(view -> {
            if (!tv_codigoIngrediente.getText().toString().equals("") &&
                !tv_descripcionIngrediente.getText().toString().equals("") &&
                !tv_kilos.getText().toString().equals("") && Utils.isNumeric(tv_kilos.getText().toString())) {

                if (mData.size() > posicion) {
                    mData.get(posicion).setDescrip_ing(tv_descripcionIngrediente.getText().toString());
                    mData.get(posicion).setCodigo_ing(tv_codigoIngrediente.getText().toString());
                    mData.get(posicion).setKilos_ing(tv_kilos.getText().toString());
                    try {
                        mainActivity.mainClass.setReceta(recetaelegida, mData);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    adapter_recetas.refrescarList(mData);
                }

                dialog.cancel();
            }

        });
        Cancelar.setOnClickListener(view -> dialog.cancel());
    }


    @Override
    public void eliminarPaso(List<FormModelReceta> mData, int posicion) {
        if (mData.size() > 1) {
            if(usersManager.modificarDatos()){
                dialogoEliminarPaso(mData,posicion);
            }else{
                Utils.Mensaje("No esta habilitado para modificar datos",R.layout.item_customtoasterror,mainActivity);
            }
        } else {
            Utils.Mensaje("No puede eliminar mas pasos", R.layout.item_customtoasterror,mainActivity);
        }
    }

    @Override
    public void agregarPaso(List<FormModelReceta> mData, int posicion) {
        if(usersManager.modificarDatos()){
            dialogoAgregarPaso(mData,posicion);
        }else{
            Utils.Mensaje("No esta habilitado para modificar datos",R.layout.item_customtoasterror,mainActivity);
        }
    }

    @Override
    public void modificarPaso(List<FormModelReceta> mData, int posicion) {
        if(usersManager.modificarDatos()){
            dialogoModificarPaso(mData,posicion);
        }else{
            Utils.Mensaje("No esta habilitado para modificar datos",R.layout.item_customtoasterror,mainActivity);
        }

    }
}


