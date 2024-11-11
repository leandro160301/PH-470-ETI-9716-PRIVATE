package com.jws.jwsapi.home;

import android.content.Context;

import com.jws.jwsapi.core.label.LabelManager;
import com.jws.jwsapi.core.printer.PrinterManager;
import com.jws.jwsapi.core.printer.PrinterPreferences;
import com.jws.jwsapi.line.Line;
import com.jws.jwsapi.line.LineManager;
import com.jws.jwsapi.shared.UserRepository;
import com.jws.jwsapi.shared.WeighRepository;
import com.jws.jwsapi.utils.date.DateUtils;
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
                if (lineManager.getLastLine() == null) return;
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

                printerManager.printLabelFromCode(serialPort, "\u0002\u001BA\u001BA3V+000H+000\u001BCS3\u001B" +
                        "#E3\u001BA106390799\u001BPM1\u001BPO2+00\u001BPO3+00\u001BYE0\u001BPH0\u001BIG1\u001BZ" +
                        "\u0003\u0002\u001BA\u001BPS\u001BWKMANILA SATO\u001B%0\u001BH0057\u001BV0488\u001BL0101" +
                        "\u001BP02\u001BXSConservar en recinto de frio a 2 ø C\u001B%0\u001BH0057\u001BV0505" +
                        "\u001BL0101\u001BP02\u001BXSKeep temp storage from 32 F to 35 F\u001B%0\u001BH0057" +
                        "\u001BV0522\u001BL0101\u001BP02\u001BXSElaborado por MANILA SA \u001B%0\u001BH0057" +
                        "\u001BV0539\u001BL0101\u001BP02\u001BXSEstablecimiento oficial  SENASA 4413\u001B%0" +
                        "\u001BH0057\u001BV0556\u001BL0101\u001BP02\u001BXSBø Pilar II, Bariloche- Pcia. Rio Negr" +
                        "o\u001B%0\u001BH0057\u001BV0573\u001BL0101\u001BP02\u001BXSArgentina\u001B%0\u001BH005" +
                        "3\u001BV0025\u001BL0101\u001BP02\u001BXSRemite / Shipper\u001B%0\u001BH0053\u001BV0090" +
                        "\u001BL0101\u001BP02\u001BXSDestino:\u001B%0\u001BH0053\u001BV0107\u001BL0101\u001BP02" +
                        "\u001BXSShipped to:\u001B%0\u001BH0053\u001BV0232\u001BL0101\u001BP02\u001BXSContiene:" +
                        "     TRUCHA REFRIGERADA\u001B%0\u001BH0053\u001BV0249\u001BL0101\u001BP02\u001BXSFres" +
                        "h Farm Raised Rainbow Trout\u001B%0\u001BH0053\u001BV0266\u001BL0101\u001BP02\u001BXS" +
                        "Oncorhynchus mykiss\u001B%0\u001BH0448\u001BV0046\u001BL0101\u001BP02\u001BXMINDUSTRI" +
                        "A ARGENTINA\u001B%0\u001BH0457\u001BV0102\u001BL0101\u001BP02\u001BXSFecha de elabora" +
                        "ci¢n:\u001B%0\u001BH0457\u001BV0119\u001BL0101\u001BP02\u001BXSPacking date:\u001B%0\u001B" +
                        "H0462\u001BV0194\u001BL0101\u001BP02\u001BXSNumero de lote:\u001B%0\u001BH0462\u001BV" +
                        "0211\u001BL0101\u001BP02\u001BXSBatch #\u001B%0\u001BH0457\u001BV0263\u001BL0101\u001B" +
                        "P02\u001BXSConsumir  antes de:\u001B%0\u001BH0457\u001BV0280\u001BL0101\u001BP02\u001B" +
                        "XSBest before date:\u001B%0\u001BH0462\u001BV0348\u001BL0101\u001BP02\u001BXSPeso Neto" +
                        ":\u001B%0\u001BH0462\u001BV0365\u001BL0101\u001BP02\u001BXSNet Weight:\u001B%0\u001BH04" +
                        "57\u001BV0424\u001BL0101\u001BP02\u001BXSPeso Bruto:\u001B%0\u001BH0457\u001BV0441\u001B" +
                        "L0101\u001BP02\u001BXSGross Weigth:\u001B%0\u001BH0082\u001BV0314\u001BL0101\u001BP02\u001B" +
                        "STRUCHA MARIPOSA\u001B%0\u001BH0082\u001BV0329\u001BL0101\u001BP02\u001BSButterfly Tro" +
                        "ut\u001B%0\u001BH0082\u001BV0365\u001BL0101\u001BP02\u001BSFILET DE TRUCHA\u001B%0\u001B" +
                        "H0082\u001BV0380\u001BL0101\u001BP02\u001BSTrout Filet\u001B%0\u001BH0082\u001BV0417\u001B" +
                        "L0101\u001BP02\u001BSEVISCERADA\u001B%0\u001BH0082\u001BV0432\u001BL0101\u001BP02\u001B" +
                        "SGutted Trout\u001B%0\u001BH0448\u001BV0095\u001BFW0404V0393H0328\u001B%0\u001BH0037\u001B" +
                        "V0017\u001BFW0404V0611H0748\u001B%0\u001BH0452\u001BV0483\u001BFW01H0322\u001B%0\u001BH044" +
                        "8\u001BV0410\u001BFW01H0322\u001B%0\u001BH0450\u001BV0338\u001BFW01H0322\u001B%0\u001BH0451" +
                        "\u001BV0258\u001BFW01H0322\u001B%0\u001BH0448\u001BV0184\u001BFW01H0322\u001B%0\u001BH0244\u001B" +
                        "V0417\u001BL0101\u001BP02\u001BSSENASA 4413/100753/3\u001B%0\u001BH0244\u001BV0432\u001BL0" +
                        "101\u001BP02\u001BSIndustria Argentina\u001B%0\u001BH0244\u001BV0358\u001BL0101\u001BP02\u001B" +
                        "SSENASA 4413/100753/2\u001B%0\u001BH0244\u001BV0373\u001BL0101\u001BP02\u001BSIndustria Arg" +
                        "entina\u001B%0\u001BH0244\u001BV0314\u001BL0101\u001BP02\u001BSSENASA 4413/100753/1\u001B%0" +
                        "\u001BH0244\u001BV0329\u001BL0101\u001BP02\u001BSIndustria Argentina\u001B%0\u001BH0053\u001B" +
                        "V0049\u001BL0202\u001BP02\u001BXSMANILA S.A.\u001B%0\u001BH0045\u001BV0314\u001BFW0202V0032H" +
                        "0032\u001B%0\u001BH0045\u001BV0365\u001BFW0202V0032H0032\u001B%0\u001BH0045\u001BV0417\u001B" +
                        "FW0202V0032H0032\u001B%0\u001BH0457\u001BV0512\u001BBG03059>H"
                        + (lineManager.getLastLine().getDestinationQuantity() + 1) + "\u001B%0\u001BH0542\u001BV05" +
                        "73\u001BP02\u001BRDB00,026,029," + (lineManager.getLastLine().getDestinationQuantity() + 1) +
                        "\u001B%0\u001BH0060\u001BV0139\u001BL0202\u001BP02\u001B" +
                        "XS" + line.getDestinatation() + "\u001B%0\u001BH0598\u001BV0214\u001BP02\u001BRDB00,031,033," + line.getBatch() + "\u001B%0\u001BH05" +
                        "64\u001BV0135\u001BP02\u001BRDB00,031,033," + DateUtils.getDate() + "\u001B%0\u001BH0545\u001BV0301\u001BP02\u001B" +
                        "RDB00,031,033," + line.getExpirateDate() + "\u001B%0\u001BH0578\u001BV0363\u001BP02\u001BRDB00,031,033,"
                        + lineManager.getLastLine().getPartsTare() + weighRepository.getUnit().getValue() + "\u001B" +
                        "%0\u001BH0587\u001BV0429\u001BP02\u001BRDB00,031,033,"
                        + weighRepository.getGrossStr().getValue() + weighRepository.getUnit().getValue() + "\u001B~A0\u001BQ1\u001BZ\u0003");

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
