package com.jws.jwsapi.shared;

import static com.jws.jwsapi.core.user.UserConstants.DB_USERS_NAME;
import static com.jws.jwsapi.core.user.UserConstants.DB_USERS_VERSION;
import static com.jws.jwsapi.core.user.UserConstants.ROLE_OPERATOR;
import static com.jws.jwsapi.core.user.UserConstants.ROLE_SUPERVISOR;

import android.app.Application;

import com.jws.jwsapi.R;
import com.jws.jwsapi.core.user.UserDatabaseHelper;
import com.jws.jwsapi.core.user.UserModel;
import com.jws.jwsapi.utils.ToastHelper;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;

public class UserRepository {
    private final Application application;
    private int userLevel = 0;
    private String userName = "";

    @Inject
    public UserRepository(Application application) {
        this.application = application;
    }

    public List<UserModel> getUsers(){
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(application, DB_USERS_NAME, null, DB_USERS_VERSION)) {
            return dbHelper.getAllUsers();
        }
    }

    public int usersQuantity() {
        return Optional.ofNullable(getUsers())
                .map(List::size)
                .orElse(0);
    }

    public void searchUser(String user, String password){
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(application, DB_USERS_NAME, null, DB_USERS_VERSION)) {
            List<UserModel> userElements=dbHelper.searchUsers(user);
            if(userElements.isEmpty()){
                showMessage(R.string.toast_user_not_exist, R.layout.item_customtoasterror);
                return;
            }
            userElements.forEach(userModel -> {
                if(userModel.getPassword().equals(password)){
                    setUserName(userModel.getName());
                    setupUserLevel(userModel, "Supervisor", ROLE_SUPERVISOR);
                    setupUserLevel(userModel, "Operador", ROLE_OPERATOR);
                    showMessage(R.string.toast_user_login_succesfull, R.layout.item_customtoastok);
                } else {
                    showMessage(R.string.toast_user_wrong_password, R.layout.item_customtoasterror);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setupUserLevel(UserModel userModel, String Supervisor, int roleSupervisor) {
        if(Objects.equals(userModel.getType(), Supervisor)){
            setUserLevel(roleSupervisor);
        }
    }

    private void showMessage(int text, int layout) {
        ToastHelper.message(application.getResources().getString(text), layout,application);
    }

    public boolean isEnabled() {
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(application, DB_USERS_NAME, null, DB_USERS_VERSION)) {
            int quantity = dbHelper.getQuantity();
            return quantity == 0 || getLevelUser() > ROLE_OPERATOR;
        }
    }

    public String getCurrentUser(){
        return userName;
    }

    public int getLevelUser(){
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
