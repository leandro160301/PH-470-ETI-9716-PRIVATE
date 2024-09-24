package com.service.Balanzas.Fragments;

import static com.service.Utils.Mensaje;
import static com.service.Utils.getFecha;
import static com.service.Utils.getHora;

import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.Service;
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
import com.service.Balanzas.Clases.OPTIMA_I;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import com.service.Comunicacion.OnFragmentChangeListener;
import com.service.PuertosSerie.PuertosSerie;
import com.service.PuertosSerie.PuertosSerie2;
import com.service.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class CalibracionOptimaFragment extends Fragment {

    Button bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6;
    static AppCompatActivity mainActivity;

    static com.service.Balanzas.Clases.OPTIMA_I BZA;
    private ButtonProvider buttonProvider;
    PuertosSerie2.SerialPortReader reader=null;

    //private final Context context;
    PuertosSerie2.PuertosSerie2Listener receiver =null;
    private PuertosSerie serialPort;
    Toast toast=null;

    TextView titulo;
    ProgressBar loadingPanel;
    TextView tvCarga;
    Button Guardar;

    Handler mHandler= new Handler();

    public String estado="VERIFICANDO_MODO";
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
    boolean BooleanRESET=false;


    ImageView animbutton,imgseteo,imgCal;
    LinearLayout togglediv,Lseteo,Lcalibracion;



    Button bt_iniciarCalibracion,btReajusteCero,bt_reset,btCalibracion,btSeteo;
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
    TextView tv_pesoConocido, tv_capacidad,tv_ultimaCalibracion;
    RadioGroup toggle1,toggle2,toggle3,toggle4,toggle5,toggle6,toggle7,toggle8;
    RadioButton OFF1,OFF2,OFF3,OFF4,OFF5,OFF6,OFF7,OFF8, ON1,ON2,ON3,ON4,ON5,ON6,ON7,ON8;

    int indiceCalibracion=1;
    ConstraintLayout table_parametrosPrincipales;
    String read;
    View viewMang=null;
    BalanzaService Service;

    Boolean stoped=false;
    private boolean isCollapsed = false;
    private int initialWidth = 258;
   // private OPTIMA_I bza;


    private OnFragmentChangeListener fragmentChangeListener;

    public static CalibracionOptimaFragment newInstance(OPTIMA_I instance, BalanzaService service) {
        CalibracionOptimaFragment fragment = new CalibracionOptimaFragment();
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
         viewMang = inflater.inflate(R.layout.standar_calibracion_v2,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        estado=M_MODO_CALIBRACION;
        if (getArguments() != null) {
            BZA = (OPTIMA_I) getArguments().getSerializable("instance");
            Service = (BalanzaService) getArguments().getSerializable("instanceService");
            mainActivity = BZA.mainActivity;
        }
        return viewMang;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);

        // Crear instancia del receptor de datos

        // Iniciar la lectura

        configuracionBotones();
        initializeViews(view);
        setClickListeners();
        String[] Lista0aF = new String[16];
        for (int i = 0; i < 16; i++) {
            Lista0aF[i] = Integer.toHexString(i).toUpperCase();
        }
        ArrayAdapter<String> adapter11 = new ArrayAdapter<>(requireContext(),R.layout.item_spinner,Lista0aF);
        adapter11.setDropDownViewResource(R.layout.item_spinner);
        sp_acu.setAdapter(adapter11);
        ArrayAdapter<String> adapter13= new ArrayAdapter<>(requireContext(),R.layout.item_spinner,Lista0aF);
        adapter13.setDropDownViewResource(R.layout.item_spinner);
        sp_off.setAdapter(adapter13);
        toggle4.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton bton= view.findViewById(R.id.btON4);
            RadioButton btoff = view.findViewById(R.id.btOFF4);
            if(toggle4.getCheckedRadioButtonId()==R.id.btON4){
                btoff.setText("NO");
                bton.setText("SI");
                btoff.setTextColor(getResources().getColor(R.color.negro));
                bton.setTextColor(getResources().getColor(R.color.blanco));
            } else {

                bton.setTextColor(getResources().getColor(R.color.negro));
                btoff.setTextColor(getResources().getColor(R.color.blanco));
                btoff.setText("NO");
                bton.setText("SI");

            }
        });
        toggle2.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton bton= view.findViewById(R.id.btON2);
            RadioButton btoff = view.findViewById(R.id.btOFF2);
            if(toggle2.getCheckedRadioButtonId()==R.id.btON2){
                btoff.setTextColor(getResources().getColor(R.color.negro));
                bton.setTextColor(getResources().getColor(R.color.blanco));
                btoff.setText("NO");
                bton.setText("SI");
            } else {
                btoff.setText("NO");
                bton.setText("SI");
                bton.setTextColor(getResources().getColor(R.color.negro));
                btoff.setTextColor(getResources().getColor(R.color.blanco));
            }

        });
        toggle8.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton bton= view.findViewById(R.id.btON8);
            RadioButton btoff = view.findViewById(R.id.btOFF8);
            if (toggle8.getCheckedRadioButtonId() == R.id.btON8) {
                btoff.setText("NO");
                bton.setText("SI");

                btoff.setTextColor(getResources().getColor(R.color.negro));
                bton.setTextColor(getResources().getColor(R.color.blanco));
            } else {
                btoff.setText("NO");
                bton.setText("SI");

                bton.setTextColor(getResources().getColor(R.color.negro));
                btoff.setTextColor(getResources().getColor(R.color.blanco));
                    }
        });

        ArrayAdapter<String> adapter12= new ArrayAdapter<>(requireContext(),R.layout.item_spinner,Lista0aF);
        adapter12.setDropDownViewResource(R.layout.item_spinner);
        sp_pro.setAdapter(adapter12);
        /*new Thread() {
            @Override
            public void run() {
                try {
                    String strbin ="";
                    int promvar=0;
                    int offvar=0;
                    int acuvar=0;
                    mainActivity.MainClass.mainactivity.MainClass1.Pedirparam();
                    Thread.sleep(500);
                    strbin = mainActivity.MainClass.mainactivity.MainClass1.LeerParam1();


                    promvar = Integer.parseInt(strbin.substring(9,10),16);
                    offvar = Integer.parseInt(strbin.substring(10,11),16);
                    acuvar = Integer.parseInt(strbin.substring(11,12),16);
                //    procesarerror(8,null,null);
                        strbin=strbin.substring(0,8);

                    char[] charstr =  strbin.toCharArray();

                    System.out.println("OPTIMA"+strbin);
                    if (charstr[0]=='0') {
                        toggle1.check(R.id.btOFF1);
                    }


                    if (charstr[1]=='0') {
                        toggle2.check(R.id.btOFF2);
                    }
                    if (charstr[2]=='0') {
                        toggle3.check(R.id.btOFF3);
                    }
                    lasttanque=charstr[2] == '1';
                    if (charstr[3]=='0') {
                        toggle4.check(R.id.btOFF4);
                    }

                    //if (charstr[4]=='0') {
                     //   toggle5.check(R.id.btOFF5);
                    //}
                    ///if (charstr[5]=='0') {
                      //  toggle6.check(R.id.btOFF6);
                    //}
                    //if (charstr[6]=='0') {
                     //   toggle7.check(R.id.btOFF7);
                    //}

                    if (charstr[7]=='0') {
                        toggle8.check(R.id.btOFF8);
                    }
                    if(promvar!=0){

                        int finalPromvar = promvar;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("OPTIMA"+ finalPromvar);
                                sp_pro.setSelection(finalPromvar);
                            }
                        });
                        int finaloffvar = offvar;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("OPTIMA"+ finaloffvar);
                                sp_off.setSelection(finaloffvar);
                            }
                        });
                        int finalacuvar = acuvar;
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                System.out.println("OPTIMA"+ finalacuvar);
                                sp_acu.setSelection(finalacuvar);
                            }
                        });



                    }
                }catch(Exception e){

                }
            }
        }.start();*/







    }
private String leertoggles(RadioGroup toggle,Integer id){ //NUEVO
        String param1 ="";
        if(toggle.getCheckedRadioButtonId()==id){
          /*  if(id == R.id.btON3){
                Boolean a = toggle.getCheckedRadioButtonId() == R.id.btON3;
                System.out.println("lastank optima " +  a);
                lasttanque = a;
            }*/
            return "1";

        }else{

            return "0";

        }
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

//                                BZA.Guardar_cal();
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
                                Thread.sleep(2000) ; //pa minima
                                //Thread.sleep(1000);//pa optima
                                BZA.serialPort.write( BZA.Salir_cal());

                                Thread.sleep(14000);
                                if(!stoped){
                                    getActivity().runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            estado= BZA.M_MODO_BALANZA;
                                            BZA.setPesoUnitario( BZA.getPesoUnitario());
                                            BZA.estado =  BZA.M_MODO_BALANZA;

                                            // method.invoke(activity);
                                            try {
                                                estado= BZA.M_MODO_BALANZA;
                                                BZA.setPesoUnitario( BZA.getPesoUnitario());
                                                BZA.estado =  BZA.M_MODO_BALANZA;
                                                Service.fragmentChangeListener.openFragmentPrincipal();
                                                bt_homebool=true;
                                                dialog.cancel();
                                            }catch (Exception e){

                                            }

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
        if(Mensaje.contains("9kg")){
            System.out.println("CICLOS");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
//                long count = Mensaje.chars().filter(ch -> ch == '9').count();
                int count=0;
                for (char ch : Mensaje.toCharArray()) {
                    if (ch == '9') {
                        count++;
                    }
                }

                System.out.println("CICLOS count "+ count);
                if(count==5){
                    try {
                        BZA.readers.startReading();
                        estado= BZA.M_MODO_BALANZA;
                        BZA.setPesoUnitario( BZA.getPesoUnitario());
                        BZA.estado =  BZA.M_MODO_BALANZA;
                        Service.fragmentChangeListener.openFragmentPrincipal();
                        bt_homebool=true;
                        reader.stopReading();
                        dialog.cancel();
                    }catch (Exception e){

                    }
                }
            }

        }
        if(Mensaje.contains("\u0006E")){
            Thread s = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(12000);
                        System.out.println("CICLOS timeoutxxxxx " );

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                BZA.readers.startReading();
                                estado= BZA.M_MODO_BALANZA;
                                BZA.setPesoUnitario( BZA.getPesoUnitario());
                                BZA.estado =  BZA.M_MODO_BALANZA;
                                Service.fragmentChangeListener.openFragmentPrincipal();
                                bt_homebool=true;
                                reader.stopReading();
                                dialog.cancel();
                            }
                        });
                    } catch (InterruptedException e) {

                    }
                }
            });
            s.start();

          }
        if(Mensaje.contains("\u0006D")){
//            BZA.Guardar_cal();
        }
        if(Mensaje.contains("\u0006T")){
            bt_resetbool=true;
            BZA.Pedirparam();
            BooleanRESET=true;

        }

        if(Mensaje.contains("\u0006P")){
            BZA.Guardar_cal();
            enviarparambool=true;
            if( BooleanRESET){
                bt_resetbool = true;
                BooleanRESET=false;
                dialog1.cancel();
            }else{
            dialog.cancel();
            }
        }
        if(Mensaje.contains("\u0006M ")){
//            BZA.Guardar_cal();
            // mainActivity.Puerto_A().write(mainActivity.MainClass.BZA1.Guardar_cal());
            mainActivity.runOnUiThread(() -> {
                BZA.Guardar_cal();
                btReajusteCerobool=true;
                dialog.cancel();
            });
        }
        if (Mensaje.contains("\u0006U")) {

            getActivity().runOnUiThread(() -> {
                TextView textView = titulo;
                String textoCompleto = "Coloque el peso conocido ("+tv_pesoConocido.getText()+sp_unidad.getSelectedItem().toString()+") y luego presione \"SIGUIENTE\"";
                SpannableStringBuilder builder = new SpannableStringBuilder(textoCompleto);
                RelativeSizeSpan sizeSpan1 = new RelativeSizeSpan(1.3f); // Aumenta el tamaño 1.5 veces
                builder.setSpan(sizeSpan1, 25,("Coloque el peso conocido ("+tv_pesoConocido.getText()+sp_unidad.getSelectedItem().toString()+")").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Aplica al texto desde el índice 10 al 15
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_ultimaCalibracion.setText(getFecha()+" "+ getHora());
                        BZA.set_UltimaCalibracion(getFecha()+" "+ getHora());
                    }
                });
                textView.setText(builder);
                 //titulo.setText("Coloque el peso conocido"+tv_pesoConocido.getText()+" y luego presione \"SIGUIENTE\"");
                if(loadingPanel!=null){

                    loadingPanel.setVisibility(View.INVISIBLE);
                    tvCarga.setVisibility(View.INVISIBLE);

                }
                //   Guardar.setClickable(true);
            });

            //    });
            //  }


        };
        if(Mensaje.contains("\u0006L ")){
           // System.out.println("WAKAWAKA LASTTANK"+ lasttanque);
            getActivity().runOnUiThread(() -> {
                if(!lasttanque) {
                    try{
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                titulo.setText("Coloque el recero y luego presione \"SIGUIENTE\"");
                                loadingPanel.setVisibility(View.INVISIBLE);
                                tvCarga.setVisibility(View.INVISIBLE);
                                tv_ultimaCalibracion.setText(DevuelveFecha()+" "+DevuelveHora());
                                BZA.set_UltimaCalibracion(DevuelveFecha()+" "+DevuelveHora());
                            }
                        });
                    }catch (Exception e){

                    }

                }else{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_ultimaCalibracion.setText(getFecha()+" "+ getHora());
                            BZA.set_UltimaCalibracion(getFecha()+" "+ getHora());
                        }
                    });
                    if(dialog1!=null){
                        dialog1.setCancelable(true);
                        dialog1.cancel();
                    }
                    try {

                        bt_iniciarCalibracionbool=true;
                        indiceCalibracion = 1;
                        Thread.sleep(1000);
//                        BZA.Guardar_cal();
                    }catch (Exception e) {

                    }

                }

            });

        }
        if(Mensaje.contains("\u0006Z ")){
            if(lasttanque || indiceCalibracion==4){

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_ultimaCalibracion.setText(DevuelveFecha()+" "+DevuelveHora());
                        BZA.set_UltimaCalibracion(DevuelveFecha()+" "+DevuelveHora());
                    }
                });
                if(!stoped ){
                    try {
                        Thread.sleep(1000);
                        getActivity().runOnUiThread(() -> {
                            BZA.Guardar_cal();

                            bt_iniciarCalibracionbool=true;
                            indiceCalibracion = 1;
                            if(dialog1!=null){

                                dialog1.setCancelable(true);
                                dialog1.cancel();
                            }
                        });
                    }catch (Exception e){

                    }


                }
            }else{
                try{
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            titulo.setText("Coloque el recero y luego presione \"SIGUIENTE\"");
                            loadingPanel.setVisibility(View.INVISIBLE);
                            tvCarga.setVisibility(View.INVISIBLE);
                            tv_ultimaCalibracion.setText(DevuelveFecha()+" "+DevuelveHora());
                            BZA.set_UltimaCalibracion(DevuelveFecha()+" "+DevuelveHora());
                        }
                    });
                }catch (Exception e){

                }
            }
        }

        if (Mensaje.contains("\u0006O ")) {
            try {
                mainActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
//                        try {
                            String strbin = "";
                            int promvar = 0;
                            int offvar = 0;
                            int acuvar = 0;
                            int pdvar = 0;
                            int divmvar = 0;
                            String pd = "0";
                            String divm = "0";
                            String promvars = "0";
                            String offvars = "0";
                            String acuvars = "0";
                            String binario = "";
                            System.out.println(Mensaje);
                            String hex = Mensaje.substring(Mensaje.indexOf("\u0006O ") + 5, Mensaje.indexOf("\u0006O ") + 7);
                            System.out.println("optimaiai1" + hex);
                            promvars = Mensaje.substring(Mensaje.indexOf("\u0006O ") + 38, Mensaje.indexOf("\u0006O ") + 39);
                            System.out.println("optimaiai2" + promvars); // 3
                            offvars = Mensaje.substring(Mensaje.indexOf("\u0006O ") + 37, Mensaje.indexOf("\u0006O ") + 38);
                            System.out.println("optimaiai3" + offvars); // 4
                            acuvars = Mensaje.substring(Mensaje.indexOf("\u0006O ") + 39, Mensaje.indexOf("\u0006O ") + 40);
                            System.out.println("optimaiai4" + acuvars); // 4
                            pd = Mensaje.substring(Mensaje.indexOf("\u0006O ") + 27, Mensaje.indexOf("\u0006O ") + 28);
                            divm = Mensaje.substring(Mensaje.indexOf("\u0006O ") + 28, Mensaje.indexOf("\u0006O ") + 29);
                            System.out.println("OPTIMA pddivmin:" + pd + divm);// necesito leer de la pos 2 sin contar el #006  asta pos 4

                            //14 == 24
                            //                 System.out.println("optimaiai" + acuvars); // 5
                            //                   System.out.println("optimaiai procesado" + hex+ " "+promvars+" "+offvars+" "+acuvars);

                            //System.out.println("OPTIMA ayuwoki:" + hex + " " + promvar);// necesito leer de la pos 2 sin contar el #006  asta pos 4
                            int decimal = Integer.parseInt(hex, 16); // Convertir hexadecimal a decimal
                            binario = Integer.toBinaryString(decimal); // Convertir decimal a binario
                            while (binario.length() < 8) {
                                binario = "0" + binario;
                            }

                            strbin = binario + " " + promvars + offvars + acuvars + pd + divm;

                            promvar = Integer.parseInt(strbin.substring(9, 10), 16);
                            offvar = Integer.parseInt(strbin.substring(10, 11), 16);
                            acuvar = Integer.parseInt(strbin.substring(11, 12), 16);
                            pdvar = Integer.parseInt(strbin.substring(12, 13), 16);
                            divmvar = Integer.parseInt(strbin.substring(13, 14), 16);
                            //    procesarerror(8,null,null);
                            strbin = strbin.substring(0, 8);
                            char[] charstr = strbin.toCharArray();
                            // System.out.println("OPTIMA" + strbin);
                            if (charstr[0] == '0') {
                                ON1.setTextColor(getResources().getColor(R.color.negro));
                                OFF1.setTextColor(getResources().getColor(R.color.blanco));
                                toggle1.check(OFF1.getId());
                            } else {
                                toggle1.check(ON1.getId());

                                OFF1.setTextColor(getResources().getColor(R.color.negro));
                                ON1.setTextColor(getResources().getColor(R.color.blanco));
                            }
                            //inittoggle(toggle1,ON1,OFF1);
                            /*
                             */
                            if (charstr[1] == '0') {
                                //System.out.println(toggle2 + "" + OFF2 + "" + R.id.btOFF2);
                                toggle2.check(OFF2.getId());
                                ON2.setTextColor(getResources().getColor(R.color.negro));
                                OFF2.setTextColor(getResources().getColor(R.color.blanco));

                            } else {
                                toggle2.check(ON2.getId());
                                OFF2.setTextColor(getResources().getColor(R.color.negro));
                                ON2.setTextColor(getResources().getColor(R.color.blanco));

                            }

                            // inittoggle(toggle2,ON2,OFF2);
                            if (charstr[2] == '0') {
                                toggle3.check(OFF3.getId());
                                ON3.setTextColor(getResources().getColor(R.color.negro));
                                OFF3.setTextColor(getResources().getColor(R.color.blanco));
                            } else {
                                toggle3.check(ON3.getId());
                                OFF3.setTextColor(getResources().getColor(R.color.negro));
                                ON3.setTextColor(getResources().getColor(R.color.blanco));
                            }
                            lasttanque = charstr[2] == '1';

                            //inittoggle(toggle3,ON3,OFF3);
                            if (charstr[3] == '0') {
                                toggle4.check(OFF4.getId());
                                ON4.setTextColor(getResources().getColor(R.color.negro));
                                OFF4.setTextColor(getResources().getColor(R.color.blanco));

                            } else {

                                OFF4.setTextColor(getResources().getColor(R.color.negro));
                                ON4.setTextColor(getResources().getColor(R.color.blanco));
                                toggle4.check(ON4.getId());
                            }

                            //inittoggle(toggle4,ON4,OFF4);
                        /*
                        if (charstr[4]=='0') {
                            toggle5.check(R.id.btOFF5);
                        }
                        if (charstr[5]=='0') {
                            toggle6.check(R.id.btOFF6);
                        }
                        if (charstr[6]=='0') {
                            toggle7.check(R.id.btOFF7);
                        }
                        */
                            if (charstr[7] == '0') {
                                toggle8.check(OFF8.getId());

                                ON8.setTextColor(getResources().getColor(R.color.negro));
                                OFF8.setTextColor(getResources().getColor(R.color.blanco));
                            } else {
                                toggle8.check(ON8.getId());

                                OFF8.setTextColor(getResources().getColor(R.color.negro));
                                ON8.setTextColor(getResources().getColor(R.color.blanco));
                            }
                            //inittoggle(toggle8,ON8,OFF8);


                            //  System.out.println("OPTIMA" + promvar);
                            sp_pro.setSelection(promvar);
                            // System.out.println("OPTIMA" + offvar);
                            sp_off.setSelection(offvar);
                            //System.out.println("OPTIMA" + acuvar);
                            sp_acu.setSelection(acuvar);

                            // System.out.println("PDDIVM"+pdvar+" "+ divmvar );
                            if (pdvar == 12) {
                                sp_puntoDecimal.setSelection(0);
                            }
                            if (pdvar == 0) {
                                sp_puntoDecimal.setSelection(1);
                            }
                            if (pdvar == 4) {

                                sp_puntoDecimal.setSelection(2);
                            }
                            if (pdvar == 8) {
                                sp_puntoDecimal.setSelection(3);
                            }
                            if (divmvar == 1) {
                                sp_divisionMinima.setSelection(0);
                            }
                            if (divmvar == 2) {

                                sp_divisionMinima.setSelection(1);
                            }
                            if (divmvar == 5) {
                                sp_divisionMinima.setSelection(2);
                            }
//                        } catch (Exception e) {
//                            Mensaje("Error al momento de leer parametros",R.layout.item_customtoasterror,mainActivity);
//                        }
                    if(BooleanRESET){
                        String param1 = "";
                        param1 += leertoggles(toggle1, R.id.btON1);
                        param1 += leertoggles(toggle2, R.id.btON2);// "0";
                        param1 += leertoggles(toggle3, R.id.btON3);
                        lasttanque= (leertoggles(toggle3, R.id.btON3).equals("1"));
                        param1 += leertoggles(toggle4, R.id.btON4); //"0";
                        param1 += "0";// leertoggles(toggle5,R.id.btON5);
                        param1 += "1";// leertoggles(toggle6,R.id.btON6); // ESTE NECESITA ESTAR EN 0
                        param1 += "1";// leertoggles(toggle7,R.id.btON7);
                        param1 += leertoggles(toggle8, R.id.btON8); // "0";
                        String param2 = "00000000";
                        BZA.serialPort.write(BZA.EnviarParametros(param1, param2, sp_pro.getSelectedItem().toString(), sp_off.getSelectedItem().toString(), sp_acu.getSelectedItem().toString()));
                    }
                    }

                    });
            } catch (Exception e) {

            }
        }
        ArrayList<String> Listerr = new ArrayList<>();
        Listerr = BZA.Errores2(Mensaje);//Errores2(Mensaje);//
        if (Listerr != null) {
            for (int i = 0; i < Listerr.size(); i++) {
                    indiceCalibracion = 1;
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (dialog1 != null) {
                                dialog1.setCancelable(true);
                                dialog1.cancel();
                                dialog1 = null;


                            }
                            if (dialog != null) {

                                dialog.setCancelable(true);
                                dialog.cancel();
                                dialog = null;
                            }
                            bt_iniciarCalibracionbool = true;
                            bt_resetbool = true;
                            btReajusteCerobool = true;

                        }
                    });



                int finalI = i;
                ArrayList<String> finalListerr = Listerr;


                Mensaje(finalListerr.get(finalI).toString(),R.layout.item_customtoasterror,mainActivity);
                    //Toast.makeText(requireContext(),finalListerr.get(finalI).toString(),Toast.LENGTH_SHORT).show();

            }}}


    private void initializeViews(View view) {
        bt_iniciarCalibracion = view.findViewById(R.id.btIniciarCalibracion);
        btReajusteCero = view.findViewById(R.id.btReajusteCero);
        bt_reset = view.findViewById(R.id.btreset);
        table_parametrosPrincipales = view.findViewById(R.id.TableParametrosprincipales);
        sp_divisionMinima = view.findViewById(R.id.spDivisionMinima);
        sp_puntoDecimal = view.findViewById(R.id.spPuntoDecimal);
        sp_puntoDecimal = view.findViewById(R.id.spPuntoDecimal);
        sp_unidad = view.findViewById(R.id.spUnidad);
        tv_capacidad = view.findViewById(R.id.tvCapacidad);

        Button btenviarparam = view.findViewById(R.id.btenviarparam);
        tv_pesoConocido = view.findViewById(R.id.tvPesoconocido);
        //-- NUEVO
        Button btSeteo= view.findViewById(R.id.btSeteo);
        toggle1 = view.findViewById(R.id.toggle1);
        OFF1 = view.findViewById(R.id.btOFF1);
        OFF2 = view.findViewById(R.id.btOFF2);
        OFF3 = view.findViewById(R.id.btOFF3);
        OFF4 = view.findViewById(R.id.btOFF4);
        OFF5 = view.findViewById(R.id.btOFF5);
        OFF6 = view.findViewById(R.id.btOFF6);
        OFF7 = view.findViewById(R.id.btOFF7);
        OFF8 = view.findViewById(R.id.btOFF8);
        ON1 = view.findViewById(R.id.btON1);
        ON2 = view.findViewById(R.id.btON2);
        ON3 = view.findViewById(R.id.btON3);
        ON4 = view.findViewById(R.id.btON4);
        ON5 = view.findViewById(R.id.btON5);
        ON6 = view.findViewById(R.id.btON6);
        ON7 = view.findViewById(R.id.btON7);
        ON8 = view.findViewById(R.id.btON8);

        toggle2 = view.findViewById(R.id.toggle2);
        toggle3 = view.findViewById(R.id.toggle3);
        toggle4 = view.findViewById(R.id.toggle4);
        toggle5 = view.findViewById(R.id.toggle5);
        toggle6 = view.findViewById(R.id.toggle6);
        toggle7 = view.findViewById(R.id.toggle7);
        toggle8 = view.findViewById(R.id.toggle8);
         animbutton = view.findViewById(R.id.animbutton);
         togglediv = view.findViewById(R.id.togglediv);
         Lseteo = view.findViewById(R.id.Lseteo);
         imgseteo = view.findViewById(R.id.imgseteo);
         imgCal = view.findViewById(R.id.imgCal);
         Lcalibracion = view.findViewById(R.id.Lcalibracion);
         btCalibracion = view.findViewById(R.id.btCalibracion);
        //PuertosSerie Puerto_A= new PuertosSerie();
      //  mainActivity.MainClass.BZA1.serialport //=mainActivity.MainClass.BZA1.mainActivity.MainClass.BZA1.serialport;//Puerto_A.open(PUERTO_A,9600,1,8,0,0,0);

        if ( BZA.serialPort!= null) {
            // Crear instancia del receptor de datos

            // Crear instancia del lector de puerto serial y pasar el receptor
            PuertosSerie2.PuertosSerie2Listener receiver = new PuertosSerie2.PuertosSerie2Listener() {
                @Override
                public void onMsjPort(String data) {

                    System.out.println("OPTIMA MSJ:"+data);
                    procesarMensajePrueba(data);

                         //    procesarerror(8,null,null);
                }
            };
            reader = new PuertosSerie2.SerialPortReader( BZA.serialPort.getInputStream(), receiver);

            System.out.println("BZASERIAL  LISTENER SETEADO");
            // Iniciar la lectura
            reader.startReading();


            // Para detener la lectura más tarde, puedes llamar a:
            // reader.stopReading();
        } else {
            System.out.println("No se pudo abrir el puerto.");
        }

        btSeteo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btSeteobool){
                    btSeteobool=false;
                    Runnable myRunnable = () -> {
                        try {
                            BZA.Pedirparam();
                            Thread.sleep(500);
                            mainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imgCal.setVisibility(View.GONE);
                                    Lcalibracion.setVisibility(View.GONE);
                                    imgseteo.setVisibility(View.VISIBLE);
                                    Lseteo.setVisibility(View.VISIBLE);
                                    collapseLinearLayout(table_parametrosPrincipales);
                                    animbutton.setRotation(0);
                                    btSeteobool=true;
                                }
                            });
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }

                    };
                    Thread myThread2 = new Thread(myRunnable);
                    myThread2.start();
                }
                }
        });
        btCalibracion.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if(btCalibracionbool) {
                    btCalibracionbool = false;
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            imgCal.setVisibility(View.VISIBLE);
                            Lcalibracion.setVisibility(View.VISIBLE);
                            imgseteo.setVisibility(View.GONE);
                            Lseteo.setVisibility(View.GONE);
                            collapseLinearLayout(table_parametrosPrincipales);
                            animbutton.setRotation(0);
                            btCalibracionbool = true;
                        }
                    });

                }

                BZA.Pedirparam();
            }
        });

        btenviarparam.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(enviarparambool) {
                    enviarparambool=false;
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                    View mView = getLayoutInflater().inflate(R.layout.dialogo_calibracion_optima, null);

                     titulo = mView.findViewById(R.id.textViewt);
                     loadingPanel = mView.findViewById(R.id.loadingPanel);
                     tvCarga = mView.findViewById(R.id.tvCarga);
                     Guardar = mView.findViewById(R.id.buttons);
                    Button Cancelar = mView.findViewById(R.id.buttonc);
                    Cancelar.setVisibility(View.INVISIBLE);
                    titulo.setText("Espere un momento...");


                    mBuilder.setView(mView);
                    dialog = mBuilder.create();
                    dialog.show();
                    Runnable myRunnable = () -> {
                        //  System.out.println("OPTIMA EERRRR  ");

                        try {
                            ejecutarenviodeparametros();

                        } catch (Exception e) {

                        }
                    };
                    Thread myThread2 = new Thread(myRunnable);
                    myThread2.start();
                }

            }



    });


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
        /*
        sp_bot = view.findViewById(R.id.spbot);
        sp_reg = view.findViewById(R.id.spreg);
        sp_uni = view.findViewById(R.id.spuni);
        sp_ano = view.findViewById(R.id.spano);
        sp_dat = view.findViewById(R.id.spdat);
        sp_bf1 = view.findViewById(R.id.spbf1);
         sp_bps = view.findViewById(R.id.spbps);
        */
        sp_acu = view.findViewById(R.id.spacu);
        sp_off = view.findViewById(R.id.spoff);
        sp_pro = view.findViewById(R.id.sppro);


        //sp_uni.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_pro.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        //sp_ano.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_acu.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        //sp_bf1.setPopupBackgroundResource(R.drawable.campollenarclickeable);
       // sp_bot.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        //sp_bps.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        //sp_dat.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_off.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        //sp_reg.setPopupBackgroundResource(R.drawable.campollenarclickeable);

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



 /*
        ArrayAdapter<String> adapter5 = new ArrayAdapter<>(requireContext(),R.layout.item_spinner,Lista0aF);
        adapter5.setDropDownViewResource(R.layout.item_spinner);
        sp_bot.setAdapter(adapter5);

        ArrayAdapter<String> adapter6 = new ArrayAdapter<>(requireContext(),R.layout.item_spinner,Lista0aF);
        adapter6.setDropDownViewResource(R.layout.item_spinner);
        sp_reg.setAdapter(adapter6);

        ArrayAdapter<String> adapter7 = new ArrayAdapter<>(requireContext(),R.layout.item_spinner,Lista0aF);
        adapter7.setDropDownViewResource(R.layout.item_spinner);
        sp_uni.setAdapter(adapter7);

        ArrayAdapter<String> adapter8= new ArrayAdapter<>(requireContext(),R.layout.item_spinner,Lista0aF);
        adapter8.setDropDownViewResource(R.layout.item_spinner);
        sp_ano.setAdapter(adapter8);

        ArrayAdapter<String> adapter9 = new ArrayAdapter<>(requireContext(),R.layout.item_spinner,Lista0aF);
        adapter9.setDropDownViewResource(R.layout.item_spinner);
        sp_dat.setAdapter(adapter9);

        ArrayAdapter<String> adapter10 = new ArrayAdapter<>(requireContext(),R.layout.item_spinner,Lista0aF);
        adapter10.setDropDownViewResource(R.layout.item_spinner);
        sp_bf1.setAdapter(adapter10);


        */

        toggle1.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton bton= view.findViewById(R.id.btON1);
            RadioButton btoff = view.findViewById(R.id.btOFF1);
            if(toggle1.getCheckedRadioButtonId()==R.id.btON1) {
               btoff.setText("NO");
               btoff.setTextColor(getResources().getColor(R.color.negro));
               bton.setTextColor(getResources().getColor(R.color.blanco));

                bton.setText("SI");
            } else {
                bton.setTextColor(getResources().getColor(R.color.negro));
                btoff.setTextColor(getResources().getColor(R.color.blanco));

                btoff.setText("NO");
                bton.setText("SI");
            }
        });
        /*


        ArrayAdapter<String> adapter14= new ArrayAdapter<>(requireContext(),R.layout.item_spinner,Lista0aF);
        adapter14.setDropDownViewResource(R.layout.item_spinner);
        sp_bps.setAdapter(adapter14);




        toggle5.setOnCheckedChangeListener((radioGroup, i) -> {
            if(toggle5.getCheckedRadioButtonId()==R.id.btON5){

            }else{

            }
        });
        toggle6.setOnCheckedChangeListener((radioGroup, i) -> {
            if(toggle6.getCheckedRadioButtonId()==R.id.btON6){

            }else{

            }
        });
        toggle7.setOnCheckedChangeListener((radioGroup, i) -> {
            if(toggle7.getCheckedRadioButtonId()==R.id.btON7){

            }else{

            }
        });

        });*/
        toggle3.setOnCheckedChangeListener((radioGroup, i) -> {
            RadioButton bton= view.findViewById(R.id.btON3);
            RadioButton btoff = view.findViewById(R.id.btOFF3);

            if(toggle3.getCheckedRadioButtonId()==R.id.btON3){
                btoff.setText("NO");
                bton.setText("SI");
                btoff.setTextColor(getResources().getColor(R.color.negro));
                bton.setTextColor(getResources().getColor(R.color.blanco));
            } else {
                btoff.setText("NO");
                bton.setText("SI");

                bton.setTextColor(getResources().getColor(R.color.negro));
                btoff.setTextColor(getResources().getColor(R.color.blanco));

            }

        });
                //--

        sp_divisionMinima.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_puntoDecimal.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_unidad.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        tv_ultimaCalibracion=view.findViewById(R.id.tvUltimaCalibracion);

       // mainActivity=(MainActivity)getActivity();

        tv_pesoConocido.setText( BZA.get_PesoConocido());
        tv_capacidad.setText( BZA.get_CapacidadMax());
      //  sp_puntoDecimal.setSelection(mainActivity.MainClass.BZA1.get_PuntoDecimal());
       // sp_divisionMinima.setSelection(mainActivity.MainClass.BZA1.get_DivisionMinima());
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
                BZA.serialPort.write( BZA.ReAjusteCero());

                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                View mView = getLayoutInflater().inflate(R.layout.dialogo_transferenciaarchivo, null);
                TextView textView = mView.findViewById(R.id.textView);
                textView.setText("Re ajustando...");
                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();
            }
        });
        bt_reset.setOnClickListener(view -> {
            if(bt_resetbool) {
                bt_resetbool = false;
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());

                View mView = getLayoutInflater().inflate(R.layout.dialogo_dossinet, null);
                TextView textView = mView.findViewById(R.id.textViewt);
                textView.setText("¿Esta seguro de resetear los parametros y la calibracion?");

                 Guardar = mView.findViewById(R.id.buttons);
                Button Cancelar = mView.findViewById(R.id.buttonc);
                Guardar.setText("Resetear");

                mBuilder.setView(mView);
                dialog = mBuilder.create();
                dialog.show();

                Guardar.setOnClickListener(view1 -> {

                    AlertDialog.Builder mBuilder1 = new AlertDialog.Builder(getContext());
                    View mView1 = getLayoutInflater().inflate(R.layout.dialogo_calibracion_optima, null);

                     titulo = mView1.findViewById(R.id.textViewt);
                     loadingPanel = mView1.findViewById(R.id.loadingPanel);
                     tvCarga = mView1.findViewById(R.id.tvCarga);
                    Button Guardar1 = mView1.findViewById(R.id.buttons);
                    Button Cancelar1 = mView1.findViewById(R.id.buttonc);
                    Cancelar1.setVisibility(View.INVISIBLE);
                    titulo.setText("Espere un momento...");
                    Guardar1.setClickable(false);
                    mBuilder1.setView(mView1);
                    dialog.cancel();
                    dialog1 = mBuilder1.create();
                    dialog1.show();


                    BZA.serialPort.write(BZA.reset());
                });

                Cancelar.setOnClickListener(view1 -> {
                    dialog.cancel();
                    bt_resetbool=true;
                });


            }
        });
        bt_iniciarCalibracion.setOnClickListener(view12 -> {
            if(bt_iniciarCalibracionbool) {
                bt_iniciarCalibracionbool = false;
                String CapDivPDecimal =  BZA.CapacidadMax_DivMin_PDecimal(tv_capacidad.getText().toString(), sp_divisionMinima.getSelectedItem().toString(), String.valueOf(sp_puntoDecimal.getSelectedItemPosition()));
                try {
                    BZA.serialPort.flush();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                if(CapDivPDecimal!=null){
                    inicioCalibracion(CapDivPDecimal);
                }else{
                    Mensaje("Revisa la capacidad, division minima y  el punto decimal",R.layout.item_customtoasterror, BZA.mainActivity);
                    bt_iniciarCalibracionbool=true;
                }
            }
            });
            tv_capacidad.setOnClickListener(view110 -> Dialog(tv_capacidad, "", "Capacidad"));
            tv_pesoConocido.setOnClickListener(view112 -> Dialog(tv_pesoConocido, "", "Peso Conocido"));
            BZA.Pedirparam();


        }

    /*private Boolean procesarerror(int indexcalib,AlertDialog dialog,Button boton){
        final ArrayList<String> listErr = new ArrayList();
        final Boolean[] Timeout = {false};
        Runnable myRunnable = () -> {
            System.out.println("OPTIMA EERRRR  ");
            try {
                Thread.sleep(1000);
                while (!Timeout[0]) {
                    if (!stoped && !Timeout[0]) {
                        getActivity().runOnUiThread(() -> {
                            try {
                                if (mainActivity.Puerto_A().HabilitadoLectura()) {
                                    read = mainActivity.Puerto_A().read_menora13();
                                    if (read != null) {
                                        //if (mainActivity.MainClass.BZA1.Errores(read) != null) {
                                        //listErr = mainActivity.MainClass.BZA1.Errores2(read);
                                            if(listErr !=null){
                                                for (int i = 0; i < listErr.size(); i++) {
                                                    mainActivity.Mensaje(listErr.get(i), R.layout.item_customtoasterror);
                                                    if(indexcalib!=8 && indexcalib>0 && (listErr.get(i).contains("L_") || listErr.get(i).contains("Z_") || listErr.get(i).contains("U_")) && !listErr.get(i).contains("S_") ){
                                                       // indiceCalibracion=indexcalib;
                                                        if(dialog!=null){
                                                            getActivity().runOnUiThread(() -> {
                                                                dialog.setCancelable(true);
                                                                dialog.cancel();
                                                            });
                                                        }
                                                      //  inicioCalibracion("ERRCONTROL");

                                                        //if(indexcalib==0 && !Errorcalib){
                                                            //inicioCalibracion("xx");
                                                        //}else{
                                                          //  Errorcalib=true;
                                                         //   inicioCalibracion("ERRCONTROL");
                                                       //}

                                                    }else{
                                                        if(boton!=null){
                                                        boton.setClickable(true);
                                                        }
                                                    }
                                                }
                                                //Thread.sleep(1000);
                                                Timeout[0]=true;
                                            }
                                        }
                                    }
                                //}
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            //catch (InterruptedException e) {
                              //  throw new RuntimeException(e);
                            //}
                        });

                    }

                    Thread.sleep(500);
                }
            } catch(InterruptedException e){
                e.printStackTrace();
                System.out.println("OPTIMA CATCH   " +e);

            }

        };
        Runnable myRunnable2= () -> {
            try {
                Thread.sleep(2000);
                Timeout[0] =true;
                if(boton!=null){
                    boton.setClickable(true);
                }
            } catch (InterruptedException e) {
                throw new RuntimeException(e);

            }
        };
        Thread myThread2 = new Thread(myRunnable);
        myThread2.start();
        Thread myThread = new Thread(myRunnable2);
        myThread.start();
        return listErr.size()>=1;
    }*/

    private void enviarpddiv(String CapDivPDecimal) throws InterruptedException {
        System.out.println("OPTIMA capdivpdec: "+CapDivPDecimal); // (cuando punto decimal es == xx.xxx) HAY UN ERROR ACA java.lang.NullPointerException: Attempt to invoke virtual method 'byte[] java.lang.String.getBytes()' on a null object reference
        BZA.serialPort.write(CapDivPDecimal);
        Thread.sleep(500);

      //  procesarerror(0,null,null);
    }
    private void inicioCalibracion(String CapDivPDecimal) {
        System.out.println("initcal _> " + indiceCalibracion);
        final Boolean[] bt_guardarbool = {true};
        if(CapDivPDecimal!="ERRCONTROL")
        {

            BZA.set_CapacidadMax(tv_capacidad.getText().toString());
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
       // Guardar.setClickable(false);

        mBuilder.setView(mView);
        dialog1 = mBuilder.create();
        dialog1.setCancelable(false);
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.show();


        Runnable myRunnable = () -> {
            try {

                if (CapDivPDecimal != "ERRCONTROL" ){//&& indiceCalibracion!=2){
                    Thread.sleep(1000);
                //Thread.sleep(2000);
                enviarpddiv(CapDivPDecimal);
                Thread.sleep(1000);
//                BZA.Guardar_cal();
            }
                //System.out.println("OPTIMA ERR"+CapDivPDecimal);


              /*  if(!stoped){
                    getActivity().runOnUiThread(() -> {
                        try {
                            if(mainActivity.Puerto_A().HabilitadoLectura()){
                                read= mainActivity.Puerto_A().read_menora13();
                                System.out.println("OPTIMA ENVIA PARAM:"+ read);
                                if(read!=null){
                                    if(mainActivity.MainClass.BZA1.Errores(read)!=null){
                                        mainActivity.Mensaje(mainActivity.MainClass.BZA1.Errores(read), R.layout.item_customtoasterror);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }*/
                        //Thread.sleep(2000);
                mainActivity.runOnUiThread(() -> {
                    if(CapDivPDecimal.equals("ERRCONTROL")){

                        System.out.println("ERRCALIB");
                        switch (indiceCalibracion){
                            case 1:
                                titulo.setText("Verifique que la balanza este en cero, luego presione \"SIGUIENTE\"");
                                loadingPanel.setVisibility(View.INVISIBLE);
                                tvCarga.setVisibility(View.INVISIBLE);
                                Guardar.setClickable(true);
                                break;
                            case 2:
                                TextView textView = titulo;
                                String textoCompleto = "Coloque el peso conocido ("+tv_pesoConocido.getText()+sp_unidad.getSelectedItem().toString()+") y luego presione \"SIGUIENTE\"";

                                SpannableStringBuilder builder = new SpannableStringBuilder(textoCompleto);

// Aplicar tamaño más grande a una parte del texto
                                RelativeSizeSpan sizeSpan1 = new RelativeSizeSpan(1.3f); // Aumenta el tamaño 1.5 veces
                                builder.setSpan(sizeSpan1, 25,("Coloque el peso conocido ("+tv_pesoConocido.getText()+sp_unidad.getSelectedItem().toString()+")").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Aplica al texto desde el índice 10 al 15

// Puedes aplicar más estilos a otras partes del texto si es necesario

                                textView.setText(builder);
                              //  titulo.setText("Coloque el peso conocido y luego presione \"SIGUIENTE\"");
                                loadingPanel.setVisibility(View.INVISIBLE);
                                tvCarga.setVisibility(View.INVISIBLE);
                                Guardar.setClickable(true);
                                break;
                            case 3:
                                if(!lasttanque){
                                titulo.setText("Coloque el recero y luego presione \"SIGUIENTE\"");
                                loadingPanel.setVisibility(View.INVISIBLE);
                                tvCarga.setVisibility(View.INVISIBLE);
                                Guardar.setClickable(true);


                                }else {
                                    indiceCalibracion = 1;
                                    dialog1.setCancelable(true);
                                    dialog1.cancel();
                                }
                                break;
                            default:

                                break;
                        }
                    }else{
                        titulo.setText("Verifique que la balanza este en cero, luego presione \"SIGUIENTE\"");
                        loadingPanel.setVisibility(View.INVISIBLE);
                        tvCarga.setVisibility(View.INVISIBLE);
                        Guardar.setClickable(true);


                    }
                    });

                //}



            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread myThread = new Thread(myRunnable);
        myThread.start();

        Guardar.setOnClickListener(view -> {
            if(bt_guardarbool[0] && !titulo.getText().toString().toLowerCase(Locale.ROOT)
                    .contains("espere un momento") && !titulo.getText().toString().toLowerCase(Locale.ROOT).contains("cargando") && loadingPanel.getVisibility()!=View.VISIBLE ){
                bt_guardarbool[0] =false;
                System.out.println("OPTIMA GUARDAR BT "+ indiceCalibracion+ lasttanque);
                switch (indiceCalibracion) {
                    case 1:

                        ejecutarCalibracionCero(Guardar,titulo,loadingPanel,tvCarga,dialog1);
                        break;
                    case 2:
                        ejecutarCalibracionPesoConocido(Guardar,titulo,loadingPanel,tvCarga, dialog1);
                       /* if(lasttanque){
                            indiceCalibracion=1;
                            bt_iniciarCalibracionbool=true;
                            dialog1.setCancelable(true);
                            dialog1.cancel();
                        }*/

                        break;
                    case 3:
                        if(!lasttanque){
                            ejecutarCalibracionRecero(Guardar, titulo, loadingPanel, tvCarga, dialog1);

                        }else {
                            indiceCalibracion=1;

                            dialog1.setCancelable(true);
                            dialog1.cancel();

                        }
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
        System.out.println("OPTIMA recero");

            BZA.serialPort.write( BZA.Recero_cal());

        //Guardar.setClickable(false);
        titulo.setText("");
        loadingPanel.setVisibility(View.VISIBLE);
        tvCarga.setVisibility(View.VISIBLE);
        /*Runnable myRunnable3 = () -> {
            try {

                Thread.sleep(4000);
           //     procesarerror(3,dialog,Guardar);

               if(!stoped){
                    getActivity().runOnUiThread(() -> {
                        try {
                            if(mainActivity.Puerto_A().HabilitadoLectura()){
                                read= mainActivity.Puerto_A().read_menora13();
                                if(read!=null){
                                    if(mainActivity.MainClass.BZA1.Errores(read)!=null){

                                          mainActivity.Mensaje(mainActivity.MainClass.BZA1.Errores(read), R.layout.item_customtoasterror);


                                         }
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });


                    //mainActivity.Puerto_A().write(mainActivity.MainClass.BZA1.Guardar_cal());
                   // Thread.sleep(1000);
                Thread.sleep(500);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tv_ultimaCalibracion.setText(mainActivity.DevuelveFecha()+" "+mainActivity.DevuelveHora());
                        mainActivity.MainClass.BZA1.set_UltimaCalibracion(mainActivity.DevuelveFecha()+" "+mainActivity.DevuelveHora());
                    }
                });
                    if(!stoped ){

                        Thread.sleep(3000);
                        getActivity().runOnUiThread(() -> {

                            guardar(3,dialog,Guardar);
                            if(dialog!=null){
                                dialog.setCancelable(true);
                                dialog.cancel();
                            }
                               /* try {
                                if(mainActivity.Puerto_A().HabilitadoLectura()){
                                    read= mainActivity.Puerto_A().read_menora13();
                                    System.out.println("OPTIMA guarda (pero recero) PARAM:"+ read);

                                    if(read!=null){
                                        if(mainActivity.MainClass.BZA1.Errores(read)!=null){

                                            mainActivity.Mensaje(mainActivity.MainClass.BZA1.Errores(read), R.layout.item_customtoasterror);
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            //procesarerror(3);

                            titulo.setText("Termine");
                            Guardar.setText("Terminar");
                            loadingPanel.setVisibility(View.INVISIBLE);
                            tvCarga.setVisibility(View.INVISIBLE);
                          //  Guardar.setClickable(true);
                        });
                       // indiceCalibracion=1;
                    }

                //}
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread myThread3 = new Thread(myRunnable3);
        myThread3.start();*/
    }

    private void ejecutarCalibracionPesoConocido(Button Guardar,TextView titulo,ProgressBar loadingPanel,TextView tvCarga,AlertDialog dialog) {
        System.out.println("OPTIMA pesocon");
        String msj = BZA.Peso_conocido(tv_pesoConocido.getText().toString(),String.valueOf(sp_puntoDecimal.getSelectedItemPosition()));//,tv_capacidad.getText().toString()
        if(msj!=null){
            BZA.serialPort.write(msj);
        }else{
            dialog1.cancel();
            bt_iniciarCalibracionbool=true;
            Mensaje("Error, peso conocido fuera de rango de acuerdo a Capacidad/punto decimal elegida", R.layout.item_customtoasterror, BZA.mainActivity);
        }
       // Guardar.setClickable(false);
        titulo.setText("");
        loadingPanel.setVisibility(View.VISIBLE);
        tvCarga.setVisibility(View.VISIBLE);
        BZA.set_PesoConocido(tv_pesoConocido.getText().toString());


       /* Runnable myRunnable2 = () -> {
            try {

                // Espera 1 segundo (1000 milisegundos)
                Thread.sleep(3000);//Thread.sleep(8000);
                if(!stoped){
                    getActivity().runOnUiThread(() -> {
                        try {
                            if(mainActivity.Puerto_A().HabilitadoLectura()){
                                read= mainActivity.Puerto_A().read_menora13();
                                if(read!=null){
                                    if(mainActivity.MainClass.BZA1.Errores(read)!=null){
                                        mainActivity.Mensaje(mainActivity.MainClass.BZA1.Errores(read), R.layout.item_customtoasterror);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }

             //   procesarerror(2,dialog,Guardar);
                Thread.sleep(7000);
                getActivity().runOnUiThread(() -> {
                                if(!lasttanque) {

                            titulo.setText("Coloque el recero y luego presione \"SIGUIENTE\"");
                            loadingPanel.setVisibility(View.INVISIBLE);
                            tvCarga.setVisibility(View.INVISIBLE);
                           // Guardar.setClickable(true);
                             }else{
                                    indiceCalibracion=1;
                                    dialog1.setCancelable(true);
                                    dialog1.cancel();

                                }

                    });
                    if(lasttanque){
                        bt_iniciarCalibracionbool=true;
                        Thread.sleep(1000);
                        guardar(8,dialog,Guardar);
                        //mainActivity.Puerto_A().write(mainActivity.MainClass.BZA1.Guardar_cal());
                        //procesarerror(1);
                        //System.out.println("OPTIMA guarda PARAM:"+ read);

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_ultimaCalibracion.setText(mainActivity.DevuelveFecha()+" "+mainActivity.DevuelveHora());
                                mainActivity.MainClass.BZA1.set_UltimaCalibracion(mainActivity.DevuelveFecha()+" "+mainActivity.DevuelveHora());
                            }
                        });
                        //dialog.cancel();
                    }
                //}


            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        };
        Thread myThread2 = new Thread(myRunnable2);
        myThread2.start();*/
    }


    private void ejecutarenviodeparametros( ) {
        //mainActivity.Puerto_A().write(mainActivity.MainClass.BZA1.Cero_cal());



        Runnable myRunnable = () -> {
            try {
                // Espera 1 segundo (1000 milisegundos)
                //Thread.sleep(1500);
                String param1="";
                param1 += leertoggles(toggle1,R.id.btON1);
                param1 += leertoggles(toggle2,R.id.btON2);// "0";
                param1 += leertoggles(toggle3,R.id.btON3);
                param1 +=leertoggles(toggle4,R.id.btON4); //"0";
                lasttanque= (leertoggles(toggle3, R.id.btON3).equals("1"));
                param1 +="0";// leertoggles(toggle5,R.id.btON5);
                param1 +=  "1";// leertoggles(toggle6,R.id.btON6); // ESTE NECESITA ESTAR EN 0
                param1 +="1";// leertoggles(toggle7,R.id.btON7);
                param1 += leertoggles(toggle8,R.id.btON8); // "0";
                String param2="00000000";
                BZA.serialPort.write( BZA.EnviarParametros(param1,param2,sp_pro.getSelectedItem().toString(),sp_off.getSelectedItem().toString(),sp_acu.getSelectedItem().toString()));
                Thread.sleep(1000);
            //    procesarerror(8,null,null);
                /*try {
                    if(mainActivity.Puerto_A().HabilitadoLectura()){
                        read= mainActivity.Puerto_A().read_menora13();
                        if(read!=null){
                            if(mainActivity.MainClass.BZA1.Errores(read)!=null){
                                mainActivity.Mensaje(mainActivity.MainClass.BZA1.Errores(read), R.layout.item_customtoasterror);
                            }else{
                                String hex= read.substring(0,0); // necesito leer de la pos 2 sin contar el #006  asta pos 4
                                // hex.toInt(16).toString(2);

                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                try {
                    if(mainActivity.Puerto_A().HabilitadoLectura()){
                        read= mainActivity.Puerto_A().read_menora13();
                        if(read!=null){
                            if(mainActivity.MainClass.BZA1.Errores(read)!=null){
                                mainActivity.Mensaje(mainActivity.MainClass.BZA1.Errores(read), R.layout.item_customtoasterror);
                            }else{
                                String hex= read.substring(0,0); // necesito leer de la pos 2 sin contar el #006  asta pos 4
                                // hex.toInt(16).toString(2);

                            }
                        }
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }*/



            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        if(!stoped){
           /* getActivity().runOnUiThread(() -> {
                //--NUEVO
                //titulo.setText("Enviando parametros");
            });*/
        }
        Thread myThread = new Thread(myRunnable);
        myThread.start();
    }
    private void ejecutarCalibracionCero(Button Guardar,TextView titulo,ProgressBar loadingPanel,TextView tvCarga,AlertDialog dialog) {
        System.out.println("OPTIMA cero");
        BZA.serialPort.write( BZA.Cero_cal());
       // Guardar.setClickable(false);
        titulo.setText("");
        loadingPanel.setVisibility(View.VISIBLE);
        tvCarga.setVisibility(View.VISIBLE);

        /*Runnable myRunnable = () -> {
            try {
                // Espera 1 segundo (1000 milisegundos)
                Thread.sleep(1500);//Thread.sleep(2500);

                if(!stoped){
                    getActivity().runOnUiThread(() -> {
                        try {
                            if(mainActivity.Puerto_A().HabilitadoLectura()){
                                read= mainActivity.Puerto_A().read_menora13();
                                if(read!=null){
                                    if(mainActivity.MainClass.BZA1.Errores(read)!=null){
                                        mainActivity.Mensaje(mainActivity.MainClass.BZA1.Errores(read), R.layout.item_customtoasterror);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                  //      procesarerror(1,dialog,Guardar);
                        Thread.sleep(2000);
                getActivity().runOnUiThread(() -> {
                    TextView textView = titulo;
                    String textoCompleto = "Coloque el peso conocido ("+tv_pesoConocido.getText()+sp_unidad.getSelectedItem().toString()+") y luego presione \"SIGUIENTE\"";
                    SpannableStringBuilder builder = new SpannableStringBuilder(textoCompleto);
                    RelativeSizeSpan sizeSpan1 = new RelativeSizeSpan(1.3f); // Aumenta el tamaño 1.5 veces
                    builder.setSpan(sizeSpan1, 25,("Coloque el peso conocido ("+tv_pesoConocido.getText()+sp_unidad.getSelectedItem().toString()+")").length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // Aplica al texto desde el índice 10 al 15

                    textView.setText(builder);
                            //titulo.setText("Coloque el peso conocido"+tv_pesoConocido.getText()+" y luego presione \"SIGUIENTE\"");
                            loadingPanel.setVisibility(View.INVISIBLE);
                            tvCarga.setVisibility(View.INVISIBLE);
                         //   Guardar.setClickable(true);
                        });

                //    });
              //  }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread myThread = new Thread(myRunnable);
        myThread.start();*/
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
            if(Texto.equals("Peso Conocido")){ // Peso conocido con coma
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

                if(Texto.equals("Peso Conocido")){
                    if(BZA.Peso_conocido(userInput.getText().toString(),String.valueOf(sp_puntoDecimal.getSelectedItemPosition()))!=null){ // ,tv_capacidad.getText().toString()
                     textView.setText(userInput.getText().toString());
                   }else{
                        Mensaje("Error, peso conocido fuera de rango de acuerdo a Capacidad/punto decimal elegida", R.layout.item_customtoasterror, BZA.mainActivity);
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


