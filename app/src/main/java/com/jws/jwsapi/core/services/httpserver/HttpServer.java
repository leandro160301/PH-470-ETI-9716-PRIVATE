package com.jws.jwsapi.core.services.httpserver;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.MainClass;
import com.jws.jwsapi.core.data.local.PreferencesManager;
import com.jws.jwsapi.core.storage.StorageJsonUtils;
import com.jws.jwsapi.core.user.UserJsonUtils;
import com.jws.jwsapi.core.user.UserManager;
import com.jws.jwsapi.pallet.PalletService;
import com.jws.jwsapi.utils.PackageUtils;
import com.jws.jwsapi.weighing.WeighingService;

import org.apache.poi.util.IOUtils;
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
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class HttpServer extends NanoWSD {
    private static final String TAG = HttpServer.class.getSimpleName();

    private static final String HTML_DIR = "";
    private static final String INDEX_HTML = "index.html";
    private static final String INDEX_HTML_error = "index2.html";
    private static final String MIME_IMAGE_SVG = "image/svg+xml";
    private static final String MIME_JS = "text/javascript";
    private static final String MIME_TEXT_PLAIN_JS = "text/plain";
    private static final String MIME_TEXT_CSS = "text/css";

    private static final String MIME_TYPESCRIPT = "application/typescript";
    private static final String MIME_JPG = "image/jpeg";
    private static final String MIME_PNG = "image/png";
    private static final String MIME_EXCEL = "application/vnd.ms-excel";
    private static final String MIME_PDF = "application/pdf";
    private static final String MIME_JSON = "application/json";
    private static final String TYPE_PARAM = "type";
    private static final String TYPE_VALUE_MOUSE_UP = "mouse_up";
    private static final String TYPE_VALUE_MOUSE_MOVE = "mouse_move";
    private static final String TYPE_VALUE_MOUSE_DOWN = "mouse_down";
    private static final String TYPE_VALUE_MOUSE_ZOOM_IN = "mouse_zoom_in";
    private static final String TYPE_VALUE_MOUSE_ZOOM_OUT = "mouse_zoom_out";
    private static final String TYPE_VALUE_BUTTON_BACK = "button_back";
    private static final String TYPE_VALUE_BUTTON_HOME = "button_home";
    private static final String TYPE_VALUE_BUTTON_RECENT = "button_recent";
    private static final String TYPE_VALUE_BUTTON_POWER = "button_power";
    private static final String TYPE_VALUE_BUTTON_LOCK = "button_lock";
    private static final String TYPE_VALUE_JOIN = "join";
    private static final String TYPE_VALUE_SDP = "sdp";
    private static final String TYPE_VALUE_ICE = "ice";
    private static final String TYPE_VALUE_BYE = "bye";
    private static MainActivity mainActivity;
    private final Context context;
    private final HttpServerInterface httpServerInterface;
    Ws webSocket = null;
    UserManager userManager;
    PreferencesManager preferencesManagerBase;
    WeighingService weighingService;
    PalletService palletService;

    public HttpServer(int port, Context context,
                      HttpServerInterface httpServerInterface, MainActivity activity, UserManager userManager, PreferencesManager preferencesManagerBase, WeighingService weighingService, PalletService palletService) {
        super(port);
        this.context = context;
        this.preferencesManagerBase = preferencesManagerBase;
        this.httpServerInterface = httpServerInterface;
        this.weighingService = weighingService;
        this.palletService = palletService;
        mainActivity = activity;
        this.userManager = userManager;
    }

    @NonNull
    private static Response bajarArchivos(IHTTPSession session) throws UnsupportedEncodingException {
        InputStream inputStream = null;
        inputStream = session.getInputStream();

        String nuev = "/storage/emulated/0/Memoria/" + URLDecoder.decode(session.getHeaders().get("nombre"), "UTF-8");
        File fil = new File(nuev);
        if (fil.exists()) {
            fil.delete();
        }
        try (OutputStream outputStream = new FileOutputStream(fil, false)) {
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
            outputStream.flush();

        } catch (IOException e) {
            System.out.println(e.getMessage());
            // handle exception here
        }

        Response response = newFixedLengthResponse(Response.Status.OK, MIME_JSON, "Archivo guardado");
        response.addHeader("Access-Control-Allow-Origin", "*");
        return response;
    }

    @NonNull
    private static Response downloadPreferences(IHTTPSession session, String uri) throws ResponseException {
        Map<String, String> files = new HashMap<>();
        String nombre = null;
        try {
            session.parseBody(files);
            nombre = files.get("postData");
            nombre = URLDecoder.decode(nombre, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (nombre != null) {
            File sharedPrefsFile = new File(mainActivity.getFilesDir().getParent() + "/shared_prefs/" + nombre + ".xml");

            if (sharedPrefsFile.exists()) {
                try {
                    InputStream fileStream = new FileInputStream(sharedPrefsFile);
                    String mime = "text/xml";
                    Response response = newChunkedResponse(Response.Status.OK, mime, fileStream);
                    response.addHeader("Access-Control-Allow-Origin", "*");
                    return response;
                } catch (IOException e) {
                    e.printStackTrace();
                    Response response = newFixedLengthResponse("Error al buscar el archivo: " + nombre);
                    response.addHeader("Access-Control-Allow-Origin", "*");
                    return response;
                }
            } else {
                Response response = newFixedLengthResponse("Error: No se encontró el archivo XML de SharedPreferences.");
                response.addHeader("Access-Control-Allow-Origin", "*");
                return response;
            }
        } else {
            Response response = newFixedLengthResponse("Error al buscar el archivo");
            response.addHeader("Access-Control-Allow-Origin", "*");
            return response;
        }
    }

    @NonNull
    private static Response downloadFile(IHTTPSession session, String uri) throws ResponseException {
        Map<String, String> files = new HashMap<String, String>();
        String nombre = null;
        try {
            session.parseBody(files);
            nombre = files.get("postData");
            nombre = URLDecoder.decode(nombre, "UTF-8");

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (nombre != null) {
            String filePath = "/storage/emulated/0/Memoria/" + nombre;
            System.out.println("Nombre del archivo codificado: " + nombre);
            InputStream fileStream;
            try {
                fileStream = new FileInputStream(new File(filePath));
            } catch (IOException e) {
                e.printStackTrace();
                Response response = newFixedLengthResponse("Error al buscar el archivo:" + nombre);
                response.addHeader("Access-Control-Allow-Origin", "*");
                return response;
            }

            String mime = MIME_TEXT_PLAIN_JS;
            if (uri.contains(".xls"))
                mime = MIME_EXCEL;
            else if (uri.contains(".pdf")) {
                mime = MIME_PDF;
            } else if (uri.contains(".png"))
                mime = MIME_PNG;
            else if (uri.contains(".jpg"))
                mime = MIME_JPG;
            else if (uri.contains(".csv"))
                mime = "text/csv";
            Response response = newChunkedResponse(Response.Status.OK, mime, fileStream);
            response.addHeader("Access-Control-Allow-Origin", "*");
            return response;
        } else {
            Response response = newFixedLengthResponse("Error al buscar el archivo");
            response.addHeader("Access-Control-Allow-Origin", "*");
            return response;
        }
    }

    @Override
    protected WebSocket openWebSocket(IHTTPSession handshake) {
        webSocket = new Ws(handshake);
        return webSocket;
    }

    @Override
    protected Response serveHttp(IHTTPSession session) throws JSONException, IOException, ResponseException {
        Method method = session.getMethod();
        String uri = session.getUri();


        return serveRequest(session, uri, method);
    }

    public void send(String message) throws IOException {
        if (webSocket != null)
            webSocket.send(message);
    }

    private Response serveRequest(IHTTPSession session, String uri, Method method) throws JSONException, IOException, ResponseException {
        if (Method.GET.equals(method))
            return handleGet(session, uri);
        if (Method.POST.equals(method))
            return handlePost(session, uri);
        if (Method.OPTIONS.equals(method)) {
            Response response = newFixedLengthResponse(Response.Status.OK, MIME_JSON, "OPTIONS RECIBIDO");
            response.addHeader("Access-Control-Allow-Origin", "*");
            response.addHeader("Access-Control-Allow-Methods", "OPTIONS,POST,GET,PUT,DELETE");
            response.addHeader("Access-Control-Allow-Headers", "name");

            return response;
        }
        return notFoundResponse();
    }

    private Response notFoundResponse() {
        return newFixedLengthResponse(Response.Status.NOT_FOUND, MIME_PLAINTEXT, "Not Found");
    }

    private Response internalErrorResponse() {
        return newFixedLengthResponse(Response.Status.INTERNAL_ERROR, MIME_PLAINTEXT,
                "Internal error");
    }

    private Response handleGet(IHTTPSession session, String uri) throws JSONException, ResponseException, IOException {
        if (uri.endsWith("/")) {
            return handleRootRequest(session);
        }

        if (uri.endsWith("getArchivos")) {
            try {
                Response response = newFixedLengthResponse(Response.Status.OK, MIME_JSON, StorageJsonUtils.jsonFiles());
                response.addHeader("Access-Control-Allow-Origin", "*");
                return response;
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }

        if (uri.endsWith("getConsultas")) {
            Response response = newFixedLengthResponse(Response.Status.OK, MIME_JSON, ServerUtil.getJsonApi());
            response.addHeader("Access-Control-Allow-Origin", "*");
            return response;

        }
        if (uri.endsWith("GetUsuarios")) {
            Response response = newFixedLengthResponse(Response.Status.OK, MIME_JSON, new UserJsonUtils().jsonUsers(context));
            response.addHeader("Access-Control-Allow-Origin", "*");
            return response;
        }
        if (uri.endsWith("GetPesadas")) {
            Response response = newFixedLengthResponse(Response.Status.OK, MIME_JSON, ServerUtil.getJsonWeighing(weighingService));
            response.addHeader("Access-Control-Allow-Origin", "*");
            return response;

        }
        if (uri.endsWith("GetPalletOpen")) {
            Response response = newFixedLengthResponse(Response.Status.OK, MIME_JSON, ServerUtil.getJsonPalletOpen(palletService));
            response.addHeader("Access-Control-Allow-Origin", "*");
            return response;
        }

        if (uri.endsWith("GetPalletClosed")) {
            Response response = newFixedLengthResponse(Response.Status.OK, MIME_JSON, ServerUtil.getJsonPalletClose(palletService));
            response.addHeader("Access-Control-Allow-Origin", "*");
            return response;
        }


        if (uri.endsWith("getVersion")) {
            Response response = newFixedLengthResponse(Response.Status.OK, MIME_TEXT_PLAIN_JS, MainActivity.VERSION);
            response.addHeader("Access-Control-Allow-Origin", "*");
            return response;

        }
        if (uri.endsWith("LoginHabilitado")) {
            Response response = newFixedLengthResponse(Response.Status.OK, MIME_TEXT_PLAIN_JS, String.valueOf(preferencesManagerBase.getAuthorization()));
            response.addHeader("Access-Control-Allow-Origin", "*");
            return response;

        }

        if (uri.endsWith("inicio")) {
            try {
                mainActivity.runOnUiThread(() -> mainActivity.mainClass.openFragmentPrincipal());

            } catch (Exception e) {
                Response response = newFixedLengthResponse("Error:" + e.getMessage());
                response.addHeader("Access-Control-Allow-Origin", "*");
                return response;
            }
            Response response = newFixedLengthResponse("ok");
            response.addHeader("Access-Control-Allow-Origin", "*");
            return response;

        } else if (uri.endsWith("REBOOT")) {

            mainActivity.jwsObject.jwsReboot("");
            return newFixedLengthResponse("Hecho");
        } else if (uri.endsWith("INSTALLAPK")) {
            PackageUtils.installApk(mainActivity, mainActivity);
            return newFixedLengthResponse("Hecho");
        } else if (uri.endsWith("CONFIGURACION")) {

            mainActivity.openSettings();
            return newFixedLengthResponse("Hecho");
        } else if (uri.contains("private")) {
            return notFoundResponse();
        }

        return handleFileRequest(session, uri);
    }

    private Response handlePost(IHTTPSession session, String uri) throws IOException, ResponseException {
        if (uri.endsWith("sendFiles")) {
            return bajarArchivos(session);
        }

        if (uri.endsWith("/descargarArchivo")) {
            return downloadFile(session, uri);
        }

        if (uri.endsWith("/descargarPreferences")) {
            return downloadPreferences(session, uri);
        }

        if (uri.endsWith("/descargarDB")) {
            return downloadDb();
        }

        if (uri.endsWith("updateApk")) {
            return updateApk(session);

        } else if (uri.contains("private")) {
            return notFoundResponse();
        }

        return handleFileRequest(session, uri);
    }

    @SuppressWarnings("all")
    @NonNull
    private Response downloadDb() {
        String filePath = context.getDatabasePath(MainClass.DB_NAME).getAbsolutePath();
        InputStream fileStream;
        try {
            fileStream = new FileInputStream(new File(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            Response response = newFixedLengthResponse("Error al buscar la base de datos: " + e.getMessage());
            response.addHeader("Access-Control-Allow-Origin", "*");
            return response;
        }

        String mime = "application/octet-stream";
        Response response = newChunkedResponse(Response.Status.OK, mime, fileStream);
        response.addHeader("Access-Control-Allow-Origin", "*");
        return response;
    }

    @SuppressWarnings("all")
    @NonNull
    private Response updateApk(IHTTPSession session) {
        InputStream inputStream = session.getInputStream();
        String nuev = "/storage/emulated/0/Download/jwsapi.apk";
        File fil = new File(nuev);
        if (fil.exists()) {
            fil.delete();
        }
        try (OutputStream outputStream = new FileOutputStream(fil, false)) {
            IOUtils.copy(inputStream, outputStream);
            inputStream.close();
            outputStream.close();
            outputStream.flush();

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        mainActivity.runOnUiThread(() -> mainActivity.jwsObject.jwsSilentInstall(fil.getAbsolutePath(), context));

        Response response = newFixedLengthResponse(Response.Status.OK, MIME_JSON, "Archivo guardado");
        response.addHeader("Access-Control-Allow-Origin", "*");
        return response;
    }

    private Response handleRootRequest(IHTTPSession session) {
        String indexHtml = readFile(HTML_DIR + INDEX_HTML);
        if (preferencesManagerBase.getRemoteFix()) {
            indexHtml = readFile(HTML_DIR + INDEX_HTML_error);
        } else {
            indexHtml = readFile(HTML_DIR + INDEX_HTML);
        }


        return newFixedLengthResponse(Response.Status.OK, MIME_HTML, indexHtml);
    }

    private void handleRequest(JSONObject json) {
        String type;
        try {
            type = json.getString(TYPE_PARAM);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        switch (type) {
            case TYPE_VALUE_MOUSE_DOWN:
                httpServerInterface.onMouseDown(json);
                break;
            case TYPE_VALUE_MOUSE_MOVE:
                httpServerInterface.onMouseMove(json);
                break;
            case TYPE_VALUE_MOUSE_UP:
                httpServerInterface.onMouseUp(json);
                break;
            case TYPE_VALUE_MOUSE_ZOOM_IN:
                httpServerInterface.onMouseZoomIn(json);
                break;
            case TYPE_VALUE_MOUSE_ZOOM_OUT:
                httpServerInterface.onMouseZoomOut(json);
                break;
            case TYPE_VALUE_BUTTON_BACK:
                httpServerInterface.onButtonBack();
                break;
            case TYPE_VALUE_BUTTON_HOME:
                httpServerInterface.onButtonHome();
                break;
            case TYPE_VALUE_BUTTON_RECENT:
                httpServerInterface.onButtonRecent();
                break;
            case TYPE_VALUE_BUTTON_POWER:
                httpServerInterface.onButtonPower();
                break;
            case TYPE_VALUE_BUTTON_LOCK:
                httpServerInterface.onButtonLock();
                break;
            case TYPE_VALUE_JOIN:
                httpServerInterface.onJoin(this);
                break;
            case TYPE_VALUE_SDP:
                httpServerInterface.onSdp(json);
                break;
            case TYPE_VALUE_ICE:
                httpServerInterface.onIceCandidate(json);
                break;
            case TYPE_VALUE_BYE:
                httpServerInterface.onBye();
                break;
        }
    }

    private String readFile(String fileName) {
        InputStream fileStream;
        String string = "";

        try {
            fileStream = context.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileStream,
                    StandardCharsets.UTF_8));

            String line;
            while ((line = reader.readLine()) != null)
                string += line;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return string;
    }

    private Response handleFileRequest(IHTTPSession session, String uri) {
        String relativePath = uri.startsWith("/") ? uri.substring(1) : uri;

        InputStream fileStream;
        try {
            fileStream = context.getAssets().open(relativePath);
        } catch (IOException e) {
            e.printStackTrace();
            return notFoundResponse();
        }

        String mime;
        if (uri.contains(".js"))
            mime = MIME_JS;
        else if (uri.contains(".svg"))
            mime = MIME_IMAGE_SVG;
        else if (uri.contains(".css"))
            mime = MIME_TEXT_CSS;
        else if (uri.contains(".ts"))
            mime = MIME_TYPESCRIPT;
        else if (uri.contains(".png"))
            mime = MIME_PNG;
        else if (uri.contains(".jpg"))
            mime = MIME_JPG;
        else
            mime = MIME_TEXT_PLAIN_JS;

        return newChunkedResponse(Response.Status.OK, mime, fileStream);
    }

    private Map<String, List<String>> extraerFiltros(IHTTPSession session) {
        Map<String, List<String>> parametros = session.getParameters();
        Map<String, List<String>> filtros = new HashMap<>();
        if (parametros.containsKey("columna")) {
            return filtros;
        }
        for (Map.Entry<String, List<String>> entry : parametros.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();
            if (!values.isEmpty()) {
                filtros.put(key, values);
            }
        }

        return filtros;
    }

    public interface HttpServerInterface {
        void onMouseDown(JSONObject message);

        void onMouseMove(JSONObject message);

        void onMouseUp(JSONObject message);

        void onMouseZoomIn(JSONObject message);

        void onMouseZoomOut(JSONObject message);

        void onButtonBack();

        void onButtonHome();

        void onButtonRecent();

        void onButtonPower();

        void onButtonLock();

        void onJoin(HttpServer server);

        void onSdp(JSONObject message);

        void onIceCandidate(JSONObject message);

        void onBye();

        void onWebSocketClose();
    }

    class Ws extends WebSocket {
        private static final int PING_INTERVAL = 20000;
        private final Timer pingTimer = new Timer();

        public Ws(IHTTPSession handshakeRequest) {
            super(handshakeRequest);
        }

        @Override
        protected void onOpen() {
            Log.d(TAG, "WebSocket open");
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {

                    try {
                        Ws.this.ping(new byte[0]);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };

            pingTimer.scheduleAtFixedRate(timerTask, PING_INTERVAL, PING_INTERVAL);
        }

        @Override
        protected void onClose(WebSocketFrame.CloseCode code, String reason,
                               boolean initiatedByRemote) {
            Log.d(TAG, "WebSocket close, reason: " + reason);
            pingTimer.cancel();
            httpServerInterface.onWebSocketClose();
        }

        @Override
        protected void onMessage(WebSocketFrame message) {
            JSONObject json;

            try {
                json = new JSONObject(message.getTextPayload());
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }

            handleRequest(json);
        }

        @Override
        protected void onPong(WebSocketFrame pong) {
        }

        @Override
        protected void onException(IOException exception) {
            Log.d(TAG, "WebSocket exception");
            //pingTimer.cancel();
        }
    }

}
