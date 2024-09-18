package com.jws.jwsapi.core.user;

import static com.jws.jwsapi.core.user.UserConstants.DB_USERS_NAME;
import static com.jws.jwsapi.core.user.UserConstants.DB_USERS_VERSION;
import static com.jws.jwsapi.core.user.UserConstants.ROLE_ADMINISTRATOR;
import static com.jws.jwsapi.core.user.UserConstants.ROLE_OPERATOR;
import static com.jws.jwsapi.core.user.UserConstants.ROLE_PROGRAMMER;
import static com.jws.jwsapi.core.user.UserConstants.ROLE_SUPERVISOR;
import static com.jws.jwsapi.core.user.UserConstants.USERS_LIST;

import android.app.Application;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.core.data.local.PreferencesManager;
import com.jws.jwsapi.utils.ToastHelper;

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
public class UserManager implements UserLoginInterface {

    private final Application application;
    private final PreferencesManager preferencesManagerBase;
    String userName ="";
    int userLevel =0;

    @Inject
    public UserManager(Application application, PreferencesManager preferencesManagerBase){
        this.application = application;
        this.preferencesManagerBase = preferencesManagerBase;
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

    @Override
    public boolean login(String password, String user) {
        boolean logeo=false;
        if(!password.isEmpty() && !user.isEmpty()){
            if((password.equals(preferencesManagerBase.consultaPIN())) && user.equals("ADMINISTRADOR")){
                userLevel = ROLE_ADMINISTRATOR;
                logeo=true;
                userName ="ADMINISTRADOR";
            }
            if((password.equals("3031")) && user.equals("PROGRAMADOR")){
                userLevel = ROLE_PROGRAMMER;
                logeo=true;
                userName ="PROGRAMADOR";
            }
            if(!logeo){
                searchUser(user, password);
            }
        }
        return logeo;
    }

    @Override
    public void logout(){
        userName = "";
        userLevel = 0;
    }

    @Override
    public List<String> getUsersSpinner(){
        List<UserModel> lista;
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(application, DB_USERS_NAME, null, DB_USERS_VERSION)) {
            lista=dbHelper.getAllUsers();
        }
        List<String> listElementsArrayList = new ArrayList<>(Arrays.asList(USERS_LIST));
        for(int i=0;i<lista.size();i++){
            listElementsArrayList.add(lista.get(i).getUser());
        }
        if(listElementsArrayList.size()>0){
            return listElementsArrayList;
        }else{
            return new ArrayList<>();
        }
    }

    public void searchUser(String user, String password){
        List<UserModel> list;
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(application, DB_USERS_NAME, null, DB_USERS_VERSION)) {
            list=dbHelper.searchUsers(user);
        }
        try {
            if(list.size()==0){
                ToastHelper.message(application.getResources().getString(R.string.toast_user_not_exist), R.layout.item_customtoasterror,application);
            }
            else{
                for(int i=0;i<list.size();i++){
                    if(list.get(i).getPassword().equals(password)){
                        userName = list.get(i).getName();
                        if(Objects.equals(list.get(i).getType(), "Supervisor")){
                            userLevel = ROLE_SUPERVISOR;
                        }
                        if(Objects.equals(list.get(i).getType(), "Operador")){
                            userLevel = ROLE_OPERATOR;
                        }
                        ToastHelper.message(application.getResources().getString(R.string.toast_user_login_succesfull),R.layout.item_customtoastok,application);
                    }
                    else
                    {
                        ToastHelper.message(application.getResources().getString(R.string.toast_user_wrong_password),R.layout.item_customtoasterror,application);
                    }
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public String getCurrentUser(){
        return userName;
    }

    public int getLevelUser(){
        return userLevel;
    }

    public Boolean isEnabled(){
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(application,DB_USERS_NAME, null, DB_USERS_VERSION)) {
            int cantidad=dbHelper.getQuantity();
            if(cantidad==0){
                return true;
            }else{
                return getLevelUser() > ROLE_OPERATOR;
            }
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

    public void loginDialog(MainActivity mainActivity) {
        UserLoginDialog userLoginDialog = new UserLoginDialog();
        userLoginDialog.showDialog(mainActivity, this);
    }
}
