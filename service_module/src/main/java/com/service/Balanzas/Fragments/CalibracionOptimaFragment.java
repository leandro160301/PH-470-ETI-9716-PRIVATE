package com.service.Balanzas.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.service.Balanzas.BalanzaService;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import com.service.Balanzas.Clases.OPTIMA_I;
import com.service.Comunicacion.OnFragmentChangeListener;
import com.service.PuertosSerie.PuertosSerie;
import com.service.Utils;
import com.service.R;

import java.io.IOException;
import java.util.Objects;

public class CalibracionOptimaFragment extends Fragment   {

    Button bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6;
    private ButtonProvider buttonProvider;
    Button bt_iniciarCalibracion,btReajusteCero;
    AlertDialog dialog;
    Spinner sp_divisionMinima, sp_puntoDecimal,sp_unidad;
    TextView tv_pesoconocido, tv_capacidad,tv_ultimacalibracion;
    int indiceCalibracion=1;
    TableLayout table_parametrosPrincipales;
    String read;
    Boolean stoped=false;
    PuertosSerie serialPort;
    OPTIMA_I BZA;
    BalanzaService service;


    public static CalibracionOptimaFragment newInstance(OPTIMA_I instance, BalanzaService service) {
        CalibracionOptimaFragment fragment = new CalibracionOptimaFragment();
        Bundle args = new Bundle();
        args.putSerializable("instance", instance);
        args.putSerializable("instanceService", service);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standar_calibracion,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        if (getArguments() != null) {
            BZA = (OPTIMA_I) getArguments().getSerializable("instance");
            service= (BalanzaService) getArguments().getSerializable("instanceService");
        }
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);

        configuracionBotones();
        initializeViews(view);
        setClickListeners();

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
                ServiceFragment fragment = ServiceFragment.newInstance(service);
                Bundle args = new Bundle();
                args.putSerializable("instanceService", service);
                service.fragmentChangeListener.openFragmentService(fragment,args);


            });

        }else{
            Utils.Mensaje("Button Provider error",R.layout.item_customtoasterror,BZA.activity);
        }
    }


    private void initializeViews(View view) {
        bt_iniciarCalibracion =view.findViewById(R.id.btIniciarCalibracion);
        btReajusteCero =view.findViewById(R.id.btReajusteCero);
        table_parametrosPrincipales =view.findViewById(R.id.TableParametrosprincipales);
        sp_divisionMinima =view.findViewById(R.id.spDivisionMinima);
        sp_puntoDecimal =view.findViewById(R.id.spPuntoDecimal);
        sp_unidad=view.findViewById(R.id.spUnidad);
        tv_capacidad =view.findViewById(R.id.tvCapacidad);
        tv_pesoconocido =view.findViewById(R.id.tvPesoconocido);
        sp_divisionMinima.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_puntoDecimal.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_unidad.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        tv_ultimacalibracion=view.findViewById(R.id.tvUltimaCalibracion);


        tv_pesoconocido.setText(BZA.get_PesoConocido());
        tv_capacidad.setText(BZA.get_CapacidadMax());
        sp_puntoDecimal.setSelection(BZA.get_PuntoDecimal());
        sp_divisionMinima.setSelection(BZA.get_DivisionMinima());
        tv_ultimacalibracion.setText(BZA.get_UltimaCalibracion());
        serialPort=BZA.serialPort;
        if(Objects.equals(BZA.getUnidad(BZA.numero), "ton")){
            sp_unidad.setSelection(2);
        }else if(Objects.equals(BZA.getUnidad(BZA.numero), "gr")){
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

        btReajusteCero.setOnClickListener(view -> {
            serialPort.write(BZA.ReAjusteCero());
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
            View mView = getLayoutInflater().inflate(R.layout.dialogo_transferenciaarchivo, null);
            TextView textView= mView.findViewById(R.id.textView);
            textView.setText("Re ajustando...");
            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();
            new Thread(() -> {
                try {
                    Thread.sleep(12000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                getActivity().runOnUiThread(() -> {
                    serialPort.write(BZA.Guardar_cal());
                    dialog.cancel();
                });
            }).start();
        });
        bt_iniciarCalibracion.setOnClickListener(view12 -> {
            String CapDivPDecimal=BZA.CapacidadMax_DivMin_PDecimal(tv_capacidad.getText().toString(), sp_divisionMinima.getSelectedItem().toString(),String.valueOf(sp_puntoDecimal.getSelectedItemPosition()));
            try {
                serialPort.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            inicioCalibracion(CapDivPDecimal);

        });
        tv_capacidad.setOnClickListener(view110 -> Dialog(tv_capacidad,"","Capacidad"));
        tv_pesoconocido.setOnClickListener(view112 -> Dialog(tv_pesoconocido,"","Peso Conocido"));
    }

    private void inicioCalibracion(String CapDivPDecimal) {
        serialPort.write(CapDivPDecimal);
        BZA.set_CapacidadMax(tv_capacidad.getText().toString());
        BZA.set_DivisionMinima(sp_divisionMinima.getSelectedItemPosition());
        BZA.set_PuntoDecimal(sp_puntoDecimal.getSelectedItemPosition());

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialogo_calibracion_optima, null);

        TextView titulo=mView.findViewById(R.id.textViewt);
        ProgressBar loadingPanel=mView.findViewById(R.id.loadingPanel);
        TextView tvCarga=mView.findViewById(R.id.tvCarga);
        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);
        Cancelar.setVisibility(View.INVISIBLE);
        titulo.setText("");
        Guardar.setClickable(false);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Runnable myRunnable = () -> {
            try {
                Thread.sleep(2500);
                if(!stoped){
                    getActivity().runOnUiThread(() -> {
                        try {
                            if(serialPort.HabilitadoLectura()){
                                read= serialPort.read_menora13();
                                if(read!=null){
                                    if(BZA.Errores(read)!=null){
                                        Utils.Mensaje(BZA.Errores(read), R.layout.item_customtoasterror,BZA.activity);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        titulo.setText("Coloque el cero y luego presione \"SIGUIENTE\"");
                        loadingPanel.setVisibility(View.INVISIBLE);
                        tvCarga.setVisibility(View.INVISIBLE);
                        Guardar.setClickable(true);

                    });

                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread myThread = new Thread(myRunnable);
        myThread.start();

        Guardar.setOnClickListener(view -> {
            switch (indiceCalibracion) {
                case 1:
                    ejecutarCalibracionCero(Guardar,titulo,loadingPanel,tvCarga);
                    break;
                case 2:
                    ejecutarCalibracionPesoConocido(Guardar,titulo,loadingPanel,tvCarga);
                    break;
                case 3:
                    ejecutarCalibracionRecero(Guardar,titulo,loadingPanel,tvCarga,dialog);
                    break;
                default:
                    break;
            }
            indiceCalibracion++;


        });
        Cancelar.setOnClickListener(view -> dialog.cancel());
    }

    private void ejecutarCalibracionRecero(Button Guardar, TextView titulo, ProgressBar loadingPanel, TextView tvCarga,AlertDialog dialog) {
        serialPort.write(BZA.Recero_cal());
        Guardar.setClickable(false);
        titulo.setText("");
        loadingPanel.setVisibility(View.VISIBLE);
        tvCarga.setVisibility(View.VISIBLE);

        Runnable myRunnable3 = () -> {
            try {
                Thread.sleep(9000);
                if(!stoped){
                    getActivity().runOnUiThread(() -> {
                        try {
                            if(serialPort.HabilitadoLectura()){
                                read= serialPort.read_menora13();
                                if(read!=null){
                                    if(BZA.Errores(read)!=null){
                                        Utils.Mensaje(BZA.Errores(read), R.layout.item_customtoasterror,BZA.activity);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                    serialPort.write(BZA.Guardar_cal());
                    Thread.sleep(2000);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            tv_ultimacalibracion.setText(Utils.getFecha()+" "+Utils.getHora());
                            BZA.set_UltimaCalibracion(Utils.getFecha()+" "+Utils.getHora());
                        }
                    });

                    if(!stoped){
                        getActivity().runOnUiThread(() -> {
                            try {
                                if(serialPort.HabilitadoLectura()){
                                    read= serialPort.read_menora13();
                                    if(read!=null){
                                        if(BZA.Errores(read)!=null){

                                            Utils.Mensaje(BZA.Errores(read), R.layout.item_customtoasterror,BZA.activity);
                                        }
                                    }
                                }
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                            titulo.setText("Coloque el recero y luego presione \"SIGUIENTE\"");
                            loadingPanel.setVisibility(View.INVISIBLE);
                            tvCarga.setVisibility(View.INVISIBLE);
                            Guardar.setClickable(true);
                            dialog.cancel();
                        });
                        indiceCalibracion=0;
                    }

                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread myThread3 = new Thread(myRunnable3);
        myThread3.start();
    }

    private void ejecutarCalibracionPesoConocido(Button Guardar,TextView titulo,ProgressBar loadingPanel,TextView tvCarga) {
        BZA.set_PesoConocido(tv_pesoconocido.getText().toString());
        serialPort.write(BZA.Peso_conocido(tv_pesoconocido.getText().toString(),String.valueOf(sp_puntoDecimal.getSelectedItemPosition())));
        Guardar.setClickable(false);
        titulo.setText("");
        loadingPanel.setVisibility(View.VISIBLE);
        tvCarga.setVisibility(View.VISIBLE);


        Runnable myRunnable2 = () -> {
            try {
                // Espera 1 segundo (1000 milisegundos)
                Thread.sleep(12000);
                if(!stoped){
                    getActivity().runOnUiThread(() -> {
                        try {
                            if(serialPort.HabilitadoLectura()){
                                read=serialPort.read_menora13();
                                if(read!=null){
                                    if(BZA.Errores(read)!=null){
                                        Utils.Mensaje(BZA.Errores(read), R.layout.item_customtoasterror,BZA.activity);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        titulo.setText("Coloque el recero y luego presione \"SIGUIENTE\"");
                        loadingPanel.setVisibility(View.INVISIBLE);
                        tvCarga.setVisibility(View.INVISIBLE);
                        Guardar.setClickable(true);

                    });
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };
        Thread myThread2 = new Thread(myRunnable2);
        myThread2.start();
    }

    private void ejecutarCalibracionCero(Button Guardar,TextView titulo,ProgressBar loadingPanel,TextView tvCarga ) {
        serialPort.write(BZA.Cero_cal());
        Guardar.setClickable(false);
        titulo.setText("");
        loadingPanel.setVisibility(View.VISIBLE);
        tvCarga.setVisibility(View.VISIBLE);

        Runnable myRunnable = () -> {
            try {
                // Espera 1 segundo (1000 milisegundos)
                Thread.sleep(2500);

                if(!stoped){
                    getActivity().runOnUiThread(() -> {
                        try {
                            if(serialPort.HabilitadoLectura()){
                                read= serialPort.read_menora13();
                                if(read!=null){
                                    if(BZA.Errores(read)!=null){
                                        Utils.Mensaje(BZA.Errores(read), R.layout.item_customtoasterror,BZA.activity);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        titulo.setText("Coloque el peso conocido y luego presione \"SIGUIENTE\"");
                        loadingPanel.setVisibility(View.INVISIBLE);
                        tvCarga.setVisibility(View.INVISIBLE);
                        Guardar.setClickable(true);

                    });
                }


            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread myThread = new Thread(myRunnable);
        myThread.start();
    }

    public void Dialog(TextView textView,String string,String Texto){

        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialogo_dosopciones, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        final LinearLayout delete_text= mView.findViewById(R.id.lndelete_text);
        delete_text.setOnClickListener(view -> userInput.setText(""));
        userInput.setOnLongClickListener(v -> true);
        TextView titulo=mView.findViewById(R.id.textViewt);
        titulo.setText(Texto);
        if(string.equals("Ceroinicial")){
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_DECIMAL);
            userInput.requestFocus();
        }
        else{
            userInput.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_SIGNED);
            userInput.requestFocus();
        }
        if(!textView.getText().toString().equals("") && !textView.getText().toString().equals("-")){
            userInput.setText(textView.getText().toString());
            userInput.requestFocus();
            userInput.setSelection(userInput.getText().length());
        }

        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Guardar.setOnClickListener(view -> {

            if(textView== tv_pesoconocido){//cambios 19/07/2024
                if(BZA.Peso_conocido(userInput.getText().toString(),String.valueOf(sp_puntoDecimal.getSelectedItemPosition()))!=null){
                    serialPort.write(BZA.Peso_conocido(userInput.getText().toString(),String.valueOf(sp_puntoDecimal.getSelectedItemPosition())));
                    textView.setText(userInput.getText().toString());
                }else{
                    Utils.Mensaje("Error, peso conocido fuera de rango de acuerdo a division minima elegida", R.layout.item_customtoasterror,BZA.activity);
                }
            }
            if(textView== tv_capacidad){
                textView.setText(userInput.getText().toString());
            }



            dialog.cancel();
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());

    }

    @Override
    public void onDestroyView() {
        stoped=true;
        super.onDestroyView();
    }



}


