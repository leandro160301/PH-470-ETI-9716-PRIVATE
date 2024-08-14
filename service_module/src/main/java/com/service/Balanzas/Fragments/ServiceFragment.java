package com.service.Balanzas.Fragments;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.service.Balanzas.BalanzaService;
import com.service.Balanzas.Clases.OPTIMA_I;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;
import com.service.Comunicacion.MyRecyclerViewAdapter;
import com.service.R;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ServiceFragment extends Fragment  {

    Button bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6;
    LinearLayout ln0,ln1,ln2,ln3,ln4,ln17,ln18;
    private ButtonProvider buttonProvider;
    Spinner sp_tipobalanzas,sp_tipobalanzas2;
    BalanzaService service;
    AlertDialog dialog;
    Boolean stoped=false;
    List<String> ListElementsArrayList= new ArrayList<>();
    RecyclerView recycler;
    MyRecyclerViewAdapter adapter;
    Boolean programador=false;
    LinearLayout lna;

    public static ServiceFragment newInstance(BalanzaService instance) {
        ServiceFragment fragment = new ServiceFragment();
        Bundle args = new Bundle();
        args.putSerializable("instanceService", instance);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standar_service,container,false);
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

        ln17=view.findViewById(R.id.ln17);
        ln18=view.findViewById(R.id.ln18);
        lna=view.findViewById(R.id.lna);
        if(!programador){
            lna.setVisibility(View.GONE);
        }
        sp_tipobalanzas=view.findViewById(R.id.sp_tipobalanzas);
        sp_tipobalanzas2=view.findViewById(R.id.sp_tipobalanzas2);
        recycler= view.findViewById(R.id.listview);
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        for(int i=0;i<service.balanzas.size();i++){
            ListElementsArrayList.add("Calibracion Balanza "+String.valueOf(i+1));
        }
        adapter = new MyRecyclerViewAdapter(getContext(), ListElementsArrayList,service.activity);
        adapter.setClickListener((view1, position) -> service.openCalibracion(position+1));
        recycler.setAdapter(adapter);

        String[] Balanzas_arrtipo = getResources().getStringArray(R.array.tipoBalanzas);
        ArrayAdapter<String> adapter7 = new ArrayAdapter<>(requireContext(),R.layout.item_spinner,Balanzas_arrtipo);
        adapter7.setDropDownViewResource(R.layout.item_spinner);
        sp_tipobalanzas.setAdapter(adapter7);
        sp_tipobalanzas.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        sp_tipobalanzas.setSelection(service.getBalanzas().get(0));
        sp_tipobalanzas.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //  mainActivity.MainClass.setModoBalanza(i);

                List<Integer> list= service.getBalanzas();
                list.set(0,i);
                service.setBalanzas(list);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        sp_tipobalanzas2.setAdapter(adapter7);
        sp_tipobalanzas2.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        List<Integer> list= service.getBalanzas();
        if(list.size()>1){
            sp_tipobalanzas2.setSelection(list.get(1));
        }
        sp_tipobalanzas2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                //  mainActivity.MainClass.setModoBalanza(i);
                List<Integer> list= service.getBalanzas();
                if(list.size()==1){
                   /* if(mainActivity.MainClass.getModoBalanza()!=0){
                        list.add(i);
                    }*/

                }else{
                   /* if(mainActivity.MainClass.getModoBalanza()==0){
                        list.remove(1);
                    }else{
                        list.set(1,i);
                    }*/

                }

                service.setBalanzas(list);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


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
        for(int i = 0; i< service.balanzas.size(); i++){
            if (Objects.equals(service.getEstado(i+1), OPTIMA_I.M_MODO_CALIBRACION)){
                calibracionoptima=true;
                service.getSerialPort(i+1).write(OPTIMA_I.Salir_cal());
            }
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
                                for(int i = 0; i< service.balanzas.size(); i++){
                                    if (Objects.equals(service.getEstado(i+1), OPTIMA_I.M_MODO_CALIBRACION)){
                                        service.setEstado(i+1,OPTIMA_I.M_MODO_BALANZA);
                                    }
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
        }else{
            service.fragmentChangeListener.openFragmentPrincipal();
        }
    }


    @Override
    public void onDestroyView() {
        stoped=true;
        super.onDestroyView();
    }


}


