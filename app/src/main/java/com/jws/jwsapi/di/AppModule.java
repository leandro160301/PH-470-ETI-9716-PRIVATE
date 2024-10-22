package com.jws.jwsapi.di;


import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.room.Room;

import com.android.jws.JwsManager;
import com.jws.jwsapi.AppDatabase;
import com.jws.jwsapi.Constants;
import com.jws.jwsapi.core.data.local.PreferencesHelper;
import com.jws.jwsapi.core.data.local.PreferencesManager;
import com.jws.jwsapi.core.gpio.GpioManager;
import com.jws.jwsapi.core.label.LabelManager;
import com.jws.jwsapi.core.printer.PrinterPreferences;
import com.jws.jwsapi.core.storage.StorageService;
import com.jws.jwsapi.core.user.UserManager;
import com.jws.jwsapi.productionline.ProductionLineManager;
import com.jws.jwsapi.productionline.ProductionLinePreferences;
import com.jws.jwsapi.shared.UserRepository;
import com.jws.jwsapi.shared.WeighRepository;
import com.jws.jwsapi.weighing.WeighingDao;
import com.jws.jwsapi.weighing.WeighingService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {

    @Provides
    @Singleton
    public static StorageService provideStorageService(@ApplicationContext Context context) {
        return new StorageService(context);
    }

    @Provides
    @Singleton
    public UserManager provideUserManager(Application application, PreferencesManager preferencesManager, UserRepository userRepository) {
        return new UserManager(application, preferencesManager, userRepository);
    }

    @Provides
    @Singleton
    public UserRepository provideUserRepository(Application application) {
        return new UserRepository(application);
    }

    @Provides
    @Singleton
    public PreferencesManager providePreferencesManager(PreferencesHelper preferencesHelper) {
        return new PreferencesManager(preferencesHelper);
    }

    @Singleton
    @Provides
    public SharedPreferences provideSharedPreferences(@ApplicationContext Context context) {
        return context.getSharedPreferences(Constants.PREFS_NAME, Context.MODE_PRIVATE);
    }

    @Provides
    @Singleton
    public LabelManager provideLabelManager(PrinterPreferences printerPreferences) {
        return new LabelManager(printerPreferences);
    }

    @Provides
    @Singleton
    public WeighingDao provideWeighingDao(AppDatabase appDatabase) {
        return appDatabase.weighingDao();
    }

    @Provides
    @Singleton
    public AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, Constants.DATABASE_NAME).build();
    }

    @Provides
    public ProductionLinePreferences provideProductionLinePreferences(PreferencesHelper preferencesHelper) {
        return new ProductionLinePreferences(preferencesHelper);
    }

    @Provides
    @Singleton
    public ProductionLineManager provideProductLineManager(ProductionLinePreferences preferences) {
        return new ProductionLineManager(preferences);
    }

    @Provides
    public WeighingService provideWeighingService(WeighingDao weighingDao) {
        return new WeighingService(weighingDao);
    }

    @Provides
    @Singleton
    public WeighRepository provideWeighRepository() {
        return new WeighRepository();
    }

    @Provides
    public JwsManager provideJwsManager(@ApplicationContext Context context) {
        return new JwsManager(context);
    }

    @Provides
    @Singleton
    public GpioManager provideGpioManager(JwsManager jwsManager) {
        return new GpioManager(jwsManager);
    }

}