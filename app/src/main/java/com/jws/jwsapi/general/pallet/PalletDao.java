package com.jws.jwsapi.general.pallet;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import java.util.List;

@Dao
public interface PalletDao {

    @Query("SELECT * FROM pallet")
    LiveData<List<Pallet>> getAllPallets();

    @Query("SELECT * FROM pallet WHERE id = :id")
    LiveData<Pallet> getPalletById(int id);

    @Query("DELETE FROM pallet")
    void deleteAllPallets();

    @Query("DELETE FROM pallet WHERE id = :id")
    void deletePalletById(int id);

    @Insert
    void insertPallet(Pallet pallet);

    @Query("UPDATE pallet SET is_closed = :isClosed WHERE id = :id")
    void updatePalletClosedStatus(int id, boolean isClosed);

}