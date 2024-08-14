package com.jws.jwsapi.base.fragments;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.github.barteksc.pdfviewer.PDFView;
import com.jws.jwsapi.base.activities.MainActivity;
import com.jws.jwsapi.base.adapters.MyRecyclerViewAdapterMultimedia;
import com.jws.jwsapi.R;
import com.jws.jwsapi.base.models.EtiquetasModel;
import com.jws.jwsapi.base.adapters.MyRecyclerViewAdapterEtiquetas;
import com.jws.jwsapi.common.storage.Storage;
import com.jws.jwsapi.utils.Utils;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ImpresorasEtiquetas extends Fragment implements MyRecyclerViewAdapterMultimedia.ItemClickListener {

    Button bt_home,bt_1,bt_2,bt_3,bt_4,bt_5,bt_6;
    MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    Handler mHandler = new Handler();
    String tipo, usbpath = "/storage/udisk0/", usbpath1 = "/storage/udisk1/", usbpath2 = "/storage/udisk2/";
    PDFView pdfView;
    WifiManager wifi;
    File file, root,fileusb = new File(usbpath),fileusb1 = new File(usbpath1),fileusb2 = new File(usbpath2);
    TextView textViewtitulo,tv_version;
    File[] fileArray;
    VideoView vi_view;
    EtiquetasModel[] ListElements = new EtiquetasModel[] {};
    String[] ListElements2 = new String[] {};
    List<EtiquetasModel> ListElementsArrayList=new ArrayList<>(Arrays.asList(ListElements));
    List<String> ListElementsArrayList2=new ArrayList<>(Arrays.asList(ListElements2));
    Boolean stoped=false;

    RecyclerView recycler,recyclerEtiqueta;
    MyRecyclerViewAdapterMultimedia adapter;
    MyRecyclerViewAdapterEtiquetas adapterEtiquetas;
    int i5=0,i6=0,i7=0,i8=0,w = 2;
    public int posicion =0;
    String nombre="",nombreArchivo ="";
    ImageView im_capturas;
    String etiquetaNOMBRE="";


    @Override
    public void onItemClick(View view, int position) {
        posicion=position;
        String etiqueta =openAndReadFile(ListElementsArrayList2.get(position));
        if(etiqueta!=null&& !etiqueta.equals("")){
            ListElementsArrayList= new ArrayList<>();
            // mainActivity.Mensaje(etiqueta,R.layout.item_customtoastok);
            String[] arr = etiqueta.split("\\^FN");

            for(int i=1;i<arr.length;i++){
                String []arr2= arr[i].split("\\^FS");
                if(arr2.length>1){
                    System.out.println("var campo:"+arr2[0]);//este string luego debemos reemplazar por FS+valorvariable con el .replace
                    String []arr3= arr2[0].split("\"");
                    if(arr3.length>1){
                        ListElementsArrayList.add(new EtiquetasModel(arr3[1],0));
                        // System.out.println("var csdasdsao:"+arr3[1]);

                    }
                    // mainActivity.Mensaje(arr[1],R.layout.item_customtoastok);
                }

            }
            if(ListElementsArrayList.size()>0){
                recyclerEtiqueta.setLayoutManager(new LinearLayoutManager(getContext()));
                etiquetaNOMBRE=ListElementsArrayList2.get(position);
                adapterEtiquetas = new MyRecyclerViewAdapterEtiquetas(getContext(), ListElementsArrayList,mainActivity,ListElementsArrayList2.get(position),recycler,posicion);
                //   adapterEtiquetas.setClickListener(this);
                recyclerEtiqueta.setAdapter(adapterEtiquetas);
            }
        }


    }


    public void SeleccionarItem(int numero){
        recycler.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                try {
                    Objects.requireNonNull(recycler.findViewHolderForAdapterPosition(numero)).itemView.performClick();
                    recycler.getViewTreeObserver().removeOnPreDrawListener(this);
                    // listaproductos.scrollToPosition(numero);
                    //listaproductos.scrollToPosition(ListElementsArrayList.size()-1);


                }catch (Exception exception) {
                    exception.printStackTrace();
                }

                return true;
            }

        });

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recycler.setLayoutManager(layoutManager);
        layoutManager.scrollToPosition(numero);

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.standar_impresoras_etiquetas,container,false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        return view;
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        configuracionBotones();

        recycler =view.findViewById(R.id.listview);
        recyclerEtiqueta=view.findViewById(R.id.recyclerEtiqueta);
        tipo = ".prn";
        seteo(tipo);


    }

    public String openAndReadFile(String archivo) {
        // Ruta del archivo que quieres leer
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

                // Ahora tienes el contenido del archivo en la variable fileContent
                // Puedes mostrarlo en un TextView, en un Toast, etc.
                //mainActivity.Mensaje(fileContent,R.layout.item_customtoastok);

            } catch (IOException e) {
                e.printStackTrace();
                Utils.Mensaje("Error al intentar leer la etiqueta"+ e.toString(),R.layout.item_customtoasterror,mainActivity);
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

    private void configuracionBotones() {
        if (buttonProvider != null) {
            bt_home = buttonProvider.getButtonHome();
            bt_1 = buttonProvider.getButton1();
            bt_2 = buttonProvider.getButton2();
            bt_3 = buttonProvider.getButton3();
            bt_4 = buttonProvider.getButton4();
            bt_5 = buttonProvider.getButton5();
            bt_6 = buttonProvider.getButton6();
            buttonProvider.getTitulo().setText("CONFIGURACION ETIQUETAS");

            bt_1.setBackgroundResource(R.drawable.boton_guardado_i);
            bt_2.setBackgroundResource(R.drawable.boton_impresora_i);
            bt_3.setBackgroundResource(R.drawable.boton_pdf_i);
            bt_4.setBackgroundResource(R.drawable.boton_video_i);
            bt_5.setBackgroundResource(R.drawable.boton_camara_i);

            //bt_1.setVisibility(View.INVISIBLE);
            bt_2.setVisibility(View.INVISIBLE);
            bt_3.setVisibility(View.INVISIBLE);
            bt_4.setVisibility(View.INVISIBLE);
            bt_5.setVisibility(View.INVISIBLE);
            bt_6.setVisibility(View.INVISIBLE);

            bt_1.setOnClickListener(view -> {
                if(adapterEtiquetas!=null){
                    if(adapterEtiquetas.ListElementsInt!=null){
                        mainActivity.mainClass.preferencesManager.saveListSpinner(adapterEtiquetas.ListElementsInternaInt,adapterEtiquetas.etiqueta);
                    }
                    if(adapterEtiquetas.ListElementsInternaFijo!=null){
                        mainActivity.mainClass.preferencesManager.saveListFijo(adapterEtiquetas.ListElementsInternaFijo,adapterEtiquetas.etiqueta);
                    }

                }
            });

            bt_2.setOnClickListener(view -> {
                if (fileusb.isDirectory()) {
                    Storage.copyFileProgress(file, fileusb);
                }
                if (fileusb1.isDirectory()) {
                    Storage.copyFileProgress(file, fileusb1);
                }
                if (fileusb2.isDirectory()) {
                    Storage.copyFileProgress(file, fileusb2);
                }


                if(!fileusb.isDirectory()&&!fileusb1.isDirectory()&&!fileusb2.isDirectory()){

                    Utils.Mensaje("Pendrive no disponible",R.layout.item_customtoasterror,mainActivity);

                }
                else{
                    Utils.Mensaje("Archivo enviado",R.layout.item_customtoastok,mainActivity);
                }
            });
            bt_3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bt_1.setVisibility(View.INVISIBLE);
                    bt_2.setVisibility(View.INVISIBLE);
                    textViewtitulo.setText("PDF");
                    vi_view.setVisibility(View.INVISIBLE);
                    im_capturas.setVisibility(View.INVISIBLE);
                    tipo = ".pdf";
                    seteo(tipo);
                }
            });
            bt_4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bt_1.setVisibility(View.INVISIBLE);
                    bt_2.setVisibility(View.INVISIBLE);
                    textViewtitulo.setText("MULTIMEDIA");
                    pdfView.setVisibility(View.INVISIBLE);
                    im_capturas.setVisibility(View.INVISIBLE);
                    tipo = ".mp4";
                    seteo(tipo);
                }
            });
            bt_5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bt_1.setVisibility(View.INVISIBLE);
                    bt_2.setVisibility(View.INVISIBLE);
                    textViewtitulo.setText("CAPTURAS");
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
            adapter = new MyRecyclerViewAdapterMultimedia(getContext(), ListElementsArrayList2,mainActivity);
            adapter.setClickListener(this);
            recycler.setAdapter(adapter);
            /*int buscarnum=ListElementsArrayList2.indexOf(mainActivity.getEtiqueta());
            if(buscarnum>=0){
                SeleccionarItem(buscarnum);
            }*/

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
                bt_1.setVisibility(View.VISIBLE);
                bt_2.setVisibility(View.VISIBLE);
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
                bt_1.setVisibility(View.VISIBLE);
            }

        }
        if (tipo.equals(".png")) {
            pdfView.setVisibility(View.INVISIBLE);
            vi_view.setVisibility(View.INVISIBLE);
            file = new File(archivocompleto);
            w = 1;
            if(buttonProvider!=null){
                bt_1.setVisibility(View.VISIBLE);
            }
            im_capturas.setVisibility(View.VISIBLE);
            File fileImagen= new File(archivocompleto);
            if(fileImagen.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(archivocompleto);
                im_capturas.setImageBitmap(myBitmap);
            }

        }

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



}


