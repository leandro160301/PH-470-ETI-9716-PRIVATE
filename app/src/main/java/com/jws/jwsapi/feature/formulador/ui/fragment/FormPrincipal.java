package com.jws.jwsapi.feature.formulador.ui.fragment;

import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.TecladoFlotanteConCancelar;
import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.dialogoCheckboxVisibilidad;
import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.dialogoTexto;
import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.dialogoTextoConCancelar;
import static com.jws.jwsapi.utils.Utils.format;
import static com.jws.jwsapi.utils.Utils.isNumeric;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.jws.jwsapi.base.ui.activities.MainActivity;
import com.jws.jwsapi.base.containers.clases.ButtonProviderSingletonPrincipal;
import com.jws.jwsapi.base.containers.interfaces.ButtonProvider_Principal;
import com.jws.jwsapi.common.impresora.ImprimirEstandar;
import com.jws.jwsapi.common.users.UsersManager;
import com.jws.jwsapi.databinding.ProgFormuladorPrincipalBinding;
import com.jws.jwsapi.feature.formulador.MainFormClass;
import com.jws.jwsapi.feature.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.feature.formulador.di.RecetaManager;
import com.jws.jwsapi.feature.formulador.models.FormModelIngredientes;
import com.jws.jwsapi.feature.formulador.models.FormModelReceta;
import com.jws.jwsapi.feature.formulador.data.sql.FormSqlHelper;
import com.jws.jwsapi.R;
import com.jws.jwsapi.feature.formulador.ui.viewmodel.FormPrincipalViewModel;
import com.jws.jwsapi.feature.formulador.di.LabelManager;
import com.jws.jwsapi.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FormPrincipal extends Fragment  {
    @Inject
    RecetaManager recetaManager;
    @Inject
    UsersManager usersManager;
    @Inject
    PreferencesManager preferencesManager;
    @Inject
    LabelManager labelManager;
    FormPrincipalViewModel viewModel;
    Handler mHandler= new Handler();
    MainActivity mainActivity;
    private ButtonProvider_Principal buttonProvider;
    boolean stoped=false;
    public int rango=0; //0=bajo, 1=acepto,2=alto
    int botonera=0;
    Button bt_2;

    MainFormClass mainClass;
    ProgFormuladorPrincipalBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingletonPrincipal.getInstance().getButtonProvider();
        binding = ProgFormuladorPrincipalBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        mainClass=mainActivity.mainClass;
        initializeViewModel();
        observeViewModel();
        setOnClickListeners();
        configuracionBotones();
        GET_PESO_cal_bza.run();

    }

    private void observeViewModel() {
        viewModel.getEstadoMensajeStr().observe(getViewLifecycleOwner(),estado -> binding.tvEstado.setText(estado));
        viewModel.getNetoTotal().observe(getViewLifecycleOwner(), netoTotal -> binding.tvNetototal.setText(netoTotal));
        viewModel.getCantidad().observe(getViewLifecycleOwner(),cantidad-> binding.tvCantidad.setText(String.valueOf(cantidad)));
        viewModel.getRealizadas().observe(getViewLifecycleOwner(),realizadas->{
            Integer cantidad=recetaManager.cantidad.getValue();
            if(cantidad!=null&&cantidad>0){
                binding.tvRestantes.setVisibility(View.VISIBLE);
                binding.tvRestantesValor.setVisibility(View.VISIBLE);
                binding.tvRestantesValor.setText(String.valueOf(cantidad-realizadas));
            }else{
                binding.tvRestantes.setVisibility(View.GONE);
                binding.tvRestantesValor.setVisibility(View.GONE);
            }
        });

    }

    public Runnable GET_PESO_cal_bza= new Runnable() {
        @Override
        public void run() {
            if(!stoped){
                actualizarVistas();
                if(recetaManager.ejecutando){
                    procesoEjecucion();
                }else{
                    procesoDetenido();
                }
                mHandler.postDelayed(GET_PESO_cal_bza,200);
            }
        }
    };

    private void initializeViewModel() {
        viewModel = new ViewModelProvider(requireActivity()).get(FormPrincipalViewModel.class);/*usamos requireActivity para siempre usar la misma instancia (Form_PrincipalViewModel es el programa y debe estar ejecutandose independientemente del programa)*/
        labelManager.opaso.value=String.valueOf(recetaManager.pasoActual);
        labelManager.ocodigoreceta.value= recetaManager.codigoReceta;
        labelManager.oreceta.value= recetaManager.nombreReceta;

        binding.lnFondolayout.setOnClickListener(view12 -> {
            if(botonera==0){
                botonera=1;
                configuracionBotonesBalanza();
            }else {
                botonera=0;
                configuracionBotones();
            }

        });

        if(recetaManager.ejecutando){
            binding.btStart.setBackgroundResource(R.drawable.circlebuttonon1);
        }else{
            binding.btStart.setBackgroundResource(R.drawable.boton__arranqueparada_selector);
        }

        binding.btStart.setOnClickListener(view1 -> {
            if(recetaManager.ejecutando){
                ConsultaFin();
            }else{
                iniciarReceta();
            }
        });

        if(!recetaManager.recetaActual.isEmpty()){
            String[]arr= recetaManager.recetaActual.split("_");
            if(arr.length==3){
                binding.tvRecetaActual.setText(arr[1].replace("_","")+" "+arr[2].replace("_",""));
            }
        }
        setupUnidad();
        binding.imEstable.setVisibility(View.INVISIBLE);

    }


    private void setupUnidad() {
        String unidad=mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA);
        binding.tvBrutoUnidad.setText(unidad);
        binding.tvNetoUnidad.setText(unidad);
        binding.tvNetototalUnidad.setText(unidad);
    }

    private void setOnClickListeners() {
        binding.btDatos.setOnClickListener(view -> mainActivity.mainClass.openFragment(new FormFragmentIngresoDatos()));
        binding.tvDatos.setOnClickListener(view -> mainActivity.mainClass.openFragment(new FormFragmentIngresoDatos()));
        binding.btCodigo.setOnClickListener(view -> mainActivity.mainClass.openFragment(new FormFragmentRecetas()));
        binding.tvRecetaActual.setOnClickListener(view -> mainActivity.mainClass.openFragment(new FormFragmentRecetas()));
    }

    private void iniciarReceta() {
        if(labelManager.olote.value==""&&preferencesManager.getModoLote()==2)nuevoLoteNumerico();
        if(preferencesManager.getModoLote()==1){
            if(labelManager.olote.value==""){
                nuevoLoteFecha();
            }else{
                //si no esta vacio ya tiene una fecha con un indice, verificamos que la fecha actual este presente por si cambio de dia (empieza en 1 con el nuevo dia)
                verificarNuevoLoteFecha();
            }
        }
        boolean empezar=verificarComienzo();
        if(empezar){
            guardarDatosEnMemoria();
            ejecutarReceta();
        }
    }

    private boolean verificarComienzo() {
        boolean empezar=true;
        if(labelManager.olote.value==""||
                labelManager.ovenci.value==""||faltanCampos()){
            Utils.Mensaje("Faltan ingresar datos",R.layout.item_customtoasterror,mainActivity);
            empezar=false;
        }
        if(recetaManager.recetaActual.isEmpty()){
            Utils.Mensaje("Debe seleccionar una receta para comenzar",R.layout.item_customtoasterror,mainActivity);
            empezar=false;
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
            if (!campos[i].isEmpty() && valores[i].isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void guardarDatosEnMemoria() {
        preferencesManager.setLote((String)labelManager.olote.value);
        preferencesManager.setVencimiento((String)labelManager.ovenci.value);
        preferencesManager.setCampo1Valor((String)labelManager.ocampo1.value);
        preferencesManager.setCampo2Valor((String)labelManager.ocampo2.value);
        preferencesManager.setCampo3Valor((String)labelManager.ocampo3.value);
        preferencesManager.setCampo4Valor((String)labelManager.ocampo4.value);
        preferencesManager.setCampo5Valor((String)labelManager.ocampo5.value);
    }

    private void nuevoLoteNumerico() {
        labelManager.olote.value=String.valueOf(preferencesManager.getLoteAutomatico()+1);
        preferencesManager.setLoteAutomatico(preferencesManager.getLoteAutomatico()+1);
    }

    private void nuevoLoteFecha() {
        String nuevolote=Utils.getFechaDDMMYYYY().replace("/","");
        labelManager.olote.value=nuevolote;
        try (FormSqlHelper form_sqlDb = new FormSqlHelper(getContext(), MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
            int ultimo= form_sqlDb.getLastLote(nuevolote);
            labelManager.olote.value=nuevolote+"-"+ (ultimo + 1);
        }
    }

    private void verificarNuevoLoteFecha() {
        String nuevolote=Utils.getFechaDDMMYYYY().replace("/","");
        try (FormSqlHelper form_sqlDb = new FormSqlHelper(getContext(), MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
            int ultimo= form_sqlDb.getLastLote(nuevolote);
            if(ultimo==0){
                labelManager.olote.value=nuevolote+"-1";
            }
        }
    }

    private void ejecutarReceta() {
        recetaManager.listRecetaActual =new ArrayList<>();
        recetaManager.listRecetaActual = mainActivity.mainClass.getReceta(recetaManager.recetaActual);
        preferencesManager.setPasosRecetaActual(recetaManager.listRecetaActual);
        if(recetaManager.listRecetaActual.size()>0){
            int mododeuso=preferencesManager.getModoUso();
            if(mododeuso==0)setupModoUso(false);
            if(mododeuso==1)setupModoUso(true);
            configurarRecetaParaPedido();
            iniciaPorModoReceta();
        }else{
            Utils.Mensaje("Error en la receta elegida",R.layout.item_customtoasterror,mainActivity);
        }
    }

    private void iniciaPorModoReceta() {
        if(preferencesManager.getModoReceta()==0){//respeta el setpoint de cada paso
            boolean empezarKilos=modoKilos();
            if(empezarKilos)setupValoresParaInicio();
        }else{//ingresa porcentaje
            boolean empezarPorcentaje=modoPorcentaje();
            if(empezarPorcentaje)setupValoresParaInicio();
        }
    }

    private void configurarRecetaParaPedido() {
        if((preferencesManager.getRecetacomopedido()||preferencesManager.getRecetacomopedidoCheckbox())&&recetaManager.cantidad.getValue()!=null){
            float kilostotalesfloat=0;
            List<FormModelReceta> nuevareceta=new ArrayList<>();
            for(int i = 0; i<recetaManager.listRecetaActual.size(); i++){
                for(int k = 0; k<recetaManager.cantidad.getValue(); k++){
                    FormModelReceta nuevaInstancia = new FormModelReceta(
                            recetaManager.listRecetaActual.get(i).getCodigo(),
                            recetaManager.listRecetaActual.get(i).getNombre(),
                            recetaManager.listRecetaActual.get(i).getKilos_totales(),
                            recetaManager.listRecetaActual.get(i).getCodigo_ing(),
                            recetaManager.listRecetaActual.get(i).getDescrip_ing(),
                            recetaManager.listRecetaActual.get(i).getKilos_ing(),
                            recetaManager.listRecetaActual.get(i).getKilos_reales_ing(),
                            recetaManager.listRecetaActual.get(i).getTolerancia_ing()
                    );
                    nuevareceta.add(nuevaInstancia); // si en vez de crear la nueva instancia le pasamos mainActivity.mainClass.listRecetaActual.get(i) entonces apuntara a las mismas direcciones de memoria
                    if(Utils.isNumeric(recetaManager.listRecetaActual.get(i).getKilos_ing())){
                        kilostotalesfloat=kilostotalesfloat+Float.parseFloat(recetaManager.listRecetaActual.get(i).getKilos_ing());
                    }
                }
            }
            for(int i=0;i<nuevareceta.size();i++){
                nuevareceta.get(i).setKilos_totales(String.valueOf(kilostotalesfloat));
            }
            recetaManager.listRecetaActual =nuevareceta;
            preferencesManager.setPasosRecetaActual(recetaManager.listRecetaActual);
        }
    }

    private boolean modoPorcentaje() {
        boolean empezar=true;
        if(preferencesManager.getModoUso()==0||!preferencesManager.getRecetacomopedidoCheckbox()){
            //por batch
            if(recetaManager.realizadas.getValue()!=null&&recetaManager.realizadas.getValue()==0||(recetaManager.cantidad.getValue()!=null)&&recetaManager.realizadas.getValue()-recetaManager.cantidad.getValue()==0){
                IngresaPorcentaje();
            }else {
                calculoporcentajeReceta();
                IngresoRecipiente();
            }
        }else{
            //por pedido
            if(recetaManager.realizadas.getValue()!=null&&recetaManager.realizadas.getValue()==0){
                IngresaPorcentaje();
            }else {
                empezar=false;
                Utils.Mensaje("Ingrese una cantidad",R.layout.item_customtoasterror,mainActivity);
            }
        }
        return empezar;
    }

    private boolean modoKilos() {
        boolean empezar=true;
        if(preferencesManager.getModoUso()==0||!preferencesManager.getRecetacomopedidoCheckbox()){
            //por batch
            IngresoRecipiente();
        }else{
            //por pedido
            if(recetaManager.realizadas.getValue()!=null&&recetaManager.realizadas.getValue()==0){
                IngresoRecipiente();
            }else {
                empezar=false;
                Utils.Mensaje("Ingrese una cantidad",R.layout.item_customtoasterror,mainActivity);
            }
        }
        return empezar;
    }

    private void setupValoresParaInicio() {
        recetaManager.ejecutando=true;
        preferencesManager.setEjecutando(true);
        labelManager.onetototal.value="0";
        recetaManager.netoTotal.setValue("0");
        preferencesManager.setNetototal("0");
        labelManager.onetototal.value = "0";
        binding.btStart.setBackgroundResource(R.drawable.circlebuttonon1);
        recetaManager.pasoActual=1;
        labelManager.opaso.value=recetaManager.pasoActual;
        preferencesManager.setPasoActual(recetaManager.pasoActual);
        verificarPasoEsAutomatico();
    }

    private void verificarPasoEsAutomatico() {
        boolean isAutomatic=false;
        List<FormModelIngredientes> lista= mainClass.getIngredientes();
        int salida=0;
        for(FormModelIngredientes ing: lista){
            if(Objects.equals(ing.getCodigo(), recetaManager.listRecetaActual.get(0).getCodigo_ing())){
                if(ing.getSalida()>0) {
                    isAutomatic=true;
                    salida=ing.getSalida();
                }
            }
        }
        configuracionAutomatico(isAutomatic,salida);

    }

    private void configuracionAutomatico(boolean isAutomatic, int salida) {
        if(isAutomatic){
            recetaManager.automatico=true;
            preferencesManager.setAutomatico(true);
            preferencesManager.setSalida(salida);
            String setPoint=String.valueOf(recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getKilos_ing());
            mainClass.BZA.Itw410FrmSetear(mainClass.N_BZA,setPoint,salida);
            if(mainClass.BZA.Itw410FrmGetSalida(mainClass.N_BZA)==salida&& Objects.equals(mainClass.BZA.Itw410FrmGetSetPoint(mainClass.N_BZA), setPoint)) {
                mainClass.BZA.Itw410FrmStart(mainClass.N_BZA);
            }else{
                Utils.Mensaje("Error de balanza",R.layout.item_customtoasterror,mainActivity);
            }
            //mandar el setpoint y la salida, esperamos y leemos que le llegue bien
            //si le llego bien ponemose el sharedpreferences automatico y recetamanager.automatico en true
        }else{
            recetaManager.automatico=false;
            preferencesManager.setAutomatico(false);
        }
    }

    private void IngresaPorcentaje() {
        String text="Ingrese los kilos a realizar totales y presione SIGUIENTE o si quiere continuar con la receta original presione CONTINUAR";
        TecladoFlotanteConCancelar(null, text, mainActivity, this::calculoporcentajeRecetaDialogo, this::IngresoRecipiente,"CONTINUAR");
    }

    private void calculoporcentajeRecetaDialogo(String toString) {
        if(recetaManager.listRecetaActual.size()>0&&Utils.isNumeric(recetaManager.listRecetaActual.get(0).getKilos_totales())){
            List<String> kilos_ing_originales = new ArrayList<>();
            for(int i = 0; i < recetaManager.listRecetaActual.size(); i++) {
                kilos_ing_originales.add(recetaManager.listRecetaActual.get(i).getKilos_ing());
            }

            float nuevo = Float.parseFloat(toString);
            float kilos_totales_original = Float.parseFloat(recetaManager.listRecetaActual.get(0).getKilos_totales());
            float multiplicador = nuevo / kilos_totales_original;

            for(int i = 0; i < recetaManager.listRecetaActual.size(); i++) {
                recetaManager.listRecetaActual.get(i).setKilos_totales(String.valueOf(nuevo));
                String nuevosp = String.valueOf(Float.parseFloat(kilos_ing_originales.get(i)) * multiplicador);
                recetaManager.listRecetaActual.get(i).setKilos_ing(mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA, nuevosp));
            }
            recetaManager.porcentajeReceta=toString;
            preferencesManager.setPorcentajeReceta(toString);
            preferencesManager.setPasosRecetaActual(recetaManager.listRecetaActual);
            IngresoRecipiente();
        }else{
            Utils.Mensaje("Ocurrio un error con la carga de la receta",R.layout.item_customtoasterror,mainActivity);
            IngresoRecipiente();
        }
    }

    private void calculoporcentajeReceta() {
        System.out.println(recetaManager.listRecetaActual);
        if(recetaManager.listRecetaActual.size()>0&&Utils.isNumeric(recetaManager.listRecetaActual.get(0).getKilos_totales())){
            float nuevo=Float.parseFloat(recetaManager.porcentajeReceta);
            float kilos_totales_original=Float.parseFloat(recetaManager.listRecetaActual.get(0).getKilos_totales());
            float multiplicador=nuevo/kilos_totales_original;
            for(int i = 0; i<recetaManager.listRecetaActual.size(); i++){
                recetaManager.listRecetaActual.get(i).setKilos_totales(String.valueOf(nuevo));
                recetaManager.listRecetaActual.get(i).setKilos_ing(mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA, String.valueOf(Float.parseFloat(recetaManager.listRecetaActual.get(i).getKilos_ing()) * multiplicador)));
            }
            preferencesManager.setPasosRecetaActual(recetaManager.listRecetaActual);
        }else{
            Utils.Mensaje("Ocurrio un error con la carga de la receta",R.layout.item_customtoasterror,mainActivity);
        }
    }

    private void IngresaCantidad() {
        String text="Ingrese la cantidad de recetas que quiere realizar y presione CONTINUAR";
        int mododeuso=preferencesManager.getModoUso();
        int visible=View.VISIBLE;
        boolean checkboxState=false;
        if(mododeuso==0){
            visible=View.INVISIBLE;
        }
        if(mododeuso==1){
            checkboxState=true;
            visible=View.INVISIBLE;
        }
        dialogoCheckboxVisibilidad(null, text, mainActivity, (texto, checkbox) -> {
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
        },true,InputType.TYPE_CLASS_NUMBER,null,"RECETAS COMO PEDIDO (BOLSAS)",visible,checkboxState);
    }

    private void setupModoUsoCheckBox(boolean modo) {
        recetaManager.recetaComoPedido =modo;
        setupModoUso(modo);

    }

    private void setupModoUso(boolean modo) {
        preferencesManager.setRecetacomopedido(modo);
        preferencesManager.setRecetacomopedidoCheckbox(modo);
    }

    private void IngresoRecipiente() {
        //si no poNe siguiente y le pone cancerlar lo manda a tara igual
        dialogoTextoConCancelar(mainActivity, "Ingrese recipiente y luego presione SIGUIENTE", "SIGUIENTE", this::realizarTaraComienzaPesar, this::setEstadoPesar);
    }

    private void realizarTaraComienzaPesar() {
        mainActivity.mainClass.BZA.setTaraDigital(mainActivity.mainClass.N_BZA, mainActivity.mainClass.BZA.getBruto(mainActivity.mainClass.N_BZA));
        setEstadoPesar();
    }

    private void setEstadoPesar() {
        recetaManager.estado = 2;
        preferencesManager.setEstado(2);
    }


    private void ConsultaFin() {
        dialogoTexto(mainActivity, "¿Quiere detener la receta?", "DETENER", this::detener);
    }


    private void configuracionBotonesBalanza() {
        if (buttonProvider != null) {
            Button bt_1 = buttonProvider.getButton1();
            bt_2 = buttonProvider.getButton2();
            Button bt_3 = buttonProvider.getButton3();
            Button bt_4 = buttonProvider.getButton4();
            Button bt_5 = buttonProvider.getButton5();

            binding.lnFondolayout.setBackgroundResource(R.drawable.boton_selector_balanza_seleccionado);

            bt_1.setVisibility(View.VISIBLE);
            bt_2.setVisibility(View.VISIBLE);
            bt_3.setVisibility(View.VISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);

            bt_1.setText("CERO");
            bt_2.setText("TARA");
            bt_3.setText("IMPRIMIR");

            bt_1.setOnClickListener(view -> mainActivity.mainClass.BZA.setCero(mainActivity.mainClass.N_BZA));
            bt_2.setOnClickListener(view -> mainActivity.mainClass.BZA.setTaraDigital(mainActivity.mainClass.N_BZA,mainActivity.mainClass.BZA.getBruto(mainActivity.mainClass.N_BZA)));
            bt_3.setOnClickListener(view -> {
                if(bt_3.isClickable()){
                    bt_3.setClickable(false);
                    ImprimirUltima();
                    view.postDelayed(() -> bt_3.setClickable(true), 2000);
                }

            });

        }
    }

    public void ImprimirUltima() {
        ImprimirEstandar imprimirEstandar = new ImprimirEstandar(getContext(), mainActivity,usersManager,preferencesManager,labelManager);
        imprimirEstandar.EnviarUltimaEtiqueta(mainClass.BZA.serialPortB);

    }


    private void configuracionBotones() {
        if (buttonProvider != null) {
            Button bt_1 = buttonProvider.getButton1();
            bt_2 = buttonProvider.getButton2();
            Button bt_3 = buttonProvider.getButton3();
            Button bt_4 = buttonProvider.getButton4();
            Button bt_5 = buttonProvider.getButton5();

            bt_1.setText("CANTIDAD");
            if(recetaManager.automatico){
                if(mainClass.BZA.Itw410FrmGetEstado(mainClass.N_BZA)==2){
                    bt_2.setText("REANUDAR");
                }else{
                    bt_2.setText("PAUSA");
                }
            }else{
                bt_2.setText("PESAR");
            }

            bt_3.setText("GUARDADO");
            bt_4.setText("RECETAS");
            bt_5.setText("INGREDIENTES");
            bt_1.setVisibility(View.VISIBLE);
            bt_2.setVisibility(View.VISIBLE);
            bt_3.setVisibility(View.VISIBLE);
            bt_4.setVisibility(View.VISIBLE);
            bt_5.setVisibility(View.VISIBLE);

            binding.lnFondolayout.setBackgroundResource(R.drawable.boton_selector_balanza);
            bt_1.setOnClickListener(view -> btCantidad());
            bt_2.setOnClickListener(view -> {
                if(recetaManager.automatico){
                    if(mainClass.BZA.Itw410FrmGetEstado(mainClass.N_BZA)==2){
                        mainClass.BZA.Itw410FrmSetEstado(mainClass.N_BZA,1);
                        bt_2.setText("PAUSAR");
                    }else{
                        mainClass.BZA.Itw410FrmSetEstado(mainClass.N_BZA,2);
                        bt_2.setText("REANUDAR");
                    }
                }else{
                    btPesar(mainActivity.mainClass.BZA.getNeto(mainActivity.mainClass.N_BZA),mainActivity.mainClass.BZA.getNetoStr(mainActivity.mainClass.N_BZA));
                }

            });
            bt_3.setOnClickListener(view -> mainActivity.mainClass.openFragment(new FormFragmentGuardados()));
            bt_4.setOnClickListener(view -> mainActivity.mainClass.openFragment(new FormFragmentRecetas()));
            bt_5.setOnClickListener(view -> mainActivity.mainClass.openFragment(new FormFragmentIngredientes()));



            bt_1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    btCantidad();
                    mainClass.BZA.estado=0;
                    mainClass.BZA.ultimo=mainClass.BZA.ultimo+1;
                }
            });
            bt_2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainClass.BZA.estado=3;
                    if(recetaManager.automatico){
                        if(mainClass.BZA.Itw410FrmGetEstado(mainClass.N_BZA)==2){
                            mainClass.BZA.Itw410FrmSetEstado(mainClass.N_BZA,1);
                        }else{
                            mainClass.BZA.Itw410FrmSetEstado(mainClass.N_BZA,2);
                        }
                    }else{
                        btPesar(mainActivity.mainClass.BZA.getNeto(mainActivity.mainClass.N_BZA),mainActivity.mainClass.BZA.getNetoStr(mainActivity.mainClass.N_BZA));
                    }
                }
            });
            bt_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mainActivity.mainClass.openFragment(new FormFragmentGuardados());
                    mainClass.BZA.estado=2;
                }
            });

        }
    }

    private void btCantidad() {
        if(!recetaManager.ejecutando){
            IngresaCantidad();
        }else{
            Utils.Mensaje("No puede cambiar la cantidad cuando esta ejecutando una receta",R.layout.item_customtoasterror,mainActivity);
        }
    }

    private void btPesar(float neto, String netoStr) {
        if(recetaManager.ejecutando&&!recetaManager.automatico){
            if(recetaManager.pasoActual<=recetaManager.listRecetaActual.size()){
                if(rango==1||preferencesManager.getContinuarFueraRango()){
                    calculaPorcentajeError(neto,netoStr);
                    manejoBasedeDatoseImpresion(neto);
                }else{
                    Utils.Mensaje("Ingrediente fuera de rango",R.layout.item_customtoasterror,mainActivity);
                }
            }else{
                recetaFinalizada();
            }
        }
    }

    private void calculaPorcentajeError(float neto,String netoStr) {
        recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).setKilos_reales_ing(netoStr);
        labelManager.okilosreales.value= netoStr +mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA);
        float porcentaje=((neto*100)/recetaManager.setPoint)-100;
        if(porcentaje<0){
            porcentaje=porcentaje*(-1);
        }
        recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).setTolerancia_ing(format(String.valueOf(porcentaje),2) + "%");
    }

    private void manejoBasedeDatoseImpresion(float neto) {
        imprimeyGuardaPrimerPasoRecetaBatch();
        imprimeyGuardaNuevoPasoRecetaBatch();
        imprimeyGuardaPrimerPasoPedidoBatch();
        imprimeyGuardaNuevoPasoPedidoBatch();
        if(recetaManager.pasoActual<recetaManager.listRecetaActual.size()){
            nuevoPaso(neto);
        }else{
            new Thread(() -> {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                requireActivity().runOnUiThread(this::imprimirEtiquetaFinal);
            }).start();

        }
    }

    private void imprimeyGuardaNuevoPasoPedidoBatch() {
        if(preferencesManager.getPasoActual()>1&&preferencesManager.getRecetacomopedido()){
            insertarNuevoPasoPedidoBatchSQL();
            imprimirEtiquetaPaso();
        }
    }

    private void imprimeyGuardaPrimerPasoPedidoBatch() {
        if(preferencesManager.getPasoActual()==1&&preferencesManager.getRecetacomopedido()){
            insertarPrimerPasoPedidoBatchSQL();
            imprimirEtiquetaPaso();
        }
    }

    private void imprimeyGuardaNuevoPasoRecetaBatch() {
        if(preferencesManager.getPasoActual()>1&&!preferencesManager.getRecetacomopedido()){
            insertarNuevoPasoRecetaBatchSQL();
            imprimirEtiquetaPaso();
        }
    }

    private void imprimeyGuardaPrimerPasoRecetaBatch() {
        if(preferencesManager.getPasoActual()==1&&!preferencesManager.getRecetacomopedido()){
            insertarPrimerPasoRecetaBatchSQL();
            imprimirEtiquetaPaso();
        }
    }

    private void imprimirEtiquetaPaso() {
        if(preferencesManager.getEtiquetaxPaso()){
            Imprimir(0);
        }
    }
    public void Imprimir(int etiqueta) {
        ImprimirEstandar imprimirEstandar = new ImprimirEstandar(getContext(), mainActivity,usersManager,preferencesManager,labelManager);
        imprimirEstandar.EnviarEtiqueta(mainClass.BZA.serialPortB,etiqueta);

    }

    private void imprimirEtiquetaFinal() {
        labelManager.onetototal.value=recetaManager.netoTotal;
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(),R.style.AlertDialogCustom);
        View mView = getLayoutInflater().inflate(R.layout.dialogo_transferenciaarchivo, null);
        TextView textView = mView.findViewById(R.id.textView);
        textView.setText("Imprimiendo etiqueta final");
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            int i = 0;
            int netiqueta = 0;
            @Override
            public void run() {
                if (i < recetaManager.listRecetaActual.size()) {
                    netiqueta++;
                    int end = Math.min(i + 5, recetaManager.listRecetaActual.size());
                    String[] pasos = {"", "", "", "", ""};
                    String[] ingredientes = {"", "", "", "", ""};
                    String[] codingredientes = {"", "", "", "", ""};
                    String[] kilos = {"", "", "", "", ""};

                    for (int j = i; j < end; j++) {
                        int index = j - i; // Esto asegura que los índices sean de 0 a 4
                        pasos[index] = String.valueOf(j + 1);
                        ingredientes[index] = recetaManager.listRecetaActual.get(j).getDescrip_ing();
                        codingredientes[index] = recetaManager.listRecetaActual.get(j).getCodigo_ing();
                        kilos[index] = recetaManager.listRecetaActual.get(j).getKilos_reales_ing() + mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA);
                    }

                    setupVariablesEtiqueta(pasos, ingredientes, codingredientes, kilos, netiqueta);
                    Imprimir(1);

                    i += 5;
                    handler.postDelayed(this, 1000);
                } else {
                    requireActivity().runOnUiThread(dialog::cancel);
                    recetaFinalizada();
                }
            }
        };
        handler.post(myRunnable);
    }

    private void setupVariablesEtiqueta(String[] pasos, String[] ingredientes, String[] codingredientes, String[] kilos, int netiqueta) {
        labelManager.opaso1.value = pasos[0];
        labelManager.opaso2.value = pasos[1];
        labelManager.opaso3.value = pasos[2];
        labelManager.opaso4.value = pasos[3];
        labelManager.opaso5.value = pasos[4];
        labelManager.oingrediente1.value = ingredientes[0];
        labelManager.oingrediente2.value = ingredientes[1];
        labelManager.oingrediente3.value = ingredientes[2];
        labelManager.oingrediente4.value = ingredientes[3];
        labelManager.oingrediente5.value = ingredientes[4];
        labelManager.ocodigoingrediente1.value = codingredientes[0];
        labelManager.ocodigoingrediente2.value = codingredientes[1];
        labelManager.ocodigoingrediente3.value = codingredientes[2];
        labelManager.ocodigoingrediente4.value = codingredientes[3];
        labelManager.ocodigoingrediente5.value = codingredientes[4];
        labelManager.opeso1.value = kilos[0];
        labelManager.opeso2.value = kilos[1];
        labelManager.opeso3.value = kilos[2];
        labelManager.opeso4.value = kilos[3];
        labelManager.opeso5.value = kilos[4];
        labelManager.onumeroetiqueta.value = String.valueOf(netiqueta);

    }

    private void nuevoPaso(float neto) {
        recetaManager.pasoActual=recetaManager.pasoActual+1;
        labelManager.opaso.value=recetaManager.pasoActual;
        preferencesManager.setPasoActual(recetaManager.pasoActual);
        if(Utils.isNumeric(String.valueOf(recetaManager.netoTotal))){
            Float resultado= Float.parseFloat(String.valueOf(recetaManager.netoTotal))+neto;
            recetaManager.netoTotal.setValue(mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(resultado)));
            preferencesManager.setNetototal(String.valueOf(recetaManager.netoTotal));
            labelManager.onetototal.value = String.valueOf(recetaManager.netoTotal);
        }
        preferencesManager.setPasosRecetaActual(recetaManager.listRecetaActual);
        if(preferencesManager.getRecipientexPaso()){
            IngresoRecipiente();
        }else{
            mainActivity.mainClass.BZA.setTaraDigital(mainActivity.mainClass.N_BZA,mainActivity.mainClass.BZA.getBruto(mainActivity.mainClass.N_BZA));
        }

    }

    private void insertarPrimerPasoRecetaBatchSQL() {//guardar en receta una nueva y que devuelva el id
        labelManager.oidreceta.value=String.valueOf(preferencesManager.getRecetaId());
        if(preferencesManager.getRecetaId()==0){
            try (FormSqlHelper form_sqlDb = new FormSqlHelper(getContext(), MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
                long id= sqlRecetaBatch(form_sqlDb);
                if(id>-1){
                    labelManager.oidreceta.value=String.valueOf(id);
                    preferencesManager.setRecetaId(id);
                    long id2=sqlPrimerPasoPesadaBatch(form_sqlDb,id);
                    if(id2!=-1){
                        labelManager.oidpesada.value=String.valueOf(id2);
                    }
                }else{
                    Utils.Mensaje("Error en base de datos, debe hacer un reset o actualizar programa",R.layout.item_customtoasterror,mainActivity);
                }
            }
        }

    }

    private long sqlPrimerPasoPesadaBatch(FormSqlHelper form_sqlDb, long id) {
        return form_sqlDb.insertarPesada(String.valueOf(id),"",recetaManager.codigoReceta,recetaManager.nombreReceta,
                recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getCodigo_ing(), recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getDescrip_ing(),(String) labelManager.olote.value,
                (String) labelManager.ovenci.value,(String) labelManager.oturno.value,mainActivity.mainClass.BZA.getNetoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                mainActivity.mainClass.BZA.getBrutoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),mainActivity.mainClass.BZA.getTaraDigital(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                Utils.getFecha(),Utils.getHora(),(String) labelManager.ocampo1.value,(String) labelManager.ocampo2.value,
                (String) labelManager.ocampo3.value,(String) labelManager.ocampo4.value,(String) labelManager.ocampo5.value,preferencesManager.getCampo1(),
                preferencesManager.getCampo2(),preferencesManager.getCampo3(),preferencesManager.getCampo4(),preferencesManager.getCampo5(),usersManager.getUsuarioActual(),
                recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilos_ing() +mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA), recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilos_reales_ing() +mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),"","",String.valueOf(mainActivity.mainClass.N_BZA));
    }

    private long sqlRecetaBatch(FormSqlHelper form_sqlDb) {
        return form_sqlDb.insertarReceta(recetaManager.codigoReceta,recetaManager.nombreReceta, (String) labelManager.olote.value,
                (String) labelManager.ovenci.value,(String) labelManager.oturno.value,mainActivity.mainClass.BZA.getNetoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                mainActivity.mainClass.BZA.getBrutoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),mainActivity.mainClass.BZA.getTaraDigital(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                Utils.getFecha(),Utils.getHora(),(String) labelManager.ocampo1.value,(String) labelManager.ocampo2.value,
                (String) labelManager.ocampo3.value,(String) labelManager.ocampo4.value,(String) labelManager.ocampo5.value,preferencesManager.getCampo1(),
                preferencesManager.getCampo2(),preferencesManager.getCampo3(),preferencesManager.getCampo4(),preferencesManager.getCampo5(),usersManager.getUsuarioActual(),
                recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilos_totales() +mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),"","",String.valueOf(mainActivity.mainClass.N_BZA));
    }

    private void insertarPrimerPasoPedidoBatchSQL() {//guardar en receta una nueva y que devuelva el id
        labelManager.oidreceta.value=preferencesManager.getPedidoId();
        if(preferencesManager.getPedidoId()==0){
            try (FormSqlHelper form_sqlDb = new FormSqlHelper(getContext(), MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
                long id= sqlRecetaPedido(form_sqlDb);
                if(id>-1){
                    labelManager.oidreceta.value=String.valueOf(id);
                    preferencesManager.setPedidoId(id);
                    long id2= sqlPrimerPasoPesadaPedido(form_sqlDb,id);
                    if(id2!=-1){
                        labelManager.oidpesada.value=String.valueOf(id2);
                    }else{
                        Utils.Mensaje("Error en base de datos pesada, debe hacer un reset o actualizar programa",R.layout.item_customtoasterror,mainActivity);
                    }

                }else{
                    Utils.Mensaje("Error en base de datos pedido, debe hacer un reset o actualizar programa",R.layout.item_customtoasterror,mainActivity);
                }
            }
        }

    }

    private long sqlPrimerPasoPesadaPedido(FormSqlHelper form_sqlDb, long id) {
        return form_sqlDb.insertarPesada("",String.valueOf(id),recetaManager.codigoReceta,recetaManager.nombreReceta,
                recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getCodigo_ing(), recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getDescrip_ing(),(String) labelManager.olote.value,
                (String) labelManager.ovenci.value,(String) labelManager.oturno.value,mainActivity.mainClass.BZA.getNetoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                mainActivity.mainClass.BZA.getBrutoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),mainActivity.mainClass.BZA.getTaraDigital(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                Utils.getFecha(),Utils.getHora(),(String) labelManager.ocampo1.value,(String) labelManager.ocampo2.value,
                (String) labelManager.ocampo3.value,(String) labelManager.ocampo4.value,(String)labelManager.ocampo5.value,preferencesManager.getCampo1(),
                preferencesManager.getCampo2(),preferencesManager.getCampo3(),preferencesManager.getCampo4(),preferencesManager.getCampo5(),usersManager.getUsuarioActual(),
                recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilos_ing() +mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA), recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilos_reales_ing() +mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),"","",String.valueOf(mainActivity.mainClass.N_BZA));
    }

    private long sqlRecetaPedido(FormSqlHelper form_sqlDb) {
        return form_sqlDb.insertarPedido(recetaManager.codigoReceta,recetaManager.nombreReceta, (String) labelManager.olote.value,
                (String) labelManager.ovenci.value,(String) labelManager.oturno.value,mainActivity.mainClass.BZA.getNetoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                mainActivity.mainClass.BZA.getBrutoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),mainActivity.mainClass.BZA.getTaraDigital(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                Utils.getFecha(),Utils.getHora(),(String) labelManager.ocampo1.value,(String) labelManager.ocampo2.value,
                (String) labelManager.ocampo3.value,(String) labelManager.ocampo4.value,(String) labelManager.ocampo5.value,preferencesManager.getCampo1(),
                preferencesManager.getCampo2(),preferencesManager.getCampo3(),preferencesManager.getCampo4(),preferencesManager.getCampo5(),usersManager.getUsuarioActual(),
                recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilos_totales() +mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),"","",String.valueOf(mainActivity.mainClass.N_BZA));
    }

    private void insertarNuevoPasoRecetaBatchSQL() {
        //agarrar id guardado_pesadas en memoria
        labelManager.oidreceta.value=String.valueOf(preferencesManager.getRecetaId());
        if(preferencesManager.getRecetaId()>0){
            try (FormSqlHelper form_sqlDb = new FormSqlHelper(getContext(), MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
                labelManager.oidreceta.value=String.valueOf(preferencesManager.getRecetaId());
                long id= form_sqlDb.insertarPesada(String.valueOf(preferencesManager.getRecetaId()),"",recetaManager.codigoReceta,recetaManager.nombreReceta,
                        recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getCodigo_ing(), recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getDescrip_ing(),(String) labelManager.olote.value,
                        (String) labelManager.ovenci.value,(String) labelManager.oturno.value,mainActivity.mainClass.BZA.getNetoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                        mainActivity.mainClass.BZA.getBrutoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),mainActivity.mainClass.BZA.getTaraDigital(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                        Utils.getFecha(),Utils.getHora(),(String) labelManager.ocampo1.value,(String) labelManager.ocampo2.value,
                        (String) labelManager.ocampo3.value,(String) labelManager.ocampo4.value,(String) labelManager.ocampo5.value,preferencesManager.getCampo1(),
                        preferencesManager.getCampo2(),preferencesManager.getCampo3(),preferencesManager.getCampo4(),preferencesManager.getCampo5(),usersManager.getUsuarioActual(),
                        recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilos_ing(), recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilos_reales_ing(),"","",String.valueOf(mainActivity.mainClass.N_BZA));

                if(recetaManager.pasoActual==recetaManager.listRecetaActual.size()){
                    float kilos=0;
                    for(int i = 0; i<recetaManager.listRecetaActual.size(); i++){
                        if(Utils.isNumeric(recetaManager.listRecetaActual.get(i).getKilos_reales_ing())){
                            kilos=kilos+Float.parseFloat(recetaManager.listRecetaActual.get(i).getKilos_reales_ing());
                        }
                    }
                    labelManager.oidreceta.value=String.valueOf(preferencesManager.getRecetaId());
                    form_sqlDb.actualizarNetoTotalReceta(mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(kilos))+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),String.valueOf(preferencesManager.getRecetaId()));
                    preferencesManager.setNetototal(mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(kilos)));
                    labelManager.onetototal.value = mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(kilos));
                }
                if(id==-1){
                    Utils.Mensaje("Error en base de datos, debe hacer un reset o actualizar programa",R.layout.item_customtoasterror,mainActivity);
                }else{
                    labelManager.oidpesada.value=String.valueOf(id);
                }

            }
        }
    }
    private void insertarNuevoPasoPedidoBatchSQL() {
        //agarrar id guardado_pesadas en memoria
        labelManager.oidreceta.value=preferencesManager.getPedidoId();
        if(preferencesManager.getPedidoId()>0){
            labelManager.oidreceta.value=preferencesManager.getPedidoId();
            try (FormSqlHelper form_sqlDb = new FormSqlHelper(getContext(), MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
                long id= form_sqlDb.insertarPesada("",
                   String.valueOf(preferencesManager.getPedidoId()),
                   recetaManager.codigoReceta,recetaManager.nombreReceta,
                        recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getCodigo_ing(),
                        recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getDescrip_ing(),
                   (String) labelManager.olote.value, (String) labelManager.ovenci.value,
                   (String) labelManager.oturno.value,mainActivity.mainClass.BZA.getNetoStr(mainActivity.mainClass.N_BZA),
                   mainActivity.mainClass.BZA.getBrutoStr(mainActivity.mainClass.N_BZA),
                   mainActivity.mainClass.BZA.getTaraDigital(mainActivity.mainClass.N_BZA),
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
                   recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilos_ing(),
                   recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilos_reales_ing(),
                   "","",String.valueOf(mainActivity.mainClass.N_BZA));

                if(recetaManager.pasoActual==recetaManager.listRecetaActual.size()){
                    float kilos=0;
                    for(int i = 0; i<recetaManager.listRecetaActual.size(); i++){
                        if(Utils.isNumeric(recetaManager.listRecetaActual.get(i).getKilos_reales_ing())){
                            kilos=kilos+Float.parseFloat(recetaManager.listRecetaActual.get(i).getKilos_reales_ing());
                        }
                    }
                    labelManager.oidreceta.value=preferencesManager.getPedidoId();
                    form_sqlDb.actualizarNetoTotalPedido(mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(kilos))+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),String.valueOf(preferencesManager.getPedidoId()));
                    preferencesManager.setNetototal(mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(kilos)));
                    labelManager.onetototal.value = mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(kilos));
                }
                if(id==-1){
                    Utils.Mensaje("Error en base de datos, debe hacer un reset o actualizar programa",R.layout.item_customtoasterror,mainActivity);
                }else{
                    labelManager.oidpesada.value=String.valueOf(id);
                }

            }
        }
    }

    private void recetaFinalizada() {
        requireActivity().runOnUiThread(() -> {
            recetaManager.estadoMensajeStr.setValue("FINALIZADO");
            detener();
        });
        restaurarDatos(aRealizarFinalizados());

    }

    private boolean aRealizarFinalizados() {
        boolean arealizarfinalizados=false;
        if(recetaManager.cantidad.getValue()!=null&&recetaManager.cantidad.getValue()>0&&recetaManager.realizadas.getValue()!=null&&(recetaManager.cantidad.getValue()-recetaManager.realizadas.getValue()>0)){
            recetaManager.realizadas.setValue(recetaManager.realizadas.getValue()+1);
            preferencesManager.setRealizadas(recetaManager.realizadas.getValue());
            if(recetaManager.cantidad.getValue()<=recetaManager.realizadas.getValue()){
                arealizarfinalizados=true;
            }
        }
        if(preferencesManager.getModoUso()==1||
                preferencesManager.getRecetacomopedidoCheckbox()){//receta como pedido
            recetaManager.realizadas.setValue(recetaManager.cantidad.getValue());
            preferencesManager.setRealizadas(recetaManager.cantidad.getValue());
            arealizarfinalizados=true;
        }
        return arealizarfinalizados;
    }

    private void detener() {
        labelManager.oidreceta.value="0";
        preferencesManager.setRecetaId(0);
        preferencesManager.setPedidoId(0);
        recetaManager.estado=0;
        preferencesManager.setEstado(0);
        recetaManager.ejecutando=false;
        preferencesManager.setEjecutando(false);
        binding.btStart.setBackgroundResource(R.drawable.boton__arranqueparada_selector);

    }

    private void restaurarDatos(boolean arealizarfinalizados) {
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


    private void actualizarVistas() {
        binding.tvNeto.setText(mainActivity.mainClass.BZA.getNetoStr(mainActivity.mainClass.N_BZA));
        binding.tvBruto.setText(mainActivity.mainClass.BZA.getBrutoStr(mainActivity.mainClass.N_BZA));
        if(!recetaManager.automatico&&botonera==0) bt_2.setText("PESAR");
        labelManager.oturno.value=mainActivity.mainClass.devuelveTurnoActual();
        if(!labelManager.olote.value.equals("")&&
                !labelManager.oturno.value.equals("")&&
                !labelManager.ovenci.value.equals("")){
            binding.tvDatos.setText(labelManager.olote.value+" | "+labelManager.oturno.value+ " ...");
        }else{
            binding.tvDatos.setText("Toque para ingresar los datos");
        }
        if(mainActivity.mainClass.BZA.getEstable(mainActivity.mainClass.N_BZA)){
            binding.imEstable.setVisibility(View.VISIBLE);
        }else{
            binding.imEstable.setVisibility(View.INVISIBLE);
        }
    }


    private void procesoDetenido() {
        if(recetaManager.recetaActual.isEmpty()){
            binding.tvRecetaActual.setText("Seleccione Receta");
        }
        if(botonera==0){
            binding.lnFondolayout.setBackgroundResource(R.drawable.boton_selector_balanza);
        }else{
            binding.lnFondolayout.setBackgroundResource(R.drawable.boton_selector_balanza_seleccionado);
        }
        binding.tvNumpaso.setText("-");
        setupProgressBarPasoStyle(Color.BLACK);
        binding.progressBar.setProgress(0);
        binding.imRango.setVisibility(View.INVISIBLE);
        setupViewsColor(Color.BLACK,R.color.negro,R.drawable.estable);
        setupTaraView(R.drawable.tare_black);
        setupLabelDescripcionPaso();
    }


    private void setupLabelDescripcionPaso() {
        boolean empezar= (labelManager.olote.value != "" || labelManager.oturno.value != "" ||
                labelManager.ovenci.value != "") && !recetaManager.recetaActual.isEmpty();
        if(empezar){
            recetaManager.estadoMensajeStr.setValue("Listo para comenzar");
            setupProgressBarStyle(R.drawable.progress,Color.BLACK);
            binding.tvNumpaso.setText("-");
        }else{
            recetaManager.estadoMensajeStr.setValue("Proceso detenido");
        }
    }

    private void procesoEjecucion() {
        if(recetaManager.automatico){
            procesoEjecucionAutomatico();
        }else{
            procesoEjecucionManual();
        }

    }

    private void procesoEjecucionAutomatico() {
        int estado=mainClass.BZA.Itw410FrmGetEstado(mainClass.N_BZA);
        bt_2.setText("PAUSAR");
        switch (estado){
            case 0:
                automaticoFinalizado();
                break;
            case 1:
                procesoEjecucionManual();
                break;
            case 2:
                bt_2.setText("REANUDAR");
                recetaManager.estadoMensajeStr.setValue("Proceso automatico en pausa...");
                break;
            case 3:
                recetaManager.estadoMensajeStr.setValue("Estabilizando...");
                break;
            default:
                break;
        }
    }

    private void automaticoFinalizado() {
        recetaManager.automatico=false;
        preferencesManager.setAutomatico(false);
        int nuevoIndice=mainClass.BZA.Itw410FrmGetUltimoIndice(mainClass.N_BZA);
        if(nuevoIndice>preferencesManager.getIndice()){
            String ultimoPeso=mainClass.BZA.Itw410FrmGetUltimoPeso(mainClass.N_BZA);
            preferencesManager.setIndice(nuevoIndice);
            if(ultimoPeso!=null&&isNumeric(ultimoPeso)) btPesar(Float.parseFloat(ultimoPeso),ultimoPeso);
        }
    }

    private void procesoEjecucionManual() {
        binding.tvNumpaso.setText(recetaManager.pasoActual +"/"+recetaManager.listRecetaActual.size());
        if(recetaManager.estado==2){
            float kilosTotales=0;
            for(int i = 0; i<recetaManager.listRecetaActual.size(); i++){
                if(Utils.isNumeric(recetaManager.listRecetaActual.get(i).getKilos_reales_ing())){
                    kilosTotales=kilosTotales+Float.parseFloat(recetaManager.listRecetaActual.get(i).getKilos_reales_ing());
                }
            }
            recetaManager.netoTotal.setValue(mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(kilosTotales)));
            if(recetaManager.pasoActual<=recetaManager.listRecetaActual.size()){
                String setPointstr= recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getKilos_ing();
                if(Utils.isNumeric(setPointstr)){
                    labelManager.okilos.value=setPointstr;
                    recetaManager.setPoint=Float.parseFloat(setPointstr);
                    float lim_max=((100+Integer.parseInt(preferencesManager.getTolerancia()))*recetaManager.setPoint)/100;
                    float lim_min=((100-Integer.parseInt(preferencesManager.getTolerancia()))*recetaManager.setPoint)/100;
                    actualizarBarraProgreso(lim_max);
                    actualizarDisplayPeso(lim_min,lim_max);
                }
                int num=determinarBalanza();
                actualizarBarraProceso(num);

                labelManager.oingredientes.value= recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getDescrip_ing();
                labelManager.ocodigoingrediente.value= recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getCodigo_ing();
            }

        }
    }

    private void actualizarBarraProceso(int num) {
        if(Utils.isNumeric(recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getKilos_ing())){
            if(Float.parseFloat(recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getKilos_ing())==0){
                String texto="Ingrese "+ recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getDescrip_ing() +" en balanza "+ num;
                if(recetaManager.automatico){
                    texto="Cargando "+ recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getDescrip_ing() +" en salida "+ preferencesManager.getSalida();
                }
                recetaManager.estadoMensajeStr.setValue(texto);
            }else{
                String texto="Ingrese "+ recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getKilos_ing() +
                        mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA)+ " de "+ recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getDescrip_ing() +
                        " en balanza "+ num;
                if(recetaManager.automatico){
                    texto="Cargando "+ recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getKilos_ing() +
                            mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA)+ " de "+ recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getDescrip_ing() +
                            " en salida "+ preferencesManager.getSalida();
                }
                recetaManager.estadoMensajeStr.setValue(texto);
            }
        }
    }

    private void actualizarDisplayPeso(Float lim_min, Float lim_max) {
        float pesoNeto = mainActivity.mainClass.BZA.getNeto(mainActivity.mainClass.N_BZA);
        if(pesoNeto>=lim_min&&pesoNeto<=lim_max)setupViewDisplay(R.drawable.boton_selector_balanza_enrango,R.drawable.flecha_abajoblanca,View.INVISIBLE,1);
        if(pesoNeto<lim_min)setupViewDisplay(R.drawable.boton_selector_balanza_fuerarango,R.drawable.flecha_arribablanca,View.VISIBLE,0);
        if(pesoNeto>lim_max)setupViewDisplay(R.drawable.boton_selector_balanza_fuerarango,R.drawable.flecha_abajoblanca,View.VISIBLE,2);
        setupTaraView(R.drawable.tare_white);
    }

    private void setupViewDisplay(int boton_selector, int flecha, int visibility,int range) {
        binding.lnFondolayout.setBackgroundResource(boton_selector);
        binding.imRango.setBackgroundResource(flecha);
        binding.imRango.setVisibility(visibility);
        setupViewsColor(Color.WHITE,R.color.blanco,R.drawable.estable_blanco);
        rango=range;
    }

    private void setupTaraView(int color) {
        if(Utils.isNumeric(mainActivity.mainClass.BZA.getTaraDigital(mainActivity.mainClass.N_BZA))){
            if(mainActivity.mainClass.BZA.getTara(mainActivity.mainClass.N_BZA)!=0||
                    Float.parseFloat(mainActivity.mainClass.BZA.getTaraDigital(mainActivity.mainClass.N_BZA))!=0){
                binding.imTare.setImageDrawable(ContextCompat.getDrawable(requireContext(),color));
                binding.imTare.setVisibility(View.VISIBLE);
            }else{
                binding.imTare.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void setupViewsColor(int Color, int Rcolor, int estable) {
        binding.tvNeto.setTextColor(Color);
        binding.tvBruto.setTextColor(Color);
        binding.lnLinea.setBackgroundResource(Rcolor);
        binding.tvNetoUnidad.setTextColor(Color);
        binding.tvBrutoUnidad.setTextColor(Color);
        binding.imEstable.setImageDrawable(ContextCompat.getDrawable(requireContext(),estable));
    }


    private void actualizarBarraProgreso(Float lim_max) {
        if(mainActivity.mainClass.BZA.getNeto(mainActivity.mainClass.N_BZA)>=0){
            float porcentaje= 100*mainActivity.mainClass.BZA.getNeto(mainActivity.mainClass.N_BZA)/recetaManager.setPoint;
            int progresoRango=R.drawable.progress;
            int progresoFuera=R.drawable.progress2;
            if(recetaManager.automatico) progresoRango=R.drawable.progressautomatico;
            if(recetaManager.automatico) progresoFuera=R.drawable.progressautomatico2;
            if(porcentaje<=100){
                binding.progressBar.setProgress((int) porcentaje);
                setupProgressBarStyle(progresoRango,Color.BLACK);
            }else{
                if(mainActivity.mainClass.BZA.getNeto(mainActivity.mainClass.N_BZA)>lim_max){
                    setupProgressBarStyle(progresoFuera,Color.WHITE);
                }else{
                    setupProgressBarStyle(progresoRango,Color.BLACK);
                }
                binding.progressBar.setProgress(100);
            }

        }else{
            binding.progressBar.setProgress(0);
            setupProgressBarPasoStyle(Color.BLACK);
        }
    }

    private void setupProgressBarStyle(int progress, int black) {
        Drawable draw = ResourcesCompat.getDrawable(getResources(), progress, null);
        binding.progressBar.setProgressDrawable(draw);
        setupProgressBarPasoStyle(black);

    }

    private void setupProgressBarPasoStyle(int black) {
        binding.tvEstado.setTextColor(black);
        binding.tvNumpaso.setTextColor(black);
        int fondoNumPaso=R.drawable.stylekeycortransparent;
        if(black==Color.WHITE)fondoNumPaso=R.drawable.stylekeycortransparentclaro;
        binding.lnNumpaso.setBackgroundResource(fondoNumPaso);
    }

    private int determinarBalanza() {
        String kilos = recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getKilos_ing();
        int modo = preferencesManager.getModoBalanza();
        int num = 0;
        if (Utils.isNumeric(kilos)) {
            float kilosFloat = Float.parseFloat(kilos);
            float bza1Limite = Float.parseFloat(preferencesManager.getBza1Limite());
            float bza2Limite = Float.parseFloat(preferencesManager.getBza2Limite());
            if (kilosFloat > bza2Limite && modo > 1) {
                num = 3;
            } else if (kilosFloat < bza2Limite && modo > 0) {
                num = 2;
                mainActivity.mainClass.N_BZA = 2;
            } else if (kilosFloat < bza1Limite) {
                num = 1;
                mainActivity.mainClass.N_BZA = 1;
            }
        }
        if (num == 0) {
            num = determinarBalanzaPorModo(modo);
        }

        return num;
    }

    private int determinarBalanzaPorModo(int modo) {
        int num = 0;
        switch (modo) {
            case 0:
                num = 1;
                mainActivity.mainClass.N_BZA = 1;
                break;
            case 1:
                num = 2;
                mainActivity.mainClass.N_BZA = 2;
                break;
            case 2:
                num = 3;
                break;
        }
        return num;
    }

    @Override
    public void onDestroyView() {
        stoped=true;
        super.onDestroyView();
    }


}
