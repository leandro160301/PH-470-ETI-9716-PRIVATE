package com.jws.jwsapi.core.services.httpserver;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import com.jws.jwsapi.weighing.Weighing;
import com.jws.jwsapi.weighing.WeighingService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class ServerUtil {

    public static String getJsonApi() {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject jsonWeighings = new JSONObject();
            jsonWeighings.put("GET", "GetPesadas");
            jsonWeighings.put("Nombre", "PESADAS");

            JSONObject jsonPalletOpen = new JSONObject();
            jsonPalletOpen.put("GET", "GetPalletOpen");
            jsonPalletOpen.put("Nombre", "PALLET ACTIVOS");

            JSONObject jsonPalletClosed = new JSONObject();
            jsonPalletClosed.put("GET", "GetPalletClosed");
            jsonPalletClosed.put("Nombre", "PALLET CERRADOS");

            jsonArray.put(jsonWeighings);
            jsonArray.put(jsonPalletOpen);
            jsonArray.put(jsonPalletClosed);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray.toString();
    }

    public static String getJsonWeighing(WeighingService weighingService) {
        List<Weighing> weighingList = weighingService.getAllWeighingsStatic();
        if (weighingList == null) return null;
        JSONArray jsonArray = new JSONArray();
        try {
            for (Weighing weighing : weighingList) {
                JSONObject weighingField = new JSONObject();
                weighingField.put("Id", String.valueOf(weighing.getId()));
                weighingField.put("Codigo", weighing.getCode());
                weighingField.put("Producto", weighing.getName());
                weighingField.put("Bruto", weighing.getGross());
                weighingField.put("Neto", weighing.getNet());
                weighingField.put("Tara", weighing.getTare());
                weighingField.put("Operador", weighing.getOperator());
                weighingField.put("Balanza", String.valueOf(weighing.getScaleNumber()));
                weighingField.put("Numero de serie", weighing.getSerialNumber());
                weighingField.put("Unidad", weighing.getUnit());
                weighingField.put("Cantidad", String.valueOf(weighing.getQuantity()));

                jsonArray.put(weighingField);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }




}
