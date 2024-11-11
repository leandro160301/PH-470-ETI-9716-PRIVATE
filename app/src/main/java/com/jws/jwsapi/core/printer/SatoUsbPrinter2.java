package com.jws.jwsapi.core.printer;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

public class SatoUsbPrinter2 {

    private static final int VENDOR_ID_SATO = 0x0828; // Vendor ID de SATO
    private static final int PRODUCT_ID_SATO = 0x0101; // Product ID de la impresora SATO

    private final UsbManager usbManager;

    public SatoUsbPrinter2(UsbManager usbManager) {
        this.usbManager = usbManager;
    }

    public void printLabel(String sbplCommand) {
        UsbDevice printerDevice = findSatoPrinter();

        if (printerDevice == null) {
            System.out.println("Sato: No se encontró la impresora SATO");
            return;
        }

        // Encuentra la ruta del dispositivo USB, no dependa de getDeviceAddress ni getBusNumber
        String devicePath = getDevicePath(printerDevice);
        if (devicePath != null) {
            writeToDevice(devicePath, sbplCommand);
        } else {
            System.out.println("Sato: No se pudo encontrar la ruta del dispositivo");
        }
    }

    // Encontrar la impresora SATO en la lista de dispositivos USB
    private UsbDevice findSatoPrinter() {
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();

        for (UsbDevice device : deviceList.values()) {
            if (device.getVendorId() == VENDOR_ID_SATO && device.getProductId() == PRODUCT_ID_SATO) {
                return device;
            }
        }
        return null;
    }

    // Obtener la ruta del dispositivo en el sistema de archivos (/dev/bus/usb)
    private String getDevicePath(UsbDevice printerDevice) {
        String busPath = "/dev/bus/usb/";

        // Listar todos los archivos en el directorio de buses USB
        File busDirectory = new File(busPath);
        File[] busDirs = busDirectory.listFiles();
        if (busDirs != null) {
            for (File busDir : busDirs) {
                if (busDir.isDirectory()) {
                    // Examinar los dispositivos dentro del bus USB
                    File[] deviceFiles = busDir.listFiles();
                    for (File devFile : deviceFiles) {
                        try {
                            // Compara los IDs de los dispositivos
                            if (devFile.getName().matches("^\\d{3}$")) { // Asegurarse de que es un número de dispositivo válido
                                String devFilePath = devFile.getAbsolutePath();
                                String fileContent = new String(Files.readAllBytes(Paths.get(devFilePath)), StandardCharsets.UTF_8);

                                if (fileContent.contains("Vendor ID: " + String.format("%04x", printerDevice.getVendorId())) &&
                                        fileContent.contains("Product ID: " + String.format("%04x", printerDevice.getProductId()))) {
                                    // Si el archivo tiene el Vendor ID y Product ID correctos, retornar la ruta
                                    return devFile.getAbsolutePath();
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return null; // Si no se encuentra la ruta del dispositivo
    }

    // Escribir el comando SBPL al dispositivo USB
    private void writeToDevice(String devicePath, String sbplCommand) {
        FileOutputStream outputStream = null;

        try {
            // Abrir el archivo de dispositivo para escritura
            outputStream = new FileOutputStream(devicePath);

            // Convertir el comando SBPL a bytes
            byte[] sbplData = sbplCommand.getBytes(StandardCharsets.US_ASCII);

            // Escribir el comando en el dispositivo USB
            outputStream.write(sbplData);

            System.out.println("Sato: Etiqueta enviada correctamente");
        } catch (IOException e) {
            System.out.println("Sato: Error al enviar la etiqueta - " + e.getMessage());
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                System.out.println("Sato: Error al cerrar el archivo - " + e.getMessage());
            }
        }
    }
}