package com.example.fitmind.ui.workout;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.fitmind.R;
import com.example.fitmind.model.WorkoutPlanModel;

import java.util.List;

public class ExerciseAdapter extends RecyclerView.Adapter<ExerciseAdapter.ExerciseViewHolder> {

    private List<WorkoutPlanModel.Exercise> exercises;
    private OnTipClickListener tipClickListener;

    public interface OnTipClickListener {
        void onTipClick(WorkoutPlanModel.Exercise exercise);
    }

    public ExerciseAdapter(List<WorkoutPlanModel.Exercise> exercises, OnTipClickListener tipClickListener) {
        this.exercises = exercises;
        this.tipClickListener = tipClickListener;
    }

    public void setExercises(List<WorkoutPlanModel.Exercise> exercises) {
        this.exercises = exercises;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ExerciseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exercise, parent, false);
        return new ExerciseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExerciseViewHolder holder, int position) {
        WorkoutPlanModel.Exercise exercise = exercises.get(position);
        holder.tvExerciseName.setText(exercise.name);
        holder.tvExerciseDetails.setText(exercise.sets + " Set x " + exercise.reps + " Tekrar");

        // Set initial state without triggering listener
        holder.cbExerciseDone.setOnCheckedChangeListener(null);
        holder.cbExerciseDone.setChecked(exercise.isCompleted);
        applyStrikethrough(holder.tvExerciseName, exercise.isCompleted);

        holder.cbExerciseDone.setOnCheckedChangeListener((buttonView, isChecked) -> {
            exercise.isCompleted = isChecked;
            applyStrikethrough(holder.tvExerciseName, isChecked);
        });

        holder.btnAiTip.setOnClickListener(v -> {
            if (tipClickListener != null) {
                tipClickListener.onTipClick(exercise);
            }
        });
    }

    private void applyStrikethrough(TextView tv, boolean isStrikethrough) {
        if (isStrikethrough) {
            tv.setPaintFlags(tv.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            tv.setAlpha(0.5f);
        } else {
            tv.setPaintFlags(tv.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
            tv.setAlpha(1.0f);
        }
    }

    @Override
    public int getItemCount() {
        return exercises != null ? exercises.size() : 0;
    }

    static class ExerciseViewHolder extends RecyclerView.ViewHolder {
        CheckBox cbExerciseDone;
        TextView tvExerciseName, tvExerciseDetails;
        ImageButton btnAiTip;

        public ExerciseViewHolder(@NonNull View itemView) {
            super(itemView);
            cbExerciseDone = itemView.findViewById(R.id.cbExerciseDone);
            tvExerciseName = itemView.findViewById(R.id.tvExerciseName);
            tvExerciseDetails = itemView.findViewById(R.id.tvExerciseDetails);
            btnAiTip = itemView.findViewById(R.id.btnAiTip);
        }
    }
}
