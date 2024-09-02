package com.jws.jwsapi.common.storage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.jws.jwsapi.R;
import com.jws.jwsapi.base.ui.activities.MainActivity;
import com.jws.jwsapi.base.ui.adapters.AdapterMultimedia2;
import com.jws.jwsapi.utils.Utils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Storage {
    String FileDialog="";
    private final List<File> usbMultimediaPaths = Arrays.asList(
            new File("/storage/udisk0"),
            new File("/storage/udisk1"),
            new File("/storage/udisk2")
    );
    private final List<File> usbPaths = Arrays.asList(
            new File("/storage/udisk0"),
            new File("/storage/udisk1"),
            new File("/storage/udisk2")
    );
    private final List<File> apks = Arrays.asList(
            new File("/storage/udisk0/instalacion/jwsapi.apk"),
            new File("/storage/udisk1/instalacion/jwsapi.apk"),
            new File("/storage/udisk2/instalacion/jwsapi.apk")
    );
    File file;
    int updateUsbState =0;
    public static Handler mHandler= new Handler();
    private final AppCompatActivity activity;

    public Storage(AppCompatActivity activity){
        this.activity=activity;
    }
    public void init(){
        startUsbRead.run();
    }
    Runnable startUsbRead = new Runnable() {
        //Runnable que se encarga de leer si ingresa un pendrive usb
        @Override
        public void run() {
            verificaMemoriaUSB();
            mHandler.postDelayed(this,1000);

        }
    };

    public void verificaMemoriaUSB(){
        if (usbPaths.stream().anyMatch(File::isDirectory)) {
            for(File apk:apks){
                if(apk.exists()){
                    installApk(activity);
                }
            }
            if(usbMultimediaPaths.stream().anyMatch(File::isDirectory)&& updateUsbState ==0){
                dialogoUSB();
                updateUsbState =1;
            }
            if(usbMultimediaPaths.stream().noneMatch(File::isDirectory)&& updateUsbState ==1){
                updateUsbState =0;
            }
        }else {
            updateUsbState =0;
        }
    }
    public boolean isPackageExisted(String targetPackage){
        PackageManager pm= activity.getApplicationContext().getPackageManager();
        try {
            pm.getPackageInfo(targetPackage,PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }
    public Boolean DevuelveEstadoUSB(){
        return updateUsbState == 1;
    }

    public void installApk(AppCompatActivity activity){
        if(isPackageExisted("com.android.documentsui")){
            Intent launchIntent = activity.getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.android.documentsui");
            if (launchIntent != null) {
                activity.startActivity(launchIntent);
            }
        }
    }


    public void dialogoUSB(){
        AlertDialog.Builder DialogoUSB = new AlertDialog.Builder(activity, R.style.AlertDialogCustom);
        View mView = activity.getLayoutInflater().inflate(R.layout.dialogo_usb, null);
        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);
        Button Borrar = mView.findViewById(R.id.btborrar);
        Button bt_pdf = mView.findViewById(R.id.bt_pdf);
        Button bt_excel = mView.findViewById(R.id.bt_excel);
        Button bt_csv = mView.findViewById(R.id.bt_csv);
        Button bt_captura = mView.findViewById(R.id.bt_captura);
        RecyclerView recyclerView=mView.findViewById(R.id.listview);
        AdapterMultimedia2 adapter =seteo(".pdf",recyclerView);
        adapter.setClickListener((view, position) -> {
            String archivo = "/storage/emulated/0/Memoria/";
            FileDialog=adapter.getItem(position);
            String archivo2=archivo.concat(adapter.getItem(position));
            file = new File(archivo2);
        });
        DialogoUSB.setView(mView);
        final AlertDialog dialogusb = DialogoUSB.create();
        dialogusb.show();
        bt_pdf.setOnClickListener(view -> {
            file=null;
            AdapterMultimedia2 adapter1 =seteo(".pdf",recyclerView);
            adapter1.setClickListener((view1, position) -> {
                String archivo = "/storage/emulated/0/Memoria/";
                FileDialog= adapter1.getItem(position);
                String archivo2=archivo.concat(adapter1.getItem(position));
                file = new File(archivo2);
            });
        });
        bt_captura.setOnClickListener(view -> {
            file=null;
            AdapterMultimedia2 adapter1 =seteo(".png",recyclerView);
            adapter1.setClickListener((view1, position) -> {
                String archivo = "/storage/emulated/0/Memoria/";
                FileDialog= adapter1.getItem(position);
                String archivo2=archivo.concat(adapter1.getItem(position));
                file = new File(archivo2);
            });
        });
        bt_excel.setOnClickListener(view -> {
            file=null;
            AdapterMultimedia2 adapter12 =seteo(".xls",recyclerView);
            adapter12.setClickListener((view13, position) -> {
                String archivo = "/storage/emulated/0/Memoria/";
                FileDialog= adapter12.getItem(position);
                String archivo2=archivo.concat(adapter12.getItem(position));
                file = new File(archivo2);
            });
        });
        bt_csv.setOnClickListener(view -> {
            file=null;
            AdapterMultimedia2 adapter13 =seteo(".csv",recyclerView);
            adapter13.setClickListener((view12, position) -> {
                String archivo = "/storage/emulated/0/Memoria/";
                FileDialog= adapter13.getItem(position);
                String archivo2=archivo.concat(adapter13.getItem(position));
                file = new File(archivo2);
            });
        });
        Guardar.setOnClickListener(view -> {
            if (file != null && file.exists()) {
                for(File dir:usbMultimediaPaths){
                    if (dir.isDirectory()) {
                        copyFileProgress(file, dir);
                    }
                }
                if (usbMultimediaPaths.stream().noneMatch(File::isDirectory)) {
                    Utils.Mensaje("Pendrive no disponible", R.layout.item_customtoasterror,activity);
                }
            }
        });
        Cancelar.setOnClickListener(view -> dialogusb.cancel());
        Borrar.setOnClickListener(view -> {
            if (file != null && file.exists()) {
                boolean eliminacion=file.delete();
                if(eliminacion){
                    Utils.Mensaje("Archivo borrado", R.layout.item_customtoastok,activity);
                }else{
                    Utils.Mensaje("El archivo no se pudo borrar", R.layout.item_customtoasterror,activity);
                }
            }else{
                Utils.Mensaje("El archivo no existe", R.layout.item_customtoasterror,activity);
            }
        });
    }

    public static void deleteCache(Context context) {
        try {
            context.getCacheDir().deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String openAndReadFile(String archivo, MainActivity mainActivity) {
        String filePath = "/storage/emulated/0/Memoria/"+archivo;
        File file = new File(filePath);
        if (!file.exists()) {
            Utils.Mensaje("La etiqueta ya no esta disponible",R.layout.item_customtoasterror,mainActivity);
            return "";
        }else{
            String fileContent="";
            FileInputStream fis = null;
            InputStreamReader isr = null;
            BufferedReader br = null;
            try {
                fis = new FileInputStream(file);
                isr = new InputStreamReader(fis);
                br = new BufferedReader(isr);
                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                fileContent = stringBuilder.toString();

            } catch (IOException e) {
                e.printStackTrace();
                Utils.Mensaje("Error al intentar leer la etiqueta"+ e,R.layout.item_customtoasterror,mainActivity);
            } finally {
                try {
                    if (br != null) br.close();
                    if (isr != null) isr.close();
                    if (fis != null) fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return fileContent;
            }
        }

    }

    public AdapterMultimedia2 seteo(String tipo, RecyclerView recyclerView) {
        List<String> ListElementsArrayList2=new ArrayList<>();
        AdapterMultimedia2 adapter3 = new AdapterMultimedia2(activity, ListElementsArrayList2);
        File  root = new File(Environment.getExternalStorageDirectory().toString()+"/Memoria");
        if(root.exists()){
            File[] fileArray = root.listFiles((dir, filename) -> filename.toLowerCase().endsWith(tipo));
            File[] fileArray2 = root.listFiles((dir, filename) -> filename.toLowerCase().endsWith(".png"));
            StringBuilder f = new StringBuilder();
            if(fileArray!=null&&fileArray.length>0){
                for (File value : fileArray) {
                    f.append(value.getName());
                    ListElementsArrayList2.add(f.toString());
                    f = new StringBuilder();
                }
            }
            if(fileArray2!=null&&fileArray2.length>0){
                f = new StringBuilder();
                for (File value : fileArray2) {
                    f.append(value.getName());
                    ListElementsArrayList2.add(f.toString());
                    f = new StringBuilder();
                }
            }
            recyclerView.setLayoutManager(new LinearLayoutManager(activity));
            adapter3 = new AdapterMultimedia2(activity, ListElementsArrayList2);
            recyclerView.setAdapter(adapter3);
        }
        return adapter3;
    }

    public void copyFileProgress(final File file, final File dir) {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity,R.style.AlertDialogCustom);
        View mView = activity.getLayoutInflater().inflate(R.layout.dialogo_transferenciaarchivo, null);
        TextView textView= mView.findViewById(R.id.textView);
        textView.setText("Copiando archivo...");
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        new Thread(() -> {
            File newFile = new File(dir, file.getName());
            try (
                    FileChannel outputChannel = new FileOutputStream(newFile).getChannel();
                    FileChannel inputChannel = new FileInputStream(file).getChannel()
            ) {
                long startTime = System.currentTimeMillis();
                inputChannel.transferTo(0, inputChannel.size(), outputChannel);
                outputChannel.force(true); // Asegura que todos los datos se escriban en el pendrive
                outputChannel.close();

                long endTime = System.currentTimeMillis();
                long elapsedTime = endTime - startTime;
                System.out.println("Tiempo de copia: " + elapsedTime + " milisegundos");

                activity.runOnUiThread(() -> {
                    dialog.cancel();
                    Utils.Mensaje("Archivo enviado", R.layout.item_customtoastok,activity);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public int cantidadRegistros() {
        List<String> Lista=new ArrayList<>();
        File  root = new File(Environment.getExternalStorageDirectory().toString()+"/Memoria");
        if(root.exists()){
            File[] filearr = root.listFiles((dir, filename) -> filename.toLowerCase().endsWith(".xls") && filename.toLowerCase().startsWith("registro"));
            StringBuilder f = new StringBuilder();
            if(filearr!=null&&filearr.length>0){
                for (File value : filearr) {
                    f.append(value.getName());
                    Lista.add(f.toString());
                    f = new StringBuilder();
                }
            }

        }
        return Lista.size();
    }

    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        // Transfer bytes from in to out
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static String JSONarchivos() throws JSONException {
        List<String> guardado= getArchivos();
        JSONArray jsonArray = new JSONArray();
        try {
            for(int i=0;i<guardado.size();i++){
                JSONObject Usuario = new JSONObject();
                Usuario.put("Nombre", guardado.get(i).replace(".pdf","").replace(".png","").replace(".xls","").replace(".csv","").replace(".jpg","").replace(".prn","").replace(".lbl","").replace(".nlbl",""));
                if(guardado.get(i).endsWith(".pdf")){
                    Usuario.put("Tipo", "pdf");
                }
                if(guardado.get(i).endsWith(".png")){
                    Usuario.put("Tipo", "png");
                }
                if(guardado.get(i).endsWith(".xls")){
                    Usuario.put("Tipo", "xls");
                }
                if(guardado.get(i).endsWith(".csv")){
                    Usuario.put("Tipo", "csv");
                }
                if(guardado.get(i).endsWith(".jpg")){
                    Usuario.put("Tipo", "jpg");
                }
                if(guardado.get(i).endsWith(".prn")){
                    Usuario.put("Tipo", "prn");
                }
                if(guardado.get(i).endsWith(".lbl")){
                    Usuario.put("Tipo", "lbl");
                }
                if(guardado.get(i).endsWith(".nlbl")){
                    Usuario.put("Tipo", "nlbl");
                }
                Usuario.put("Fecha", "");
                jsonArray.put(Usuario);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonArray.toString();
    }

    public static List<String> getArchivosExtension(String extension){
        List <String>lista =new ArrayList<>();
        File root = new File(Environment.getExternalStorageDirectory().toString()+"/Memoria");
        if(root.exists()){
            File [] fileArray = root.listFiles((dir, filename) -> filename.toLowerCase().endsWith(extension));
            StringBuilder f = new StringBuilder();
            if(fileArray!=null&&fileArray.length>0){
                for (File value : fileArray) {
                    f.append(value.getName());
                    lista.add(f.toString());
                    f = new StringBuilder();
                }
            }
        }
        return lista;
    }


    public static void createMemoryDirectory() {
        String memoryPath ="/storage/emulated/0/Memoria";
        File fileMemoria = new File(memoryPath);
        if (!fileMemoria.isDirectory()){
            fileMemoria.mkdir();
        }
    }

    public static List<String> getArchivos() {
        List<String> lista = new ArrayList<>();
        File root2 = new File(Environment.getExternalStorageDirectory().toString() + "/Memoria");

        if (root2.exists()) {
            Set<String> extensions = new HashSet<>(Arrays.asList(".pdf", ".png", ".xls", ".csv", ".jpg", ".prn", ".lbl", ".nlbl"));
            File[] files = root2.listFiles();
            if (files != null) {
                lista = Arrays.stream(files)
                        .map(File::getName)
                        .filter(name -> extensions.stream().anyMatch(ext -> name.toLowerCase().endsWith(ext)))
                        .collect(Collectors.toList());
            }
        }
        return lista;
    }

}
