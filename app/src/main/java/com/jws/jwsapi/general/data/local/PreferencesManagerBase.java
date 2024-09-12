package com.jws.jwsapi.general.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import com.jws.jwsapi.R;

public class PreferencesManagerBase {
    private Context context;
    public static String SP_NAME = "MAIN_SP";

    public PreferencesManagerBase(Context context) {
        this.context=context;
    }
    public String consultaPIN() {
        //Trae el PIN guardado en memoria
        SharedPreferences preferences3=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return preferences3.getString("PIN","1111");
    }

    public void nuevoPin(String pin){
        SharedPreferences preferencias2=context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=preferencias2.edit();
        ObjEditor2.putString("PIN",pin);
        ObjEditor2.apply();
    }

    public Boolean getCorreccionRemoto() {
        SharedPreferences preferences3=context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return preferences3.getBoolean("Display",false);
    }

    public void setCorreccionRemoto(Boolean display){
        SharedPreferences preferencias2=context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=preferencias2.edit();
        ObjEditor2.putBoolean("Display",display);
        ObjEditor2.apply();
    }

    public int consultaTema() {
        SharedPreferences preferences3=context.getSharedPreferences("Tema", Context.MODE_PRIVATE);
        return preferences3.getInt("theme", R.style.AppTheme_NoActionBar);
    }

    public void nuevoTema(int tema){
        SharedPreferences preferencias2=context.getSharedPreferences("Tema",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=preferencias2.edit();
        ObjEditor2.putInt("theme",tema);
        ObjEditor2.apply();
    }

    public int getEthMode() {
        SharedPreferences preferences3=context.getSharedPreferences("ETH", Context.MODE_PRIVATE);
        return preferences3.getInt("ethmode",0);// 0=DHCP  1= ESTATICO
    }

    public void setEthMode(int modo){
        SharedPreferences preferencias2=context.getSharedPreferences("ETH",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=preferencias2.edit();
        ObjEditor2.putInt("ethmode",modo);
        ObjEditor2.apply();
    }
    public Boolean getAuthorization() {
        SharedPreferences preferences3=context.getSharedPreferences("AuthorizationEnable", Context.MODE_PRIVATE);
        return preferences3.getBoolean("AuthorizationEnable",false);
    }

    public void setAuthorization(Boolean auth){
        SharedPreferences preferencias2=context.getSharedPreferences("AuthorizationEnable",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=preferencias2.edit();
        ObjEditor2.putBoolean("AuthorizationEnable",auth);
        ObjEditor2.apply();
    }
    public String getIPstatic() {
        SharedPreferences preferences3=context.getSharedPreferences("ETH", Context.MODE_PRIVATE);
        return preferences3.getString("ethip","10.41.0.47");
    }

    public void setIPstatic(String ip){
        SharedPreferences preferencias2=context.getSharedPreferences("ETH",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=preferencias2.edit();
        ObjEditor2.putString("ethip",ip);
        ObjEditor2.apply();
    }
    public String getSubnet() {
        SharedPreferences preferences3=context.getSharedPreferences("ETH", Context.MODE_PRIVATE);
        return preferences3.getString("ethsub","255.255.255.0");
    }

    public void setSubnet(String ip){
        SharedPreferences preferencias2=context.getSharedPreferences("ETH",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=preferencias2.edit();
        ObjEditor2.putString("ethsub",ip);
        ObjEditor2.apply();
    }

    public String getGateway() {
        SharedPreferences preferences3=context.getSharedPreferences("ETH", Context.MODE_PRIVATE);
        return preferences3.getString("ethgateway","10.41.0.254");
    }

    public void setGateway(String ip){
        SharedPreferences preferencias2=context.getSharedPreferences("ETH",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=preferencias2.edit();
        ObjEditor2.putString("ethgateway",ip);
        ObjEditor2.apply();
    }

    public String getDNS1() {
        SharedPreferences preferences3=context.getSharedPreferences("ETH", Context.MODE_PRIVATE);
        return preferences3.getString("ethDNS1","10.41.0.112");
    }

    public void setDNS1(String ip){
        SharedPreferences preferencias2=context.getSharedPreferences("ETH",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=preferencias2.edit();
        ObjEditor2.putString("ethDNS1",ip);
        ObjEditor2.apply();
    }

    public String getDNS2() {
        SharedPreferences preferences3=context.getSharedPreferences("ETH", Context.MODE_PRIVATE);
        return preferences3.getString("ethDNS2","10.41.0.112");
    }

    public void setDNS2(String ip){
        SharedPreferences preferencias2=context.getSharedPreferences("ETH",Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=preferencias2.edit();
        ObjEditor2.putString("ethDNS2",ip);
        ObjEditor2.apply();
    }

}
