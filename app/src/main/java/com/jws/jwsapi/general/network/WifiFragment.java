package com.jws.jwsapi.general.network;


import static android.content.Context.WIFI_SERVICE;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.jws.JwsManager;
import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.general.utils.ToastHelper;
import com.jws.jwsapi.general.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.util.List;

public class WifiFragment extends Fragment  {

    Button bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6;

    LinearLayout ln6,ln5,ln4;
    ListView wifiList;
    TextView tvSSIRED,tvMAC;
    WifiManager wifiManager;
    Spinner sethernetsino;
    Handler handler=new Handler();
    TextView tvSSIconnected,tvIP;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    private JwsManager jwsobject;
    WifiReceiver receiverWifi;
    String guardada="",RedWiFi="";
    Boolean stoped=false;

    MainActivity mainActivity;
    private ButtonProvider buttonProvider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standar_wifi,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        configuracionBotones();

        wifiList = view.findViewById(R.id.wifiList);
        TextView tvred = view.findViewById(R.id.tvred);
        LinearLayout layoutwifilist= view.findViewById(R.id.layoutwifilist);
        LinearLayout tableLayoutwifi= view.findViewById(R.id.tableLayoutwifi);
        ln6= view.findViewById(R.id.ln6);
        ln5= view.findViewById(R.id.ln5);
        ln4= view.findViewById(R.id.ln4);

        tvMAC= view.findViewById(R.id.tvMAC);
        RadioGroup rGroup=view.findViewById(R.id.toggle);
        tvSSIconnected= view.findViewById(R.id.tvSSIconnected);
        tvIP= view.findViewById(R.id.tvIP);
        mainActivity=(MainActivity)getActivity() ;

        layoutwifilist.setVisibility(View.INVISIBLE);
        tvSSIconnected.setVisibility(View.INVISIBLE);
        sethernetsino=view.findViewById(R.id.sethernetsino);
        sethernetsino.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        tvSSIRED=view.findViewById(R.id.tvSSIRED);
        tvIP.setText(Utils.getIPAddress(true));

        String mac =Utils.jwsGetEthWifiAddress();
        if(mac!=null){
            tvMAC.setText(mac);
        }


        wifiManager = (WifiManager) requireActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
        jwsobject = JwsManager.create(getContext());

        if(jwsobject.jwsGetEthernetState()){
            sethernetsino.setSelection(0);
        }
        else{
            sethernetsino.setSelection(1);
        }
        if(isWifiEnabled()){
            // Toast.makeText(getContext(),"true",Toast.LENGTH_SHORT).show();
            rGroup.check(R.id.btON);
        }else{
            rGroup.check(R.id.btOFF);
            ln4.setVisibility(View.INVISIBLE);
            ln5.setVisibility(View.INVISIBLE);
            ln6.setVisibility(View.INVISIBLE);
        }



        rGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(rGroup.getCheckedRadioButtonId()==R.id.btON){
                    if(!wifiManager.isWifiEnabled()){
                        jwsobject.getWifiInterface(getContext()).wifiOpen();
                        ln4.setVisibility(View.VISIBLE);
                        ln5.setVisibility(View.VISIBLE);
                        ln6.setVisibility(View.VISIBLE);
                    }
                }else{
                    if(wifiManager.isWifiEnabled()){
                        jwsobject.getWifiInterface(getContext()).wifiClose();
                        ln4.setVisibility(View.INVISIBLE);
                        ln5.setVisibility(View.INVISIBLE);
                        ln6.setVisibility(View.INVISIBLE);
                    }

                }
            }
        });

        sethernetsino.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
               /* selectedItemet = parent.getItemAtPosition(position).toString();
                if(selectedItemet.equals("SI"))
                {
                    jwsObject.jwsSetEthernetState(true);
                    //jwsObject.jwsSetEthDHCPAddress();
                }
                if(selectedItemet.equals("NO"))
                {
                    jwsObject.jwsSetEthernetState(false);
                }*/
            }
            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });

       /* tvestatica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.Mensaje("estatica",R.layout.item_customtoasterror);
                jwsObject.jwsSetEthStaticIPAddress("10.42.0.45","255.255.255.0","10.41.0.254","10.41.0.112","10.41.0.112");
            }
        });
        tvdin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.Mensaje("dinamica",R.layout.item_customtoasterror);
                jwsObject.jwsSetEthDHCPAddress();
            }
        });*/




        bt_1.setOnClickListener(v -> {
            if (buttonProvider != null) {
                bt_1.setVisibility(View.INVISIBLE);
            }

            layoutwifilist.setVisibility(View.INVISIBLE);
            tableLayoutwifi.setVisibility(View.VISIBLE);

        });

        tvred.setOnClickListener(v -> {

            if(isWifiEnabled()){
                LayoutInflater inflater = getLayoutInflater();
                View layout = inflater.inflate(R.layout.item_customtoast,
                        view.findViewById(R.id.toast_layout_root));


                ImageView image = layout.findViewById(R.id.image);
                image.setImageResource(R.drawable.error_preview_rev_1);
                TextView text = layout.findViewById(R.id.text);
                text.setText("Si usted modifico una red ya guardada debera eliminar los datos antiguos (mantenga presionado el name de la red)");


                Toast toast = new Toast(getContext());
                toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(layout);
                toast.show();
                if (buttonProvider != null) {
                    bt_1.setVisibility(View.VISIBLE);
                }

                tableLayoutwifi.setVisibility(View.INVISIBLE);
                layoutwifilist.setVisibility(View.VISIBLE);
                tvSSIconnected.setVisibility(View.VISIBLE);


            }
            else {
                ToastHelper.message("WIFI apagado, debe encender el WIFI para ver las redes",R.layout.item_customtoasterror,mainActivity);
            }
        });

        receiverWifi = new WifiReceiver(wifiManager, wifiList);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        requireActivity().registerReceiver(receiverWifi, intentFilter);
        getWifi();

        /*List<ScanResult> wifiList2 = wifiManager.getScanResults();
        List strReturnList = new ArrayList();

        strReturnList= jwsObject.getWifiInterface(getContext()).scanResultToString(wifiList2);

        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1, Objects.requireNonNull(strReturnList.toArray()));
*/
        //wifiList.setAdapter(arrayAdapter);

        // wifiList= (ListView) jwsObject.getWifiInterface(getContext()).getScanResults();
        wifiList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                String saveSSI2 =(String)wifiList.getItemAtPosition(i);
                List<WifiConfiguration> configuradas =wifiManager.getConfiguredNetworks ();

                for ( WifiConfiguration z : configuradas) {
                    if(z.SSID != null && z.SSID.equals("\"" + saveSSI2 + "\"")){

                        AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                        View mView = getLayoutInflater().inflate(R.layout.dialogo_cambiarprod, null);
                        TextView textView=mView.findViewById(R.id.textViewt);
                        textView.setText("Eliminar datos de la red:" + saveSSI2);
          /*  userInput.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_DECIMAL);
            userInput.requestFocus();

            userInput.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_SIGNED);
            userInput.requestFocus();*/


                        Button Guardar =  mView.findViewById(R.id.buttons);
                        Button Cancelar =  mView.findViewById(R.id.buttonc);

                        Guardar.setText("Eliminar");

                        mBuilder.setView(mView);
                        final AlertDialog dialog = mBuilder.create();
                        dialog.show();

                        Guardar.setOnClickListener(view14 -> {
                            wifiManager.removeNetwork(z.networkId);
                            dialog.cancel();
                        });

                        Cancelar.setOnClickListener(view15 -> dialog.cancel());


                    }}
                return true;
            }
        });
        wifiList.setOnItemClickListener((adapterView, view1, i, l) -> {

            String saveSSI =(String)wifiList.getItemAtPosition(i);

            List<WifiConfiguration> configuradas =wifiManager.getConfiguredNetworks ();

            for ( WifiConfiguration z : configuradas) {
                if(z.SSID != null && z.SSID.equals("\"" + saveSSI + "\"")){
                    guardada="existe";
                    wifiManager.getConnectionInfo().getSSID();
                    wifiManager.disconnect();
                    wifiManager.enableNetwork(z.networkId, true);
                    wifiManager.reconnect();
                    WifiInfo info = wifiManager.getConnectionInfo();
                    String ssid  = info.getSSID();

                /*    if(ssid==null){
                        wifiManager.removeNetwork(z.networkId);
                    }*/
                }}
            if(!guardada.equals("existe")){


                AlertDialog.Builder mBuilder = new AlertDialog.Builder(getContext());
                View mView = getLayoutInflater().inflate(R.layout.dialogo_dosopciones, null);
                final EditText userInput = mView.findViewById(R.id.etDatos);
                final LinearLayout delete_text= mView.findViewById(R.id.lndelete_text);
                delete_text.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(android.view.View view) {
                        userInput.setText("");
                    }
                });
                TextView textView=mView.findViewById(R.id.textViewt);
                textView.setText("Ingrese Contraseña");
          /*  userInput.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_DECIMAL);
            userInput.requestFocus();

            userInput.setInputType(InputType.TYPE_CLASS_NUMBER |InputType.TYPE_NUMBER_FLAG_SIGNED);
            userInput.requestFocus();*/


                Button Guardar =  mView.findViewById(R.id.buttons);
                Button Cancelar =  mView.findViewById(R.id.buttonc);

                Guardar.setText("Conectar");

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.show();

                Guardar.setOnClickListener(view14 -> {

                    WifiConfiguration wifiConfig = new WifiConfiguration();
                    wifiConfig.SSID = String.format("\"%s\"", saveSSI);
                    wifiConfig.preSharedKey = String.format("\"%s\"", userInput.getText().toString());

                    WifiManager wifiManager = (WifiManager) requireActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
                    wifiConfig.status = WifiConfiguration.Status.DISABLED;
                    wifiConfig.priority = 40;
                    int netId = wifiManager.addNetwork(wifiConfig);
                    wifiManager.enableNetwork(netId, true);
                    dialog.cancel();
                });

                Cancelar.setOnClickListener(view15 -> dialog.cancel());

            }



            guardada="";

        });


        mToastRunnable.run();

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
            buttonProvider.getTitulo().setText("WIFI");

            bt_1.setBackgroundResource(R.drawable.boton_atras_i);
            bt_1.setVisibility(View.INVISIBLE);
            bt_2.setVisibility(View.INVISIBLE);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);

            bt_home.setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());

        }
    }
    private void getWifi() {

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions( new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {

            wifiManager.startScan();
        }
    }
    public boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) mainActivity.getSystemService(Context.WIFI_SERVICE);
        return wifiManager != null && wifiManager.isWifiEnabled();
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_ACCESS_COARSE_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                wifiManager.startScan();
            }
        }
    }
    Runnable mToastRunnable= new Runnable() {
        @Override
        public void run() {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            RedWiFi=wifiInfo.getSSID();

            if(RedWiFi=="<unknown ssid>"){
                tvSSIRED.setText("DESCONECTADO");
                tvSSIconnected.setText("DESCONECTADO");
            }else{
                tvSSIRED.setText(wifiInfo.getSSID());
                tvSSIconnected.setText("conectado a:"+wifiInfo.getSSID());
            }
            tvIP.setText(Utils.getIPAddress(true));


            if(!stoped){
                handler.postDelayed(this,1000);
            }


        }
    };

    @Override
    public void onDestroyView() {
        stoped=true;
        super.onDestroyView();
    }


}


