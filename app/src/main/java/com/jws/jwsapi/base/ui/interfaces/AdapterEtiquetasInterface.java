package com.jws.jwsapi.base.ui.interfaces;

import com.jws.jwsapi.feature.formulador.models.Form_Model_Receta;

import java.util.List;

public interface AdapterEtiquetasInterface {
    void eliminarPaso(List<Form_Model_Receta> mData, int posicion);
    void agregarPaso(List<Form_Model_Receta> mData, int posicion);
    void modificarPaso(List<Form_Model_Receta> mData, int posicion);
}
