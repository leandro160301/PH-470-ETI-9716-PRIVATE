package com.service.Balanzas.Interfaz;

import java.util.ArrayList;

public class serviceDevice {
        private String Salida;
        private int TipoDevice;
        private int NumID;
        private int Modelo;
        private ArrayList<String> Direccion;
        private int Numerobza;
        private Boolean Seteado;

    public int getNB() {
        return Numerobza;
    }
    public void setSeteo(Boolean set) {Seteado=set;}
    public Boolean getSeteo() {
        return Seteado;
    }
    public void setNB(int numeroBza) {Numerobza = numeroBza;}
    public int getID() {
        return NumID;
    }
    public void setID(int ID) {NumID = ID;}
    public String getSalida() {
        return Salida;
    }
    public void setSalida(String salida) {Salida = salida;}
    public int getModelo() {
        return Modelo;
    }
    public void setModelo(int modelo) {Modelo = modelo;}
    public int getTipo() {
        return TipoDevice;
    }
    public void setTipo(int Tipo) {
        TipoDevice = Tipo;
    }
    public void setDireccion(ArrayList<String> direccion) {
        Direccion = direccion;
    }
    public ArrayList<String> getDireccion() {
        return Direccion;
    }
    public void setDevice(serviceDevice Device){
        NumID=Device.getID();
        Salida= Device.getSalida();
        Modelo = Device.getModelo();
        TipoDevice=Device.getTipo();
        Direccion = Device.getDireccion();
        Seteado= Device.getSeteo();
        Numerobza=Device.getNB();

    }

}
