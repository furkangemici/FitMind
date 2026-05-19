package com.example.fitmind.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "water_log")
public class WaterLog {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public float amountMl;
    public String date;         // "2026-05-17"
    public long timestamp;
}
