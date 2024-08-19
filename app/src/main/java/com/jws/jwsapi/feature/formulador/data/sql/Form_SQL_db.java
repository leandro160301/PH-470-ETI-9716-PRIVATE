package com.jws.jwsapi.feature.formulador.data.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.TextUtils;
import androidx.annotation.Nullable;
import com.jws.jwsapi.feature.formulador.models.Form_Model_PesadasDB;
import com.jws.jwsapi.feature.formulador.models.Form_Model_RecetaDB;
import com.jws.jwsapi.utils.Utils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Form_SQL_db extends  SQLiteOpenHelper {

    private static final String[] COLUMN_PESADAS = {
            "Id", "idreceta","idpedido", "codigoreceta", "descripcionreceta", "codigoingrediente", "descripcioningrediente",
            "lote", "vencimiento", "turno", "neto", "bruto", "tara", "fecha", "hora", "campo1", "campo2", "campo3",
            "campo4", "campo5", "campo1valor", "campo2valor", "campo3valor", "campo4valor", "campo5valor",
            "operador", "setpoint", "reales", "campoextra1", "campoextra2", "balanza"
    };
    public static final String[] COLUMN_PESADAS_DES = {
            "N° pesada", "N° receta","N° pedido", "Codigo receta", "Descripcion receta", "Codigo Ingrediente", "Descripcion ingrediente",
            "Lote", "Vencimiento", "Turno", "Neto", "Bruto", "Tara", "Fecha", "Hora", "Campo 1", "Campo 2", "Campo 3",
            "Campo 4", "Campo 5", "Campo 1 valor", "Campo 2 valor", "Campo 3 valor", "Campo 4 valor", "Campo 5 valor",
            "Operador", "Setpoint", "Reales", "Campoextra1", "Campoextra2", "Balanza"
    };
    private static final String[] COLUMN_RECETAS = {
            "Id", "codigoreceta", "descripcionreceta", "lote", "vencimiento", "turno", "neto", "bruto", "tara", "fecha", "hora",
            "campo1", "campo2", "campo3", "campo4", "campo5", "campo1valor", "campo2valor", "campo3valor", "campo4valor", "campo5valor",
            "operador", "kilosaproducir", "campoextra1", "campoextra2", "balanza"
    };
    public static final String[] COLUMN_RECETAS_DES = {
            "N°", "Codigo receta", "Descripcion receta", "Lote", "Vencimiento", "Turno", "Acumulado", "Bruto", "Tara", "Fecha", "Hora",
            "Campo 1", "Campo 2", "Campo 3", "Campo 4", "Campo 5", "Campo 1 valor", "Campo 2 valor", "Campo 3 valor", "Campo 4 valor", "Campo 5 valor",
            "Operador", "Kilos a producir", "Campo extra 1", "Campo extra 2", "Balanza"
    };

    public Form_SQL_db(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase BaseDeDatos) {
        BaseDeDatos.execSQL("create table pesadas(Id INTEGER PRIMARY KEY AUTOINCREMENT , idreceta,idpedido, codigoreceta,descripcionreceta,codigoingrediente,descripcioningrediente, lote, vencimiento, turno, neto, bruto, tara, fecha, hora,campo1,campo2,campo3,campo4,campo5,campo1valor,campo2valor,campo3valor,campo4valor,campo5valor,operador,setpoint,reales, campoextra1,campoextra2,balanza)");
        BaseDeDatos.execSQL("create table recetas(Id INTEGER PRIMARY KEY AUTOINCREMENT, codigoreceta,descripcionreceta, lote, vencimiento, turno, neto, bruto, tara, fecha, hora,campo1,campo2,campo3,campo4,campo5,campo1valor,campo2valor,campo3valor,campo4valor,campo5valor,operador,kilosaproducir, campoextra1,campoextra2,balanza)");
        BaseDeDatos.execSQL("create table pedidos(Id INTEGER PRIMARY KEY AUTOINCREMENT, codigoreceta,descripcionreceta, lote, vencimiento, turno, neto, bruto, tara, fecha, hora,campo1,campo2,campo3,campo4,campo5,campo1valor,campo2valor,campo3valor,campo4valor,campo5valor,operador,kilosaproducir, campoextra1,campoextra2,balanza)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public long insertarReceta(String codigoreceta ,String descripcionreceta ,String  lote ,String  vencimiento ,String  turno ,String  neto ,String  bruto ,String  tara ,String  fecha ,String  hora ,String campo1 ,String campo2 ,String campo3 ,String campo4 ,String campo5 ,String campo1valor ,String campo2valor ,String campo3valor ,String campo4valor ,String campo5valor ,String operador ,String kilosaproducir ,String  campoextra1 ,String campoextra2,String balanza) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("codigoreceta", codigoreceta);
        values.put("descripcionreceta", descripcionreceta);
        values.put("lote", lote);
        values.put("vencimiento", vencimiento);
        values.put("turno", turno);
        values.put("neto", neto);
        values.put("bruto", bruto);
        values.put("tara", tara);
        values.put("fecha", fecha);
        values.put("hora", hora);
        values.put("campo1", campo1);
        values.put("campo2", campo2);
        values.put("campo3", campo3);
        values.put("campo4", campo4);
        values.put("campo5", campo5);
        values.put("campo1valor", campo1valor);
        values.put("campo2valor", campo2valor);
        values.put("campo3valor", campo3valor);
        values.put("campo4valor", campo4valor);
        values.put("campo5valor", campo5valor);
        values.put("operador", operador);
        values.put("kilosaproducir", kilosaproducir);
        values.put("campoextra1", campoextra1);
        values.put("campoextra2", campoextra2);
        values.put("balanza", balanza);
        long id = -1;
        try {
            id = db.insertOrThrow("recetas", null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return id;

    }

    public long insertarPedido(String codigoreceta ,String descripcionreceta ,String  lote ,String  vencimiento ,String  turno ,String  neto ,String  bruto ,String  tara ,String  fecha ,String  hora ,String campo1 ,String campo2 ,String campo3 ,String campo4 ,String campo5 ,String campo1valor ,String campo2valor ,String campo3valor ,String campo4valor ,String campo5valor ,String operador ,String kilosaproducir ,String  campoextra1 ,String campoextra2,String balanza) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("codigoreceta", codigoreceta);
        values.put("descripcionreceta", descripcionreceta);
        values.put("lote", lote);
        values.put("vencimiento", vencimiento);
        values.put("turno", turno);
        values.put("neto", neto);
        values.put("bruto", bruto);
        values.put("tara", tara);
        values.put("fecha", fecha);
        values.put("hora", hora);
        values.put("campo1", campo1);
        values.put("campo2", campo2);
        values.put("campo3", campo3);
        values.put("campo4", campo4);
        values.put("campo5", campo5);
        values.put("campo1valor", campo1valor);
        values.put("campo2valor", campo2valor);
        values.put("campo3valor", campo3valor);
        values.put("campo4valor", campo4valor);
        values.put("campo5valor", campo5valor);
        values.put("operador", operador);
        values.put("kilosaproducir", kilosaproducir);
        values.put("campoextra1", campoextra1);
        values.put("campoextra2", campoextra2);
        values.put("balanza", balanza);
        long id = -1;
        try {
            id = db.insertOrThrow("pedidos", null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return id;

    }

    public long insertarPesada(String idreceta , String idpedido,String  codigoreceta ,String descripcionreceta ,String codigoingrediente ,String descripcioningrediente ,String  lote ,String  vencimiento ,String  turno ,String  neto ,String  bruto ,String  tara ,String  fecha ,String  hora ,String campo1 ,String campo2 ,String campo3 ,String campo4 ,String campo5 ,String campo1valor ,String campo2valor ,String campo3valor ,String campo4valor ,String campo5valor ,String operador ,String setpoint ,String reales ,String  campoextra1 ,String campoextra2,String balanza) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("idreceta", idreceta);
        values.put("idpedido", idpedido);
        values.put("codigoreceta", codigoreceta);
        values.put("descripcionreceta", descripcionreceta);
        values.put("codigoingrediente", codigoingrediente);
        values.put("descripcioningrediente", descripcioningrediente);
        values.put("lote", lote);
        values.put("vencimiento", vencimiento);
        values.put("turno", turno);
        values.put("neto", neto);
        values.put("bruto", bruto);
        values.put("tara", tara);
        values.put("fecha", fecha);
        values.put("hora", hora);
        values.put("campo1", campo1);
        values.put("campo2", campo2);
        values.put("campo3", campo3);
        values.put("campo4", campo4);
        values.put("campo5", campo5);
        values.put("campo1valor", campo1valor);
        values.put("campo2valor", campo2valor);
        values.put("campo3valor", campo3valor);
        values.put("campo4valor", campo4valor);
        values.put("campo5valor", campo5valor);
        values.put("operador", operador);
        values.put("setpoint", setpoint);
        values.put("reales", reales);
        values.put("campoextra1", campoextra1);
        values.put("campoextra2", campoextra2);
        values.put("balanza", balanza);
        long id = -1;
        try {
            id = db.insertOrThrow("pesadas", null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }
        db.close();
        return id;

    }

    public int actualizarNetoTotalReceta(String netototal,String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put("neto", netototal);
            String whereClause = "id = ?";
            String[] whereArgs = { id };

            int filasActualizadas = db.update("recetas", values, whereClause, whereArgs);
            if (filasActualizadas > 0) {
                db.setTransactionSuccessful();
            }

            return filasActualizadas;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public int actualizarNetoTotalPedido(String netototal,String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        try {
            db.beginTransaction();

            ContentValues values = new ContentValues();
            values.put("neto", netototal);
            String whereClause = "id = ?";
            String[] whereArgs = { id };

            int filasActualizadas = db.update("pedidos", values, whereClause, whereArgs);
            if (filasActualizadas > 0) {
                db.setTransactionSuccessful();
            }

            return filasActualizadas;
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public void eliminarTodos() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query1 = "DELETE FROM pesadas";
        String query2 = "DELETE FROM recetas";
        String query3 = "DELETE FROM pedidos";
        db.execSQL(query1);
        db.execSQL(query2);
        db.execSQL(query3);
        db.close();
    }

    public void eliminarPesadas() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query1 = "DELETE FROM pesadas";
        db.execSQL(query1);
        db.close();
    }
    public void eliminarRecetas() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query1 = "DELETE FROM recetas";
        db.execSQL(query1);
        db.close();
    }
    public void eliminarPedidos() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query1 = "DELETE FROM pedidos";
        db.execSQL(query1);
        db.close();
    }


    public int getLastLote(String fecha) {
        SQLiteDatabase db = this.getReadableDatabase();

        // Construir la consulta SQL para obtener el máximo número de lote para la fecha dada
        String query = "SELECT MAX(CAST(SUBSTR(lote, INSTR(lote, '-') + 1) AS INTEGER)) AS max_numero_lote " +
                "FROM pesadas " +
                "WHERE SUBSTR(lote, 1, 8) = ?";

        Cursor cursor = db.rawQuery(query, new String[] { fecha });

        int maxNumeroLote = 0; // Valor predeterminado si no se encuentra ningún lote

        try {
            if (cursor.moveToFirst()) {
                int index=cursor.getColumnIndex("max_numero_lote");
                if(index>=0){
                    maxNumeroLote = cursor.getInt(index);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }

        return maxNumeroLote;
    }

    public List<Form_Model_PesadasDB> getPesadasSQL(Map<String, List<String>> filtros) {
        List<Form_Model_PesadasDB> filas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        List<String> whereClauses = new ArrayList<>();
        List<String> whereArgs = new ArrayList<>();

        if (filtros != null) {
            for (Map.Entry<String, List<String>> filtro : filtros.entrySet()) {
                String columna = filtro.getKey();
                List<String> valores = filtro.getValue();

                if (Arrays.asList(COLUMN_PESADAS_DES).contains(columna)) {
                    int index = Utils.indexOf(COLUMN_PESADAS_DES, columna);
                    if (index != -1) {
                        List<String> subClauses = new ArrayList<>();
                        for (String valor : valores) {
                            subClauses.add("LOWER(" + COLUMN_PESADAS[index] + ") LIKE ?");
                            whereArgs.add("%" + valor.toLowerCase() + "%");
                        }
                        if (!subClauses.isEmpty()) {
                            whereClauses.add("(" + TextUtils.join(" OR ", subClauses) + ")");
                        }
                    }
                }
            }
        }

        String whereClause = null;
        if (!whereClauses.isEmpty()) {
            whereClause = TextUtils.join(" AND ", whereClauses);
        }

        String query = "SELECT * FROM pesadas";
        if (whereClause != null) {
            query += " WHERE " + whereClause;
        }
        query += " ORDER BY Id DESC";

        if (filtros != null) {
            if (filtros.containsKey("limit")) {
                String limitStr = filtros.get("limit").get(0);
                query += " LIMIT " + Integer.parseInt(limitStr);
            }

            if (filtros.containsKey("offset")) {
                String offsetStr = filtros.get("offset").get(0);
                query += " OFFSET " + Integer.parseInt(offsetStr);
            }
        }

        Cursor cursor = db.rawQuery(query, whereArgs.toArray(new String[0]));

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String idReceta = cursor.getString(1);
                String idPedido = cursor.getString(2);
                String codigoReceta = cursor.getString(3);
                String descripcionReceta = cursor.getString(4);
                String codigoIngrediente = cursor.getString(5);
                String descripcionIngrediente = cursor.getString(6);
                String lote = cursor.getString(7);
                String vencimiento = cursor.getString(8);
                String turno = cursor.getString(9);
                String neto = cursor.getString(10);
                String bruto = cursor.getString(11);
                String tara = cursor.getString(12);
                String fecha = cursor.getString(13);
                String hora = cursor.getString(14);
                String campo1 = cursor.getString(15);
                String campo2 = cursor.getString(16);
                String campo3 = cursor.getString(17);
                String campo4 = cursor.getString(18);
                String campo5 = cursor.getString(19);
                String campo1Valor = cursor.getString(20);
                String campo2Valor = cursor.getString(21);
                String campo3Valor = cursor.getString(22);
                String campo4Valor = cursor.getString(23);
                String campo5Valor = cursor.getString(24);
                String operador = cursor.getString(25);
                String setPoint = cursor.getString(26);
                String reales = cursor.getString(27);
                String campoExtra1 = cursor.getString(28);
                String campoExtra2 = cursor.getString(29);
                String balanza = cursor.getString(30);

                Form_Model_PesadasDB fila = new Form_Model_PesadasDB(
                        id, idReceta, idPedido, codigoReceta, descripcionReceta, codigoIngrediente, descripcionIngrediente,
                        lote, vencimiento, turno, neto, bruto, tara, fecha, hora, campo1, campo2, campo3,
                        campo4, campo5, campo1Valor, campo2Valor, campo3Valor, campo4Valor, campo5Valor,
                        operador, setPoint, reales, campoExtra1, campoExtra2,balanza
                );
                filas.add(fila);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return filas;
    }


    public List<Form_Model_PesadasDB> getPesadasSQLconId(String ID, Boolean receta) {
        List<Form_Model_PesadasDB> filas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query="";

        if(receta){
            query = "SELECT * FROM pesadas WHERE idreceta='"+ID+"'";
        }else{
            query = "SELECT * FROM pesadas WHERE idpedido='"+ID+"'";
        }


        Cursor cursor = db.rawQuery(query,null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String idReceta = cursor.getString(1);
                String idPedido = cursor.getString(2);
                String codigoReceta = cursor.getString(3);
                String descripcionReceta = cursor.getString(4);
                String codigoIngrediente = cursor.getString(5);
                String descripcionIngrediente = cursor.getString(6);
                String lote = cursor.getString(7);
                String vencimiento = cursor.getString(8);
                String turno = cursor.getString(9);
                String neto = cursor.getString(10);
                String bruto = cursor.getString(11);
                String tara = cursor.getString(12);
                String fecha = cursor.getString(13);
                String hora = cursor.getString(14);
                String campo1 = cursor.getString(15);
                String campo2 = cursor.getString(16);
                String campo3 = cursor.getString(17);
                String campo4 = cursor.getString(18);
                String campo5 = cursor.getString(19);
                String campo1Valor = cursor.getString(20);
                String campo2Valor = cursor.getString(21);
                String campo3Valor = cursor.getString(22);
                String campo4Valor = cursor.getString(23);
                String campo5Valor = cursor.getString(24);
                String operador = cursor.getString(25);
                String setPoint = cursor.getString(26);
                String reales = cursor.getString(27);
                String campoExtra1 = cursor.getString(28);
                String campoExtra2 = cursor.getString(29);
                String balanza = cursor.getString(30);

                Form_Model_PesadasDB fila = new Form_Model_PesadasDB(
                        id, idReceta,idPedido,codigoReceta, descripcionReceta, codigoIngrediente, descripcionIngrediente,
                        lote, vencimiento, turno, neto, bruto, tara, fecha, hora, campo1, campo2, campo3,
                        campo4, campo5, campo1Valor, campo2Valor, campo3Valor, campo4Valor, campo5Valor,
                        operador, setPoint, reales, campoExtra1, campoExtra2, balanza
                );
                filas.add(fila);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return filas;
    }

    public List<Form_Model_RecetaDB> getRecetasSQL(Map<String, List<String>> filtros) {
        List<Form_Model_RecetaDB> filas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        List<String> whereClauses = new ArrayList<>();
        List<String> whereArgs = new ArrayList<>();

        if (filtros != null) {
            for (Map.Entry<String, List<String>> filtro : filtros.entrySet()) {
                String columna = filtro.getKey();
                List<String> valores = filtro.getValue();

                if (Arrays.asList(COLUMN_RECETAS_DES).contains(columna)) {
                    int index = Utils.indexOf(COLUMN_RECETAS_DES, columna);
                    if (index != -1) {
                        List<String> subClauses = new ArrayList<>();
                        for (String valor : valores) {
                            subClauses.add("LOWER(" + COLUMN_RECETAS[index] + ") LIKE ?");
                            whereArgs.add("%" + valor.toLowerCase() + "%");
                        }
                        if (!subClauses.isEmpty()) {
                            whereClauses.add("(" + TextUtils.join(" OR ", subClauses) + ")");
                        }
                    }
                }
            }
        }

        String whereClause = null;
        if (!whereClauses.isEmpty()) {
            whereClause = TextUtils.join(" AND ", whereClauses);
        }

        String query = "SELECT * FROM recetas";
        if (whereClause != null) {
            query += " WHERE " + whereClause;
        }
        query += " ORDER BY Id DESC";

        if (filtros != null) {
            if (filtros.containsKey("limit")) {
                String limitStr = filtros.get("limit").get(0);
                query += " LIMIT " + Integer.parseInt(limitStr);
            }

            if (filtros.containsKey("offset")) {
                String offsetStr = filtros.get("offset").get(0);
                query += " OFFSET " + Integer.parseInt(offsetStr);
            }
        }

        Cursor cursor = db.rawQuery(query, whereArgs.toArray(new String[0]));

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String codigoReceta = cursor.getString(1);
                String descripcionReceta = cursor.getString(2);
                String lote = cursor.getString(3);
                String vencimiento = cursor.getString(4);
                String turno = cursor.getString(5);
                String neto = cursor.getString(6);
                String bruto = cursor.getString(7);
                String tara = cursor.getString(8);
                String fecha = cursor.getString(9);
                String hora = cursor.getString(10);
                String campo1 = cursor.getString(11);
                String campo2 = cursor.getString(12);
                String campo3 = cursor.getString(13);
                String campo4 = cursor.getString(14);
                String campo5 = cursor.getString(15);
                String campo1Valor = cursor.getString(16);
                String campo2Valor = cursor.getString(17);
                String campo3Valor = cursor.getString(18);
                String campo4Valor = cursor.getString(19);
                String campo5Valor = cursor.getString(20);
                String operador = cursor.getString(21);
                String kilosAProducir = cursor.getString(22);
                String campoExtra1 = cursor.getString(23);
                String campoExtra2 = cursor.getString(24);
                String balanza = cursor.getString(25);

                Form_Model_RecetaDB fila = new Form_Model_RecetaDB(
                        id, codigoReceta, descripcionReceta, lote, vencimiento, turno,
                        neto, bruto, tara, fecha, hora, campo1, campo2, campo3, campo4, campo5,
                        campo1Valor, campo2Valor, campo3Valor, campo4Valor, campo5Valor,
                        operador, kilosAProducir, campoExtra1, campoExtra2,balanza
                );
                filas.add(fila);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return filas;
    }

    public List<Form_Model_RecetaDB> getPedidosSQL(Map<String, List<String>> filtros) {
        List<Form_Model_RecetaDB> filas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        List<String> whereClauses = new ArrayList<>();
        List<String> whereArgs = new ArrayList<>();

        if (filtros != null) {
            for (Map.Entry<String, List<String>> filtro : filtros.entrySet()) {
                String columna = filtro.getKey();
                List<String> valores = filtro.getValue();

                if (Arrays.asList(COLUMN_RECETAS_DES).contains(columna)) {
                    int index = Utils.indexOf(COLUMN_RECETAS_DES, columna);
                    if (index != -1) {
                        List<String> subClauses = new ArrayList<>();
                        for (String valor : valores) {
                            subClauses.add("LOWER(" + COLUMN_RECETAS[index] + ") LIKE ?");
                            whereArgs.add("%" + valor.toLowerCase() + "%");
                        }
                        if (!subClauses.isEmpty()) {
                            whereClauses.add("(" + TextUtils.join(" OR ", subClauses) + ")");
                        }
                    }
                }
            }
        }

        String whereClause = null;
        if (!whereClauses.isEmpty()) {
            whereClause = TextUtils.join(" AND ", whereClauses);
        }

        String query = "SELECT * FROM pedidos";
        if (whereClause != null) {
            query += " WHERE " + whereClause;
        }
        query += " ORDER BY Id DESC";

        if (filtros != null) {
            if (filtros.containsKey("limit")) {
                String limitStr = filtros.get("limit").get(0);
                query += " LIMIT " + Integer.parseInt(limitStr);
            }

            if (filtros.containsKey("offset")) {
                String offsetStr = filtros.get("offset").get(0);
                query += " OFFSET " + Integer.parseInt(offsetStr);
            }
        }

        Cursor cursor = db.rawQuery(query, whereArgs.toArray(new String[0]));

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String codigoReceta = cursor.getString(1);
                String descripcionReceta = cursor.getString(2);
                String lote = cursor.getString(3);
                String vencimiento = cursor.getString(4);
                String turno = cursor.getString(5);
                String neto = cursor.getString(6);
                String bruto = cursor.getString(7);
                String tara = cursor.getString(8);
                String fecha = cursor.getString(9);
                String hora = cursor.getString(10);
                String campo1 = cursor.getString(11);
                String campo2 = cursor.getString(12);
                String campo3 = cursor.getString(13);
                String campo4 = cursor.getString(14);
                String campo5 = cursor.getString(15);
                String campo1Valor = cursor.getString(16);
                String campo2Valor = cursor.getString(17);
                String campo3Valor = cursor.getString(18);
                String campo4Valor = cursor.getString(19);
                String campo5Valor = cursor.getString(20);
                String operador = cursor.getString(21);
                String kilosAProducir = cursor.getString(22);
                String campoExtra1 = cursor.getString(23);
                String campoExtra2 = cursor.getString(24);
                String balanza = cursor.getString(25);

                Form_Model_RecetaDB fila = new Form_Model_RecetaDB(
                        id, codigoReceta, descripcionReceta, lote, vencimiento, turno,
                        neto, bruto, tara, fecha, hora, campo1, campo2, campo3, campo4, campo5,
                        campo1Valor, campo2Valor, campo3Valor, campo4Valor, campo5Valor,
                        operador, kilosAProducir, campoExtra1, campoExtra2, balanza
                );
                filas.add(fila);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();

        return filas;
    }


}
