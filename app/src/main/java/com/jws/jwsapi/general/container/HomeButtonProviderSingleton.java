package com.jws.jwsapi.general.container;

public class HomeButtonProviderSingleton {
    private static HomeButtonProviderSingleton instance;
    private HomeButtonProvider buttonProvider;

    private HomeButtonProviderSingleton() {
        // Constructor privado para evitar instanciación directa
    }

    public static HomeButtonProviderSingleton getInstance() {
        if (instance == null) {
            instance = new HomeButtonProviderSingleton();
        }
        return instance;
    }

    public HomeButtonProvider getButtonProvider() {
        return buttonProvider;
    }

    public void setButtonProvider(HomeButtonProvider buttonProvider) {
        this.buttonProvider = buttonProvider;
    }
}