package com.jws.jwsapi.core.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class WifiReceiver extends BroadcastReceiver {
    List<ScanResult> wifiList;
    WifiManager wifiManager;
    StringBuilder sb;
    ListView wifiDeviceList;


    public WifiReceiver(WifiManager wifiManager, ListView wifiDeviceList) {
        this.wifiManager = wifiManager;
        this.wifiDeviceList = wifiDeviceList;
    }

    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)) {
            sb = new StringBuilder();
            wifiList = wifiManager.getScanResults();

            ArrayList<String> deviceList = new ArrayList<>();
            for (ScanResult scanResult : wifiList) {
                System.out.println(scanResult.toString());
                sb.append("\n").append(scanResult.SSID).append(" - ").append(scanResult.capabilities);
                if(!scanResult.SSID.equals("")&&!deviceList.contains(scanResult.SSID)){
                deviceList.add(scanResult.SSID);
            }}

            ArrayAdapter arrayAdapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1, Objects.requireNonNull(deviceList.toArray()));

            wifiDeviceList.setAdapter(arrayAdapter);
        }
    }
}
