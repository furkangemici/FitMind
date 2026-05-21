package com.example.fitmind.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.fitmind.model.MeasurementLog;

import java.util.List;

@Dao
public interface MeasurementDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MeasurementLog log);

    @Query("SELECT * FROM measurement_logs WHERE date = :date LIMIT 1")
    MeasurementLog getByDate(String date);

    @Query("SELECT * FROM measurement_logs ORDER BY date DESC LIMIT 1")
    MeasurementLog getLatest();

    @Query("SELECT * FROM measurement_logs ORDER BY date ASC")
    List<MeasurementLog> getAll();
}
