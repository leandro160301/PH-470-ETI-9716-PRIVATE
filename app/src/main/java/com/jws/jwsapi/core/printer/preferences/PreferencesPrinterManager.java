package com.jws.jwsapi.core.printer.preferences;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferencesPrinterManager {
    private final Context context;
    public static String SP_NAME = "Printer";

    public PreferencesPrinterManager(Context context) {
        this.context=context;
    }

    public int consultaModo() {
        int modo =0;
        SharedPreferences Preferencias=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        modo =(Preferencias.getInt("Modo",0));
        return modo;
    }
    public void setModo(int Modo) {
        SharedPreferences Preferencias2=context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=Preferencias2.edit();
        ObjEditor2.putInt("Modo",Modo);
        ObjEditor2.apply();
    }

    public String consultaIP() {
        String  modo ="10.41.0.109";
        SharedPreferences Preferencias=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        modo =(Preferencias.getString("IP",modo));
        return modo;
    }
    public void setIP(String ip) {
        SharedPreferences Preferencias2=context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=Preferencias2.edit();
        ObjEditor2.putString("IP",ip);
        ObjEditor2.apply();
    }

    public String consultaMAC() {
        SharedPreferences Preferencias=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String mac=Preferencias.getString("MAC","60:95:32:0C:D6:DE");
        return mac;
    }
    public void setMAC(String MAC) {
        SharedPreferences Preferencias2=context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=Preferencias2.edit();
        ObjEditor2.putString("MAC",MAC);
        ObjEditor2.apply();
    }

    public String getultimaEtiqueta() {
        SharedPreferences Preferencias=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        String mac=Preferencias.getString("Ultimaetiqueta","");
        return mac;
    }
    public void setUltimaEtiqueta(String MAC) {
        SharedPreferences Preferencias2=context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=Preferencias2.edit();
        ObjEditor2.putString("Ultimaetiqueta",MAC);
        ObjEditor2.apply();
    }

}
