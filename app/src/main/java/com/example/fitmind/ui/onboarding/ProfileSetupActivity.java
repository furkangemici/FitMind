package com.example.fitmind.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitmind.R;
import com.example.fitmind.db.AppDatabase;
import com.example.fitmind.model.UserProfile;
import com.example.fitmind.ui.main.MainContainerActivity;
import com.example.fitmind.util.CalorieCalculator;
import com.example.fitmind.util.SharedPrefsManager;
import com.example.fitmind.util.WaterCalculator;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.Executors;

public class ProfileSetupActivity extends AppCompatActivity {

    private TextInputEditText etName, etAge, etWeight, etHeight, etGymDays;
    private TextInputEditText etTargetWeight, etTargetWeeks;
    private RadioGroup rgGender, rgGoal;
    private MaterialButton btnSave;
    private LinearLayout layoutTargetFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_setup);

        etName = findViewById(R.id.etName);
        etAge = findViewById(R.id.etAge);
        etWeight = findViewById(R.id.etWeight);
        etHeight = findViewById(R.id.etHeight);
        etGymDays = findViewById(R.id.etGymDays);
        etTargetWeight = findViewById(R.id.etTargetWeight);
        etTargetWeeks = findViewById(R.id.etTargetWeeks);
        rgGoal = findViewById(R.id.rgGoal);
        rgGender = findViewById(R.id.rgGender);
        btnSave = findViewById(R.id.btnSaveProfile);
        layoutTargetFields = findViewById(R.id.layoutTargetFields);

        rgGoal.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.rbStayFit) {
                layoutTargetFields.setVisibility(View.GONE);
            } else {
                layoutTargetFields.setVisibility(View.VISIBLE);
            }
        });

        btnSave.setOnClickListener(v -> saveProfile());
    }

    private void saveProfile() {
        String name = getText(etName);
        String ageStr = getText(etAge);
        String weightStr = getText(etWeight);
        String heightStr = getText(etHeight);
        String gymDaysStr = getText(etGymDays);
        String targetWeightStr = getText(etTargetWeight);
        String targetWeeksStr = getText(etTargetWeeks);

        if (name.isEmpty() || ageStr.isEmpty() || weightStr.isEmpty() || heightStr.isEmpty() || gymDaysStr.isEmpty()) {
            Toast.makeText(this, R.string.error_empty_fields, Toast.LENGTH_SHORT).show();
            return;
        }

        String gender = rgGender.getCheckedRadioButtonId() == R.id.rbMale ? "male" : "female";
        
        String goal = "lose_weight";
        int checkedGoalId = rgGoal.getCheckedRadioButtonId();
        if (checkedGoalId == R.id.rbGainMuscle) {
            goal = "gain_muscle";
        } else if (checkedGoalId == R.id.rbStayFit) {
            goal = "stay_fit";
        }

        UserProfile profile = new UserProfile();
        profile.name = name;
        profile.age = Integer.parseInt(ageStr);
        profile.weight = Float.parseFloat(weightStr);
        profile.height = Float.parseFloat(heightStr);
        profile.chest = 0;
        profile.waist = 0;
        profile.hip = 0;
        profile.gender = gender;
        profile.goal = goal;
        profile.gymDaysPerWeek = Integer.parseInt(gymDaysStr);
        profile.dailyCalorie = CalorieCalculator.calculateDailyCalorie(
                profile.weight, profile.height, profile.age, profile.gender, profile.gymDaysPerWeek);
        profile.dailyWaterGoal = WaterCalculator.getDailyGoalLitre(profile.weight);

        profile.targetWeight = targetWeightStr.isEmpty() ? profile.weight : Float.parseFloat(targetWeightStr);
        profile.targetWeeks = targetWeeksStr.isEmpty() ? 0 : Integer.parseInt(targetWeeksStr);
        profile.targetDailyCalorie = CalorieCalculator.calculateTargetCalorie(
                profile.dailyCalorie, profile.weight, profile.targetWeight, profile.targetWeeks, profile.goal);

        AppDatabase db = AppDatabase.getInstance(this);
        SharedPrefsManager prefs = new SharedPrefsManager(this);

        Executors.newSingleThreadExecutor().execute(() -> {
            long id = db.userDao().insert(profile);
            prefs.saveUserId((int) id);
            prefs.setFirstLaunchDone();

            runOnUiThread(() -> {
                Intent intent = new Intent(this, MainContainerActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        });
    }

    private String getText(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}
