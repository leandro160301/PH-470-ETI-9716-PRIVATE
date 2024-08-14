package com.jws.jwsapi.feature.formulador.models;

public class Form_Model_RecetaDB {

    private int id;
    private String codigoReceta;
    private String descripcionReceta;
    private String lote;
    private String vencimiento;
    private String turno;
    private String neto;
    private String bruto;
    private String tara;
    private String fecha;
    private String hora;
    private String campo1;
    private String campo2;
    private String campo3;
    private String campo4;
    private String campo5;
    private String campo1Valor;
    private String campo2Valor;
    private String campo3Valor;
    private String campo4Valor;
    private String campo5Valor;
    private String operador;
    private String kilosAProducir;
    private String campoExtra1;
    private String campoExtra2;
    private String balanza;

    // Constructor con todos los campos
    public Form_Model_RecetaDB(int id, String codigoReceta, String descripcionReceta, String lote, String vencimiento, String turno, String neto, String bruto, String tara, String fecha, String hora, String campo1, String campo2, String campo3, String campo4, String campo5, String campo1Valor, String campo2Valor, String campo3Valor, String campo4Valor, String campo5Valor, String operador, String kilosAProducir, String campoExtra1, String campoExtra2, String balanza) {
        this.id = id;
        this.codigoReceta = codigoReceta;
        this.descripcionReceta = descripcionReceta;
        this.lote = lote;
        this.vencimiento = vencimiento;
        this.turno = turno;
        this.neto = neto;
        this.bruto = bruto;
        this.tara = tara;
        this.fecha = fecha;
        this.hora = hora;
        this.campo1 = campo1;
        this.campo2 = campo2;
        this.campo3 = campo3;
        this.campo4 = campo4;
        this.campo5 = campo5;
        this.campo1Valor = campo1Valor;
        this.campo2Valor = campo2Valor;
        this.campo3Valor = campo3Valor;
        this.campo4Valor = campo4Valor;
        this.campo5Valor = campo5Valor;
        this.operador = operador;
        this.kilosAProducir = kilosAProducir;
        this.campoExtra1 = campoExtra1;
        this.campoExtra2 = campoExtra2;
        this.balanza = balanza;
    }

    // Getters
    public int getId() {
        return id;
    }

    public String getCodigoReceta() {
        return codigoReceta;
    }

    public String getDescripcionReceta() {
        return descripcionReceta;
    }

    public String getLote() {
        return lote;
    }

    public String getVencimiento() {
        return vencimiento;
    }

    public String getTurno() {
        return turno;
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

    public String getCampo1() {
        return campo1;
    }

    public String getCampo2() {
        return campo2;
    }

    public String getCampo3() {
        return campo3;
    }

    public String getCampo4() {
        return campo4;
    }

    public String getCampo5() {
        return campo5;
    }

    public String getCampo1Valor() {
        return campo1Valor;
    }

    public String getCampo2Valor() {
        return campo2Valor;
    }

    public String getCampo3Valor() {
        return campo3Valor;
    }

    public String getCampo4Valor() {
        return campo4Valor;
    }

    public String getCampo5Valor() {
        return campo5Valor;
    }

    public String getOperador() {
        return operador;
    }

    public String getKilosAProducir() {
        return kilosAProducir;
    }

    public String getCampoExtra1() {
        return campoExtra1;
    }

    public String getCampoExtra2() {
        return campoExtra2;
    }

    public String getBalanza() {
        return balanza;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setCodigoReceta(String codigoReceta) {
        this.codigoReceta = codigoReceta;
    }

    public void setDescripcionReceta(String descripcionReceta) {
        this.descripcionReceta = descripcionReceta;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }

    public void setVencimiento(String vencimiento) {
        this.vencimiento = vencimiento;
    }

    public void setTurno(String turno) {
        this.turno = turno;
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

    public void setCampo1(String campo1) {
        this.campo1 = campo1;
    }

    public void setCampo2(String campo2) {
        this.campo2 = campo2;
    }

    public void setCampo3(String campo3) {
        this.campo3 = campo3;
    }

    public void setCampo4(String campo4) {
        this.campo4 = campo4;
    }

    public void setCampo5(String campo5) {
        this.campo5 = campo5;
    }

    public void setCampo1Valor(String campo1Valor) {
        this.campo1Valor = campo1Valor;
    }

    public void setCampo2Valor(String campo2Valor) {
        this.campo2Valor = campo2Valor;
    }

    public void setCampo3Valor(String campo3Valor) {
        this.campo3Valor = campo3Valor;
    }

    public void setCampo4Valor(String campo4Valor) {
        this.campo4Valor = campo4Valor;
    }

    public void setCampo5Valor(String campo5Valor) {
        this.campo5Valor = campo5Valor;
    }

    public void setOperador(String operador) {
        this.operador = operador;
    }

    public void setKilosAProducir(String kilosAProducir) {
        this.kilosAProducir = kilosAProducir;
    }

    public void setCampoExtra1(String campoExtra1) {
        this.campoExtra1 = campoExtra1;
    }

    public void setCampoExtra2(String campoExtra2) {
        this.campoExtra2 = campoExtra2;
    }

    public void setBalanza(String balanza) {
        this.balanza = balanza;
    }
}