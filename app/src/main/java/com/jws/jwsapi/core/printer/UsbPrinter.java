package com.jws.jwsapi.core.printer;

import android.content.Context;
import android.hardware.usb.UsbManager;

import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.ToastHelper;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.printer.FieldDescriptionData;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;
import com.zebra.sdk.printer.discovery.DiscoveredPrinter;
import com.zebra.sdk.printer.discovery.DiscoveryHandler;
import com.zebra.sdk.printer.discovery.UsbDiscoverer;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UsbPrinter {

    private final Context context;
    List<FieldDescriptionData> variablesList = new ArrayList<>();
    Connection connection;
    DiscoveredPrinterListAdapter discoveredPrinterListAdapter;
    Map<Integer, String> vars;

    public UsbPrinter(Context context) {
        this.context = context;
    }


    public void print(String label, Context context, Boolean memory, List<String> memoryList) {
        Runnable myRunnable = () -> {
            try {
                discoveredPrinterListAdapter = new DiscoveredPrinterListAdapter(context);
                UsbManager usbManager = (UsbManager) context.getSystemService(Context.USB_SERVICE);

                UsbDiscoverer.findPrinters(usbManager, new DiscoveryHandler() {
                    public void foundPrinter(final DiscoveredPrinter printer) {
                        discoveredPrinterListAdapter.addPrinter(printer);
                    }

                    public void discoveryFinished() {
                    }

                    public void discoveryError(String message) {
                    }
                });
                Thread.sleep(300);
                if (discoveredPrinterListAdapter.getCount() > 0) {
                    DiscoveredPrinter printer = discoveredPrinterListAdapter.getPrinter(0);
                    SelectedPrinterManager.setSelectedPrinter(printer);
                    Print(label, memory, memoryList);
                }

            } catch (InterruptedException e) {
                ToastHelper.message("usb init:" + e.getMessage(), R.layout.item_customtoasterror, context);
            }
        };

        Thread myThread = new Thread(myRunnable);
        myThread.start();


    }

    protected void Print(String Etiqueta, Boolean Memoria, List<String> ListaMemoria) {

        try {
            connection = SelectedPrinterManager.getPrinterConnection();
        } catch (Exception e) {
            ToastHelper.message("usb 0:" + e.getMessage(), R.layout.item_customtoasterror, context);
        }


        if (connection != null) {
            try {
                connection.open();
                if (Memoria) {
                    ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
                    String formatContents = new String(printer.retrieveFormatFromPrinter(Etiqueta), StandardCharsets.UTF_8);

                    FieldDescriptionData[] variables;
                    variables = printer.getVariableFields(formatContents);

                    Collections.addAll(variablesList, variables);

                    vars = new HashMap<>();
                    for (int i = 0; i < ListaMemoria.size(); i++) {
                        FieldDescriptionData var = variablesList.get(i);
                        vars.put(var.fieldNumber, ListaMemoria.get(i));
                    }
                }

                ZebraPrinter printer = null;

                try {
                    printer = ZebraPrinterFactory.getInstance(connection);
                } catch (Exception e) {
                    ToastHelper.message("usb 1:" + e.getMessage(), R.layout.item_customtoasterror, context);
                }

                if (printer != null && Memoria) {
                    printer.printStoredFormat(Etiqueta, vars);
                } else {
                    if (printer != null) {
                        printer.sendCommand(Etiqueta);
                    } else {
                        ToastHelper.message("usb 4: printer null", R.layout.item_customtoasterror, context);
                    }

                }

            } catch (ConnectionException | ZebraPrinterLanguageUnknownException e) {
                ToastHelper.message("usb 2:" + e.getMessage(), R.layout.item_customtoasterror, context);
            } finally {
                try {
                    connection.close();
                } catch (ConnectionException e) {
                    ToastHelper.message("usb 3:" + e.getMessage(), R.layout.item_customtoasterror, context);
                }
            }
        } else {
            ToastHelper.message("usb 5: connection null", R.layout.item_customtoasterror, context);
        }

    }


}
