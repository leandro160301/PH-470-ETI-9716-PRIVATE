package com.jws.jwsapi.general;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.jws.jwsapi.general.pallet.Pallet;
import com.jws.jwsapi.general.pallet.PalletDao;

@Database(entities = {Pallet.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract PalletDao palletDao();
}