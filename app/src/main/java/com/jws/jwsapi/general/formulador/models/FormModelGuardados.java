package com.jws.jwsapi.general.formulador.models;

public class FormModelGuardados {

    private int id;
    private String producto;
    private String empresa;
    private String dimensiones;
    private String lote;
    private String neto;
    private String bruto;
    private String tara;
    private String fecha;
    private String hora;


    public FormModelGuardados(int id,
                              String producto,
                              String empresa,
                              String dimensiones,
                              String lote,
                              String neto,
                              String bruto,
                              String tara,
                              String fecha,
                              String hora) {

        this.id=id;
        this.lote=lote;
        this.producto=producto;
        this.neto=neto;
        this.bruto=bruto;
        this.tara=tara;
        this.fecha=fecha;
        this.hora=hora;
        this.empresa=empresa;
        this.dimensiones =dimensiones;

    }

    // Getters
    public int getId() {
        return id;
    }

    public String getProducto() {
        return producto;
    }

    public String getEmpresa() {
        return empresa;
    }

    public String getDimensiones() {
        return dimensiones;
    }

    public String getLote() {
        return lote;
    }

    public String getNeto() {
        return neto;
    }

    public String getBruto() {
        return bruto;
    }

    public String getTara() {
        return tara;
    }

    public String getFecha() {
        return fecha;
    }

    public String getHora() {
        return hora;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setProducto(String producto) {
        this.producto = producto;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public void setDimensiones(String dimensiones) {
        this.dimensiones = dimensiones;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public void setNeto(String neto) {
        this.neto = neto;
    }

    public void setBruto(String bruto) {
        this.bruto = bruto;
    }

    public void setTara(String tara) {
        this.tara = tara;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }




}