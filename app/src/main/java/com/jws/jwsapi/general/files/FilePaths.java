package com.jws.jwsapi.general.files;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public abstract class FilePaths {
    public static final List<File> usbMultimediaPaths = Arrays.asList(
            new File("/storage/udisk0"),
            new File("/storage/udisk1"),
            new File("/storage/udisk2")
    );
    public static final List<File> usbPaths = Arrays.asList(
            new File("/storage/udisk0"),
            new File("/storage/udisk1"),
            new File("/storage/udisk2")
    );
    public static final List<File> apks = Arrays.asList(
            new File("/storage/udisk0/instalacion/jwsapi.apk"),
            new File("/storage/udisk1/instalacion/jwsapi.apk"),
            new File("/storage/udisk2/instalacion/jwsapi.apk")
    );
    public static final String memoryPath = "/storage/emulated/0/Memoria/";
}
