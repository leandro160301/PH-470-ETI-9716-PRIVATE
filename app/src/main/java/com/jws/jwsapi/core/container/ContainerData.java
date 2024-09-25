package com.jws.jwsapi.core.container;

public interface ContainerData {
    String getIp();

    String getVersion();

    void openStorage();

    boolean getStorageState();
}
