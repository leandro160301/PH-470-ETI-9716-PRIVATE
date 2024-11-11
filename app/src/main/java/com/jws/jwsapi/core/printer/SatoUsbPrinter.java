package com.jws.jwsapi.core.printer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class SatoUsbPrinter {

    private static final int VENDOR_ID_SATO = 0x0828;
    private static final int PRODUCT_ID_SATO = 0x0101;

    private final Context context;
    private final UsbManager usbManager;
    private UsbDeviceConnection connection;
    private UsbEndpoint endpointOut;
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
                UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                if (device != null && device.getVendorId() == VENDOR_ID_SATO) {
                    closeConnection(); // Cierra la conexión cuando se desconecta
                    System.out.println("Sato: Dispositivo USB desconectado, conexión cerrada.");
                }
            }
        }
    };

    public SatoUsbPrinter(Context context) {
        this.context = context;
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    public void printLabel(String sbplCommand) {
        IntentFilter filter = new IntentFilter();
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        context.registerReceiver(usbReceiver, filter);
        UsbDevice printerDevice = findSatoPrinter();

        if (printerDevice == null) {
            System.out.println("Sato: No se encontró la impresora SATO");
            return;
        }

        openConnection(printerDevice);

        if (connection != null && endpointOut != null) {
            byte[] sbplData = sbplCommand.getBytes(StandardCharsets.US_ASCII);
            int result = connection.bulkTransfer(endpointOut, sbplData, sbplData.length, 1000);

            if (result >= 0) {
                System.out.println("Sato: Etiqueta enviada correctamente");
                System.out.println("Sato:" + sbplCommand);
            } else {
                System.out.println("Sato: Error al enviar la etiqueta");
            }
            try {
                Thread.sleep(400); // Retardo opcional
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            closeConnection();
            context.unregisterReceiver(usbReceiver);
        } else {
            System.out.println("Sato: No se pudo establecer la conexión USB");
        }
    }

    private UsbDevice findSatoPrinter() {
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        for (UsbDevice device : deviceList.values()) {
            if (device.getVendorId() == VENDOR_ID_SATO && device.getProductId() == PRODUCT_ID_SATO) {
                return device;
            }
        }
        return null;
    }

    private void openConnection(UsbDevice printerDevice) {
        connection = usbManager.openDevice(printerDevice);

        if (connection != null) {
            UsbInterface usbInterface = printerDevice.getInterface(0);
            connection.claimInterface(usbInterface, true);

            // Buscar el endpoint de salida
            for (int i = 0; i < usbInterface.getEndpointCount(); i++) {
                UsbEndpoint endpoint = usbInterface.getEndpoint(i);
                if (endpoint.getDirection() == UsbConstants.USB_DIR_OUT) {
                    endpointOut = endpoint;
                    break;
                }
            }
        }
    }

    private void closeConnection() {
        if (connection != null) {
            connection.close();
            connection = null;
            endpointOut = null;
            System.out.println("Sato: Conexión USB cerrada correctamente.");
        }
    }
}