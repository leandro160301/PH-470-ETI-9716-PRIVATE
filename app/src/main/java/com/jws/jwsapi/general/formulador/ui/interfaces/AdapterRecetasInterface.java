package com.jws.jwsapi.general.formulador.ui.interfaces;

import com.jws.jwsapi.general.formulador.models.FormModelReceta;
import java.util.List;

public interface AdapterRecetasInterface {
    void eliminarPaso(List<FormModelReceta> mData, int posicion);
    void agregarPaso(List<FormModelReceta> mData, int posicion);
    void modificarPaso(List<FormModelReceta> mData, int posicion);
}
