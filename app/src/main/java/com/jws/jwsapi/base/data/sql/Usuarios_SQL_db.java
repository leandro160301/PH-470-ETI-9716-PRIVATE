package com.jws.jwsapi.base.data.sql;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.jws.jwsapi.base.models.UsuariosModel;

import java.util.ArrayList;
import java.util.List;

public class Usuarios_SQL_db extends  SQLiteOpenHelper {

    public Usuarios_SQL_db(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, 3);
    }

    @Override
    public void onCreate(SQLiteDatabase BaseDeDatos) {
        BaseDeDatos.execSQL("create table usuarios(Id INTEGER PRIMARY KEY AUTOINCREMENT ,nombre,usuario," +
                "password,codigo,tipo,Permiso1," +
                "Permiso2,Permiso3)");

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }


    public long nuevoUsuario(String nombre, String usuario, String password, String codigo,String tipo,String Permiso1, String Permiso2, String Permiso3) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("usuario", usuario);
        values.put("password", password);
        values.put("codigo", codigo);
        values.put("tipo", tipo);
        values.put("Permiso1",Permiso1);
        values.put("Permiso2", Permiso2);
        values.put("Permiso3", Permiso3);

        long id = -1;  // Inicializamos id con un valor predeterminado

        try {
            id = db.insertOrThrow("usuarios", null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();
        return id;
    }

    public void eliminarTodos() {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "DELETE FROM usuarios";
        db.execSQL(query);

        db.close();
    }

    public void eliminarUsuario(String user) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "DELETE FROM usuarios WHERE usuario='"+user+"'";
        db.execSQL(query);

        db.close();
    }

    public List<UsuariosModel> obtenerUsuarios() {
        List<UsuariosModel> filas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = " SELECT * FROM " + "usuarios" + " ORDER BY Id DESC";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                int id = cursor.getInt(0);
                String nombre=cursor.getString(1);
                String usuario=cursor.getString(2);
                String password=cursor.getString(3);
                String codigo=cursor.getString(4);
                String tipo=cursor.getString(5);
                UsuariosModel fila = new UsuariosModel(id,nombre,usuario,password,codigo,tipo);
                filas.add(fila);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return filas;
    }

    public List<UsuariosModel> buscarUsuario(String user) {
        List<UsuariosModel> filas = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = " SELECT * FROM " + "usuarios" + " WHERE usuario='"+user+"'";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            do {

                int id = cursor.getInt(0);
                String nombre=cursor.getString(1);
                String usuario=cursor.getString(2);
                String password=cursor.getString(3);
                String codigo=cursor.getString(4);
                String tipo=cursor.getString(5);

                UsuariosModel fila = new UsuariosModel(id,nombre,usuario,password,codigo,tipo);
                filas.add(fila);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return filas;
    }

    public int cantidadUsuarios() {
        int count = 0;
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT COUNT(*) FROM usuarios";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        db.close();

        return count;
    }

}
