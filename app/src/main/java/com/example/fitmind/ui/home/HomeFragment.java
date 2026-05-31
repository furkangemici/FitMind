package com.example.fitmind.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.example.fitmind.model.CalorieLog;
import com.example.fitmind.model.UserProfile;
import com.example.fitmind.model.WaterLog;
import com.example.fitmind.ui.nutrition.MealAdapter;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {

    private TextView tvDateDisplay, tvConsumedCalories, tvTargetCalorie;
    private TextView tvTotalProtein, tvTotalCarbs, tvTotalFat;
    private TextView tvWaterAmount;
    private ImageButton btnPrevDay, btnNextDay;
    private MaterialButton btnAddMeal;
    private MaterialButton btnAddWater250, btnAddWater330, btnAddWater500;
    private ImageButton btnRemoveWater;
    private TextInputEditText etMealName;
    private ProgressBar progressMeal;
    private RecyclerView rvMeals;

    private MealAdapter mealAdapter;
    private AppDatabase db;
    private Calendar currentCalendar;
    private SimpleDateFormat dateFormat;
    private SimpleDateFormat displayFormat;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        tvDateDisplay = view.findViewById(R.id.tvDateDisplay);
        tvConsumedCalories = view.findViewById(R.id.tvConsumedCalories);
        tvTargetCalorie = view.findViewById(R.id.tvTargetCalorie);
        tvTotalProtein = view.findViewById(R.id.tvTotalProtein);
        tvTotalCarbs = view.findViewById(R.id.tvTotalCarbs);
        tvTotalFat = view.findViewById(R.id.tvTotalFat);
        tvWaterAmount = view.findViewById(R.id.tvWaterAmount);
        
        btnPrevDay = view.findViewById(R.id.btnPrevDay);
        btnNextDay = view.findViewById(R.id.btnNextDay);
        
        btnAddWater250 = view.findViewById(R.id.btnAddWater250);
        btnAddWater330 = view.findViewById(R.id.btnAddWater330);
        btnAddWater500 = view.findViewById(R.id.btnAddWater500);
        btnRemoveWater = view.findViewById(R.id.btnRemoveWater);
        
        btnAddMeal = view.findViewById(R.id.btnAddMeal);
        
        etMealName = view.findViewById(R.id.etMealName);
        progressMeal = view.findViewById(R.id.progressMeal);
        rvMeals = view.findViewById(R.id.rvMeals);

        rvMeals.setLayoutManager(new LinearLayoutManager(getContext()));
        db = AppDatabase.getInstance(getContext());
        
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        displayFormat = new SimpleDateFormat("dd MMM EEE", new Locale("tr", "TR"));
        currentCalendar = Calendar.getInstance();

        mealAdapter = new MealAdapter(null, log -> {
            Executors.newSingleThreadExecutor().execute(() -> {
                db.calorieDao().deleteById(log.id);
                loadDataForCurrentDate();
            });
        });
        rvMeals.setAdapter(mealAdapter);

        btnPrevDay.setOnClickListener(v -> changeDate(-1));
        btnNextDay.setOnClickListener(v -> changeDate(1));
        
        btnAddWater250.setOnClickListener(v -> addWater(250));
        btnAddWater330.setOnClickListener(v -> addWater(330));
        btnAddWater500.setOnClickListener(v -> addWater(500));
        btnRemoveWater.setOnClickListener(v -> removeLastWater());

        btnAddMeal.setOnClickListener(v -> addMeal());

        updateDateDisplay();
        loadDataForCurrentDate();

        return view;
    }

    private void changeDate(int days) {
        currentCalendar.add(Calendar.DAY_OF_YEAR, days);
        updateDateDisplay();
        loadDataForCurrentDate();
    }

    private void updateDateDisplay() {
        Calendar today = Calendar.getInstance();
        if (dateFormat.format(currentCalendar.getTime()).equals(dateFormat.format(today.getTime()))) {
            tvDateDisplay.setText("Bugün");
        } else {
            tvDateDisplay.setText(displayFormat.format(currentCalendar.getTime()));
        }
    }

    private void loadDataForCurrentDate() {
        String queryDate = dateFormat.format(currentCalendar.getTime());

        Executors.newSingleThreadExecutor().execute(() -> {
            UserProfile user = db.userDao().getLatestProfile();
            List<CalorieLog> dailyLogs = db.calorieDao().getLogsByDate(queryDate);
            
            int totalConsumed = 0;
            int totalProt = 0, totalCarb = 0, totalFat = 0;
            
            for (CalorieLog log : dailyLogs) {
                totalConsumed += log.calories;
                totalProt += log.protein;
                totalCarb += log.carbs;
                totalFat += log.fat;
            }

            int finalTotalConsumed = totalConsumed;
            int finalProt = totalProt;
            int finalCarb = totalCarb;
            int finalFat = totalFat;
            
            int waterAmount = (int) db.waterDao().getTotalByDate(queryDate);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    mealAdapter.setMeals(dailyLogs);
                    
                    if (user != null) {
                        tvTargetCalorie.setText(String.valueOf(user.targetDailyCalorie));
                        tvWaterAmount.setText(waterAmount + " / " + (int)(user.dailyWaterGoal * 1000) + " ml");
                    }
                    
                    tvConsumedCalories.setText(String.valueOf(finalTotalConsumed));
                    tvTotalProtein.setText(finalProt + "g");
                    tvTotalCarbs.setText(finalCarb + "g");
                    tvTotalFat.setText(finalFat + "g");
                });
            }
        });
    }

    private void addWater(int amount) {
        String queryDate = dateFormat.format(currentCalendar.getTime());
        Executors.newSingleThreadExecutor().execute(() -> {
            WaterLog log = new WaterLog();
            log.date = queryDate;
            log.amountMl = amount;
            log.timestamp = System.currentTimeMillis();
            db.waterDao().insert(log);
            loadDataForCurrentDate();
        });
    }

    private void removeLastWater() {
        String queryDate = dateFormat.format(currentCalendar.getTime());
        Executors.newSingleThreadExecutor().execute(() -> {
            List<WaterLog> logs = db.waterDao().getLogsByDate(queryDate);
            if (logs != null && !logs.isEmpty()) {
                WaterLog lastLog = logs.get(0); // due to ORDER BY timestamp DESC
                db.waterDao().deleteById(lastLog.id);
                loadDataForCurrentDate();
            } else {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> 
                        Toast.makeText(getContext(), "Silinecek su kaydı yok.", Toast.LENGTH_SHORT).show()
                    );
                }
            }
        });
    }

    private void addMeal() {
        String mealText = etMealName.getText().toString().trim();
        if (mealText.isEmpty()) {
            Toast.makeText(getContext(), "Yediğiniz yemeği yazın", Toast.LENGTH_SHORT).show();
            return;
        }

        btnAddMeal.setEnabled(false);
        progressMeal.setVisibility(View.VISIBLE);

        String prompt = "Kullanıcı şu yemeği yedi: '" + mealText + "'. " +
                "Bu yemeğin tahmini kalorisini ve makrolarını (protein, karbonhidrat, yağ gram olarak) hesapla. " +
                "SADECE aşağıdaki formatta saf JSON olarak yanıt ver:\n" +
                "{\"calories\": 450, \"protein\": 30, \"carbs\": 40, \"fat\": 15}";

        GroqRequest request = new GroqRequest(prompt);
        String authHeader = "Bearer " + BuildConfig.GROQ_API_KEY;

        GroqClient.getService().generateContent(authHeader, request).enqueue(new Callback<GroqResponse>() {
            @Override
            public void onResponse(Call<GroqResponse> call, Response<GroqResponse> response) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    btnAddMeal.setEnabled(true);
                    progressMeal.setVisibility(View.GONE);

                    if (response.isSuccessful() && response.body() != null) {
                        String text = response.body().getText().trim();
                        try {
                            if (text.startsWith("```json")) {
                                text = text.replace("```json", "").replace("```", "").trim();
                            }
                            
                            JSONObject json = new JSONObject(text);
                            int calories = json.getInt("calories");
                            int protein = json.getInt("protein");
                            int carbs = json.getInt("carbs");
                            int fat = json.getInt("fat");

                            String queryDate = dateFormat.format(currentCalendar.getTime());
                            CalorieLog log = new CalorieLog();
                            log.mealName = mealText;
                            log.calories = calories;
                            log.protein = protein;
                            log.carbs = carbs;
                            log.fat = fat;
                            log.date = queryDate;

                            Executors.newSingleThreadExecutor().execute(() -> {
                                db.calorieDao().insert(log);
                                if (getActivity() != null) {
                                    getActivity().runOnUiThread(() -> {
                                        etMealName.setText("");
                                        etMealName.clearFocus();
                                        Toast.makeText(getContext(), "Yemek eklendi", Toast.LENGTH_SHORT).show();
                                        loadDataForCurrentDate();
                                    });
                                }
                            });

                        } catch (Exception e) {
                            Toast.makeText(getContext(), "Anlaşılamadı, farklı şekilde yazmayı dene.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "API Hatası", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<GroqResponse> call, Throwable t) {
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    btnAddMeal.setEnabled(true);
                    progressMeal.setVisibility(View.GONE);
                    Toast.makeText(getContext(), "Bağlantı hatası", Toast.LENGTH_SHORT).show();
                });
            }
        });
    }
}
