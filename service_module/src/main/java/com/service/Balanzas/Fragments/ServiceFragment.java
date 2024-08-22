package com.service.Balanzas.Fragments;

import static android.view.View.GONE;

import static com.service.Utils.Mensaje;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.service.Balanzas.BalanzaService;
import com.service.Balanzas.Clases.OPTIMA_I;
import com.service.Balanzas.Interfaz.modbus;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import com.service.Comunicacion.MyRecyclerViewAdapter;
import com.service.R;
import com.service.Recyclers.recyclerModbus;
import com.service.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public  class ServiceFragment extends Fragment  {

    Button bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6;
    LinearLayout ln0,ln1,ln2,ln3,ln4,ln17,ln18;
    private ButtonProvider buttonProvider;
    Spinner sp_tipobalanzas,sp_tipobalanzas2;
    BalanzaService service;
    LinearLayout linearcalib,linearadd,linearbalanzas,linearescaner,linearsalientra,linearimpresora;
    TabLayout tablayout;
    AlertDialog dialog;
    RecyclerView recyclerView;
    Button btAdd,btback;
    String tipobza= String.valueOf(410);
    Boolean stoped=false;
    List<String> ListElementsArrayList= new ArrayList<>();
    RecyclerView recycler;
    MyRecyclerViewAdapter adapter;
    int menu = 0;
    Boolean programador=false;

    public static ServiceFragment newInstance(BalanzaService instance) {
        ServiceFragment fragment = new ServiceFragment() ;
        Bundle args = new Bundle();
        args.putSerializable("instanceService", instance);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standar_service2,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        if (getArguments() != null) {
            service = (BalanzaService) getArguments().getSerializable("instanceService");
            programador=getArguments().getBoolean("NIVEL");
        }else{
            System.out.println("ERROR DE INSTANCE SERVICE");
        }

        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        configuracionBotones();
        linearbalanzas= view.findViewById(R.id.balanzalinear);
        linearescaner = view.findViewById(R.id.escanerlinear);
        linearsalientra=view.findViewById(R.id.salentlinear);
        linearimpresora=view.findViewById(R.id.impresoralinear);
        linearcalib = view.findViewById(R.id.linearCalib);
        linearadd = view.findViewById(R.id.linearañadir);
        tablayout = view.findViewById(R.id.tablayout);
        recyclerView=view.findViewById(R.id.recyclerview);
        recycler= view.findViewById(R.id.listview);
        //linear añadir
        btback = view.findViewById(R.id.back);
        btback.setOnClickListener(View ->{
            if(linearadd.getVisibility()==android.view.View.VISIBLE){
                linearadd.setVisibility(android.view.View.GONE);
                linearcalib.setVisibility(android.view.View.VISIBLE);
            }

            ListElementsArrayList.clear();
            for (int i = 0; i < service.Balanzas.getBalanzas().size(); i++) {
                ListElementsArrayList.add("Calibracion Balanza " + String.valueOf(i + 1));
            }
            adapter = new MyRecyclerViewAdapter(getContext(), ListElementsArrayList, service.activity);
            adapter.setClickListener((view1, position) -> service.Balanzas.openCalibracion(position + 1));
            recycler.setAdapter(adapter);
        });
        tablayout.setAlpha(0.5f);
        tablayout.setClickable(false);
        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                int position = tab.getPosition();
                menu=position;
                switch (position) {
                    case 0:
                        CargarRecyclerBzas();
                        break;
                    case 1:
                        //CargarRecyclerImpresoras();
                        break;
                    case 2:
                        //CargarRecyclerEscaner();
                        break;
                    case 3:
                        // CargarRecyclerSalidaEntrada();
                        break;
                }
            }
            @Override
            public void onTabUnselected(@NonNull TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(@NonNull TabLayout.Tab tab) {
            }
        });


        final ArrayList<modbus>[] modbuslistconfig = new ArrayList[]{BalanzaService.Balanzas.getConfigModbus()};
        CargarDatosRecycler410();
        final recyclerModbus.ItemClickListener[] itemClickListener = {new recyclerModbus.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                showdialogbza(position, 0);
            }

        }};
        //final linear añadir
        //linear Calibracion
        btAdd = view.findViewById(R.id.btadd);
        btAdd.setOnClickListener(View -> {

            if (linearadd.getVisibility() == android.view.View.GONE) {
                linearadd.setVisibility(android.view.View.VISIBLE);
                linearcalib.setVisibility(android.view.View.GONE);
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            modbuslistconfig[0].clear();
            modbuslistconfig[0] =BalanzaService.Balanzas.getConfigModbus();
            itemClickListener[0] = new recyclerModbus.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position) {
                    showdialogbza(position,0);
                }
            };
            if(modbuslistconfig[0].size()>=1) {

                recyclerModbus adapter = new recyclerModbus(service.activity.getApplicationContext(), modbuslistconfig[0], service.activity, itemClickListener[0]);
                recyclerView.setAdapter(adapter);
            }
        });
        CargarRecyclerBzas();
        ln17=view.findViewById(R.id.ln17);
        sp_tipobalanzas=view.findViewById(R.id.sp_tipobalanzas);

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        for(int i = 0; i< service.Balanzas.getBalanzas().size(); i++){
            ListElementsArrayList.add("Calibracion Balanza "+String.valueOf(i+1));
        }
        adapter = new MyRecyclerViewAdapter(getContext(), ListElementsArrayList,service.activity);
        adapter.setClickListener((view1, position) -> service.Balanzas.openCalibracion(position+1));
        recycler.setAdapter(adapter);
        String[] Balanzas_arrtipo = getResources().getStringArray(R.array.tipoBalanzas);
        ArrayAdapter<String> adapter7 = new ArrayAdapter<>(requireContext(),R.layout.item_spinner,Balanzas_arrtipo);
        adapter7.setDropDownViewResource(R.layout.item_spinner);
        sp_tipobalanzas.setAdapter(adapter7);
        sp_tipobalanzas.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_tipobalanzas.setSelection(service.Balanzas.getBalanzas().get(0));
        sp_tipobalanzas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //  mainActivity.MainClass.setModoBalanza(i);
            /*    List<Integer> list= service.Balanzas.getBalanzas();
                list.set(0,i);
                service.Balanzas.setBalanzas(list);*/
                if(sp_tipobalanzas.getSelectedItem()=="ITW410"){
                    CargarDatosRecycler410();

                    }

                }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        //fin linear calibrar

            }
    private void CargarDatosRecycler410(){
        final ArrayList<modbus>[] modbuslistconfig = new ArrayList[]{BalanzaService.Balanzas.getConfigModbus()};
        final recyclerModbus.ItemClickListener[] itemClickListener = {new recyclerModbus.ItemClickListener() {
        @Override
        public void onItemClick(View view, int position) {
            showdialogbza(position, 0);
        }

        }};
        if(modbuslistconfig[0].size()>=1) {
            recyclerModbus adapter = new recyclerModbus(service.activity.getApplicationContext(), modbuslistconfig[0], service.activity, itemClickListener[0]);
            recyclerView.setAdapter(adapter);
        }
    }
    public void dialogText(TextView textView,String string,String Texto){

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
        TextView titulo=mView.findViewById(R.id.textViewt);
        titulo.setText(Texto);
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
                textView.setText(userInput.getText().toString());
                dialog.cancel();
            }
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());

    }

    private void dialog410(int nModbus){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialogconfigmodbus, null);
        Spinner spPort=mView.findViewById(R.id.sp_port);
        TextView tvBaud = mView.findViewById(R.id.tv_Baud);
        TextView tvStopb = mView.findViewById(R.id.tv_Stopbit);
        TextView tvDatab = mView.findViewById(R.id.tv_Databit);
        TextView tvSlave = mView.findViewById(R.id.tv_Slave);
        TextView tvParity = mView.findViewById(R.id.tv_Parity);
        Button Guardar = mView.findViewById(R.id.Guardar);
        tvBaud.setOnClickListener(View ->{
            dialogText(tvBaud,"","Ingrese Baud");
        });
        tvSlave.setOnClickListener(View ->{
            dialogText(tvSlave,"","Ingrese Slave");
        });
        tvStopb.setOnClickListener(View ->{
            dialogText(tvStopb,"","Ingrese StopBit");
        });
        tvDatab.setOnClickListener(View ->{
            dialogText(tvDatab,"","Ingrese DataBit");
        });
        tvParity.setOnClickListener(View ->{
            dialogText(tvParity,"","Ingrese Parity");
        });

        Guardar.setOnClickListener(View ->{
            int Slave=0,Data=0,Stop=0,Parity=0,Baud=0;
            try {
                Slave=Integer.parseInt(tvSlave.getText().toString());
                Data=Integer.parseInt(tvDatab.getText().toString());
                Stop=Integer.parseInt(tvStopb.getText().toString());
                Parity=Integer.parseInt(tvParity.getText().toString());
                Baud=Integer.parseInt(tvSlave.getText().toString());
                BalanzaService.Balanzas.setConfigModbus(spPort.getSelectedItem().toString(),Slave,Baud,Data,Stop,Parity,nModbus);
                BalanzaService.Balanzas.addBalanza("ITW410",nModbus);
                dialog.cancel();
            }catch (Exception e){
                Utils.Mensaje("Error, deben ser todos los valores numeros enteros",R.layout.item_customtoasterror,service.activity);
            }
        });
        spPort.setSelection(0);
        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();
    }
    private void showdialogbza(int numBza,int tipobza){
        switch(tipobza){
            case 0:{
                dialog410(numBza);
                break;
            }
            case 1:{

            }
            case 2: {

            }

        }
    }
    private void CargarRecyclerBzas(){
        linearbalanzas.setVisibility(View.VISIBLE);
        linearcalib.setVisibility(View.VISIBLE);
        linearadd.setVisibility(GONE);
        linearsalientra.setVisibility(GONE);
        linearimpresora.setVisibility(GONE);
        linearescaner.setVisibility(GONE);

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
            buttonProvider.getTitulo().setText("CONFIGURACION SERVICE");

            bt_1.setBackgroundResource(R.drawable.boton_editar_i);
            if(!programador){
                bt_1.setVisibility(View.INVISIBLE);
            }
            bt_2.setVisibility(View.INVISIBLE);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);

            bt_home.setOnClickListener(view -> {
                volveraPrincipal();
            });

        }
    }

    private void volveraPrincipal() {
        Boolean calibracionoptima=false;
        for(int i = 0; i< service.Balanzas.getBalanzas().size(); i++){
           /* if (Objects.equals(service.Balanzas.getEstado(i+1), OPTIMA_I.M_MODO_CALIBRACION)){
                calibracionoptima=true;
                service.Balanzas.getSerialPort(i+1).write(OPTIMA_I.Salir_cal());
            }*/
        }
        if(calibracionoptima){
            new Thread() {
                @Override
                public void run() {
                    try {
                        getActivity().runOnUiThread(() -> {
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                            View mView = getLayoutInflater().inflate(R.layout.dialogo_calibracion_optima, null);

                            TextView titulo=mView.findViewById(R.id.textViewt);
                            ProgressBar loadingPanel=mView.findViewById(R.id.loadingPanel);
                            TextView tvCarga=mView.findViewById(R.id.tvCarga);
                            Button Guardar =  mView.findViewById(R.id.buttons);
                            Button Cancelar =  mView.findViewById(R.id.buttonc);
                            Cancelar.setVisibility(View.INVISIBLE);
                            Guardar.setVisibility(View.INVISIBLE);
                            titulo.setText("espere un momento...");
                            Guardar.setClickable(false);

                            mBuilder.setView(mView);
                            dialog = mBuilder.create();
                            dialog.show();
                        });
                        Thread.sleep(15000);
                        if(!stoped){
                            getActivity().runOnUiThread(() -> {
                                for(int i = 0; i< service.Balanzas.getBalanzas().size(); i++){
                                 //   if (Objects.equals(service.Balanzas.getEstado(i+1), OPTIMA_I.M_MODO_CALIBRACION)){
                                      //  service.Balanzas.setEstado(i+1,OPTIMA_I.M_MODO_BALANZA);
                                   // }
                                }
                                service.fragmentChangeListener.openFragmentPrincipal();
                                dialog.cancel();
                            });
                        }

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }
            service.fragmentChangeListener.openFragmentPrincipal();

    }


    @Override
    public void onDestroyView() {
        stoped=true;
        super.onDestroyView();
    }


}


