package com.jws.jwsapi.core.user;

import static com.jws.jwsapi.core.user.UserConstants.DB_USERS_NAME;
import static com.jws.jwsapi.core.user.UserConstants.DB_USERS_VERSION;
import static com.jws.jwsapi.core.user.UserConstants.ROLE_SUPERVISOR;
import static com.jws.jwsapi.dialog.DialogUtil.dialogText;

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

import java.util.List;
import java.util.function.Predicate;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class UserFragment extends Fragment implements UserButtonClickListener, UserCreateInterface {

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
        setupButtons();
        setupRecycler(view);
    }

    private void setupRecycler(@NonNull View view) {
        recycler = view.findViewById(R.id.listausuarios);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UserAdapter(userManager.getUsers(),this);
        recycler.setAdapter(adapter);
    }

    private void setupButtons() {
        if (buttonProvider != null) {
            buttonProvider.getTitle().setText(getString(R.string.users_title));
            buttonProvider.getButton1().setBackgroundResource(R.drawable.boton_add_i);
            buttonProvider.getButton2().setVisibility(View.INVISIBLE);
            buttonProvider.getButton3().setVisibility(View.INVISIBLE);
            buttonProvider.getButton4().setVisibility(View.INVISIBLE);
            buttonProvider.getButton5().setVisibility(View.INVISIBLE);
            buttonProvider.getButton6().setVisibility(View.INVISIBLE);
            buttonProvider.getButton1().setOnClickListener(view -> new UserCreateDialog(requireContext(),this).showDialog());
            buttonProvider.getButtonHome().setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());
        }
    }

    @Override
    public void handleCreateUserButton(DialogoUsuarioBinding binding, AlertDialog dialog) {
        if (userManager.getLevelUser() <= ROLE_SUPERVISOR){
            showMessage(R.string.user_error_create_login);
            return;
        }
        String name= binding.tvnNombre.getText().toString();
        String user= binding.tvnUsuario.getText().toString();
        String password= binding.tvnContrasena.getText().toString();
        String code= binding.tvcodigo.getText().toString();
        if (areFieldsValid(name, user, password, code)) {
            long id;
            id = insertUserFromDatabase(binding, name, user, password, code);
            if (id != -1) {
                addElementToList(userManager.getUsers());
            } else {
                showMessage(R.string.error_reset);
            }
            dialog.cancel();
        }

    }

    private long insertUserFromDatabase(DialogoUsuarioBinding binding, String name, String user, String password, String code) {
        long id;
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(getContext(), DB_USERS_NAME, null, DB_USERS_VERSION)) {
            id = dbHelper.createUser(name, user, password, code, binding.spinnertipo.getSelectedItem().toString(), "SI", "SI", "SI");
        }
        return id;
    }

    private static boolean areFieldsValid(String name, String user, String password, String code) {
        return !name.isEmpty() && !user.isEmpty() && !password.isEmpty() && !code.isEmpty();
    }

    public void addElementToList(List<UserModel> userList){
        adapter.filterList(userList);
    }

    @Override
    public boolean searchUserOrCode(Predicate<UserModel> predicate) {
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(getContext(), DB_USERS_NAME, null, DB_USERS_VERSION)) {
            return dbHelper.getAllUsers().stream().anyMatch(predicate);
        }
    }

    @Override
    public void deleteUser(List<UserModel> userList, int position) {
        if(userManager.getLevelUser()<=ROLE_SUPERVISOR) {
            showMessage(R.string.user_error_create_login);
            return;
        }
        if (userList.size() == 0) {
            showMessage(R.string.user_error_create_2);
            return;
        }
        if(!userList.get(position).getName().equals(userManager.getCurrentUser())){
            dialogText(getContext(), "Quiere eliminar el usuario " + userList.get(position).getName() + "?", "ELIMINAR", () -> {
                deleteUserFromDatabase(userList, position);
                userList.remove(position);
                adapter.filterList(userList);
            });
        } else {
            showMessage(R.string.user_error_create);
        }
    }

    private void deleteUserFromDatabase(List<UserModel> mData, int position) {
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(mainActivity, DB_USERS_NAME, null, DB_USERS_VERSION)) {
            dbHelper.deleteUser(mData.get(position).getUser());
        }
    }

    private void showMessage(int user_error_create) {
        ToastHelper.message(getString(user_error_create),R.layout.item_customtoasterror,requireContext());
    }
}


