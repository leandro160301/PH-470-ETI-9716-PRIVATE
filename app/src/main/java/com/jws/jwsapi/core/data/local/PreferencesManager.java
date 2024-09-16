package com.jws.jwsapi.core.data.local;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import javax.inject.Inject;

public class PreferencesManager {
    private final Application application;
    public static String SP_NAME = "FRM_SP";

    @Inject
    public PreferencesManager(Application application) {
        this.application = application;
    }

    public int getBalanza() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("Balanza", 1));
    }

    public void setBalanza(int Balanza) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("Balanza", Balanza);
        ObjEditor2.apply();
    }



    public String getNetototal() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return Preferencias.getString("Netototal", "0");
    }


    public String getLote() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Lote", ""));
    }


    public String getCampo1Valor() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Campo1Valor", ""));
    }



    public String getCampo2Valor() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Campo2Valor", ""));
    }



    public String getCampo3Valor() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Campo3Valor", ""));
    }



    public String getCampo4Valor() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Campo4Valor", ""));
    }



    public String getCampo5Valor() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Campo5Valor", ""));
    }



    public String getVencimiento() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Vencimiento", ""));
    }

    public String getCampo1() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("campo1", "Analisis"));
    }

    public String getCampo2() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("campo2", "Orden"));
    }

    public String getCampo3() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("campo3", ""));
    }

    public String getCampo4() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("campo4", ""));
    }

    public String getCampo5() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("campo5", ""));
    }

    public List<Integer> getListSpinner(String etiqueta) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(etiqueta, null);
        Type type = new TypeToken<List<Integer>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public String getSeparador(String etiqueta, int posicion) {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString(etiqueta + "_concat_separador_" + posicion, ","));
    }

    public void setSeparador(String separador, String etiqueta, int posicion) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString(etiqueta + "_concat_separador_" + posicion, separador);
        ObjEditor2.apply();
    }

    public void saveListConcat(List<Integer> intList, String etiqueta, int posicion) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(intList);
        editor.putString(etiqueta + "_concat_" + posicion, json);
        editor.apply();
    }

    public List<Integer> getListConcat(String etiqueta, int posicion) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(etiqueta + "_concat_" + posicion, null);
        Type type = new TypeToken<List<Integer>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void saveListFijo(List<String> stringList, String etiqueta) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(stringList);
        editor.putString(etiqueta + "_fijo", json);
        editor.apply();
    }

    public List<String> getListFijo(String etiqueta) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(etiqueta + "_fijo", null);
        Type type = new TypeToken<List<String>>() {
        }.getType();
        return gson.fromJson(json, type);
    }

    public void saveListSpinner(List<Integer> stringList, String etiqueta) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(stringList);
        editor.putString(etiqueta, json);
        editor.apply();
    }
    
    public String getEtiqueta(int etiqueta) {
        SharedPreferences Preferencias= application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("etiqueta_actual_"+String.valueOf(etiqueta),""));
    }

    public void setEtiqueta(String etiqueta,int numetiqueta){
        SharedPreferences Preferencias2= application.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=Preferencias2.edit();
        ObjEditor2.putString("etiqueta_actual_"+String.valueOf(numetiqueta),etiqueta);
        ObjEditor2.apply();
    }

}
