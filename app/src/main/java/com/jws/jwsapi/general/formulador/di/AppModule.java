package com.jws.jwsapi.general.formulador.di;

import android.app.Application;
import android.content.Context;

import com.jws.jwsapi.common.users.UsersManager;
import com.jws.jwsapi.general.formulador.MainFormClass;
import com.jws.jwsapi.general.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.general.formulador.data.sql.DatabaseHelper;

import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides //se usa siempre en modulo para una nueva instancia
    @Singleton //instancia unica en toda la aplicacion
    public RecetaManager provideRecetaManager(PreferencesManager preferencesManager) {
        return new RecetaManager(preferencesManager);
    }


    @Provides
    public DatabaseHelper provideFormSqlHelper(@ApplicationContext Context context) {
        return new DatabaseHelper(context, MainFormClass.DB_NAME, null, MainFormClass.DB_VERSION);
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