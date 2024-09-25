package com.jws.jwsapi.utils.file;

import static com.jws.jwsapi.core.storage.StoragePaths.MEMORY_PATH;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileExtensionUtils {

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
