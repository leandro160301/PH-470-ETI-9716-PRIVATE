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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.android.jws.JwsManager;
import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.StandarWifiBinding;
import com.jws.jwsapi.utils.NetworkUtils;
import com.jws.jwsapi.utils.ToastHelper;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.util.List;
import java.util.Objects;

public class WifiFragment extends Fragment {

    private final Handler handler = new Handler();
    private Button bt_1;
    private WifiManager wifiManager;
    private JwsManager jwsobject;
    private boolean stoped = false;
    private MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    private StandarWifiBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        binding = StandarWifiBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setupButtons();
        mainActivity = (MainActivity) getActivity();

        setupViews();

        initClass();

        setupToggle();

        handleWifiOnOff();

        setOnClickListeners();

        initWifiReceiver();

        handleWifiListItem();

        mToastRunnable.run();

    }

    private void initWifiReceiver() {
        WifiReceiver receiverWifi = new WifiReceiver(wifiManager, binding.wifiList);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        requireActivity().registerReceiver(receiverWifi, intentFilter);
        getWifi();
    }

    private void handleWifiListItem() {
        binding.wifiList.setOnItemLongClickListener((adapterView, view12, i, l) -> {
            handleDeleteWifi(i);
            return true;
        });

        binding.wifiList.setOnItemClickListener((adapterView, view1, i, l) -> handleWifiSelected(i));
    }

    private void setupViews() {
        binding.layoutwifilist.setVisibility(View.INVISIBLE);
        binding.tvSSIconnected.setVisibility(View.INVISIBLE);
        binding.tvMAC.setText(NetworkUtils.getWifiMac());
    }

    private void setOnClickListeners() {
        bt_1.setOnClickListener(v -> {
            if (buttonProvider != null) {
                bt_1.setVisibility(View.INVISIBLE);
            }
            setupVisibilityPanel(binding.layoutwifilist, View.INVISIBLE, binding.tableLayoutwifi, View.VISIBLE);
        });

        binding.tvred.setOnClickListener(v -> showWifiList(binding.layoutwifilist, binding.tableLayoutwifi));
    }

    private void setupToggle() {
        if (isWifiEnabled()) {
            binding.toggle.check(R.id.btON);
        } else {
            binding.toggle.check(R.id.btOFF);
            setupLinearVisibility(View.INVISIBLE);
        }
    }

    private void initClass() {
        wifiManager = (WifiManager) requireActivity().getApplicationContext().getSystemService(WIFI_SERVICE);
        jwsobject = JwsManager.create(getContext());
    }

    private void handleWifiOnOff() {
        binding.toggle.setOnCheckedChangeListener((radioGroup, i) -> {
            if (binding.toggle.getCheckedRadioButtonId() == R.id.btON) {
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
    }

    private void setupLinearVisibility(int invisible) {
        setupVisibilityPanel(binding.ln4, invisible, binding.ln5, invisible);
        binding.ln6.setVisibility(invisible);
    }

    private void showWifiList(LinearLayout layoutwifilist, LinearLayout tableLayoutwifi) {
        if (isWifiEnabled()) {
            ToastHelper.message(getString(R.string.toast_wifi_on), R.layout.item_customtoast, requireContext());
            if (buttonProvider != null) bt_1.setVisibility(View.VISIBLE);
            setupVisibilityPanel(layoutwifilist, View.VISIBLE, tableLayoutwifi, View.INVISIBLE);
            binding.tvSSIconnected.setVisibility(View.VISIBLE);
        } else {
            ToastHelper.message(getString(R.string.toast_wifi_off), R.layout.item_customtoasterror, requireContext());
        }
    }

    private void handleDeleteWifi(int i) {
        String saveSSI2 = (String) binding.wifiList.getItemAtPosition(i);
        List<WifiConfiguration> configurationList = getWifiConfigurationList();
        if (configurationList == null) return;

        for (WifiConfiguration network : configurationList) {
            if (network.SSID != null && network.SSID.equals("\"" + saveSSI2 + "\"")) {
                deleteWifiDialog(saveSSI2, network);
            }
        }
    }

    private void handleWifiSelected(int i) {
        String saveSSI = (String) binding.wifiList.getItemAtPosition(i);
        List<WifiConfiguration> configurationList = getWifiConfigurationList();
        if (configurationList == null) return;
        boolean isConfigurated = false;
        for (WifiConfiguration network : configurationList) {
            if (network.SSID != null && network.SSID.equals("\"" + saveSSI + "\"")) {
                isConfigurated = true;
                wifiManager.getConnectionInfo().getSSID();
                wifiManager.disconnect();
                wifiManager.enableNetwork(network.networkId, true);
                wifiManager.reconnect();
            }
        }
        if (!isConfigurated) {
            keyboardPassword(null, getString(R.string.dialog_wifi_password), requireContext(), false, password -> connectToWifi(saveSSI, password), PasswordTransformationMethod.getInstance());
        }
    }

    @Nullable
    private List<WifiConfiguration> getWifiConfigurationList() {
        if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return null;
        return wifiManager.getConfiguredNetworks();
    }

    private void connectToWifi(String ssid, String password) {
        WifiConfiguration wifiConfig = new WifiConfiguration();
        wifiConfig.SSID = String.format("\"%s\"", ssid);
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
            buttonProvider.getTitle().setText(R.string.title_fragment_wifi);

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

    Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID();
            try {
                if (Objects.equals(ssid, "<unknown ssid>")) {
                    binding.tvSSIRED.setText(R.string.fragment_wifi_disconnect);
                    binding.tvSSIconnected.setText(R.string.fragment_wifi_disconnect);
                } else {
                    binding.tvSSIRED.setText(wifiInfo.getSSID());
                    binding.tvSSIconnected.setText(String.format("%s%s", requireContext().getString(R.string.fragment_wifi_connect_to), wifiInfo.getSSID()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (!stoped) {
                handler.postDelayed(this, 1000);
            }
        }
    };

    private static void setupVisibilityPanel(LinearLayout layoutwifilist, int visible, LinearLayout tableLayoutwifi, int invisible) {
        layoutwifilist.setVisibility(visible);
        tableLayoutwifi.setVisibility(invisible);
    }


    @Override
    public void onDestroyView() {
        stoped = true;
        super.onDestroyView();
    }
}


