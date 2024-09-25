package com.jws.jwsapi.core.services.httpserver;

import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjection;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.core.data.local.PreferencesManager;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.CameraEnumerator;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.Logging;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.RtpParameters;
import org.webrtc.RtpSender;
import org.webrtc.ScreenCapturerAndroid;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebRtcManager {

    private static final String TAG = WebRtcManager.class.getSimpleName();
    private static final boolean ENABLE_INTEL_VP8_ENCODER = false;
    private static final boolean ENABLE_H264_HIGH_PROFILE = false;
    private static final int FRAMES_PER_SECOND = 3;
    private static final String SDP_PARAM = "sdp";
    private static final String ICE_PARAM = "ice";
    private final MainActivity mainActivity;
    private final HttpServer server;
    private final Display display;
    private final PreferencesManager preferencesManagerBase;
    RtpSender rtpsender;
    RtpParameters rtpParameters;
    List<PeerConnection.IceServer> peerIceServers = new ArrayList<>();
    private VideoCapturer videoCapturer;
    private EglBase rootEglBase;
    private PeerConnectionFactory peerConnectionFactory;
    private VideoTrack localVideoTrack;
    private PeerConnection localPeer = null;
    private MediaConstraints sdpConstraints;
    private List<IceServer> iceServers = null;
    private DisplayMetrics screenMetrics = new DisplayMetrics();
    private Thread rotationDetectorThread = null;

    public WebRtcManager(Intent intent, Context context, HttpServer server, MainActivity activity, PreferencesManager preferencesManagerBase) {
        this.server = server;
        this.preferencesManagerBase = preferencesManagerBase;
        this.mainActivity = activity;
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        display = wm.getDefaultDisplay();
        createMediaProjection(intent);
        initWebRTC(context);

    }

    public void close() {
        stop();
        stopRotationDetector();
        destroyMediaProjection();
    }

    private void createMediaProjection(Intent intent) {
        videoCapturer = new ScreenCapturerAndroid(intent,
                new MediaProjection.Callback() {
                    @Override
                    public void onStop() {
                        super.onStop();
                        Log.e(TAG, "User has revoked media projection permissions");
                    }
                });
    }

    private void destroyMediaProjection() {
        try {
            videoCapturer.stopCapture();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        }
        videoCapturer = null;
    }

    private void initWebRTC(Context context) {
        rootEglBase = EglBase.create();

        PeerConnectionFactory.InitializationOptions initializationOptions =
                PeerConnectionFactory.InitializationOptions.builder(context)
                        .createInitializationOptions();
        PeerConnectionFactory.initialize(initializationOptions);

        PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
        DefaultVideoEncoderFactory defaultVideoEncoderFactory = new DefaultVideoEncoderFactory(
                rootEglBase.getEglBaseContext(), ENABLE_INTEL_VP8_ENCODER,
                ENABLE_H264_HIGH_PROFILE);
        DefaultVideoDecoderFactory defaultVideoDecoderFactory = new
                DefaultVideoDecoderFactory(rootEglBase.getEglBaseContext());
        peerConnectionFactory = PeerConnectionFactory.builder()
                .setOptions(options)
                .setVideoEncoderFactory(defaultVideoEncoderFactory)
                .setVideoDecoderFactory(defaultVideoDecoderFactory)
                .createPeerConnectionFactory();
        //XXX enable camera
        //videoCapturer = createCameraCapturer(new Camera1Enumerator(false));


        SurfaceTextureHelper surfaceTextureHelper;
        surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread",
                rootEglBase.getEglBaseContext());
        VideoSource videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
        //videoSource.adaptOutputFormat(1280,800,3);


        videoCapturer.initialize(surfaceTextureHelper, context, videoSource.getCapturerObserver());
        localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource);
        //TODO audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        //TODO localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);

        display.getRealMetrics(screenMetrics);
        if (videoCapturer != null) {

            if (preferencesManagerBase.getRemoteFix()) {
                videoCapturer.startCapture((screenMetrics.widthPixels), (screenMetrics.heightPixels),
                        FRAMES_PER_SECOND);
            } else {
                videoCapturer.startCapture((screenMetrics.heightPixels), (screenMetrics.widthPixels),
                        FRAMES_PER_SECOND);
            }

            // startRotationDetector();
        }


        /*
         SurfaceTextureHelper surfaceTextureHelper;
        surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread",
                rootEglBase.getEglBaseContext());
        VideoSource videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
        videoSource.adaptOutputFormat(1280,800,3);


        videoCapturer.initialize(surfaceTextureHelper, context, videoSource.getCapturerObserver());

        localVideoTrack = peerConnectionFactory.createVideoTrack("100", videoSource);

        //TODO audioSource = peerConnectionFactory.createAudioSource(audioConstraints);
        //TODO localAudioTrack = peerConnectionFactory.createAudioTrack("101", audioSource);

        display.getRealMetrics(screenMetrics);
        if (videoCapturer != null) {
            videoCapturer.startCapture((screenMetrics.heightPixels), (screenMetrics.widthPixels),
                    FRAMES_PER_SECOND);
           // startRotationDetector();
        }
        * */

    }

    public void start(HttpServer server) {
        Log.d(TAG, "WebRTC start");
        createPeerConnection();
        doCall(server);
    }

    public void stop() {
        Log.d(TAG, "WebRTC stop");
        if (localPeer == null)
            return;
        localPeer.close();
        localPeer = null;
    }

    private void createPeerConnection() {
        PeerConnection.RTCConfiguration rtcConfig =
                new PeerConnection.RTCConfiguration(peerIceServers);
        // TCP candidates are only useful when connecting to a server that supports
        // ICE-TCP.
        rtcConfig.tcpCandidatePolicy = PeerConnection.TcpCandidatePolicy.DISABLED;
        rtcConfig.bundlePolicy = PeerConnection.BundlePolicy.MAXBUNDLE;
        rtcConfig.rtcpMuxPolicy = PeerConnection.RtcpMuxPolicy.REQUIRE;
        rtcConfig.continualGatheringPolicy = PeerConnection.ContinualGatheringPolicy
                .GATHER_CONTINUALLY;

        rtcConfig.keyType = PeerConnection.KeyType.ECDSA;
        localPeer = peerConnectionFactory.createPeerConnection(rtcConfig,
                new CustomPeerConnectionObserver("localPeerCreation") {
                    @Override
                    public void onIceCandidate(IceCandidate iceCandidate) {
                        super.onIceCandidate(iceCandidate);
                        onIceCandidateReceived(iceCandidate);
                    }

                    @Override
                    public void onAddStream(MediaStream mediaStream) {
                        super.onAddStream(mediaStream);
                        Log.d(TAG, "Unexpected remote stream received.");
                    }
                });

        addStreamToLocalPeer();
    }

    public void onIceCandidateReceived(IceCandidate iceCandidate) {
        JSONObject messageJson = new JSONObject();
        JSONObject iceJson = new JSONObject();
        try {
            iceJson.put("type", "candidate");
            iceJson.put("label", iceCandidate.sdpMLineIndex);
            iceJson.put("id", iceCandidate.sdpMid);
            iceJson.put("candidate", iceCandidate.sdp);

            messageJson.put("type", "ice");
            messageJson.put("ice", iceJson);

            String messageJsonStr = messageJson.toString();
            //XXX broadcast
            server.send(messageJson.toString());
            Log.d(TAG, "Send ICE candidates: " + messageJsonStr);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addStreamToLocalPeer() {
        MediaStream stream = peerConnectionFactory.createLocalMediaStream("102");
        //stream.addTrack(localAudioTrack);
        stream.addTrack(localVideoTrack);
        localPeer.addStream(stream);

    }

    private void doCall(HttpServer server) {
        sdpConstraints = new MediaConstraints();
        //TODO sdpConstraints.mandatory.add(
        //        new MediaConstraints.KeyValuePair("OfferToReceiveAudio", "true"));
        sdpConstraints.mandatory.add(
                new MediaConstraints.KeyValuePair("OfferToReceiveVideo", "true"));
        localPeer.createOffer(new CustomSdpObserver("localCreateOffer") {
            @Override
            public void onCreateSuccess(SessionDescription sessionDescription) {
                super.onCreateSuccess(sessionDescription);

                localPeer.setLocalDescription(new CustomSdpObserver("localSetLocalDesc"),
                        sessionDescription);

                JSONObject messageJson = new JSONObject();
                JSONObject sdpJson = new JSONObject();
                try {
                    sdpJson.put("type", sessionDescription.type.canonicalForm());
                    sdpJson.put("sdp", sessionDescription.description);

                    messageJson.put("type", "sdp");
                    messageJson.put("sdp", sdpJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                    return;
                }

                String messageJsonStr = messageJson.toString();
                try {
                    server.send(messageJsonStr);
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                Log.d(TAG, "Send SDP: " + messageJsonStr);
            }
        }, sdpConstraints);
    }

    public void onAnswerReceived(JSONObject data) {
        JSONObject json;
        try {
            json = data.getJSONObject(SDP_PARAM);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Log.d(TAG, "Remote SDP received: " + json);

        try {
            localPeer.setRemoteDescription(new CustomSdpObserver("localSetRemote"),
                    new SessionDescription(SessionDescription.Type.fromCanonicalForm(json.getString(
                            "type").toLowerCase()), json.getString("sdp")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void onIceCandidateReceived(JSONObject data) {
        JSONObject json;
        try {
            json = data.getJSONObject(ICE_PARAM);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        Log.d(TAG, "ICE candidate received: " + json);

        try {
            localPeer.addIceCandidate(new IceCandidate(json.getString("id"), json.getInt("label"),
                    json.getString("candidate")));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private VideoCapturer createCameraCapturer(CameraEnumerator enumerator) {
        Log.d(TAG, new Object() {
        }.getClass().getEnclosingMethod().getName());
        final String[] deviceNames = enumerator.getDeviceNames();

        // First, try to find front facing camera
        Logging.d(TAG, "Looking for front facing cameras.");
        for (String deviceName : deviceNames) {
            if (enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating front facing camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        // Front facing camera not found, try something else
        Logging.d(TAG, "Looking for other cameras.");
        for (String deviceName : deviceNames) {
            if (!enumerator.isFrontFacing(deviceName)) {
                Logging.d(TAG, "Creating other camera capturer.");
                VideoCapturer videoCapturer = enumerator.createCapturer(deviceName, null);

                if (videoCapturer != null) {
                    return videoCapturer;
                }
            }
        }

        return null;
    }

    public void getIceServers() {
        final String API_ENDPOINT = "https://global.xirsys.net";

        Log.d(TAG, "getIceServers");

        byte[] data = new byte[0];
        data = ("<xirsys_ident>:<xirsys_secret>").getBytes(StandardCharsets.UTF_8);
        Log.d(TAG, "getIceServers2");

        String authToken = "Basic " + Base64.encodeToString(data, Base64.NO_WRAP);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        Log.d(TAG, "getIceServers3");
        TurnServer turnServer = retrofit.create(TurnServer.class);
        Log.d(TAG, "getIceServers4");
        turnServer.getIceCandidates(authToken).enqueue(new Callback<TurnServerPojo>() {
            @Override
            public void onResponse(@NonNull Call<TurnServerPojo> call,
                                   @NonNull Response<TurnServerPojo> response) {
                Log.d(TAG, "getIceServers Response");
                TurnServerPojo body = response.body();
                if (body != null)
                    iceServers = body.iceServerList.iceServers;

                Log.d(TAG, "getIceServers iceServers=" + iceServers);

                for (IceServer iceServer : iceServers) {
                    if (iceServer.credential == null) {
                        PeerConnection.IceServer peerIceServer = PeerConnection.IceServer
                                .builder(iceServer.url).createIceServer();
                        peerIceServers.add(peerIceServer);
                    } else {
                        PeerConnection.IceServer peerIceServer = PeerConnection.IceServer
                                .builder(iceServer.url)
                                .setUsername(iceServer.username)
                                .setPassword(iceServer.credential)
                                .createIceServer();
                        peerIceServers.add(peerIceServer);
                    }
                }
                Log.d(TAG, "IceServers:\n" + iceServers.toString());
            }

            @Override
            public void onFailure(@NonNull Call<TurnServerPojo> call, @NonNull Throwable t) {
                t.printStackTrace();
            }
        });
    }

    private void startRotationDetector() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Rotation detector start");
                display.getRealMetrics(screenMetrics);
                while (true) {
                    DisplayMetrics metrics = new DisplayMetrics();
                    display.getRealMetrics(metrics);
                    if (metrics.widthPixels != screenMetrics.widthPixels ||
                            metrics.heightPixels != screenMetrics.heightPixels) {
                        Log.d(TAG, "Rotation detected\n" + "w=" + metrics.widthPixels + " h=" +
                                metrics.heightPixels + " d=" + metrics.densityDpi);
                        screenMetrics = metrics;
                        if (videoCapturer != null) {
                            try {
                                videoCapturer.stopCapture();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            videoCapturer.startCapture(screenMetrics.widthPixels,
                                    screenMetrics.heightPixels, FRAMES_PER_SECOND);
                        }
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        Log.d(TAG, "Rotation detector exit");
                        Thread.interrupted();
                        break;
                    }
                }
            }
        };
        rotationDetectorThread = new Thread(runnable);
        rotationDetectorThread.start();
    }

    private void stopRotationDetector() {
        rotationDetectorThread.interrupt();
    }
}
