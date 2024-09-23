package com.jws.jwsapi.home;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.core.label.LabelManager;
import com.jws.jwsapi.core.printer.PrinterManager;
import com.jws.jwsapi.core.printer.PrinterPreferences;
import com.jws.jwsapi.core.user.UserManager;
import com.service.PuertosSerie.PuertosSerie2;

import javax.inject.Inject;

public class HomeService {

    UserManager userManager;
    PrinterPreferences printerPreferences;
    LabelManager labelManager;

    @Inject
    public HomeService(UserManager userManager, PrinterPreferences printerPreferences, LabelManager labelManager) {
        this.userManager = userManager;
        this.printerPreferences = printerPreferences;
        this.labelManager = labelManager;
    }

    public void print(MainActivity mainActivity, PuertosSerie2 serialPort){
        PrinterManager printerManager = new PrinterManager(mainActivity,mainActivity,userManager, printerPreferences,labelManager);
        printerManager.printLabelInMemory(serialPort,0);
    }
}
