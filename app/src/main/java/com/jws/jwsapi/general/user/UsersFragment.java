package com.jws.jwsapi.general.user;

import static com.jws.jwsapi.general.dialog.DialogUtil.dialogText;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.general.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.general.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UsersFragment extends Fragment implements UserAdapter.ItemClickListener, UserButtonClickListener {

    Button bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6;
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    TextView tv_nnombre, tv_nusuario, tv_ncontrasena,tvcodigo;
    RecyclerView recycler_usuarios;
    UserAdapter adapter;
    List<UserModel> ListElementsArrayList=new ArrayList<>();
    String nusuario="",nnombre="",ncontrasena="",ncodigo="";
    @Inject
    UsersManager usersManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standar_usuarios,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        configuracionBotones();
        recycler_usuarios =view.findViewById(R.id.listausuarios);
        DevuelveElementos();

        recycler_usuarios.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserAdapter(getContext(), ListElementsArrayList,this);
        adapter.setClickListener(this);
        recycler_usuarios.setAdapter(adapter);


    }
    @Override
    public void onItemClick(View view, int position) {

    }

    private void configuracionBotones() {
        if (buttonProvider != null) {
            bt_home = buttonProvider.getButtonHome();
            bt_1 = buttonProvider.getButton1();
            bt_2 = buttonProvider.getButton2();
            bt_3 = buttonProvider.getButton3();
            bt_4 = buttonProvider.getButton4();
            bt_5 = buttonProvider.getButton5();
            bt_6 = buttonProvider.getButton6();
            buttonProvider.getTitulo().setText("USUARIOS");

            bt_1.setBackgroundResource(R.drawable.boton_add_i);
            bt_2.setVisibility(View.INVISIBLE);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);

            bt_1.setOnClickListener(view -> DialogoNuevoUsuario());
            bt_home.setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());

        }
    }

    public void DevuelveElementos(){

        List<UserModel> lista= usersManager.obtenerUsuarios();
        for(int i=0;i<lista.size();i++){
            ListElementsArrayList.add(new UserModel(lista.get(i).id,lista.get(i).nombre,lista.get(i).usuario,lista.get(i).password, lista.get(i).codigo,
                    lista.get(i).tipo));
        }

    }



    public void DialogoNuevoUsuario(){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(),R.style.AlertDialogCustom);
        View mView = getLayoutInflater().inflate(R.layout.dialogo_usuario, null);


        tv_nnombre =  mView.findViewById(R.id.tvnNombre);
        tv_nusuario =  mView.findViewById(R.id.tvnUsuario);
        tv_ncontrasena =  mView.findViewById(R.id.tvnContrasena);
        tvcodigo= mView.findViewById(R.id.tvcodigo);
        Spinner spinnertipo=mView.findViewById(R.id.spinnertipo);
        String[] Balanzas_arrtipo = getResources().getStringArray(R.array.tipoUsuarios);
        ArrayAdapter<String> adapter7 = new ArrayAdapter<>(requireContext(),R.layout.item_spinner,Balanzas_arrtipo);
        adapter7.setDropDownViewResource(R.layout.item_spinner);
        spinnertipo.setAdapter(adapter7);
        spinnertipo.setPopupBackgroundResource(R.drawable.campollenarclickeable);

        tv_nnombre.setOnClickListener(view -> DialogoSeteoVariablesNuevoUsuario(tv_nnombre));
        tv_nusuario.setOnClickListener(view -> DialogoSeteoVariablesNuevoUsuario(tv_nusuario));
        tv_ncontrasena.setOnClickListener(view -> DialogoSeteoVariablesNuevoUsuario(tv_ncontrasena));
        tvcodigo.setOnClickListener(view -> DialogoSeteoVariablesNuevoUsuario(tvcodigo));


        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {
            if(usersManager.getNivelUsuario()>2){
                if(!nnombre.equals("")&&!nusuario.equals("")
                        &&!ncontrasena.equals("")&&!ncodigo.equals("")){

                    long id=-1;

                    try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(getContext(), UsersManager.DB_USERS_NAME, null, UsersManager.DB_USERS_VERSION)) {
                        id=dbHelper.nuevoUsuario(nnombre,nusuario,ncontrasena,ncodigo,spinnertipo.getSelectedItem().toString(),"SI","SI","SI");
                    }
                    if(id!=-1){
                        AgregarItemLista((int) id,nnombre,nusuario,ncontrasena,ncodigo,spinnertipo.getSelectedItem().toString());
                    }else{
                        Utils.Mensaje("Ocurrio un error con la base de datos, haga un reset del indicador",R.layout.item_customtoasterror,mainActivity);
                    }


                    nnombre="";
                    ncontrasena="";
                    nusuario="";

                    dialog.cancel();
                }
            }else{
                Utils.Mensaje("El usuario logeado no puede modificar otros usuarios",R.layout.item_customtoasterror,mainActivity);
            }


        });
        Cancelar.setOnClickListener(view -> {
            nnombre="";
            ncontrasena="";
            nusuario="";
            dialog.cancel();
        });

    }
    public void DialogoSeteoVariablesNuevoUsuario(TextView textViewelegido){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialogo_dosopciones, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        final LinearLayout delete_text= mView.findViewById(R.id.lndelete_text);
        delete_text.setOnClickListener(view -> userInput.setText(""));
        TextView textView=mView.findViewById(R.id.textViewt);
        textView.setText("Ingrese");

        if(textViewelegido== tvcodigo|| textViewelegido==tv_ncontrasena){
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        }

        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {
            if(textViewelegido== tv_nnombre){
                nnombre=userInput.getText().toString();
                tv_nnombre.setText(userInput.getText().toString());
            }
            if(textViewelegido== tv_nusuario){
                if(!BuscarUsuario(userInput.getText().toString())){
                    nusuario=userInput.getText().toString();
                    tv_nusuario.setText(userInput.getText().toString());
                }
                else{
                    Utils.Mensaje("Ya existe un usuario igual al ingresado",R.layout.item_customtoasterror,mainActivity);
                }

            }
            if(textViewelegido== tv_ncontrasena){
                ncontrasena=userInput.getText().toString();
                tv_ncontrasena.setText(userInput.getText().toString());
            }

            if(textViewelegido==tvcodigo){
                if(!BuscarCodigo(userInput.getText().toString())){
                    ncodigo=userInput.getText().toString();
                    tvcodigo.setText(userInput.getText().toString());
                }
                else{
                    Utils.Mensaje("Ya existe un codigo igual al ingresado",R.layout.item_customtoasterror,mainActivity);
                }

            }

            dialog.cancel();
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());

    }
    public void AgregarItemLista(int id,String nombre, String usuario, String password,String codigo,String tipo){

        ListElementsArrayList.add(new UserModel(id,nombre,usuario,password,codigo,tipo));
        adapter.notifyItemInserted(ListElementsArrayList.size()-1);
        adapter.notifyDataSetChanged();

    }

    public boolean BuscarNombre(String nombre){
        return false;

    }
    public boolean BuscarUsuario(String user){

        List<UserModel> lista=new ArrayList<>();
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(getContext(), UsersManager.DB_USERS_NAME, null, UsersManager.DB_USERS_VERSION)) {
            lista=dbHelper.obtenerUsuarios();
        }
        for(int i=0;i<lista.size();i++){
            if(lista.get(i).usuario.equals(user)){
                return true;
            }
        }
        return false;
    }
    public boolean BuscarCodigo(String codigo){
        List<UserModel> lista;
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(getContext(), UsersManager.DB_USERS_NAME, null, UsersManager.DB_USERS_VERSION)) {
            lista=dbHelper.obtenerUsuarios();
        }
        for(int i=0;i<lista.size();i++){
            if(lista.get(i).codigo.equals(codigo)){
                return true;
            }
        }
        return false;
    }


    @Override
    public void eliminarUsuario(List<UserModel> mData, int posicion) {
        if(usersManager.getNivelUsuario()>2){
            if (mData.size() > 0) {
                if(!mData.get(posicion).nombre.equals(usersManager.getUsuarioActual())){
                    dialogText(getContext(), "Quiere eliminar el usuario " + mData.get(posicion).nombre + "?", "ELIMINAR", () -> {
                        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(mainActivity, UsersManager.DB_USERS_NAME, null, UsersManager.DB_USERS_VERSION)) {
                            dbHelper.eliminarUsuario(mData.get(posicion).usuario);
                        }
                        mData.remove(posicion);
                        adapter.filterList(mData);
                    });
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
}


