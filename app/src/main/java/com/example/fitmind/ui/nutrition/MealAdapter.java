package com.example.fitmind.ui.nutrition;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitmind.R;
import com.example.fitmind.model.CalorieLog;

import java.util.List;

public class MealAdapter extends RecyclerView.Adapter<MealAdapter.MealViewHolder> {

    private List<CalorieLog> meals;
    private OnMealDeleteListener deleteListener;

    public interface OnMealDeleteListener {
        void onDeleteClick(CalorieLog log);
    }

    public MealAdapter(List<CalorieLog> meals, OnMealDeleteListener deleteListener) {
        this.meals = meals;
        this.deleteListener = deleteListener;
    }

    public void setMeals(List<CalorieLog> meals) {
        this.meals = meals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_meal, parent, false);
        return new MealViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        CalorieLog log = meals.get(position);
        holder.tvMealName.setText(log.mealName);
        holder.tvMealCalories.setText(log.calories + " kcal");
        holder.tvMealMacros.setText(String.format("P: %dg • K: %dg • Y: %dg", log.protein, log.carbs, log.fat));

        holder.btnDeleteMeal.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onDeleteClick(log);
            }
        });
    }

    @Override
    public int getItemCount() {
        return meals != null ? meals.size() : 0;
    }

    static class MealViewHolder extends RecyclerView.ViewHolder {
        TextView tvMealName, tvMealCalories, tvMealMacros;
        ImageView btnDeleteMeal;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMealName = itemView.findViewById(R.id.tvMealName);
            tvMealCalories = itemView.findViewById(R.id.tvMealCalories);
            tvMealMacros = itemView.findViewById(R.id.tvMealMacros);
            btnDeleteMeal = itemView.findViewById(R.id.btnDeleteMeal);
        }
    }
}
