package com.jws.jwsapi.feature.formulador.data.preferences;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jws.jwsapi.feature.formulador.models.FormModelReceta;
import java.lang.reflect.Type;
import java.util.ArrayList;
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


    public long getRecetaId() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return Preferencias.getLong("RecetaID", 0);
    }
    public void setRecetaId(long RecetaID) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putLong("RecetaID", RecetaID);
        ObjEditor2.apply();
    }

    public long getPedidoId() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return Preferencias.getLong("PedidoID", 0);
    }
    public void setPedidoId(long RecetaID) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putLong("PedidoID", RecetaID);
        ObjEditor2.apply();
    }


    public void setPasosRecetaActual(List<FormModelReceta> recetaActual) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(recetaActual);
        editor.putString("pasos_recetaactual", json);
        editor.apply();
    }

    public List<FormModelReceta> getPasosRecetaActual() {
        SharedPreferences sharedPreferences = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("pasos_recetaactual", null);
        Type type = new TypeToken<ArrayList<FormModelReceta>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public int getModoReceta() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("ModoReceta", 0));
    }

    public void setModoReceta(int Balanza) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("ModoReceta", Balanza);
        ObjEditor2.apply();
    }

    public int getModoUso() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("ModoUso", 0));
    }

    public void setModoUso(int Balanza) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("ModoUso", Balanza);
        ObjEditor2.apply();
    }

    public int getModoBalanza() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("ModoBalanza", 0));
    }

    public void setModoBalanza(int Balanza) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("ModoBalanza", Balanza);
        ObjEditor2.apply();
    }

    public int getTurno1() {
        SharedPreferences Preferencias= application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("turno1",8));
    }
    public void setTurno1(int turno1){
        SharedPreferences Preferencias2= application.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=Preferencias2.edit();
        ObjEditor2.putInt("turno1",turno1);
        ObjEditor2.apply();
    }

    public int getTurno2() {
        SharedPreferences Preferencias= application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("turno2",13));
    }
    public void setTurno2(int turno2){
        SharedPreferences Preferencias2= application.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=Preferencias2.edit();
        ObjEditor2.putInt("turno2",turno2);
        ObjEditor2.apply();
    }

    public int getTurno3() {
        SharedPreferences Preferencias= application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("turno3",17));
    }
    public void setTurno3(int turno3){
        SharedPreferences Preferencias2= application.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=Preferencias2.edit();
        ObjEditor2.putInt("turno3",turno3);
        ObjEditor2.apply();
    }

    public int getTurno4() {
        SharedPreferences Preferencias= application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("turno4",21));
    }
    public void setTurno4(int turno4){
        SharedPreferences Preferencias2= application.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=Preferencias2.edit();
        ObjEditor2.putInt("turno4",turno4);
        ObjEditor2.apply();
    }
    public int getPasoActual() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("PasoActual", 0));
    }
    public void setPasoActual(int modoLote) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("PasoActual", modoLote);
        ObjEditor2.apply();
    }
    public int getEstado() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("Estado", 0));
    }
    public void setEstado(int modoLote) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("Estado", modoLote);
        ObjEditor2.apply();
    }
    public int getCantidad() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("Cantidad", 1));
    }
    public void setCantidad(int modoLote) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("Cantidad", modoLote);
        ObjEditor2.apply();
    }
    public int getRealizadas() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("Realizadas", 0));
    }
    public void setRealizadas(int modoLote) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("Realizadas", modoLote);
        ObjEditor2.apply();
    }
    public String getNetototal() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return Preferencias.getString("Netototal", "0");
    }

    public void setNetototal(String Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("Netototal", Dato);
        ObjEditor2.apply();
    }

    public String getPorcentajeReceta() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("PorcentajeReceta", "0"));
    }

    public void setPorcentajeReceta(String Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("PorcentajeReceta", Dato);
        ObjEditor2.apply();
    }

    public int getModoLote() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("ModoLote", 0));
    }

    public void setModoLote(int modoLote) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("ModoLote", modoLote);
        ObjEditor2.apply();
    }

    public int getModoVencimiento() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("ModoVencimiento", 0));
    }

    public void setModoVencimiento(int modoVencimiento) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("ModoVencimiento", modoVencimiento);
        ObjEditor2.apply();
    }

    public String getLote() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Lote", ""));
    }
    public void setLote(String Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("Lote", Dato);
        ObjEditor2.apply();
    }
    public int getResetLote() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("ResetLote", 0));
    }
    public void setResetLote(int Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("ResetLote", Dato);
        ObjEditor2.apply();
    }
    public int getResetVencimiento() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("ResetVencimiento", 0));
    }
    public void setResetVencimiento(int Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("ResetVencimiento", Dato);
        ObjEditor2.apply();
    }
    public int getResetCampo1() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("ResetCampo1", 0));
    }
    public void setResetCampo1(int Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("ResetCampo1", Dato);
        ObjEditor2.apply();
    }
    public int getResetCampo2() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("ResetCampo2", 0));
    }
    public void setResetCampo2(int Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("ResetCampo2", Dato);
        ObjEditor2.apply();
    }
    public int getResetCampo3() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("ResetCampo3", 0));
    }
    public void setResetCampo3(int Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("ResetCampo3", Dato);
        ObjEditor2.apply();
    }
    public int getResetCampo4() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("ResetCampo4", 0));
    }
    public void setResetCampo4(int Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("ResetCampo4", Dato);
        ObjEditor2.apply();
    }
    public int getResetCampo5() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("ResetCampo5", 0));
    }
    public void setResetCampo5(int Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("ResetCampo5", Dato);
        ObjEditor2.apply();
    }
    public String getCampo1Valor() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Campo1Valor", ""));
    }
    public void setCampo1Valor(String Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("Campo1Valor", Dato);
        ObjEditor2.apply();
    }
    public String getCampo2Valor() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Campo2Valor", ""));
    }
    public void setCampo2Valor(String Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("Campo2Valor", Dato);
        ObjEditor2.apply();
    }
    public String getCampo3Valor() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Campo3Valor", ""));
    }
    public void setCampo3Valor(String Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("Campo3Valor", Dato);
        ObjEditor2.apply();
    }
    public String getCampo4Valor() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Campo4Valor", ""));
    }
    public void setCampo4Valor(String Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("Campo4Valor", Dato);
        ObjEditor2.apply();
    }
    public String getCampo5Valor() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Campo5Valor", ""));
    }
    public void setCampo5Valor(String Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("Campo5Valor", Dato);
        ObjEditor2.apply();
    }
    public String getVencimiento() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Vencimiento", ""));
    }
    public void setVencimiento(String Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("Vencimiento", Dato);
        ObjEditor2.apply();
    }
    public int getLoteAutomatico() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getInt("LoteAutomatico", 1));
    }
    public void setLoteAutomatico(int Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("LoteAutomatico", Dato);
        ObjEditor2.apply();
    }



    public Boolean getEjecutando() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return Preferencias.getBoolean("Ejecutando", false);
    }

    public void setEjecutando(Boolean Ejecutando) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putBoolean("Ejecutando", Ejecutando);
        ObjEditor2.apply();
    }


    public Boolean getRecetacomopedido() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return Preferencias.getBoolean("Recetacomopedido", false);
    }

    public void setRecetacomopedido(Boolean Recetacomopedido) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putBoolean("Recetacomopedido",Recetacomopedido);
        ObjEditor2.apply();
    }


    public Boolean getRecipientexPaso() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return Preferencias.getBoolean("RecipientexPaso", false);
    }

    public void setRecipientexPaso(Boolean RecipientexPaso) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putBoolean("RecipientexPaso", RecipientexPaso);
        ObjEditor2.apply();
    }

    public Boolean getContinuarFueraRango() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return Preferencias.getBoolean("ContinuarFueraRango", false);
    }

    public void setContinuarFueraRango(Boolean ContinuarFueraRango) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putBoolean("ContinuarFueraRango", ContinuarFueraRango);
        ObjEditor2.apply();
    }

    public Boolean getEtiquetaxPaso() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return Preferencias.getBoolean("EtiquetaxPaso", false);
    }

    public void setEtiquetaxPaso(Boolean EtiquetaxPaso) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putBoolean("EtiquetaxPaso", EtiquetaxPaso);
        ObjEditor2.apply();
    }


    public String getRecetaactual() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Recetaactual", ""));
    }

    public void setRecetaactual(String Tolerancia) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("Recetaactual", Tolerancia);
        ObjEditor2.apply();
    }

    public String getNombreRecetaactual() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("NombreRecetaactual", ""));
    }

    public void setNombreRecetaactual(String Tolerancia) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("NombreRecetaactual", Tolerancia);
        ObjEditor2.apply();
    }

    public String getCodigoRecetaactual() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("CodigoRecetaactual", ""));
    }

    public void setCodigoRecetaactual(String Tolerancia) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("CodigoRecetaactual", Tolerancia);
        ObjEditor2.apply();
    }
    public String getTolerancia() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Tolerancia", "5"));
    }

    public void setTolerancia(String Tolerancia) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("Tolerancia", Tolerancia);
        ObjEditor2.apply();
    }

    public String getBza1Limite() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Bza1Limite", "1"));
    }

    public void setBza1Limite(String Bza1Limite) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("Bza1Limite", Bza1Limite);
        ObjEditor2.apply();
    }

    public String getBza2Limite() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Bza2Limite", "5"));
    }

    public void setBza2Limite(String Bza2Limite) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("Bza2Limite", Bza2Limite);
        ObjEditor2.apply();
    }

    public String getBza3Limite() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Bza3Limite", "10"));
    }

    public void setBza3Limite(String Bza3Limite) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("Bza3Limite", Bza3Limite);
        ObjEditor2.apply();
    }

    public Boolean getReset1() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return Preferencias.getBoolean("reset1", false);
    }

    public void setReset1(Boolean Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putBoolean("reset1", Dato);
        ObjEditor2.apply();
    }

    public Boolean getReset2() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return Preferencias.getBoolean("reset2", false);
    }

    public void setReset2(Boolean Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putBoolean("reset2", Dato);
        ObjEditor2.apply();
    }

    public Boolean getReset3() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return Preferencias.getBoolean("reset3", false);
    }

    public Boolean getReset4() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return Preferencias.getBoolean("reset4", false);
    }

    public void setReset4(Boolean Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putBoolean("reset4", Dato);
        ObjEditor2.apply();
    }

    public void setReset3(Boolean Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putBoolean("reset3", Dato);
        ObjEditor2.apply();
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

    public void setCampo1(String campo) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("campo1", campo);
        ObjEditor2.apply();
    }

    public void setCampo2(String campo) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("campo2", campo);
        ObjEditor2.apply();
    }

    public void setCampo3(String campo) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("campo3", campo);
        ObjEditor2.apply();
    }

    public void setCampo4(String campo) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("campo4", campo);
        ObjEditor2.apply();
    }

    public void setCampo5(String campo) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("campo5", campo);
        ObjEditor2.apply();
    }

    public String getUltimapesada() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString("Ultimapesada", ""));
    }

    public void setUltimapesada(String Dato) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putString("Ultimapesada", Dato);
        ObjEditor2.apply();
    }
    public Boolean getRecetacomopedidoCheckbox() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return Preferencias.getBoolean("RecetacomopedidoCheckbox", false);
    }

    public void setRecetacomopedidoCheckbox(Boolean Recetacomopedido) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putBoolean("RecetacomopedidoCheckbox",Recetacomopedido);
        ObjEditor2.apply();
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


    public void saveListSpinner(List<Integer> stringList, String etiqueta) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(stringList);
        editor.putString(etiqueta, json);
        editor.apply();
    }

    public List<Integer> getListSpinner(String etiqueta) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(etiqueta, null);
        Type type = new TypeToken<List<Integer>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public String getSeparador(String etiqueta,int posicion) {
        SharedPreferences Preferencias= application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return (Preferencias.getString(etiqueta+"_concat_separador_"+posicion,","));
    }
    public void setSeparador(String separador,String etiqueta,int posicion){
        SharedPreferences Preferencias2= application.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2=Preferencias2.edit();
        ObjEditor2.putString(etiqueta+"_concat_separador_"+posicion,separador);
        ObjEditor2.apply();
    }

    public void saveListConcat( List<Integer> intList, String etiqueta,int posicion) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(intList);
        editor.putString(etiqueta+"_concat_"+posicion, json);
        editor.apply();
    }

    public List<Integer> getListConcat(String etiqueta,int posicion) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(etiqueta+"_concat_"+posicion, null);
        Type type = new TypeToken<List<Integer>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public void saveListFijo( List<String> stringList, String etiqueta) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(stringList);
        editor.putString(etiqueta+"_fijo", json);
        editor.apply();
    }

    public List<String> getListFijo(String etiqueta) {
        SharedPreferences sharedPreferences = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString(etiqueta+"_fijo", null);
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public Boolean getAutomatico() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return Preferencias.getBoolean("Automatico", false);
    }

    public void setAutomatico(Boolean Ejecutando) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putBoolean("Automatico", Ejecutando);
        ObjEditor2.apply();
    }


    public int getIndice() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return Preferencias.getInt("Indice", 0);
    }

    public void setIndice(int indice) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("Indice", indice);
        ObjEditor2.apply();
    }

    public int getSalida() {
        SharedPreferences Preferencias = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        return Preferencias.getInt("Salida", 0);
    }

    public void setSalida(int indice) {
        SharedPreferences Preferencias2 = application.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putInt("Salida", indice);
        ObjEditor2.apply();
    }
}
