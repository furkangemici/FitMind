package com.example.fitmind.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "calorie_log")
public class CalorieLog {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String mealName;
    public int calories;
    public int protein;
    public int carbs;
    public int fat;
    public String date;
}
