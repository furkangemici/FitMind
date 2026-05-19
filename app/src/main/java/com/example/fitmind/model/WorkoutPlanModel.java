package com.example.fitmind.model;

import java.util.List;

public class WorkoutPlanModel {
    public String routineName;
    public List<WorkoutDay> days;

    public static class WorkoutDay {
        public String dayName;
        public List<Exercise> exercises;
    }

    public static class Exercise {
        public String name;
        public String sets;
        public String reps;
        public boolean isCompleted = false; // For session tracking
    }
}
