package com.jws.jwsapi.feature.formulador.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jws.jwsapi.feature.formulador.MainFormClass;
import com.jws.jwsapi.feature.formulador.data.sql.FormSqlHelper;
import com.jws.jwsapi.feature.formulador.models.FormModelPesadasDB;
import com.jws.jwsapi.feature.formulador.models.FormModelRecetaDB;
import java.util.List;

public class FormFragmentGuardadosViewModel extends ViewModel {
    private final MutableLiveData<List<FormModelPesadasDB>> pesadasLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<FormModelRecetaDB>> pedidosLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<FormModelRecetaDB>> recetasLiveData = new MutableLiveData<>();

    public LiveData<List<FormModelPesadasDB>> getPesadas() {
        return pesadasLiveData;
    }

    public LiveData<List<FormModelRecetaDB>> getRecetas() {
        return recetasLiveData;
    }

    public LiveData<List<FormModelRecetaDB>> getPedidos() {
        return pedidosLiveData;
    }

    public void cargarPesadas(Context context) {
        try (FormSqlHelper guardadosSQL = new FormSqlHelper(context, MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
            pesadasLiveData.setValue(guardadosSQL.getPesadasSQL(null));
        }
    }

    public void cargarPedidos(Context context) {
        try (FormSqlHelper guardadosSQL = new FormSqlHelper(context, MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
            pedidosLiveData.setValue(guardadosSQL.getPedidosSQL(null));
        }
    }

    public void cargarRecetas(Context context) {
        try (FormSqlHelper guardadosSQL = new FormSqlHelper(context, MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
            recetasLiveData.setValue(guardadosSQL.getRecetasSQL(null));
        }
    }

    public void cargarPesadasConId(Context context, String id, Boolean receta) {
        try (FormSqlHelper guardadosSQL = new FormSqlHelper(context, MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
            pesadasLiveData.setValue(guardadosSQL.getPesadasSQLconId(id, receta));
        }
    }

    public void eliminarDatos(Context context, int menu) {
        try (FormSqlHelper productosSQL = new FormSqlHelper(context, MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
            switch (menu) {
                case 0:
                    productosSQL.eliminarPesadas();
                    cargarPesadas(context);
                    break;
                case 1:
                    productosSQL.eliminarRecetas();
                    cargarRecetas(context);
                    break;
                case 2:
                    productosSQL.eliminarPedidos();
                    cargarPedidos(context);
                    break;
                default:
                    throw new IllegalArgumentException("Menú no válido");
            }
        }
    }
}