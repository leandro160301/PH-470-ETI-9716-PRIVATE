package com.jws.jwsapi;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.jws.jwsapi.pallet.Pallet;
import com.jws.jwsapi.pallet.PalletDao;
import com.jws.jwsapi.weighing.Weighing;
import com.jws.jwsapi.weighing.WeighingDao;

@Database(entities = {Pallet.class, Weighing.class}, version = 6)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PalletDao palletDao();
    public abstract WeighingDao weighingDao();
}