package com.jws.jwsapi.feature.formulador.ui.fragment;

import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.Teclado;
import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.TecladoEntero;
import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.dialogoTexto;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.base.ui.activities.MainActivity;
import com.jws.jwsapi.common.impresora.ImprimirEstandar;
import com.jws.jwsapi.common.users.UsersManager;
import com.jws.jwsapi.databinding.ProgFormuladorPantallaIngredientesBinding;
import com.jws.jwsapi.feature.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.feature.formulador.data.repository.RecipeRepository;
import com.jws.jwsapi.feature.formulador.ui.adapter.FormAdapterIngredientes;
import com.jws.jwsapi.feature.formulador.di.RecetaManager;
import com.jws.jwsapi.feature.formulador.models.FormModelIngredientes;
import com.jws.jwsapi.R;
import com.jws.jwsapi.feature.formulador.ui.interfaces.AdapterIngredientesInterface;
import com.jws.jwsapi.feature.formulador.di.LabelManager;
import com.jws.jwsapi.utils.ToastHelper;
import com.jws.jwsapi.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FormFragmentIngredientes extends Fragment implements FormAdapterIngredientes.ItemClickListener, AdapterIngredientesInterface , ToastHelper {

    @Inject
    RecetaManager recetaManager;
    @Inject
    LabelManager labelManager;
    @Inject
    PreferencesManager preferencesManager;
    @Inject
    RecipeRepository recipeRepository;
    public static final int CANTIDAD=4;
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    FormAdapterIngredientes adapter;
    List<FormModelIngredientes> listIngredientes;
    List<FormModelIngredientes> listFilteredList = new ArrayList<>();
    int posicion_recycler=-1;
    List<Integer> posiciones= new ArrayList<>();
    Boolean filtro=false;
    ProgFormuladorPantallaIngredientesBinding binding;
    @Inject
    UsersManager usersManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = ProgFormuladorPantallaIngredientesBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        listIngredientes =recipeRepository.getIngredientes(this);
        configuracionBotones();
        cargarRecyclerView();

    }


    private void cargarRecyclerView(){
        binding.listaIngredientes.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new FormAdapterIngredientes(getContext(), listIngredientes,true,this);
        adapter.setClickListener(this);
        binding.listaIngredientes.setAdapter(adapter);

    }

    public void dialogoNuevoIngrediente(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
        View mView = getLayoutInflater().inflate(R.layout.dialogo_nuevo_ingrediente, null);
        TextView tv_codigo=  mView.findViewById(R.id.tv_codigoingrediente);
        TextView tv_descripcion=  mView.findViewById(R.id.tv_descripcioningrediente);
        TextView tv_salida= mView.findViewById(R.id.tv_salida);
        tv_salida.setText("0");
        tv_codigo.setOnClickListener(view -> TecladoEntero(tv_codigo,"Ingrese el codigo del ingrediente",mainActivity,null));
        tv_descripcion.setOnClickListener(view -> Teclado(tv_descripcion,"Ingrese la descripcion del ingrediente",mainActivity,null));
        tv_salida.setOnClickListener(view -> TecladoEntero(tv_salida,"Ingrese el numero de salida del 1 al "+CANTIDAD+" (0 para manual)",mainActivity,null));

        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        Guardar.setText("CREAR");
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {
            String codigo=tv_codigo.getText().toString();
            String descripcion=tv_descripcion.getText().toString();
            String salida=tv_salida.getText().toString();
            if(!codigo.isEmpty() && !descripcion.isEmpty()&& Utils.isNumeric(codigo)&& Utils.isNumeric(salida)&&Integer.parseInt(salida)<=CANTIDAD){
                List<FormModelIngredientes> ing=recipeRepository.getIngredientes(this);
                boolean existe=false;
                for(int i=0;i<ing.size();i++){
                    if(codigo.equals(ing.get(i).getCodigo())){
                        existe=true;
                    }
                }
                if(!existe){
                    FormModelIngredientes form_model_ingredientes=new FormModelIngredientes(codigo,descripcion,Integer.parseInt(salida));
                    ing.add(form_model_ingredientes);
                    try {
                        recipeRepository.setIngredientes(ing);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    listIngredientes.add(form_model_ingredientes);
                    adapter.refrescarList(listIngredientes);
                    binding.listaIngredientes.smoothScrollToPosition(listIngredientes.size()-1);
                }else{
                    Utils.Mensaje("Ya existe un ingrediente con el codigo ingresado",R.layout.item_customtoasterror,mainActivity);
                }
            }else{
                Utils.Mensaje("Los valores ingresados no son validos",R.layout.item_customtoasterror,mainActivity);
            }
            dialog.cancel();

        });
        Cancelar.setOnClickListener(view -> dialog.cancel());
    }


    private void configuracionBotones() {
        if (buttonProvider != null) {
            Button bt_home = buttonProvider.getButtonHome();
            Button bt_1 = buttonProvider.getButton1();
            Button bt_2 = buttonProvider.getButton2();
            Button bt_3 = buttonProvider.getButton3();
            Button bt_4 = buttonProvider.getButton4();
            Button bt_5 = buttonProvider.getButton5();
            Button bt_6 = buttonProvider.getButton6();
            buttonProvider.getTitulo().setText("INGREDIENTES");

            bt_1.setBackgroundResource(R.drawable.boton_buscar_i);
            bt_2.setBackgroundResource(R.drawable.boton_add_i);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);
            bt_home.setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());

            bt_2.setOnClickListener(view -> dialogoNuevoIngrediente());
            bt_1.setOnClickListener(view -> Buscador());

        }
    }

    public void Buscador() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(mainActivity,R.style.AlertDialogCustom);
        View mView = mainActivity.getLayoutInflater().inflate(R.layout.dialogo_buscador, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        RecyclerView listview = mView.findViewById(R.id.listview);
        ImageView im_buscador = mView.findViewById(R.id.im_buscador);
        Button Cancelar = mView.findViewById(R.id.buttonc);
        filtro=false;
        Cancelar.setText("CERRAR");
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        List<FormModelIngredientes> listIngredientes2 = recipeRepository.getIngredientes(this);
        listview.setLayoutManager(new LinearLayoutManager(mainActivity));
        FormAdapterIngredientes adapter2 = new FormAdapterIngredientes(mainActivity, listIngredientes2, false,this);
        adapter2.setClickListener((view, position) -> {
            if(!filtro){
                binding.listaIngredientes.smoothScrollToPosition(position);
            }else{
                if(posiciones.size()>position){
                    binding.listaIngredientes.smoothScrollToPosition(posiciones.get(position));
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
                filter(editable.toString(),adapter2, listIngredientes2);
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
    private void filter(String text, FormAdapterIngredientes adapter2, List<FormModelIngredientes> inge) {
        filtro=true;
        listFilteredList = new ArrayList<>();
        posiciones=new ArrayList<>();
        for (int i= 0; i < inge.size(); i++) {
            if (inge.get(i).getNombre().toLowerCase().contains(text.toLowerCase()) || inge.get(i).getCodigo().toLowerCase().contains(text.toLowerCase())) {
                listFilteredList.add(inge.get(i));
                posiciones.add(i);
            }
        }
        adapter2.refrescarList(listFilteredList);
    }

    private void dialogoEliminarIngrediente(int posicion, List<FormModelIngredientes> mData) {
        dialogoTexto(mainActivity, "¿Quiere eliminar el ingrediente " + mData.get(posicion).getNombre() + "?","ELIMINAR" ,() -> {
            mData.remove(posicion);
            try {
                recipeRepository.setIngredientes(mData);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            adapter.refrescarList(mData);
        });
    }


    @Override
    public void onItemClick(View view, int position) {
        posicion_recycler=position;
    }

    @Override
    public void eliminarIngrediente(List<FormModelIngredientes> mData, int posicion) {
        if(mData.size()>1){
            if(usersManager.modificarDatos()){
                dialogoEliminarIngrediente(posicion,mData);
            }else{
                Utils.Mensaje("No esta habilitado para modificar datos",R.layout.item_customtoasterror,mainActivity);
            }
        }else{
            Utils.Mensaje("No puede eliminar mas ingredientes",R.layout.item_customtoasterror,mainActivity);
        }
    }
    public void Imprimir(int etiqueta) {
        ImprimirEstandar imprimirEstandar = new ImprimirEstandar(getContext(), mainActivity,usersManager,preferencesManager,labelManager);
        imprimirEstandar.EnviarEtiqueta(mainActivity.mainClass.service.B,etiqueta);

    }

    @Override
    public void imprimirEtiqueta(String codigo, String nombre) {
        labelManager.ocodigoingrediente.value=codigo;
        labelManager.oingredientes.value=nombre;
        Imprimir(3);
    }

    @Override
    public void mensajeError(String str) {
        Utils.Mensaje(str,R.layout.item_customtoasterror,mainActivity);
    }

}


