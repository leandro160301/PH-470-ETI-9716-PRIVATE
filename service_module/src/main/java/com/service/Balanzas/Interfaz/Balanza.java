package com.service.Balanzas.Interfaz;

public interface Balanza {
    interface Struct{
        float getNeto(int numero);
        String getNetoStr(int numero);
        float getBruto(int numero);
        String getBrutoStr(int numero);
        float getTara(int numero);
        String getTaraStr(int numero);
        void setTara(int numero);
        void setCero(int numero);
        void setTaraDigital(int numero, float TaraDigital);
        String getTaraDigital(int numero);
        Boolean getBandaCero(int numero);
        void setBandaCero(int numero, Boolean bandaCeroi);
        float getBandaCeroValue(int numero);
        void setBandaCeroValue(int numero, float bandaCeroValue);
        Boolean getEstable(int numero);
        String format(int numero, String peso);
        String getUnidad(int numero);
        String getPicoStr(int numero);
        float getPico(int numero);
        void init(int numero);
        void stop(int numero);
        void start(int numero);
        void openCalibracion(int numero);
        Boolean getSobrecarga(int numero);
        String getEstado(int numero);
        void setEstado(int numero,String estado);
        void Itw410FrmSetear(int numero,String setPoint, int Salida);
        String Itw410FrmGetSetPoint(int numero);
        int Itw410FrmGetSalida(int numero);
        void Itw410FrmStart(int numero);
        int Itw410FrmGetEstado(int numero);
        void Itw410FrmSetEstado(int numero,int estado);
        String Itw410FrmGetUltimoPeso(int numero);
        int Itw410FrmGetUltimoIndice(int numero);


    }

}
