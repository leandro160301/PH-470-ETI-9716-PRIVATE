package com.service.Impresora.Tipos;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.service.R;
import com.service.Utils;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class ImprimirBluetooth {

    private final Context context;
    private AppCompatActivity mainActivity;
    private String mac = "";
    BluetoothSocket bluetoothSocket;

    public ImprimirBluetooth(Context context, AppCompatActivity activity, String mac) {
        this.context = context;
        this.mainActivity = activity;
        this.mac = mac;
    }

    public void Imprimir(String etiqueta) {
        /*Bluetooth bluetooth= new Bluetooth(mainActivity);
        bluetooth.connectDevice(mac);*/

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice bluetoothDevice = bluetoothAdapter.getRemoteDevice(mac);
                    if (ActivityCompat.checkSelfPermission(mainActivity, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    bluetoothSocket = bluetoothDevice.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
                    bluetoothSocket.connect();
                    if (!bluetoothSocket.isConnected()) {
                        mainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Utils.Mensaje("Error impresora bluetooth, verifique que la misma se encuentra encendida", R.layout.item_customtoasterror,mainActivity);
                            }
                        });
                    }
                    Thread.sleep(1000);
                    OutputStream outputStream = bluetoothSocket.getOutputStream();
                    byte[] bytes = etiqueta.getBytes();
                    outputStream.write(bytes);

                    // Maneja la respuesta aquí si es necesario

                    outputStream.close(); // Cerrar el flujo de salida después de escribir
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    mainActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Utils.Mensaje("Error impresora bluetooth, verifique que la misma se encuentra encendida", R.layout.item_customtoasterror,mainActivity);
                        }
                    });
                    // Maneja errores de conexión aquí
                } finally {
                    try {
                        if(bluetoothSocket!=null){
                            bluetoothSocket.close();
                        }
                         // Cerrar el socket después de la escritura
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Thread myThread = new Thread(myRunnable);
        myThread.start();
    }


}
