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
        if(!password.isEmpty() && !user.isEmpty())return false;

        if(password.equals(preferencesManagerBase.getPin()) && user.equals("ADMINISTRADOR")){
            repository.setUserLevel(ROLE_ADMINISTRATOR);
            repository.setUserName("ADMINISTRADOR");
            return true;
        } else if ((password.equals("3031")) && user.equals("PROGRAMADOR")){
            repository.setUserLevel(ROLE_PROGRAMMER);
            repository.setUserName("PROGRAMADOR");
            return true;
        } else {
            repository.searchUser(user, password);
            return false;
        }

    }

    @Override
    public void logout(){
        repository.setUserLevel(ROLE_NOT_LOGGED);
        repository.setUserName("");
    }

    @Override
    public List<String> getUsersSpinner(){
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(application, DB_USERS_NAME, null)) {
            List<UserModel> userElements=dbHelper.getAllUsers();
            List<String> userNames = new ArrayList<>(Arrays.asList(USERS_LIST));
            userElements.forEach(userModel -> userNames.add(userModel.getUser()));
            return userNames;
        }
    }

    public String JsonUsers() throws JSONException {
        List<UserModel> userElements;
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(application, DB_USERS_NAME, null)) {
            userElements=dbHelper.getAllUsers();
        }
        JSONArray jsonArray = new JSONArray();
        putUserToJson("ADMINISTRADOR", jsonArray);
        putUserToJson("PROGRAMADOR", jsonArray);

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

    private static void putUserToJson(String user, JSONArray jsonArray) throws JSONException {
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
