package com.jws.jwsapi.general.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    public static boolean isIP(String ip){
        String patronIP = "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";
        Pattern pattern = Pattern.compile(patronIP);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }
    public static boolean isLong(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            long l = Long.parseLong(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
    public static String format(String peso, int decimales) {
        String formato = "0.";
        StringBuilder capacidadBuilder = new StringBuilder(formato);
        for (int i = 0; i < decimales; i++) {
            capacidadBuilder.append("0");
        }
        formato = capacidadBuilder.toString();
        DecimalFormat df = new DecimalFormat(formato);

        double pesoNumero = Double.parseDouble(peso);
        return df.format(pesoNumero);
    }
    
    /**
     * Convert byte array to hex string
     * @param bytes toConvert
     * @return hexValue
     */
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sbuf = new StringBuilder();
        for(int idx=0; idx < bytes.length; idx++) {
            int intVal = bytes[idx] & 0xff;
            if (intVal < 0x10) sbuf.append("0");
            sbuf.append(Integer.toHexString(intVal).toUpperCase());
        }
        return sbuf.toString();
    }

    /**
     * Get utf8 byte array.
     * @param str which to be converted
     * @return  array of NULL if error was found
     */
    public static byte[] getUTF8Bytes(String str) {
        try { return str.getBytes(StandardCharsets.UTF_8); } catch (Exception ex) { return null; }
    }

    /**
     * Load UTF8withBOM or any ansi text file.
     * @param filename which to be converted to string
     * @return String value of File
     * @throws java.io.IOException if error occurs
     */
    public static String loadFileAsString(String filename) throws java.io.IOException {
        final int BUFLEN=1024;
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
            byte[] bytes = new byte[BUFLEN];
            boolean isUTF8=false;
            int read,count=0;
            while((read=is.read(bytes)) != -1) {
                if (count==0 && bytes[0]==(byte)0xEF && bytes[1]==(byte)0xBB && bytes[2]==(byte)0xBF ) {
                    isUTF8=true;
                    baos.write(bytes, 3, read-3); // drop UTF8 bom marker
                } else {
                    baos.write(bytes, 0, read);
                }
                count+=read;
            }
            return isUTF8 ? baos.toString(String.valueOf(StandardCharsets.UTF_8)) : baos.toString();
        } finally {
            try{ is.close(); } catch(Exception ignored){}
        }
    }

    public static String jwsGetEthWifiAddress() {
        try {
            return loadFileAsString("/sys/class/net/wlan0/address").toUpperCase().substring(0, 17);
        } catch (IOException var2) {
            var2.printStackTrace();
            return null;
        }
    }

    /**
     * Returns MAC address of the given interface name.
     * @param interfaceName eth0, wlan0 or NULL=use first interface
     * @return  mac address or empty string
     */
    public static String getMACAddress(String interfaceName) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                if (interfaceName != null) {
                    if (!intf.getName().equalsIgnoreCase(interfaceName)) continue;
                }
                byte[] mac = intf.getHardwareAddress();
                if (mac==null) return "";
                StringBuilder buf = new StringBuilder();
                for (byte aMac : mac) buf.append(String.format("%02X:",aMac));
                if (buf.length()>0) buf.deleteCharAt(buf.length()-1);
                return buf.toString();
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
        /*try {
            // this is so Linux hack
            return loadFileAsString("/sys/class/net/" +interfaceName + "/address").toUpperCase().trim();
        } catch (IOException ex) {
            return null;
        }*/
    }

    /**
     * Get IP address from first non-localhost interface
     * @param useIPv4   true=return ipv4, false=return ipv6
     * @return  address or empty string
     */
    public static String getIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress();
                        //boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        boolean isIPv4 = sAddr.indexOf(':')<0;

                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 zone suffix
                                return delim<0 ? sAddr.toUpperCase() : sAddr.substring(0, delim).toUpperCase();
                            }
                        }
                    }
                }
            }
        } catch (Exception ignored) { } // for now eat exceptions
        return "";
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        Bitmap bitmap = null;
        if (drawable instanceof BitmapDrawable) {
            BitmapDrawable bitmapDrawable = (BitmapDrawable) drawable;
            if(bitmapDrawable.getBitmap() != null) {
                return bitmapDrawable.getBitmap();
            }
        }

        if(drawable.getIntrinsicWidth() <= 0 || drawable.getIntrinsicHeight() <= 0) {
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888); // Single color bitmap will be created of 1x1 pixel
        } else {
            bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        }

        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }

    public static String getHora(){
        Calendar calendar = Calendar.getInstance();
        int hour24hrs = calendar.get(Calendar.HOUR_OF_DAY);
        int minutes = calendar.get(Calendar.MINUTE);
        int seconds = calendar.get(Calendar.SECOND);
        String hora24=String.valueOf(hour24hrs);
        String minutos=String.valueOf(minutes);
        String segundos=String.valueOf(seconds);
        return hora24 +":"+minutos+":"+segundos;
    }
    public static String getFecha(){
        return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    }
    public static String getFechaDDMMYYYY(){
        return new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
    }

    public static List<String> obtenerCampoJSON(String campo,String JSON){
        List<String> resultados = new ArrayList<>();
        Gson gson = new Gson();

        // Analizar el JSON en una matriz de objetos JSON
        JsonElement jsonElement = JsonParser.parseString(JSON);

        if (jsonElement.isJsonArray()) {
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            // Iterar sobre los elementos y obtener los valores de "Articulo"
            for (JsonElement element : jsonArray) {
                if (element.isJsonObject()) {
                    JsonObject jsonObject = element.getAsJsonObject();
                    if (jsonObject.has(campo)&&jsonElement != null && !jsonElement.isJsonNull()) {
                        if(!jsonObject.get(campo).isJsonNull()){
                            String articulo = jsonObject.get(campo).getAsString();
                            resultados.add(articulo);
                            System.out.println(articulo);
                        }else{
                            resultados.add("");
                        }

                    }
                }
            }
        }else{
            if(jsonElement.isJsonObject()){
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                if (jsonObject.has(campo)&&jsonElement != null && !jsonElement.isJsonNull()) {
                    if(!jsonObject.get(campo).isJsonNull()){
                        String articulo = jsonObject.get(campo).getAsString();
                        resultados.add(articulo);
                        System.out.println(articulo);
                    }else{
                        resultados.add("");
                    }

                }
            }
        }
        return resultados;
    }

    public static List<String> obtenerCampoJSONconMatriz(String campo, String JSON,String Matriz) {
        List<String> resultados = new ArrayList<>();
        Gson gson = new Gson();

        // Analizar el JSON en un objeto JSON
        JsonElement jsonElement = JsonParser.parseString(JSON);
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObject = jsonElement.getAsJsonObject();

            // Obtener la matriz "rows" del objeto JSON
            if (jsonObject.has(Matriz)) {
                JsonArray jsonArray = jsonObject.getAsJsonArray(Matriz);

                // Iterar sobre los elementos de la matriz "rows" y obtener los valores de "descripcion"
                for (JsonElement element : jsonArray) {
                    if (element.isJsonObject()) {
                        JsonObject rowObject = element.getAsJsonObject();
                        if (rowObject.has(campo)&&jsonElement != null && !jsonElement.isJsonNull()) {
                            if(!rowObject.get(campo).isJsonNull()){
                                String descripcion = rowObject.get(campo).getAsString();
                                resultados.add(descripcion);
                                System.out.println(descripcion);
                            }else{
                                resultados.add("");
                            }

                        }
                    }
                }
            }
        }
        return resultados;
    }

    public static boolean isValidURL(String urlString) {
        try {
            new URL(urlString);
            return true;
        } catch (MalformedURLException e) {
            return false;
        }
    }

    public static int devuelveCodigoUnico() {
        int min = 1000;
        int max = 9999;
        int random_int = (int)Math.floor(Math.random() * (max - min + 1) + min);
        // Printing the generated random numbers
        return random_int;
    }

    public static String pointDecimalFormat(String numero, int decimales) {
        try {
            Double.parseDouble(numero);
        } catch (NumberFormatException e) {
            return "0000";
        }

        String formato = "0";
        if (decimales > 0) {
            formato += ".";
            for (int i = 0; i < decimales; i++) {
                formato += "0";
            }
        }

        DecimalFormat decimalFormat = new DecimalFormat(formato);
        return decimalFormat.format(Double.parseDouble(numero));
    }
    public static int indexOf(String[] array, String target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equals(target)) {
                return i;
            }
        }
        return -1;
    }
}