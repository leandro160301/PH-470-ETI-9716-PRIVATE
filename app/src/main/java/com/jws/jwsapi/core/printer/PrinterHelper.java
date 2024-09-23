package com.jws.jwsapi.core.printer;

import static com.jws.jwsapi.core.storage.Storage.openAndReadFile;

import androidx.annotation.NonNull;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.core.label.LabelManager;
import com.jws.jwsapi.core.label.LabelModel;
import com.jws.jwsapi.core.user.UserManager;
import com.jws.jwsapi.utils.ToastHelper;
import com.jws.jwsapi.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class PrinterHelper {
    private final MainActivity mainActivity;
    private final PrinterPreferences printerPreferences;
    private final LabelManager labelManager;
    private final UserManager userManager;

    public PrinterHelper(MainActivity mainActivity, PrinterPreferences printerPreferences, LabelManager labelManager, UserManager userManager) {
        this.mainActivity = mainActivity;
        this.printerPreferences = printerPreferences;
        this.labelManager = labelManager;
        this.userManager = userManager;
    }

    public String getLabelCode(int numetiqueta){
        try {
            String currentLabel= printerPreferences.getLabel(numetiqueta);
            String labelCode =openAndReadFile(currentLabel,mainActivity);

            if(!isValidLabel(currentLabel, labelCode))return "";

            List<Integer> ListElementsInt= printerPreferences.getListSpinner(currentLabel);
            List<String> ListElementsFijo= printerPreferences.getListFijo(currentLabel);

            if(ListElementsInt==null&&ListElementsFijo==null){
                return showErrorMessage("Error, la etiqueta no esta configurada");
            }
            String[] arr = labelCode.split("\\^FN");
            assert ListElementsInt != null;
            if(areElementsMatching(ListElementsInt, ListElementsFijo, arr)) {
                return showErrorMessage("Error, faltan campos por configurar");
            }
            List<String> ListElementsFinales = getFinalElements(currentLabel, ListElementsInt, ListElementsFijo, arr);
            if(ListElementsFinales.size()== arr.length-1){
                String etique= replaceLabelFields(ListElementsFinales,labelCode).replace("Ñ","\\A5").replace("ñ","\\A4").replace("á","\\A0").replace("é","\\82").replace("í","\\A1").replace("ó","\\A2").replace("Á","\\B5").replace("É","\\90").replace("Í","\\D6").replace("Ó","\\E3") ;
                printerPreferences.setLastLabel(etique);
                return etique;
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return showErrorMessage("Ocurrió un error al procesar la etiqueta:" + e.getMessage());
        }
    }

    @NonNull
    private List<String> getFinalElements(String currentLabel, List<Integer> ListElementsInt, List<String> ListElementsFijo, String[] arr) {
        List<String>ListElementsFinales=new ArrayList<>();
        for(int i = 0; i< arr.length-1; i++){
            String []arr2= arr[i+1].split("\\^FS");
            if(arr2.length>1){
                if(ListElementsInt.get(i)<labelManager.constantPrinterList.size()){
                    String finalElement = getFinalElementValue(currentLabel, ListElementsInt, ListElementsFijo, i);
                    if(finalElement!=null)ListElementsFinales.add(finalElement);
                }else if(ListElementsInt.get(i)<labelManager.varPrinterList.size()+labelManager.constantPrinterList.size()){
                    for(int j = 0; j<labelManager.varPrinterList.size(); j++){
                        if(ListElementsInt.get(i)-labelManager.constantPrinterList.size()==j){
                            ListElementsFinales.add(labelManager.varPrinterList.get(j).value());
                        }
                    }
                }
            }

        }
        return ListElementsFinales;
    }

    private String getFinalElementValue(String currentLabel, List<Integer> ListElementsInt, List<String> ListElementsFijo, int i) {
        switch (ListElementsInt.get(i)){
            case 0: return "";
            case 1: return mainActivity.mainClass.bza.getBrutoStr(mainActivity.mainClass.nBza)+mainActivity.mainClass.bza.getUnidad(mainActivity.mainClass.nBza);
            case 2: return mainActivity.mainClass.bza.getTaraDigital(mainActivity.mainClass.nBza)+mainActivity.mainClass.bza.getUnidad(mainActivity.mainClass.nBza);
            case 3: return mainActivity.mainClass.bza.getNetoStr(mainActivity.mainClass.nBza)+mainActivity.mainClass.bza.getUnidad(mainActivity.mainClass.nBza);
            case 4: return userManager.getCurrentUser();
            case 5: return Utils.getFecha();
            case 6: return Utils.getHora();
            default:
                if(ListElementsInt.get(i)==labelManager.constantPrinterList.size()-1){
                    return getConcatenatedValue(currentLabel, i);
                }
                if(ListElementsInt.get(i)==labelManager.constantPrinterList.size()-2){
                    return ListElementsFijo.get(i);
                }
            break;
        }
        return null;
    }

    @NonNull
    private String showErrorMessage(String texto) {
        ToastHelper.message(texto, R.layout.item_customtoasterror,mainActivity);
        return "";
    }

    @NonNull
    private String getConcatenatedValue(String currentLabel, int i) {
        List<Integer> listElementsConcat = printerPreferences.getListConcat(currentLabel, i);
        String separador = printerPreferences.getSeparator(currentLabel, i);
        StringBuilder concat = new StringBuilder();

        if (listElementsConcat != null) {
            int constSize = labelManager.constantPrinterList.size();
            int varSize = labelManager.varPrinterList.size();
            int totalSize = constSize + varSize;

            for (Integer concatValue : listElementsConcat) {
                if (concatValue < totalSize) {
                    if (concatValue < constSize) {
                        concat.append(getConcatValue(concat.toString(), concatValue)).append(separador);
                    } else {
                        int varIndex = concatValue - constSize;
                        concat.append(labelManager.varPrinterList.get(varIndex).value()).append(separador);
                    }
                }
            }
        }
        return deleteLastSeparator(separador, concat.toString());
    }

    private String getConcatValue(String concat, Integer concatValue) {
        switch (concatValue){
            case 0:return "";
            case 1:return mainActivity.mainClass.bza.getBrutoStr(mainActivity.mainClass.nBza);
            case 2:return mainActivity.mainClass.bza.getTaraDigital(mainActivity.mainClass.nBza);
            case 3:return mainActivity.mainClass.bza.getNetoStr(mainActivity.mainClass.nBza);
            case 4:return userManager.getCurrentUser();
            case 5:return Utils.getFecha();
            case 6:return Utils.getHora();
        }
        return concat;
    }

    @NonNull
    private static String deleteLastSeparator(String separador, String concat) {
        StringBuilder stringBuilder = new StringBuilder(concat);
        int lastCommaIndex = stringBuilder.lastIndexOf(separador);
        if (lastCommaIndex >= 0) {
            stringBuilder.deleteCharAt(lastCommaIndex);
        }
        return stringBuilder.toString();
    }

    private static boolean areElementsMatching(List<Integer> ListElementsInt, List<String> ListElementsFijo, String[] arr) {
        return arr.length - 1 == ListElementsInt.size() && arr.length - 1 == ListElementsFijo.size();
    }

    private static boolean isValidLabel(String currentLabel, String labelCode) {
        return labelCode != null && !labelCode.isEmpty() && !currentLabel.isEmpty();
    }

    public String replaceLabelFields(List<String> newList, String labelCode){
        try {
            String[] fnCommandsArray = labelCode.split("\\^FN");
            String nameLabelCommand = getLabelNameFromCodeCommand(labelCode);
            List<String> oldList = getOldFieldsFromCode(fnCommandsArray);
            labelCode = updateCodeWithNewList(newList, labelCode, oldList);
            labelCode=labelCode.replace("^FN","^FD").replace(nameLabelCommand,"");
            System.out.println("etiqueta:"+labelCode);
            return labelCode;
        } catch (Exception e) {
            e.printStackTrace();
            return showErrorMessage("Ocurrió un error al procesar la etiqueta:" + e.getMessage());
        }
    }

    private static String updateCodeWithNewList(List<String> newList, String labelCode, List<String> oldList) {
        if(oldList.size()== newList.size()){
            for(int i = 0; i< newList.size(); i++){
                labelCode = labelCode.replace(oldList.get(i), newList.get(i));
            }
        }
        return labelCode;
    }

    @NonNull
    private static List<String> getOldFieldsFromCode(String[] commandResult) {
        List<String> oldList=new ArrayList<>();
        for(int i = 1; i< commandResult.length; i++){
            String []oldFieldArray= commandResult[i].split("\\^FS");
            if(oldFieldArray.length>1){
                oldList.add(oldFieldArray[0]);
            }
        }
        return oldList;
    }

    @NonNull
    private static String getLabelNameFromCodeCommand(String labelCode) {
        String[] dfeCommandList = labelCode.split("\\^DFE");
        String delete="";
        if(dfeCommandList.length>1){
            String []array= dfeCommandList[1].split("\\^FS");
            if(array.length>1){
                delete="^DFE"+array[0]+"^FS\n";
            }
        }
        return delete;
    }

    public static List<LabelModel> getFieldsFromLabel(String etiqueta){
        List<LabelModel> listaCampos = new ArrayList<>();
        String[] arr = etiqueta.split("\\^FN");
        for(int i=1;i<arr.length;i++){
            String []arr2= arr[i].split("\\^FS");
            if(arr2.length>1){
                System.out.println("var campo:"+arr2[0]);//este string luego debemos reemplazar por FS+valorvariable con el .replace
                String []arr3= arr2[0].split("\"");
                if(arr3.length>1){
                    listaCampos.add(new LabelModel(arr3[1],0));
                }
            }
        }
        return listaCampos;
    }

}
