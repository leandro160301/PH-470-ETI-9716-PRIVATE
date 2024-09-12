package com.jws.jwsapi.general.user;

import static com.jws.jwsapi.general.dialog.DialogUtil.dialogText;
import static com.jws.jwsapi.general.dialog.DialogUtil.keyboard;
import static com.jws.jwsapi.general.dialog.DialogUtil.keyboardInt;
import static com.jws.jwsapi.general.dialog.DialogUtil.keyboardPassword;
import static com.jws.jwsapi.general.utils.SpinnerHelper.setupSpinner;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.DialogoUsuarioBinding;
import com.jws.jwsapi.general.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UsersFragment extends Fragment implements UserButtonClickListener {

    Button bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6;
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    RecyclerView recycler_usuarios;
    UserAdapter adapter;
    List<UserModel> ListElementsArrayList=new ArrayList<>();
    @Inject
    UserManager userManager;

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
        recycler_usuarios.setAdapter(adapter);


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

            bt_1.setOnClickListener(view -> newUserDialog());
            bt_home.setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());

        }
    }

    public void DevuelveElementos(){
        List<UserModel> lista= userManager.getUsers();
        for(int i=0;i<lista.size();i++){
            ListElementsArrayList.add(new UserModel(lista.get(i).getId(), lista.get(i).getName(), lista.get(i).getUser(), lista.get(i).getPassword(), lista.get(i).getCode(),
                    lista.get(i).getType()));
        }
    }

    public void newUserDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
        DialogoUsuarioBinding binding = DialogoUsuarioBinding.inflate(getLayoutInflater());

        setupSpinner(binding.spinnertipo,requireContext(), Arrays.asList(getResources().getStringArray(R.array.tipoUsuarios)));

        binding.tvnContrasena.setOnClickListener(v -> keyboardPassword(binding.tvnContrasena,"Ingrese la contraseña",getContext(),null,null));
        binding.tvnNombre.setOnClickListener(v -> keyboard(binding.tvnNombre, "Ingrese el nombre", getContext(),null));
        binding.tvnUsuario.setOnClickListener(v -> keyboard(binding.tvnUsuario, "Ingrese el usuario", getContext(), texto -> {
            if(BuscarUsuario(texto)){
                Utils.Mensaje("Ya existe un usuario igual al ingresado",R.layout.item_customtoasterror,mainActivity);
                binding.tvnUsuario.setText("");
            }
        }));

        binding.tvcodigo.setOnClickListener(v -> keyboardInt(binding.tvcodigo, "Ingrese el codigo", getContext(), texto -> {
            if(BuscarCodigo(texto)){
                Utils.Mensaje("Ya existe un codigo igual al ingresado",R.layout.item_customtoasterror,mainActivity);
                binding.tvcodigo.setText("");
            }
        }));

        mBuilder.setView(binding.getRoot());
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        binding.buttons.setOnClickListener(view -> {
            if (userManager.getNivelUsuario() > 2) {
                String nombre=binding.tvnNombre.getText().toString();
                String usuario=binding.tvnUsuario.getText().toString();
                String contrasena=binding.tvnContrasena.getText().toString();
                String codigo=binding.tvcodigo.getText().toString();
                if (!nombre.isEmpty() && !usuario.isEmpty() && !contrasena.isEmpty() && !codigo.isEmpty()) {
                    long id;

                    try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(getContext(), UserManager.DB_USERS_NAME, null, UserManager.DB_USERS_VERSION)) {
                        id = dbHelper.newUser(nombre, usuario, contrasena, codigo, binding.spinnertipo.getSelectedItem().toString(), "SI", "SI", "SI");
                    }
                    if (id != -1) {
                        AgregarItemLista((int) id, nombre, usuario, contrasena, codigo, binding.spinnertipo.getSelectedItem().toString());
                    } else {
                        Utils.Mensaje("Ocurrio un error con la base de datos, haga un reset del indicador", R.layout.item_customtoasterror, mainActivity);
                    }
                    resetDialogTexts(binding);
                    dialog.cancel();
                }
            } else {
                Utils.Mensaje("El user logeado no puede modificar otros usuarios", R.layout.item_customtoasterror, mainActivity);
            }
        });

        binding.buttonc.setOnClickListener(view -> {
            resetDialogTexts(binding);
            dialog.cancel();
        });
    }

    private void resetDialogTexts(DialogoUsuarioBinding binding) {
        binding.tvcodigo.setText("");
        binding.tvnUsuario.setText("");
        binding.tvnNombre.setText("");
    }


    public void AgregarItemLista(int id,String nombre, String usuario, String password,String codigo,String tipo){

        ListElementsArrayList.add(new UserModel(id,nombre,usuario,password,codigo,tipo));
        adapter.notifyItemInserted(ListElementsArrayList.size()-1);
        adapter.notifyDataSetChanged();

    }

    public boolean BuscarUsuario(String user){

        List<UserModel> lista;
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(getContext(), UserManager.DB_USERS_NAME, null, UserManager.DB_USERS_VERSION)) {
            lista=dbHelper.getAllUsers();
        }
        for(int i=0;i<lista.size();i++){
            if(lista.get(i).getUser().equals(user)){
                return true;
            }
        }
        return false;
    }
    public boolean BuscarCodigo(String codigo){
        List<UserModel> lista;
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(getContext(), UserManager.DB_USERS_NAME, null, UserManager.DB_USERS_VERSION)) {
            lista=dbHelper.getAllUsers();
        }
        for(int i=0;i<lista.size();i++){
            if(lista.get(i).getCode().equals(codigo)){
                return true;
            }
        }
        return false;
    }


    @Override
    public void deleteUser(List<UserModel> mData, int posicion) {
        if(userManager.getNivelUsuario()>2){
            if (mData.size() > 0) {
                if(!mData.get(posicion).getName().equals(userManager.getUsuarioActual())){
                    dialogText(getContext(), "Quiere eliminar el usuario " + mData.get(posicion).getName() + "?", "ELIMINAR", () -> {
                        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(mainActivity, UserManager.DB_USERS_NAME, null, UserManager.DB_USERS_VERSION)) {
                            dbHelper.deleteUser(mData.get(posicion).getUser());
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
            Utils.Mensaje("El user logeado no puede modificar otros usuarios",R.layout.item_customtoasterror,mainActivity);
        }

    }
}


