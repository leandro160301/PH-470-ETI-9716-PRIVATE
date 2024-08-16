package com.jws.jwsapi.feature.formulador.ui.fragment;

import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.TecladoFlotanteConCancelar;
import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.dialogoCheckboxVisibilidad;
import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.dialogoTexto;
import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.dialogoTextoConCancelar;
import static com.jws.jwsapi.utils.Utils.format;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
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
import com.jws.jwsapi.databinding.ProgFormuladorPrincipalBinding;
import com.jws.jwsapi.feature.formulador.MainFormClass;
import com.jws.jwsapi.feature.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.feature.formulador.di.RecetaManager;
import com.jws.jwsapi.feature.formulador.models.Form_Model_Receta;
import com.jws.jwsapi.feature.formulador.data.sql.Form_SQL_db;
import com.jws.jwsapi.R;
import com.jws.jwsapi.feature.formulador.ui.dialog.DialogCheckboxInterface;
import com.jws.jwsapi.feature.formulador.ui.viewmodel.Form_PrincipalViewModel;
import com.jws.jwsapi.utils.Utils;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class Form_Principal extends Fragment  {
    @Inject
    RecetaManager recetaManager;
    Form_PrincipalViewModel viewModel;
    Handler mHandler= new Handler();
    MainActivity mainActivity;
    private ButtonProvider_Principal buttonProvider;
    boolean stoped=false;
    String campo1="";
    String campo2="";
    String campo3="";
    String campo4="";
    String campo5="";
    String unidad="kg";
    public int rango=0; //0=bajo, 1=acepto,2=alto
    int botonera=0;
    Float setPoint=0f;
    MainFormClass mainClass;
    ProgFormuladorPrincipalBinding binding;
    PreferencesManager preferencesManager;


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
        preferencesManager=new PreferencesManager(mainActivity);
        initializeViewModel();
        observeViewModel();
        setOnClickListeners();
        configuracionBotones();
        GET_PESO_cal_bza.run();

    }

    private void observeViewModel() {
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
        viewModel = new ViewModelProvider(requireActivity()).get(Form_PrincipalViewModel.class);
        /*usamos requireActivity para siempre usar la misma instancia (Form_PrincipalViewModel es el
        programa y debe estar ejecutandose independientemente del programa)*/
        mainActivity.mainClass.opaso.value=String.valueOf(recetaManager.pasoActual);
        mainActivity.mainClass.ocodigoreceta.value= recetaManager.codigoReceta;
        mainActivity.mainClass.oreceta.value= recetaManager.nombreReceta;

        initializePreferences();

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

        if(!Objects.equals(recetaManager.recetaActual, "")){
            String[]arr= recetaManager.recetaActual.split("_");
            if(arr.length==3){
                binding.tvCodigoingrediente.setText(arr[1].replace("_","")+" "+arr[2].replace("_",""));
            }
        }
        setupUnidad();
        binding.imEstable.setVisibility(View.INVISIBLE);

    }

    private void initializePreferences() {
        campo1=preferencesManager.getCampo1();
        campo2=preferencesManager.getCampo2();
        campo3=preferencesManager.getCampo3();
        campo4=preferencesManager.getCampo4();
        campo5=preferencesManager.getCampo5();
    }

    private void setupUnidad() {
        unidad=mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA);
        binding.tvBrutoUnidad.setText(unidad);
        binding.tvNetoUnidad.setText(unidad);
        binding.tvNetototalUnidad.setText(unidad);
    }

    private void setOnClickListeners() {
        binding.btDatos.setOnClickListener(view -> mainActivity.mainClass.openFragment(new Form_Fragment_IngresoDatos()));
        binding.tvDatos.setOnClickListener(view -> mainActivity.mainClass.openFragment(new Form_Fragment_IngresoDatos()));
        binding.btCodigo.setOnClickListener(view -> mainActivity.mainClass.openFragment(new Form_Fragment_Recetas()));
        binding.tvCodigoingrediente.setOnClickListener(view -> mainActivity.mainClass.openFragment(new Form_Fragment_Recetas()));
    }

    private void iniciarReceta() {
        if(mainActivity.mainClass.olote.value==""&&preferencesManager.getModoLote()==2)nuevoLoteNumerico();


        if(preferencesManager.getModoLote()==1){
            if(mainActivity.mainClass.olote.value==""){
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
        if(mainActivity.mainClass.olote.value==""||
                mainActivity.mainClass.ovenci.value==""||faltanCampos()){
            Utils.Mensaje("Faltan ingresar datos",R.layout.item_customtoasterror,mainActivity);
            empezar=false;
        }
        if(recetaManager.recetaActual.equals("")){
            Utils.Mensaje("Debe seleccionar una receta para comenzar",R.layout.item_customtoasterror,mainActivity);
            empezar=false;
        }
        return empezar;
    }

    private boolean faltanCampos() {
        String[] campos = {campo1, campo2, campo3, campo4, campo5};
        String[] valores = {
                (String)mainActivity.mainClass.ocampo1.value,
                (String)mainActivity.mainClass.ocampo2.value,
                (String)mainActivity.mainClass.ocampo3.value,
                (String)mainActivity.mainClass.ocampo4.value,
                (String)mainActivity.mainClass.ocampo5.value
        };
        for (int i = 0; i < campos.length; i++) {
            if (!campos[i].isEmpty() && valores[i].isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private void guardarDatosEnMemoria() {
        preferencesManager.setLote((String)mainActivity.mainClass.olote.value);
        preferencesManager.setVencimiento((String)mainActivity.mainClass.ovenci.value);
        preferencesManager.setCampo1Valor((String)mainActivity.mainClass.ocampo1.value);
        preferencesManager.setCampo2Valor((String)mainActivity.mainClass.ocampo2.value);
        preferencesManager.setCampo3Valor((String)mainActivity.mainClass.ocampo3.value);
        preferencesManager.setCampo4Valor((String)mainActivity.mainClass.ocampo4.value);
        preferencesManager.setCampo5Valor((String)mainActivity.mainClass.ocampo5.value);
    }

    private void nuevoLoteNumerico() {
        mainActivity.mainClass.olote.value=String.valueOf(preferencesManager.getLoteAutomatico()+1);
        preferencesManager.setLoteAutomatico(preferencesManager.getLoteAutomatico()+1);
    }

    private void nuevoLoteFecha() {
        String nuevolote=Utils.getFechaDDMMYYYY().replace("/","");
        mainActivity.mainClass.olote.value=nuevolote;
        try (Form_SQL_db form_sql_db = new Form_SQL_db(getContext(), MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
            int ultimo=form_sql_db.getLastLote(nuevolote);
            mainActivity.mainClass.olote.value=nuevolote+"-"+ (ultimo + 1);
        }
    }

    private void verificarNuevoLoteFecha() {
        String nuevolote=Utils.getFechaDDMMYYYY().replace("/","");
        try (Form_SQL_db form_sql_db = new Form_SQL_db(getContext(), MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
            int ultimo=form_sql_db.getLastLote(nuevolote);
            if(ultimo==0){
                mainActivity.mainClass.olote.value=nuevolote+"-1";
            }
        }
    }

    private void ejecutarReceta() {
        recetaManager.listRecetaActual =new ArrayList<>();
        recetaManager.listRecetaActual = mainActivity.mainClass.getReceta(recetaManager.recetaActual);
        preferencesManager.setPasosRecetaActual(recetaManager.listRecetaActual);
        if(recetaManager.listRecetaActual.size()>0){
            int mododeuso=preferencesManager.getModoUso();
            if(mododeuso==0){
                setupModoUso(false);
            }
            if(mododeuso==1){
                setupModoUso(true);
            }
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
            List<Form_Model_Receta> nuevareceta=new ArrayList<>();
            for(int i = 0; i<recetaManager.listRecetaActual.size(); i++){
                for(int k = 0; k<recetaManager.cantidad.getValue(); k++){
                    Form_Model_Receta nuevaInstancia = new Form_Model_Receta(
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
            if(recetaManager.realizadas.getValue()!=null&&recetaManager.realizadas.getValue()==0||recetaManager.realizadas.getValue()-recetaManager.cantidad.getValue()==0){
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
        mainActivity.mainClass.onetototal.value="0";
        recetaManager.netoTotal.setValue("0");
        preferencesManager.setNetototal("0");
        mainActivity.mainClass.onetototal.value = "0";
        binding.btStart.setBackgroundResource(R.drawable.circlebuttonon1);
        recetaManager.pasoActual=1;
        mainActivity.mainClass.opaso.value=recetaManager.pasoActual;
        preferencesManager.setPasoActual(recetaManager.pasoActual);
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
        dialogoCheckboxVisibilidad(null, text, mainActivity, (DialogCheckboxInterface) (texto, checkbox) -> {
            if(Float.parseFloat(texto)>0){
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
            Button bt_2 = buttonProvider.getButton2();
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
                    mainActivity.mainClass.ImprimirUltima();
                    view.postDelayed(() -> bt_3.setClickable(true), 2000);
                }

            });

        }
    }



    private void configuracionBotones() {
        if (buttonProvider != null) {
            Button bt_1 = buttonProvider.getButton1();
            Button bt_2 = buttonProvider.getButton2();
            Button bt_3 = buttonProvider.getButton3();
            Button bt_4 = buttonProvider.getButton4();
            Button bt_5 = buttonProvider.getButton5();

            bt_1.setText("CANTIDAD");
            bt_2.setText("PESAR");
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
            bt_2.setOnClickListener(view -> btPesar());
            bt_3.setOnClickListener(view -> mainActivity.mainClass.openFragment(new Form_Fragment_Guardados()));
            bt_4.setOnClickListener(view -> mainActivity.mainClass.openFragment(new Form_Fragment_Recetas()));
            bt_5.setOnClickListener(view -> mainActivity.mainClass.openFragment(new Form_Fragment_Ingredientes()));

        }
    }

    private void btCantidad() {
        if(!recetaManager.ejecutando){
            IngresaCantidad();
        }else{
            Utils.Mensaje("No puede cambiar la cantidad cuando esta ejecutando una receta",R.layout.item_customtoasterror,mainActivity);
        }
    }

    private void btPesar() {
        if(recetaManager.ejecutando){
            if(recetaManager.pasoActual<=recetaManager.listRecetaActual.size()){
                if(rango==1||preferencesManager.getContinuarFueraRango()){
                    calculaPorcentajeError();
                    manejoBasedeDatoseImpresion();
                }else{
                    Utils.Mensaje("Ingrediente fuera de rango",R.layout.item_customtoasterror,mainActivity);
                }
            }else{
                recetaFinalizada();
            }
        }
    }

    private void calculaPorcentajeError() {
        String netoactual=mainActivity.mainClass.BZA.getNetoStr(mainActivity.mainClass.N_BZA);
        recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).setKilos_reales_ing(netoactual);
        mainActivity.mainClass.okilosreales.value=netoactual+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA);
        float porcentaje=((mainActivity.mainClass.BZA.getNeto(mainActivity.mainClass.N_BZA)*100)/setPoint)-100;
        if(porcentaje<0){
            porcentaje=porcentaje*(-1);
        }
        recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).setTolerancia_ing(format(String.valueOf(porcentaje),2) + "%");
    }

    private void manejoBasedeDatoseImpresion() {
        imprimeyGuardaPrimerPasoRecetaBatch();
        imprimeyGuardaNuevoPasoRecetaBatch();
        imprimeyGuardaPrimerPasoPedidoBatch();
        imprimeyGuardaNuevoPasoPedidoBatch();
        if(recetaManager.pasoActual<recetaManager.listRecetaActual.size()){
            nuevoPaso();
        }else{
            imprimirEtiquetaFinal();
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
            mainActivity.mainClass.Imprimir(0);
        }
    }

    private void imprimirEtiquetaFinal() {
        mainActivity.mainClass.onetototal.value=recetaManager.netoTotal;
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext(),R.style.AlertDialogCustom);
        View mView = getLayoutInflater().inflate(R.layout.dialogo_transferenciaarchivo, null);
        TextView textView = mView.findViewById(R.id.textView);
        textView.setText("Imprimiendo etiqueta final");
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        Runnable myRunnable = () -> {
            int netiqueta = 0;
            for (int i = 0; i < recetaManager.listRecetaActual.size(); i += 5) {
                if (i == 0) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
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
                setupVariablesEtiqueta(pasos,ingredientes,codingredientes,kilos,netiqueta);
                mainActivity.mainClass.Imprimir(1);

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            requireActivity().runOnUiThread(dialog::cancel);
            recetaFinalizada();
        };
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.execute(myRunnable);
        executorService.shutdown();
    }

    private void setupVariablesEtiqueta(String[] pasos, String[] ingredientes, String[] codingredientes, String[] kilos, int netiqueta) {
        mainActivity.mainClass.opaso1.value = pasos[0];
        mainActivity.mainClass.opaso2.value = pasos[1];
        mainActivity.mainClass.opaso3.value = pasos[2];
        mainActivity.mainClass.opaso4.value = pasos[3];
        mainActivity.mainClass.opaso5.value = pasos[4];
        mainActivity.mainClass.oingrediente1.value = ingredientes[0];
        mainActivity.mainClass.oingrediente2.value = ingredientes[1];
        mainActivity.mainClass.oingrediente3.value = ingredientes[2];
        mainActivity.mainClass.oingrediente4.value = ingredientes[3];
        mainActivity.mainClass.oingrediente5.value = ingredientes[4];
        mainActivity.mainClass.ocodigoingrediente1.value = codingredientes[0];
        mainActivity.mainClass.ocodigoingrediente2.value = codingredientes[1];
        mainActivity.mainClass.ocodigoingrediente3.value = codingredientes[2];
        mainActivity.mainClass.ocodigoingrediente4.value = codingredientes[3];
        mainActivity.mainClass.ocodigoingrediente5.value = codingredientes[4];
        mainActivity.mainClass.opeso1.value = kilos[0];
        mainActivity.mainClass.opeso2.value = kilos[1];
        mainActivity.mainClass.opeso3.value = kilos[2];
        mainActivity.mainClass.opeso4.value = kilos[3];
        mainActivity.mainClass.opeso5.value = kilos[4];
        mainActivity.mainClass.onumeroetiqueta.value = String.valueOf(netiqueta);

    }

    private void nuevoPaso() {
        recetaManager.pasoActual=recetaManager.pasoActual+1;
        mainActivity.mainClass.opaso.value=recetaManager.pasoActual;
        preferencesManager.setPasoActual(recetaManager.pasoActual);
        if(Utils.isNumeric(String.valueOf(recetaManager.netoTotal))){
            Float resultado= Float.parseFloat(String.valueOf(recetaManager.netoTotal))+mainActivity.mainClass.BZA.getNeto(mainActivity.mainClass.N_BZA);
            recetaManager.netoTotal.setValue(mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(resultado)));
            preferencesManager.setNetototal(String.valueOf(recetaManager.netoTotal));
            mainActivity.mainClass.onetototal.value = String.valueOf(recetaManager.netoTotal);
        }
        preferencesManager.setPasosRecetaActual(recetaManager.listRecetaActual);
        if(preferencesManager.getRecipientexPaso()){
            IngresoRecipiente();
        }else{
            mainActivity.mainClass.BZA.setTaraDigital(mainActivity.mainClass.N_BZA,mainActivity.mainClass.BZA.getBruto(mainActivity.mainClass.N_BZA));
            //tarar
        }

    }

    private void insertarPrimerPasoRecetaBatchSQL() {//guardar en receta una nueva y que devuelva el id
        mainActivity.mainClass.oidreceta.value=String.valueOf(preferencesManager.getRecetaId());
        if(preferencesManager.getRecetaId()==0){
            try (Form_SQL_db form_sql_db = new Form_SQL_db(getContext(), MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
                long id= sqlRecetaBatch(form_sql_db);
                if(id>-1){
                    mainActivity.mainClass.oidreceta.value=String.valueOf(id);
                    preferencesManager.setRecetaId(id);
                    long id2=sqlPrimerPasoPesadaBatch(form_sql_db,id);
                    if(id2!=-1){
                        mainActivity.mainClass.oidpesada.value=String.valueOf(id2);
                    }
                }else{
                    Utils.Mensaje("Error en base de datos, debe hacer un reset o actualizar programa",R.layout.item_customtoasterror,mainActivity);
                }
            }
        }

    }

    private long sqlPrimerPasoPesadaBatch(Form_SQL_db form_sql_db, long id) {
        return form_sql_db.insertarPesada(String.valueOf(id),"",recetaManager.codigoReceta,recetaManager.nombreReceta,
                recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getCodigo_ing(), recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getDescrip_ing(),(String) mainActivity.mainClass.olote.value,
                (String) mainActivity.mainClass.ovenci.value,(String) mainActivity.mainClass.oturno.value,mainActivity.mainClass.BZA.getNetoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                mainActivity.mainClass.BZA.getBrutoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),mainActivity.mainClass.BZA.getTaraDigital(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                Utils.getFecha(),Utils.getHora(),(String) mainActivity.mainClass.ocampo1.value,(String) mainActivity.mainClass.ocampo2.value,
                (String) mainActivity.mainClass.ocampo3.value,(String) mainActivity.mainClass.ocampo4.value,(String) mainActivity.mainClass.ocampo5.value,preferencesManager.getCampo1(),
                preferencesManager.getCampo2(),preferencesManager.getCampo3(),preferencesManager.getCampo4(),preferencesManager.getCampo5(),mainActivity.getUsuarioActual(),
                recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilos_ing() +mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA), recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilos_reales_ing() +mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),"","",String.valueOf(mainActivity.mainClass.N_BZA));
    }

    private long sqlRecetaBatch(Form_SQL_db form_sql_db) {
        return form_sql_db.insertarReceta(recetaManager.codigoReceta,recetaManager.nombreReceta, (String) mainActivity.mainClass.olote.value,
                (String) mainActivity.mainClass.ovenci.value,(String) mainActivity.mainClass.oturno.value,mainActivity.mainClass.BZA.getNetoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                mainActivity.mainClass.BZA.getBrutoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),mainActivity.mainClass.BZA.getTaraDigital(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                Utils.getFecha(),Utils.getHora(),(String) mainActivity.mainClass.ocampo1.value,(String) mainActivity.mainClass.ocampo2.value,
                (String) mainActivity.mainClass.ocampo3.value,(String) mainActivity.mainClass.ocampo4.value,(String) mainActivity.mainClass.ocampo5.value,preferencesManager.getCampo1(),
                preferencesManager.getCampo2(),preferencesManager.getCampo3(),preferencesManager.getCampo4(),preferencesManager.getCampo5(),mainActivity.getUsuarioActual(),
                recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilos_totales() +mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),"","",String.valueOf(mainActivity.mainClass.N_BZA));
    }

    private void insertarPrimerPasoPedidoBatchSQL() {//guardar en receta una nueva y que devuelva el id
        mainActivity.mainClass.oidreceta.value=preferencesManager.getPedidoId();
        if(preferencesManager.getPedidoId()==0){
            try (Form_SQL_db form_sql_db = new Form_SQL_db(getContext(), MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
                long id=form_sql_db.insertarPedido(recetaManager.codigoReceta,recetaManager.nombreReceta, (String) mainActivity.mainClass.olote.value,
                        (String) mainActivity.mainClass.ovenci.value,(String) mainActivity.mainClass.oturno.value,mainActivity.mainClass.BZA.getNetoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                        mainActivity.mainClass.BZA.getBrutoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),mainActivity.mainClass.BZA.getTaraDigital(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                        Utils.getFecha(),Utils.getHora(),(String) mainActivity.mainClass.ocampo1.value,(String) mainActivity.mainClass.ocampo2.value,
                        (String) mainActivity.mainClass.ocampo3.value,(String) mainActivity.mainClass.ocampo4.value,(String) mainActivity.mainClass.ocampo5.value,preferencesManager.getCampo1(),
                        preferencesManager.getCampo2(),preferencesManager.getCampo3(),preferencesManager.getCampo4(),preferencesManager.getCampo5(),mainActivity.getUsuarioActual(),
                        recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilos_totales() +mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),"","",String.valueOf(mainActivity.mainClass.N_BZA));
                if(id>-1){
                    mainActivity.mainClass.oidreceta.value=String.valueOf(id);
                    preferencesManager.setPedidoId(id);
                    long id2=form_sql_db.insertarPesada("",String.valueOf(id),recetaManager.codigoReceta,recetaManager.nombreReceta,
                            recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getCodigo_ing(), recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getDescrip_ing(),(String) mainActivity.mainClass.olote.value,
                            (String) mainActivity.mainClass.ovenci.value,(String) mainActivity.mainClass.oturno.value,mainActivity.mainClass.BZA.getNetoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                            mainActivity.mainClass.BZA.getBrutoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),mainActivity.mainClass.BZA.getTaraDigital(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                            Utils.getFecha(),Utils.getHora(),(String) mainActivity.mainClass.ocampo1.value,(String) mainActivity.mainClass.ocampo2.value,
                            (String) mainActivity.mainClass.ocampo3.value,(String) mainActivity.mainClass.ocampo4.value,(String) mainActivity.mainClass.ocampo5.value,preferencesManager.getCampo1(),
                            preferencesManager.getCampo2(),preferencesManager.getCampo3(),preferencesManager.getCampo4(),preferencesManager.getCampo5(),mainActivity.getUsuarioActual(),
                            recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilos_ing() +mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA), recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilos_reales_ing() +mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),"","",String.valueOf(mainActivity.mainClass.N_BZA));
                    if(id2!=-1){
                        mainActivity.mainClass.oidpesada.value=String.valueOf(id2);
                    }else{
                        Utils.Mensaje("Error en base de datos pesada, debe hacer un reset o actualizar programa",R.layout.item_customtoasterror,mainActivity);
                    }

                }else{
                    Utils.Mensaje("Error en base de datos pedido, debe hacer un reset o actualizar programa",R.layout.item_customtoasterror,mainActivity);
                }
            }
        }

    }

    private void insertarNuevoPasoRecetaBatchSQL() {
        //agarrar id guardado_pesadas en memoria
        mainActivity.mainClass.oidreceta.value=String.valueOf(preferencesManager.getRecetaId());
        if(preferencesManager.getRecetaId()>0){
            try (Form_SQL_db form_sql_db = new Form_SQL_db(getContext(), MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
                mainActivity.mainClass.oidreceta.value=String.valueOf(preferencesManager.getRecetaId());
                long id=form_sql_db.insertarPesada(String.valueOf(preferencesManager.getRecetaId()),"",recetaManager.codigoReceta,recetaManager.nombreReceta,
                        recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getCodigo_ing(), recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getDescrip_ing(),(String) mainActivity.mainClass.olote.value,
                        (String) mainActivity.mainClass.ovenci.value,(String) mainActivity.mainClass.oturno.value,mainActivity.mainClass.BZA.getNetoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                        mainActivity.mainClass.BZA.getBrutoStr(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),mainActivity.mainClass.BZA.getTaraDigital(mainActivity.mainClass.N_BZA)+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),
                        Utils.getFecha(),Utils.getHora(),(String) mainActivity.mainClass.ocampo1.value,(String) mainActivity.mainClass.ocampo2.value,
                        (String) mainActivity.mainClass.ocampo3.value,(String) mainActivity.mainClass.ocampo4.value,(String) mainActivity.mainClass.ocampo5.value,preferencesManager.getCampo1(),
                        preferencesManager.getCampo2(),preferencesManager.getCampo3(),preferencesManager.getCampo4(),preferencesManager.getCampo5(),mainActivity.getUsuarioActual(),
                        recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilos_ing(), recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getKilos_reales_ing(),"","",String.valueOf(mainActivity.mainClass.N_BZA));

                if(recetaManager.pasoActual==recetaManager.listRecetaActual.size()){
                    float kilos=0;
                    for(int i = 0; i<recetaManager.listRecetaActual.size(); i++){
                        if(Utils.isNumeric(recetaManager.listRecetaActual.get(i).getKilos_reales_ing())){
                            kilos=kilos+Float.parseFloat(recetaManager.listRecetaActual.get(i).getKilos_reales_ing());
                        }
                    }
                    mainActivity.mainClass.oidreceta.value=String.valueOf(preferencesManager.getRecetaId());
                    form_sql_db.actualizarNetoTotalReceta(mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(kilos))+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),String.valueOf(preferencesManager.getRecetaId()));
                    preferencesManager.setNetototal(mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(kilos)));
                    mainActivity.mainClass.onetototal.value = mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(kilos));
                }
                if(id==-1){
                    Utils.Mensaje("Error en base de datos, debe hacer un reset o actualizar programa",R.layout.item_customtoasterror,mainActivity);
                }else{
                    mainActivity.mainClass.oidpesada.value=String.valueOf(id);
                }

            }
        }
    }
    private void insertarNuevoPasoPedidoBatchSQL() {
        //agarrar id guardado_pesadas en memoria
        mainActivity.mainClass.oidreceta.value=preferencesManager.getPedidoId();
        if(preferencesManager.getPedidoId()>0){
            mainActivity.mainClass.oidreceta.value=preferencesManager.getPedidoId();
            try (Form_SQL_db form_sql_db = new Form_SQL_db(getContext(), MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
                long id=form_sql_db.insertarPesada("",
                   String.valueOf(preferencesManager.getPedidoId()),
                   recetaManager.codigoReceta,recetaManager.nombreReceta,
                        recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getCodigo_ing(),
                        recetaManager.listRecetaActual.get(preferencesManager.getPasoActual() - 1).getDescrip_ing(),
                   (String) mainActivity.mainClass.olote.value, (String) mainActivity.mainClass.ovenci.value,
                   (String) mainActivity.mainClass.oturno.value,mainActivity.mainClass.BZA.getNetoStr(mainActivity.mainClass.N_BZA),
                   mainActivity.mainClass.BZA.getBrutoStr(mainActivity.mainClass.N_BZA),
                   mainActivity.mainClass.BZA.getTaraDigital(mainActivity.mainClass.N_BZA),
                   Utils.getFecha(),Utils.getHora(),(String) mainActivity.mainClass.ocampo1.value,
                   (String) mainActivity.mainClass.ocampo2.value,
                   (String) mainActivity.mainClass.ocampo3.value,
                   (String) mainActivity.mainClass.ocampo4.value,
                   (String) mainActivity.mainClass.ocampo5.value,
                   preferencesManager.getCampo1(),
                   preferencesManager.getCampo2(),
                   preferencesManager.getCampo3(),
                   preferencesManager.getCampo4(),
                   preferencesManager.getCampo5(),
                   mainActivity.getUsuarioActual(),
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
                    mainActivity.mainClass.oidreceta.value=preferencesManager.getPedidoId();
                    form_sql_db.actualizarNetoTotalPedido(mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(kilos))+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),String.valueOf(preferencesManager.getPedidoId()));
                    preferencesManager.setNetototal(mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(kilos)));
                    mainActivity.mainClass.onetototal.value = mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(kilos));
                }
                if(id==-1){
                    Utils.Mensaje("Error en base de datos, debe hacer un reset o actualizar programa",R.layout.item_customtoasterror,mainActivity);
                }else{
                    mainActivity.mainClass.oidpesada.value=String.valueOf(id);
                }

            }
        }
    }

    private void recetaFinalizada() {
        requireActivity().runOnUiThread(() -> {
            binding.tvEstado.setText("FINALIZADO");
            detener();
        });
        restaurarDatos(aRealizarFinalizados());

    }

    private boolean aRealizarFinalizados() {
        boolean arealizarfinalizados=false;
        if(recetaManager.cantidad.getValue()!=null&&recetaManager.cantidad.getValue()>0&&(recetaManager.cantidad.getValue()-recetaManager.realizadas.getValue()>0)){
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
        mainActivity.mainClass.oidreceta.value="0";
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
                mainActivity.mainClass.ocampo5.value="";
            }
        }
        if(preferencesManager.getResetCampo5()==2){
            preferencesManager.setCampo5Valor("");
            mainActivity.mainClass.ocampo5.value="";
        }
    }

    private void restaurarCampo4(boolean arealizarfinalizados) {
        if(preferencesManager.getResetCampo4()==1){
            if(arealizarfinalizados){
                preferencesManager.setCampo4Valor("");
                mainActivity.mainClass.ocampo4.value="";
            }
        }
        if(preferencesManager.getResetCampo4()==2){
            preferencesManager.setCampo4Valor("");
            mainActivity.mainClass.ocampo4.value="";
        }
    }

    private void restaurarCampo3(boolean arealizarfinalizados) {
        if(preferencesManager.getResetCampo3()==1){
            if(arealizarfinalizados){
                preferencesManager.setCampo3Valor("");
                mainActivity.mainClass.ocampo3.value="";
            }
        }
        if(preferencesManager.getResetCampo3()==2){
            preferencesManager.setCampo3Valor("");
            mainActivity.mainClass.ocampo3.value="";
        }
    }

    private void restaurarCampo2(boolean arealizarfinalizados) {
        if(preferencesManager.getResetCampo2()==1){
            if(arealizarfinalizados){
                preferencesManager.setCampo2Valor("");
                mainActivity.mainClass.ocampo2.value="";
            }
        }
        if(preferencesManager.getResetCampo2()==2){
            preferencesManager.setCampo2Valor("");
            mainActivity.mainClass.ocampo2.value="";
        }
    }

    private void restaurarCampo1(boolean arealizarfinalizados) {
        if(preferencesManager.getResetCampo1()==1){
            if(arealizarfinalizados){
                preferencesManager.setCampo1Valor("");
                mainActivity.mainClass.ocampo1.value="";
            }
        }
        if(preferencesManager.getResetCampo1()==2){
            preferencesManager.setCampo1Valor("");
            mainActivity.mainClass.ocampo1.value="";
        }
    }

    private void restaurarVencimiento(boolean arealizarfinalizados) {
        if(preferencesManager.getResetVencimiento()==1){
            if(arealizarfinalizados){
                preferencesManager.setVencimiento("");
                mainActivity.mainClass.ovenci.value="";
            }
        }
        if(preferencesManager.getResetVencimiento()==2){
            preferencesManager.setVencimiento("");
            mainActivity.mainClass.ovenci.value="";
        }
    }

    private void restaurarLote(boolean arealizarfinalizados) {
        if(preferencesManager.getResetLote()==1){
            if(arealizarfinalizados){
                preferencesManager.setLote("");
                mainActivity.mainClass.olote.value="";
            }
        }
        if(preferencesManager.getResetLote()==2){
            preferencesManager.setLote("");
            mainActivity.mainClass.olote.value="";
        }
    }


    private void actualizarVistas() {
        binding.tvNeto.setText(mainActivity.mainClass.BZA.getNetoStr(mainActivity.mainClass.N_BZA));
        binding.tvBruto.setText(mainActivity.mainClass.BZA.getBrutoStr(mainActivity.mainClass.N_BZA));
        mainActivity.mainClass.oturno.value=mainActivity.mainClass.devuelveTurnoActual();
        if(!mainActivity.mainClass.olote.value.equals("")&&
                !mainActivity.mainClass.oturno.value.equals("")&&
                !mainActivity.mainClass.ovenci.value.equals("")){
            binding.tvDatos.setText(mainActivity.mainClass.olote.value+" | "+mainActivity.mainClass.oturno.value+ " ...");
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
        if(recetaManager.recetaActual.equals("")){
            binding.tvCodigoingrediente.setText("Seleccione Receta");
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
        boolean empezar= (mainActivity.mainClass.olote.value != "" || mainActivity.mainClass.oturno.value != "" ||
                mainActivity.mainClass.ovenci.value != "") && !recetaManager.recetaActual.isEmpty();
        if(empezar){
            binding.tvEstado.setText("Listo para comenzar");
            binding.tvNumpaso.setText("-");
        }else{
            binding.tvEstado.setText("Proceso Detenido");
        }
    }

    private void procesoEjecucion() {
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
                    mainActivity.mainClass.okilos.value=setPointstr;
                    setPoint=Float.parseFloat(setPointstr);
                    Float lim_max=0f,lim_min=0f;
                    lim_max=((100+Integer.parseInt(preferencesManager.getTolerancia()))*setPoint)/100;
                    lim_min=((100-Integer.parseInt(preferencesManager.getTolerancia()))*setPoint)/100;
                    actualizarBarraProgreso(lim_max);
                    actualizarDisplayPeso(lim_min,lim_max);
                }
                int num=determinarBalanza();
                actualizarBarraProceso(num);

                mainActivity.mainClass.oingredientes.value= recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getDescrip_ing();
                mainActivity.mainClass.ocodigoingrediente.value= recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getCodigo_ing();
            }

        }
    }

    private void actualizarBarraProceso(int num) {
        if(Utils.isNumeric(recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getKilos_ing())){
            if(Float.parseFloat(recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getKilos_ing())==0){
                binding.tvEstado.setText("Ingrese "+ recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getDescrip_ing() +
                        " en balanza "+ num);
            }else{
                binding.tvEstado.setText("Ingrese "+ recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getKilos_ing() +
                        mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA)+ " de "+ recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getDescrip_ing() +
                        " en balanza "+ num);
            }
        }
    }

    private void actualizarDisplayPeso(Float lim_min, Float lim_max) {
        if(mainActivity.mainClass.BZA.getNeto(mainActivity.mainClass.N_BZA)>=lim_min&&mainActivity.mainClass.BZA.getNeto(mainActivity.mainClass.N_BZA)<=lim_max){
            binding.lnFondolayout.setBackgroundResource(R.drawable.boton_selector_balanza_enrango);
            rango=1;
            binding.imRango.setVisibility(View.INVISIBLE);
            setupViewsColor(Color.WHITE,R.color.blanco,R.drawable.estable_blanco);
        }
        if(mainActivity.mainClass.BZA.getNeto(mainActivity.mainClass.N_BZA)<lim_min){
            binding.lnFondolayout.setBackgroundResource(R.drawable.boton_selector_balanza_fuerarango);
            binding.imRango.setBackgroundResource(R.drawable.flecha_arribablanca);
            binding.imRango.setVisibility(View.VISIBLE);
            setupViewsColor(Color.WHITE,R.color.blanco,R.drawable.estable_blanco);
            rango=0;
        }
        if(mainActivity.mainClass.BZA.getNeto(mainActivity.mainClass.N_BZA)>lim_max){
            binding.lnFondolayout.setBackgroundResource(R.drawable.boton_selector_balanza_fuerarango);
            binding.imRango.setBackgroundResource(R.drawable.flecha_abajoblanca);
            binding.imRango.setVisibility(View.VISIBLE);
            setupViewsColor(Color.WHITE,R.color.blanco,R.drawable.estable_blanco);
            rango=2;
        }
        setupTaraView(R.drawable.tare_white);

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
            float porcentaje= 100*mainActivity.mainClass.BZA.getNeto(mainActivity.mainClass.N_BZA)/setPoint;
            if(porcentaje<=100){
                binding.progressBar.setProgress((int) porcentaje);
                setupProgressBarStyle(R.drawable.progress,Color.BLACK);
            }else{
                if(mainActivity.mainClass.BZA.getNeto(mainActivity.mainClass.N_BZA)>lim_max){
                    setupProgressBarStyle(R.drawable.progress2,Color.WHITE);
                }else{
                    setupProgressBarStyle(R.drawable.progress,Color.BLACK);
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
        binding.lnNumpaso.setBackgroundResource(R.drawable.stylekeycortransparent);
    }

    private int determinarBalanza() {
        String kilos= recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getKilos_ing();
        int modo=preferencesManager.getModoBalanza();
        int num=0;
        if(Utils.isNumeric(kilos)){
            Float kilosFloat=Float.parseFloat(kilos);
            if(kilosFloat>Float.parseFloat(preferencesManager.getBza2Limite())&&modo>1){
                num=3;
            }
            if(kilosFloat<Float.parseFloat(preferencesManager.getBza2Limite())&&modo>0){
                num=2;
                mainActivity.mainClass.N_BZA=2;
            }
            if(kilosFloat<Float.parseFloat(preferencesManager.getBza1Limite())){
                num=1;
                mainActivity.mainClass.N_BZA=1;
            }
        }
        if (num==0){
            if(modo==0){
                num=1;
                mainActivity.mainClass.N_BZA=1;
            }
            if(modo==1){
                num=2;
                mainActivity.mainClass.N_BZA=2;
            }
            if(modo==2){
                num=3;
            }

        }
        return num;
    }

    @Override
    public void onDestroyView() {
        stoped=true;
        super.onDestroyView();
    }


}
