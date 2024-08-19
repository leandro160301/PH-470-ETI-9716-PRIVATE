package com.jws.jwsapi.feature.formulador.ui.interfaces;

import com.jws.jwsapi.feature.formulador.models.FormModelIngredientes;
import java.util.List;

public interface AdapterIngredientesInterface {
    void eliminarIngrediente(List<FormModelIngredientes> mData, int posicion);
    void imprimirEtiqueta(String codigo, String nombre);
}
