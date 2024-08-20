package com.jws.jwsapi.feature.formulador.models;

public class FormModelIngredientes {

    private String codigo;
    private String nombre;
    private int salida;


    public FormModelIngredientes(String codigo,
                                 String nombre,
                                 int salida) {

        this.codigo=codigo;
        this.nombre=nombre;
        this.salida=salida;

    }
    // Getters
    public String getCodigo(){
        return codigo;
    }

    public String getNombre(){
        return nombre;
    }

    public int getSalida(){
        return salida;
    }
    // Setters
    public void setCodigo(String codigo){
        this.codigo=codigo;
    }

    public void setNombre(String nombre){
        this.nombre=nombre;
    }

    public void setSalida(int salida){
        this.salida=salida;
    }

}