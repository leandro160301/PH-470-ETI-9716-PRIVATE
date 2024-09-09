package com.jws.jwsapi.general;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.jws.jwsapi.general.pallet.Pallet;
import com.jws.jwsapi.general.pallet.PalletDao;
import com.jws.jwsapi.general.weighing.Weighing;
import com.jws.jwsapi.general.weighing.WeighingDao;

@Database(entities = {Pallet.class, Weighing.class}, version = 5)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PalletDao palletDao();
    public abstract WeighingDao weighingDao();
}