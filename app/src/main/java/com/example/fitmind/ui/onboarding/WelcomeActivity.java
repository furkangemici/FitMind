package com.example.fitmind.ui.onboarding;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.fitmind.R;
import com.google.android.material.button.MaterialButton;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        MaterialButton btnStart = findViewById(R.id.btnStart);
        btnStart.setOnClickListener(v -> {
            startActivity(new Intent(this, ProfileSetupActivity.class));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            finish();
        });
    }
}
