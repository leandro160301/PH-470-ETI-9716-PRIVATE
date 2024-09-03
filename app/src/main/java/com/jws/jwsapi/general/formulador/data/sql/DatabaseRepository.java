package com.jws.jwsapi.general.formulador.data.sql;

import com.jws.jwsapi.common.users.UsersManager;
import com.jws.jwsapi.general.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.general.formulador.di.LabelManager;
import com.jws.jwsapi.general.formulador.di.RecetaManager;
import com.jws.jwsapi.general.formulador.viewmodel.FormPreferencesLabelViewModel;
import com.jws.jwsapi.utils.ToastHelper;
import com.jws.jwsapi.utils.Utils;
import com.service.Balanzas.BalanzaService;

public class DatabaseRepository {
    DatabaseHelper formSqlHelper;
    PreferencesManager preferencesManager;
    LabelManager labelManager;
    BalanzaService.Balanzas bza;
    RecetaManager recetaManager;
    UsersManager usersManager;
    ToastHelper toastHelper;
    FormPreferencesLabelViewModel labelPreferencesViewModel;
    int nBza;

    public DatabaseRepository(DatabaseHelper formSqlHelper, PreferencesManager preferencesManager, LabelManager labelManager, BalanzaService.Balanzas bza, RecetaManager recetaManager, int nBza, UsersManager usersManager, ToastHelper toastHelper, FormPreferencesLabelViewModel labelPreferencesViewModel){
        this.nBza = nBza;
        this.formSqlHelper = formSqlHelper;
        this.preferencesManager = preferencesManager;
        this.labelManager = labelManager;
        this.recetaManager = recetaManager;
        this.bza = bza;
        this.usersManager = usersManager;
        this.toastHelper = toastHelper;
        this.labelPreferencesViewModel = labelPreferencesViewModel;
    }
    public void insertarPrimerPasoRecetaBatchSQL() {//guardar en receta una nueva y que devuelva el id
        labelManager.oidreceta.value=String.valueOf(preferencesManager.getRecetaId());
        if(preferencesManager.getRecetaId()==0){
            long id= sqlRecetaBatch(formSqlHelper);
            if(id>-1){
                labelManager.oidreceta.value=String.valueOf(id);
                preferencesManager.setRecetaId(id);
                long id2=sqlPrimerPasoPesadaBatch(formSqlHelper,id);
                if(id2!=-1){
                    labelManager.oidpesada.value=String.valueOf(id2);
                }
            }else{
                toastHelper.mensajeError("Error en base de datos, debe hacer un reset o actualizar programa");
            }
        }
    }

    private long sqlPrimerPasoPesadaBatch(DatabaseHelper form_sqlDb, long id) {
        return form_sqlDb.insertarPesada(String.valueOf(id),"",recetaManager.codigoReceta,recetaManager.nombreReceta,
                recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getCodigoIng(), recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getDescIng(),(String) labelManager.olote.value,
                (String) labelManager.ovenci.value,(String) labelManager.oturno.value,bza.getNetoStr(nBza)+bza.getUnidad(nBza),
                bza.getBrutoStr(nBza)+bza.getUnidad(nBza),bza.getTaraDigital(nBza)+bza.getUnidad(nBza),
                Utils.getFecha(),Utils.getHora(),(String) labelManager.ocampo1.value,(String) labelManager.ocampo2.value,
                (String) labelManager.ocampo3.value,(String) labelManager.ocampo4.value,(String) labelManager.ocampo5.value,preferencesManager.getCampo1(),
                preferencesManager.getCampo2(),preferencesManager.getCampo3(),preferencesManager.getCampo4(),preferencesManager.getCampo5(),usersManager.getUsuarioActual(),
                recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilosIng() +bza.getUnidad(nBza), recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilosRealesIng() +bza.getUnidad(nBza),"","",String.valueOf(nBza));
    }

    private long sqlRecetaBatch(DatabaseHelper form_sqlDb) {
        return form_sqlDb.insertarReceta(recetaManager.codigoReceta,recetaManager.nombreReceta, (String) labelManager.olote.value,
                (String) labelManager.ovenci.value,(String) labelManager.oturno.value,bza.getNetoStr(nBza)+bza.getUnidad(nBza),
                bza.getBrutoStr(nBza)+bza.getUnidad(nBza),bza.getTaraDigital(nBza)+bza.getUnidad(nBza),
                Utils.getFecha(),Utils.getHora(),(String) labelManager.ocampo1.value,(String) labelManager.ocampo2.value,
                (String) labelManager.ocampo3.value,(String) labelManager.ocampo4.value,(String) labelManager.ocampo5.value,preferencesManager.getCampo1(),
                preferencesManager.getCampo2(),preferencesManager.getCampo3(),preferencesManager.getCampo4(),preferencesManager.getCampo5(),usersManager.getUsuarioActual(),
                recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilosTotales() +bza.getUnidad(nBza),"","",String.valueOf(nBza));
    }

    public void insertarPrimerPasoPedidoBatchSQL() {//guardar en receta una nueva y que devuelva el id
        labelManager.oidreceta.value=preferencesManager.getPedidoId();
        if(preferencesManager.getPedidoId()==0){
            long id= sqlRecetaPedido(formSqlHelper);
            if(id>-1){
                labelManager.oidreceta.value=String.valueOf(id);
                preferencesManager.setPedidoId(id);
                long id2= sqlPrimerPasoPesadaPedido(formSqlHelper,id);
                if(id2!=-1){
                    labelManager.oidpesada.value=String.valueOf(id2);
                }else{
                    toastHelper.mensajeError("Error en base de datos pesada, debe hacer un reset o actualizar programa");
                }
            }else{
                toastHelper.mensajeError("Error en base de datos pedido, debe hacer un reset o actualizar programa");
            }
        }

    }

    private long sqlPrimerPasoPesadaPedido(DatabaseHelper form_sqlDb, long id) {
        return form_sqlDb.insertarPesada("",String.valueOf(id),recetaManager.codigoReceta,recetaManager.nombreReceta,
                recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getCodigoIng(), recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getDescIng(),(String) labelManager.olote.value,
                (String) labelManager.ovenci.value,(String) labelManager.oturno.value,bza.getNetoStr(nBza)+bza.getUnidad(nBza),
                bza.getBrutoStr(nBza)+bza.getUnidad(nBza),bza.getTaraDigital(nBza)+bza.getUnidad(nBza),
                Utils.getFecha(),Utils.getHora(),(String) labelManager.ocampo1.value,(String) labelManager.ocampo2.value,
                (String) labelManager.ocampo3.value,(String) labelManager.ocampo4.value,(String)labelManager.ocampo5.value,preferencesManager.getCampo1(),
                preferencesManager.getCampo2(),preferencesManager.getCampo3(),preferencesManager.getCampo4(),preferencesManager.getCampo5(),usersManager.getUsuarioActual(),
                recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilosIng() +bza.getUnidad(nBza), recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilosRealesIng() +bza.getUnidad(nBza),"","",String.valueOf(nBza));
    }

    private long sqlRecetaPedido(DatabaseHelper form_sqlDb) {
        return form_sqlDb.insertarPedido(recetaManager.codigoReceta,recetaManager.nombreReceta, (String) labelManager.olote.value,
                (String) labelManager.ovenci.value,(String) labelManager.oturno.value,bza.getNetoStr(nBza)+bza.getUnidad(nBza),
                bza.getBrutoStr(nBza)+bza.getUnidad(nBza),bza.getTaraDigital(nBza)+bza.getUnidad(nBza),
                Utils.getFecha(),Utils.getHora(),(String) labelManager.ocampo1.value,(String) labelManager.ocampo2.value,
                (String) labelManager.ocampo3.value,(String) labelManager.ocampo4.value,(String) labelManager.ocampo5.value,preferencesManager.getCampo1(),
                preferencesManager.getCampo2(),preferencesManager.getCampo3(),preferencesManager.getCampo4(),preferencesManager.getCampo5(),usersManager.getUsuarioActual(),
                recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilosTotales() +bza.getUnidad(nBza),"","",String.valueOf(nBza));
    }

    public void insertarNuevoPasoRecetaBatchSQL(float kilos) {
        //agarrar id guardado_pesadas en memoria
        labelManager.oidreceta.value=String.valueOf(preferencesManager.getRecetaId());
        if(preferencesManager.getRecetaId()>0){
            labelManager.oidreceta.value=String.valueOf(preferencesManager.getRecetaId());
            long id= formSqlHelper.insertarPesada(String.valueOf(preferencesManager.getRecetaId()),"",recetaManager.codigoReceta,recetaManager.nombreReceta,
                    recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getCodigoIng(), recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getDescIng(),(String) labelManager.olote.value,
                    (String) labelManager.ovenci.value,(String) labelManager.oturno.value,bza.getNetoStr(nBza)+bza.getUnidad(nBza),
                    bza.getBrutoStr(nBza)+bza.getUnidad(nBza),bza.getTaraDigital(nBza)+bza.getUnidad(nBza),
                    Utils.getFecha(),Utils.getHora(),(String) labelManager.ocampo1.value,(String) labelManager.ocampo2.value,
                    (String) labelManager.ocampo3.value,(String) labelManager.ocampo4.value,(String) labelManager.ocampo5.value,preferencesManager.getCampo1(),
                    preferencesManager.getCampo2(),preferencesManager.getCampo3(),preferencesManager.getCampo4(),preferencesManager.getCampo5(),usersManager.getUsuarioActual(),
                    recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilosIng(), recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilosRealesIng(),"","",String.valueOf(nBza));

            if(recetaManager.pasoActual==recetaManager.listRecetaActual.size()){
                actualizaDatosNetoTotalReceta(bza.format(nBza,String.valueOf(kilos)));
            }
            if(id==-1){
                toastHelper.mensajeError("Error en base de datos, debe hacer un reset o actualizar programa");
            }else{
                labelManager.oidpesada.value=String.valueOf(id);
            }

        }
    }

    private void actualizaDatosNetoTotalReceta(String kilosStr) {
        labelPreferencesViewModel.setupIdNetoTotal(kilosStr,preferencesManager.getRecetaId());
        formSqlHelper.actualizarNetoTotalReceta(kilosStr+bza.getUnidad(nBza),String.valueOf(preferencesManager.getRecetaId()));
    }

    private void actualizaDatosNetoTotalPedido(String kilosStr) {
        labelPreferencesViewModel.setupIdNetoTotal(kilosStr,preferencesManager.getPedidoId());
        formSqlHelper.actualizarNetoTotalPedido(kilosStr+bza.getUnidad(nBza),String.valueOf(preferencesManager.getPedidoId()));

    }

    public void insertarNuevoPasoPedidoBatchSQL(float kilos) {
        //agarrar id guardado_pesadas en memoria
        labelManager.oidreceta.value=preferencesManager.getPedidoId();
        if(preferencesManager.getPedidoId()>0){
            labelManager.oidreceta.value=preferencesManager.getPedidoId();
            long id= formSqlHelper.insertarPesada("",
                    String.valueOf(preferencesManager.getPedidoId()),
                    recetaManager.codigoReceta,recetaManager.nombreReceta,
                    recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getCodigoIng(),
                    recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getDescIng(),
                    (String) labelManager.olote.value, (String) labelManager.ovenci.value,
                    (String) labelManager.oturno.value,bza.getNetoStr(nBza),
                    bza.getBrutoStr(nBza),
                    bza.getTaraDigital(nBza),
                    Utils.getFecha(),Utils.getHora(),(String) labelManager.ocampo1.value,
                    (String) labelManager.ocampo2.value,
                    (String) labelManager.ocampo3.value,
                    (String) labelManager.ocampo4.value,
                    (String) labelManager.ocampo5.value,
                    preferencesManager.getCampo1(),
                    preferencesManager.getCampo2(),
                    preferencesManager.getCampo3(),
                    preferencesManager.getCampo4(),
                    preferencesManager.getCampo5(),
                    usersManager.getUsuarioActual(),
                    recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilosIng(),
                    recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilosRealesIng(),
                    "","",String.valueOf(nBza));
            updateNetoTotal(kilos);
            if(id==-1){
                toastHelper.mensajeError("Error en base de datos, debe hacer un reset o actualizar programa");
            }else{
                labelManager.oidpesada.value=String.valueOf(id);
            }

        }
    }

    private void updateNetoTotal(float kilos) {
        if(recetaManager.pasoActual==recetaManager.listRecetaActual.size()){
            actualizaDatosNetoTotalPedido(bza.format(nBza,String.valueOf(kilos)));
        }
    }

}
