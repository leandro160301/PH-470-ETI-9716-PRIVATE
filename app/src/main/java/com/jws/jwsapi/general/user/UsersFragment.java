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

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UsersFragment extends Fragment implements UserButtonClickListener {

    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    RecyclerView recycler_usuarios;
    UserAdapter adapter;
    @Inject
    UserManager userManager;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_users,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        configuracionBotones();
        recycler_usuarios =view.findViewById(R.id.listausuarios);

        recycler_usuarios.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserAdapter(getContext(), userManager.getUsers(),this);
        recycler_usuarios.setAdapter(adapter);

    }

    private void configuracionBotones() {
        if (buttonProvider != null) {
            buttonProvider.getTitulo().setText("USUARIOS");
            buttonProvider.getButton1().setBackgroundResource(R.drawable.boton_add_i);
            buttonProvider.getButton2().setVisibility(View.INVISIBLE);
            buttonProvider.getButton3().setVisibility(View.INVISIBLE);
            buttonProvider.getButton4().setVisibility(View.INVISIBLE);
            buttonProvider.getButton5().setVisibility(View.INVISIBLE);
            buttonProvider.getButton6().setVisibility(View.INVISIBLE);
            buttonProvider.getButton1().setOnClickListener(view -> createUserDialog());
            buttonProvider.getButtonHome().setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());
        }
    }


    public void createUserDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(), R.style.AlertDialogCustom);
        DialogoUsuarioBinding binding = DialogoUsuarioBinding.inflate(getLayoutInflater());

        setupSpinner(binding.spinnertipo,requireContext(), Arrays.asList(getResources().getStringArray(R.array.tipoUsuarios)));
        handleCreateDialogListeners(binding);
        mBuilder.setView(binding.getRoot());
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        binding.buttons.setOnClickListener(view -> handleCreateUserButton(binding, dialog));
        binding.buttonc.setOnClickListener(view -> {
            resetDialogTexts(binding);
            dialog.cancel();
        });
    }

    private void handleCreateUserButton(DialogoUsuarioBinding binding, AlertDialog dialog) {
        if (userManager.getNivelUsuario() > 2) {
            String nombre= binding.tvnNombre.getText().toString();
            String usuario= binding.tvnUsuario.getText().toString();
            String contrasena= binding.tvnContrasena.getText().toString();
            String codigo= binding.tvcodigo.getText().toString();
            if (areFieldsValid(nombre, usuario, contrasena, codigo)) {
                long id;
                try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(getContext(), UserManager.DB_USERS_NAME, null, UserManager.DB_USERS_VERSION)) {
                    id = dbHelper.newUser(nombre, usuario, contrasena, codigo, binding.spinnertipo.getSelectedItem().toString(), "SI", "SI", "SI");
                }
                if (id != -1) {
                    AgregarItemLista((int) id, nombre, usuario, contrasena, codigo, binding.spinnertipo.getSelectedItem().toString(),userManager.getUsers());
                } else {
                    Utils.Mensaje("Ocurrio un error con la base de datos, haga un reset del indicador", R.layout.item_customtoasterror, mainActivity);
                }
                resetDialogTexts(binding);
                dialog.cancel();
            }
        } else {
            Utils.Mensaje("El user logeado no puede modificar otros usuarios", R.layout.item_customtoasterror, mainActivity);
        }
    }

    private static boolean areFieldsValid(String nombre, String usuario, String contrasena, String codigo) {
        return !nombre.isEmpty() && !usuario.isEmpty() && !contrasena.isEmpty() && !codigo.isEmpty();
    }

    private void handleCreateDialogListeners(DialogoUsuarioBinding binding) {
        binding.tvnContrasena.setOnClickListener(v -> keyboardPassword(binding.tvnContrasena,"Ingrese la contraseña",getContext(),null,null));
        binding.tvnNombre.setOnClickListener(v -> keyboard(binding.tvnNombre, "Ingrese el nombre", getContext(),null));
        binding.tvnUsuario.setOnClickListener(v -> keyboard(binding.tvnUsuario, "Ingrese el usuario", getContext(), texto -> {
            if(searchUserOrCode(usuario -> usuario.getUser().equals(texto))){
                Utils.Mensaje("Ya existe un usuario igual al ingresado",R.layout.item_customtoasterror,mainActivity);
                binding.tvnUsuario.setText("");
            }
        }));
        binding.tvcodigo.setOnClickListener(v -> keyboardInt(binding.tvcodigo, "Ingrese el codigo", getContext(), texto -> {
            if(searchUserOrCode(usuario -> usuario.getCode().equals(texto))){
                Utils.Mensaje("Ya existe un codigo igual al ingresado",R.layout.item_customtoasterror,mainActivity);
                binding.tvcodigo.setText("");
            }
        }));
    }

    private void resetDialogTexts(DialogoUsuarioBinding binding) {
        binding.tvcodigo.setText("");
        binding.tvnUsuario.setText("");
        binding.tvnNombre.setText("");
    }


    public void AgregarItemLista(int id,String nombre, String usuario, String password,String codigo,String tipo, List<UserModel> ListElementsArrayList){
        ListElementsArrayList.add(new UserModel(id,nombre,usuario,password,codigo,tipo));
        adapter.filterList(ListElementsArrayList);
    }

    public boolean searchUserOrCode(Predicate<UserModel> predicate) {
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(getContext(), UserManager.DB_USERS_NAME, null, UserManager.DB_USERS_VERSION)) {
            return dbHelper.getAllUsers().stream().anyMatch(predicate);
        }
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


