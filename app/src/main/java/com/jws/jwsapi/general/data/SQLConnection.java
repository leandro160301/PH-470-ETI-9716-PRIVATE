package com.jws.jwsapi.general.data;

import android.os.StrictMode;
import android.util.Log;
import androidx.annotation.NonNull;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


/**IMPLEMENTACION:
 *                 connection= SQLConnection.connect(ip,instance,db,user,pass);
 *                 if(connection!=null){
 *                     runOnUiThread(new Runnable() {
 *                         @Override
 *                         public void run() {
 *                             // Código que se ejecutará en el hilo de la interfaz de usuario
 *                             Mensaje("Conexion exitosa", R.layout.customtoastok);
 *                         }
 *                     });
 *
 *
 *
 *
 */


public class SQLConnection {
    private static final String LOG = "DEBUG";
   // private static String vanillaip = "10.41.0.78\\";
   // private static String instance = "SQLSERVER";
    private static final String port = "1433";
  //  private static String db = "prueba_db";
    private static final String classs = "net.sourceforge.jtds.jdbc.Driver";
  /*  private static String un = "sa";
    private static String password = "sa";*/
    public static Connection connect(@NonNull String IpAddress,String Instance, String Database, String Username, String Password) {
        Connection conn = null;
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Class.forName(classs).newInstance();
             String ConnURL = "jdbc:jtds:sqlserver://" + IpAddress +":"+port+";" +"instance="+Instance+";"+"databaseName="+Database+";";
            conn = DriverManager.getConnection(ConnURL,Username,Password);
            System.out.println("Connection successful!");
        } catch (SQLException | ClassNotFoundException e) {
            Log.d(LOG, e.getMessage());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }
        return conn;
    }
}