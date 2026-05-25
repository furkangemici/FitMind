package com.example.fitmind.util;

public class CalorieCalculator {

    public static int calculateBMR(float weight, float height, int age, String gender) {
        if (gender.equals("male")) {
            return (int) (88.36 + (13.4 * weight) + (4.8 * height) - (5.7 * age));
        } else {
            return (int) (447.6 + (9.2 * weight) + (3.1 * height) - (4.3 * age));
        }
    }

    public static int calculateDailyCalorie(float weight, float height,
                                             int age, String gender, int gymDays) {
        int bmr = calculateBMR(weight, height, age, gender);
        float multiplier;
        if (gymDays == 0)      multiplier = 1.2f;
        else if (gymDays <= 2) multiplier = 1.375f;
        else if (gymDays <= 4) multiplier = 1.55f;
        else                   multiplier = 1.725f;
        return (int) (bmr * multiplier);
    }

    public static int calculateTargetCalorie(int normalCalorie, float currentWeight,
                                              float targetWeight, int targetWeeks, String goal) {
        if (targetWeeks <= 0) return normalCalorie;

        switch (goal) {
            case "lose_weight": {
                if (targetWeight >= currentWeight) return normalCalorie;
                float kgToLose = currentWeight - targetWeight;
                float totalDeficit = kgToLose * 7700;
                int dailyDeficit = (int) (totalDeficit / (targetWeeks * 7));
                return Math.max(normalCalorie - dailyDeficit, 1200);
            }
            case "gain_muscle": {
                if (targetWeight <= currentWeight) return normalCalorie + 400;
                float kgToGain = targetWeight - currentWeight;
                float totalSurplus = kgToGain * 7700;
                int dailySurplus = (int) (totalSurplus / (targetWeeks * 7));
                dailySurplus = Math.min(dailySurplus, 700);
                return normalCalorie + dailySurplus;
            }
            case "stay_fit":
            default:
                return normalCalorie;
        }
    }
}
