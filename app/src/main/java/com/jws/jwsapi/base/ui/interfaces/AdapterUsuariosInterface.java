package com.jws.jwsapi.base.ui.interfaces;

import com.jws.jwsapi.base.models.UsuariosModel;
import java.util.List;

public interface AdapterUsuariosInterface {
    void eliminarUsuario(List<UsuariosModel>  mData, int posicion);
}
