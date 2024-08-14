package com.jws.jwsapi.base.models;

public class StatsModel {

    public String  Tolerancia;
    public String   Neto;
    public String  Bruto;
    public String  Tara;
    public String  TaraDigital;
    public String Contenedor;
    public String  Transporte;
    public String  Destinatario;
    public String  Fecha;
    public String Hora;
    public String  Rango;
    public String  PesoTeorico;

    // Constructor
    public StatsModel(String tolerancia, String neto, String bruto, String tara, String taradigital, String contenedor, String transporte, String destinatario, String fecha, String hora,String rango, String pesoteorico ) {
        this.Tolerancia = tolerancia;
        this.Neto = neto;
        this.Bruto = bruto;
        this.Tara = tara;
        this.TaraDigital = taradigital;
        this.Contenedor = contenedor;
        this.Transporte = transporte;
        this.Destinatario = destinatario;
        this.Fecha = fecha;
        this.Hora = hora;
        this.Rango = rango;
        this.PesoTeorico = pesoteorico;
    }

}