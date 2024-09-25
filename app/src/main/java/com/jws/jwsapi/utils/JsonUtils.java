package com.jws.jwsapi.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JsonUtils {
    public static String jsonFiles() throws JSONException {
        List<String> guardado = FileUtils.getAllFiles();
        JSONArray jsonArray = new JSONArray();

        Map<String, String> tipoPorExtension = new HashMap<>();
        tipoPorExtension.put(".pdf", "pdf");
        tipoPorExtension.put(".png", "png");
        tipoPorExtension.put(".xls", "xls");
        tipoPorExtension.put(".csv", "csv");
        tipoPorExtension.put(".jpg", "jpg");
        tipoPorExtension.put(".prn", "prn");
        tipoPorExtension.put(".lbl", "lbl");
        tipoPorExtension.put(".nlbl", "nlbl");

        try {
            for (String archivo : guardado) {
                JSONObject usuario = new JSONObject();
                String nombre = archivo;
                String tipo = "";

                for (Map.Entry<String, String> entry : tipoPorExtension.entrySet()) {
                    if (archivo.endsWith(entry.getKey())) {
                        nombre = archivo.replace(entry.getKey(), "");
                        tipo = entry.getValue();
                        break;
                    }
                }
                usuario.put("Nombre", nombre);
                usuario.put("Tipo", tipo);
                usuario.put("Fecha", "");
                jsonArray.put(usuario);
            }
        } catch (JSONException e) {
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
}
