package com.example.fitmind.ui.measurements;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.fitmind.R;
import com.example.fitmind.db.AppDatabase;
import com.example.fitmind.model.MeasurementLog;
import com.example.fitmind.model.UserProfile;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executors;

public class MeasurementsFragment extends Fragment {

    private TextView tvDate, tvBodyFatResult;
    private TextInputEditText etNeck, etWaist, etHip;
    private ImageButton btnPrevDate, btnNextDate;
    private MaterialButton btnSaveMeasurements;

    private Calendar currentDate;
    private SimpleDateFormat dateFormat;
    private AppDatabase db;

    private UserProfile currentUser;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_measurements, container, false);

        tvDate = view.findViewById(R.id.tvDate);
        tvBodyFatResult = view.findViewById(R.id.tvBodyFatResult);
        etNeck = view.findViewById(R.id.etNeck);
        etWaist = view.findViewById(R.id.etWaist);
        etHip = view.findViewById(R.id.etHip);
        btnPrevDate = view.findViewById(R.id.btnPrevDate);
        btnNextDate = view.findViewById(R.id.btnNextDate);
        btnSaveMeasurements = view.findViewById(R.id.btnSaveMeasurements);

        currentDate = Calendar.getInstance();
        dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        db = AppDatabase.getInstance(requireContext());

        loadUserAndData();

        btnPrevDate.setOnClickListener(v -> {
            currentDate.add(Calendar.DAY_OF_MONTH, -1);
            loadDataForDate();
        });

        btnNextDate.setOnClickListener(v -> {
            currentDate.add(Calendar.DAY_OF_MONTH, 1);
            loadDataForDate();
        });

        btnSaveMeasurements.setOnClickListener(v -> saveMeasurements());

        return view;
    }

    private void loadUserAndData() {
        Executors.newSingleThreadExecutor().execute(() -> {
            currentUser = db.userDao().getLatestProfile();
            if (getActivity() != null) {
                getActivity().runOnUiThread(this::loadDataForDate);
            }
        });
    }

    private void loadDataForDate() {
        String dateStr = dateFormat.format(currentDate.getTime());
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd MMMM yyyy", new Locale("tr"));
        tvDate.setText(displayFormat.format(currentDate.getTime()));

        Executors.newSingleThreadExecutor().execute(() -> {
            MeasurementLog log = db.measurementDao().getByDate(dateStr);
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (log != null) {
                        etNeck.setText(log.neck > 0 ? String.valueOf(log.neck) : "");
                        etWaist.setText(log.waist > 0 ? String.valueOf(log.waist) : "");
                        etHip.setText(log.hip > 0 ? String.valueOf(log.hip) : "");
                        
                        if (log.bodyFatPercentage > 0) {
                            tvBodyFatResult.setText(String.format(Locale.getDefault(), "%%%.1f", log.bodyFatPercentage));
                        } else {
                            tvBodyFatResult.setText("%0.0");
                        }
                    } else {
                        etNeck.setText("");
                        etWaist.setText("");
                        etHip.setText("");
                        tvBodyFatResult.setText("%0.0");
                    }
                });
            }
        });
    }

    private void saveMeasurements() {
        if (currentUser == null) return;

        String neckStr = etNeck.getText() != null ? etNeck.getText().toString() : "";
        String waistStr = etWaist.getText() != null ? etWaist.getText().toString() : "";
        String hipStr = etHip.getText() != null ? etHip.getText().toString() : "";

        if (neckStr.isEmpty() || waistStr.isEmpty() || (currentUser.gender.equals("female") && hipStr.isEmpty())) {
            Toast.makeText(getContext(), "Lütfen gerekli ölçüleri girin", Toast.LENGTH_SHORT).show();
            return;
        }

        float neck, waist, hip;
        try {
            neck = Float.parseFloat(neckStr.replace(",", "."));
            waist = Float.parseFloat(waistStr.replace(",", "."));
            hip = hipStr.isEmpty() ? 0 : Float.parseFloat(hipStr.replace(",", "."));
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Lütfen geçerli sayılar girin", Toast.LENGTH_SHORT).show();
            return;
        }

        float height = currentUser.height;
        
        if (currentUser.gender.equals("male") && waist <= neck) {
            Toast.makeText(getContext(), "Bel ölçüsü boyun ölçüsünden büyük olmalıdır.", Toast.LENGTH_SHORT).show();
            return;
        } else if (currentUser.gender.equals("female") && (waist + hip) <= neck) {
            Toast.makeText(getContext(), "Hatalı ölçüm! Değerleri kontrol edin.", Toast.LENGTH_SHORT).show();
            return;
        }

        float calculatedBodyFat = 0;
        
        // US Navy Body Fat Formula
        if (currentUser.gender.equals("male")) {
            // Male: 495 / (1.0324 - 0.19077 * log10(waist - neck) + 0.15456 * log10(height)) - 450
            double logWaistNeck = Math.log10(waist - neck);
            double logHeight = Math.log10(height);
            calculatedBodyFat = (float) (495 / (1.0324 - 0.19077 * logWaistNeck + 0.15456 * logHeight) - 450);
        } else {
            // Female: 495 / (1.29579 - 0.35004 * log10(waist + hip - neck) + 0.22100 * log10(height)) - 450
            double logWaistHipNeck = Math.log10(waist + hip - neck);
            double logHeight = Math.log10(height);
            calculatedBodyFat = (float) (495 / (1.29579 - 0.35004 * logWaistHipNeck + 0.22100 * logHeight) - 450);
        }

        final float finalBodyFat = calculatedBodyFat;

        // Validate
        if (finalBodyFat < 2 || finalBodyFat > 60 || Double.isNaN(finalBodyFat)) {
            Toast.makeText(getContext(), "Hatalı ölçüm! Değerleri kontrol edin.", Toast.LENGTH_SHORT).show();
            return;
        }

        tvBodyFatResult.setText(String.format(Locale.getDefault(), "%%%.1f", finalBodyFat));

        String dateStr = dateFormat.format(currentDate.getTime());

        Executors.newSingleThreadExecutor().execute(() -> {
            MeasurementLog log = db.measurementDao().getByDate(dateStr);
            if (log == null) {
                log = new MeasurementLog();
                log.date = dateStr;
            }
            log.weight = currentUser.weight; // just keeping track of current weight
            log.neck = neck;
            log.waist = waist;
            log.hip = hip;
            log.bodyFatPercentage = finalBodyFat;
            
            db.measurementDao().insert(log);

            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> 
                    Toast.makeText(getContext(), "Ölçümler kaydedildi!", Toast.LENGTH_SHORT).show()
                );
            }
        });
    }
}
