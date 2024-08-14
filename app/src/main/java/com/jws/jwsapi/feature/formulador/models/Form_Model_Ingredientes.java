package com.jws.jwsapi.feature.formulador.models;

public class Form_Model_Ingredientes {

    private String codigo;
    private String nombre;


    public Form_Model_Ingredientes(String codigo,
                                   String nombre) {

        this.codigo=codigo;
        this.nombre=nombre;

    }
    // Getters
    public String getCodigo(){
        return codigo;
    }

    public String getNombre(){
        return nombre;
    }
    // Setters
    public void setCodigo(String codigo){
        this.codigo=codigo;
    }

    public void setNombre(String nombre){
        this.nombre=nombre;
    }

}