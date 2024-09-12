package com.jws.jwsapi.general.label;

import com.jws.jwsapi.general.printer.clases.Printer;
import com.jws.jwsapi.general.printer.clases.PrinterObject;
import com.jws.jwsapi.general.data.local.PreferencesManager;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class LabelManager {

    PreferencesManager preferencesManager;
    @Inject
    public LabelManager(PreferencesManager preferencesManager){
        this.preferencesManager=preferencesManager;
        initPrint();
    }

    public List<String> nombreEtiquetas=new ArrayList<>();
    public PrinterObject olote = new PrinterObject();
    public PrinterObject ovenci = new PrinterObject();
    public PrinterObject oturno = new PrinterObject();
    public PrinterObject ocampo1 = new PrinterObject();
    public PrinterObject ocampo2 = new PrinterObject();
    public PrinterObject ocampo3 = new PrinterObject();
    public PrinterObject ocampo4 = new PrinterObject();
    public PrinterObject ocampo5 = new PrinterObject();
    public PrinterObject oreceta = new PrinterObject();
    public PrinterObject ocodigoreceta = new PrinterObject();
    public PrinterObject oingredientes = new PrinterObject();
    public PrinterObject ocodigoingrediente = new PrinterObject();
    public PrinterObject okilos = new PrinterObject();
    public PrinterObject okilosreales = new PrinterObject();
    public PrinterObject onetototal = new PrinterObject();
    public PrinterObject opaso = new PrinterObject();
    public PrinterObject oidreceta = new PrinterObject();
    public PrinterObject oidpesada = new PrinterObject();
    public PrinterObject opaso1=new PrinterObject();
    public PrinterObject opaso2=new PrinterObject();
    public PrinterObject opaso3=new PrinterObject();
    public PrinterObject opaso4=new PrinterObject();
    public PrinterObject opaso5=new PrinterObject();
    public PrinterObject oingrediente1=new PrinterObject();
    public PrinterObject oingrediente2=new PrinterObject();
    public PrinterObject oingrediente3=new PrinterObject();
    public PrinterObject oingrediente4=new PrinterObject();
    public PrinterObject oingrediente5=new PrinterObject();
    public PrinterObject ocodigoingrediente1=new PrinterObject();
    public PrinterObject ocodigoingrediente2=new PrinterObject();
    public PrinterObject ocodigoingrediente3=new PrinterObject();
    public PrinterObject ocodigoingrediente4=new PrinterObject();
    public PrinterObject ocodigoingrediente5=new PrinterObject();
    public PrinterObject opeso1=new PrinterObject();
    public PrinterObject opeso2=new PrinterObject();
    public PrinterObject opeso3=new PrinterObject();
    public PrinterObject opeso4=new PrinterObject();
    public PrinterObject opeso5=new PrinterObject();
    public PrinterObject onumeroetiqueta=new PrinterObject();
    public List<Printer> variablesImprimibles;
    public List<String> imprimiblesPredefinidas;

    public void initPrint() {
        olote.value = preferencesManager.getLote();
        ovenci.value = preferencesManager.getVencimiento();
        oturno.value = "";
        ocampo1.value = preferencesManager.getCampo1Valor();
        ocampo2.value = preferencesManager.getCampo2Valor();
        ocampo3.value = preferencesManager.getCampo3Valor();
        ocampo4.value = preferencesManager.getCampo4Valor();
        ocampo5.value = preferencesManager.getCampo5Valor();
        oreceta.value =  "";
        ocodigoreceta.value =  "";
        oingredientes.value =  "";
        ocodigoingrediente.value =  "";
        okilos.value =  "";
        okilosreales.value =  "";
        onetototal.value = preferencesManager.getNetototal();
        opaso.value = "";
        oidreceta.value =  "";
        oidpesada.value =  "";
        opaso1.value="";
        opaso2.value="";
        opaso3.value="";
        opaso4.value="";
        opaso5.value="";
        oingrediente1.value="";
        oingrediente2.value="";
        oingrediente3.value="";
        oingrediente4.value="";
        oingrediente5.value="";
        ocodigoingrediente1.value="";
        ocodigoingrediente2.value="";
        ocodigoingrediente3.value="";
        ocodigoingrediente4.value="";
        ocodigoingrediente5.value="";
        opeso1.value="";
        opeso2.value="";
        opeso3.value="";
        opeso4.value="";
        opeso5.value="";
        onumeroetiqueta.value="";

        imprimiblesPredefinidas = new ArrayList<>();
        imprimiblesPredefinidas.add("");
        imprimiblesPredefinidas.add("Bruto");//w0001
        imprimiblesPredefinidas.add("Tara");//w0002
        imprimiblesPredefinidas.add("Neto");//w0003
        imprimiblesPredefinidas.add("Operador");//w0004
        imprimiblesPredefinidas.add("Fecha");//w0005
        imprimiblesPredefinidas.add("Hora");//w0006
        imprimiblesPredefinidas.add("Ingresar texto (fijo)"); //si agregamos nuevas mantener estas ultimas dos ultimas
        imprimiblesPredefinidas.add("Concatenar datos");//si agregamos nuevas mantener estas ultimas dos ultimas

        nombreEtiquetas= new ArrayList<>();
        nombreEtiquetas.add("PASO DE RECETA");
        nombreEtiquetas.add("RECETA FINALIZADA");
        nombreEtiquetas.add("BOLSA DE PEDIDO");
        nombreEtiquetas.add("INGREDIENTE");

        variablesImprimibles = new ArrayList<>();
        variablesImprimibles.add(new Printer("x0001",olote, "Lote", variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0002",ovenci, "Vencimiento", variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0003",oturno, "Turno", variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0004",oreceta,"Receta",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0005",ocodigoreceta,"Codigo receta",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0006",oingredientes,"Ingrediente",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0007",ocodigoingrediente,"Codigo ingrediente",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0008",okilos,"Kilos",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0009",okilosreales,"Kilos reales",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0010",onetototal,"Neto total",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0011",opaso,"Numero paso",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0012",oidreceta,"Id receta",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0013",oidpesada,"Id pesada",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0014",opaso1,"N° paso 1",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0015",opaso2,"N° paso 2",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0016",opaso3,"N° paso 3",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0017",opaso4,"N° paso 4",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0018",opaso5,"N° paso 5",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0019",oingrediente1,"Ingrediente 1",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0020",oingrediente2,"Ingrediente 2",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0021",oingrediente3,"Ingrediente 3",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0022",oingrediente4,"Ingrediente 4",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0023",oingrediente5,"Ingrediente 5",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0024",ocodigoingrediente1,"Codigo ingrediente 1",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0025",ocodigoingrediente2,"Codigo ingrediente 2",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0026",ocodigoingrediente3,"Codigo ingrediente 3",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0027",ocodigoingrediente4,"Codigo ingrediente 4",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0028",ocodigoingrediente5,"Codigo ingrediente 5",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0029",opeso1,"Peso 1",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0030",opeso2,"Peso 2",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0031",opeso3,"Peso 3",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0032",opeso4,"Peso 4",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0033",opeso5,"Peso 5",variablesImprimibles.size()));
        variablesImprimibles.add(new Printer("x0034",onumeroetiqueta,"N° etiqueta",variablesImprimibles.size()));

        if(!preferencesManager.getCampo1().isEmpty()){
            variablesImprimibles.add(new Printer("x0035",ocampo1, preferencesManager.getCampo1(), variablesImprimibles.size()));
        }else{
            variablesImprimibles.add(new Printer("x0035",ocampo1, "Campo 1", variablesImprimibles.size()));
        }

        if(!preferencesManager.getCampo2().isEmpty()){
            variablesImprimibles.add(new Printer("x0036",ocampo2, preferencesManager.getCampo2(), variablesImprimibles.size()));
        }else{
            variablesImprimibles.add(new Printer("x0036",ocampo2, "Campo 2", variablesImprimibles.size()));
        }

        if(!preferencesManager.getCampo3().isEmpty()){
            variablesImprimibles.add(new Printer("x0037",ocampo3, preferencesManager.getCampo3(), variablesImprimibles.size()));
        }else{
            variablesImprimibles.add(new Printer("x0037",ocampo3, "Campo 3", variablesImprimibles.size()));
        }

        if(!preferencesManager.getCampo4().isEmpty()){
            variablesImprimibles.add(new Printer("x0038",ocampo4, preferencesManager.getCampo4(), variablesImprimibles.size()));
        }else{
            variablesImprimibles.add(new Printer("x0038",ocampo4, "Campo 4", variablesImprimibles.size()));
        }

        if(!preferencesManager.getCampo5().isEmpty()){
            variablesImprimibles.add(new Printer("x0039",ocampo5, preferencesManager.getCampo5(), variablesImprimibles.size()));
        }else{
            variablesImprimibles.add(new Printer("x0039",ocampo5, "Campo 5", variablesImprimibles.size()));
        }
    }

    public void setupVariablesEtiqueta(String[] pasos, String[] ingredientes, String[] codingredientes, String[] kilos, int netiqueta) {
        opaso1.value = pasos[0];
        opaso2.value = pasos[1];
        opaso3.value = pasos[2];
        opaso4.value = pasos[3];
        opaso5.value = pasos[4];
        oingrediente1.value = ingredientes[0];
        oingrediente2.value = ingredientes[1];
        oingrediente3.value = ingredientes[2];
        oingrediente4.value = ingredientes[3];
        oingrediente5.value = ingredientes[4];
        ocodigoingrediente1.value = codingredientes[0];
        ocodigoingrediente2.value = codingredientes[1];
        ocodigoingrediente3.value = codingredientes[2];
        ocodigoingrediente4.value = codingredientes[3];
        ocodigoingrediente5.value = codingredientes[4];
        opeso1.value = kilos[0];
        opeso2.value = kilos[1];
        opeso3.value = kilos[2];
        opeso4.value = kilos[3];
        opeso5.value = kilos[4];
        onumeroetiqueta.value = String.valueOf(netiqueta);

    }

}
