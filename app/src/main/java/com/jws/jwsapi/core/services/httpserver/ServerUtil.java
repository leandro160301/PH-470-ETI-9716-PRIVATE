package com.jws.jwsapi.core.services.httpserver;

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

            jsonArray.put(jsonWeighings);

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
                weighingField.put("N° Pesada", weighing.getTopTare());
                weighingField.put("Producto", weighing.getProduct());
                weighingField.put("Destinatario", weighing.getDestination());
                weighingField.put("Lote", weighing.getBatch());
                weighingField.put("Calibre", weighing.getCaliber());
                weighingField.put("Fecha", weighing.getDate());
                weighingField.put("Hora", weighing.getHour());
                weighingField.put("Linea", weighing.getLine());
                weighingField.put("Vencimiento", weighing.getExpirateDate());
                weighingField.put("Tara envase", weighing.getBoxTare());
                weighingField.put("Tara partes", weighing.getPartsTare());
                weighingField.put("Cantidad piezas", weighing.getPartsQuantity());
                weighingField.put("Bruto", weighing.getGross());
                weighingField.put("Neto", weighing.getNet());
                weighingField.put("Unidad", weighing.getUnit());
                weighingField.put("Operador", weighing.getOperator());

                jsonArray.put(weighingField);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }


}
