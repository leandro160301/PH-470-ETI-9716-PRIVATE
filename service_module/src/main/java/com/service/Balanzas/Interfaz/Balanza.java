package com.service.Balanzas.Interfaz;

import java.util.concurrent.CountDownLatch;

public interface Balanza {
    interface Struct{
        void setID(int numID,int numBza);
        int getID(int numBza);
        float getNeto(int numBza);
        String getNetoStr(int numBza);
        float getBruto(int numBza);
        String getBrutoStr(int numBza);
        float getTara(int numBza);
        String getTaraStr(int numBza);
        void setTara(int numBza);
        void setCero(int numBza);
        void setTaraDigital(int numBza, float TaraDigital);
        String getTaraDigital(int numBza);
        Boolean getBandaCero(int numBza);
        void setBandaCero(int numBza, Boolean bandaCeroi);
        float getBandaCeroValue(int numBza);
        void setBandaCeroValue(int numBza, float bandaCeroValue);
        Boolean getEstable(int numBza);
        String format(int numero, String peso);
        String getUnidad(int numBza);
        String getPicoStr(int numBza);
        float getPico(int numBza);
        void init(int numBza);
        void escribir(String msj,int numBza);
        void stop(int numBza);
        void start(int numBza);
        Boolean calibracionHabilitada(int numBza);
        void openCalibracion(int numBza);
        Boolean getSobrecarga(int numBza);

        Boolean getEstadoCentroCero(int numBza);

        Boolean getEstadoSobrecarga(int numBza);

        Boolean getEstadoNeto(int numBza);

        Boolean getEstadoPesoNeg(int numBza);

        Boolean getEstadoBajoCero(int numBza);

        Boolean getEstadoBzaEnCero(int numBza);

        Boolean getEstadoBajaBateria(int numBza);

        String getFiltro1(int numBza);
        String getFiltro2(int numBza);
        String getFiltro3(int numBza);
        String getFiltro4(int numBza);

        Boolean getEstadoEstable(int numBza);
        String getEstado(int numBza);
        void setEstado(int numBza, String estado);
        void onEvent();
        Boolean Itw410FrmSetear(int numero,String setPoint, int Salida);//void Itw410FrmSetear(int numero,String setPoint, int Salida);
        String Itw410FrmGetSetPoint(int numero);
        int Itw410FrmGetSalida(int numero);
        void Itw410FrmStart(int numero);
        int Itw410FrmGetEstado(int numero);
        String Itw410FrmGetUltimoPeso(int numero);
        int Itw410FrmGetUltimoIndice(int numero);
        void itw410FrmPause(int numero);
        void itw410FrmStop(int numero);
        void Itw410FrmSetTiempoEstabilizacion(int numero, int Tiempo);
        }

}
