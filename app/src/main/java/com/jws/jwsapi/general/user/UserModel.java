package com.jws.jwsapi.general.user;

public class UserModel {

    public int id;
    public String nombre;
    public String usuario;
    public String password;
    public String codigo;
    public String tipo;

    public UserModel(int id,
                     String nombre,
                     String usuario,
                     String password,
                     String codigo,
                     String tipo) {

        this.id=id;
        this.nombre=nombre;
        this.password=password;
        this.usuario=usuario;
        this.codigo=codigo;
        this.tipo=tipo;

    }




}