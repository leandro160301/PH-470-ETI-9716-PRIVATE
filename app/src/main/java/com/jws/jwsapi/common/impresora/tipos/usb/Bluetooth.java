package com.jws.jwsapi.common.impresora.tipos.usb;

import android.annotation.SuppressLint;

import com.harrysoft.androidbluetoothserial.BluetoothManager;
import com.harrysoft.androidbluetoothserial.BluetoothSerialDevice;
import com.harrysoft.androidbluetoothserial.SimpleBluetoothDeviceInterface;
import com.jws.jwsapi.base.ui.activities.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.Utils;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class Bluetooth {
    private MainActivity mainActivity;
    public Bluetooth(MainActivity activity) {
        this.mainActivity = activity;

    }

    @SuppressLint("CheckResult")
    public void connectDevice(String mac) {
        BluetoothManager bluetoothManager = BluetoothManager.getInstance();
        bluetoothManager.openSerialDevice(mac)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onConnected, this::onError);
    }

    private void onConnected(BluetoothSerialDevice connectedDevice) {
        // You are now connected to this device!
        // Here you may want to retain an instance to your device:
        SimpleBluetoothDeviceInterface deviceInterface;

        deviceInterface = connectedDevice.toSimpleDeviceInterface();

        // Listen to bluetooth events
        deviceInterface.setListeners(this::onMessageReceived, this::onMessageSent, this::onError);

        // Let's send a message:
        deviceInterface.sendMessage("^XA~TA000~JSN^LT0^MNW^MTD^PON^PMN^LH0,0^JMA^PR5,5~SD6^JUS^LRN^CI0^XZ\n" +
                "^XA\n" +
                "^MMT\n" +
                "^PW799\n" +
                "^LL0799\n" +
                "^LS0\n" +
                "^FO448,64^GFA,06400,06400,00040,:Z64:\n" +
                "eJzt2LENwCAMRFGoMgajMFoYLaMwRiqITLo0Z0t0+Vc/CQl3PyX38pTr5g7tbnNFu2Guajcj7nS4FnAONi9zTX7xuZw+Rd3sSsfhcDgcDofD4XA4HA6Hw+Fw38U6XJPu7X9dsbx64u6OWR0uBVzRbPVdby/29mf98NAXY//YA6WQOPI=:C464\n" +
                "^FT84,361^A0I,45,36^FH\\^FD4^FS\n" +
                "^FT185,428^A0I,45,36^FH\\^FD27/02/2024^FS\n" +
                "^FT653,430^A0I,51,45^FH\\^FD10.00kg^FS\n" +
                "^FT661,498^A0I,48,43^FH\\^FD-10.00kg^FS\n" +
                "^FT784,594^A0I,73,76^FH\\^FDCREMOSO^FS\n" +
                "^FT732,114^A0I,54,52^FH\\^FD5.00kg^FS\n" +
                "^FT657,363^A0I,48,45^FH\\^FD789^FS\n" +
                "^FT545,732^A0I,37,36^FH\\^FD15:19^FS\n" +
                "^FT765,730^A0I,39,38^FH\\^FD12/12/2023^FS\n" +
                "^FT775,429^A0I,48,50^FH\\^FDTara^FS\n" +
                "^FT395,361^A0I,42,38^FH\\^FDUnidades por caja:^FS\n" +
                "^FT742,229^A0I,31,31^FH\\^FDPESO NETO^FS\n" +
                "^FT779,503^A0I,39,40^FH\\^FDBruto^FS\n" +
                "^FT395,428^A0I,42,38^FH\\^FDVencimiento:^FS\n" +
                "^FT140,729^A0I,39,43^FH\\^FDCOPIA^FS\n" +
                "^FT775,366^A0I,45,48^FH\\^FDLote^FS\n" +
                "^FO14,703^GB778,0,8^FS\n" +
                "^BY3,3,160^FT410,60^BCI,,N,N\n" +
                "^FD>;000114563502>68^FS\n" +
                "^PQ1,0,1,Y^XZ");
    }

    private void onMessageSent(String message) {
        // We sent a message! Handle it here.
       Utils.Mensaje("mensaje enviado",R.layout.item_customtoasterror,mainActivity);
    }

    private void onMessageReceived(String message) {
        // We received a message! Handle it here.
        Utils.Mensaje("llega mensaje",R.layout.item_customtoasterror,mainActivity);
    }

    private void onError(Throwable error) {
        Utils.Mensaje("error:"+error.toString(),R.layout.item_customtoasterror,mainActivity);
    }
}
