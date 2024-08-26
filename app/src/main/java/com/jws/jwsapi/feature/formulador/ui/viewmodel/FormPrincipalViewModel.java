package com.jws.jwsapi.feature.formulador.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jws.jwsapi.feature.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.feature.formulador.di.RecetaManager;
import java.util.ArrayList;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FormPrincipalViewModel extends ViewModel {
    private final RecetaManager recetaManager;
    private final PreferencesManager preferencesManager;
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();
    public LiveData<String> mensajeToastError = mensajeError;

    @Inject
    public FormPrincipalViewModel(RecetaManager recetaManager, PreferencesManager preferencesManager) {
        this.recetaManager = recetaManager;
        this.preferencesManager = preferencesManager;
        initializeRecetaManager();
    }

    private void initializeRecetaManager() {
        this.recetaManager.pasoActual=preferencesManager.getPasoActual();
        this.recetaManager.estado = preferencesManager.getEstado();
        this.recetaManager.cantidad.setValue(preferencesManager.getCantidad());
        this.recetaManager.realizadas.setValue(preferencesManager.getRealizadas());
        this.recetaManager.netoTotal.setValue(preferencesManager.getNetototal());
        this.recetaManager.recetaActual =preferencesManager.getRecetaactual();
        this.recetaManager.codigoReceta =preferencesManager.getCodigoRecetaactual();
        this.recetaManager.nombreReceta =preferencesManager.getNombreRecetaactual();
        this.recetaManager.listRecetaActual = preferencesManager.getPasosRecetaActual();
        this.recetaManager.ejecutando=preferencesManager.getEjecutando();
        this.recetaManager.automatico=preferencesManager.getAutomatico();
        this.recetaManager.recetaComoPedido =preferencesManager.getRecetacomopedido();
        this.recetaManager.porcentajeReceta=preferencesManager.getPorcentajeReceta();
        preferencesManager.setIndice(0);
        if(this.recetaManager.listRecetaActual ==null){
            this.recetaManager.listRecetaActual =new ArrayList<>();
        }
    }
    public void mostrarMensajeDeError(String mensaje) {
        mensajeError.setValue(mensaje);
    }
    public MutableLiveData<String> getNetoTotal(){return recetaManager.netoTotal;}
    public MutableLiveData<Integer> getCantidad(){return recetaManager.cantidad;}
    public MutableLiveData<Integer> getRealizadas(){return recetaManager.realizadas;}
    public MutableLiveData<Integer> getProgreso(){return recetaManager.progreso;}
    public MutableLiveData<String> getEstadoMensajeStr(){return recetaManager.estadoMensajeStr;}
    public MutableLiveData<String> getMensajeToastError(){return mensajeError;}


}