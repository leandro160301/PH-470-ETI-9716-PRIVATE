package com.jws.jwsapi.home;

import android.content.Context;

import com.jws.jwsapi.core.label.LabelManager;
import com.jws.jwsapi.core.printer.PrinterManager;
import com.jws.jwsapi.core.printer.PrinterPreferences;
import com.jws.jwsapi.line.Line;
import com.jws.jwsapi.line.LineManager;
import com.jws.jwsapi.shared.UserRepository;
import com.jws.jwsapi.shared.WeighRepository;
import com.service.PuertosSerie.PuertosSerie2;

import javax.inject.Inject;

public class HomeService {

    private final UserRepository userRepository;
    private final PrinterPreferences printerPreferences;
    private final LabelManager labelManager;
    private final WeighRepository weighRepository;
    private final LineManager lineManager;

    @Inject
    public HomeService(UserRepository userRepository, PrinterPreferences printerPreferences, LabelManager labelManager, WeighRepository weighRepository, LineManager lineManager) {
        this.userRepository = userRepository;
        this.printerPreferences = printerPreferences;
        this.labelManager = labelManager;
        this.weighRepository = weighRepository;
        this.lineManager = lineManager;
    }

    public void print(Context context, PuertosSerie2 serialPort) {
        Runnable myRunnable = () -> {
            try {
                Line line = lineManager.getCurrentProductionLine();
                labelManager.linea.value = String.valueOf(lineManager.getCurrentProductionLineNumber());
                labelManager.batch.value = line.getBatch();
                labelManager.caliber.value = line.getCaliber();
                labelManager.destinatation.value = line.getDestinatation();
                labelManager.name.value = line.getProduct();
                labelManager.expirateDate.value = line.getExpirateDate();
                labelManager.tareBox.value = line.getBoxTare() + weighRepository.getUnit().getValue();
                labelManager.tareIce.value = line.getIceTare() + weighRepository.getUnit().getValue();
                labelManager.tareParts.value = line.getPartsTare() + weighRepository.getUnit().getValue();
                labelManager.tareTop.value = line.getTopTare() + weighRepository.getUnit().getValue();
                PrinterManager printerManager = new PrinterManager(context, userRepository, printerPreferences, labelManager, weighRepository);
                printerManager.printLabelInMemory(serialPort, 0);

            } catch (Exception e) {
                e.printStackTrace();
            }

        };

        Thread myThread = new Thread(myRunnable);
        myThread.start();


    }

    public void printMemory(Context context, PuertosSerie2 serialPort) {
        Runnable myRunnable = () -> {
            try {
                PrinterManager printerManager = new PrinterManager(context, userRepository, printerPreferences, labelManager, weighRepository);
                printerManager.printLastLabel(serialPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Thread myThread = new Thread(myRunnable);
        myThread.start();
    }

}
