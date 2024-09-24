package com.jws.jwsapi.core.user;

import static com.jws.jwsapi.core.user.UserConstants.DB_USERS_NAME;
import static com.jws.jwsapi.core.user.UserConstants.DB_USERS_VERSION;
import static com.jws.jwsapi.core.user.UserConstants.ROLE_SUPERVISOR;
import static com.jws.jwsapi.dialog.DialogUtil.dialogText;
import static com.jws.jwsapi.dialog.DialogUtil.keyboard;
import static com.jws.jwsapi.dialog.DialogUtil.keyboardInt;
import static com.jws.jwsapi.dialog.DialogUtil.keyboardPassword;
import static com.jws.jwsapi.utils.SpinnerHelper.setupSpinner;

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
import com.jws.jwsapi.utils.ToastHelper;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UserFragment extends Fragment implements UserButtonClickListener {

    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    RecyclerView recycler;
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
        setupRecycler(view);

    }

    private void setupRecycler(@NonNull View view) {
        recycler = view.findViewById(R.id.listausuarios);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserAdapter(userManager.getUsers(),this);
        recycler.setAdapter(adapter);
    }

    private void configuracionBotones() {
        if (buttonProvider != null) {
            buttonProvider.getTitle().setText(getString(R.string.users_title));
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
        if (userManager.getLevelUser() > ROLE_SUPERVISOR) {
            String nombre= binding.tvnNombre.getText().toString();
            String usuario= binding.tvnUsuario.getText().toString();
            String contrasena= binding.tvnContrasena.getText().toString();
            String codigo= binding.tvcodigo.getText().toString();
            if (areFieldsValid(nombre, usuario, contrasena, codigo)) {
                long id;
                try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(getContext(), DB_USERS_NAME, null, DB_USERS_VERSION)) {
                    id = dbHelper.createUser(nombre, usuario, contrasena, codigo, binding.spinnertipo.getSelectedItem().toString(), "SI", "SI", "SI");
                }
                if (id != -1) {
                    AgregarItemLista((int) id, nombre, usuario, contrasena, codigo, binding.spinnertipo.getSelectedItem().toString(),userManager.getUsers());
                } else {
                    ToastHelper.message(getString(R.string.error_reset), R.layout.item_customtoasterror, mainActivity);
                }
                resetDialogTexts(binding);
                dialog.cancel();
            }
        } else {
            ToastHelper.message(getString(R.string.user_error_create_login), R.layout.item_customtoasterror, mainActivity);
        }
    }

    private static boolean areFieldsValid(String nombre, String usuario, String contrasena, String codigo) {
        return !nombre.isEmpty() && !usuario.isEmpty() && !contrasena.isEmpty() && !codigo.isEmpty();
    }

    private void handleCreateDialogListeners(DialogoUsuarioBinding binding) {
        binding.tvnContrasena.setOnClickListener(v -> keyboardPassword(binding.tvnContrasena,getString(R.string.input_password),getContext(), true,null,null));
        binding.tvnNombre.setOnClickListener(v -> keyboard(binding.tvnNombre, getString(R.string.input_name), getContext(),null));
        binding.tvnUsuario.setOnClickListener(v -> keyboard(binding.tvnUsuario, getString(R.string.input_user), getContext(), texto -> {
            if(searchUserOrCode(usuario -> usuario.getUser().equals(texto))){
                ToastHelper.message(getString(R.string.user_error_user_exist),R.layout.item_customtoasterror,mainActivity);
                binding.tvnUsuario.setText("");
            }
        }));
        binding.tvcodigo.setOnClickListener(v -> keyboardInt(binding.tvcodigo, getString(R.string.input_code), getContext(), texto -> {
            if(searchUserOrCode(usuario -> usuario.getCode().equals(texto))){
                ToastHelper.message(getString(R.string.user_error_code_exist),R.layout.item_customtoasterror,mainActivity);
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
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(getContext(), DB_USERS_NAME, null, DB_USERS_VERSION)) {
            return dbHelper.getAllUsers().stream().anyMatch(predicate);
        }
    }

    @Override
    public void deleteUser(List<UserModel> mData, int posicion) {
        if(userManager.getLevelUser()>ROLE_SUPERVISOR){
            if (mData.size() > 0) {
                if(!mData.get(posicion).getName().equals(userManager.getCurrentUser())){
                    dialogText(getContext(), "Quiere eliminar el userName " + mData.get(posicion).getName() + "?", "ELIMINAR", () -> {
                        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(mainActivity, DB_USERS_NAME, null, DB_USERS_VERSION)) {
                            dbHelper.deleteUser(mData.get(posicion).getUser());
                        }
                        mData.remove(posicion);
                        adapter.filterList(mData);
                    });
                }
                else {
                    ToastHelper.message(getString(R.string.user_error_create),R.layout.item_customtoasterror,mainActivity);
                }
            }
            else {
                ToastHelper.message(getString(R.string.user_error_create_2),R.layout.item_customtoasterror,mainActivity);
            }
        }else{
            ToastHelper.message(getString(R.string.user_error_create_login),R.layout.item_customtoasterror,mainActivity);
        }

    }
}


