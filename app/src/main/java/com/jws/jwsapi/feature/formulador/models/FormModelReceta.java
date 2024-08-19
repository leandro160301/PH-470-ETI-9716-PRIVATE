package com.jws.jwsapi.feature.formulador.models;

public class FormModelReceta {

    private String codigo;
    private String nombre;
    private String kilos_totales;
    private String codigo_ing;
    private String descrip_ing;
    private String kilos_ing;
    private String kilos_reales_ing;
    private String tolerancia_ing;

    // Constructor
    public FormModelReceta(String codigo,
                           String nombre,
                           String kilos_totales,
                           String codigo_ing,
                           String descrip_ing,
                           String kilos_ing,
                           String kilos_reales_ing,
                           String tolerancia_ing) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.kilos_totales = kilos_totales;
        this.codigo_ing = codigo_ing;
        this.descrip_ing = descrip_ing;
        this.kilos_ing = kilos_ing;
        this.kilos_reales_ing = kilos_reales_ing;
        this.tolerancia_ing = tolerancia_ing;
    }

    // Getters
    public String getCodigo() {
        return codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getKilos_totales() {
        return kilos_totales;
    }

    public String getCodigo_ing() {
        return codigo_ing;
    }

    public String getDescrip_ing() {
        return descrip_ing;
    }

    public String getKilos_ing() {
        return kilos_ing;
    }

    public String getKilos_reales_ing() {
        return kilos_reales_ing;
    }

    public String getTolerancia_ing() {
        return tolerancia_ing;
    }

    // Setters
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setKilos_totales(String kilos_totales) {
        this.kilos_totales = kilos_totales;
    }

    public void setCodigo_ing(String codigo_ing) {
        this.codigo_ing = codigo_ing;
    }

    public void setDescrip_ing(String descrip_ing) {
        this.descrip_ing = descrip_ing;
    }

    public void setKilos_ing(String kilos_ing) {
        this.kilos_ing = kilos_ing;
    }

    public void setKilos_reales_ing(String kilos_reales_ing) {
        this.kilos_reales_ing = kilos_reales_ing;
    }

    public void setTolerancia_ing(String tolerancia_ing) {
        this.tolerancia_ing = tolerancia_ing;
    }
}