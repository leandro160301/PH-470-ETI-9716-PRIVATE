package com.jws.jwsapi.home;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.core.label.LabelManager;
import com.jws.jwsapi.core.printer.PrinterManager;
import com.jws.jwsapi.core.printer.PrinterPreferences;
import com.jws.jwsapi.shared.UserRepository;
import com.jws.jwsapi.shared.WeighRepository;
import com.service.PuertosSerie.PuertosSerie2;

import javax.inject.Inject;

public class HomeService {

    private final UserRepository userRepository;
    private final PrinterPreferences printerPreferences;
    private final LabelManager labelManager;
    private final WeighRepository weighRepository;

    @Inject
    public HomeService(UserRepository userRepository, PrinterPreferences printerPreferences, LabelManager labelManager, WeighRepository weighRepository) {
        this.userRepository = userRepository;
        this.printerPreferences = printerPreferences;
        this.labelManager = labelManager;
        this.weighRepository = weighRepository;
    }

    public void print(MainActivity mainActivity, PuertosSerie2 serialPort) {
        Runnable myRunnable = () -> {
            try {
                Thread.sleep(500);
                try {
                    PrinterManager printerManager = new PrinterManager(mainActivity, mainActivity, userRepository, printerPreferences, labelManager, weighRepository);
                    printerManager.printLabelInMemory(serialPort, 0);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        Thread myThread = new Thread(myRunnable);
        myThread.start();


    }

    public void printMemory(MainActivity mainActivity, PuertosSerie2 serialPort) {
        Runnable myRunnable = () -> {
            try {
                PrinterManager printerManager = new PrinterManager(mainActivity, mainActivity, userRepository, printerPreferences, labelManager, weighRepository);
                printerManager.printLastLabel(serialPort);
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
        Thread myThread = new Thread(myRunnable);
        myThread.start();
    }

}
