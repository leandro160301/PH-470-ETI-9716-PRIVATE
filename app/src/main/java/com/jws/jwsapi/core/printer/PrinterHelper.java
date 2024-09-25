package com.jws.jwsapi.core.printer;

import static com.jws.jwsapi.core.storage.Storage.openAndReadFile;

import androidx.annotation.NonNull;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.core.label.LabelManager;
import com.jws.jwsapi.core.label.LabelModel;
import com.jws.jwsapi.core.user.UserManager;
import com.jws.jwsapi.shared.UserRepository;
import com.jws.jwsapi.utils.ToastHelper;
import com.jws.jwsapi.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class PrinterHelper {
    private final MainActivity mainActivity;
    private final PrinterPreferences printerPreferences;
    private final LabelManager labelManager;
    private final UserRepository userRepository;

    @Inject
    public PrinterHelper(MainActivity mainActivity, PrinterPreferences printerPreferences, LabelManager labelManager, UserRepository userRepository) {
        this.mainActivity = mainActivity;
        this.printerPreferences = printerPreferences;
        this.labelManager = labelManager;
        this.userRepository = userRepository;
    }

    public String getLabelCode(int numetiqueta){
        try {
            String currentLabel= printerPreferences.getLabel(numetiqueta);
            String labelCode =openAndReadFile(currentLabel,mainActivity);

            if(!isValidLabel(currentLabel, labelCode))return "";

            List<Integer> elementsInt= printerPreferences.getListSpinner(currentLabel);
            List<String> elementsFijo= printerPreferences.getListFijo(currentLabel);

            if(elementsInt==null&&elementsFijo==null){
                return showErrorMessage("Error, la etiqueta no esta configurada");
            }
            String[] arr = labelCode.split("\\^FN");
            if(!areElementsMatching(elementsInt, elementsFijo, arr)) {
                return showErrorMessage("Error, faltan campos por configurar");
            }
            List<String> finalElements = getFinalElements(currentLabel, elementsInt, elementsFijo, arr);
            if(finalElements.size()== arr.length-1){
                return labelResult(labelCode, finalElements);
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            return showErrorMessage("Ocurrió un error al procesar la etiqueta:" + e.getMessage());
        }
    }

    @NonNull
    private String labelResult(String labelCode, List<String> ListElementsFinales) {
        String labelResult= replaceLabelFields(ListElementsFinales, labelCode).replace("Ñ","\\A5").replace("ñ","\\A4").replace("á","\\A0").replace("é","\\82").replace("í","\\A1").replace("ó","\\A2").replace("Á","\\B5").replace("É","\\90").replace("Í","\\D6").replace("Ó","\\E3") ;
        printerPreferences.setLastLabel(labelResult);
        return labelResult;
    }

    @NonNull
    private List<String> getFinalElements(String currentLabel, List<Integer> elementsInt, List<String> elementsFijo, String[] arr) {
        List<String> finalElements = new ArrayList<>();
        int constantListSize = labelManager.constantPrinterList.size();
        int variableListSize = labelManager.varPrinterList.size();
        for(int i = 0; i< arr.length-1; i++){
            String []arrSplit = arr[i+1].split("\\^FS");

            if(arrSplit.length>1){
                int elementIndex = elementsInt.get(i);

                if(elementIndex < constantListSize){
                    String finalElement = getFinalElementValue(elementsInt.get(i), currentLabel, elementsFijo, i,true);
                    addIfNotNull(finalElements, finalElement);
                }else if(elementIndex < variableListSize+constantListSize){
                    int variableIndex = elementIndex-constantListSize;
                    addVariableElement(finalElements, variableIndex);
                }
            }

        }
        return finalElements;
    }

    private void addVariableElement(List<String> finalElements, int varIndex) {
        if (varIndex >= 0 && varIndex < labelManager.varPrinterList.size()) {
            finalElements.add(labelManager.varPrinterList.get(varIndex).value());
        }
    }

    private static void addIfNotNull(List<String> finalElements, String finalElement) {
        if(finalElement !=null) finalElements.add(finalElement);
    }

    private String getFinalElementValue(Integer concatValue, String currentLabel,  List<String> ListElementsFijo, Integer i, boolean isFinalElement) {
        switch (concatValue){
            case 0: return "";
            case 1: return mainActivity.mainClass.bza.getBrutoStr(mainActivity.mainClass.nBza)+mainActivity.mainClass.bza.getUnidad(mainActivity.mainClass.nBza);
            case 2: return mainActivity.mainClass.bza.getTaraDigital(mainActivity.mainClass.nBza)+mainActivity.mainClass.bza.getUnidad(mainActivity.mainClass.nBza);
            case 3: return mainActivity.mainClass.bza.getNetoStr(mainActivity.mainClass.nBza)+mainActivity.mainClass.bza.getUnidad(mainActivity.mainClass.nBza);
            case 4: return userRepository.getCurrentUser();
            case 5: return Utils.getFecha();
            case 6: return Utils.getHora();
            default:
                if(isFinalElement && concatValue==labelManager.constantPrinterList.size()-1) {
                    return getConcatenatedValue(currentLabel, i);
                }
                if(isFinalElement && concatValue==labelManager.constantPrinterList.size()-2){
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
        String separator = printerPreferences.getSeparator(currentLabel, i);
        StringBuilder concat = new StringBuilder();

        if (listElementsConcat != null) {
            int constSize = labelManager.constantPrinterList.size();
            int varSize = labelManager.varPrinterList.size();
            int totalSize = constSize + varSize;

            for (Integer concatValue : listElementsConcat) {
                if (concatValue < totalSize) {
                    if (concatValue < constSize) {
                        String concatElement = getFinalElementValue(concatValue,null,null,null,false);
                        if(concatElement!=null) {
                            concat.append(concatElement).append(separator);
                        }
                    } else {
                        int varIndex = concatValue - constSize;
                        concat.append(labelManager.varPrinterList.get(varIndex).value()).append(separator);
                    }
                }
            }
        }
        return removeLastSeparator(separator, concat.toString());
    }

    @NonNull
    public static String removeLastSeparator(String separated, String concat) {
        if (concat.endsWith(separated)) {
            return concat.substring(0, concat.length() - separated.length());
        }
        return concat;
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
            String []arrFs= dfeCommandList[1].split("\\^FS");
            if(arrFs.length>1){
                delete="^DFE"+arrFs[0]+"^FS\n";
            }
        }
        return delete;
    }

    public static List<LabelModel> getFieldsFromLabel(String label){
        List<LabelModel> fieldList = new ArrayList<>();
        String[] arrFn = label.split("\\^FN");
        for(int i=1;i<arrFn.length;i++){
            String []arrFs= arrFn[i].split("\\^FS");
            if(arrFs.length>1){
                String []arrSplit= arrFs[0].split("\"");
                if(arrSplit.length>1){
                    fieldList.add(new LabelModel(arrSplit[1],0));
                }
            }
        }
        return fieldList;
    }

}
