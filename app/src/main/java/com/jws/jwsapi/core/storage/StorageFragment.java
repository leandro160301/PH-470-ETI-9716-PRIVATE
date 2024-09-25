package com.jws.jwsapi.core.storage;

import static com.jws.jwsapi.core.storage.Storage.getFilesExtension;
import static com.jws.jwsapi.core.storage.StoragePaths.MEMORY_PATH;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.databinding.StandarManualesBinding;
import com.jws.jwsapi.utils.AdapterCommon;
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
        String filePath = MEMORY_PATH.concat(adapter.getItem(position));
        if (!extension.equals(".csv")) {
            loadFile(filePath);
        } else {
            file = new File(filePath);
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
        super.onViewCreated(view, savedInstanceState);
        mainActivity = (MainActivity) getActivity();
        setupButtons();
        setupRecycler(extension);
        hideVideoImage(View.INVISIBLE);
    }

    private void hideVideoImage(int invisible) {
        binding.videoView.setVisibility(View.INVISIBLE);
        binding.imageViewCapturas.setVisibility(invisible);
    }

    private void setupButtons() {
        if (buttonProvider != null) {
            buttonProvider.getTitle().setText(R.string.title_file_fragment);
            handleButtonProvider(buttonProvider.getButton1(), R.drawable.boton_pendrive_on_i, View.INVISIBLE, null);
            handleButtonProvider(buttonProvider.getButton2(), R.drawable.boton_impresora_i, View.INVISIBLE, null);
            handleButtonProvider(buttonProvider.getButton3(), R.drawable.boton_pdf_i, View.INVISIBLE, view -> setupAsPdf());
            handleButtonProvider(buttonProvider.getButton4(), R.drawable.boton_video_i, View.INVISIBLE, view -> setupAsMp4());
            handleButtonProvider(buttonProvider.getButton5(), R.drawable.boton_camara_i, View.INVISIBLE, view -> setupAsPng());
            handleButtonProvider(buttonProvider.getButton6(), null, View.INVISIBLE, null);
            handleButtonProvider(buttonProvider.getButtonHome(), null, null, view -> mainActivity.mainClass.openFragmentPrincipal());
        }
    }

    private void handleButtonProvider(Button button, Integer resid, Integer visibility, View.OnClickListener clickListener) {
        if (resid != null) button.setBackgroundResource(resid);
        if (visibility != null) button.setVisibility(visibility);
        if (clickListener != null) button.setOnClickListener(clickListener);
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
        File fileImage = new File(filePath);
        if (fileImage.exists()) {
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


