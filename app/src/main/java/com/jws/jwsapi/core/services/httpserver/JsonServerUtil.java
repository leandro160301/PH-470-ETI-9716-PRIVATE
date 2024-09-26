package com.jws.jwsapi.core.services.httpserver;

import android.annotation.SuppressLint;

import androidx.annotation.Nullable;

import com.jws.jwsapi.pallet.Pallet;
import com.jws.jwsapi.pallet.PalletService;
import com.jws.jwsapi.weighing.Weighing;
import com.jws.jwsapi.weighing.WeighingService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class JsonServerUtil {

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

    @SuppressLint("DefaultLocale")
    public static String getJsonPalletOpen(PalletService palletService) {
        return getJsonPallet(false, palletService);
    }

    @SuppressLint("DefaultLocale")
    public static String getJsonPalletClose(PalletService palletService) {
        return getJsonPallet(true, palletService);
    }

    @SuppressLint("DefaultLocale")
    @Nullable
    private static String getJsonPallet(boolean closed, PalletService palletService) {
        List<Pallet> palletList = palletService.getAllPalletsStatic(closed);
        if (palletList == null) return null;
        JSONArray jsonArray = new JSONArray();
        try {
            for (Pallet pallet : palletList) {
                JSONObject palletField = new JSONObject();
                palletField.put("Id", String.valueOf(pallet.getId()));
                palletField.put("Codigo", pallet.getCode());
                palletField.put("Producto", pallet.getName());
                palletField.put("Origen", pallet.getOriginPallet());
                palletField.put("Destino", pallet.getDestinationPallet());
                palletField.put("Cantidad", String.format("%d/%d", pallet.getDone(), pallet.getQuantity()));
                palletField.put("Balanza", String.valueOf(pallet.getScaleNumber()));
                palletField.put("Numero de serie", pallet.getSerialNumber());
                palletField.put("Acumulado", pallet.getTotalNet());
                jsonArray.put(palletField);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }


}
