package com.jws.jwsapi.general.files;

import static com.jws.jwsapi.general.dialog.DialogUtil.dialogLoading;
import static com.jws.jwsapi.general.files.FilePaths.memoryPath;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.general.utils.AdapterCommonFix;
import com.jws.jwsapi.general.utils.ToastHelper;

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

    public static boolean isPackageExisted(String targetPackage, Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void installApk(Context context) {
        if (isPackageExisted("com.android.documentsui", context)) {
            Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage("com.android.documentsui");
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  // Añadido por si el contexto no es una actividad
                context.startActivity(launchIntent);
            }
        }
    }

    public static void deleteCache(Context context) {
        try {
            context.getCacheDir().deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String openAndReadFile(String archivo, MainActivity mainActivity) {
        String filePath = memoryPath+archivo;
        File file = new File(filePath);
        if (!file.exists()) {
            ToastHelper.message("La etiqueta ya no esta disponible",R.layout.item_customtoasterror,mainActivity);
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
                ToastHelper.message("Error al intentar leer la etiqueta"+ e,R.layout.item_customtoasterror,mainActivity);
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

    public static AdapterCommonFix setupRecyclerExtension(String extension, RecyclerView recyclerView, Context context) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        AdapterCommonFix adapter3 = new AdapterCommonFix(context, getFilesExtension(extension));
        recyclerView.setAdapter(adapter3);
        return adapter3;
    }

    public static void copyFileProgress(final File file, final File dir, Context context) {
        AlertDialog dialog =dialogLoading(context, String.valueOf(R.string.dialog_copy_file));
        copyFileTransfer(file, dir, dialog, context);
    }

    private static void copyFileTransfer(File file, File dir, AlertDialog dialog, Context context) {
        new Thread(() -> {
            File newFile = new File(dir, file.getName());
            try (
                    FileChannel outputChannel = new FileOutputStream(newFile).getChannel();
                    FileChannel inputChannel = new FileInputStream(file).getChannel()
            ) {
                inputChannel.transferTo(0, inputChannel.size(), outputChannel);
                outputChannel.force(true); // Asegura que todos los datos se escriban en el pendrive
                outputChannel.close();

                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(() -> {
                    dialog.cancel();
                    ToastHelper.message("Archivo enviado", R.layout.item_customtoastok, context);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public int cantidadRegistros() {
        List<String> Lista=new ArrayList<>();
        File  root = new File(memoryPath);
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

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    public static String JSONarchivos() throws JSONException {
        List<String> guardado= getAllFiles();
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

    public static List<String> getFilesExtension(String extension){
        List <String>lista =new ArrayList<>();
        File root = new File(memoryPath);
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
        File fileMemoria = new File(memoryPath);
        if (!fileMemoria.isDirectory()){
            fileMemoria.mkdir();
        }
    }

    public static List<String> getAllFiles() {
        List<String> lista = new ArrayList<>();
        File root2 = new File(memoryPath);

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
