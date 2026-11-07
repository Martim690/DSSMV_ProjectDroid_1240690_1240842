package mrgym.controllers;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import mrgym.R;
import mrgym.models.Exercise;
import mrgym.models.WorkoutPlan;
import mrgym.network.RestClient;
import mrgym.utils.ImageLoader;
import mrgym.utils.SharedPrefsUtils;
import mrgym.adapters.WorkoutAdapter;

public class WorkoutActivity extends AppCompatActivity {

    private static final String API_KEY = "a9a7646274509cf090183d26fba41bb423130";
    private static final String WORKOUTS_URL = "https://mrgym-dbdf.restdb.io/rest/workoutplans";

    private Toolbar toolbar;
    private ListView listViewWorkouts;
    private TextView tvEmptyWorkouts;
    private Button btnAddWorkout;

    private List<WorkoutPlan> workoutPlanList;
    private WorkoutAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_workout);

        toolbar = findViewById(R.id.toolbarWorkout);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.label_workout_management);
        }

        listViewWorkouts = findViewById(R.id.listViewWorkouts);
        tvEmptyWorkouts = findViewById(R.id.tvEmptyWorkouts);
        btnAddWorkout = findViewById(R.id.btnAddWorkout);

        workoutPlanList = new ArrayList<>();
        adapter = new WorkoutAdapter(this, workoutPlanList);
        listViewWorkouts.setAdapter(adapter);

        listViewWorkouts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                WorkoutPlan plan = workoutPlanList.get(position);
                showWorkoutDetailsDialog(plan);
            }
        });

        btnAddWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(WorkoutActivity.this, CreateWorkoutActivity.class));
            }
        });

        loadWorkoutsFromRestDB();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWorkoutsFromRestDB();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadWorkoutsFromRestDB() {
        RestClient.makeRequest(WORKOUTS_URL, "GET", null, API_KEY, new RestClient.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    WorkoutPlan[] plans = new Gson().fromJson(result, WorkoutPlan[].class);
                    workoutPlanList.clear();

                    String currentEmail = SharedPrefsUtils.getEmail(WorkoutActivity.this);

                    if (plans != null) {
                        for (WorkoutPlan p : plans) {
                            if (p.getUserEmail() == null || currentEmail == null || p.getUserEmail().equals(currentEmail)) {
                                workoutPlanList.add(p);
                            }
                        }
                    }

                    adapter.notifyDataSetChanged();
                    updateEmptyState();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                updateEmptyState();
            }
        });
    }

    private void showWorkoutDetailsDialog(final WorkoutPlan plan) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_workout_details, null);
        TextView tvTitle = dialogView.findViewById(R.id.tvDialogWorkoutName);
        TextView tvSubtitle = dialogView.findViewById(R.id.tvDialogWorkoutSubtitle);
        LinearLayout llExerciseContainer = dialogView.findViewById(R.id.llExerciseContainer);
        Button btnDelete = dialogView.findViewById(R.id.btnDialogDeleteWorkout);
        View btnClose = dialogView.findViewById(R.id.btnDialogClose);

        tvTitle.setText(plan.getName());

        List<Exercise> exercises = plan.getExercises();
        int count = exercises != null ? exercises.size() : 0;
        tvSubtitle.setText(count + (count == 1 ? " exercise" : " exercises"));

        // Inflate each exercise card directly into the LinearLayout (avoids ListView-in-ScrollView issue)
        if (exercises != null) {
            LayoutInflater inflater = LayoutInflater.from(this);
            for (Exercise exercise : exercises) {
                View card = inflater.inflate(R.layout.list_item_workout_detail_exercise, llExerciseContainer, false);

                ImageView ivPhoto = card.findViewById(R.id.ivDetailExercisePhoto);
                TextView tvName = card.findViewById(R.id.tvDetailExerciseName);
                TextView tvSetsReps = card.findViewById(R.id.tvDetailExerciseSetsReps);
                TextView tvDesc = card.findViewById(R.id.tvDetailExerciseDesc);

                tvName.setText(exercise.getName());

                int sets = exercise.getSets() > 0 ? exercise.getSets() : 3;
                int reps = exercise.getReps() > 0 ? exercise.getReps() : 10;
                double weight = exercise.getWeight();
                StringBuilder sb = new StringBuilder();
                sb.append("🎯 ").append(sets).append(" sets × ").append(reps).append(" reps");
                if (weight > 0) {
                    if (weight == Math.floor(weight)) {
                        sb.append(" • ").append((int) weight).append(" kg");
                    } else {
                        sb.append(" • ").append(String.format(java.util.Locale.US, "%.1f kg", weight));
                    }
                } else {
                    sb.append(" • Bodyweight");
                }
                tvSetsReps.setText(sb.toString());

                String desc = exercise.getDescription();
                if (desc == null || desc.trim().isEmpty()) {
                    desc = "No description provided.";
                }
                tvDesc.setText(desc);

                ImageLoader.loadImage(exercise.getImageUrl(), ivPhoto);

                llExerciseContainer.addView(card);
            }
        }

        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .create();

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                confirmDeleteWorkout(plan);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void confirmDeleteWorkout(final WorkoutPlan plan) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Workout")
                .setMessage("Are you sure you want to delete \"" + plan.getName() + "\"?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteWorkoutFromRestDB(plan);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteWorkoutFromRestDB(WorkoutPlan plan) {
        if (plan.getId() == null || plan.getId().isEmpty()) {
            workoutPlanList.remove(plan);
            adapter.notifyDataSetChanged();
            updateEmptyState();
            return;
        }

        String deleteUrl = WORKOUTS_URL + "/" + plan.getId();
        RestClient.makeRequest(deleteUrl, "DELETE", null, API_KEY, new RestClient.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(WorkoutActivity.this, "Workout deleted.", Toast.LENGTH_SHORT).show();
                loadWorkoutsFromRestDB();
            }

            @Override
            public void onError(String error) {
                Toast.makeText(WorkoutActivity.this, "Failed to delete: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateEmptyState() {
        if (workoutPlanList.isEmpty()) {
            tvEmptyWorkouts.setVisibility(View.VISIBLE);
            listViewWorkouts.setVisibility(View.GONE);
        } else {
            tvEmptyWorkouts.setVisibility(View.GONE);
            listViewWorkouts.setVisibility(View.VISIBLE);
        }
    }
}
