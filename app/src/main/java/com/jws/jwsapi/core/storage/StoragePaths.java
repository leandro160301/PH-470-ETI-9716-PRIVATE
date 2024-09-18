package com.jws.jwsapi.core.storage;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class StoragePaths {
    public static final List<File> DIRECTORY_MEMORY_PATHS = Collections.unmodifiableList(Arrays.asList(
            new File("/storage/udisk0"),
            new File("/storage/udisk1"),
            new File("/storage/udisk2")
    ));
    public static final List<File> FILE_APKS = Collections.unmodifiableList(Arrays.asList(
            new File("/storage/udisk0/instalacion/jwsapi.apk"),
            new File("/storage/udisk1/instalacion/jwsapi.apk"),
            new File("/storage/udisk2/instalacion/jwsapi.apk")
    ));
    public static final String MEMORY_PATH = "/storage/emulated/0/Memoria/";
    public static final int USB_CONNECTED = 1;
    public static final int USB_NOT_AVAIBLE = 0;
}
