package com.jws.jwsapi.general.printer.types;

import android.content.Context;
import android.hardware.usb.UsbManager;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.general.printer.types.usb.more.DiscoveredPrinterListAdapter;
import com.jws.jwsapi.general.printer.types.usb.more.SelectedPrinterManager;
import com.jws.jwsapi.R;
import com.jws.jwsapi.general.utils.Utils;
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


    /**
     * ImprimirUSB imprimirUSB= new ImprimirUSB();
     *         imprimirUSB.Imprimir("^XA~TA000~JSN^LT0^MNW^MTT^PON^PMN^LH0,0^JMA^PR6,6~SD15^JUS^LRN^CI0^XZ\n" +
     *                 "^XA\n" +
     *                 "^MMT\n" +
     *                 "^PW799\n" +
     *                 "^LL0519\n" +
     *                 "^LS0\n" +
     *                 "^FO0,416^GFA,02688,02688,00028,:Z64:\n" +
     *                 "eJztkzFuwjAUhp9jRUZI2AxU6oDaFfUMFeEIDCD1GBm74a1jr5AR+RQ+AkPZiJQjZGRAdZ8dR4USe6Cq1CFPUQZ/+nj+3wsAffXV12+KRZiInGYRNo+w1xtZ/vdshdOQQI04Itpof5qoodomSkJWQWaEkchOnpFyVB5GpQZzBIOsQmbOvaEqiDk5VlsmPRwDTCawpNWc1oKTI+Q5axvOAAYzmFH9SLXgOJQ8p5Vny8ZjUuDD4RkZqS49BuwNkE2RQX3Wb2xjs3fLHs5Z4yGjjnHL2sncA0yngGcdzHpPAWb73QV+03pDJZm/C4f68p7pXrcZLpj1QBVtdk6q7+zWA7JkWriZcVr5mSXew3nWbtbAhc6Fm3Xa5AN4wR0RY5Bl0u8oka1nd7txDPxuyaLph+9Njd8EsjlsPputF+0erv9ZZOf3N/5J0Ns6b2DfV96q/V66vES5eRbXLD2QEvOlpQ569rvu6hcsvGewyMeNXqyfCrO0LCNeVzLfb71eRLwi6O33u4gXTIj9ggnRiyTs61/XF/ZBjzM=:9986\n" +
     *                 "^FT56,364^A0I,37,36^FH\\^FD"+contenedor+"^FS\n" +
     *                 "^FT662,300^A0I,40,36^FH\\^FD"+tara+"kg"+"^FS\n" +
     *                 "^FT662,361^A0I,40,36^FH\\^FD"+bruto+"kg"+"^FS\n" +
     *                 "^FT579,239^A0I,40,36^FH\\^FD"+destinatario+"^FS\n" +
     *                 "^FT325,42^A0I,65,62^FH\\^FD"+neto+"kg"+"^FS\n" +
     *                 "^FT601,179^A0I,39,36^FH\\^FD"+transporte+"^FS\n" +
     *                 "^FT451,457^A0I,37,36^FH\\^FD"+hora+"^FS\n" +
     *                 "^FT778,457^A0I,37,36^FH\\^FD"+fecha+"^FS\n" +
     *                 "^FT783,300^A0I,39,40^FH\\^FDTara:^FS\n" +
     *                 "^FT783,361^A0I,39,40^FH\\^FDBruto:^FS\n" +
     *                 "^FT244,364^A0I,37,38^FH\\^FDContenedor:^FS\n" +
     *                 "^FT782,241^A0I,39,38^FH\\^FDDestinatario:^FS\n" +
     *                 "^FT781,181^A0I,37,38^FH\\^FDTransporte:^FS\n" +
     *                 "^FO14,423^GB778,0,8^FS\n" +
     *                 "^FO6,7^GB787,505,8^FS\n" +
     *                 "^FO7,8^GB351,116,8^FS\n" +
     *                 "^PQ1,0,1,Y^XZ",this,getApplicationContext(),false,null);
     */


    List<FieldDescriptionData> variablesList = new ArrayList<FieldDescriptionData>();
    Connection connection;
    DiscoveredPrinterListAdapter discoveredPrinterListAdapter;
    Map<Integer, String> vars;


    private MainActivity mainActivity;

    public UsbPrinter(MainActivity activity) {
        this.mainActivity = activity;
    }


    public void Imprimir(String Etiqueta,Context context,Boolean Memoria,List<String> ListaMemoria){
        Runnable myRunnable = () -> {
            try {
                discoveredPrinterListAdapter = new DiscoveredPrinterListAdapter(context);
                UsbManager usbManager = (UsbManager) mainActivity.getSystemService(Context.USB_SERVICE);

                UsbDiscoverer.findPrinters(usbManager, new DiscoveryHandler() {
                    public void foundPrinter(final DiscoveredPrinter printer) {
                        discoveredPrinterListAdapter.addPrinter(printer);
                    }
                    public void discoveryFinished() { }
                    public void discoveryError(String message) { }
                });
                Thread.sleep(300);
                if(discoveredPrinterListAdapter.getCount()>0){
                    DiscoveredPrinter printer = discoveredPrinterListAdapter.getPrinter(0);
                    SelectedPrinterManager.setSelectedPrinter(printer);
                    DiscoveredPrinter formatPrinter;
                    formatPrinter = SelectedPrinterManager.getSelectedPrinter();
                    Print(Etiqueta,Memoria,ListaMemoria);
                }

            } catch (InterruptedException e) {
                Utils.Mensaje("usb init:"+e.getMessage(),R.layout.item_customtoasterror,mainActivity);
            }
        };

        Thread myThread = new Thread(myRunnable);
        myThread.start();



    }

    protected void Print(String Etiqueta,Boolean Memoria,List<String> ListaMemoria) {

        try {
            connection = SelectedPrinterManager.getPrinterConnection();
        }catch (Exception e){
            Utils.Mensaje("usb 0:"+e.getMessage(), R.layout.item_customtoasterror,mainActivity);
        }


        if (connection != null) {
            try {
                connection.open();
                /*
                *  ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
                String pruebasasasss= new String(printer.retrieveFormatFromPrinter("! U1 do \"file.dir\" \"E:\""),StandardCharsets.UTF_8);
                System.out.println("oooole1");
                System.out.println(pruebasasasss);
                String pruebasasa= new String(printer.retrieveFormatFromPrinter("^XA^HFE:ENS.ZPL^XZ"),StandardCharsets.UTF_8);
                System.out.println("oooole2");
                System.out.println(pruebasasa);
                * */

                if(Memoria){
                    ZebraPrinter printer = ZebraPrinterFactory.getInstance(connection);
                    String formatContents = new String(printer.retrieveFormatFromPrinter(Etiqueta), StandardCharsets.UTF_8);

                    FieldDescriptionData[] variables;
                    variables = printer.getVariableFields(formatContents);

                    Collections.addAll(variablesList, variables);

                    vars = new HashMap<Integer, String>();
                    for (int i = 0; i < ListaMemoria.size(); i++) {
                        FieldDescriptionData var = variablesList.get(i);
                        vars.put(var.fieldNumber, ListaMemoria.get(i));
                    }
                }

                String quantityString ="1";

                int quantity;
                try {
                    quantity = Integer.parseInt(quantityString);
                    if (quantity < 1) {
                        quantity = 1;
                    }
                } catch (NumberFormatException e) {
                    quantity = 1;
                }
                ZebraPrinter printer=null;

                try {
                    printer = ZebraPrinterFactory.getInstance(connection);
                }catch (Exception e){
                    Utils.Mensaje("usb 1:"+e.getMessage(), R.layout.item_customtoasterror,mainActivity);
                }

                if(printer!=null&&Memoria){
                    printer.printStoredFormat(Etiqueta, vars);
                }else{
                    if(printer!=null){
                        printer.sendCommand(Etiqueta);
                    }else{
                        Utils.Mensaje("usb 4: printer null", R.layout.item_customtoasterror,mainActivity);
                    }

                }
                    //  printer.printStoredFormat("", vars);



            } catch (ConnectionException | ZebraPrinterLanguageUnknownException e) {
                Utils.Mensaje("usb 2:"+e.getMessage(), R.layout.item_customtoasterror,mainActivity);
            } finally {
                try {
                    connection.close();
                } catch (ConnectionException e) {
                    Utils.Mensaje("usb 3:"+e.getMessage(), R.layout.item_customtoasterror,mainActivity);
                }
            }
        }else{
            Utils.Mensaje("usb 5: connection null", R.layout.item_customtoasterror,mainActivity);
        }

    }


}
