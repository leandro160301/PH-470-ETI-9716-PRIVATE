package com.service.Balanzas.Fragments;

import static com.service.Utils.Mensaje;
import static com.service.Utils.getFecha;
import static com.service.Utils.getHora;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.service.Balanzas.BalanzaService;
import com.service.Balanzas.Clases.ITW410;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import com.service.Comunicacion.OnFragmentChangeListener;
import com.service.PuertosSerie.PuertosSerie;
import com.service.PuertosSerie.PuertosSerie2;
import com.service.R;
import com.service.Utils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CalibracionItw410Fragment extends Fragment {

    Button bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6;
    static AppCompatActivity mainActivity;

    static ITW410 BZA;
    private ButtonProvider buttonProvider;
    PuertosSerie2.SerialPortReader reader=null;
    int subnombre=0;

    //private final Context context;
    PuertosSerie2.PuertosSerie2Listener receiver =null;
    private PuertosSerie serialPort;
    Toast toast=null;

    TextView titulo;
    ProgressBar loadingPanel;
    TextView tvCarga;
    Button Guardar;
    BalanzaService Service;
    Handler mHandler= new Handler();

    public String estado=M_MODO_CALIBRACION;
    public static final String M_VERIFICANDO_MODO="VERIFICANDO_MODO";
    public static final String M_MODO_BALANZA="MODO_BALANZA";
    public static final String M_MODO_CALIBRACION="MODO_CALIBRACION";
    public static final String M_ERROR_COMUNICACION="M_ERROR_COMUNICACION";
    public float taraDigital=0,Bruto=0,Tara=0,Neto=0,pico=0;
    public String estable="";
    float pesoUnitario=0.5F;
    float pesoBandaCero=0F;
    public Boolean bandaCero =true,btSeteobool=true,bt_homebool=true,bt_resetbool=true,btCalibracionbool=true,enviarparambool=true,bt_iniciarCalibracionbool=true,btReajusteCerobool=true;
    public Boolean inicioBandaPeso=false;
    public int puntoDecimal=1;
    public String ultimaCalibracion="";
    public String brutoStr="0",netoStr="0",taraStr="0",taraDigitalStr="0",picoStr="0";
    public int acumulador=0;
    public int numero=1;


    ImageView animbutton,imgCal;
    LinearLayout togglediv,Lcalibracion;
    Button bt_iniciarCalibracion,btReajusteCero,btCalibracion;
    AlertDialog dialog, dialog1;
    Spinner sp_divisionMinima;
    Spinner sp_puntoDecimal;
    Spinner sp_unidad;
    Spinner sp_bot;
    Spinner sp_reg;
    Spinner sp_uni;
    Spinner sp_ano;
    Spinner sp_dat;
    Spinner sp_bf1;
    Spinner sp_acu;
     Spinner sp_pro;
    Spinner sp_off;
    Spinner sp_bps;
    Boolean lasttanque = true;
    TextView tv_pesoConocido,tv_ultimaCalibracion,tv_filtros1,tv_filtros2,tv_filtros3;
    RadioGroup toggle1,toggle2,toggle3,toggle4,toggle5,toggle6,toggle7,toggle8;
    RadioButton OFF1,OFF2,OFF3,OFF4,OFF5,OFF6,OFF7,OFF8, ON1,ON2,ON3,ON4,ON5,ON6,ON7,ON8;

    int indiceCalibracion=1;
    ConstraintLayout table_parametrosPrincipales;
    String read;
    View viewMang=null;

    Boolean stoped=false;
    private boolean isCollapsed = false;
    private int initialWidth = 258;
   // private OPTIMA_I bza;


    private OnFragmentChangeListener fragmentChangeListener;

    public static CalibracionItw410Fragment newInstance(ITW410 instance, BalanzaService service) {
        CalibracionItw410Fragment fragment = new CalibracionItw410Fragment();
        Bundle args = new Bundle();
        args.putSerializable("instance", instance);
        args.putSerializable("instanceService", service);

        fragment.setArguments(args);
        return fragment;
    }
    public void setFragmentChangeListener(OnFragmentChangeListener listener) {
        this.fragmentChangeListener = listener;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //mainActivity=(MainActivity)getActivity();
         viewMang = inflater.inflate(R.layout.standar_calibracion_v2_410,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        estado=M_MODO_CALIBRACION;
        if (getArguments() != null) {
            BZA = (ITW410) getArguments().getSerializable("instance");
            BZA.estado=M_MODO_CALIBRACION;
            Service = (BalanzaService) getArguments().getSerializable("instanceService");
                mainActivity = Service.activity;
        }
        return viewMang;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        configuracionBotones();
        initializeViews(view);
        setClickListeners();


    }

    private void collapseLinearLayout(final ConstraintLayout linearLayout) {
        ValueAnimator animator = ValueAnimator.ofInt(initialWidth, 0);
        animator.setDuration(300); // Duración de la animación en milisegundos
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
                layoutParams.width = (int) valueAnimator.getAnimatedValue();

                linearLayout.setLayoutParams(layoutParams);
                isCollapsed=true;

            }
        });
        animator.start();
    }

    private void expandLinearLayout(final ConstraintLayout linearLayout) {
        ValueAnimator animator = ValueAnimator.ofInt(0, initialWidth);
        animator.setDuration(300); // Duración de la animación en milisegundos
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                ViewGroup.LayoutParams layoutParams = linearLayout.getLayoutParams();
                layoutParams.width = (int) valueAnimator.getAnimatedValue();
                linearLayout.setLayoutParams(layoutParams);
                isCollapsed=false;
                 }
        });
        animator.start();
    }

    private void configuracionBotones() {
        if (buttonProvider != null) {

            bt_home = buttonProvider.getButtonHome();
            bt_1 = buttonProvider.getButton1();
            bt_2 = buttonProvider.getButton2();
            bt_3 = buttonProvider.getButton3();
            bt_4 = buttonProvider.getButton4();
            bt_5 = buttonProvider.getButton5();
            bt_6 = buttonProvider.getButton6();

            bt_1.setVisibility(View.INVISIBLE);
            bt_2.setVisibility(View.INVISIBLE);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);

            bt_home.setOnClickListener(view1 -> {
                if(bt_homebool){
                    bt_homebool=false;
                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                estado= BZA.M_MODO_BALANZA;
                                //Thread.sleep(1000);
                                 BZA.Guardar_cal();
                                //mainActivity.Puerto_A().write(mainActivity.MainClass.BZA1.Guardar_cal());
                                //      procesarerror(8,null,null);
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                                        View mView = getLayoutInflater().inflate(R.layout.dialogo_calibracion_optima, null);

                                         titulo=mView.findViewById(R.id.textViewt);
                                         loadingPanel=mView.findViewById(R.id.loadingPanel);
                                         tvCarga=mView.findViewById(R.id.tvCarga);
                                         Guardar =  mView.findViewById(R.id.buttons);
                                        Button Cancelar =  mView.findViewById(R.id.buttonc);
                                        Cancelar.setVisibility(View.INVISIBLE);
                                        Guardar.setVisibility(View.INVISIBLE);
                                        titulo.setText("espere un momento...");
                                        //Guardar.setClickable(false);

                                        mBuilder.setView(mView);
                                        dialog = mBuilder.create();
                                        dialog.show();

                                    }

                                });
                                Thread.sleep(2000) ;
                                if(!stoped){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            estado= BZA.M_MODO_BALANZA;
                                            BZA.setPesoUnitario( BZA.getPesoUnitario());
                                            BZA.estado =  BZA.M_MODO_BALANZA;
                                            BZA.salir_cal();
                                            // method.invoke(activity);
                                            //mainActivity.MainClass.openFragmentPrincipal();
                                            bt_homebool=true;
                                            dialog.cancel();

                                        }
                                    });
                                }


                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();

                }
            });

        }
    }
        //--

   // }
    private String DevuelveHora(){
        String Fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        Calendar calendar = Calendar.getInstance();
        int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        String hora24=String.valueOf(hour24hrs);
        String minutos=String.valueOf(minutes);
        String segundos=String.valueOf(seconds);
        String Hora= hora24 +":"+minutos+":"+segundos;

        return Hora;
    }
    private String DevuelveFecha(){
        String Fecha = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        Calendar calendar = Calendar.getInstance();
        int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        String hora24=String.valueOf(hour24hrs);
        String minutos=String.valueOf(minutes);
        String segundos=String.valueOf(seconds);
        String Hora= hora24 +":"+minutos+":"+segundos;


        return Fecha;
    }
    public void procesarMensajePrueba(String Mensaje) {

        if (Mensaje.contains("\u0006D")) {
            BZA.Guardar_cal();
        }
        if (Mensaje.contains("\u0006T")) {
            bt_resetbool = true;

        }
        if (Mensaje.contains("\u0006P")) {
            BZA.Guardar_cal();
        }
        if (Mensaje.contains("\u0006M ")) {
            BZA.Guardar_cal();
            // mainActivity.Puerto_A().write(mainActivity.MainClass.BZA1.Guardar_cal());
            mainActivity.runOnUiThread(() -> {
                btReajusteCerobool = true;
                dialog.cancel();
            });
        }
        if (Mensaje.contains("\u0006U")) {

        }
        ;
        if (Mensaje.contains("\u0006L ")) {
            // System.out.println("WAKAWAKA LASTTANK"+ lasttanque);


        }
        if (Mensaje.contains("\u0006Z ")) {
        }

    }


    private void initializeViews(View view) {
        bt_iniciarCalibracion = view.findViewById(R.id.btIniciarCalibracion);
        btReajusteCero = view.findViewById(R.id.btReajusteCero);
        table_parametrosPrincipales = view.findViewById(R.id.TableParametrosprincipales);
        sp_divisionMinima = view.findViewById(R.id.spDivisionMinima);
        sp_puntoDecimal = view.findViewById(R.id.spPuntoDecimal);
        sp_puntoDecimal = view.findViewById(R.id.spPuntoDecimal);
        sp_unidad = view.findViewById(R.id.spUnidad);
        tv_pesoConocido = view.findViewById(R.id.tvPesoconocido);
        tv_filtros1 = view.findViewById(R.id.tv_filtro1X);
        tv_filtros2 = view.findViewById(R.id.tv_filtro2X);
        tv_filtros3 = view.findViewById(R.id.tv_filtro3X);
        //-- NUEVO
         animbutton = view.findViewById(R.id.animbutton);
         togglediv = view.findViewById(R.id.togglediv);
         imgCal = view.findViewById(R.id.imgCal);
         Lcalibracion = view.findViewById(R.id.Lcalibracion);
         btCalibracion = view.findViewById(R.id.btCalibracion);
        //PuertosSerie Puerto_A= new PuertosSerie();
      //  mainActivity.MainClass.BZA1.serialport //=mainActivity.MainClass.BZA1.mainActivity.MainClass.BZA1.serialport;//Puerto_A.open(PUERTO_A,9600,1,8,0,0,0);

        if ( BZA!= null) {
            // Crear instancia del receptor de datos

            // Crear instancia del lector de puerto serial y pasar el receptor
            PuertosSerie2.PuertosSerie2Listener receiver = new PuertosSerie2.PuertosSerie2Listener() {
                @Override
                public void onMsjPort(String data) {

                    System.out.println("ITW 410 MSJ:"+data);
                    procesarMensajePrueba(data);

                         //    procesarerror(8,null,null);
                }
            };



            // Para detener la lectura más tarde, puedes llamar a:
            // reader.stopReading();
        } else {
            System.out.println("No se pudo abrir el puerto.");
        }

   

        collapseLinearLayout(table_parametrosPrincipales);
        togglediv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCollapsed) {
                    expandLinearLayout(table_parametrosPrincipales);
                    animbutton.setRotation(180);
                } else {
                    collapseLinearLayout(table_parametrosPrincipales);
                    animbutton.setRotation(0);
                }
               // isCollapsed = !isCollapsed;
            }
        });
        animbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCollapsed) {
                    expandLinearLayout(table_parametrosPrincipales);
                    animbutton.setRotation(180);
                } else {
                    collapseLinearLayout(table_parametrosPrincipales);
                    animbutton.setRotation(0);
                }
               // isCollapsed = !isCollapsed;
            }
        });
       
        String[] DivisionMin_arr = getResources().getStringArray(R.array.DivisionMinima);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, DivisionMin_arr);
        adapter2.setDropDownViewResource(R.layout.item_spinner);
        sp_divisionMinima.setAdapter(adapter2);

        String[] unidad_arr = getResources().getStringArray(R.array.Unidad);
        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, unidad_arr);
        adapter3.setDropDownViewResource(R.layout.item_spinner);
        sp_unidad.setAdapter(adapter3);

        String[] puntoDecimal_arr = getResources().getStringArray(R.array.PositionDecimal);
        ArrayAdapter<String> adapter4 = new ArrayAdapter<>(requireContext(), R.layout.item_spinner, puntoDecimal_arr);
        adapter4.setDropDownViewResource(R.layout.item_spinner);
        sp_puntoDecimal.setAdapter(adapter4);
        sp_divisionMinima.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_puntoDecimal.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_puntoDecimal.setSelection(BZA.get_PuntoDecimal());
        sp_puntoDecimal.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                BZA.set_PuntoDecimal(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Código a ejecutar cuando no se selecciona nada
            }
        });
        sp_unidad.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        tv_ultimaCalibracion=view.findViewById(R.id.tvUltimaCalibracion);
        tv_pesoConocido.setText( BZA.get_PesoConocido());
         tv_ultimaCalibracion.setText( BZA.get_UltimaCalibracion());
        if(Objects.equals( BZA.getUnidad(), "ton")){
            sp_unidad.setSelection(2);
        }else if(Objects.equals( BZA.getUnidad(), "gr")){
            sp_unidad.setSelection(0);
        }else{
            sp_unidad.setSelection(1);
        }
        sp_unidad.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0){
                    BZA.setUnidad("gr");
                }
                if(i==1){
                    BZA.setUnidad("kg");
                }
                if(i==2){
                    BZA.setUnidad("ton");
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }
    private void setClickListeners() {
        btReajusteCero.setOnClickListener(view -> { // SIN PROBAR
            if(btReajusteCerobool) {
                btReajusteCerobool = false;

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                View mView = getLayoutInflater().inflate(R.layout.dialogo_transferenciaarchivo, null);
                TextView textView = mView.findViewById(R.id.textView);
                textView.setText("Re ajustando...");
                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        BZA.Recero_cal();
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {

                                btReajusteCerobool = true;
                                dialog.cancel();

                            }
                        });
                    }
                }).start();
            }
        });
        bt_iniciarCalibracion.setOnClickListener(view12 -> {
            if(bt_iniciarCalibracionbool) {
                bt_iniciarCalibracionbool = false;
                String CapDivPDecimal =  "";
                if(CapDivPDecimal!=null){
                    inicioCalibracion(CapDivPDecimal);
                    BZA.set_PuntoDecimal(sp_puntoDecimal.getSelectedItemPosition());
                }else{
                    Mensaje("Revisa la capacidad, division minima y  el punto decimal",R.layout.item_customtoasterror, Service.activity);
                    bt_iniciarCalibracionbool=true;
                }
            }
            });
        final ArrayList<String>[] listdat = new ArrayList[1];
        new Thread(new Runnable() {
            @Override
            public void run() {
                puntoDecimal= BZA.get_PuntoDecimal();
                ArrayList<String> listdata=   BZA.Pedirparam();
               // String x = BZA.format(numero,String.valueOf(listdata.get(0)));
              //  tv_pesoConocido.setText(x);
                try {
                    tv_pesoConocido.setText(BZA.format(numero,String.valueOf(BZA.formatpuntodec(Integer.parseInt(listdata.get(0))))));
                tv_filtros1.setText(listdata.get(2));
                tv_filtros2.setText(listdata.get(3));
                tv_filtros3.setText(listdata.get(4));
                listdat[0] =listdata;
                switch (listdata.get(1)){
                    case "1":{
                        sp_divisionMinima.setSelection(0);
                        break;
                    }
                    case "2":{
                        sp_divisionMinima.setSelection(1);
                        break;
                    }
                    case "5":{
                        sp_divisionMinima.setSelection(2);
                        break;
                    }
                    default:{
                        //none
                    }
                } }catch(Exception e){
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.Mensaje("Error al leer los datos",R.layout.item_customtoasterror,mainActivity);
                        }});
                }
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        tv_pesoConocido.setOnClickListener(view112 ->
                                mainActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Dialog(tv_pesoConocido, listdat[0] .get(0), "Peso Conocido");
                                    }
                                })
                        );


                       
                        tv_filtros1.setOnClickListener(view112 -> mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Dialog(tv_filtros1, listdat[0] .get(2), "Filtro 1");
                            }
                        }));
                        tv_filtros2.setOnClickListener(view112 -> mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Dialog(tv_filtros2, listdat[0] .get(3), "Filtro 2");
                            }
                        }));

                      
                        tv_filtros3.setOnClickListener(view112 ->
                                mainActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Dialog(tv_filtros3, listdat[0].get(4), "Filtro 3");
                                    }
                                })
                        );


                    }

                });
            }
            }).start();



    }
    private void enviarpddiv(ArrayList<Integer> CapDivPDecimal) throws InterruptedException {
        BZA.enviarParametros(CapDivPDecimal);


      }
    private void inicioCalibracion(String CapDivPDecimal) {
        indiceCalibracion=1;
        final Boolean[] bt_guardarbool = {true};
        if(CapDivPDecimal!="ERRCONTROL")
        {
            BZA.set_DivisionMinima(sp_divisionMinima.getSelectedItemPosition());
            BZA.set_PuntoDecimal(sp_puntoDecimal.getSelectedItemPosition());
        }
         AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialogo_calibracion_optima, null);
        titulo=mView.findViewById(R.id.textViewt);
         loadingPanel=mView.findViewById(R.id.loadingPanel);
         tvCarga=mView.findViewById(R.id.tvCarga);
         Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);
        Cancelar.setVisibility(View.INVISIBLE);
        titulo.setText("");
        mBuilder.setView(mView);
        dialog1 = mBuilder.create();
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();
        Runnable myRunnable = () -> {
            try {
                ArrayList<Integer> Listavals = new ArrayList<>();
                Listavals.add(Integer.parseInt(sp_divisionMinima.getSelectedItem().toString()));
                Listavals.add(Integer.parseInt(BZA.format(numero,tv_pesoConocido.getText().toString()).replace(".","")));
                Listavals.add(Integer.parseInt(tv_filtros1.getText().toString()));
                Listavals.add(Integer.parseInt(tv_filtros2.getText().toString()));
                Listavals.add(Integer.parseInt(tv_filtros3.getText().toString()));
                 enviarpddiv(Listavals); // //Division minim,Pesoconocido,filtro1,filtro2,filtro3 5vals

                mainActivity.runOnUiThread(() -> {
               titulo.setText("Verifique que la balanza este en cero, luego presione \"SIGUIENTE\"");
                        loadingPanel.setVisibility(View.INVISIBLE);
                        tvCarga.setVisibility(View.INVISIBLE);
                        Guardar.setClickable(true);
                    });
            } catch (InterruptedException e) {
                e.printStackTrace();
                Utils.Mensaje("Peso conocido invalido, Vuelva a intentarlo 1",R.layout.item_customtoasterror,mainActivity);
            }
        };
        Thread myThread = new Thread(myRunnable);
        myThread.start();
        
        Guardar.setOnClickListener(view -> {
            if(bt_guardarbool[0] && !titulo.getText().toString().toLowerCase(Locale.ROOT).contains("espere un momento") && !titulo.getText().toString().toLowerCase(Locale.ROOT).contains("cargando") && loadingPanel.getVisibility()!=View.VISIBLE ){
                bt_guardarbool[0] =false;
                System.out.println("ITW 410 GUARDAR BT "+ indiceCalibracion+ lasttanque);
                switch (indiceCalibracion) {
                    case 1:
                        ejecutarCalibracionCero(Guardar,titulo,loadingPanel,tvCarga,dialog1);
                        break;
                    case 2:
                        ejecutarCalibracionPesoConocido(Guardar,titulo,loadingPanel,tvCarga, dialog1);
                        break;
                    case 3:
                            ejecutarCalibracionRecero(Guardar, titulo, loadingPanel, tvCarga, dialog1);
                        break;

                    default:
                        break;
                }
                indiceCalibracion++;
                bt_guardarbool[0]=true;
        }
    });
        Cancelar.setOnClickListener(view -> dialog1.cancel());
    }
        private void ejecutarCalibracionRecero(Button Guardar, TextView titulo, ProgressBar loadingPanel, TextView tvCarga,AlertDialog dialog) {
        System.out.println("ITW 410 recero");
            indiceCalibracion = 1;
        BZA.setRecerocal();

        titulo.setText("");
        loadingPanel.setVisibility(View.VISIBLE);
        tvCarga.setVisibility(View.VISIBLE);
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_ultimaCalibracion.setText(DevuelveFecha() + " " + DevuelveHora());
                    BZA.set_UltimaCalibracion(DevuelveFecha() + " " + DevuelveHora());
                }
            });
            if (!stoped) {
                try {
                    getActivity().runOnUiThread(() -> {
                        BZA.Guardar_cal();
                        bt_iniciarCalibracionbool = true;

                        if (dialog1 != null) {
                            dialog1.setCancelable(true);
                            dialog1.cancel();
                        }
                    });
                } catch (Exception e) {
                }
            }
    }

    private void ejecutarCalibracionPesoConocido(Button Guardar,TextView titulo,ProgressBar loadingPanel,TextView tvCarga,AlertDialog dialog) {
        String msj = BZA.format(numero,tv_pesoConocido.getText().toString());//,tv_capacidad.getText().toString()
        if(msj!=null){
            BZA.setSpancal();
            titulo.setText("");
            loadingPanel.setVisibility(View.VISIBLE);
            tvCarga.setVisibility(View.VISIBLE);
            BZA.set_PesoConocido(tv_pesoConocido.getText().toString());
            getActivity().runOnUiThread(() -> {
                try {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            titulo.setText("Coloque el recero y luego presione \"SIGUIENTE\"");
                            loadingPanel.setVisibility(View.INVISIBLE);
                            tvCarga.setVisibility(View.INVISIBLE);
                            tv_ultimaCalibracion.setText(DevuelveFecha() + " " + DevuelveHora());
                            BZA.set_UltimaCalibracion(DevuelveFecha() + " " + DevuelveHora());
                        }
                    });
                } catch (Exception e) {
                }
            });
        }else{
            dialog1.cancel();
            bt_iniciarCalibracionbool=true;
            Mensaje("Error, peso conocido fuera de rango de acuerdo a Capacidad/punto decimal elegida", R.layout.item_customtoasterror, Service.activity);
        }
    }
    private void ejecutarCalibracionCero(Button Guardar,TextView titulo,ProgressBar loadingPanel,TextView tvCarga,AlertDialog dialog) {
        System.out.println("ITW 410 cero");
        BZA.setCerocal();
        getActivity().runOnUiThread(() -> {
            TextView textView = titulo;
            String textoCompleto = "Coloque el peso conocido (" + tv_pesoConocido.getText() + sp_unidad.getSelectedItem().toString() + ") y luego presione \"SIGUIENTE\"";
            SpannableStringBuilder builder = new SpannableStringBuilder(textoCompleto);
            RelativeSizeSpan sizeSpan1 = new RelativeSizeSpan(1.3f); // Aumenta el tamaño 1.5 veces
            builder.setSpan(sizeSpan1, 25, ("Coloque el peso conocido (" + tv_pesoConocido.getText() + sp_unidad.getSelectedItem().toString() + ")").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Aplica al texto desde el índice 10 al 15
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    tv_ultimaCalibracion.setText(getFecha() + " " + getHora());
                    BZA.set_UltimaCalibracion(getFecha() + " " + getHora());
                }
            });
            textView.setText(builder);
            //titulo.setText("Coloque el peso conocido"+tv_pesoConocido.getText()+" y luego presione \"SIGUIENTE\"");
            if (loadingPanel != null) {

                loadingPanel.setVisibility(View.INVISIBLE);
                tvCarga.setVisibility(View.INVISIBLE);

            }
            //   Guardar.setClickable(true);
        });


    }

    public void Dialog(TextView textView,String string,String Texto){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialogo_dosopciones, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        final LinearLayout delete_text= mView.findViewById(R.id.lndelete_text);
        delete_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInput.setText("");
            }
        });
        userInput.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
         titulo=mView.findViewById(R.id.textViewt);
        titulo.setText(Texto);
        if(string.equals("Ceroinicial")){
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_DECIMAL);
            userInput.requestFocus();
        }
        else{
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER );
            userInput.requestFocus();
            if(Texto=="Peso Conocido"|| Texto.toLowerCase().contains("filtro") ){ // Peso conocido con coma
                userInput.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_DECIMAL);
                userInput.requestFocus();
            }
        }
        if(!textView.getText().toString().equals("") && !textView.getText().toString().equals("-")){
            userInput.setText(textView.getText().toString());
            userInput.requestFocus();
            userInput.setSelection(userInput.getText().length());
        }

        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(Texto== "Peso Conocido"){
                    if(BZA.format(numero,userInput.getText().toString())!=null){ // ,tv_capacidad.getText().toString()
                        //mainActivity.Puerto_A().write(mainActivity.MainClass.BZA1.Peso_conocido(userInput.getText().toString(),String.valueOf(sp_puntoDecimal.getSelectedItemPosition())));
                       // procesarerror(2,dialog);
                        textView.setText(userInput.getText().toString());
                      //  System.out.println("SETTEXT WOW1");
                    }else{
                        Mensaje("Error, peso conocido fuera de rango de acuerdo a Capacidad/punto decimal elegida", R.layout.item_customtoasterror, Service.activity);
                    }
                } else{
                    textView.setText(userInput.getText().toString());
                  //  System.out.println("SETTEXT WOW2");
                }
                dialog.cancel();
            }
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());

    }

    @Override
    public void onDestroyView() {
        stoped=true;
        super.onDestroyView();
    }



}


