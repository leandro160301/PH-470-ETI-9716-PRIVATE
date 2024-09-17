package com.jws.jwsapi.core.network;

import static android.content.Context.WIFI_SERVICE;
import static com.jws.jwsapi.dialog.DialogUtil.dialogText;
import static com.jws.jwsapi.dialog.DialogUtil.keyboardPassword;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.jws.JwsManager;
import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.ToastHelper;
import com.jws.jwsapi.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.util.List;
import java.util.Objects;

public class WifiFragment extends Fragment {

    Button bt_1;
    LinearLayout ln6, ln5, ln4;
    ListView wifiList;
    TextView tvSSIRED, tvMAC;
    WifiManager wifiManager;
    Spinner sethernetsino;
    Handler handler = new Handler();
    TextView tvSSIconnected, tvIP;
    private final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;
    private JwsManager jwsobject;
    WifiReceiver receiverWifi;
    Boolean stoped = false;

    MainActivity mainActivity;
    private ButtonProvider buttonProvider;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standar_wifi, container, false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupButtons();

        wifiList = view.findViewById(R.id.wifiList);
        TextView tvred = view.findViewById(R.id.tvred);
        LinearLayout layoutwifilist = view.findViewById(R.id.layoutwifilist);
        LinearLayout tableLayoutwifi = view.findViewById(R.id.tableLayoutwifi);
        ln6 = view.findViewById(R.id.ln6);
        ln5 = view.findViewById(R.id.ln5);
        ln4 = view.findViewById(R.id.ln4);

        tvMAC = view.findViewById(R.id.tvMAC);
        RadioGroup rGroup = view.findViewById(R.id.toggle);
        tvSSIconnected = view.findViewById(R.id.tvSSIconnected);
        tvIP = view.findViewById(R.id.tvIP);
        mainActivity = (MainActivity) getActivity();

        layoutwifilist.setVisibility(View.INVISIBLE);
        tvSSIconnected.setVisibility(View.INVISIBLE);
        sethernetsino = view.findViewById(R.id.sethernetsino);
        sethernetsino.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        tvSSIRED = view.findViewById(R.id.tvSSIRED);
        tvIP.setText(Utils.getIPAddress(true));

        String mac = Utils.jwsGetEthWifiAddress();
        if (mac != null) {
            tvMAC.setText(mac);
        }

        wifiManager = (WifiManager) requireActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
        jwsobject = JwsManager.create(getContext());

        if (jwsobject.jwsGetEthernetState()) {
            sethernetsino.setSelection(0);
        } else {
            sethernetsino.setSelection(1);
        }
        if (isWifiEnabled()) {
            rGroup.check(R.id.btON);
        } else {
            rGroup.check(R.id.btOFF);
            setupLinearVisibility(View.INVISIBLE);
        }


        rGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (rGroup.getCheckedRadioButtonId() == R.id.btON) {
                if (!wifiManager.isWifiEnabled()) {
                    jwsobject.getWifiInterface(getContext()).wifiOpen();
                    setupLinearVisibility(View.VISIBLE);
                }
            } else {
                if (wifiManager.isWifiEnabled()) {
                    jwsobject.getWifiInterface(getContext()).wifiClose();
                    setupLinearVisibility(View.INVISIBLE);
                }
            }
        });

        bt_1.setOnClickListener(v -> {
            if (buttonProvider != null) {
                bt_1.setVisibility(View.INVISIBLE);
            }
            layoutwifilist.setVisibility(View.INVISIBLE);
            tableLayoutwifi.setVisibility(View.VISIBLE);

        });

        tvred.setOnClickListener(v -> showWifiList(layoutwifilist, tableLayoutwifi));

        receiverWifi = new WifiReceiver(wifiManager, wifiList);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        requireActivity().registerReceiver(receiverWifi, intentFilter);
        getWifi();

        wifiList.setOnItemLongClickListener((adapterView, view12, i, l) -> {
            handleDeleteWifi(i);
            return true;
        });

        wifiList.setOnItemClickListener((adapterView, view1, i, l) -> handleWifiSelected(i));

        mToastRunnable.run();

    }

    private void setupLinearVisibility(int invisible) {
        ln4.setVisibility(invisible);
        ln5.setVisibility(invisible);
        ln6.setVisibility(invisible);
    }

    private void showWifiList(LinearLayout layoutwifilist, LinearLayout tableLayoutwifi) {
        if (isWifiEnabled()) {
            ToastHelper.message(getString(R.string.toast_wifi_on),
                    R.layout.item_customtoast, requireContext());
            if (buttonProvider != null) {
                bt_1.setVisibility(View.VISIBLE);
            }
            layoutwifilist.setVisibility(View.VISIBLE);
            tableLayoutwifi.setVisibility(View.INVISIBLE);
            tvSSIconnected.setVisibility(View.VISIBLE);
        } else {
            ToastHelper.message(getString(R.string.toast_wifi_off), R.layout.item_customtoasterror, mainActivity);
        }
    }

    private void handleDeleteWifi(int i) {
        String saveSSI2 = (String) wifiList.getItemAtPosition(i);
        List<WifiConfiguration> configurationList = getWifiConfigurationList();
        if (configurationList == null) return;

        for (WifiConfiguration z : configurationList) {
            if (z.SSID != null && z.SSID.equals("\"" + saveSSI2 + "\"")) {
                deleteWifiDialog(saveSSI2, z);
            }
        }
    }

    private void handleWifiSelected(int i) {
        String saveSSI = (String) wifiList.getItemAtPosition(i);
        List<WifiConfiguration> configurationList = getWifiConfigurationList();
        if (configurationList == null) return;
        boolean isConfigurated = false;
        for ( WifiConfiguration network : configurationList) {
            if(network.SSID != null && network.SSID.equals("\"" + saveSSI + "\"")){
                isConfigurated = true;
                wifiManager.getConnectionInfo().getSSID();
                wifiManager.disconnect();
                wifiManager.enableNetwork(network.networkId, true);
                wifiManager.reconnect();
            }}
        if(!isConfigurated){
            keyboardPassword(null, "Ingrese contraseña", requireContext(), false,password -> connectToWifi(saveSSI, password), PasswordTransformationMethod.getInstance());
        }
    }

    @Nullable
    private List<WifiConfiguration> getWifiConfigurationList() {
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return null;
        return wifiManager.getConfiguredNetworks();
    }

    private void connectToWifi(String saveSSI, String password) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", saveSSI);
        wifiConfig.preSharedKey = String.format("\"%s\"", password);

        WifiManager wifiManager = (WifiManager) requireActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
        wifiConfig.status = WifiConfiguration.Status.DISABLED;
        wifiConfig.priority = 40;
        int netId = wifiManager.addNetwork(wifiConfig);
        wifiManager.enableNetwork(netId, true);
    }

    private void deleteWifiDialog(String saveSSI2, WifiConfiguration z) {
        dialogText(requireContext(), getString(R.string.dialog_wifi_delete) + saveSSI2, getString(R.string.dialog_wifi_delete_button), () -> wifiManager.removeNetwork(z.networkId));
    }

    private void setupButtons() {
        if (buttonProvider != null) {
            bt_1 = buttonProvider.getButton1();
            buttonProvider.getTitulo().setText(R.string.title_fragment_wifi);

            bt_1.setBackgroundResource(R.drawable.boton_atras_i);
            bt_1.setVisibility(View.INVISIBLE);
            buttonProvider.getButton2().setVisibility(View.INVISIBLE);
            buttonProvider.getButton3().setVisibility(View.INVISIBLE);
            buttonProvider.getButton4().setVisibility(View.INVISIBLE);
            buttonProvider.getButton5().setVisibility(View.INVISIBLE);
            buttonProvider.getButton6().setVisibility(View.INVISIBLE);

            buttonProvider.getButtonHome().setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());

        }
    }
    private void getWifi() {
        wifiManager.startScan();
    }
    public boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) mainActivity.getSystemService(Context.WIFI_SERVICE);
        return wifiManager != null && wifiManager.isWifiEnabled();
    }

    Runnable mToastRunnable= new Runnable() {
        @Override
        public void run() {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid=wifiInfo.getSSID();

            if(Objects.equals(ssid, "<unknown ssid>")){
                tvSSIRED.setText(R.string.fragment_wifi_disconnect);
                tvSSIconnected.setText(R.string.fragment_wifi_disconnect);
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


