package com.example.fitmind.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.fitmind.model.CalorieLog;

import java.util.List;

@Dao
public interface CalorieDao {

    @Insert
    long insert(CalorieLog calorieLog);

    @Query("SELECT * FROM calorie_log WHERE date = :date ORDER BY id DESC")
    List<CalorieLog> getLogsByDate(String date);

    @Query("SELECT COALESCE(SUM(calories), 0) FROM calorie_log WHERE date = :date")
    int getTotalByDate(String date);

    @Query("SELECT DISTINCT date FROM calorie_log ORDER BY date DESC")
    List<String> getAllDates();

    @Query("DELETE FROM calorie_log WHERE id = :id")
    void deleteById(int id);
}
