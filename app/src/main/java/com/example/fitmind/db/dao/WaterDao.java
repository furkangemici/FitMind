package com.example.fitmind.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.fitmind.model.WaterLog;

import java.util.List;

@Dao
public interface WaterDao {

    @Insert
    long insert(WaterLog waterLog);

    @Query("SELECT * FROM water_log WHERE date = :date ORDER BY timestamp DESC")
    List<WaterLog> getLogsByDate(String date);

    @Query("SELECT COALESCE(SUM(amountMl), 0) FROM water_log WHERE date = :date")
    float getTotalByDate(String date);

    @Query("SELECT DISTINCT date FROM water_log ORDER BY date DESC")
    List<String> getAllDates();

    @Query("DELETE FROM water_log WHERE id = :id")
    void deleteById(int id);

    @Query("DELETE FROM water_log WHERE date = :date")
    void deleteByDate(String date);
}
