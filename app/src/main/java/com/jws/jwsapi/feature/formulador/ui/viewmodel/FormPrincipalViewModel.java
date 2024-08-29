package com.jws.jwsapi.feature.formulador.ui.viewmodel;

import static com.jws.jwsapi.utils.Utils.format;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.jws.jwsapi.feature.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.feature.formulador.data.repository.RecipeRepository;
import com.jws.jwsapi.feature.formulador.di.LabelManager;
import com.jws.jwsapi.feature.formulador.di.RecetaManager;
import com.jws.jwsapi.feature.formulador.models.FormModelReceta;
import com.jws.jwsapi.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class FormPrincipalViewModel extends ViewModel {
    private final RecetaManager recetaManager;
    private final PreferencesManager preferencesManager;
    private final RecipeRepository recipeRepository;
    private final LabelManager labelManager;
    private final MutableLiveData<String> mensajeError = new MutableLiveData<>();
    public LiveData<String> mensajeToastError = mensajeError;

    @Inject
    public FormPrincipalViewModel(RecetaManager recetaManager, PreferencesManager preferencesManager,RecipeRepository recipeRepository, LabelManager labelManager) {
        this.recetaManager = recetaManager;
        this.preferencesManager = preferencesManager;
        this.recipeRepository = recipeRepository;
        this.labelManager = labelManager;
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
        if(this.recetaManager.listRecetaActual ==null)this.recetaManager.listRecetaActual =new ArrayList<>();
    }

    public boolean ejecutarReceta(List<FormModelReceta> lista) {
        recetaManager.listRecetaActual = lista;
        preferencesManager.setPasosRecetaActual(recetaManager.listRecetaActual);
        if(recetaManager.listRecetaActual.size()>0){
            setupModoUso();
            return true;
        }else{
            mostrarMensajeDeError("Error en la receta elegida");
            return false;
        }
    }

    private void setupModoUso() {
        int mododeuso=preferencesManager.getModoUso();
        if(mododeuso==0)setupModoUso(false);
        if(mododeuso==1)setupModoUso(true);
        configurarRecetaParaPedido();
    }

    public boolean modoKilos() {
        if(preferencesManager.getModoUso()==0||!preferencesManager.getRecetacomopedidoCheckbox()){
            return true;  //por batch
        }else{  //por pedido
            if(recetaManager.realizadas.getValue()!=null&&recetaManager.realizadas.getValue()==0){
                return true;
            }else {
                mostrarMensajeDeError("Ingrese una cantidad");
                return false;
            }
        }
    }

    public void actualizarBarraProceso(int num, String unidad) {
        if(isManualOEstadoProceso()){
            String kilosIng=recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getKilos_ing();
            String kilosDesc=recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getDescrip_ing();
            if(Utils.isNumeric(kilosIng)){
                if(Float.parseFloat(kilosIng)==0){
                    String texto="Ingrese "+ kilosDesc +" en balanza "+ num;
                    if(recetaManager.automatico){
                        texto="Cargando "+ kilosDesc +" en salida "+ preferencesManager.getSalida();
                    }
                    recetaManager.estadoMensajeStr.setValue(texto);
                }else{
                    String texto="Ingrese "+ kilosIng + unidad+ " de "+ kilosDesc + " en balanza "+ num;
                    if(recetaManager.automatico){
                        texto="Cargando "+ kilosIng + unidad+ " de "+ kilosDesc + " en salida "+ preferencesManager.getSalida();
                    }
                    recetaManager.estadoMensajeStr.setValue(texto);
                }
            }
        }
    }

    private boolean isManualOEstadoProceso() {
        return !recetaManager.automatico || recetaManager.estadoBalanza == RecetaManager.PROCESO;
    }


    public int determinarBalanza() {
        String kilos = recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getKilos_ing();
        int modo = preferencesManager.getModoBalanza();
        int num = 1;
        if (Utils.isNumeric(kilos)) {
            float kilosFloat = Float.parseFloat(kilos);
            float bza1Limite = Float.parseFloat(preferencesManager.getBza1Limite());
            float bza2Limite = Float.parseFloat(preferencesManager.getBza2Limite());
            if (kilosFloat > bza2Limite && modo > 1) {
                num = 3;
            } else if (kilosFloat < bza2Limite && modo > 0) {
                num = 2;
            } else if (kilosFloat < bza1Limite) {
                num = 1;
            }
        }
        if (num == 1) {
            num = determinarBalanzaPorModo(modo);
        }

        return num;
    }

    private int determinarBalanzaPorModo(int modo) {
        int num = 0;
        switch (modo) {
            case 0:
                num = 1;
                break;
            case 1:
                num = 2;
                break;
            case 2:
                num = 3;
                break;
        }
        return num;
    }


    private void configurarRecetaParaPedido() {
        if((preferencesManager.getRecetacomopedido()||preferencesManager.getRecetacomopedidoCheckbox())&&recetaManager.cantidad.getValue()!=null){
            float kilosTotalesFloat=0;
            List<FormModelReceta> nuevareceta=new ArrayList<>();
            kilosTotalesFloat = getKilosTotales(kilosTotalesFloat, nuevareceta);
            updateRecetaYTotales(kilosTotalesFloat, nuevareceta);
        }
    }

    private float getKilosTotales(float kilosTotalesFloat, List<FormModelReceta> nuevareceta) {
        for(int i = 0; i<recetaManager.listRecetaActual.size(); i++){
            kilosTotalesFloat = instanciaPorCantidad(kilosTotalesFloat, nuevareceta, i);
        }
        return kilosTotalesFloat;
    }

    private float instanciaPorCantidad(float kilosTotalesFloat, List<FormModelReceta> nuevareceta, int i) {
        if(recetaManager.cantidad.getValue()!=null) return 0;
        for(int k = 0; k<recetaManager.cantidad.getValue(); k++){
            FormModelReceta nuevaInstancia = getNuevaInstancia(i);
            nuevareceta.add(nuevaInstancia); // si en vez de crear la nueva instancia le pasamos mainActivity.mainClass.listRecetaActual.get(i) entonces apuntara a las mismas direcciones de memoria
            if(Utils.isNumeric(recetaManager.listRecetaActual.get(i).getKilos_ing())){
                kilosTotalesFloat = kilosTotalesFloat +Float.parseFloat(recetaManager.listRecetaActual.get(i).getKilos_ing());
            }
        }
        return kilosTotalesFloat;
    }

    private void updateRecetaYTotales(float kilosTotalesFloat, List<FormModelReceta> nuevareceta) {
        for(int i = 0; i< nuevareceta.size(); i++){
            nuevareceta.get(i).setKilos_totales(String.valueOf(kilosTotalesFloat));
        }
        recetaManager.listRecetaActual = nuevareceta;
        preferencesManager.setPasosRecetaActual(recetaManager.listRecetaActual);
    }

    private FormModelReceta getNuevaInstancia(int i) {
        return new FormModelReceta(
                recetaManager.listRecetaActual.get(i).getCodigo(),
                recetaManager.listRecetaActual.get(i).getNombre(),
                recetaManager.listRecetaActual.get(i).getKilos_totales(),
                recetaManager.listRecetaActual.get(i).getCodigo_ing(),
                recetaManager.listRecetaActual.get(i).getDescrip_ing(),
                recetaManager.listRecetaActual.get(i).getKilos_ing(),
                recetaManager.listRecetaActual.get(i).getKilos_reales_ing(),
                recetaManager.listRecetaActual.get(i).getTolerancia_ing()
        );
    }

    public void nuevoLoteFecha() {
        String lote = recipeRepository.getNuevoLoteFecha();
        if (lote != null) {
            labelManager.olote.value = lote;
        }
    }

    public void verificarNuevoLoteFecha() {
        String lote = recipeRepository.verificarNuevoLoteFecha();
        if (lote != null) {
            labelManager.olote.value = lote;
        }
    }


    public void restaurarDatos() {
        boolean arealizarfinalizados=aRealizarFinalizados();
        restaurarLote(arealizarfinalizados);
        restaurarVencimiento(arealizarfinalizados);
        restaurarCampo1(arealizarfinalizados);
        restaurarCampo2(arealizarfinalizados);
        restaurarCampo3(arealizarfinalizados);
        restaurarCampo4(arealizarfinalizados);
        restaurarCampo5(arealizarfinalizados);

    }

    private void restaurarCampo5(boolean arealizarfinalizados) {
        if(preferencesManager.getResetCampo5()==1){
            if(arealizarfinalizados){
                preferencesManager.setCampo5Valor("");
                labelManager.ocampo5.value="";
            }
        }
        if(preferencesManager.getResetCampo5()==2){
            preferencesManager.setCampo5Valor("");
            labelManager.ocampo5.value="";
        }
    }

    private void restaurarCampo4(boolean arealizarfinalizados) {
        if(preferencesManager.getResetCampo4()==1){
            if(arealizarfinalizados){
                preferencesManager.setCampo4Valor("");
                labelManager.ocampo4.value="";
            }
        }
        if(preferencesManager.getResetCampo4()==2){
            preferencesManager.setCampo4Valor("");
            labelManager.ocampo4.value="";
        }
    }

    private void restaurarCampo3(boolean arealizarfinalizados) {
        if(preferencesManager.getResetCampo3()==1){
            if(arealizarfinalizados){
                preferencesManager.setCampo3Valor("");
                labelManager.ocampo3.value="";
            }
        }
        if(preferencesManager.getResetCampo3()==2){
            preferencesManager.setCampo3Valor("");
            labelManager.ocampo3.value="";
        }
    }

    private void restaurarCampo2(boolean arealizarfinalizados) {
        if(preferencesManager.getResetCampo2()==1){
            if(arealizarfinalizados){
                preferencesManager.setCampo2Valor("");
                labelManager.ocampo2.value="";
            }
        }
        if(preferencesManager.getResetCampo2()==2){
            preferencesManager.setCampo2Valor("");
            labelManager.ocampo2.value="";
        }
    }

    private void restaurarCampo1(boolean arealizarfinalizados) {
        if(preferencesManager.getResetCampo1()==1){
            if(arealizarfinalizados){
                preferencesManager.setCampo1Valor("");
                labelManager.ocampo1.value="";
            }
        }
        if(preferencesManager.getResetCampo1()==2){
            preferencesManager.setCampo1Valor("");
            labelManager.ocampo1.value="";
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

    private void restaurarVencimiento(boolean arealizarfinalizados) {
        if(preferencesManager.getResetVencimiento()==1){
            if(arealizarfinalizados){
                preferencesManager.setVencimiento("");
                labelManager.ovenci.value="";
            }
        }
        if(preferencesManager.getResetVencimiento()==2){
            preferencesManager.setVencimiento("");
            labelManager.ovenci.value="";
        }
    }

    private boolean aRealizarFinalizados() {
        boolean arealizarfinalizados;
        arealizarfinalizados = isCantidadFinalizada();
        arealizarfinalizados = isArealizarfinalizados(arealizarfinalizados);
        return arealizarfinalizados;
    }

    private boolean isCantidadFinalizada() {
        if(recetaManager.cantidad.getValue()!=null&&recetaManager.cantidad.getValue()>0&&recetaManager.realizadas.getValue()!=null&&(recetaManager.cantidad.getValue()-recetaManager.realizadas.getValue()>0)){
            recetaManager.realizadas.setValue(recetaManager.realizadas.getValue()+1);
            preferencesManager.setRealizadas(recetaManager.realizadas.getValue());
            return recetaManager.cantidad.getValue() <= recetaManager.realizadas.getValue();
        }
        return false;
    }

    private boolean isArealizarfinalizados(boolean arealizarfinalizados) {
        if(preferencesManager.getModoUso()==1||preferencesManager.getRecetacomopedidoCheckbox()){//receta como pedido
            recetaManager.realizadas.setValue(recetaManager.cantidad.getValue());
            preferencesManager.setRealizadas(recetaManager.cantidad.getValue());
            arealizarfinalizados =true;
        }
        return arealizarfinalizados;
    }


    public void calculaPorcentajeError(float neto, String netoStr, String unidad) {
        recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).setKilos_reales_ing(netoStr);
        labelManager.okilosreales.value= netoStr +unidad;
        float porcentaje=((neto*100)/recetaManager.setPoint)-100;
        if(porcentaje<0){
            porcentaje=porcentaje*(-1);
        }
        recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).setTolerancia_ing(format(String.valueOf(porcentaje),2) + "%");
    }


    public void setearModoUsoDialogo(String texto, boolean checkbox, int mododeuso) {
        if(recetaManager.cantidad.getValue()!=null&&Float.parseFloat(texto)>0){
            recetaManager.cantidad.setValue ((int) Float.parseFloat(texto));
            preferencesManager.setCantidad(recetaManager.cantidad.getValue());
            recetaManager.realizadas.setValue(0);
            preferencesManager.setRealizadas(0);
            if(mododeuso==1){
                setupModoUsoCheckBox(true);
            }else{
                setupModoUsoCheckBox(false);
                if(checkbox){
                    setupModoUsoCheckBox(true);
                }
            }
        }
    }

    public void detener(){
        labelManager.oidreceta.value="0";
        preferencesManager.setRecetaId(0);
        preferencesManager.setPedidoId(0);
        recetaManager.estado=0;
        preferencesManager.setEstado(0);
        recetaManager.ejecutando=false;
        preferencesManager.setEjecutando(false);
        recetaManager.automatico=false;
        preferencesManager.setAutomatico(false);
        recetaManager.estadoBalanza=RecetaManager.DETENIDO;
    }


    private void setupModoUsoCheckBox(boolean modo) {
        recetaManager.recetaComoPedido =modo;
        setupModoUso(modo);

    }

    public void setupModoUso(boolean modo) {
        preferencesManager.setRecetacomopedido(modo);
        preferencesManager.setRecetacomopedidoCheckbox(modo);
    }

    public void setEstadoPesar() {
        recetaManager.estado = 2;
        preferencesManager.setEstado(2);
    }


    public void mostrarMensajeDeError(String mensaje) {
        mensajeError.setValue(mensaje);
    }

    public LiveData<String> getNetoTotal() {
        return recetaManager.netoTotal;
    }

    public LiveData<Integer> getCantidad() {
        return recetaManager.cantidad;
    }

    public LiveData<Integer> getRealizadas() {
        return recetaManager.realizadas;
    }

    public LiveData<String> getEstadoMensajeStr() {
        return recetaManager.estadoMensajeStr;
    }

}