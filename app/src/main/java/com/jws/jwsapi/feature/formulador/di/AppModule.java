package com.jws.jwsapi.feature.formulador.di;

import android.app.Application;
import com.jws.jwsapi.common.users.UsersManager;
import com.jws.jwsapi.feature.formulador.data.preferences.PreferencesManager;
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

    @Provides
    @Singleton
    public UsersManager provideUserManager(Application application){
        return new UsersManager(application);
    }

    @Provides
    @Singleton
    public PreferencesManager providePreferencesManager(Application application) {
        return new PreferencesManager(application);
    }

    @Provides
    @Singleton
    public LabelManager provideLabelManager(PreferencesManager preferencesManager){
        return new LabelManager(preferencesManager);
    }

}