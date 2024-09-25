package com.jws.jwsapi.utils;

import static com.jws.jwsapi.core.storage.StoragePaths.MEMORY_PATH;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
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

    public static List<String> getFilesExtension(String extension) {
        List<String> list = new ArrayList<>();
        File root = new File(MEMORY_PATH);
        if (root.exists()) {
            File[] fileArray = root.listFiles((dir, filename) -> filename.toLowerCase().endsWith(extension));
            StringBuilder f = new StringBuilder();
            if (fileArray != null && fileArray.length > 0) {
                for (File value : fileArray) {
                    f.append(value.getName());
                    list.add(f.toString());
                    f = new StringBuilder();
                }
            }
        }
        return list;
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

    public int quantityExtension(String extension) {
        List<String> Lista = new ArrayList<>();
        File root = new File(MEMORY_PATH);
        if (root.exists()) {
            File[] filearr = root.listFiles((dir, filename) -> filename.toLowerCase().endsWith(extension));
            StringBuilder f = new StringBuilder();
            if (filearr != null && filearr.length > 0) {
                for (File value : filearr) {
                    f.append(value.getName());
                    Lista.add(f.toString());
                    f = new StringBuilder();
                }
            }

        }
        return Lista.size();
    }

}
