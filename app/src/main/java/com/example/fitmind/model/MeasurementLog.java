package com.example.fitmind.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "measurement_logs")
public class MeasurementLog {
    @PrimaryKey
    @NonNull
    public String date = ""; // Format: "yyyy-MM-dd"

    public float weight;
    public float neck;
    public float waist;
    public float hip;
    public float bodyFatPercentage;
}
