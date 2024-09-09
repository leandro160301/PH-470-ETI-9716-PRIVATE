package com.jws.jwsapi.general.weighing;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface WeighingDao {

    @Query("SELECT * FROM weighing")
    LiveData<List<Weighing>> getAllWeighing();

    @Query("SELECT * FROM weighing WHERE id = :id")
    LiveData<Weighing> getWeighingById(int id);

    @Query("DELETE FROM weighing")
    void deleteAllWeighings();

    @Query("DELETE FROM weighing WHERE id = :id")
    void deleteWeighingById(int id);

    @Insert
    void insertWeighing(Weighing weighing);

}
