package com.jws.jwsapi.feature.formulador.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jws.jwsapi.feature.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.feature.formulador.di.LabelManager;
import com.jws.jwsapi.feature.formulador.di.RecetaManager;

import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FormPreferencesLabelViewModel extends ViewModel {
    private final PreferencesManager preferencesManager;
    private final LabelManager labelManager;
    private final RecetaManager recetaManager;
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();
    public LiveData<String> mensajeToastError = mensajeError;

    @Inject
    public FormPreferencesLabelViewModel(PreferencesManager preferencesManager, LabelManager labelManager, RecetaManager recetaManager) {
        this.preferencesManager = preferencesManager;
        this.labelManager = labelManager;
        this.recetaManager = recetaManager;
    }
    public void saveDataInMemory() {
        preferencesManager.setLote((String)labelManager.olote.value);
        preferencesManager.setVencimiento((String)labelManager.ovenci.value);
        preferencesManager.setCampo1Valor((String)labelManager.ocampo1.value);
        preferencesManager.setCampo2Valor((String)labelManager.ocampo2.value);
        preferencesManager.setCampo3Valor((String)labelManager.ocampo3.value);
        preferencesManager.setCampo4Valor((String)labelManager.ocampo4.value);
        preferencesManager.setCampo5Valor((String)labelManager.ocampo5.value);
    }

    public void restaurarDatos() {
        boolean aRealizarFinalizados=aRealizarFinalizados();
        restaurarLote(aRealizarFinalizados);
        restaurarVencimiento(aRealizarFinalizados);
        restaurarCampo(1,aRealizarFinalizados);
        restaurarCampo(2,aRealizarFinalizados);
        restaurarCampo(3,aRealizarFinalizados);
        restaurarCampo(4,aRealizarFinalizados);
        restaurarCampo(5,aRealizarFinalizados);
    }

    private void restaurarCampo(int campo, boolean arealizarfinalizados) {
        int resetValor = getResetCampo(campo);
        if (resetValor == 1 && arealizarfinalizados || resetValor == 2) {
            setCampoValor(campo);
            setOcampoValue(campo);
        }
    }
    private int getResetCampo(int campo) {
        switch (campo) {
            case 1:
                return preferencesManager.getResetCampo1();
            case 2:
                return preferencesManager.getResetCampo2();
            case 3:
                return preferencesManager.getResetCampo3();
            case 4:
                return preferencesManager.getResetCampo4();
            case 5:
                return preferencesManager.getResetCampo5();
            default:
                return 0;
        }
    }

    private void setCampoValor(int campo) {
        switch (campo) {
            case 1:
                preferencesManager.setCampo1Valor("");
                break;
            case 2:
                preferencesManager.setCampo2Valor("");
                break;
            case 3:
                preferencesManager.setCampo3Valor("");
                break;
            case 4:
                preferencesManager.setCampo4Valor("");
                break;
            case 5:
                preferencesManager.setCampo5Valor("");
                break;
            default:
                break;
        }
    }

    public void setOcampoValue(int campo) {
        switch (campo) {
            case 1:
                labelManager.ocampo1.value = "";
                break;
            case 2:
                labelManager.ocampo2.value = "";
                break;
            case 3:
                labelManager.ocampo3.value = "";
                break;
            case 4:
                labelManager.ocampo4.value = "";
                break;
            case 5:
                labelManager.ocampo5.value = "";
                break;
            default:
                break;
        }
    }

    private void restaurarLote(boolean arealizarfinalizados) {
        if(preferencesManager.getResetLote()==1){
            if(arealizarfinalizados){
                preferencesManager.setLote("");
                labelManager.olote.value="";
            }
        }
        if(preferencesManager.getResetLote()==2){
            preferencesManager.setLote("");
            labelManager.olote.value="";
        }
    }

    private void restaurarVencimiento(boolean aRealizarFinalizados) {
        if(preferencesManager.getResetVencimiento()==1){
            if(aRealizarFinalizados){
                preferencesManager.setVencimiento("");
                labelManager.ovenci.value="";
            }
        }
        if(preferencesManager.getResetVencimiento()==2){
            preferencesManager.setVencimiento("");
            labelManager.ovenci.value="";
        }
    }

    public boolean isDataCompleted(boolean empezar) {
        if(labelManager.olote.value==""||labelManager.ovenci.value==""||faltanCampos()){
            mostrarMensajeDeError("Faltan ingresar datos");
            empezar =false;
        }
        return empezar;
    }

    private boolean faltanCampos() {
        String[] campos = {preferencesManager.getCampo1(), preferencesManager.getCampo2(), preferencesManager.getCampo3(), preferencesManager.getCampo4(), preferencesManager.getCampo5()};
        String[] valores = {
                (String)labelManager.ocampo1.value,
                (String)labelManager.ocampo2.value,
                (String)labelManager.ocampo3.value,
                (String)labelManager.ocampo4.value,
                (String)labelManager.ocampo5.value
        };
        for (int i = 0; i < campos.length; i++) {
            if (!campos[i].isEmpty() && valores[i].isEmpty()) return true;
        }
        return false;
    }


    private boolean aRealizarFinalizados() {
        boolean aRealizarFinalizados;
        aRealizarFinalizados = isCantidadFinalizada();
        aRealizarFinalizados = isArealizarfinalizados(aRealizarFinalizados);
        return aRealizarFinalizados;
    }

    private boolean isCantidadFinalizada() {
        if(recetaManager.cantidad.getValue()!=null&&recetaManager.cantidad.getValue()>0&&recetaManager.realizadas.getValue()!=null&&(recetaManager.cantidad.getValue()-recetaManager.realizadas.getValue()>0)){
            recetaManager.realizadas.setValue(recetaManager.realizadas.getValue()+1);
            preferencesManager.setRealizadas(recetaManager.realizadas.getValue());
            return recetaManager.cantidad.getValue() <= recetaManager.realizadas.getValue();
        }
        return false;
    }

    private boolean isArealizarfinalizados(boolean aRealizarFinalizados) {
        if(preferencesManager.getModoUso()==1||preferencesManager.getRecetaComoPedidoCheckbox()){//receta como pedido
            recetaManager.realizadas.setValue(recetaManager.cantidad.getValue());
            preferencesManager.setRealizadas(recetaManager.cantidad.getValue());
            aRealizarFinalizados =true;
        }
        return aRealizarFinalizados;
    }

    public void labelSetupStop() {
        labelManager.oidreceta.value="0";
    }

    public void preferencesSetupStop() {
        preferencesManager.setRecetaId(0);
        preferencesManager.setPedidoId(0);
        preferencesManager.setEstado(0);
        preferencesManager.setEjecutando(false);
        preferencesManager.setAutomatico(false);
    }

    public void labelSetupInit() {
        labelManager.onetototal.value = "0";
        labelManager.opaso.value=recetaManager.pasoActual;
    }

    public void preferencesSetupInit() {
        preferencesManager.setNetototal("0");
        preferencesManager.setEjecutando(true);
        preferencesManager.setPasoActual(recetaManager.pasoActual);
    }

    public void updateCurrentIngredient(String desc, String cod) {
        labelManager.oingredientes.value= desc;
        labelManager.ocodigoingrediente.value= cod;
    }

    public boolean isDatosDisable() {
        return !labelManager.olote.value.equals("") &&
                !labelManager.oturno.value.equals("") &&
                !labelManager.ovenci.value.equals("");
    }

    public void setupIdNetoTotal(String kilosStr, long id) {
        labelManager.oidreceta.value=id;
        preferencesManager.setNetototal(kilosStr);
        labelManager.onetototal.value = kilosStr;
    }

    public boolean isRecipeBatch() {
        return preferencesManager.getModoUso() == 0 || !preferencesManager.getRecetaComoPedidoCheckbox();
    }

    public boolean isLoteVacio() {
        return labelManager.olote.value == "";
    }

    public void nuevoLoteNumerico() {
        labelManager.olote.value=String.valueOf(preferencesManager.getLoteAutomatico()+1);
        preferencesManager.setLoteAutomatico(preferencesManager.getLoteAutomatico()+1);
    }

    public void initializeLabelVariables() {
        labelManager.opaso.value=String.valueOf(recetaManager.pasoActual);
        labelManager.ocodigoreceta.value= recetaManager.codigoReceta;
        labelManager.oreceta.value= recetaManager.nombreReceta;
    }

    public void mostrarMensajeDeError(String mensaje) {
        mensajeError.setValue(mensaje);
    }


}