package com.jws.jwsapi.feature.formulador.data.sql;

import android.content.Context;
import com.jws.jwsapi.feature.formulador.MainFormClass;
import com.jws.jwsapi.feature.formulador.models.FormModelPesadasDB;
import com.jws.jwsapi.feature.formulador.models.FormModelRecetaDB;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FormSqlUtil {

    public static String JSONconsultas() throws JSONException {
        JSONArray jsonArray = new JSONArray();
        try {
            JSONObject PESADAS = new JSONObject();
            PESADAS.put("GET", "GetPesadas");
            PESADAS.put("Nombre", "PESADAS");

            JSONObject RECETAS = new JSONObject();
            RECETAS.put("GET", "GetRecetas");
            RECETAS.put("Nombre", "RECETAS");

            JSONObject INGREDIENTES = new JSONObject();
            INGREDIENTES.put("GET", "GetPedidos");
            INGREDIENTES.put("Nombre", "PEDIDOS");

            jsonArray.put(PESADAS);
            jsonArray.put(RECETAS);
            jsonArray.put(INGREDIENTES);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray.toString();
    }

    public static String JSONpesadas(Map<String, List<String>> filtros, String columnaEspecifica, Context context) throws JSONException {
        List<FormModelPesadasDB> guardado;
        try (FormSqlHelper guardadosSQL = new FormSqlHelper(context, MainFormClass.DB_NAME, null, MainFormClass.DB_VERSION)) {
            guardado = guardadosSQL.getPesadasSQL(filtros);
        }

        Set<Object> valoresUnicos = new HashSet<>();
        JSONArray jsonArray = new JSONArray();

        try {
            for (int i = 0; i < guardado.size(); i++) {
                JSONObject Pesada = new JSONObject();
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[0], String.valueOf(guardado.get(i).getId()));
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[1], guardado.get(i).getIdReceta());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[2], String.valueOf(guardado.get(i).getIdPedido()));
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[3], guardado.get(i).getCodigoReceta());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[4], guardado.get(i).getDescripcionReceta());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[5], guardado.get(i).getCodigoIngrediente());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[6], guardado.get(i).getDescripcionIngrediente());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[7], guardado.get(i).getLote());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[8], guardado.get(i).getVencimiento());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[9], guardado.get(i).getTurno());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[10], guardado.get(i).getNeto());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[25], guardado.get(i).getOperador());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[26], guardado.get(i).getSetPoint());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[27], guardado.get(i).getReales());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[11], guardado.get(i).getBruto());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[12], guardado.get(i).getTara());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[13], guardado.get(i).getFecha());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[14], guardado.get(i).getHora());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[15], guardado.get(i).getCampo1());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[16], guardado.get(i).getCampo2());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[17], guardado.get(i).getCampo3());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[18], guardado.get(i).getCampo4());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[19], guardado.get(i).getCampo5());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[20], guardado.get(i).getCampo1Valor());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[21], guardado.get(i).getCampo2Valor());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[22], guardado.get(i).getCampo3Valor());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[23], guardado.get(i).getCampo4Valor());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[24], guardado.get(i).getCampo5Valor());
                Pesada.put(FormSqlHelper.COLUMN_PESADAS_DES[30], guardado.get(i).getBalanza());

                if (columnaEspecifica != null && !columnaEspecifica.isEmpty()) {
                    Object valorColumna = Pesada.get(columnaEspecifica);
                    if (valoresUnicos.add(valorColumna)) {
                        jsonArray.put(valorColumna);
                    }
                }else {
                    jsonArray.put(Pesada);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray.toString();
    }
    public static String JSONrecetas(Map<String, List<String>> filtros, String columnaEspecifica, Context context) throws JSONException {
        List<FormModelRecetaDB> guardado;
        // Supongamos que tienes un método para obtener los datos desde la base de datos
        try (FormSqlHelper guardadosSQL = new FormSqlHelper(context, MainFormClass.DB_NAME, null, MainFormClass.DB_VERSION)) {
            guardado = guardadosSQL.getRecetasSQL(filtros);
        }
        Set<Object> valoresUnicos = new HashSet<>();
        JSONArray jsonArray = new JSONArray();

        try {
            for (FormModelRecetaDB receta : guardado) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[0], String.valueOf(receta.getId()));
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[1], receta.getCodigoReceta());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[2], receta.getDescripcionReceta());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[3], receta.getLote());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[4], receta.getVencimiento());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[5], receta.getTurno());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[6], receta.getNeto());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[22], receta.getKilosAProducir());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[21], receta.getOperador());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[7], receta.getBruto());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[8], receta.getTara());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[9], receta.getFecha());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[10], receta.getHora());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[11], receta.getCampo1());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[12], receta.getCampo2());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[13], receta.getCampo3());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[14], receta.getCampo4());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[15], receta.getCampo5());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[16], receta.getCampo1Valor());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[17], receta.getCampo2Valor());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[18], receta.getCampo3Valor());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[19], receta.getCampo4Valor());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[20], receta.getCampo5Valor());

                if (columnaEspecifica != null && !columnaEspecifica.isEmpty()) {
                    Object valorColumna = jsonObject.get(columnaEspecifica);
                    if (valoresUnicos.add(valorColumna)) {
                        jsonArray.put(valorColumna);
                    }
                }else {
                    jsonArray.put(jsonObject);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray.toString();
    }

    public static String JSONpedidos(Map<String, List<String>> filtros, String columnaEspecifica, Context context) throws JSONException {
        List<FormModelRecetaDB> guardado;
        try (FormSqlHelper guardadosSQL = new FormSqlHelper(context, MainFormClass.DB_NAME, null, MainFormClass.DB_VERSION)) {
            guardado = guardadosSQL.getPedidosSQL(filtros);
        }
        Set<Object> valoresUnicos = new HashSet<>();
        JSONArray jsonArray = new JSONArray();

        try {
            for (FormModelRecetaDB receta : guardado) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[0], String.valueOf(receta.getId()));
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[1], receta.getCodigoReceta());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[2], receta.getDescripcionReceta());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[3], receta.getLote());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[4], receta.getVencimiento());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[5], receta.getTurno());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[6], receta.getNeto());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[22], receta.getKilosAProducir());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[21], receta.getOperador());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[7], receta.getBruto());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[8], receta.getTara());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[9], receta.getFecha());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[10], receta.getHora());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[11], receta.getCampo1());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[12], receta.getCampo2());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[13], receta.getCampo3());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[14], receta.getCampo4());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[15], receta.getCampo5());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[16], receta.getCampo1Valor());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[17], receta.getCampo2Valor());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[18], receta.getCampo3Valor());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[19], receta.getCampo4Valor());
                jsonObject.put(FormSqlHelper.COLUMN_RECETAS_DES[20], receta.getCampo5Valor());

                if (columnaEspecifica != null && !columnaEspecifica.isEmpty()) {
                    Object valorColumna = jsonObject.get(columnaEspecifica);
                    if (valoresUnicos.add(valorColumna)) {
                        jsonArray.put(valorColumna);
                    }
                }else {
                    jsonArray.put(jsonObject);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray.toString();
    }
}
