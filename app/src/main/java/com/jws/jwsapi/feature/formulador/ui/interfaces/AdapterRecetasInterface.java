package com.jws.jwsapi.feature.formulador.ui.interfaces;

import com.jws.jwsapi.feature.formulador.models.FormModelReceta;
import java.util.List;

public interface AdapterRecetasInterface {
    void eliminarPaso(List<FormModelReceta> mData, int posicion);
    void agregarPaso(List<FormModelReceta> mData, int posicion);
    void modificarPaso(List<FormModelReceta> mData, int posicion);
}
