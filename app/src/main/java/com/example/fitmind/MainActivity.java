package com.example.fitmind;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitmind.db.AppDatabase;
import com.example.fitmind.ui.main.MainContainerActivity;
import com.example.fitmind.ui.onboarding.WelcomeActivity;
import com.example.fitmind.util.SharedPrefsManager;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPrefsManager prefs = new SharedPrefsManager(this);
        AppDatabase db = AppDatabase.getInstance(this);

        Executors.newSingleThreadExecutor().execute(() -> {
            boolean hasUser = db.userDao().getLatestProfile() != null;

            runOnUiThread(() -> {
                if (prefs.isFirstLaunch() || !hasUser) {
                    prefs.clear();
                    Intent intent = new Intent(this, WelcomeActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(this, MainContainerActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        });
    }
}