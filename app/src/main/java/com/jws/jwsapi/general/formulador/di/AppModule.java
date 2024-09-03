package com.jws.jwsapi.general.formulador.di;

import static net.sourceforge.jtds.jdbc.DefaultProperties.DATABASE_NAME;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.jws.jwsapi.common.users.UsersManager;
import com.jws.jwsapi.general.AppDatabase;
import com.jws.jwsapi.general.formulador.MainFormClass;
import com.jws.jwsapi.general.formulador.data.preferences.PreferencesManager;
import com.jws.jwsapi.general.formulador.data.sql.DatabaseHelper;
import com.jws.jwsapi.general.pallet.PalletApi;
import com.jws.jwsapi.general.pallet.PalletDao;
import com.jws.jwsapi.general.pallet.PalletService;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import javax.inject.Singleton;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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

    private static final String BASE_URL = "http://10.41.0.78:8080/";

    @Provides
    public Retrofit provideRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }
    @Provides
    public PalletApi providePalletApi(Retrofit retrofit) {
        return retrofit.create(PalletApi.class);
    }

    @Provides
    public PalletDao providePalletDao(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, "database-name").build().palletDao();
    }

    @Provides
    public AppDatabase provideAppDatabase(@ApplicationContext Context context) {
        return Room.databaseBuilder(context, AppDatabase.class, DATABASE_NAME).build();
    }

    @Provides
    public PalletService providePalletService(PalletApi palletApi, PalletDao palletDao) {
        return new PalletService(palletApi, palletDao);
    }

}