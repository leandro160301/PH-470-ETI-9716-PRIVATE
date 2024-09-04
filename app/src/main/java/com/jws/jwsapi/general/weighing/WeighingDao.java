package com.jws.jwsapi.general.weighing;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.jws.jwsapi.general.pallet.Pallet;

import java.util.List;

@Dao
public interface WeighingDao {

    @Query("SELECT * FROM weighing")
    LiveData<List<Pallet>> getAllWeighing();

    @Query("SELECT * FROM weighing WHERE id = :id")
    LiveData<Pallet> getWeighingById(int id);

    @Query("DELETE FROM weighing")
    void deleteAllWeighings();

    @Query("DELETE FROM weighing WHERE id = :id")
    void deleteWeighingById(int id);

    @Insert
    void insertWeighing(Weighing weighing);

    @Query("UPDATE weighing SET total_net = :totalNet WHERE id = :id")
    void updateWeighingTotalNet(int id, String totalNet);
}
