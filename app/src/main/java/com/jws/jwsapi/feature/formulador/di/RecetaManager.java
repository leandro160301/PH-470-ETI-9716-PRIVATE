package com.jws.jwsapi.feature.formulador.di;

import androidx.lifecycle.MutableLiveData;
import com.jws.jwsapi.feature.formulador.models.FormModelReceta;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Singleton;

@Singleton
public class RecetaManager {
    public int estado = 0;
    public int estadoBalanza = 0;
    public Float setPoint=0f;
    public static int DETENIDO=0;
    public static int PROCESO=1;
    public static int PAUSA=2;
    public static int ESTABILIZANDO=3;
    public static int BUENO=1;
    public static int BAJO=0;
    public static int ALTO=2;
    //estado 1=que pida recipiente en los pasos
    //estado 2=que pese el paso

    public String lote = "";
    public String codigoReceta = "";
    public String nombreReceta = "";
    public String recetaActual = "";
    public Boolean ejecutando = false;
    public Boolean automatico = false;
    public Boolean recetaComoPedido =false;
    public List<FormModelReceta> listRecetaActual = new ArrayList<>();
    public int pasoActual = 0;
    public MutableLiveData<Integer> cantidad = new MutableLiveData<>(0);
    public MutableLiveData<Integer> realizadas = new MutableLiveData<>(0);
    public MutableLiveData<String> netoTotal= new MutableLiveData<>("0");
    public MutableLiveData<String> estadoMensajeStr= new MutableLiveData<>("");
    public MutableLiveData<Integer> progreso = new MutableLiveData<>(0);
    public String porcentajeReceta= "0";
}
