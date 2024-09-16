package com.jws.jwsapi.general.storage;

import static com.jws.jwsapi.general.storage.Storage.getFilesExtension;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.StandarManualesBinding;
import com.jws.jwsapi.general.utils.AdapterCommon;
import com.service.Comunicacion.ButtonProvider;
import com.service.Comunicacion.ButtonProviderSingleton;

import java.io.File;

public class StorageFragment extends Fragment implements AdapterCommon.ItemClickListener {

    private MainActivity mainActivity;
    private ButtonProvider buttonProvider;
    private String extension = ".pdf";
    private File file;
    private AdapterCommon adapter;
    private StandarManualesBinding binding;

    @Override
    public void onItemClick(View view, int position) {
        fileSelected(position);
    }

    private void fileSelected(int position) {
        String archivo = "/storage/emulated/0/Memoria/";
        String archivocompleto = archivo.concat(adapter.getItem(position));
        if (!extension.equals(".csv")) {
            loadFile(archivocompleto);
        } else {
            file = new File(archivocompleto);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = StandarManualesBinding.inflate(inflater, container, false);
        buttonProvider = ButtonProviderSingleton.getInstance().getButtonProvider();
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view,savedInstanceState);
        mainActivity=(MainActivity)getActivity();
        configuracionBotones();
        setupRecycler(extension);
        hideVideoImage(View.INVISIBLE);
    }

    private void hideVideoImage(int invisible) {
        binding.videoView.setVisibility(View.INVISIBLE);
        binding.imageViewCapturas.setVisibility(invisible);
    }

    private void configuracionBotones() {
        if (buttonProvider != null) {
            buttonProvider.getTitulo().setText(R.string.title_file_fragment);
            buttonProvider.getButton1().setBackgroundResource(R.drawable.boton_pendrive_on_i);
            buttonProvider.getButton2().setBackgroundResource(R.drawable.boton_impresora_i);
            buttonProvider.getButton3().setBackgroundResource(R.drawable.boton_pdf_i);
            buttonProvider.getButton4().setBackgroundResource(R.drawable.boton_video_i);
            buttonProvider.getButton5().setBackgroundResource(R.drawable.boton_camara_i);

            setupButtonsInvisible();
            buttonProvider.getButton3().setVisibility(View.INVISIBLE);
            buttonProvider.getButton4().setVisibility(View.INVISIBLE);
            buttonProvider.getButton5().setVisibility(View.INVISIBLE);
            buttonProvider.getButton6().setVisibility(View.INVISIBLE);

            buttonProvider.getButton3().setOnClickListener(view -> setupAsPdf());
            buttonProvider.getButton4().setOnClickListener(view -> setupAsMp4());
            buttonProvider.getButton5().setOnClickListener(view -> setupAsPng());
            buttonProvider.getButtonHome().setOnClickListener(view -> mainActivity.mainClass.openFragmentPrincipal());

        }
    }

    private void setupAsPng() {
        setupButtonsInvisible();
        binding.pdfviewer.setVisibility(View.INVISIBLE);
        hideVideoImage(View.VISIBLE);
        extension = ".png";
        setupRecycler(extension);
    }

    private void setupAsMp4() {
        setupButtonsInvisible();
        binding.pdfviewer.setVisibility(View.INVISIBLE);
        binding.imageViewCapturas.setVisibility(View.INVISIBLE);
        extension = ".mp4";
        setupRecycler(extension);
    }

    private void setupAsPdf() {
        setupButtonsInvisible();
        hideVideoImage(View.INVISIBLE);
        extension = ".pdf";
        setupRecycler(extension);
    }

    private void setupButtonsInvisible() {
        buttonProvider.getButton1().setVisibility(View.INVISIBLE);
        buttonProvider.getButton2().setVisibility(View.INVISIBLE);
    }

    public void setupRecycler(String extension) {
        binding.listview.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new AdapterCommon(getContext(), getFilesExtension(extension));
        adapter.setClickListener(this);
        binding.listview.setAdapter(adapter);
    }

    public void loadFile(String filePath) {
        file = new File(filePath);
        switch (extension) {
            case ".pdf":
                showPdf();
                break;
            case ".mp4":
                showMp4(filePath);
                break;
            case ".png":
                showPng(filePath);
                break;
            default:
                break;
        }
    }

    private void showPng(String filePath) {
        binding.pdfviewer.setVisibility(View.INVISIBLE);
        binding.videoView.setVisibility(View.INVISIBLE);
        File fileImagen= new File(filePath);
        if(fileImagen.exists()){
            Bitmap myBitmap = BitmapFactory.decodeFile(filePath);
            binding.imageViewCapturas.setImageBitmap(myBitmap);
        }
    }

    private void showMp4(String filePath) {
        binding.pdfviewer.setVisibility(View.INVISIBLE);
        binding.videoView.setVisibility(View.VISIBLE);
        binding.videoView.setVideoPath(filePath);
        binding.videoView.start();
    }

    private void showPdf() {
        binding.videoView.setVisibility(View.INVISIBLE);
        binding.pdfviewer.setVisibility(View.VISIBLE);
        binding.pdfviewer.recycle();
        binding.pdfviewer.fromFile(file);
        binding.pdfviewer.fromFile(file).load();
    }

}


