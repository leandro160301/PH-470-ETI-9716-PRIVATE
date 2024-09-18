package com.jws.jwsapi.core.printer;

import com.jws.jwsapi.core.data.local.PreferencesHelper;

import java.util.List;

import javax.inject.Inject;

public class PrinterPreferences {

    private final PreferencesHelper preferencesHelper;

    @Inject
    public PrinterPreferences(PreferencesHelper preferencesHelper) {
        this.preferencesHelper = preferencesHelper;
    }

    public List<Integer> getListSpinner(String etiqueta) {
        return preferencesHelper.getIntegerList(etiqueta);
    }

    public void saveListSpinner(List<Integer> stringList, String etiqueta) {
        preferencesHelper.putIntegerList(etiqueta,stringList);
    }

    public String getSeparador(String etiqueta, int posicion) {
        return preferencesHelper.getString(etiqueta+"_concat_separador_" + posicion, ",");
    }

    public void setSeparador(String separador, String etiqueta, int posicion) {
        preferencesHelper.putString(etiqueta + "_concat_separador_" + posicion, separador);
    }

    public void saveListConcat(List<Integer> intList, String etiqueta, int posicion) {
        preferencesHelper.putIntegerList(etiqueta + "_concat_" + posicion, intList);
    }

    public List<Integer> getListConcat(String etiqueta, int posicion) {
        return preferencesHelper.getIntegerList(etiqueta + "_concat_" + posicion);
    }

    public void saveListFijo(List<String> stringList, String etiqueta) {
        preferencesHelper.putStringList(etiqueta + "_fijo",stringList);
    }

    public List<String> getListFijo(String etiqueta) {
        return preferencesHelper.getStringList(etiqueta + "_fijo");
    }
    
    public String getEtiqueta(int etiqueta) {
        return preferencesHelper.getString("etiqueta_actual_"+ etiqueta,"");
    }

    public void setEtiqueta(String etiqueta,int numetiqueta){
        preferencesHelper.putString("etiqueta_actual_"+ numetiqueta,etiqueta);
    }

    public String getLastLabel() {
        return preferencesHelper.getString("Ultimaetiqueta","");
    }

    public void setLastLabel(String zplCode) {
        preferencesHelper.putString("Ultimaetiqueta",zplCode);
    }

    public int getMode() {
        return preferencesHelper.getInt("Modo",0);
    }

    public void setMode(int Modo) {
        preferencesHelper.putInt("Modo",Modo);
    }

    public String getIp() {
        return preferencesHelper.getString("IP","10.41.0.109");
    }

    public void setIp(String ip) {
        preferencesHelper.putString("IP",ip);
    }

    public String getMac() {
        return preferencesHelper.getString("MAC","60:95:32:0C:D6:DE");
    }

    public void setMac(String mac) {
        preferencesHelper.putString("MAC",mac);
    }

}
