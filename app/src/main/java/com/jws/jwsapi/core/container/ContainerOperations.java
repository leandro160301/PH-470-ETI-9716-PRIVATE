package com.jws.jwsapi.core.container;

public interface ContainerOperations {
    String getIpAdress();

    String getFirmwareVersion();

    void openStorage();

    boolean isStorageActive();
}
