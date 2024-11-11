package com.jws.jwsapi.core.printer;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;

import java.nio.charset.StandardCharsets;

public class UsbPrinterManager {
    private static final int VENDOR_ID_SATO = 0x0828;
    private static final int PRODUCT_ID_SATO = 0x0101;
    private final UsbManager usbManager;
    private UsbDeviceConnection connection;

    public UsbPrinterManager(Context context) {
        usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);
    }

    public boolean printLabel(String label) {
        UsbDevice device = findPrinterDevice();
        if (device == null) {
            return false;
        }

        UsbInterface usbInterface = device.getInterface(0);
        UsbEndpoint outEndpoint = usbInterface.getEndpoint(0);

        connection = usbManager.openDevice(device);
        if (connection == null || !connection.claimInterface(usbInterface, true)) {
            return false;
        }

        byte[] data = label.getBytes(StandardCharsets.UTF_8);
        int result = connection.bulkTransfer(outEndpoint, data, data.length, 1000);

        connection.releaseInterface(usbInterface);
        connection.close();

        return result >= 0;
    }

    private UsbDevice findPrinterDevice() {
        for (UsbDevice device : usbManager.getDeviceList().values()) {
            if (device.getVendorId() == VENDOR_ID_SATO && device.getProductId() == PRODUCT_ID_SATO) {
                return device;
            }
        }
        return null;
    }
}