package com.example.fitmind.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user_profile")
public class UserProfile {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public int age;
    public float weight;
    public float height;
    public float chest;
    public float waist;
    public float hip;
    public String gender;
    public String goal;
    public int gymDaysPerWeek;
    public int dailyCalorie;
    public int targetDailyCalorie;
    public float targetWeight;
    public int targetWeeks;
    public float dailyWaterGoal;

    // AI Planları
    public String currentWorkoutPlanName;
    public String currentWorkoutPlanDetail;
    public String currentNutritionPlanDetail;

    public int currentWorkoutWeek = 1;
    public int completedWorkoutDaysThisWeek = 0;
}
