package com.jws.jwsapi.utils.file;

import static com.jws.jwsapi.core.storage.StoragePaths.MEMORY_PATH;

import com.jws.jwsapi.MainActivity;
import com.jws.jwsapi.R;
import com.jws.jwsapi.utils.ToastHelper;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FileUtils {
    public static List<String> getAllFiles() {
        List<String> lista = new ArrayList<>();
        File root = new File(MEMORY_PATH);

        if (root.exists()) {
            Set<String> extensions = new HashSet<>(Arrays.asList(".pdf", ".png", ".xls", ".csv", ".jpg", ".prn", ".lbl", ".nlbl"));
            File[] files = root.listFiles();
            if (files != null) {
                lista = Arrays.stream(files)
                        .map(File::getName)
                        .filter(name -> extensions.stream().anyMatch(ext -> name.toLowerCase().endsWith(ext)))
                        .collect(Collectors.toList());
            }
        }
        return lista;
    }

    @SuppressWarnings("all")
    public static String loadFileAsString(String filename) throws java.io.IOException {
        final int BUFLEN = 1024;
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(filename), BUFLEN);
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream(BUFLEN);
            byte[] bytes = new byte[BUFLEN];
            boolean isUTF8 = false;
            int read, count = 0;
            while ((read = is.read(bytes)) != -1) {
                if (count == 0 && bytes[0] == (byte) 0xEF && bytes[1] == (byte) 0xBB && bytes[2] == (byte) 0xBF) {
                    isUTF8 = true;
                    baos.write(bytes, 3, read - 3);
                } else {
                    baos.write(bytes, 0, read);
                }
                count += read;
            }
            return isUTF8 ? baos.toString(String.valueOf(StandardCharsets.UTF_8)) : baos.toString();
        } finally {
            try {
                is.close();
            } catch (Exception ignored) {
            }
        }
    }

    public static String openAndReadFile(String fileName, MainActivity mainActivity) {
        String filePath = MEMORY_PATH + fileName;
        File file = new File(filePath);
        if (!file.exists()) {
            ToastHelper.message(mainActivity.getString(R.string.toast_file_not_avaible), R.layout.item_customtoasterror, mainActivity);
            return "";
        } else {
            String fileContent = "";
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

            } catch (IOException e) {
                e.printStackTrace();
                ToastHelper.message(mainActivity.getString(R.string.toast_read_file_error) + e, R.layout.item_customtoasterror, mainActivity);
            } finally {
                try {
                    if (br != null) br.close();
                    if (isr != null) isr.close();
                    if (fis != null) fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return fileContent;
        }

    }

    @SuppressWarnings("all")
    public static void createMemoryDirectory() {
        File memoryFile = new File(MEMORY_PATH);
        if (!memoryFile.isDirectory()) {
            memoryFile.mkdir();
        }
    }

    @SuppressWarnings("all")
    public void copy(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);

        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0) {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

}
