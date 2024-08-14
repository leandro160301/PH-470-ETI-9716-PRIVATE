package com.jws.jwsapi.feature.formulador.di;

import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides //se usa siempre en modulo para una nueva instancia
    @Singleton //instancia unica en toda la aplicacion
    public RecetaManager provideRecetaManager() {
        return new RecetaManager();
    }

}