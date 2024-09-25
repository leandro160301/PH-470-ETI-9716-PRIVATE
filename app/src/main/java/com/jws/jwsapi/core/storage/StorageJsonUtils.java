package com.jws.jwsapi.core.storage;

import com.jws.jwsapi.utils.file.FileUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StorageJsonUtils {
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
}
