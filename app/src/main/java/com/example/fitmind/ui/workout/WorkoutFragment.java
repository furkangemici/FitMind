package com.example.fitmind.ui.workout;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitmind.BuildConfig;
import com.example.fitmind.R;
import com.example.fitmind.api.GroqClient;
import com.example.fitmind.api.model.GroqRequest;
import com.example.fitmind.api.model.GroqResponse;
import com.example.fitmind.db.AppDatabase;
import com.example.fitmind.model.UserProfile;
import com.example.fitmind.model.WorkoutPlanModel;
import com.google.android.material.button.MaterialButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WorkoutFragment extends Fragment {

    private LinearLayout layoutNoPlan, layoutActivePlan;
    private MaterialButton btnCreatePlan, btnFinishWorkout;
    private ImageButton btnRegeneratePlan, btnPrevDay, btnNextDay;
    private TextView tvRoutineName, tvCurrentDayName, tvWeekCounter;
    private RecyclerView rvExercises;
    private ProgressBar progressWorkout;

    private AppDatabase db;
    private UserProfile user;
    private WorkoutPlanModel currentPlan;
    private int currentDayIndex = 0;
    private ExerciseAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_workout, container, false);

        layoutNoPlan = view.findViewById(R.id.layoutNoPlan);
        layoutActivePlan = view.findViewById(R.id.layoutActivePlan);
        btnCreatePlan = view.findViewById(R.id.btnCreatePlan);
        btnFinishWorkout = view.findViewById(R.id.btnFinishWorkout);
        btnRegeneratePlan = view.findViewById(R.id.btnRegeneratePlan);
        btnPrevDay = view.findViewById(R.id.btnPrevDay);
        btnNextDay = view.findViewById(R.id.btnNextDay);
        tvRoutineName = view.findViewById(R.id.tvRoutineName);
        tvCurrentDayName = view.findViewById(R.id.tvCurrentDayName);
        tvWeekCounter = view.findViewById(R.id.tvWeekCounter);
        rvExercises = view.findViewById(R.id.rvExercises);
        progressWorkout = view.findViewById(R.id.progressWorkout);

        db = AppDatabase.getInstance(getContext());

        rvExercises.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ExerciseAdapter(new ArrayList<>(), this::showAiTipDialog);
        rvExercises.setAdapter(adapter);

        btnCreatePlan.setOnClickListener(v -> generateWorkoutPlan());
        btnRegeneratePlan.setOnClickListener(v -> generateWorkoutPlan());

        btnPrevDay.setOnClickListener(v -> {
            if (currentPlan != null && currentDayIndex > 0) {
                currentDayIndex--;
                displayDay(currentDayIndex);
            }
        });

        btnNextDay.setOnClickListener(v -> {
            if (currentPlan != null && currentDayIndex < currentPlan.days.size() - 1) {
                currentDayIndex++;
                displayDay(currentDayIndex);
            }
        });

        btnFinishWorkout.setOnClickListener(v -> finishWorkoutSession());

        loadUserData();

        return view;
    }

    private void loadUserData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            user = db.userDao().getLatestProfile();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (user != null && user.currentWorkoutPlanDetail != null && user.currentWorkoutPlanDetail.contains("{")) {
                        try {
                            parsePlanFromJson(user.currentWorkoutPlanDetail);
                            layoutNoPlan.setVisibility(View.GONE);
                            layoutActivePlan.setVisibility(View.VISIBLE);
                            btnRegeneratePlan.setVisibility(View.VISIBLE);
                            displayDay(0);
                        } catch (Exception e) {
                            // Invalid JSON (maybe old markdown data)
                            layoutNoPlan.setVisibility(View.VISIBLE);
                            layoutActivePlan.setVisibility(View.GONE);
                            btnRegeneratePlan.setVisibility(View.GONE);
                        }
                    } else {
                        layoutNoPlan.setVisibility(View.VISIBLE);
                        layoutActivePlan.setVisibility(View.GONE);
                        btnRegeneratePlan.setVisibility(View.GONE);
                    }
                });
            }
        });
    }

    private void parsePlanFromJson(String jsonString) throws Exception {
        if (jsonString.startsWith("```json")) {
            jsonString = jsonString.replace("```json", "").replace("```", "").trim();
        }

        JSONObject root = new JSONObject(jsonString);
        currentPlan = new WorkoutPlanModel();
        currentPlan.routineName = root.getString("routineName");
        currentPlan.days = new ArrayList<>();

        JSONArray daysArray = root.getJSONArray("days");
        for (int i = 0; i < daysArray.length(); i++) {
            JSONObject dayObj = daysArray.getJSONObject(i);
            WorkoutPlanModel.WorkoutDay day = new WorkoutPlanModel.WorkoutDay();
            day.dayName = dayObj.getString("dayName");
            day.exercises = new ArrayList<>();

            JSONArray exArray = dayObj.getJSONArray("exercises");
            for (int j = 0; j < exArray.length(); j++) {
                JSONObject exObj = exArray.getJSONObject(j);
                WorkoutPlanModel.Exercise ex = new WorkoutPlanModel.Exercise();
                ex.name = exObj.getString("name");
                ex.sets = exObj.getString("sets");
                ex.reps = exObj.getString("reps");
                day.exercises.add(ex);
            }
            currentPlan.days.add(day);
        }
    }

    private void displayDay(int index) {
        if (currentPlan == null || currentPlan.days.isEmpty()) return;
        
        tvRoutineName.setText(currentPlan.routineName);
        WorkoutPlanModel.WorkoutDay currentDay = currentPlan.days.get(index);
        tvCurrentDayName.setText(currentDay.dayName);
        
        // Reset checkboxes when viewing a day
        for (WorkoutPlanModel.Exercise ex : currentDay.exercises) {
            ex.isCompleted = false;
        }
        adapter.setExercises(currentDay.exercises);

        btnPrevDay.setAlpha(index == 0 ? 0.3f : 1.0f);
        btnPrevDay.setEnabled(index != 0);

        btnNextDay.setAlpha(index == currentPlan.days.size() - 1 ? 0.3f : 1.0f);
        btnNextDay.setEnabled(index != currentPlan.days.size() - 1);

        if (user != null) {
            tvWeekCounter.setText(String.format(java.util.Locale.getDefault(), 
                "%d. Hafta (%d/%d Gün)", user.currentWorkoutWeek, user.completedWorkoutDaysThisWeek, currentPlan.days.size()));
        }
    }

    private void finishWorkoutSession() {
        if (user == null || currentPlan == null) return;

        user.completedWorkoutDaysThisWeek++;
        
        if (user.completedWorkoutDaysThisWeek >= currentPlan.days.size()) {
            // Haftayı tamamladı
            user.completedWorkoutDaysThisWeek = 0;
            user.currentWorkoutWeek++;
            currentDayIndex = 0;

            new AlertDialog.Builder(getContext())
                .setTitle("Tebrikler!")
                .setMessage("Harika bir iş çıkardın! Bu haftaki tüm antrenmanları tamamladın. Yeni haftaya geçiliyor...")
                .setPositiveButton("Süper", null)
                .show();
        } else {
            // Sadece günü tamamladı
            Toast.makeText(getContext(), "Antrenman kaydedildi! Sıradaki güne geçiliyor...", Toast.LENGTH_SHORT).show();
            if (currentDayIndex < currentPlan.days.size() - 1) {
                currentDayIndex++;
            }
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            db.userDao().update(user);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> displayDay(currentDayIndex));
            }
        });
    }

    private void generateWorkoutPlan() {
        if (user == null) return;

        progressWorkout.setVisibility(View.VISIBLE);
        layoutNoPlan.setVisibility(View.GONE);
        layoutActivePlan.setVisibility(View.GONE);

        String prompt = String.format("Sen profesyonel bir spor eğitmenisin. Kullanıcı Profili: Yaş: %d, Kilo: %.1f kg, Hedef: %s. " +
                "Kullanıcıya %d günlük bir spor salonu antrenman programı yaz. " +
                "SADECE AŞAĞIDAKİ FORMATTA SAF JSON DÖNDÜR, BAŞKA HİÇBİR KELİME YAZMA:\n" +
                "{\n" +
                "  \"routineName\": \"Push Pull Legs (Örnek)\",\n" +
                "  \"days\": [\n" +
                "    {\n" +
                "      \"dayName\": \"1. Gün: İtiş\",\n" +
                "      \"exercises\": [\n" +
                "        {\"name\": \"Bench Press\", \"sets\": \"3\", \"reps\": \"10-12\"},\n" +
                "        {\"name\": \"Overhead Press\", \"sets\": \"3\", \"reps\": \"10\"}\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}", user.age, user.weight, user.goal, user.gymDaysPerWeek);

        GroqRequest request = new GroqRequest(prompt);
        String authHeader = "Bearer " + BuildConfig.GROQ_API_KEY;

        GroqClient.getService().generateContent(authHeader, request).enqueue(new Callback<GroqResponse>() {
            @Override
            public void onResponse(Call<GroqResponse> call, Response<GroqResponse> response) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    progressWorkout.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        String jsonResponse = response.body().getText().trim();
                        try {
                            parsePlanFromJson(jsonResponse);
                            
                            // Save to user profile
                            user.currentWorkoutPlanDetail = jsonResponse;
                            user.currentWorkoutWeek = 1;
                            user.completedWorkoutDaysThisWeek = 0;
                            Executors.newSingleThreadExecutor().execute(() -> db.userDao().update(user));

                            layoutActivePlan.setVisibility(View.VISIBLE);
                            btnRegeneratePlan.setVisibility(View.VISIBLE);
                            currentDayIndex = 0;
                            displayDay(0);
                            
                            Toast.makeText(getContext(), "Plan başarıyla oluşturuldu!", Toast.LENGTH_SHORT).show();
                        } catch (Exception e) {
                            Toast.makeText(getContext(), "API format hatası, tekrar deneyin.", Toast.LENGTH_LONG).show();
                            layoutNoPlan.setVisibility(View.VISIBLE);
                        }
                    } else {
                        Toast.makeText(getContext(), "Hata oluştu", Toast.LENGTH_SHORT).show();
                        layoutNoPlan.setVisibility(View.VISIBLE);
                    }
                });
            }

            @Override
            public void onFailure(Call<GroqResponse> call, Throwable t) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    progressWorkout.setVisibility(View.GONE);
                    layoutNoPlan.setVisibility(View.VISIBLE);
                    Toast.makeText(getContext(), "Bağlantı hatası", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void showAiTipDialog(WorkoutPlanModel.Exercise exercise) {
        progressWorkout.setVisibility(View.VISIBLE);

        String prompt = "Şu spor hareketi hakkında bana tek cümlelik vurucu, form ve güvenlik odaklı bir taktik ver: " + exercise.name;
        GroqRequest request = new GroqRequest(prompt);
        String authHeader = "Bearer " + BuildConfig.GROQ_API_KEY;

        GroqClient.getService().generateContent(authHeader, request).enqueue(new Callback<GroqResponse>() {
            @Override
            public void onResponse(Call<GroqResponse> call, Response<GroqResponse> response) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    progressWorkout.setVisibility(View.GONE);
                    if (response.isSuccessful() && response.body() != null) {
                        String tip = response.body().getText().trim();
                        new AlertDialog.Builder(getContext())
                                .setTitle(exercise.name + " Taktiği")
                                .setMessage(tip)
                                .setPositiveButton("Tamam", null)
                                .show();
                    }
                });
            }

            @Override
            public void onFailure(Call<GroqResponse> call, Throwable t) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> progressWorkout.setVisibility(View.GONE));
            }
        });
    }
}
