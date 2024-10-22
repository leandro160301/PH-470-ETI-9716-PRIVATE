package com.jws.jwsapi;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.jws.jwsapi.weighing.Weighing;
import com.jws.jwsapi.weighing.WeighingDao;

@Database(entities = {Weighing.class}, version = 2)
public abstract class AppDatabase extends RoomDatabase {

    public abstract WeighingDao weighingDao();
}