package com.jws.jwsapi.core.user;

import static com.jws.jwsapi.core.user.UserConstants.DB_USERS_NAME;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class UserJsonUtils {

    private static void putUserToJson(String user, JSONArray jsonArray) throws JSONException {
        JSONObject userJson = new JSONObject();
        userJson.put("Id", "-");
        userJson.put("Nombre", user);
        userJson.put("Usuario", user);
        jsonArray.put(userJson);
    }

    public String jsonUsers(Context context) throws JSONException {
        List<UserModel> userElements;
        try (UserDatabaseHelper dbHelper = new UserDatabaseHelper(context, DB_USERS_NAME, null)) {
            userElements = dbHelper.getAllUsers();
        }
        JSONArray jsonArray = new JSONArray();
        putUserToJson("ADMINISTRADOR", jsonArray);
        putUserToJson("PROGRAMADOR", jsonArray);

        for (UserModel userModel : userElements) {
            JSONObject userJson = new JSONObject();
            try {
                userJson.put("Id", userModel.getId());
                userJson.put("Nombre", userModel.getName());
                userJson.put("Usuario", userModel.getUser());
                jsonArray.put(userJson);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return jsonArray.toString();
    }
}
