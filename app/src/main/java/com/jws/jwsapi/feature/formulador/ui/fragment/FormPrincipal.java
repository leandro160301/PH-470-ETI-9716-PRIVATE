package com.jws.jwsapi.feature.formulador.ui.fragment;

import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.TecladoFlotanteConCancelar;
import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.dialogoCheckboxVisibilidad;
import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.dialogoTexto;
import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.dialogoTextoConCancelar;
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
import com.jws.jwsapi.feature.formulador.data.repository.RecipeRepository;
import com.jws.jwsapi.feature.formulador.di.RecetaManager;
import com.jws.jwsapi.feature.formulador.models.FormModelIngredientes;
import com.jws.jwsapi.feature.formulador.data.sql.FormSqlHelper;
import com.jws.jwsapi.R;
import com.jws.jwsapi.feature.formulador.viewmodel.FormPrincipalViewModel;
import com.jws.jwsapi.feature.formulador.di.LabelManager;
import com.jws.jwsapi.utils.ToastHelper;
import com.jws.jwsapi.utils.Utils;

import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class FormPrincipal extends Fragment  implements ToastHelper {
    @Inject
    RecetaManager recetaManager;
    @Inject
    UsersManager usersManager;
    @Inject
    PreferencesManager preferencesManager;
    @Inject
    LabelManager labelManager;
    @Inject
    FormSqlHelper formSqlHelper;
    @Inject
    RecipeRepository recipeRepository;
    FormPrincipalViewModel viewModel;
    Handler mHandler= new Handler();
    MainActivity mainActivity;
    private ButtonProvider_Principal buttonProvider;
    boolean stoped=false;
    public int rango=0; //0=bajo, 1=acepto,2=alto
    public static int BOTONERANORMAL=0;
    public static int BOTONERABALANZA=1;
    int botonera=BOTONERANORMAL;
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
        viewModel.mensajeToastError.observe(getViewLifecycleOwner(), mensaje -> {
            if(mensaje!=null)Utils.Mensaje(mensaje,R.layout.item_customtoasterror,mainActivity);
        });
        viewModel.getRealizadas().observe(getViewLifecycleOwner(), this::updateViewRealizadas);
        viewModel.getEjecutando().observe(getViewLifecycleOwner(), this::updateViewBtStart);

    }

    private void updateViewBtStart(Boolean ejecutando) {
        if(ejecutando){
            binding.btStart.setBackgroundResource(R.drawable.circlebuttonon1);
        }else{
            binding.btStart.setBackgroundResource(R.drawable.boton__arranqueparada_selector);
        }
    }

    private void updateViewRealizadas(Integer realizadas) {
        Integer cantidad = recetaManager.cantidad.getValue();
        if (cantidad != null && cantidad > 0) {
            binding.tvRestantes.setVisibility(View.VISIBLE);
            binding.tvRestantesValor.setVisibility(View.VISIBLE);
            binding.tvRestantesValor.setText(String.valueOf(cantidad - realizadas));
        } else {
            binding.tvRestantes.setVisibility(View.GONE);
            binding.tvRestantesValor.setVisibility(View.GONE);
        }
    }

    public Runnable GET_PESO_cal_bza= new Runnable() {
        @Override
        public void run() {
            if(!stoped){
                actualizarVistas();
                if(isEjecutando()){
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
        viewModel.mostrarMensajeDeError(null);
        labelManager.opaso.value=String.valueOf(recetaManager.pasoActual);
        labelManager.ocodigoreceta.value= recetaManager.codigoReceta;
        labelManager.oreceta.value= recetaManager.nombreReceta;

        binding.lnFondolayout.setOnClickListener(view12 -> btBalanza());
        binding.btStart.setOnClickListener(view1 -> btStart());

        if(!recetaManager.recetaActual.isEmpty()){
            String[]arr= recetaManager.recetaActual.split("_");
            if(arr.length==3){
                binding.tvRecetaActual.setText(arr[1].replace("_","")+" "+arr[2].replace("_",""));
            }
        }
        setupUnidad();
        binding.imEstable.setVisibility(View.INVISIBLE);

    }

    private void btStart() {
        if(isEjecutando()){
            ConsultaFin();
        }else{
            iniciarReceta();
        }
    }

    private boolean isEjecutando() {
        return recetaManager.ejecutando.getValue() != null && recetaManager.ejecutando.getValue();
    }

    private void btBalanza() {
        if(botonera==BOTONERANORMAL){
            botonera=BOTONERABALANZA;
            configuracionBotonesBalanza();
        }else {
            botonera=BOTONERANORMAL;
            configuracionBotones();
        }
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
                viewModel.nuevoLoteFecha();
            }else{
                //si no esta vacio ya tiene una fecha con un indice, verificamos que la fecha actual este presente por si cambio de dia (empieza en 1 con el nuevo dia)
                viewModel.verificarNuevoLoteFecha();
            }
        }
        boolean empezar=viewModel.verificarComienzo();
        if(empezar){
            guardarDatosEnMemoria();
            if(viewModel.ejecutarReceta(recipeRepository.getReceta(recetaManager.recetaActual,this))){
                iniciaPorModoReceta();
            }
        }
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

    private void iniciaPorModoReceta() {
        if(preferencesManager.getModoReceta()==0){//respeta el setpoint de cada paso
            boolean empezarKilos=viewModel.modoKilos();
            if(empezarKilos){
                viewModel.setupValoresParaInicio();
                verificarPasoEsAutomatico();
            }
        }else{//ingresa porcentaje
             modoPorcentaje();
        }
    }

    private void modoPorcentaje() {
        if(preferencesManager.getModoUso()==0||!preferencesManager.getRecetacomopedidoCheckbox()){
            setupRecetaBatch(); //por batch
        }else{
            setupRecetaPedido();//por pedido
        }
    }

    private void setupRecetaPedido() {
        if(isRealizadasCero()){
            IngresaPorcentaje();
        }else {
            viewModel.mostrarMensajeDeError("Ingrese una cantidad");
        }
    }

    private boolean isRealizadasCero() {
        return recetaManager.realizadas.getValue() != null && recetaManager.realizadas.getValue() == 0;
    }

    private void setupRecetaBatch() {
        if(isRealizadasCero() ||(recetaManager.cantidad.getValue()!=null)&&recetaManager.realizadas.getValue()!=null&&recetaManager.realizadas.getValue()-recetaManager.cantidad.getValue()==0){
            IngresaPorcentaje();
        }else {
            updatePorcetajeReceta(recetaManager.porcentajeReceta);
            viewModel.setupValoresParaInicio();
            verificarPasoEsAutomatico();
        }
    }


    private boolean verificarPasoEsAutomatico() {
        boolean isAutomatic=false;
        List<FormModelIngredientes> lista= recipeRepository.getIngredientes(this);
        int salida=0;
        for(FormModelIngredientes ing: lista){
            if(Objects.equals(ing.getCodigo(), recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getCodigo_ing())){
                if(ing.getSalida()>0) {
                    isAutomatic=true;
                    salida=ing.getSalida();
                }
            }
        }
        configuracionAutomatico(isAutomatic,salida);
        if(isAutomatic){
            viewModel.setEstadoPesar();
        }else{
            if(preferencesManager.getRecipientexPaso()||recetaManager.pasoActual==1){
                IngresoRecipiente();
            }else{
                mainClass.BZA.setTaraDigital(mainClass.N_BZA,mainClass.BZA.getBruto(mainClass.N_BZA));
            }
        }
        return isAutomatic;
    }

    private void configuracionAutomatico(boolean isAutomatic, int salida) {
        if(isAutomatic){
            String setPoint=mainClass.BZA.format(mainClass.N_BZA,String.valueOf(recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getKilos_ing()) );
            Runnable myRunnable = () -> {
                boolean result=mainClass.BZA.Itw410FrmSetear(mainClass.N_BZA,setPoint,salida);
                if(result){
                    mainClass.BZA.Itw410FrmStart(mainClass.N_BZA);
                    recetaManager.automatico=true;
                    preferencesManager.setAutomatico(true);
                    preferencesManager.setSalida(salida);
                }else{
                    mainActivity.runOnUiThread(() ->  viewModel.mostrarMensajeDeError("Error de balanza:410 escritura"));
                }
            };
            Thread myThread = new Thread(myRunnable);
            myThread.start();
        }else{
            recetaManager.automatico=false;
            preferencesManager.setAutomatico(false);
        }
    }

    private void IngresaPorcentaje() {
        String text="Ingrese los kilos a realizar totales y presione SIGUIENTE o si quiere continuar con la receta original presione CONTINUAR";
        TecladoFlotanteConCancelar(null, text, mainActivity, this::calculoporcentajeRecetaDialogo, () -> {
            if(recetaManager.listRecetaActual.size()>0) recetaManager.porcentajeReceta=recetaManager.listRecetaActual.get(0).getKilos_totales();
            viewModel.setupValoresParaInicio();
            verificarPasoEsAutomatico();
        }, "CONTINUAR");
    }

    private void calculoporcentajeRecetaDialogo(String nuevosKilosTotales) {
        if(kilosTotalesRecetaCheck()){
            updatePorcetajeReceta(nuevosKilosTotales);
            recetaManager.porcentajeReceta=nuevosKilosTotales;
            preferencesManager.setPorcentajeReceta(nuevosKilosTotales);
            viewModel.setupValoresParaInicio();
            verificarPasoEsAutomatico();
        }else{
            viewModel.mostrarMensajeDeError("Ocurrio un error con la carga de la receta");
        }
    }

    private void updatePorcetajeReceta(String nuevoPorcentaje) {
        if(kilosTotalesRecetaCheck()){
            float nuevo=Float.parseFloat(nuevoPorcentaje);
            float kilos_totales_original=Float.parseFloat(recetaManager.listRecetaActual.get(0).getKilos_totales());
            float multiplicador=nuevo/kilos_totales_original;
            for(int i = 0; i<recetaManager.listRecetaActual.size(); i++){
                recetaManager.listRecetaActual.get(i).setKilos_totales(String.valueOf(nuevo));
                recetaManager.listRecetaActual.get(i).setKilos_ing(mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA, String.valueOf(Float.parseFloat(recetaManager.listRecetaActual.get(i).getKilos_ing()) * multiplicador)));
            }
            preferencesManager.setPasosRecetaActual(recetaManager.listRecetaActual);
        }else{
            viewModel.mostrarMensajeDeError("Ocurrio un error con la carga de la receta");
        }
    }

    private boolean kilosTotalesRecetaCheck() {
        return recetaManager.listRecetaActual.size() > 0 && Utils.isNumeric(recetaManager.listRecetaActual.get(0).getKilos_totales());
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
        dialogoCheckboxVisibilidad(null, text, mainActivity, (texto, checkbox) -> viewModel.setearModoUsoDialogo(texto,checkbox,mododeuso),true,InputType.TYPE_CLASS_NUMBER,null,"RECETAS COMO PEDIDO (BOLSAS)",visible,checkboxState);
    }



    private void IngresoRecipiente() {
        //si no pone siguiente y le pone cancerlar lo manda a tara igual
        dialogoTextoConCancelar(mainActivity, "Ingrese recipiente y luego presione SIGUIENTE", "SIGUIENTE", this::realizarTaraComienzaPesar, () -> viewModel.setEstadoPesar());
    }

    private void realizarTaraComienzaPesar() {
        mainActivity.mainClass.BZA.setTaraDigital(mainActivity.mainClass.N_BZA, mainActivity.mainClass.BZA.getBruto(mainActivity.mainClass.N_BZA));
        viewModel.setEstadoPesar();
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
            bt_3.setOnClickListener(view -> btImprimir(bt_3, view));
        }
    }

    private void btImprimir(Button bt_3, View view) {
        if(bt_3.isClickable()){
            bt_3.setClickable(false);
            ImprimirUltima();
            view.postDelayed(() -> bt_3.setClickable(true), 2000);
        }
    }

    public void ImprimirUltima() {
        ImprimirEstandar imprimirEstandar = new ImprimirEstandar(getContext(), mainActivity,usersManager,preferencesManager,labelManager);
        imprimirEstandar.EnviarUltimaEtiqueta(mainClass.Service.B);
    }


    private void configuracionBotones() {
        if (buttonProvider != null) {
            Button bt_1 = buttonProvider.getButton1();
            bt_2 = buttonProvider.getButton2();
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
            bt_2.setOnClickListener(view -> btPlus());
            bt_3.setOnClickListener(view -> mainActivity.mainClass.openFragment(new FormFragmentGuardados()));
            bt_4.setOnClickListener(view -> mainActivity.mainClass.openFragment(new FormFragmentRecetas()));
            bt_5.setOnClickListener(view -> mainActivity.mainClass.openFragment(new FormFragmentIngredientes()));

        }
    }

    private void btPlus() {
        if(recetaManager.automatico){
            if(mainClass.BZA.Itw410FrmGetEstado(mainClass.N_BZA)==2){
                mainClass.BZA.Itw410FrmStart(mainClass.N_BZA);
            }else{
                mainClass.BZA.itw410FrmPause(mainClass.N_BZA);
            }
        }else{
            btPesar(mainActivity.mainClass.BZA.getNeto(mainActivity.mainClass.N_BZA),mainActivity.mainClass.BZA.getNetoStr(mainActivity.mainClass.N_BZA));
        }
    }

    private void btCantidad() {
        if(!isEjecutando()){
            IngresaCantidad();
        }else{
            viewModel.mostrarMensajeDeError("No puede cambiar la cantidad cuando esta ejecutando una receta");
        }
    }

    private void btPesar(float neto, String netoStr) {
        if(isEjecutando()&&!recetaManager.automatico){
            if(recetaManager.pasoActual<=recetaManager.listRecetaActual.size()){
                if(rango==1||preferencesManager.getContinuarFueraRango()){
                    viewModel.calculaPorcentajeError(neto,netoStr,mainClass.BZA.getUnidad(mainClass.N_BZA));
                    manejoBasedeDatoseImpresion(neto);
                }else{
                    viewModel.mostrarMensajeDeError("Ingrediente fuera de rango");
                }
            }else{
                recetaFinalizada();
            }
        }
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
        imprimirEstandar.EnviarEtiqueta(mainClass.Service.B, etiqueta);

    }

    private void imprimirEtiquetaFinal() {
        labelManager.onetototal.value=recetaManager.netoTotal.getValue();
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

                    labelManager.setupVariablesEtiqueta(pasos, ingredientes, codingredientes, kilos, netiqueta);
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
        boolean isAutomatic=verificarPasoEsAutomatico();
        if(!isAutomatic&&!preferencesManager.getRecipientexPaso())mainActivity.mainClass.BZA.setTaraDigital(mainActivity.mainClass.N_BZA,mainActivity.mainClass.BZA.getBruto(mainActivity.mainClass.N_BZA));


    }

    private void insertarPrimerPasoRecetaBatchSQL() {//guardar en receta una nueva y que devuelva el id
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
                viewModel.mostrarMensajeDeError("Error en base de datos, debe hacer un reset o actualizar programa");
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
             long id= sqlRecetaPedido(formSqlHelper);
             if(id>-1){
                 labelManager.oidreceta.value=String.valueOf(id);
                 preferencesManager.setPedidoId(id);
                 long id2= sqlPrimerPasoPesadaPedido(formSqlHelper,id);
                 if(id2!=-1){
                     labelManager.oidpesada.value=String.valueOf(id2);
                 }else{
                     viewModel.mostrarMensajeDeError("Error en base de datos pesada, debe hacer un reset o actualizar programa");
                 }

             }else{
                 viewModel.mostrarMensajeDeError("Error en base de datos pedido, debe hacer un reset o actualizar programa");
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
            labelManager.oidreceta.value=String.valueOf(preferencesManager.getRecetaId());
            long id= formSqlHelper.insertarPesada(String.valueOf(preferencesManager.getRecetaId()),"",recetaManager.codigoReceta,recetaManager.nombreReceta,
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
                formSqlHelper.actualizarNetoTotalReceta(mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(kilos))+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),String.valueOf(preferencesManager.getRecetaId()));
                preferencesManager.setNetototal(mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(kilos)));
                labelManager.onetototal.value = mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(kilos));
            }
            if(id==-1){
                viewModel.mostrarMensajeDeError("Error en base de datos, debe hacer un reset o actualizar programa");
            }else{
                labelManager.oidpesada.value=String.valueOf(id);
            }


        }
    }
    private void insertarNuevoPasoPedidoBatchSQL() {
        //agarrar id guardado_pesadas en memoria
        labelManager.oidreceta.value=preferencesManager.getPedidoId();
        if(preferencesManager.getPedidoId()>0){
            labelManager.oidreceta.value=preferencesManager.getPedidoId();
            long id= formSqlHelper.insertarPesada("",
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
                formSqlHelper.actualizarNetoTotalPedido(mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(kilos))+mainActivity.mainClass.BZA.getUnidad(mainActivity.mainClass.N_BZA),String.valueOf(preferencesManager.getPedidoId()));
                preferencesManager.setNetototal(mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(kilos)));
                labelManager.onetototal.value = mainActivity.mainClass.BZA.format(mainActivity.mainClass.N_BZA,String.valueOf(kilos));
            }
            if(id==-1){
                viewModel.mostrarMensajeDeError("Error en base de datos, debe hacer un reset o actualizar programa");
            }else{
                labelManager.oidpesada.value=String.valueOf(id);
            }

        }
    }

    private void recetaFinalizada() {
        requireActivity().runOnUiThread(() -> {
            recetaManager.estadoMensajeStr.setValue("FINALIZADO");
            detener();
        });
        viewModel.restaurarDatos();
    }


    private void detener() {
        viewModel.detener();
        if(recetaManager.automatico)mainClass.BZA.itw410FrmStop(mainClass.N_BZA);
    }

    private void actualizarVistas() {
        binding.tvNeto.setText(mainActivity.mainClass.BZA.getNetoStr(mainActivity.mainClass.N_BZA));
        binding.tvBruto.setText(mainActivity.mainClass.BZA.getBrutoStr(mainActivity.mainClass.N_BZA));
        if(isManualAndBotoneraNormal()) bt_2.setText("PESAR");
        labelManager.oturno.value=mainActivity.mainClass.devuelveTurnoActual();
        if(isDatosDisable()){
            binding.tvDatos.setText(labelManager.olote.value+" | "+labelManager.oturno.value+ " ...");
        }else{
            binding.tvDatos.setText("Toque para ingresar los datos");
        }
        if(mainActivity.mainClass.BZA.getEstable(mainActivity.mainClass.N_BZA)!=null&&mainActivity.mainClass.BZA.getEstable(mainActivity.mainClass.N_BZA)){
            binding.imEstable.setVisibility(View.VISIBLE);
        }else{
            binding.imEstable.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isDatosDisable() {
        return !labelManager.olote.value.equals("") &&
                !labelManager.oturno.value.equals("") &&
                !labelManager.ovenci.value.equals("");
    }

    private boolean isManualAndBotoneraNormal() {
        return !recetaManager.automatico && botonera == BOTONERANORMAL;
    }


    private void procesoDetenido() {
        if(recetaManager.recetaActual.isEmpty())binding.tvRecetaActual.setText("Seleccione Receta");
        if(botonera==BOTONERANORMAL){
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
        if(botonera==BOTONERANORMAL)bt_2.setText("PAUSAR");
        switch (estado){
            case 0:
                if(recetaManager.estadoBalanza== RecetaManager.ESTABILIZANDO ||recetaManager.estadoBalanza== RecetaManager.PROCESO){
                    automaticoFinalizado();
                    recetaManager.estadoBalanza=RecetaManager.DETENIDO;
                }
                break;
            case 1:
                procesoEjecucionManual();
                recetaManager.estadoBalanza=RecetaManager.PROCESO;
                break;
            case 2:
                if(botonera==BOTONERANORMAL)bt_2.setText("REANUDAR");
                recetaManager.estadoMensajeStr.setValue("Proceso automatico en pausa...");
                recetaManager.estadoBalanza=RecetaManager.PAUSA;
                break;
            case 3:
                procesoEjecucionManual();
                recetaManager.estadoBalanza=RecetaManager.ESTABILIZANDO;
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
                mainClass.N_BZA=viewModel.determinarBalanza();
                viewModel.actualizarBarraProceso(mainClass.N_BZA,mainClass.BZA.getUnidad(mainClass.N_BZA));

                labelManager.oingredientes.value= recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getDescrip_ing();
                labelManager.ocodigoingrediente.value= recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getCodigo_ing();
            }

        }
    }


    private void actualizarDisplayPeso(Float limMin, Float limMax) {
        float pesoNeto = mainActivity.mainClass.BZA.getNeto(mainActivity.mainClass.N_BZA);
        if(isInRange(limMin, limMax, pesoNeto))setupViewDisplay(R.drawable.boton_selector_balanza_enrango,R.drawable.flecha_abajoblanca,View.INVISIBLE, RecetaManager.BUENO);
        if(isLowRange(limMin, pesoNeto))setupViewDisplay(R.drawable.boton_selector_balanza_fuerarango,R.drawable.flecha_arribablanca,View.VISIBLE,RecetaManager.BAJO);
        if(isHighRange(limMax, pesoNeto))setupViewDisplay(R.drawable.boton_selector_balanza_fuerarango,R.drawable.flecha_abajoblanca,View.VISIBLE,RecetaManager.ALTO);
        setupTaraView(R.drawable.tare_white);
    }

    private static boolean isHighRange(Float lim_max, float pesoNeto) {
        return pesoNeto > lim_max;
    }

    private static boolean isLowRange(Float limMin, float pesoNeto) {
        return pesoNeto < limMin;
    }

    private static boolean isInRange(Float limMin, Float limMax, float pesoNeto) {
        return pesoNeto >= limMin && pesoNeto <= limMax;
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
                if(isHighRange(lim_max, mainActivity.mainClass.BZA.getNeto(mainActivity.mainClass.N_BZA))){
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

    @Override
    public void onDestroyView() {
        stoped=true;
        super.onDestroyView();
    }

    @Override
    public void mensajeError(String str) {
        viewModel.mostrarMensajeDeError(str);
    }

    @Override
    public void mensajeCorrecto(String str) {
        viewModel.mostrarMensajeDeError(str);
    }
}
