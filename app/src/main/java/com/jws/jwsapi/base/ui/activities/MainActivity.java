/********************************************************************************
 ################################################################################
 --------------------------------------------------------------------------------
 ################################################################################
 *******************************************************************************/

package com.jws.jwsapi.base.ui.activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import com.android.jws.JwsManager;
import com.jws.jwsapi.base.data.preferences.PreferencesManagerBase;
import com.jws.jwsapi.base.data.sql.Usuarios_SQL_db;
import com.jws.jwsapi.base.models.UsuariosModel;
import com.jws.jwsapi.common.httpwebrtcserver.InitServer;
import com.jws.jwsapi.common.storage.Storage;
import com.jws.jwsapi.feature.formulador.MainFormClass;
import com.jws.jwsapi.feature.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.feature.formulador.models.Form_Model_Guardados;
import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.Utils;
import org.apache.ftpserver.ConnectionConfigFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class MainActivity extends AppCompatActivity {
    public static String VERSION ="PH 470 FRM 1.02";
    public JwsManager jwsObject;
    public static String DB_USERS_NAME ="Usuarios_DB";
    public static int DB_USERS_VERSION =1;
    List<String> listElementsArrayList =new ArrayList<>();

    FtpServerFactory serverFactory;
    String usuario ="";
    int nivelUsuario =0; //0 no logeado, 1 operador, 2 supervisor, 3 administrador, 4 programador
    public static final String[] USUARIOS = {
            "ADMINISTRADOR", "PROGRAMADOR",
    };
    String Contrasena="";
    String memoryPath ="/storage/emulated/0/Memoria";
    File fileMemoria = new File(memoryPath);
    TextView tvnContrasena;

    public MainFormClass mainClass;
    public Storage storage;
    InitServer initServer;
    public PreferencesManagerBase preferencesManagerBase;

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
        initServer = new InitServer(this,this);
        try {
            initServer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createMemoryDirectory() {
        if (!fileMemoria.isDirectory()){
            fileMemoria.mkdir();
        }
    }

    private void initJwsObject() {
        jwsObject = new JwsManager(getApplicationContext());
        setContentView(R.layout.activity_main);
    }

    private void initMainClass() {
        mainClass = new MainFormClass(this,this);
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




    public void Logeo(){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogCustom);
        View mView = getLayoutInflater().inflate(R.layout.dialogo_dosopciones, null);
        final EditText userInput = mView.findViewById(R.id.etDatos);
        final LinearLayout delete_text= mView.findViewById(R.id.lndelete_text);
        delete_text.setOnClickListener(view -> userInput.setText(""));
        TextView textView=mView.findViewById(R.id.textViewt);
        textView.setText("");
        userInput.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS| InputType.TYPE_CLASS_NUMBER| InputType.TYPE_TEXT_VARIATION_PASSWORD);
        userInput.setTransformationMethod(PasswordTransformationMethod.getInstance());
        userInput.requestFocus();
        userInput.setOnLongClickListener(v -> true);
        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);
        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();
        Guardar.setOnClickListener(view -> {
            if(!userInput.getText().toString().equals("")){
                Contrasena=userInput.getText().toString();
                String copia = Contrasena;
                copia=copia.replaceAll("(?s).", "*");
                tvnContrasena.setText(copia);
            }
            dialog.cancel();
        });
        Cancelar.setOnClickListener(view -> dialog.cancel());
    }
    public void BuscarUsuario(String user,String contrasenia){
        List<UsuariosModel> lista=new ArrayList<>();
        try (Usuarios_SQL_db dbHelper = new Usuarios_SQL_db(getApplicationContext(), DB_USERS_NAME, null, DB_USERS_VERSION)) {
            lista=dbHelper.buscarUsuario(user);
        }

        try {
            if(lista.size()==0){
                Utils.Mensaje("No existe el usuario ingresado",R.layout.item_customtoasterror,this);
            }
            else{
                for(int i=0;i<lista.size();i++){
                    if(lista.get(i).password.equals(contrasenia)){
                        usuario =lista.get(i).nombre;
                        if(Objects.equals(lista.get(i).tipo, "Supervisor")){
                            nivelUsuario=2;
                        }
                        if(Objects.equals(lista.get(i).tipo, "Operador")){
                            nivelUsuario=1;
                        }
                        Utils.Mensaje("LOGEO CORRECTO",R.layout.item_customtoastok,this);

                    }
                    else
                    {
                        Utils.Mensaje("Contraseña incorrecta",R.layout.item_customtoasterror,this);
                    }
                }



                }

            } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public void clearCache(){
        if(getNivelUsuario()>2){
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogCustom);
            View mView = getLayoutInflater().inflate(R.layout.dialogo_dossinet, null);

            TextView textView=mView.findViewById(R.id.textViewt);
            textView.setText("¿Esta seguro de volver a los valores de fabrica del equipo?");

            Button Guardar =  mView.findViewById(R.id.buttons);
            Button Cancelar =  mView.findViewById(R.id.buttonc);

            Guardar.setText("RESET");

            mBuilder.setView(mView);
            final AlertDialog dialog = mBuilder.create();
            dialog.show();

            Guardar.setOnClickListener(view1 -> {
                SharedPreferences preferences = getSharedPreferences(PreferencesManager.SP_NAME, Context.MODE_PRIVATE);
                preferences.edit().clear().apply();

                // Elimina la caché de la aplicación
                deleteCache(this);

                // Puedes agregar más código para eliminar otros datos específicos de tu aplicación si es necesario
                deleteDatabase(mainClass.DB_NAME);
                jwsObject.jwsReboot("");
                dialog.cancel();
            });
            Cancelar.setOnClickListener(view12 -> dialog.cancel());
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


    public List<String> DevuelveListaUsuarios(){

        List<UsuariosModel> lista=new ArrayList<>();
        try (Usuarios_SQL_db dbHelper = new Usuarios_SQL_db(getApplicationContext(), DB_USERS_NAME, null, DB_USERS_VERSION)) {
            lista=dbHelper.obtenerUsuarios();
        }
        listElementsArrayList =new ArrayList<>();
        listElementsArrayList.addAll(Arrays.asList(USUARIOS));
        for(int i=0;i<lista.size();i++){
            listElementsArrayList.add(lista.get(i).usuario);
        }
        if(listElementsArrayList.size()>0){
            return listElementsArrayList;
        }else{
            return new ArrayList<>();
        }
    }



    public String JSONusuarios() throws JSONException {

        List<UsuariosModel> lista=new ArrayList<>();
        try (Usuarios_SQL_db dbHelper = new Usuarios_SQL_db(getApplicationContext(), DB_USERS_NAME, null, DB_USERS_VERSION)) {
            lista=dbHelper.obtenerUsuarios();
        }

        JSONArray jsonArray = new JSONArray();
        JSONObject Usuario1 = new JSONObject();
        Usuario1.put("Id", "-");
        Usuario1.put("Nombre", "ADMINISTRADOR");
        Usuario1.put("Usuario","ADMINISTRADOR");
        jsonArray.put(Usuario1);

        JSONObject Usuario2 = new JSONObject();
        Usuario2.put("Id", "-");
        Usuario2.put("Nombre", "PROGRAMADOR");
        Usuario2.put("Usuario","PROGRAMADOR");
        jsonArray.put(Usuario2);

        for(int i=0; i<lista.size();i++){
            JSONObject Usuario = new JSONObject();
            try {
                Usuario.put("Id", lista.get(i).id);
                Usuario.put("Nombre", lista.get(i).nombre);
                Usuario.put("Usuario", lista.get(i).usuario);
                jsonArray.put(Usuario);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return jsonArray.toString();
    }
    public String getUsuarioActual(){
        return usuario;
    }
    public int getNivelUsuario(){
        return nivelUsuario;
    }
    public Boolean modificarDatos(){
        try (Usuarios_SQL_db dbHelper = new Usuarios_SQL_db(getApplicationContext(), MainActivity.DB_USERS_NAME, null, MainActivity.DB_USERS_VERSION)) {
            int cantidad=dbHelper.cantidadUsuarios();
            if(cantidad==0){
                return true;
            }else{
                return getNivelUsuario() > 1;
            }
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
        usuarioGregoArchivos.setName(USUARIOS[1]);
        usuarioGregoArchivos.setPassword("3031");
        usuarioGregoArchivos.setAuthorities(authorities);
        try {
            serverFactory.getUserManager().save(usuarioGregoArchivos);
        } catch (FtpException e) {
            e.printStackTrace();
        }
        BaseUser user = new BaseUser();
        user.setName(USUARIOS[0]);
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
        List<UsuariosModel> lista=new ArrayList<>();
        try (Usuarios_SQL_db dbHelper = new Usuarios_SQL_db(getApplicationContext(), DB_USERS_NAME, null, DB_USERS_VERSION)) {
            lista=dbHelper.obtenerUsuarios();
        }
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
    public void SpinnerTextoNegro(Spinner spinner) {
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(Color.BLACK);
                ((TextView) adapterView.getChildAt(0)).setTextSize(30);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }




    public int cantidadUsuarios(){
        List<UsuariosModel> lista=new ArrayList<>();
        try (Usuarios_SQL_db dbHelper = new Usuarios_SQL_db(getApplicationContext(), DB_USERS_NAME, null, DB_USERS_VERSION)) {
            lista=dbHelper.obtenerUsuarios();
        }
        if(lista!=null){
            return lista.size();
        }else {
            return 0;
        }

    }

    public void BotonLogeo (){
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this,R.style.AlertDialogCustom);
        View mView = getLayoutInflater().inflate(R.layout.dialogo_logeo, null);
        tvnContrasena=  mView.findViewById(R.id.tvnContrasena);
        Spinner spinner=mView.findViewById(R.id.spinner);
        spinner.setPopupBackgroundResource(R.drawable.campollenarclickeable);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                R.layout.item_spinner, DevuelveListaUsuarios());
        adapter.setDropDownViewResource(R.layout.item_spinner);
        SpinnerTextoNegro(spinner);
        spinner.setAdapter(adapter);
        spinner.setSelection(0);

        tvnContrasena.setOnClickListener(view -> Logeo());

        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);
        Button Deslogear = mView.findViewById(R.id.buttond);
        Guardar.setText("LOGEAR");

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.show();

        Deslogear.setOnClickListener(view -> {
            Deslogear();
            dialog.cancel();

        });
        Guardar.setOnClickListener(view -> {
            if(!Contrasena.equals("") && !spinner.getSelectedItem().toString().equals("")){
                Boolean logeo=false;
                if((Contrasena.equals(preferencesManagerBase.consultaPIN())) && spinner.getSelectedItemPosition()==0){
                    nivelUsuario =3;
                    logeo=true;
                    usuario ="ADMINISTRADOR";
                    Utils.Mensaje("LOGEO CORRECTO",R.layout.item_customtoastok,this);

                }
                if((Contrasena.equals("3031")) && spinner.getSelectedItemPosition()==1){
                    nivelUsuario =4;
                    logeo=true;
                    usuario ="PROGRAMADOR";
                    Utils.Mensaje("LOGEO CORRECTO",R.layout.item_customtoastok,this);

                }
                if(!logeo){
                    BuscarUsuario(spinner.getSelectedItem().toString(),Contrasena);
                }
                Contrasena="";
                dialog.cancel();

            }

        });
        Cancelar.setOnClickListener(view -> dialog.cancel());
    }


    public void Deslogear(){
        usuario ="";
        nivelUsuario =0;

    }

    public void openSettings(){
        Intent launchIntent = getApplicationContext().getPackageManager().getLaunchIntentForPackage("com.android.settings");
        if (launchIntent != null) {
            startActivity(launchIntent);
        }
    }


    public void Dialogo_excel(List<Form_Model_Guardados> lista) throws IOException {
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


