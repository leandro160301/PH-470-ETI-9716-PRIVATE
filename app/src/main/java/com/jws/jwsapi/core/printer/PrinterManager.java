package com.jws.jwsapi.core.printer;

import static com.jws.jwsapi.core.printer.PrinterMode.MODE_NETWORK;
import static com.jws.jwsapi.core.printer.PrinterMode.MODE_RS232;
import static com.jws.jwsapi.core.printer.PrinterMode.MODE_USB;

import android.content.Context;

import com.jws.jwsapi.R;
import com.jws.jwsapi.core.label.LabelManager;
import com.jws.jwsapi.shared.UserRepository;
import com.jws.jwsapi.shared.WeighRepository;
import com.jws.jwsapi.utils.NetworkUtils;
import com.jws.jwsapi.utils.ToastHelper;
import com.service.PuertosSerie.PuertosSerie2;

public class PrinterManager {
    private final Context context;
    UserRepository userRepository;
    PrinterPreferences printerPreferences;
    LabelManager labelManager;
    PrinterHelper printerHelper;

    public PrinterManager(Context context, UserRepository userRepository, PrinterPreferences printerPreferences, LabelManager labelManager, WeighRepository weighRepository) {
        this.context = context;
        this.printerPreferences = printerPreferences;
        this.labelManager = labelManager;
        this.userRepository = userRepository;
        printerHelper = new PrinterHelper(context, printerPreferences, labelManager, userRepository, weighRepository);
    }

    public void printLabelInMemory(PuertosSerie2 serialPort, int numetiqueta) {
        printLabelFromCode(serialPort, printerHelper.getLabelCode(numetiqueta));
    }

    public void printLastLabel(PuertosSerie2 serialPort) {
        printLabelFromCode(serialPort, printerPreferences.getLastLabel());
    }

    public void printLabelFromCode(PuertosSerie2 serialPort, String label) {
        int modeOutput = printerPreferences.getMode();
        switch (modeOutput) {
            case MODE_USB:
                usbPrint(label);
                break;
            case MODE_NETWORK:
                networkPrint(label);
                break;
            case MODE_RS232:
                rs232Print(serialPort, label);
                break;
        }
    }

    private void rs232Print(PuertosSerie2 serialPort, String label) {
        if (serialPort != null) {
            SerialPortPrinter serialPortPrinter = new SerialPortPrinter(serialPort);
            serialPortPrinter.print(label);
        } else {
            ToastHelper.message(context.getString(R.string.toast_printer_rs232_error), R.layout.item_customtoasterror, context);
        }
    }

    private void networkPrint(String label) {
        if (NetworkUtils.isValidIp(printerPreferences.getIp())) {
            NetworkPrinter networkPrinter = new NetworkPrinter();
            networkPrinter.print(printerPreferences.getIp(), label);
        } else {
            ToastHelper.message(context.getString(R.string.toast_printer_network_error), R.layout.item_customtoasterror, context);
        }
    }

    private void usbPrint(String label) {
        UsbPrinter usbPrinter = new UsbPrinter(context);
        usbPrinter.print(label, context, false, null);
    }

}
