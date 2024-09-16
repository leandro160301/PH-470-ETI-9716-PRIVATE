package com.jws.jwsapi.core.container;

public class ContainerButtonProviderSingleton {
    private static ContainerButtonProviderSingleton instance;
    private ContainerButtonProvider buttonProvider;

    private ContainerButtonProviderSingleton() {
        // Constructor privado para evitar instanciación directa
    }

    public static ContainerButtonProviderSingleton getInstance() {
        if (instance == null) {
            instance = new ContainerButtonProviderSingleton();
        }
        return instance;
    }

    public ContainerButtonProvider getButtonProvider() {
        return buttonProvider;
    }

    public void setButtonProvider(ContainerButtonProvider buttonProvider) {
        this.buttonProvider = buttonProvider;
    }
}