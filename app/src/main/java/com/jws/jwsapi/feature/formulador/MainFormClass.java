/**
 * /////////////////////////////////////////////////////////////////////////////////
 * /////////////////////////////480_FORM 1.00///////////////////////////////////
 * /////////////////////////////////////////////////////////////////////////////////
 * <p>
 * ---------------------------------------------------------------------------------
 * ----Release version 1.00 --------------------------------------------------------
 * ---------------------------------------------------------------------------------
 * <p>
 * <p>
 * /********************************************************************************
 * ################################################################################
 * Puerto A(232)(/dev/ttyXRUSB0)= OPTIMA
 * Puerto B(232)(/dev/ttyXRUSB1)= -
 * Puerto C(485)(/dev/ttyXRUSB2)= -
 * Puerto D(USB)= DISPONIBLE PARA IMPRESORA
 * --------------------------------------------------------------------------------
 * ################################################################################
 * --------------------------------------------------------------------------------
 * Key store path: Navega y selecciona my-release-key.jks.
 * Key store password: myP12Password
 * Key alias: my-key-alias
 * Key password: myP12Password
 *******************************************************************************/

////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////

package com.jws.jwsapi.feature.formulador;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.jws.jwsapi.base.ui.activities.MainActivity;
import com.jws.jwsapi.common.impresora.clases.Printer;
import com.jws.jwsapi.common.impresora.clases.PrinterObject;
import com.jws.jwsapi.common.users.UsersManager;
import com.jws.jwsapi.feature.formulador.data.preferences.PreferencesManager;
import com.service.Balanzas.BalanzaService;
import com.jws.jwsapi.common.impresora.ImprimirEstandar;
import com.service.Comunicacion.OnFragmentChangeListener;
import com.jws.jwsapi.base.containers.ContainerFragment;
import com.jws.jwsapi.base.containers.ContainerPrincipalFragment;
import com.jws.jwsapi.feature.formulador.ui.fragment.Form_Principal;
import com.jws.jwsapi.feature.formulador.models.Form_Model_Ingredientes;
import com.jws.jwsapi.feature.formulador.models.Form_Model_PesadasDB;
import com.jws.jwsapi.feature.formulador.models.Form_Model_Receta;
import com.jws.jwsapi.feature.formulador.models.Form_Model_RecetaDB;
import com.jws.jwsapi.feature.formulador.data.sql.Form_SQL_db;
import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.Utils;
import com.opencsv.CSVReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import au.com.bytecode.opencsv.CSVWriter;

public class MainFormClass implements OnFragmentChangeListener {

    private final Context context;
    private MainActivity mainActivity;
    public static String DB_NAME = "Frm_DB";
    public static int db_version = 4;
    public BalanzaService BZA;
    public int N_BZA=1;
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
    UsersManager usersManager;

    Boolean permitirClic=true;
    public PreferencesManager preferencesManager;

    public MainFormClass(Context context, MainActivity activity,UsersManager usersManager) { //constructor
        this.context = context;
        this.mainActivity = activity;
        this.usersManager = usersManager;
    }

    public void init() {
        BZA= new BalanzaService(mainActivity,this);
        BZA.init();
        preferencesManager=new PreferencesManager(mainActivity);
        initPrint();
        openFragmentPrincipal();
    }


    private void initPrint() {
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
        onetototal.value = preferencesManager.getNetototal()+BZA.getUnidad(N_BZA);
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

        if(!Objects.equals(preferencesManager.getCampo1(), "") ||!Objects.equals(preferencesManager.getCampo1(), " ") ){
            variablesImprimibles.add(new Printer("x0035",ocampo1, preferencesManager.getCampo1(), variablesImprimibles.size()));
        }else{
            variablesImprimibles.add(new Printer("x0035",ocampo1, "Campo 1", variablesImprimibles.size()));
        }

        if(!Objects.equals(preferencesManager.getCampo2(), "") ||!Objects.equals(preferencesManager.getCampo2(), " ") ){
            variablesImprimibles.add(new Printer("x0036",ocampo2, preferencesManager.getCampo2(), variablesImprimibles.size()));
        }else{
            variablesImprimibles.add(new Printer("x0036",ocampo2, "Campo 2", variablesImprimibles.size()));
        }

        if(!Objects.equals(preferencesManager.getCampo3(), "") ||!Objects.equals(preferencesManager.getCampo3(), " ") ){
            variablesImprimibles.add(new Printer("x0037",ocampo3, preferencesManager.getCampo3(), variablesImprimibles.size()));
        }else{
            variablesImprimibles.add(new Printer("x0037",ocampo3, "Campo 3", variablesImprimibles.size()));
        }

        if(!Objects.equals(preferencesManager.getCampo4(), "") ||!Objects.equals(preferencesManager.getCampo4(), " ") ){
            variablesImprimibles.add(new Printer("x0038",ocampo4, preferencesManager.getCampo4(), variablesImprimibles.size()));
        }else{
            variablesImprimibles.add(new Printer("x0038",ocampo4, "Campo 4", variablesImprimibles.size()));
        }

        if(!Objects.equals(preferencesManager.getCampo5(), "") ||!Objects.equals(preferencesManager.getCampo5(), " ") ){
            variablesImprimibles.add(new Printer("x0039",ocampo5, preferencesManager.getCampo5(), variablesImprimibles.size()));
        }else{
            variablesImprimibles.add(new Printer("x0039",ocampo5, "Campo 5", variablesImprimibles.size()));
        }
    }


    public void Imprimir(int etiqueta) {
        ImprimirEstandar imprimirEstandar = new ImprimirEstandar(context, mainActivity,usersManager);
        imprimirEstandar.EnviarEtiqueta(BZA.serialPortB,etiqueta);

    }

    public void ImprimirUltima() {
        ImprimirEstandar imprimirEstandar = new ImprimirEstandar(context, mainActivity,usersManager);
        imprimirEstandar.EnviarUltimaEtiqueta(BZA.serialPortB);

    }

    public void openFragmentPrincipal() {
        Fragment fragment = new Form_Principal();
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        Fragment fragmentoActual = new ContainerPrincipalFragment();
        ContainerPrincipalFragment containerFragment = ContainerPrincipalFragment.newInstance(fragment.getClass());
        containerFragment.setFragmentActual(fragmentoActual);
        fragmentManager.beginTransaction()
                .replace(R.id.container_fragment, containerFragment)
                .commit();
    }

    public void openFragment(Fragment fragment) {
        FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        Fragment fragmentoActual = new ContainerFragment();

        ContainerFragment containerFragment = ContainerFragment.newInstance(fragment.getClass());
        containerFragment.setFragmentActual(fragmentoActual);
        fragmentManager.beginTransaction()
                .replace(R.id.container_fragment, containerFragment)
                .commit();
    }

    public void openFragmentService(Fragment fragment, Bundle arg) {
        if(permitirClic){
            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            Fragment fragmentoActual = new ContainerFragment();
            boolean programador= usersManager.getNivelUsuario() > 3;
            ContainerFragment containerFragment = ContainerFragment.newInstanceService(fragment.getClass(),arg,programador);
            containerFragment.setFragmentActual(fragmentoActual);
            fragmentManager.beginTransaction()
                    .replace(R.id.container_fragment, containerFragment)
                    .commit();
            permitirClic = false;
            Handler handler= new Handler();
            handler.postDelayed(() -> permitirClic = true, 1000); //arreglar problema de que mas de una optima llame a service al mismo tiempo
        }

    }

    public String devuelveTurnoActual(){
        String hora=Utils.getHora();
        String turno="0";
        if(hora.length()>4){
            String []arr=hora.split(":");
            if(arr.length>2){
                String horanum=arr[0].replace(":","");
                if(Utils.isNumeric(horanum)){
                    float horaActual = Float.parseFloat(horanum);
                    float turno1 = preferencesManager.getTurno1();
                    float turno2 = preferencesManager.getTurno2();
                    float turno3 = preferencesManager.getTurno3();
                    float turno4 = preferencesManager.getTurno4();

                    if (turno4 > 0) { // Verifica si el turno 4 esta habilitado
                        if ((horaActual >= turno1) && (horaActual < turno2)) {
                            turno = "TURNO 1";
                        } else if ((horaActual >= turno2) && (horaActual < turno3)) {
                            turno = "TURNO 2";
                        } else if ((horaActual >= turno3) && (horaActual < turno4)) {
                            turno = "TURNO 3";
                        } else {
                            turno = "TURNO 4";
                        }
                    } else { // Si el turno 4 esta deshabilitado
                        if ((horaActual >= turno1) && (horaActual < turno2)) {
                            turno = "TURNO 1";
                        } else if ((horaActual >= turno2) && (horaActual < turno3)) {
                            turno = "TURNO 2";
                        } else {
                            turno = "TURNO 3";
                        }
                    }
                }
            }

        }
        return turno;
    }

    ////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////Getters ///////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    public List<Form_Model_Ingredientes> getIngredientes() {
        List<Form_Model_Ingredientes> cod = new ArrayList<>();
        try {
            String filePath = Environment.getExternalStorageDirectory() + "/Memoria/Ingredientes.csv";
            File filess = new File(filePath);

            Boolean error = false;
            if (filess.exists()) {
                try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
                    String[] nextLine;
                    while ((nextLine = reader.readNext()) != null) {
                        if (nextLine.length > 1) {
                            String pos0 = nextLine[0].replace("\"", "");
                            String pos1 = nextLine[1].replace("\"", "");
                            cod.add(new Form_Model_Ingredientes(pos0, pos1));
                        } else {
                            error = true;
                        }
                    }
                    if (error) {
                        Utils.Mensaje("Error en el archivo, verifique que todos los datos son correctos", R.layout.item_customtoasterror,mainActivity);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Utils.Mensaje("Error no se encuentran los ingredientes", R.layout.item_customtoasterror,mainActivity);
            }
        } catch (Exception e) {
            // Manejar la excepción de manera apropiada (por ejemplo, imprimir un mensaje de registro)
            e.printStackTrace();
        }
        return cod;
    }

    public List<Form_Model_Receta> getReceta(String receta) {
        List<Form_Model_Receta> listreceta = new ArrayList<>();
        String[] arr = receta.split("_");
        float total = 0f;
        try {
            String filePath = Environment.getExternalStorageDirectory() + "/Memoria/" + receta + ".csv";
            File filess = new File(filePath);
            Boolean error = false;
            if (filess.exists()) {
                try (CSVReader reader = new CSVReader(new FileReader(filePath))) {
                    String[] nextLine;
                    while ((nextLine = reader.readNext()) != null) {
                        if (nextLine.length > 2) {
                            String codigo =nextLine[0].replace("\"", "");
                            String ingrediente =nextLine[1].replace("\"", "");
                            String kilos = nextLine[2].replace("\"", "");
                            if (codigo != null && ingrediente != null && kilos != null && arr.length == 3 && Utils.isNumeric(kilos)) {

                                listreceta.add(new Form_Model_Receta(arr[1].replace("_", ""), arr[2].replace("_", ""),
                                        "0", codigo, ingrediente, kilos, "NO", ""));
                                if (Utils.isNumeric(kilos)) {
                                    total = total + Float.parseFloat(kilos);
                                }

                            } else {
                                error = true;
                            }
                        } else {
                            error = true;
                        }
                    }
                    for (int i = 0; i < listreceta.size(); i++) {
                        listreceta.get(i).setKilos_totales(String.valueOf(total));
                    }
                    if (error) {
                        Utils.Mensaje("Error en el archivo, verifique que todos los datos son correctos", R.layout.item_customtoasterror,mainActivity);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Utils.Mensaje("Error no se encuentra el archivo", R.layout.item_customtoasterror,mainActivity);
            }
        } catch (Exception e) {
            // Manejar la excepción de manera apropiada (por ejemplo, imprimir un mensaje de registro)
            e.printStackTrace();
        }
        return listreceta;

    }

    public void setReceta(String receta, List<Form_Model_Receta> lista) throws IOException {
        Runnable myRunnable = () -> {
            File filePath = new File(Environment.getExternalStorageDirectory() + "/Memoria/" + receta + ".csv");
            // Si el archivo no existe, crearlo
            if (!filePath.exists()) {
                try {
                    filePath.createNewFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            filePath.delete();

            try {
                filePath.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            char separador = ',';
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(filePath.getAbsolutePath(), true), separador);
                for (int i = 0; i < lista.size(); i++) {
                    writer.writeNext(new String[]{lista.get(i).getCodigo_ing(), lista.get(i).getDescrip_ing(), lista.get(i).getKilos_ing()});
                }
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
        Thread myThread = new Thread(myRunnable);
        myThread.start();

    }

    public void setIngredientes(List<Form_Model_Ingredientes> lista) throws IOException {
        Runnable myRunnable = () -> {
            File filePath = new File(Environment.getExternalStorageDirectory() + "/Memoria/Ingredientes.csv");
            if (filePath.exists()) {
                filePath.delete();
            }
            try {
                Boolean bool = filePath.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            char separador = ',';
            try {
                CSVWriter writer = new CSVWriter(new FileWriter(filePath.getAbsolutePath(), true), separador);
                for (int i = 0; i < lista.size(); i++) {
                    writer.writeNext(new String[]{lista.get(i).getCodigo(), lista.get(i).getNombre()});
                }
                writer.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };

        Thread myThread = new Thread(myRunnable);
        myThread.start();


    }

    ////////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////Pagina web ////////////////////////////////
    ////////////////////////////////////////////////////////////////////////////////

    public String JSONconsultas() throws JSONException {
        JSONArray jsonArray = new JSONArray();

        try {

            JSONObject PESADAS = new JSONObject();
            PESADAS.put("GET", "GetPesadas");
            PESADAS.put("Nombre", "PESADAS");

            JSONObject RECETAS = new JSONObject();
            RECETAS.put("GET", "GetRecetas");
            RECETAS.put("Nombre", "RECETAS");

            JSONObject INGREDIENTES = new JSONObject();
            INGREDIENTES.put("GET", "GetPedidos");
            INGREDIENTES.put("Nombre", "PEDIDOS");


            jsonArray.put(PESADAS);
            jsonArray.put(RECETAS);
            jsonArray.put(INGREDIENTES);

        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return jsonArray.toString();
    }

    public String JSONpesadas(Map<String, List<String>> filtros, String columnaEspecifica) throws JSONException {
        List<Form_Model_PesadasDB> guardado;
        try (Form_SQL_db guardadosSQL = new Form_SQL_db(context, MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
            guardado = guardadosSQL.getPesadasSQL(filtros);
        }

        Set<Object> valoresUnicos = new HashSet<>();
        JSONArray jsonArray = new JSONArray();

        try {
            for (int i = 0; i < guardado.size(); i++) {
                JSONObject Pesada = new JSONObject();
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[0], String.valueOf(guardado.get(i).getId()));
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[1], guardado.get(i).getIdReceta());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[2], String.valueOf(guardado.get(i).getIdPedido()));
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[3], guardado.get(i).getCodigoReceta());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[4], guardado.get(i).getDescripcionReceta());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[5], guardado.get(i).getCodigoIngrediente());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[6], guardado.get(i).getDescripcionIngrediente());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[7], guardado.get(i).getLote());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[8], guardado.get(i).getVencimiento());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[9], guardado.get(i).getTurno());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[10], guardado.get(i).getNeto());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[25], guardado.get(i).getOperador());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[26], guardado.get(i).getSetPoint());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[27], guardado.get(i).getReales());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[11], guardado.get(i).getBruto());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[12], guardado.get(i).getTara());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[13], guardado.get(i).getFecha());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[14], guardado.get(i).getHora());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[15], guardado.get(i).getCampo1());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[16], guardado.get(i).getCampo2());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[17], guardado.get(i).getCampo3());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[18], guardado.get(i).getCampo4());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[19], guardado.get(i).getCampo5());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[20], guardado.get(i).getCampo1Valor());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[21], guardado.get(i).getCampo2Valor());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[22], guardado.get(i).getCampo3Valor());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[23], guardado.get(i).getCampo4Valor());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[24], guardado.get(i).getCampo5Valor());
                Pesada.put(Form_SQL_db.COLUMN_PESADAS_DES[30], guardado.get(i).getBalanza());

                if (columnaEspecifica != null && !columnaEspecifica.isEmpty()) {
                    Object valorColumna = Pesada.get(columnaEspecifica);
                    if (valoresUnicos.add(valorColumna)) {
                        jsonArray.put(valorColumna);
                    }
                }else {
                    jsonArray.put(Pesada);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray.toString();
    }
    public String JSONrecetas(Map<String, List<String>> filtros, String columnaEspecifica) throws JSONException {
        List<Form_Model_RecetaDB> guardado;
        // Supongamos que tienes un método para obtener los datos desde la base de datos
        try (Form_SQL_db guardadosSQL = new Form_SQL_db(context, MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
            guardado = guardadosSQL.getRecetasSQL(filtros);
        }
        Set<Object> valoresUnicos = new HashSet<>();
        JSONArray jsonArray = new JSONArray();

        try {
            for (Form_Model_RecetaDB receta : guardado) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[0], String.valueOf(receta.getId()));
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[1], receta.getCodigoReceta());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[2], receta.getDescripcionReceta());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[3], receta.getLote());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[4], receta.getVencimiento());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[5], receta.getTurno());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[6], receta.getNeto());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[22], receta.getKilosAProducir());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[21], receta.getOperador());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[7], receta.getBruto());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[8], receta.getTara());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[9], receta.getFecha());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[10], receta.getHora());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[11], receta.getCampo1());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[12], receta.getCampo2());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[13], receta.getCampo3());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[14], receta.getCampo4());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[15], receta.getCampo5());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[16], receta.getCampo1Valor());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[17], receta.getCampo2Valor());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[18], receta.getCampo3Valor());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[19], receta.getCampo4Valor());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[20], receta.getCampo5Valor());

               // jsonObject.put("CampoExtra1", receta.campoExtra1);
               // jsonObject.put("CampoExtra2", receta.campoExtra2);
                if (columnaEspecifica != null && !columnaEspecifica.isEmpty()) {
                    Object valorColumna = jsonObject.get(columnaEspecifica);
                    if (valoresUnicos.add(valorColumna)) {
                        jsonArray.put(valorColumna);
                    }
                }else {
                    jsonArray.put(jsonObject);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray.toString();
    }

    public String JSONpedidos(Map<String, List<String>> filtros, String columnaEspecifica) throws JSONException {
        List<Form_Model_RecetaDB> guardado = new ArrayList<>();
        // Supongamos que tienes un método para obtener los datos desde la base de datos
        try (Form_SQL_db guardadosSQL = new Form_SQL_db(context, MainFormClass.DB_NAME, null, MainFormClass.db_version)) {
            guardado = guardadosSQL.getPedidosSQL(filtros);
        }
        Set<Object> valoresUnicos = new HashSet<>();
        JSONArray jsonArray = new JSONArray();

        try {
            for (Form_Model_RecetaDB receta : guardado) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[0], String.valueOf(receta.getId()));
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[1], receta.getCodigoReceta());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[2], receta.getDescripcionReceta());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[3], receta.getLote());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[4], receta.getVencimiento());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[5], receta.getTurno());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[6], receta.getNeto());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[22], receta.getKilosAProducir());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[21], receta.getOperador());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[7], receta.getBruto());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[8], receta.getTara());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[9], receta.getFecha());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[10], receta.getHora());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[11], receta.getCampo1());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[12], receta.getCampo2());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[13], receta.getCampo3());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[14], receta.getCampo4());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[15], receta.getCampo5());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[16], receta.getCampo1Valor());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[17], receta.getCampo2Valor());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[18], receta.getCampo3Valor());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[19], receta.getCampo4Valor());
                jsonObject.put(Form_SQL_db.COLUMN_RECETAS_DES[20], receta.getCampo5Valor());

                if (columnaEspecifica != null && !columnaEspecifica.isEmpty()) {
                    Object valorColumna = jsonObject.get(columnaEspecifica);
                    if (valoresUnicos.add(valorColumna)) {
                        jsonArray.put(valorColumna);
                    }
                }else {
                    jsonArray.put(jsonObject);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray.toString();
    }


}
