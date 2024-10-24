package com.jws.jwsapi.home;

import android.content.Context;

import com.jws.jwsapi.core.label.LabelManager;
import com.jws.jwsapi.core.printer.PrinterManager;
import com.jws.jwsapi.core.printer.PrinterPreferences;
import com.jws.jwsapi.productionline.ProductionLine;
import com.jws.jwsapi.productionline.ProductionLineManager;
import com.jws.jwsapi.shared.UserRepository;
import com.jws.jwsapi.shared.WeighRepository;
import com.service.PuertosSerie.PuertosSerie2;

import javax.inject.Inject;

public class HomeService {

    private final UserRepository userRepository;
    private final PrinterPreferences printerPreferences;
    private final LabelManager labelManager;
    private final WeighRepository weighRepository;
    private final ProductionLineManager productionLineManager;

    @Inject
    public HomeService(UserRepository userRepository, PrinterPreferences printerPreferences, LabelManager labelManager, WeighRepository weighRepository, ProductionLineManager productionLineManager) {
        this.userRepository = userRepository;
        this.printerPreferences = printerPreferences;
        this.labelManager = labelManager;
        this.weighRepository = weighRepository;
        this.productionLineManager = productionLineManager;
    }

    public void print(Context context, PuertosSerie2 serialPort) {
        Runnable myRunnable = () -> {
            try {
                ProductionLine productionLine = productionLineManager.getCurrentProductionLine();
                labelManager.linea.value = String.valueOf(productionLineManager.getCurrentProductionLineNumber());
                labelManager.batch.value = productionLine.getBatch();
                labelManager.caliber.value = productionLine.getCaliber();
                labelManager.destinatation.value = productionLine.getDestinatation();
                labelManager.name.value = productionLine.getProduct();
                labelManager.expirateDate.value = productionLine.getExpirateDate();
                labelManager.tareBox.value = productionLine.getBoxTare();
                labelManager.tareIce.value = productionLine.getIceTare();
                labelManager.tareParts.value = productionLine.getPartsTare();
                labelManager.tareTop.value = productionLine.getTopTare();
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
