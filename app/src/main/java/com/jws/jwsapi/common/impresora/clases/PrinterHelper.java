package com.jws.jwsapi.common.impresora.clases;

import com.jws.jwsapi.base.models.EtiquetasModel;
import java.util.ArrayList;
import java.util.List;

public class PrinterHelper {

    public static List<EtiquetasModel> getCamposEtiqueta(String etiqueta){
        List<EtiquetasModel> listaCampos = new ArrayList<>();
        String[] arr = etiqueta.split("\\^FN");
        for(int i=1;i<arr.length;i++){
            String []arr2= arr[i].split("\\^FS");
            if(arr2.length>1){
                System.out.println("var campo:"+arr2[0]);//este string luego debemos reemplazar por FS+valorvariable con el .replace
                String []arr3= arr2[0].split("\"");
                if(arr3.length>1){
                    listaCampos.add(new EtiquetasModel(arr3[1],0));
                }
            }
        }
        return listaCampos;
    }

}
