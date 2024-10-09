package com.jws.jwsapi.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class JsonUtils {

    public static String getJsonFromModel(List<?> objectList) {
        if (objectList == null || objectList.isEmpty()) return null;
        JSONArray jsonArray = new JSONArray();
        try {
            for (Object obj : objectList) {
                JSONObject jsonObject = new JSONObject();
                Field[] fields = obj.getClass().getDeclaredFields();

                for (Field field : fields) {
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    Object fieldValue = field.get(obj);

                    jsonObject.put(fieldName, fieldValue != null ? fieldValue.toString() : JSONObject.NULL);
                }

                jsonArray.put(jsonObject);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }

    public static List<String> getFieldFromJson(String campo, String JSON) {
        List<String> resultados = new ArrayList<>();
        JsonElement jsonElement = JsonParser.parseString(JSON);
        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();
            for (JsonElement element : jsonArray) {
                getFieldFromElement(campo, resultados, jsonElement, element);
            }
        } else {
            getFieldFromElement(campo, resultados, jsonElement, jsonElement);
        }
        return resultados;
    }

    public static List<String> getFieldFromJsonMatrix(String campo, String JSON, String Matriz) {
        List<String> resultados = new ArrayList<>();
        JsonElement jsonElement = JsonParser.parseString(JSON);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            if (jsonObject.has(Matriz)) {
                JsonArray jsonArray = jsonObject.getAsJsonArray(Matriz);
                for (JsonElement element : jsonArray) {
                    getFieldFromElement(campo, resultados, jsonElement, element);
                }
            }
        }
        return resultados;
    }

    private static void getFieldFromElement(String campo, List<String> resultados, JsonElement jsonElement, JsonElement element) {
        if (element.isJsonObject()) {
            JsonObject jsonObject = element.getAsJsonObject();
            if (jsonObject.has(campo) && jsonElement != null && !jsonElement.isJsonNull()) {
                if (!jsonObject.get(campo).isJsonNull()) {
                    String field = jsonObject.get(campo).getAsString();
                    resultados.add(field);
                    System.out.println(field);
                } else {
                    resultados.add("");
                }

            }
        }
    }
}
