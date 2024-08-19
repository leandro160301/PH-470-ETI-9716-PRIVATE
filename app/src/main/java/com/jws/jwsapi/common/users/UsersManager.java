package com.jws.jwsapi.common.users;

import static com.jws.jwsapi.feature.formulador.ui.dialog.DialogUtil.TecladoPassword;
import static com.jws.jwsapi.helpers.SpinnerHelper.configurarSpinner;
import android.app.AlertDialog;
import android.app.Application;
import android.content.Context;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.jws.jwsapi.R;
import com.jws.jwsapi.base.data.preferences.PreferencesManagerBase;
import com.jws.jwsapi.base.data.sql.Usuarios_SQL_db;
import com.jws.jwsapi.base.models.UsuariosModel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class UsersManager {

    private final Application application;
    public static String DB_USERS_NAME ="Usuarios_DB";
    public static int DB_USERS_VERSION =1;
    String usuario ="";
    int nivelUsuario =0; //0 no logeado, 1 operador, 2 supervisor, 3 administrador, 4 programador
    public static final String[] USUARIOS = {
            "ADMINISTRADOR", "PROGRAMADOR",
    };
    String password ="";

    @Inject
    public UsersManager(Application application){
        this.application =application;
    }


    public int cantidadUsuarios(){
        List<UsuariosModel> lista=obtenerUsuarios();
        if(lista!=null){
            return lista.size();
        }else {
            return 0;
        }
    }

    public List<UsuariosModel> obtenerUsuarios(){
        List<UsuariosModel> lista;
        try (Usuarios_SQL_db dbHelper = new Usuarios_SQL_db(application, DB_USERS_NAME, null, DB_USERS_VERSION)) {
            lista=dbHelper.obtenerUsuarios();
        }
        return lista;
    }
    public void BotonLogeo (Context context){
        PreferencesManagerBase preferencesManagerBase=new PreferencesManagerBase(application);
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(context, R.style.AlertDialogCustom);
        View mView = LayoutInflater.from(context).inflate(R.layout.dialogo_logeo, null);
        TextView tvnContrasena=  mView.findViewById(R.id.tvnContrasena);
        Spinner spinner=mView.findViewById(R.id.spinner);
        configurarSpinner(spinner,application,DevuelveListaUsuarios());
        spinner.setSelection(0);
        tvnContrasena.setOnClickListener(view -> Logeo(tvnContrasena,context));

        Button Guardar =  mView.findViewById(R.id.buttons);
        Button Cancelar =  mView.findViewById(R.id.buttonc);
        Button Deslogear = mView.findViewById(R.id.buttond);
        Guardar.setText("LOGEAR");

        mBuilder.setView(mView);
        final AlertDialog dialog = mBuilder.create();
        dialog.show();

        Deslogear.setOnClickListener(view -> {
            Deslogear();
            dialog.cancel();

        });
        Guardar.setOnClickListener(view -> {
            if(!password.equals("") && !spinner.getSelectedItem().toString().equals("")){
                boolean logeo=false;
                if((password.equals(preferencesManagerBase.consultaPIN())) && spinner.getSelectedItemPosition()==0){
                    nivelUsuario =3;
                    logeo=true;
                    usuario ="ADMINISTRADOR";
                   // Utils.Mensaje("LOGEO CORRECTO",R.layout.item_customtoastok,this);

                }
                if((password.equals("3031")) && spinner.getSelectedItemPosition()==1){
                    nivelUsuario =4;
                    logeo=true;
                    usuario ="PROGRAMADOR";
                  //  Utils.Mensaje("LOGEO CORRECTO",R.layout.item_customtoastok,this);

                }
                if(!logeo){
                    BuscarUsuario(spinner.getSelectedItem().toString(), password);
                }
                password ="";
                dialog.cancel();

            }

        });
        Cancelar.setOnClickListener(view -> dialog.cancel());
    }


    public void Deslogear(){
        usuario ="";
        nivelUsuario =0;

    }

    public List<String> DevuelveListaUsuarios(){
        List<UsuariosModel> lista;
        try (Usuarios_SQL_db dbHelper = new Usuarios_SQL_db(application, DB_USERS_NAME, null, DB_USERS_VERSION)) {
            lista=dbHelper.obtenerUsuarios();
        }
        List<String> listElementsArrayList = new ArrayList<>(Arrays.asList(USUARIOS));
        for(int i=0;i<lista.size();i++){
            listElementsArrayList.add(lista.get(i).usuario);
        }
        if(listElementsArrayList.size()>0){
            return listElementsArrayList;
        }else{
            return new ArrayList<>();
        }
    }


    public void Logeo(TextView textView,Context context){
        TecladoPassword(null, "", context, texto -> {
            password =texto;
            String copia = password;
            copia=copia.replaceAll("(?s).", "*");
            textView.setText(copia);
        }, PasswordTransformationMethod.getInstance());
    }
    public void BuscarUsuario(String user,String contrasenia){
        List<UsuariosModel> lista;
        try (Usuarios_SQL_db dbHelper = new Usuarios_SQL_db(application, DB_USERS_NAME, null, DB_USERS_VERSION)) {
            lista=dbHelper.buscarUsuario(user);
        }
        try {
            if(lista.size()==0){
                //Utils.Mensaje("No existe el usuario ingresado", R.layout.item_customtoasterror,this);
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
                        //Utils.Mensaje("LOGEO CORRECTO",R.layout.item_customtoastok,this);
                    }
                    else
                    {
                        //Utils.Mensaje("Contraseña incorrecta",R.layout.item_customtoasterror,this);
                    }
                }

            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }


    }

    public String JSONusuarios() throws JSONException {
        List<UsuariosModel> lista;
        try (Usuarios_SQL_db dbHelper = new Usuarios_SQL_db(application, DB_USERS_NAME, null, DB_USERS_VERSION)) {
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
        try (Usuarios_SQL_db dbHelper = new Usuarios_SQL_db(application,DB_USERS_NAME, null, DB_USERS_VERSION)) {
            int cantidad=dbHelper.cantidadUsuarios();
            if(cantidad==0){
                return true;
            }else{
                return getNivelUsuario() > 1;
            }
        }
    }

}
