package com.jws.jwsapi.core.alarms;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.ToastHelper;

public class Alarm<T> {
    private T value;
    private final MainActivity mainActivity;
    private final Context context;
    Boolean run = true;
    public String name = "nueva alarma";
    public String errorName = "alarma activada";
    public Boolean mensaje = false;
    public Boolean estado = false;
    private Boolean alarmaActiva = false;
    int id = 0;
    private int tipoalarma = 0;
    Runnable runnable;


    @FunctionalInterface
    public interface Condicion {
        boolean check(Number variable);
    }

    public Alarm(int id, MainActivity mainActivity, Context context) {
        this.mainActivity = mainActivity;
        this.context = context;
        this.id = id;
    }

    /**
     * public ObjetoAlarma ALARMA_BAJO= new ObjetoAlarma();
     * public Alarma<Integer> alarma1;
     * alarma1= new Alarma<>(alarmas.size(),mainActivity,context);
     * alarma1.errorName="Error de alarma, alcanzo el valor 5";
     * alarma1.name="Alarma 1";
     * alarma1.mensaje=true;
     * ALARMA_BAJO.var_number=0;
     * alarma1.startNumeric(ALARMA_BAJO,(num) -> num.intValue() >= 5);
     *
     * @param alarma
     * @param condicion
     */

    public void startNumeric(AlarmObject alarma, Condicion condicion) {
        tipoalarma = 0;
        estado = getEstado();
        Handler handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (condicion.check(alarma.var_number)) {
                    alarmaActiva = true;
                    if (!estado) {
                        activarAlarma();
                    }
                } else {
                    alarmaActiva = false;
                }
                if (run) {
                    handler.postDelayed(this, 200);
                }

            }
        };

        handler.post(runnable);
    }

    /**
     * public ObjetoAlarma ALARMA_CODE= new ObjetoAlarma();
     * public Alarma<String> alarma2;
     * alarma2= new Alarma<>(alarmas.size(),mainActivity,context);
     * alarma2.errorName="Error de alarma str, alcanzo el valor";
     * alarma2.name="Alarma 2";
     * alarma2.mensaje=true;
     * ALARMA_CODE.var="";
     * alarma2.start(ALARMA_CODE,"401");
     *
     * @param alarma
     * @param value
     */

    public void start(AlarmObject alarma, T value) {
        tipoalarma = 1;
        this.value = value;
        estado = getEstado();
        Handler handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (alarma.var == value) {
                    alarmaActiva = true;
                    if (!estado) {
                        activarAlarma();
                    }
                } else {
                    alarmaActiva = false;
                }
                if (run) {
                    handler.postDelayed(this, 200);
                }

            }
        };

        handler.post(runnable);
    }


    public void stop() {
        run = false;
    }

    private void activarAlarma() {
        if (mensaje) {
            ToastHelper.message(errorName, R.layout.item_customtoasterror, mainActivity);
        }
        setEstado(true);
    }


    private Boolean getEstado() {
        SharedPreferences Preferencias = context.getSharedPreferences("ALARMAS", Context.MODE_PRIVATE);
        return Preferencias.getBoolean(name, false);
    }

    public void apagarAlarma() {
        if (!alarmaActiva) {
            setEstado(false);
        } else {
            ToastHelper.message("No puede desactivar la alarma porque continua en error, solucione el error para desactivarla", R.layout.item_customtoasterror, mainActivity);
        }

    }

    private void setEstado(Boolean value) {
        estado = value;
        SharedPreferences Preferencias2 = context.getSharedPreferences("ALARMAS", Context.MODE_PRIVATE);
        SharedPreferences.Editor ObjEditor2 = Preferencias2.edit();
        ObjEditor2.putBoolean(name, value);
        ObjEditor2.apply();
    }

}
