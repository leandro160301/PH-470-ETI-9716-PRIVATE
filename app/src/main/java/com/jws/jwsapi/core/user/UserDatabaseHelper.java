package com.jws.jwsapi.core.user;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class UserDatabaseHelper extends  SQLiteOpenHelper {

    public UserDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
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


    public long createUser(String nombre, String usuario, String password, String codigo, String tipo, String Permiso1, String Permiso2, String Permiso3) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("nombre", nombre);
        values.put("userName", usuario);
        values.put("password", password);
        values.put("codigo", codigo);
        values.put("tipo", tipo);
        values.put("Permiso1",Permiso1);
        values.put("Permiso2", Permiso2);
        values.put("Permiso3", Permiso3);

        long id = -1;

        try {
            id = db.insertOrThrow("usuarios", null, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        db.close();
        return id;
    }

    public void deleteAllUsers() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM usuarios";
        db.execSQL(query);
        db.close();
    }

    public void deleteUser(String user) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "DELETE FROM usuarios WHERE usuario='"+user+"'";
        db.execSQL(query);
        db.close();
    }

    public List<UserModel> getAllUsers() {
        List<UserModel> filas = new ArrayList<>();
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
                UserModel fila = new UserModel(id,nombre,usuario,password,codigo,tipo);
                filas.add(fila);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return filas;
    }

    public List<UserModel> searchUsers(String user) {
        List<UserModel> filas = new ArrayList<>();
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

                UserModel fila = new UserModel(id,nombre,usuario,password,codigo,tipo);
                filas.add(fila);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return filas;
    }

    public int getQuantity() {
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
