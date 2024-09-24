package com.service.Balanzas.Fragments;

import static android.view.View.GONE;

import android.app.AlertDialog;
import android.app.Dialog;
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
import com.service.Recyclers.recyclerModbus2;
import com.service.Utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public  class ServiceFragment extends Fragment {

    Button bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6;
    LinearLayout ln0,ln1,ln2,ln3,ln4,ln17,ln18;
    private ButtonProvider buttonProvider;
    Spinner sp_tipobalanzas2,sp_tipopuerto;
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
    static ArrayList<serviceDevice> lista;
    static ArrayList<serviceDevice> listaglob;


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
        View view = inflater.inflate(R.layout.standar_service3,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        if (getArguments() != null) {
            service = (BalanzaService) getArguments().getSerializable("instanceService");
            programador=getArguments().getBoolean("NIVEL");
        }else{
//            System.out.println("ERROR DE INSTANCE SERVICE");
        }

        // ESTA FUNCION HAY QUE SEGUIR PROBANDOLA: POR AHORA FUNCIONA IGUAL QUE ANTES PERO TIENE MUCHAS MENOS COSAS QUE GET_BALANZALISTGLOB
        // ADEMAS LA VOY ACTUALIZANDO SOLO CUANDO ACTUALIZO LISTA, ANTES HABIA OTROS LUGARES TMB ASI QUE POR AHI HAY ALGUNA EXCEPCION
//        listaglob=service.Balanzas.get_balanzalistglobindex();

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
        sp_tipopuerto=view.findViewById(R.id.sp_tipopuerto);
        recycler= view.findViewById(R.id.listview);
        //linear añadir
       /* TabLayout.Tab Puertos= tablayout.newTab();
        Puertos.setText("Puertos");


        TabLayout.Tab Balanzas = tablayout.newTab();
        Puertos.setText("Balanzas");

        TabLayout.Tab Impresoras = tablayout.newTab();
        Puertos.setText("Impresoras");

        TabLayout.Tab IO = tablayout.newTab();
        Puertos.setText("I/O");

        TabLayout.Tab Escaneres = tablayout.newTab();
        Puertos.setText("Escaneres");
        TabLayout.Tab Dispositivos = tablayout.newTab();
        Puertos.setText("Dispositivos");
        tablayout.addTab(Balanzas);
        tablayout.addTab(Impresoras);
        tablayout.addTab(IO);
        tablayout.addTab(Escaneres);
        tablayout.addTab(Dispositivos);*/

        btback = view.findViewById(R.id.back);
        lista= BalanzaService.Balanzas.get_listPerPort(sp_tipopuerto.getSelectedItem().toString(),tablayout.getSelectedTabPosition());
        addapterItem(sp_tipopuerto.getSelectedItem().toString());
        btback.setOnClickListener(View ->{
            if(linearadd.getVisibility()==android.view.View.VISIBLE){
                linearadd.setVisibility(android.view.View.GONE);
                linearcalib.setVisibility(android.view.View.VISIBLE);
                try {

                    recycler.setLayoutManager(new LinearLayoutManager(getContext()));
                    for(int i = 0; i< service.Balanzas.getBalanzas().size(); i++){
                        ListElementsArrayList.add("Calibracion Balanza "+String.valueOf(i+1));
                    }
                    adapter = new MyRecyclerViewAdapter(getContext(), ListElementsArrayList,service.activity);
                    adapter.setClickListener((view1, position) ->{
                        try {
                            service.Balanzas.init(position + 1);
                        }catch (Exception e){

                        }finally {
                            service.Balanzas.openCalibracion(position + 1);
                        };
                    });
                    recycler.setAdapter(adapter);

                }catch(Exception e ){

                }
            }

            ListElementsArrayList.clear();

            for (int i = 0; i < service.Balanzas.getBalanzas().size(); i++) {
                if(service.Balanzas.getBalanzas().get(i)!=-1) {

                    ListElementsArrayList.add("Calibracion Balanza " + String.valueOf(i + 1));
                }
            }
            adapter = new MyRecyclerViewAdapter(getContext(), ListElementsArrayList,service.activity);

            adapter.setClickListener((view1, position) ->{
                try {
                    service.Balanzas.init(position + 1);
                }catch (Exception e){

                }finally {
                    service.Balanzas.openCalibracion(position + 1);
                }
                recycler.setAdapter(adapter);
            });
        });




        tablayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(@NonNull TabLayout.Tab tab) {
                int position = tab.getPosition();
                menu=position;

                switch (position) {
                    case 0:
                        //CargarRecyclerBzas();
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


        final recyclerModbus2.ItemClickListener[] itemClickListener = {new recyclerModbus2.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position,serviceDevice Device,String Salida,int TipoDevice) {
                showdialogbza(Device,Salida);
            }
        }};
        //final linear añadir
        //linear Calibracion
        btAdd = view.findViewById(R.id.btadd);
        btAdd.setOnClickListener(View -> {
            if (linearadd.getVisibility() == android.view.View.GONE) {
                linearadd.setVisibility(android.view.View.VISIBLE);
                linearcalib.setVisibility(android.view.View.GONE);
                sp_tipopuerto.setSelection(0);
                CargarDatosRecycler("PuertoSerie 1");
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            //  modbuslistconfig[0] =BalanzaService.Balanzas.getConfigModbus();
            itemClickListener[0] = new recyclerModbus2.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position,serviceDevice Device,String Salida,int TipoDevice) {
                    showdialogbza(Device,Salida);
                }
            };
            if(lista.size()>=1) {
                recyclerModbus2 adapter = new recyclerModbus2(getContext(), lista, service.activity, itemClickListener[0],"PuertoSerie 1",tablayout.getSelectedTabPosition());
                recyclerView.setAdapter(adapter);
            }
        });
        CargarRecyclerBzas();
        ln17=view.findViewById(R.id.ln17);
        //sp_tipobalanzas=view.findViewById(R.id.sp_tipobalanzas);
        try {
            recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            for(int i = 0; i< service.Balanzas.getBalanzas().size(); i++){
                ListElementsArrayList.add("Calibracion Balanza "+String.valueOf(i+1));
            }
            adapter = new MyRecyclerViewAdapter(getContext(), ListElementsArrayList,service.activity);
            adapter.setClickListener((view1, position) ->{
                try {
                    service.Balanzas.init(position + 1);
                }catch (Exception e){

                }finally {
                    service.Balanzas.openCalibracion(position + 1);
                };
            });
            recycler.setAdapter(adapter);

        }catch(Exception e ){

        }
        String[] Balanzas_arrtipo = getResources().getStringArray(R.array.tipoSalida);
        ArrayAdapter<String> adapter7 = new ArrayAdapter<>(getContext(),R.layout.item_spinner,Balanzas_arrtipo);
        adapter7.setDropDownViewResource(R.layout.item_spinner);
        sp_tipopuerto.setAdapter(adapter7);
        sp_tipopuerto.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        try {
            sp_tipopuerto.setSelection(service.Balanzas.getBalanzas().get(0));
        } catch (Exception e) {
        }
        sp_tipopuerto.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //  mainActivity.MainClass.setModoBalanza(i);
            /*    List<Integer> list= service.Balanzas.getBalanzas();
                list.set(0,i);
                service.Balanzas.setBalanzas(list);*/

               /* String value= String.valueOf(sp_tipobalanzas.getItemAtPosition(i));
                if (value.equals("Optima")) {
                    CargarDatosRecycler(0);
                }
                else if(value.equals("Minima")){
                    CargarDatosRecycler(1);
                }else if(value.equals("R31P30")){
                    CargarDatosRecycler(2);
                }else if (value.equals("ITW410")) {
                    CargarDatosRecycler(3);
                }*/
                String value= String.valueOf(sp_tipopuerto.getItemAtPosition(i));
                CargarDatosRecycler(value);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }


    private void CargarDatosRecycler(String Puerto){

        try {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            lista.clear();
            lista = service.Balanzas.get_listPerPort(Puerto,tablayout.getSelectedTabPosition());
            listaglob=service.Balanzas.get_listglobindex(tablayout.getSelectedTabPosition());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }finally {
            addapterItem(Puerto);

        }


        try {
            recyclerModbus2.ItemClickListener itemClickListener = new recyclerModbus2.ItemClickListener() {
                @Override
                public void onItemClick(View view, int position,serviceDevice Device,String Salida,int TipoDevice) {
                    showdialogbza(Device,Salida);
                }
            };
            if(lista.size()>=1) {
                recyclerModbus2 adapter = new recyclerModbus2(getContext(), lista, service.activity, itemClickListener, Puerto,tablayout.getSelectedTabPosition());
                recyclerView.setAdapter(adapter);
            }
            ;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
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
        Dialog dialog1 = mBuilder.create();
        dialog1.show();
        Guardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                textView.setText(userInput.getText().toString());
                dialog1.cancel();
            }
        });
        Cancelar.setOnClickListener(view -> dialog1.cancel());

    }

    private int indexofservicedevice(ArrayList<serviceDevice> arr , serviceDevice b){
        int response =0;
        if(b.getTipo()!=-1) {
            for (serviceDevice a : arr) {
                if (a.getNB() == b.getNB()) {
                    response = a.getNB();
//                    System.out.println("REMOVE"+response);
                }
            }
        }else{
            response=0;
        }




        return response;
    }
    private void dialogBZA(serviceDevice Device){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
        View mView = getLayoutInflater().inflate(R.layout.dialogconfigmodbus2, null);
        Spinner sp_Modelo=mView.findViewById(R.id.sp_port);
        TextView tvBaud = mView.findViewById(R.id.tv_Baud);
        LinearLayout LtvSlave = mView.findViewById(R.id.linearLayout3);
        TextView tvSlave = mView.findViewById(R.id.tv_Slave);
        TextView textView1 = mView.findViewById(R.id.textView1);
        TextView textView = mView.findViewById(R.id.numMOD);
        int z= Device.getNB()-Device.getNumborrados()+1;
        textView.setText("Nº Balanza: "+z);
//        System.out.println("NB POSITION dialog "+Device.getNB()+"+"+Device.getNumborrados()+" = "+(Device.getNB()+Device.getNumborrados()) );
        if(Device.getSalida().contains("IP")||Device.getSalida().contains("RED")){
            tvBaud.setVisibility(View.VISIBLE);
        }else{
            tvBaud.setVisibility(GONE);
        }
        LtvSlave.setVisibility(GONE);// POR AHORA NO SERA SETEABLE HASTA HACER LOS PROTOCOLOS CON ID LUEGO SOLO SERA PARA LOS DISPOSITIVOS SIN ID

        sp_Modelo.setSelection(Device.getModelo());
        if(indexofservicedevice(lista,Device)>1){
            sp_Modelo.setEnabled(false); // POR AHORA kponmPROBAR
            sp_Modelo.setClickable(false);
        }

        textView1.setText(textView1.getText().toString());
        int lenghtdireccion=Device.getDireccion().size();
        if(Device.getModelo()==3){
            String[] list=  getResources().getStringArray(R.array.Modelobza);
            ArrayAdapter<String> adapter11 = new ArrayAdapter<>(getContext(),R.layout.item_spinner,list);
            adapter11.setDropDownViewResource(R.layout.item_spinner);
            sp_Modelo.setAdapter(adapter11);
            textView1.setText("Slave");
            textView.setText("Nº Modbus: "+z);
        }
        tvBaud.setOnClickListener(View ->{
            dialogText(tvBaud,"","Ingrese Baud");
        });
        if(lenghtdireccion>0 && tvBaud!=null) {
            tvBaud.setText(String.valueOf(Device.getDireccion().get(0)));
        }
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
        if(Device.getID()==-1){
            remove.setVisibility(GONE);
        }
        remove.setOnClickListener(view ->{
            System.out.println("SISIZEZE "+listaglob.size());
            if(listaglob.size()>1) {
                try {

                    serviceDevice deviceaux = new serviceDevice();
                    deviceaux.setDevice(Device);
                    switch (Device.getSalida()) {
                        case "Puerto Serie 1": {
                            deviceaux.setSalida("PuertoSerie 1");
                            break;
                        }
                        case "Puerto Serie 2": {
                            deviceaux.setSalida("PuertoSerie 2");
                            break;
                        }
                        case "Puerto Serie 3": {
                            deviceaux.setSalida("PuertoSerie 3");
                            break;
                        }
                    }
                    serviceDevice Adapteritem = new serviceDevice();
                    Adapteritem.setTipo(-1);
                    Adapteritem.setModelo(-1);
                    Adapteritem.setSalida("-1");
                    Adapteritem.setID(-1);
                    Adapteritem.setNB(Device.getNB());
//            System.out.println("NUMBERO BORRADOS REMOVE?¿"+Device.getNumborrados()+1);
                    Adapteritem.setNumborrados(Device.getNumborrados());
                    Adapteritem.setSeteo(false);
                    ArrayList<String> diraux = new ArrayList<String>();
                    diraux.add("");
                    diraux.add("");
                    diraux.add("");
                    diraux.add("");
                    diraux.add("");
                    diraux.add("");
                    Adapteritem.setDireccion(diraux);
              /*  for (int i=0;i<listaglob.size();i++) {
                    serviceDevice j =listaglob.get(i);
                    if(j.getSalida().equals(deviceaux.getSalida())&&j.getSeteo()==deviceaux.getSeteo()&&j.getTipo()==deviceaux.getTipo()&&j.getNB()==deviceaux.getNB()&&j.getID()==deviceaux.getID()&&j.getDireccion().equals(deviceaux.getDireccion())&&j.getModelo()==deviceaux.getModelo()){
                        System.out.println("REMOVE"+i+" IN "+listaglob.size());
                        listaglob.remove(i);
                        System.out.println("SIZE ACTUALIZATE"+listaglob.size());
                    }
                }*/
                    BalanzaService.addDevice(Adapteritem); //

//            Thread.sleep(100);
//            listaglob=service.Balanzas.get_listglobindex();
                    CargarDatosRecycler(sp_tipopuerto.getSelectedItem().toString());
                    //lista.get(numBza).setDevice(Adapteritem);
                    dialog.cancel();
                } catch (Exception e) {
                    Utils.Mensaje("Error en eliminacion", R.layout.item_customtoasterror, service.activity);
                }
            }else{
                Utils.Mensaje("Debe tener almenos 1 balanza configurada",R.layout.item_customtoasterror,service.activity);
            }
        });
        Guardar.setOnClickListener(View ->{
            int Slave=0,Data=0,Stop=0,Parity=0,Baud=0;
            try {

                serviceDevice x=new serviceDevice();

                if(tvSlave!=null && tvSlave.getVisibility()==View.VISIBLE) {
                    Slave = Integer.parseInt(tvSlave.getText().toString());
                }
                ArrayList<String> listaux= new ArrayList<>();
                String value=sp_Modelo.getSelectedItem().toString();
                if(tvBaud!=null && tvBaud.getVisibility()==View.VISIBLE) {
                    Baud = Integer.parseInt(tvBaud.getText().toString());
                }
                switch (value){
                    case "Optima":{
                        x.setModelo(0);
                        listaux.add(OPTIMA_I.Bauddef);
                        listaux.add(OPTIMA_I.DataBdef);
                        listaux.add(OPTIMA_I.StopBdef);
                        listaux.add(OPTIMA_I.Paritydef);
                        break;
                    }
                    case "Minima":{
                        x.setModelo(1);
                        listaux.add(MINIMA_I.Bauddef);
                        listaux.add(MINIMA_I.DataBdef);
                        listaux.add(MINIMA_I.StopBdef);
                        listaux.add(MINIMA_I.Paritydef);
                        break;
                    }
                    case "R31P30":{
                        x.setModelo(2);
                        listaux.add(R31P30_I.Bauddef);
                        listaux.add(R31P30_I.DataBdef);
                        listaux.add(R31P30_I.StopBdef);
                        listaux.add(R31P30_I.Paritydef);
                        break;
                    }
                    case "ITW410":{
                        x.setModelo(3);
                        listaux.add(ITW410.Bauddef);
                        listaux.add(ITW410.DataBdef);
                        listaux.add(ITW410.StopBdef);
                        listaux.add(ITW410.Paritydef);
                        break;
                    }
                }
                int position = tablayout.getSelectedTabPosition();
                x.setTipo(position);
                String value2=sp_tipopuerto.getSelectedItem().toString();
                switch (value2){
                    case "Puerto Serie 1":{
                        x.setSalida("PuertoSerie 1");
                        break;
                    }
                    case "Puerto Serie 2":{
                        x.setSalida("PuertoSerie 2");break;
                    }
                    case "Puerto Serie 3":{
                        x.setSalida("PuertoSerie 3");break;
                    }
                }
                Boolean replacebool=false;
                x.setDireccion(listaux);
//                System.out.println("ID ?!?!?!?!? "+Slave);
                x.setID(Slave);
                x.setSeteo(true);
                x.setNB(Device.getNB());
                x.setNumborrados(Device.getNumborrados());

//                System.out.println("NUMERO BORRADOS AL GUARDAR"+Device.getNumborrados());


//                System.out.println("NB agregar "+ Device.getNB());
                BalanzaService.Balanzas.getBalanzas().add(x.getModelo());
                for (serviceDevice dv:listaglob) {
                    if(dv.getNB()==x.getNB()){
                        replacebool=true;
//                        System.out.println("REPLACE?");
                    }
                }
                BalanzaService.addDevice(x);
//                listaglob=service.Balanzas.get_listglobindex();
                if(!replacebool) {
                    listaglob.add(x);
                    addapterItem(sp_tipopuerto.getSelectedItem().toString());
                }



                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        CargarDatosRecycler(sp_tipopuerto.getSelectedItem().toString());
                        recyclerView.getAdapter().notifyDataSetChanged();

                    }
                });

                // BalanzaService.Balanzas.setConfigModbus(spPort.getSelectedItem().toString(),Slave,Baud,Data,Stop,Parity,nModbus);
                //BalanzaService.Balanzas.addBalanza("ITW410",nModbus);
                dialog.cancel();
            }catch (Exception e){
                Utils.Mensaje("Error, deben ser todos los valores numeros enteros",R.layout.item_customtoasterror,service.activity);
                dialog.cancel();
            }
        });
//        sp_Modelo.setSelection(0);

    }
    private void addapterItem(String Puerto){
        try {
            serviceDevice Adapteritem  = new serviceDevice();
            Adapteritem.setTipo(-1);
            Adapteritem.setModelo(-1);
            Adapteritem.setSalida(Puerto);
            Adapteritem.setID(-1);
            Adapteritem.setSeteo(false);
            Adapteritem.setNumborrados(listaglob.get(listaglob.size()-1).getNumborrados());
            Adapteritem.setNB(listaglob.get(listaglob.size()-1).getNB()+1);

            //+service.get_nborrados());//listaglob.get((listaglob.size()-1)).getNB()+1);
            ArrayList<String> diraux= new ArrayList<String>();

            Adapteritem.setDireccion(diraux);
            lista.add(Adapteritem);
        } catch (Exception e) {
            System.out.println("ERROR ADDAPTER");
            serviceDevice Adapteritem  = new serviceDevice();
            Adapteritem.setTipo(-1);
            Adapteritem.setModelo(-1);
            Adapteritem.setSalida(Puerto);
            Adapteritem.setID(-1);
            Adapteritem.setSeteo(false);
            Adapteritem.setNumborrados(0);
            Adapteritem.setNB(0);
            //+service.get_nborrados());//listaglob.get((listaglob.size()-1)).getNB()+1);
            ArrayList<String> diraux= new ArrayList<String>();
            Adapteritem.setDireccion(diraux);
            lista.add(Adapteritem);
        }
    }

    private void showdialogbza(serviceDevice Device,String Salida){
        Device.setID(0);//POR AHORA VA A SER 0 POR DEFAULT HASTA HACER LO DEL ID

        if(Device.getSalida().equals("null")){
            if(Device.getTipo()==-1 && Device.getID()==-1&& Device.getModelo()<4 ) {

                Device.setSalida(Salida);
                Device.setTipo(0);
                showdialogbza(Device,Salida);
            }else if(Salida.equals("null")){
                Utils.Mensaje("Error en configuracion del service",R.layout.item_customtoasterror,service.activity);
            }
        }else{
            dialogBZA( Device);
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


