package com.service.Balanzas.Interfaz;

import java.util.ArrayList;

public class conjuntobalanza {
        private int Salida;
        private int TipoBza;
        private ArrayList<String> Direccion;

    public int getSalida() {
        return Salida;
    }
    public void setSalida(int salida) {
        Salida = salida;
    }
    public int getTipoBza() {
        return TipoBza;
    }
    public void setTipoBza(int Tipo) {
        TipoBza = Tipo;
    }

    public void setDireccion(ArrayList<String> direccion) {
        Direccion = direccion;
    }

    public ArrayList<String> getDireccion() {
        return Direccion;
    }


}
