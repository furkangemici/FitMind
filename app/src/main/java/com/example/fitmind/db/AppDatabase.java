package com.example.fitmind.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.fitmind.db.dao.CalorieDao;
import com.example.fitmind.db.dao.MeasurementDao;
import com.example.fitmind.db.dao.UserDao;
import com.example.fitmind.db.dao.WaterDao;
import com.example.fitmind.model.CalorieLog;
import com.example.fitmind.model.MeasurementLog;
import com.example.fitmind.model.UserProfile;
import com.example.fitmind.model.WaterLog;

@Database(entities = {UserProfile.class, WaterLog.class, CalorieLog.class, MeasurementLog.class}, version = 7, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static volatile AppDatabase INSTANCE;

    public abstract UserDao userDao();
    public abstract WaterDao waterDao();
    public abstract CalorieDao calorieDao();
    public abstract MeasurementDao measurementDao();

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "fitmind_database"
                    )
                    .fallbackToDestructiveMigration()
                    .build();
                }
            }
        }
        return INSTANCE;
    }
}
