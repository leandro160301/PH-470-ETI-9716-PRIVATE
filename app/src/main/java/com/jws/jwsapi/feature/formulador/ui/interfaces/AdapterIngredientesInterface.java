package com.jws.jwsapi.feature.formulador.ui.interfaces;

import com.jws.jwsapi.feature.formulador.models.Form_Model_Ingredientes;
import java.util.List;

public interface AdapterIngredientesInterface {
    void eliminarIngrediente(List<Form_Model_Ingredientes> mData,int posicion);
    void imprimirEtiqueta(String codigo, String nombre);
}
