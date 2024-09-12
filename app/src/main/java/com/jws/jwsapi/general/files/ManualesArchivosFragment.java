package com.jws.jwsapi.general.files;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.barteksc.pdfviewer.PDFView;
import com.jws.jwsapi.general.MainActivity;

import com.jws.jwsapi.R;
import com.jws.jwsapi.general.adapter.AdapterMultimedia;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ManualesArchivosFragment extends Fragment implements AdapterMultimedia.ItemClickListener {

    Button bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6;
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    Handler mHandler = new Handler();
    String tipo, usbpath = "/storage/udisk0/", usbpath1 = "/storage/udisk1/", usbpath2 = "/storage/udisk2/";
    PDFView pdfView;
    WifiManager wifi;
    File file, root,fileusb = new File(usbpath),fileusb1 = new File(usbpath1),fileusb2 = new File(usbpath2);
    File[] fileArray;
    VideoView vi_view;
    String[] ListElements = new String[] {};
    String[] ListElements2 = new String[] {};
    List<String> ListElementsArrayList=new ArrayList<>(Arrays.asList(ListElements));
    List<String> ListElementsArrayList2=new ArrayList<>(Arrays.asList(ListElements2));
    Boolean stoped=false;

    RecyclerView recycler;
    AdapterMultimedia adapter;
    int i5=0,i6=0,i7=0,i8=0,w = 2;
    int posicion =0;
    String nombre="",nombreArchivo ="";
    ImageView im_capturas;


    @Override
    public void onItemClick(View view, int position) {

        nombreArchivo = adapter.getItem(position);
        nombreArchivo = nombreArchivo.replace(".pdf","");
        String archivo = "/storage/emulated/0/Memoria/";
        String archivocompleto = archivo.concat(adapter.getItem(position));
        nombre= adapter.getItem(position);
        posicion =position;

        if (!tipo.equals(".csv")) {
            carga(archivocompleto);

        } else {
            file = new File(archivocompleto);
            if(buttonProvider!=null){
                bt_1.setVisibility(View.VISIBLE);
            }

        }
    }





    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standar_manuales,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        configuracionBotones();

        recycler =view.findViewById(R.id.listview);
        bt_home= view.findViewById(R.id.bt_home);
        im_capturas = view.findViewById(R.id.imageViewCapturas);
        tipo = ".pdf";
        seteo(tipo);

        im_capturas.setVisibility(View.INVISIBLE);
        pdfView = view.findViewById(R.id.pdfviewer);
        vi_view = view.findViewById(R.id.videoView);
        vi_view.setVisibility(View.INVISIBLE);

        mToastRunnable.run();





    }

    private void configuracionBotones() {
        if (buttonProvider != null) {
            bt_home = buttonProvider.getButtonHome();
            bt_1 = buttonProvider.getButton1();
            bt_2 = buttonProvider.getButton2();
            bt_3 = buttonProvider.getButton3();
            bt_4 = buttonProvider.getButton4();
            bt_5 = buttonProvider.getButton5();
            bt_6 = buttonProvider.getButton6();
            buttonProvider.getTitulo().setText("MANUALES Y ARCHIVOS");

            bt_1.setBackgroundResource(R.drawable.boton_pendrive_on_i);
            bt_2.setBackgroundResource(R.drawable.boton_impresora_i);
            bt_3.setBackgroundResource(R.drawable.boton_pdf_i);
            bt_4.setBackgroundResource(R.drawable.boton_video_i);
            bt_5.setBackgroundResource(R.drawable.boton_camara_i);

            bt_1.setVisibility(View.INVISIBLE);
            bt_2.setVisibility(View.INVISIBLE);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);

            bt_2.setOnClickListener(view -> {
            });
            bt_3.setOnClickListener(view -> {
                bt_1.setVisibility(View.INVISIBLE);
                bt_2.setVisibility(View.INVISIBLE);

                vi_view.setVisibility(View.INVISIBLE);
                im_capturas.setVisibility(View.INVISIBLE);
                tipo = ".pdf";
                seteo(tipo);
            });
            bt_4.setOnClickListener(view -> {
                bt_1.setVisibility(View.INVISIBLE);
                bt_2.setVisibility(View.INVISIBLE);

                pdfView.setVisibility(View.INVISIBLE);
                im_capturas.setVisibility(View.INVISIBLE);
                tipo = ".mp4";
                seteo(tipo);
            });
            bt_5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bt_1.setVisibility(View.INVISIBLE);
                    bt_2.setVisibility(View.INVISIBLE);
                    pdfView.setVisibility(View.INVISIBLE);
                    vi_view.setVisibility(View.INVISIBLE);
                    im_capturas.setVisibility(View.VISIBLE);
                    tipo = ".png";
                    seteo(tipo);
                }
            });
            bt_home.setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());

        }
    }
    Runnable mToastRunnable = new Runnable() {
        @Override
        public void run() {


            wifi = (WifiManager) requireContext().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi.isWifiEnabled()) {

                if(i7==0){
                    i7=1;
                    if(buttonProvider!=null){
                        bt_2.setBackgroundResource(R.drawable.boton_impresora_i);
                    }


                    i8=0;
                }
            }
            else{

                if(i8==0){
                    i8=1;
                    if(buttonProvider!=null){
                        bt_2.setBackgroundResource(R.drawable.boton_impresora_off_i);
                    }

                    i7=0;
                }
            }

            if (fileusb.isDirectory() || fileusb1.isDirectory() || fileusb2.isDirectory()) {

                if(i5==0){
                    i5=1;
                    if(buttonProvider!=null){
                        bt_1.setBackgroundResource(R.drawable.boton_pendrive_on_i);
                    }


                    i6=0;
                }

            } else {
                if(i6==0){
                    i6=1;
                    if(buttonProvider!=null){
                        bt_1.setBackgroundResource(R.drawable.boton_pendrive_i);
                    }

                    i5=0;
                }

            }
            if(!stoped){
                mHandler.postDelayed(this, 2000);
            }


        }
    };

    public void seteo(String tipo) {

        ListElementsArrayList2=new ArrayList<>(Arrays.asList(ListElements2));

        root = new File(Environment.getExternalStorageDirectory().toString()+"/Memoria");

        if(root.exists()){
            fileArray = root.listFiles((dir, filename) -> filename.toLowerCase().endsWith(tipo));

            StringBuilder f = new StringBuilder();


            if(fileArray.length>0){

                for(int i=0; i < fileArray.length; i++){

                    f.append(fileArray[i].getName());

                    ListElementsArrayList2.add(f.toString());
                    f = new StringBuilder();


                }

            }

            recycler.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new AdapterMultimedia(getContext(), ListElementsArrayList2);
            adapter.setClickListener(this);
            recycler.setAdapter(adapter);

        }


    }

    public void carga(String archivocompleto) {

        if (tipo.equals(".pdf")) {
            vi_view.setVisibility(View.INVISIBLE);
            pdfView.setVisibility(View.VISIBLE);
            file = new File(archivocompleto);
            pdfView.recycle();
            w = 0;
            pdfView.fromFile(file);
            // mToastRunnable.run();
            pdfView.fromFile(file).load();
            if(buttonProvider!=null){
               // bt_1.setVisibility(View.VISIBLE);
               // bt_2.setVisibility(View.VISIBLE);
            }

        }

        if (tipo.equals(".mp4")) {
            pdfView.setVisibility(View.INVISIBLE);
            vi_view.setVisibility(View.VISIBLE);
            file = new File(archivocompleto);
            vi_view.setVideoPath(archivocompleto);
            w = 1;
            vi_view.start();
            if(buttonProvider!=null){
              //  bt_1.setVisibility(View.VISIBLE);
            }

        }
        if (tipo.equals(".png")) {
            pdfView.setVisibility(View.INVISIBLE);
            vi_view.setVisibility(View.INVISIBLE);
            file = new File(archivocompleto);
            w = 1;
            if(buttonProvider!=null){
               // bt_1.setVisibility(View.VISIBLE);
            }
            //im_capturas.setVisibility(View.VISIBLE);
            File fileImagen= new File(archivocompleto);
            if(fileImagen.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(archivocompleto);
                im_capturas.setImageBitmap(myBitmap);
            }

        }

    }

    @Override
    public void onDestroyView() {
        stoped=true;
        mHandler.removeCallbacks(mToastRunnable);

        super.onDestroyView();
    }



}


