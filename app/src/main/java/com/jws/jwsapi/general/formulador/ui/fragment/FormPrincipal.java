package com.jws.jwsapi.general.formulador.ui.fragment;

import static com.jws.jwsapi.general.formulador.ui.dialog.DialogUtil.TecladoFlotanteConCancelar;
import static com.jws.jwsapi.general.formulador.ui.dialog.DialogUtil.dialogoCargando;
import static com.jws.jwsapi.general.formulador.ui.dialog.DialogUtil.dialogoCheckboxVisibilidad;
import static com.jws.jwsapi.general.formulador.ui.dialog.DialogUtil.dialogoTexto;
import static com.jws.jwsapi.general.formulador.ui.dialog.DialogUtil.dialogoTextoConCancelar;
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
import com.jws.jwsapi.general.formulador.MainFormClass;
import com.jws.jwsapi.general.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.general.formulador.data.repository.RecipeRepository;
import com.jws.jwsapi.general.formulador.data.sql.DatabaseHelper;
import com.jws.jwsapi.general.formulador.data.sql.DatabaseRepository;
import com.jws.jwsapi.general.formulador.di.RecetaManager;
import com.jws.jwsapi.general.formulador.models.FormModelIngredientes;
import com.jws.jwsapi.R;
import com.jws.jwsapi.general.formulador.viewmodel.FormPreferencesLabelViewModel;
import com.jws.jwsapi.general.formulador.viewmodel.FormPrincipalViewModel;
import com.jws.jwsapi.general.formulador.di.LabelManager;
import com.jws.jwsapi.general.pallet.PalletRequest;
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
    DatabaseHelper formSqlHelper;
    @Inject
    RecipeRepository recipeRepository;
    FormPrincipalViewModel viewModel;
    FormPreferencesLabelViewModel labelPreferencesViewModel;
    DatabaseRepository databaseRepository;
    Handler mHandler= new Handler();
    MainActivity mainActivity;
    private ButtonProvider_Principal buttonProvider;
    boolean stoped=false;
    public int rango=RecetaManager.BAJO;
    public static int BOTONERA_NORMAL =0;
    public static int BOTONERA_BALANZA =1;
    public static int LOTE_FECHA = 1;
    public static int LOTE_NUMERICO = 2;
    private int botonera= BOTONERA_NORMAL;
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
        viewModel.mensajeToastError.observe(getViewLifecycleOwner(), this::mensaje);
        viewModel.getRealizadas().observe(getViewLifecycleOwner(), this::updateViewRealizadas);
        viewModel.getEjecutando().observe(getViewLifecycleOwner(), this::updateViewBtStart);
    }

    private void mensaje(String mensaje) {
        if(mensaje !=null)Utils.Mensaje(mensaje,R.layout.item_customtoasterror,mainActivity);
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
        labelPreferencesViewModel = new ViewModelProvider(this).get(FormPreferencesLabelViewModel.class);
        viewModel.mostrarMensajeDeError(null);
        labelPreferencesViewModel.initializeLabelVariables();
        binding.lnFondolayout.setOnClickListener(view12 -> btBalanza());
        binding.btStart.setOnClickListener(view1 -> btStart());
        updateViewRecetaText();
        setupUnidad();
        binding.imEstable.setVisibility(View.INVISIBLE);
        databaseRepository= new DatabaseRepository(formSqlHelper,preferencesManager,labelManager, mainClass.bza,recetaManager,mainClass.nBza,usersManager,this,labelPreferencesViewModel);
    }

    private void updateViewRecetaText() {
        if(!recetaManager.recetaActual.isEmpty()){
            String recetaActual = getFormatRecetaActual();
            binding.tvRecetaActual.setText(recetaActual);
        }
    }

    private String getFormatRecetaActual() {
        String recetaActual="";
        String[]arr= recetaManager.recetaActual.split("_");
        if(arr.length==3){
            recetaActual=arr[1].replace("_","")+" "+arr[2].replace("_","");
        }
        return recetaActual;
    }

    private void btStart() {
        if(isEjecutando()){
            consultaFin();
        }else{
            iniciarReceta();
        }
    }

    private boolean isEjecutando() {
        return recetaManager.ejecutando.getValue() != null && recetaManager.ejecutando.getValue();
    }

    private void btBalanza() {
        if(botonera == BOTONERA_NORMAL){
            botonera = BOTONERA_BALANZA;
            configuracionBotonesBalanza();
        }else {
            botonera = BOTONERA_NORMAL;
            configuracionBotones();
        }
    }

    private void setupUnidad() {
        String unidad=mainActivity.mainClass.bza.getUnidad(mainActivity.mainClass.nBza);
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
        if(labelPreferencesViewModel.isLoteVacio() &&preferencesManager.getModoLote()== LOTE_NUMERICO){
            labelPreferencesViewModel.nuevoLoteNumerico();
        }
        if(preferencesManager.getModoLote()== LOTE_FECHA){
            nuevoLoteFecha();
        }
        if(verificarComienzo()){
            runInitRecipe();
        }
    }

    public boolean verificarComienzo() {
        boolean empezar=true;
        empezar = labelPreferencesViewModel.isDataCompleted(empezar);
        empezar = viewModel.isRecipeSelected(empezar);
        return empezar;
    }

    private void runInitRecipe() {
        labelPreferencesViewModel.saveDataInMemory();
        if(viewModel.ejecutarReceta(recipeRepository.getReceta(recetaManager.recetaActual,this))){
            initRecipe();
        }
    }

    private void nuevoLoteFecha() {
        if(labelPreferencesViewModel.isLoteVacio()){
            viewModel.nuevoLoteFecha();
        }else{
            viewModel.verificarNuevoLoteFecha();
        }
    }

    private void initRecipe() {
        if(preferencesManager.getModoReceta()==0){//respeta el setpoint de cada paso
            boolean empezarKilos=viewModel.modoKilos();
            if(empezarKilos){
                viewModelsInit();
                verificarPasoEsAutomatico();
            }
        }else{//ingresa porcentaje
             modoPorcentaje();
        }
    }

    private void viewModelsInit() {
        viewModel.setupValoresParaInicio();
        labelPreferencesViewModel.preferencesSetupInit();
        labelPreferencesViewModel.labelSetupInit();
    }

    private void modoPorcentaje() {
        if(labelPreferencesViewModel.isRecipeBatch()){
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
            viewModelsInit();
            verificarPasoEsAutomatico();
        }
    }

    private boolean verificarPasoEsAutomatico() {
        boolean isAutomatic=false;
        List<FormModelIngredientes> lista= recipeRepository.getIngredientes(this);
        int salida=0;
        for(FormModelIngredientes ing: lista){
            if(isCodigoDePaso(ing)){
                if(ing.getSalida()>0) {
                    isAutomatic=true;
                    salida=ing.getSalida();
                }
            }
        }
        configuracionAutomatico(isAutomatic,salida);
        comienzaReceta(isAutomatic);
        return isAutomatic;
    }

    private boolean isCodigoDePaso(FormModelIngredientes ing) {
        return Objects.equals(ing.getCodigo(), recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getCodigoIng());
    }

    private void comienzaReceta(boolean isAutomatic) {
        if(isAutomatic){
            viewModel.setEstadoPesar();
        }else{
            if(preferencesManager.getRecipientexPaso()||recetaManager.pasoActual==1){
                IngresoRecipiente();
            }else{
                mainClass.bza.setTaraDigital(mainClass.nBza,mainClass.bza.getBruto(mainClass.nBza));
            }
        }
    }

    private void configuracionAutomatico(boolean isAutomatic, int salida) {
        if(isAutomatic){
            String setPoint=mainClass.bza.format(mainClass.nBza,String.valueOf(recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getKilosIng()) );
            Runnable myRunnable = () -> setearArranqueBza(salida, setPoint);
            Thread myThread = new Thread(myRunnable);
            myThread.start();
        }else{
            setupAutomatic(false);
        }
    }

    private void setupAutomatic(boolean Boolean) {
        recetaManager.automatico=Boolean;
        preferencesManager.setAutomatico(Boolean);
    }

    private void setearArranqueBza(int salida, String setPoint) {
        boolean result=mainClass.bza.Itw410FrmSetear(mainClass.nBza, setPoint, salida);
        if(result){
            mainClass.bza.Itw410FrmStart(mainClass.nBza);
            setupAutomatic(true);
            preferencesManager.setSalida(salida);
        }else{
            mainActivity.runOnUiThread(() ->  viewModel.mostrarMensajeDeError("Error de balanza:410 escritura"));
        }
    }

    private void IngresaPorcentaje() {
        String text="Ingrese los kilos a realizar totales y presione SIGUIENTE o si quiere continuar con la receta original presione CONTINUAR";
        TecladoFlotanteConCancelar(null, text, mainActivity, this::calculoporcentajeRecetaDialogo, () -> {
            if(recetaManager.listRecetaActual.size()>0) recetaManager.porcentajeReceta=recetaManager.listRecetaActual.get(0).getKilosTotales();
            viewModelsInit();
            verificarPasoEsAutomatico();
        }, "CONTINUAR");
    }

    private void calculoporcentajeRecetaDialogo(String nuevosKilosTotales) {
        if(kilosTotalesRecetaCheck()){
            updatePorcetajeReceta(nuevosKilosTotales);
            recetaManager.porcentajeReceta=nuevosKilosTotales;
            preferencesManager.setPorcentajeReceta(nuevosKilosTotales);
            viewModelsInit();
            verificarPasoEsAutomatico();
        }else{
            viewModel.mostrarMensajeDeError("Ocurrio un error con la carga de la receta");
        }
    }

    private void updatePorcetajeReceta(String nuevoPorcentaje) {
        if(kilosTotalesRecetaCheck()){
            updateRecipeStep(nuevoPorcentaje);
            preferencesManager.setPasosRecetaActual(recetaManager.listRecetaActual);
        }else{
            viewModel.mostrarMensajeDeError("Ocurrio un error con la carga de la receta");
        }
    }

    private void updateRecipeStep(String nuevoPorcentaje) {
        float nuevo=Float.parseFloat(nuevoPorcentaje);
        float kilos_totales_original=Float.parseFloat(recetaManager.listRecetaActual.get(0).getKilosTotales());
        float multiplicador=nuevo/kilos_totales_original;
        for(int i = 0; i<recetaManager.listRecetaActual.size(); i++){
            recetaManager.listRecetaActual.get(i).setKilos_totales(String.valueOf(nuevo));
            recetaManager.listRecetaActual.get(i).setKilos_ing(mainActivity.mainClass.bza.format(mainActivity.mainClass.nBza, String.valueOf(Float.parseFloat(recetaManager.listRecetaActual.get(i).getKilosIng()) * multiplicador)));
        }
    }

    private boolean kilosTotalesRecetaCheck() {
        return recetaManager.listRecetaActual.size() > 0 && Utils.isNumeric(recetaManager.listRecetaActual.get(0).getKilosTotales());
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
        mainActivity.mainClass.bza.setTaraDigital(mainActivity.mainClass.nBza, mainActivity.mainClass.bza.getBruto(mainActivity.mainClass.nBza));
        viewModel.setEstadoPesar();
    }

    private void consultaFin() {
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

            bt_1.setOnClickListener(view -> mainActivity.mainClass.bza.setCero(mainActivity.mainClass.nBza));
            bt_2.setOnClickListener(view -> mainActivity.mainClass.bza.setTaraDigital(mainActivity.mainClass.nBza,mainActivity.mainClass.bza.getBruto(mainActivity.mainClass.nBza)));
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
        imprimirEstandar.EnviarUltimaEtiqueta(mainClass.service.B);
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
            btPausaReanuda();
        }else{
            btPesar(mainActivity.mainClass.bza.getNeto(mainActivity.mainClass.nBza),mainActivity.mainClass.bza.getNetoStr(mainActivity.mainClass.nBza));
        }
    }

    private void btPausaReanuda() {
        if(mainClass.bza.Itw410FrmGetEstado(mainClass.nBza)==RecetaManager.PAUSA){
            mainClass.bza.Itw410FrmStart(mainClass.nBza);
        }else{
            mainClass.bza.itw410FrmPause(mainClass.nBza);
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
                    viewModel.calculaPorcentajeError(neto,netoStr,mainClass.bza.getUnidad(mainClass.nBza));
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
            esperaImprimeFinal();
        }
    }

    private void esperaImprimeFinal() {
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            requireActivity().runOnUiThread(this::imprimirEtiquetaFinalDialog);
        }).start();
    }

    private void imprimeyGuardaNuevoPasoPedidoBatch() {
        if(preferencesManager.getPasoActual()>1&&preferencesManager.getRecetaComoPedido()){
            databaseRepository.insertarNuevoPasoPedidoBatchSQL(viewModel.calculaNetoTotal());
            imprimirEtiquetaPaso();
        }
    }

    private void imprimeyGuardaPrimerPasoPedidoBatch() {
        if(preferencesManager.getPasoActual()==1&&preferencesManager.getRecetaComoPedido()){
            databaseRepository.insertarPrimerPasoPedidoBatchSQL();
            imprimirEtiquetaPaso();
        }
    }

    private void imprimeyGuardaNuevoPasoRecetaBatch() {
        if(preferencesManager.getPasoActual()>1&&!preferencesManager.getRecetaComoPedido()){
            databaseRepository.insertarNuevoPasoRecetaBatchSQL(viewModel.calculaNetoTotal());
            imprimirEtiquetaPaso();
        }
    }

    private void imprimeyGuardaPrimerPasoRecetaBatch() {
        if(preferencesManager.getPasoActual()==1&&!preferencesManager.getRecetaComoPedido()){
            databaseRepository.insertarPrimerPasoRecetaBatchSQL();
            imprimirEtiquetaPaso();
        }
    }

    private void imprimirEtiquetaPaso() {
        if(preferencesManager.getEtiquetaxPaso()){
            imprimir(0);
        }
    }
    public void imprimir(int etiqueta) {
        ImprimirEstandar imprimirEstandar = new ImprimirEstandar(getContext(), mainActivity,usersManager,preferencesManager,labelManager);
        imprimirEstandar.EnviarEtiqueta(mainClass.service.B, etiqueta);
    }

    private void imprimirEtiquetaFinalDialog() {
        labelManager.onetototal.value=recetaManager.netoTotal.getValue();
        AlertDialog dialog=dialogoCargando(mainActivity,"Imprimiento etiqueta final");
        imprimirEtiquetaFinal(dialog);
    }

    private void imprimirEtiquetaFinal(AlertDialog dialog) {
        Handler handler = new Handler(Looper.getMainLooper());
        Runnable runnable = new Runnable() {
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
                        ingredientes[index] = recetaManager.listRecetaActual.get(j).getDescIng();
                        codingredientes[index] = recetaManager.listRecetaActual.get(j).getCodigoIng();
                        kilos[index] = recetaManager.listRecetaActual.get(j).getKilosRealesIng() + mainActivity.mainClass.bza.getUnidad(mainActivity.mainClass.nBza);
                    }
                    labelManager.setupVariablesEtiqueta(pasos, ingredientes, codingredientes, kilos, netiqueta);
                    imprimir(1);

                    i += 5;
                    handler.postDelayed(this, 1000);
                } else {
                    requireActivity().runOnUiThread(dialog::cancel);
                    recetaFinalizada();
                }
            }
        };
        handler.post(runnable);
    }


    private void nuevoPaso(float neto) {
        recetaManager.pasoActual=recetaManager.pasoActual+1;
        labelManager.opaso.value=recetaManager.pasoActual;
        preferencesManager.setPasoActual(recetaManager.pasoActual);
        if(Utils.isNumeric(String.valueOf(recetaManager.netoTotal))){
            Float resultado= Float.parseFloat(String.valueOf(recetaManager.netoTotal))+neto;
            recetaManager.netoTotal.setValue(mainActivity.mainClass.bza.format(mainActivity.mainClass.nBza,String.valueOf(resultado)));
            preferencesManager.setNetototal(String.valueOf(recetaManager.netoTotal));
            labelManager.onetototal.value = String.valueOf(recetaManager.netoTotal);
        }
        preferencesManager.setPasosRecetaActual(recetaManager.listRecetaActual);
        boolean isAutomatic=verificarPasoEsAutomatico();
        if(!isAutomatic&&!preferencesManager.getRecipientexPaso())mainActivity.mainClass.bza.setTaraDigital(mainActivity.mainClass.nBza,mainActivity.mainClass.bza.getBruto(mainActivity.mainClass.nBza));
    }

    private void recetaFinalizada() {
        requireActivity().runOnUiThread(() -> {
            recetaManager.estadoMensajeStr.setValue("FINALIZADO");
            detener();
        });
        labelPreferencesViewModel.restaurarDatos();
    }

    private void detener() {
        viewModel.detener();
        labelPreferencesViewModel.labelSetupStop();
        labelPreferencesViewModel.preferencesSetupStop();
        if(recetaManager.automatico)mainClass.bza.itw410FrmStop(mainClass.nBza);
    }

    private void actualizarVistas() {
        binding.tvNeto.setText(mainActivity.mainClass.bza.getNetoStr(mainActivity.mainClass.nBza));
        binding.tvBruto.setText(mainActivity.mainClass.bza.getBrutoStr(mainActivity.mainClass.nBza));
        if(isManualAndBotoneraNormal()) bt_2.setText("PESAR");
        labelManager.oturno.value=mainActivity.mainClass.devuelveTurnoActual();
        updateDatosLabel();
        updateViewEstable();
    }

    private void updateDatosLabel() {
        if(labelPreferencesViewModel.isDatosDisable()){
            binding.tvDatos.setText(labelManager.olote.value+" | "+labelManager.oturno.value+ " ...");
        }else{
            binding.tvDatos.setText("Toque para ingresar los datos");
        }
    }

    private void updateViewEstable() {
        if(mainActivity.mainClass.bza.getEstable(mainActivity.mainClass.nBza)!=null&&mainActivity.mainClass.bza.getEstable(mainActivity.mainClass.nBza)){
            binding.imEstable.setVisibility(View.VISIBLE);
        }else{
            binding.imEstable.setVisibility(View.INVISIBLE);
        }
    }

    private boolean isManualAndBotoneraNormal() {
        return !recetaManager.automatico && botonera == BOTONERA_NORMAL;
    }


    private void procesoDetenido() {
        if(recetaManager.recetaActual.isEmpty())binding.tvRecetaActual.setText("Seleccione Receta");
        if(botonera== BOTONERA_NORMAL){
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
        int estado=mainClass.bza.Itw410FrmGetEstado(mainClass.nBza);
        if(botonera== BOTONERA_NORMAL)bt_2.setText("PAUSAR");
        switch (estado){
            case 0:
                estadoDetenido();
                break;
            case 1:
                estadoProceso();
                break;
            case 2:
                estadoPausa();
                break;
            case 3:
                estadoEstabilizando();
                break;
            default:
                break;
        }
    }

    private void estadoEstabilizando() {
        procesoEjecucionManual();
        recetaManager.estadoBalanza=RecetaManager.ESTABILIZANDO;
        recetaManager.estadoMensajeStr.setValue("Estabilizando...");
    }

    private void estadoPausa() {
        if(botonera== BOTONERA_NORMAL)bt_2.setText("REANUDAR");
        recetaManager.estadoMensajeStr.setValue("Proceso automatico en pausa...");
        recetaManager.estadoBalanza=RecetaManager.PAUSA;
    }

    private void estadoProceso() {
        procesoEjecucionManual();
        recetaManager.estadoBalanza=RecetaManager.PROCESO;
    }

    private void estadoDetenido() {
        if(recetaManager.estadoBalanza== RecetaManager.ESTABILIZANDO ||recetaManager.estadoBalanza== RecetaManager.PROCESO){
            automaticoFinalizado();
            recetaManager.estadoBalanza=RecetaManager.DETENIDO;
        }
    }

    private void automaticoFinalizado() {
        setupAutomatic(false);
        int nuevoIndice=mainClass.bza.Itw410FrmGetUltimoIndice(mainClass.nBza);
        if(nuevoIndice>preferencesManager.getIndice()){
            String ultimoPeso=mainClass.bza.Itw410FrmGetUltimoPeso(mainClass.nBza);
            preferencesManager.setIndice(nuevoIndice);
            if(isNumeric(ultimoPeso)) btPesar(Float.parseFloat(ultimoPeso),ultimoPeso);
        }
    }

    private void procesoEjecucionManual() {
        String numPaso=recetaManager.pasoActual +"/"+recetaManager.listRecetaActual.size();
        binding.tvNumpaso.setText(numPaso);
        if(recetaManager.estado==2){
            actualizaNetoTotales();
            if(recetaManager.pasoActual<=recetaManager.listRecetaActual.size()){
                calculoActualizaLimites();
                mainClass.nBza =viewModel.determinarBalanza();
                viewModel.actualizarBarraProceso(mainClass.nBza,mainClass.bza.getUnidad(mainClass.nBza));
                labelPreferencesViewModel.updateCurrentIngredient(recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getDescIng(), recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getCodigoIng());
            }
        }
    }

    private void actualizaNetoTotales() {
        float kilosTotales = viewModel.calculaNetoTotal();
        recetaManager.netoTotal.setValue(mainActivity.mainClass.bza.format(mainActivity.mainClass.nBza,String.valueOf(kilosTotales)));
    }

    private void calculoActualizaLimites() {
        String setPointstr= recetaManager.listRecetaActual.get(recetaManager.pasoActual - 1).getKilosIng();
        if(Utils.isNumeric(setPointstr)){
            labelManager.okilos.value=setPointstr;
            recetaManager.setPoint=Float.parseFloat(setPointstr);
            float lim_max=((100+Integer.parseInt(preferencesManager.getTolerancia()))*recetaManager.setPoint)/100;
            float lim_min=((100-Integer.parseInt(preferencesManager.getTolerancia()))*recetaManager.setPoint)/100;
            actualizarBarraProgreso(lim_max);
            actualizarDisplayPeso(lim_min,lim_max);
        }
    }

    private void actualizarDisplayPeso(Float limMin, Float limMax) {
        float pesoNeto = mainActivity.mainClass.bza.getNeto(mainActivity.mainClass.nBza);
        if(isInRange(limMin, limMax, pesoNeto))setupViewDisplay(R.drawable.boton_selector_balanza_enrango,R.drawable.flecha_abajoblanca,View.INVISIBLE, RecetaManager.BUENO);
        if(isLowRange(limMin, pesoNeto))setupViewDisplay(R.drawable.boton_selector_balanza_fuerarango,R.drawable.flecha_arribablanca,View.VISIBLE,RecetaManager.BAJO);
        if(isHighRange(limMax, pesoNeto))setupViewDisplay(R.drawable.boton_selector_balanza_fuerarango,R.drawable.flecha_abajoblanca,View.VISIBLE,RecetaManager.ALTO);
        setupTaraView(R.drawable.tare_white);
    }

    private static boolean isHighRange(Float limMax, float pesoNeto) {
        return pesoNeto > limMax;
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
        if(Utils.isNumeric(mainActivity.mainClass.bza.getTaraDigital(mainActivity.mainClass.nBza))){
            if(isTareZero()){
                binding.imTare.setImageDrawable(ContextCompat.getDrawable(requireContext(),color));
                binding.imTare.setVisibility(View.VISIBLE);
            }else{
                binding.imTare.setVisibility(View.INVISIBLE);
            }
        }
    }

    private boolean isTareZero() {
        return mainActivity.mainClass.bza.getTara(mainActivity.mainClass.nBza) != 0 ||
                Float.parseFloat(mainActivity.mainClass.bza.getTaraDigital(mainActivity.mainClass.nBza)) != 0;
    }

    private void setupViewsColor(int Color, int Rcolor, int estable) {
        binding.tvNeto.setTextColor(Color);
        binding.tvBruto.setTextColor(Color);
        binding.lnLinea.setBackgroundResource(Rcolor);
        binding.tvNetoUnidad.setTextColor(Color);
        binding.tvBrutoUnidad.setTextColor(Color);
        binding.imEstable.setImageDrawable(ContextCompat.getDrawable(requireContext(),estable));
    }

    private void actualizarBarraProgreso(Float limMax) {
        float pesoNeto=mainActivity.mainClass.bza.getNeto(mainActivity.mainClass.nBza);
        if(pesoNeto>=0){
            float porcentaje= 100*pesoNeto/recetaManager.setPoint;
            int progresoRango=R.drawable.progress;
            int progresoFuera=R.drawable.progress2;
            if(recetaManager.automatico) progresoRango=R.drawable.progressautomatico;
            if(recetaManager.automatico) progresoFuera=R.drawable.progressautomatico2;
            updateViewProgressBarStyle(limMax, pesoNeto, porcentaje, progresoRango, progresoFuera);
        }else{
            binding.progressBar.setProgress(0);
            setupProgressBarPasoStyle(Color.BLACK);
        }
    }

    private void updateViewProgressBarStyle(Float limMax, float pesoNeto, float porcentaje, int progresoRango, int progresoFuera) {
        if(porcentaje <=100){
            binding.progressBar.setProgress((int) porcentaje);
            setupProgressBarStyle(progresoRango,Color.BLACK);
        }else{
            if(isHighRange(limMax, pesoNeto)){
                setupProgressBarStyle(progresoFuera,Color.WHITE);
            }else{
                setupProgressBarStyle(progresoRango,Color.BLACK);
            }
            binding.progressBar.setProgress(100);
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

}
