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

    @Query("DELETE FROM pallet WHERE serial_number = :serialNumber")
    void deletePalletBySerialNumber(String serialNumber);

    @Insert
    void insertPallet(Pallet pallet);

    @Query("UPDATE pallet SET is_closed = :isClosed WHERE serial_number = :serialNumber")
    void updatePalletClosedStatus(String serialNumber, boolean isClosed);

    @Query("UPDATE pallet SET done = done + 1 WHERE id = :id")
    void incrementDoneById(int id);

    @Query("UPDATE pallet SET total_net = :totalNet WHERE serial_number = :serialNumber")
    void updatePalletTotalNet(String serialNumber, String totalNet);

}