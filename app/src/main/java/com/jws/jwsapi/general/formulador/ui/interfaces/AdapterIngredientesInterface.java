package com.jws.jwsapi.general.formulador.ui.interfaces;

import com.jws.jwsapi.general.formulador.models.FormModelIngredientes;
import java.util.List;

public interface AdapterIngredientesInterface {
    void eliminarIngrediente(List<FormModelIngredientes> mData, int posicion);
    void imprimirEtiqueta(String codigo, String nombre);
}
