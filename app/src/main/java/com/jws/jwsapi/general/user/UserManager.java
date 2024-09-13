package com.jws.jwsapi.general.user;

import static com.jws.jwsapi.general.dialog.DialogUtil.keyboardPassword;
import static com.jws.jwsapi.general.utils.SpinnerHelper.setupSpinner;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.jws.jwsapi.R;
import com.jws.jwsapi.general.data.local.PreferencesManagerBase;
import com.jws.jwsapi.general.utils.ToastHelper;
import com.jws.jwsapi.general.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserManager {

    private final Application application;
    public static String DB_USERS_NAME ="Usuarios_DB";
    public static int DB_USERS_VERSION =1;
    String usuario ="";
    int userLevel =0;
    public static final int ROLE_NOT_LOGGED = 0;
    public static final int ROLE_OPERATOR = 1;
    public static final int ROLE_SUPERVISOR = 2;
    public static final int ROLE_ADMINISTRATOR = 3;
    public static final int ROLE_PROGRAMMER = 4;
    public static final String[] USUARIOS = {
            "ADMINISTRADOR", "PROGRAMADOR",
    };
    String password ="";

    @Inject
    public UserManager(Application application){
        this.application =application;
    }


    public int usersQuantity(){
        List<UserModel> lista= getUsers();
        if(lista!=null){
            return lista.size();
        }else {
            return 0;
        }
    }

    public List<UserModel> getUsers(){
        List<UserModel> lista;
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(application, DB_USERS_NAME, null, DB_USERS_VERSION)) {
            lista=dbHelper.getAllUsers();
        }
        return lista;
    }

    public void BotonLogeo (Context context,AppCompatActivity activity){
        PreferencesManagerBase preferencesManagerBase=new PreferencesManagerBase(application);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        View mView = LayoutInflater.from(context).inflate(R.layout.dialogo_logeo, null);
        TextView tvnContrasena=  mView.findViewById(R.id.tvnContrasena);
        Spinner spinner=mView.findViewById(R.id.spinner);
        setupSpinner(spinner,application,DevuelveListaUsuarios());
        spinner.setSelection(0);
        tvnContrasena.setOnClickListener(view -> Logeo(tvnContrasena,context));

        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);
        Button Deslogear = mView.findViewById(R.id.buttond);
        Guardar.setText("LOGEAR");

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Deslogear.setOnClickListener(view -> {
            logout();
            dialog.cancel();

        });
        Guardar.setOnClickListener(view -> {
            if(!password.isEmpty() && !spinner.getSelectedItem().toString().isEmpty()){
                boolean logeo=false;
                if((password.equals(preferencesManagerBase.consultaPIN())) && spinner.getSelectedItemPosition()==0){
                    userLevel = ROLE_ADMINISTRATOR;
                    logeo=true;
                    usuario ="ADMINISTRADOR";
                }
                if((password.equals("3031")) && spinner.getSelectedItemPosition()==1){
                    userLevel = ROLE_PROGRAMMER;
                    logeo=true;
                    usuario ="PROGRAMADOR";
                }
                if(!logeo){
                    BuscarUsuario(spinner.getSelectedItem().toString(), password,activity);
                }
                password ="";
                dialog.cancel();

            }

        });
        Cancelar.setOnClickListener(view -> dialog.cancel());
    }


    public void logout(){
        usuario ="";
        userLevel =0;
    }

    public List<String> DevuelveListaUsuarios(){
        List<UserModel> lista;
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(application, DB_USERS_NAME, null, DB_USERS_VERSION)) {
            lista=dbHelper.getAllUsers();
        }
        List<String> listElementsArrayList = new ArrayList<>(Arrays.asList(USUARIOS));
        for(int i=0;i<lista.size();i++){
            listElementsArrayList.add(lista.get(i).getUser());
        }
        if(listElementsArrayList.size()>0){
            return listElementsArrayList;
        }else{
            return new ArrayList<>();
        }
    }


    public void Logeo(TextView textView,Context context){
        keyboardPassword(null, "", context, texto -> {
            password =texto;
            String copia = password;
            copia=copia.replaceAll("(?s).", "*");
            textView.setText(copia);
        }, PasswordTransformationMethod.getInstance());
    }

    public void BuscarUsuario(String user, String contrasenia, AppCompatActivity activity){
        List<UserModel> lista;
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(application, DB_USERS_NAME, null, DB_USERS_VERSION)) {
            lista=dbHelper.searchUsers(user);
        }
        try {
            if(lista.size()==0){
                ToastHelper.message("No existe el user ingresado", R.layout.item_customtoasterror,activity);
            }
            else{
                for(int i=0;i<lista.size();i++){
                    if(lista.get(i).getPassword().equals(contrasenia)){
                        usuario = lista.get(i).getName();
                        if(Objects.equals(lista.get(i).getType(), "Supervisor")){
                            userLevel = ROLE_SUPERVISOR;
                        }
                        if(Objects.equals(lista.get(i).getType(), "Operador")){
                            userLevel = ROLE_OPERATOR;
                        }
                        ToastHelper.message("LOGEO CORRECTO",R.layout.item_customtoastok,activity);
                    }
                    else
                    {
                        ToastHelper.message("Contraseña incorrecta",R.layout.item_customtoasterror,activity);
                    }
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public String JSONusuarios() throws JSONException {
        List<UserModel> lista;
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(application, DB_USERS_NAME, null, DB_USERS_VERSION)) {
            lista=dbHelper.getAllUsers();
        }

        JSONArray jsonArray = new JSONArray();
        JSONObject Usuario1 = new JSONObject();
        Usuario1.put("Id", "-");
        Usuario1.put("Nombre", "ADMINISTRADOR");
        Usuario1.put("Usuario","ADMINISTRADOR");
        jsonArray.put(Usuario1);

        JSONObject Usuario2 = new JSONObject();
        Usuario2.put("Id", "-");
        Usuario2.put("Nombre", "PROGRAMADOR");
        Usuario2.put("Usuario","PROGRAMADOR");
        jsonArray.put(Usuario2);

        for(int i=0; i<lista.size();i++){
            JSONObject Usuario = new JSONObject();
            try {
                Usuario.put("Id", lista.get(i).getId());
                Usuario.put("Nombre", lista.get(i).getName());
                Usuario.put("Usuario", lista.get(i).getUser());
                jsonArray.put(Usuario);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return jsonArray.toString();
    }
    public String getUsuarioActual(){
        return usuario;
    }
    public int getUserLevel(){
        return userLevel;
    }
    public Boolean modificarDatos(){
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(application,DB_USERS_NAME, null, DB_USERS_VERSION)) {
            int cantidad=dbHelper.getQuantity();
            if(cantidad==0){
                return true;
            }else{
                return getUserLevel() > ROLE_OPERATOR;
            }
        }
    }

}
