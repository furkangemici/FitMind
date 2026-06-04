package com.example.fitmind.ui.profile;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitmind.R;
import com.example.fitmind.db.AppDatabase;
import com.example.fitmind.model.UserProfile;
import com.example.fitmind.ui.onboarding.WelcomeActivity;
import com.example.fitmind.util.SharedPrefsManager;
import com.google.android.material.button.MaterialButton;

import java.util.Locale;
import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private TextView tvProfileName, tvProfileAgeGender;
    private TextView tvWeight, tvHeight, tvBmi, tvProfileBodyFat;
    private TextView tvGoalText, tvDailyCalorie, tvGymDays;
    private MaterialButton btnResetData;

    private AppDatabase db;
    private SharedPrefsManager prefs;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        tvProfileName = view.findViewById(R.id.tvProfileName);
        tvProfileAgeGender = view.findViewById(R.id.tvProfileAgeGender);
        tvWeight = view.findViewById(R.id.tvWeight);
        tvHeight = view.findViewById(R.id.tvHeight);
        tvBmi = view.findViewById(R.id.tvBmi);
        tvProfileBodyFat = view.findViewById(R.id.tvProfileBodyFat);
        tvGoalText = view.findViewById(R.id.tvGoalText);
        tvDailyCalorie = view.findViewById(R.id.tvDailyCalorie);
        tvGymDays = view.findViewById(R.id.tvGymDays);
        btnResetData = view.findViewById(R.id.btnResetData);

        db = AppDatabase.getInstance(getContext());
        prefs = new SharedPrefsManager(requireContext());

        loadUserProfile();

        btnResetData.setOnClickListener(v -> showResetDialog());

        return view;
    }

    private void loadUserProfile() {
        Executors.newSingleThreadExecutor().execute(() -> {
            UserProfile user = db.userDao().getLatestProfile();
            if (user != null && getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    tvProfileName.setText(user.name);
                    
                    String genderText = user.gender.equals("male") ? "Erkek" : "Kadın";
                    tvProfileAgeGender.setText(user.age + " Yaş | " + genderText);

                    tvWeight.setText(String.format(Locale.getDefault(), "%.1f kg", user.weight));
                    tvHeight.setText(String.format(Locale.getDefault(), "%.0f cm", user.height));

                    // BMI Hesabı: Kilo / (Boy * Boy) [Boy metre cinsinden]
                    float heightInMeters = user.height / 100f;
                    float bmi = user.weight / (heightInMeters * heightInMeters);
                    tvBmi.setText(String.format(Locale.getDefault(), "%.1f", bmi));

                    String goalTr = "Form Korumak";
                    if (user.goal.equals("lose_weight")) goalTr = "Kilo Vermek";
                    else if (user.goal.equals("gain_muscle")) goalTr = "Kas Kazanmak";

                    tvGoalText.setText(goalTr);
                    tvDailyCalorie.setText(user.targetDailyCalorie + " kcal");
                    tvGymDays.setText(user.gymDaysPerWeek + " Gün");
                });
            }

            com.example.fitmind.model.MeasurementLog latestLog = db.measurementDao().getLatest();
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (latestLog != null && latestLog.bodyFatPercentage > 0) {
                        tvProfileBodyFat.setText(String.format(Locale.getDefault(), "%%%.1f", latestLog.bodyFatPercentage));
                    } else {
                        tvProfileBodyFat.setText("%-");
                    }
                });
            }
        });
    }

    private void showResetDialog() {
        new AlertDialog.Builder(getContext())
                .setTitle("⚠️ DİKKAT: Tüm Verileri Sıfırla")
                .setMessage("Bu işlem GERİ ALINAMAZ!\n\nŞu ana kadarki tüm antrenman geçmişin, yediğin yemekler, içtiğin sular ve profilin tamamen silinir. Uygulamaya sıfırdan başlamak istediğine emin misin?")
                .setPositiveButton("Evet, Her Şeyi Sil", (dialog, which) -> resetAndLogout())
                .setNegativeButton("İptal", null)
                .show();
    }

    private void resetAndLogout() {
        Executors.newSingleThreadExecutor().execute(() -> {
            db.clearAllTables();
            prefs.clear();

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    Intent intent = new Intent(getActivity(), WelcomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    getActivity().finish();
                });
            }
        });
    }
}
