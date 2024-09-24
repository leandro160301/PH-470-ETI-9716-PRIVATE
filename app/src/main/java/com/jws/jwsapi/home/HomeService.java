package com.jws.jwsapi.home;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.core.label.LabelManager;
import com.jws.jwsapi.core.printer.PrinterManager;
import com.jws.jwsapi.core.printer.PrinterPreferences;
import com.jws.jwsapi.core.user.UserManager;
import com.jws.jwsapi.pallet.Pallet;
import com.jws.jwsapi.shared.PalletRepository;
import com.jws.jwsapi.shared.WeighRepository;
import com.service.Balanzas.BalanzaService;
import com.service.PuertosSerie.PuertosSerie2;

import javax.inject.Inject;

public class HomeService {

    UserManager userManager;
    PrinterPreferences printerPreferences;
    LabelManager labelManager;
    WeighRepository weighRepository;
    PalletRepository palletRepository;

    @Inject
    public HomeService(UserManager userManager, PrinterPreferences printerPreferences, LabelManager labelManager, WeighRepository weighRepository, PalletRepository palletRepository) {
        this.userManager = userManager;
        this.printerPreferences = printerPreferences;
        this.labelManager = labelManager;
        this.weighRepository = weighRepository;
        this.palletRepository = palletRepository;
    }

    public void print(MainActivity mainActivity, PuertosSerie2 serialPort){
        Runnable myRunnable = () -> {
            try {
                Thread.sleep(500);
                try {
                    Pallet pallet = palletRepository.getCurrentPallet().getValue();
                    if(pallet != null) {
                        labelManager.setDestination(pallet.getDestinationPallet());
                        labelManager.setScale(String.valueOf(weighRepository.getScaleNumber()));
                        labelManager.setCode(pallet.getCode());
                        labelManager.setNumber(String.valueOf(pallet.getDone()));
                        labelManager.setOrigin(pallet.getOriginPallet());
                        labelManager.setName(pallet.getName());
                        PrinterManager printerManager = new PrinterManager(mainActivity,mainActivity,userManager, printerPreferences,labelManager);
                        printerManager.printLabelInMemory(serialPort,0);
                    }
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
}
