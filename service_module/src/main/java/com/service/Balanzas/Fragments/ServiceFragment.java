package com.service.Balanzas.Fragments;

import static android.view.View.GONE;

import android.app.AlertDialog;

import android.os.Bundle;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.service.Balanzas.BalanzaService;
import com.service.Balanzas.Clases.ITW410;
import com.service.Balanzas.Clases.MINIMA_I;
import com.service.Balanzas.Clases.OPTIMA_I;
import com.service.Balanzas.Clases.R31P30_I;
import com.service.Balanzas.Interfaz.serviceDevice;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import com.service.Comunicacion.MyRecyclerViewAdapter;
import com.service.R;
import com.service.Recyclers.recyclerModbus;
import com.service.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public  class ServiceFragment extends Fragment {

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
    ArrayList<serviceDevice> lista;
    ArrayList<serviceDevice> listaglob;


    public static ServiceFragment newInstance(Serializable instance) {
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
        listaglob=service.Balanzas.get_balanzalistglob();

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
         lista= BalanzaService.Balanzas.get_balanzalist(0);
        addapterItem(0);
        btback.setOnClickListener(View ->{
            if(linearadd.getVisibility()==android.view.View.VISIBLE){
                linearadd.setVisibility(android.view.View.GONE);
                linearcalib.setVisibility(android.view.View.VISIBLE);
            }

            ListElementsArrayList.clear();
            for (int i = 0; i < service.Balanzas.getBalanzas().size(); i++) {
                if(service.Balanzas.getBalanzas().get(i)!=-1) {
                    ListElementsArrayList.add("Calibracion Balanza " + String.valueOf(i + 1));
                }
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


        final recyclerModbus.ItemClickListener[] itemClickListener = {new recyclerModbus.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position,serviceDevice Device,int ModeloaAgregar) {
                showdialogbza(position,Device,ModeloaAgregar);
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
          //  modbuslistconfig[0] =BalanzaService.Balanzas.getConfigModbus();
            itemClickListener[0] = new recyclerModbus.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position,serviceDevice Device,int ModeloaAgregar) {
                    showdialogbza(position,Device,ModeloaAgregar);
                }
            };
            if(lista.size()>=1) {
                recyclerModbus adapter = new recyclerModbus(getContext(), lista, service.activity, itemClickListener[0],0);
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
        String[] Balanzas_arrtipo = getResources().getStringArray(R.array.tipoBalanzasxx);
        ArrayAdapter<String> adapter7 = new ArrayAdapter<>(getContext(),R.layout.item_spinner,Balanzas_arrtipo);
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



                String value= String.valueOf(sp_tipobalanzas.getItemAtPosition(i));
                if (value.equals("Optima")) {
                    CargarDatosRecycler(0);
                }
                else if(value.equals("Minima")){
                    CargarDatosRecycler(1);
                }else if(value.equals("R31P30")){
                    CargarDatosRecycler(2);
                }else if (value.equals("ITW410")) {
                    CargarDatosRecycler(3);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
            }


    private void CargarDatosRecycler(int Modelo){
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        lista.clear();
        System.out.println("MODELO DE CARGA DE DATOS"+Modelo);
        lista = service.Balanzas.get_balanzalist(Modelo);
        addapterItem(Modelo);
        recyclerModbus.ItemClickListener itemClickListener = new recyclerModbus.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position,serviceDevice Device,int ModeloaAgregar) {
                showdialogbza(position,Device,ModeloaAgregar);
            }
        };
         if(lista.size()>=1) {
            recyclerModbus adapter = new recyclerModbus(getContext(), lista, service.activity, itemClickListener,Modelo);
            recyclerView.setAdapter(adapter);
        };
    }
    public void dialogText(TextView textView,String string,String Texto){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialogo_dosopciones, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        final LinearLayout delete_text= mView.findViewById(R.id.lndelete_text);
        userInput.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
        TextView titulo=mView.findViewById(R.id.textViewt);
        delete_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userInput.setText("");
            }
        });
        titulo.setText(Texto);
        if(!textView.getText().toString().equals("")){
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
    private void dialogBZA(int numBza,serviceDevice Device){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialogconfigmodbus, null);
        Spinner spPort=mView.findViewById(R.id.sp_port);
        TextView tvBaud = mView.findViewById(R.id.tv_Baud);
        TextView tvStopb = mView.findViewById(R.id.tv_Stopbit);
        TextView tvDatab = mView.findViewById(R.id.tv_Databit);
        TextView tvSlave = mView.findViewById(R.id.tv_Slave);
        TextView tvParity = mView.findViewById(R.id.tv_Parity);
        TextView textView1 = mView.findViewById(R.id.textView1);
        TextView textView = mView.findViewById(R.id.numMOD);
        int z= Device.getNB()+1;
        textView.setText("Nº Balanza: "+z);
       /* boolean primeravez=true;
        if(numBza>0){
            primeravez=false;
        }*/
        textView1.setText(textView1.getText().toString());
        int lenghtdireccion=Device.getDireccion().size();
        if(Device.getModelo()==3){
            ArrayList<String> list= new ArrayList<String>();
            list.add("PuertoSerie 1");
            list.add("PuertoSerie 2");
            list.add("PuertoSerie 3");
            list.add("TCP/IP");
            ArrayAdapter<String> adapter11 = new ArrayAdapter<>(getContext(),R.layout.item_spinner,list);
            adapter11.setDropDownViewResource(R.layout.item_spinner);
            spPort.setAdapter(adapter11);
            textView1.setText("Slave");
            textView.setText("Nº Modbus: "+z);
        }
       /* if(!primeravez){
            spPort.setEnabled(false);
            spPort.setClickable(false);
            tvDatab.setClickable(false);
            tvParity.setClickable(false);
            tvStopb.setClickable(false);
            ArrayList<String> direcc= service.Balanzas.get_primeraDireccion(Device.getModelo());
            lenghtdireccion=direcc.size();

            if(lenghtdireccion>0) {
                tvBaud.setText(String.valueOf(direcc.get(0)));
            }
            if(lenghtdireccion>1){
                tvDatab.setText(String.valueOf(direcc.get(1)));
            }

            if(lenghtdireccion>2){
                tvStopb.setText(String.valueOf(direcc.get(2)));
            }
            if(lenghtdireccion>3){
                tvParity.setText(String.valueOf(direcc.get(3)));
            }
        }else{*/
            tvBaud.setOnClickListener(View ->{
                dialogText(tvBaud,"","Ingrese Baud");
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

            if(lenghtdireccion>0) {
                tvBaud.setText(String.valueOf(Device.getDireccion().get(0)));
            }
            if(lenghtdireccion>1){
                tvDatab.setText(String.valueOf(Device.getDireccion().get(1)));
            }

            if(lenghtdireccion>2){
                tvStopb.setText(String.valueOf(Device.getDireccion().get(2)));
            }
            if(lenghtdireccion>3){
                tvParity.setText(String.valueOf(Device.getDireccion().get(3)));
            }
       // }
        if(Device.getID()!=-1){
            tvSlave.setText(String.valueOf(Device.getID()));
        }
        tvSlave.setOnClickListener(View ->{
            dialogText(tvSlave,"","Ingrese ID");
        });

            Button Guardar = mView.findViewById(R.id.Guardar);

        mBuilder.setView(mView);
        dialog = mBuilder.create();
        dialog.show();
        Button remove = mView.findViewById(R.id.Remove);
      /*  remove.setOnClickListener(view ->{
            serviceDevice Adapteritem  = new serviceDevice();
            Adapteritem.setTipo(-1);
            Adapteritem.setModelo(-1);
            Adapteritem.setSalida("-1");
            Adapteritem.setID(-1);
            ArrayList<String> diraux= new ArrayList<String>();
            diraux.add("");diraux.add("");diraux.add("");diraux.add("");diraux.add("");diraux.add("");
            Adapteritem.setDireccion(diraux);
            lista.get(numBza).setDevice(Adapteritem);
        });*/
        Guardar.setOnClickListener(View ->{
            int Slave=0,Data=0,Stop=0,Parity=0,Baud=0;
            try {
                Slave=Integer.parseInt(tvSlave.getText().toString());
                Data=Integer.parseInt(tvDatab.getText().toString());
                Stop=Integer.parseInt(tvStopb.getText().toString());
                Parity=Integer.parseInt(tvParity.getText().toString());
                Baud=Integer.parseInt(tvBaud.getText().toString());
                ArrayList<String> listaux= new ArrayList<>();
                listaux.add(String.valueOf(Baud));
                listaux.add(String.valueOf(Data));
                listaux.add(String.valueOf(Stop));
                listaux.add(String.valueOf(Parity));
                serviceDevice x=new serviceDevice();
                x.setModelo(Device.getModelo());
                x.setTipo(0);

                x.setSalida(spPort.getSelectedItem().toString());
                x.setDireccion(listaux);
                x.setID(Slave);
                x.setSeteo(true);
                Boolean replacebool=false;
                for (serviceDevice dv:listaglob) {
                    if(dv.getNB()==x.getNB()){
                        replacebool=true;
                    }
                }
                BalanzaService.addDevice(x,replacebool);
                x.setNB(Device.getNB());// numBza
                /*if(Device.getSeteo()||Device.getNB()==0){
                    System.out.println("WAW");
                    x.setNB(Device.getNB());
                    BalanzaService.addDevice(x,Device.getNB()); // numBza
                }else if(Device.getNB()!=0){
                    System.out.println("WOW");
                    x.setNB(Device.getNB()+1);
                    BalanzaService.addDevice(x,Device.getNB()+1); // +1?   /  numBza

                }*/
                BalanzaService.Balanzas.getBalanzas().add(x.getModelo());
                System.out.println("NumeroIDfrag " + Device.getID());
                lista.get(numBza).setDevice(x);
                listaglob.add(x);
                if(Device.getNB()!=0) {
                    addapterItem(Device.getModelo());
                }
                service.activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        recyclerView.getAdapter().notifyDataSetChanged();
                        dialog.cancel();
                    }
                });

                // BalanzaService.Balanzas.setConfigModbus(spPort.getSelectedItem().toString(),Slave,Baud,Data,Stop,Parity,nModbus);
                //BalanzaService.Balanzas.addBalanza("ITW410",nModbus);

            }catch (Exception e){
                Utils.Mensaje("Error, deben ser todos los valores numeros enteros",R.layout.item_customtoasterror,service.activity);
            }
        });
        spPort.setSelection(0);

    }
    private void addapterItem(int Modelo){
        serviceDevice Adapteritem  = new serviceDevice();
        Adapteritem.setTipo(-1);
        Adapteritem.setModelo(Modelo);
        Adapteritem.setSalida("-1");
        Adapteritem.setID(-1);
        Adapteritem.setSeteo(false);
        System.out.println("ADDAPTER POS"+ listaglob.get((listaglob.size()-1)).getNB()+1);
       Adapteritem.setNB(listaglob.get((listaglob.size()-1)).getNB()+1);
        ArrayList<String> diraux= new ArrayList<String>();
        switch (Modelo){
            case 0:{
                diraux.add(OPTIMA_I.Bauddef);diraux.add(OPTIMA_I.DataBdef);diraux.add(OPTIMA_I.StopBdef);diraux.add(OPTIMA_I.Paritydef);
            }
            case 1:{
                diraux.add(MINIMA_I.Bauddef);diraux.add(MINIMA_I.DataBdef);diraux.add(MINIMA_I.StopBdef);diraux.add(MINIMA_I.Paritydef);
            }
            case 2:{
                diraux.add(R31P30_I.Bauddef);diraux.add(R31P30_I.DataBdef);diraux.add(R31P30_I.StopBdef);diraux.add(R31P30_I.Paritydef);
            }
            case 3:{
                diraux.add(ITW410.Bauddef);diraux.add(ITW410.DataBdef);diraux.add(ITW410.StopBdef);diraux.add(ITW410.Paritydef);
            }
            default:{
                diraux.add("");diraux.add("");diraux.add("");diraux.add("");diraux.add("");diraux.add("");
            }
        }
        Adapteritem.setDireccion(diraux);
        lista.add(Adapteritem);
    }

    private void showdialogbza(int id, serviceDevice Device,int ModeloAgregar){
     /*   ArrayList<String> Direccion=BalanzaService.Balanzas.get_primeraDireccion(ModeloAgregar);
        Boolean primeravez=true;
        System.out.println(Direccion.get(0));
        if (Direccion.get(0)!="" ) {
            if(Device.getID()!=Integer.parseInt(Direccion.get(5))) {
                Device.setSalida(Direccion.get(4));
                Direccion.remove(4);
                Direccion.remove(4);
                Device.setDireccion(Direccion);
                primeravez = false;
            }
        }*/
        if(Device.getModelo()>3||Device.getModelo()==-1){
            if(Device.getTipo()==-1 && Device.getID()==-1&& Device.getModelo()<4 && ModeloAgregar!=-1) {
               Device.setModelo(ModeloAgregar);
                Device.setTipo(0);
                showdialogbza(id,Device,ModeloAgregar);
            }else if(ModeloAgregar==-1){
                Utils.Mensaje("Error en configuracion del service",R.layout.item_customtoasterror,service.activity);
            }
        }else{
            dialogBZA(id, Device);
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
            buttonProvider.getTitle().setText("CONFIGURACION SERVICE");

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


