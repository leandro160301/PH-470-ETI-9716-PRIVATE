package com.jws.jwsapi.core.user;

import static com.jws.jwsapi.core.user.UserConstants.DB_USERS_NAME;
import static com.jws.jwsapi.core.user.UserConstants.ROLE_ADMINISTRATOR;
import static com.jws.jwsapi.core.user.UserConstants.ROLE_NOT_LOGGED;
import static com.jws.jwsapi.core.user.UserConstants.ROLE_PROGRAMMER;
import static com.jws.jwsapi.core.user.UserConstants.USERS_LIST;

import android.app.Application;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.core.data.local.PreferencesManager;
import com.jws.jwsapi.shared.UserRepository;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UserManager implements UserLoginInterface {

    private final Application application;
    private final PreferencesManager preferencesManagerBase;
    private final UserRepository repository;

    @Inject
    public UserManager(Application application, PreferencesManager preferencesManagerBase, UserRepository repository){
        this.application = application;
        this.preferencesManagerBase = preferencesManagerBase;
        this.repository = repository;
    }

    @Override
    public boolean login(String password, String user) {
        boolean login=false;
        if(!password.isEmpty() && !user.isEmpty()){
            if((password.equals(preferencesManagerBase.getPin())) && user.equals("ADMINISTRADOR")){
                repository.setUserLevel(ROLE_ADMINISTRATOR);
                login=true;
                repository.setUserName("ADMINISTRADOR");
            }
            if((password.equals("3031")) && user.equals("PROGRAMADOR")){
                repository.setUserLevel(ROLE_PROGRAMMER);
                login=true;
                repository.setUserName("PROGRAMADOR");
            }
            if(!login){
                repository.searchUser(user, password);
            }
        }
        return login;
    }

    @Override
    public void logout(){
        repository.setUserLevel(ROLE_NOT_LOGGED);
        repository.setUserName("");
    }

    @Override
    public List<String> getUsersSpinner(){
        List<UserModel> lista;
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(application, DB_USERS_NAME, null)) {
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

    public String JSONusuarios() throws JSONException {
        List<UserModel> userElements;
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(application, DB_USERS_NAME, null)) {
            userElements=dbHelper.getAllUsers();
        }
        JSONArray jsonArray = new JSONArray();
        putUser("ADMINISTRADOR", jsonArray);
        putUser("PROGRAMADOR", jsonArray);

        userElements.forEach(userModel -> {
            JSONObject userJson = new JSONObject();
            try {
                userJson.put("Id", userModel.getId());
                userJson.put("Nombre", userModel.getName());
                userJson.put("Usuario", userModel.getUser());
                jsonArray.put(userJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });

        return jsonArray.toString();
    }

    private static void putUser(String user, JSONArray jsonArray) throws JSONException {
        JSONObject userJson = new JSONObject();
        userJson.put("Id", "-");
        userJson.put("Nombre", user);
        userJson.put("Usuario", user);
        jsonArray.put(userJson);
    }

    public void loginDialog(MainActivity mainActivity) {
        UserLoginDialog userLoginDialog = new UserLoginDialog();
        userLoginDialog.showDialog(mainActivity, this);
    }
}
