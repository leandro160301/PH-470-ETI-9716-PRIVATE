/********************************************************************************
 ################################################################################
 --------------------------------------------------------------------------------
 ################################################################################
 *******************************************************************************/

package com.jws.jwsapi.base.ui.activities;

import static com.jws.jwsapi.common.storage.Storage.createMemoryDirectory;
import static com.jws.jwsapi.common.users.UsersManager.obtenerUsuarios;
import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.dialogoTexto;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.WindowManager;
import com.android.jws.JwsManager;
import com.jws.jwsapi.base.data.preferences.PreferencesManagerBase;
import com.jws.jwsapi.base.models.UsuariosModel;
import com.jws.jwsapi.common.httpwebrtcserver.InitServer;
import com.jws.jwsapi.common.storage.Storage;
import com.jws.jwsapi.common.users.UsersManager;
import com.jws.jwsapi.feature.formulador.MainFormClass;
import com.jws.jwsapi.feature.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.feature.formulador.models.Form_Model_Guardados;
import com.jws.jwsapi.R;
import com.jws.jwsapi.feature.formulador.ui.dialog.DialogButtonInterface;
import com.jws.jwsapi.utils.Utils;
import org.apache.ftpserver.ConnectionConfigFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import javax.inject.Inject;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    public static String VERSION ="PH 470 FRM 1.02";
    public JwsManager jwsObject;
    FtpServerFactory serverFactory;
    public MainFormClass mainClass;
    public Storage storage;
    InitServer initServer;
    public PreferencesManagerBase preferencesManagerBase;
    @Inject
    UsersManager usersManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        preferencesManagerBase= new PreferencesManagerBase(this);
        setTheme();
        initJwsObject();
        updateViews();
        createMemoryDirectory();
        hideNav();
        initFtpWebRTC();
        initMainClass();
        initPendrive();

    }

    private void initPendrive() {
        storage =new Storage(this);
        storage.init();
    }

    private void initFtpWebRTC() {
        try {
            FtpServidor();
        } catch (Exception e) {
            e.printStackTrace();
        }
        initServer = new InitServer(this,this,usersManager);
        try {
            initServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initJwsObject() {
        jwsObject = new JwsManager(getApplicationContext());
        setContentView(R.layout.activity_main);
    }

    private void initMainClass() {
        mainClass = new MainFormClass(this,this,usersManager);
        mainClass.init();
    }

    private void updateViews() {
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        @SuppressLint("UseCompatLoadingForDrawables") Drawable drawable = getResources().getDrawable(R.drawable.blanco);
        Bitmap bitmap = Utils.drawableToBitmap(drawable);
        try {
            wallpaperManager.setBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void hideNav() {
        Intent hidenav = new Intent("android.intent.action.HIDE_NAVIGATION_BAR");
        this.getApplicationContext().sendBroadcast(hidenav);
    }

    public void setTheme() {
        if(preferencesManagerBase.consultaTema()!=R.style.AppTheme_NoActionBar){
            try {
                setTheme(preferencesManagerBase.consultaTema());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }






    public void clearCache(){
        Context context=getApplicationContext();
        if(usersManager.getNivelUsuario()>2){
            dialogoTexto(this, "¿Esta seguro de volver a los valores de fabrica del equipo?", "RESER", new DialogButtonInterface() {
                @Override
                public void buttonClick() {
                    SharedPreferences preferences = getSharedPreferences(PreferencesManager.SP_NAME, Context.MODE_PRIVATE);
                    preferences.edit().clear().apply();
                    // Elimina la caché de la aplicación
                    deleteCache(context);
                    // Puedes agregar más código para eliminar otros datos específicos de tu aplicación si es necesario
                    deleteDatabase(MainFormClass.DB_NAME);
                    jwsObject.jwsReboot("");
                }
            });
        }else{
            Utils.Mensaje("Debe ingresar la clave para acceder a esta configuracion",R.layout.item_customtoasterror,this);
        }


    }
    private void deleteCache(Context context) {
        try {
            context.getCacheDir().deleteOnExit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void FtpServidor(){
        serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(2221);
        serverFactory.addListener("default", factory.createListener());
        ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
        connectionConfigFactory.setAnonymousLoginEnabled(false);
        serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());
        List<Authority> authorities = new ArrayList<>();
        authorities.add(new WritePermission());
        BaseUser usuarioGregoArchivos = new BaseUser();
        usuarioGregoArchivos.setName(UsersManager.USUARIOS[1]);
        usuarioGregoArchivos.setPassword("3031");
        usuarioGregoArchivos.setAuthorities(authorities);
        try {
            serverFactory.getUserManager().save(usuarioGregoArchivos);
        } catch (FtpException e) {
            e.printStackTrace();
        }
        BaseUser user = new BaseUser();
        user.setName(UsersManager.USUARIOS[0]);
        user.setPassword(preferencesManagerBase.consultaPIN());
        user.setHomeDirectory(Environment.getExternalStorageDirectory().toString()+"/Memoria");
        user.setAuthorities(authorities);
        try {
            serverFactory.getUserManager().save(user);
        } catch (FtpException e) {
            e.printStackTrace();
        }
        cargadeUsuariosFtp();
        FtpServer server = serverFactory.createServer();
        try {
            server.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }

    }
    public void cargadeUsuariosFtp(){
        List<UsuariosModel> lista= obtenerUsuarios();
        for(int i=0;i<lista.size();i++){
            BaseUser user = new BaseUser();
            user.setName(lista.get(i).usuario);
            user.setPassword(lista.get(i).password);
            user.setHomeDirectory(Environment.getExternalStorageDirectory().getAbsolutePath());
            try {
                serverFactory.getUserManager().save(user);
            } catch (FtpException e) {
                e.printStackTrace();
            }
        }

    }




    public void openSettings(){
        Intent launchIntent = getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.android.settings");
        if (launchIntent != null) {
            startActivity(launchIntent);
        }
    }


    public void dialogoExcel(List<Form_Model_Guardados> lista) throws IOException {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogCustom);
        View mView = getLayoutInflater().inflate(R.layout.dialogo_transferenciaarchivo, null);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        ExcelEstadisticas(lista);
        new Thread(() -> {
            try {
                Thread.sleep(7000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            runOnUiThread(() -> {
                dialog.cancel();
                Utils.Mensaje("Excel creado", R.layout.item_customtoastok,this);
            });
        }).start();
    }

    public int DevuelveCodigoUnico() {
        int min = 1000;
        int max = 9999;
        int random_int = (int)Math.floor(Math.random() * (max - min + 1) + min);
        // Printing the generated random numbers
        return random_int;
    }

    public void ExcelEstadisticas(List<Form_Model_Guardados> lista) throws IOException {
        if(lista.size()>0){
            int j=1;
            File filePath = new File(Environment.getExternalStorageDirectory() + "/Memoria/Registro.xls");
            while(filePath.exists()){
                filePath = new File(Environment.getExternalStorageDirectory() + "/Memoria/Registro ("+ j +").xls");
                j++;
            }
            HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
            HSSFSheet hssfSheet = hssfWorkbook.createSheet("Pesadas");
            HSSFRow row = hssfSheet.createRow(0);
            row.createCell(0).setCellValue("Id");
            row.createCell(1).setCellValue("Producto");
            row.createCell(2).setCellValue("Lote");
            row.createCell(3).setCellValue("Cliente");
            row.createCell(4).setCellValue("Dimensiones");
            row.createCell(5).setCellValue("Neto");
            row.createCell(6).setCellValue("Bruto");
            row.createCell(7).setCellValue("Tara");
            row.createCell(8).setCellValue("Fecha");
            row.createCell(9).setCellValue("Hora");

            int i;
            for(i=0;i<lista.size();i++){
                row = hssfSheet.createRow(i + 1); // Crea una nueva fila en cada iteración
                row.createCell(0).setCellValue(lista.get(i).getId());
                row.createCell(1).setCellValue(lista.get(i).getProducto());
                row.createCell(2).setCellValue(lista.get(i).getLote());
                row.createCell(3).setCellValue(lista.get(i).getEmpresa());
                row.createCell(4).setCellValue(lista.get(i).getDimensiones());
                row.createCell(5).setCellValue(lista.get(i).getNeto());
                row.createCell(6).setCellValue(lista.get(i).getBruto());
                row.createCell(7).setCellValue(lista.get(i).getTara());
                row.createCell(8).setCellValue(lista.get(i).getFecha());
                row.createCell(9).setCellValue(lista.get(i).getHora());
            }
            try {
                if (!filePath.exists()){
                    filePath.createNewFile();
                }
                FileOutputStream fileOutputStream= new FileOutputStream(filePath);
                hssfWorkbook.write(fileOutputStream);
                if (fileOutputStream!=null){
                    fileOutputStream.flush();
                    fileOutputStream.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        initServer.handleActivityResult(requestCode, resultCode, data);
    }

}


