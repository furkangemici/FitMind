package com.example.fitmind.util;

public class WaterCalculator {

    public static float getDailyGoalLitre(float weightKg) {
        return weightKg * 0.033f;
    }

    public static float getDailyGoalWithActivity(float weightKg, boolean isWorkoutDay) {
        float base = getDailyGoalLitre(weightKg);
        return isWorkoutDay ? base + 0.5f : base;
    }
}
