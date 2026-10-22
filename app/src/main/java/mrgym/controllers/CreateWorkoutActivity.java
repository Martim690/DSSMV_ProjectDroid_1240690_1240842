package mrgym.controllers;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;
import mrgym.R;
import mrgym.models.Exercise;
import mrgym.models.WorkoutPlan;
import mrgym.views.ExerciseSelectAdapter;

public class CreateWorkoutActivity extends AppCompatActivity {

    private static final String API_KEY = "a9a7646274509cf090183d26fba41bb423130";
    private static final String WORKOUTS_URL = "https://mrgym-dbdf.restdb.io/rest/workoutplans";

    private Toolbar toolbar;
    private EditText etWorkoutName;
    private Button btnCreateWorkout, btnCancelWorkout;
    private TextView tvSelectedCount;
    private ProgressBar progressBarExercises;
    private ListView listViewAvailableExercises;

    private List<Exercise> exerciseList;
    private ExerciseSelectAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_workout);

        toolbar = findViewById(R.id.toolbarCreateWorkout);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("Create Workout");
        }

        etWorkoutName = findViewById(R.id.etWorkoutName);
        btnCreateWorkout = findViewById(R.id.btnCreateWorkout);
        btnCancelWorkout = findViewById(R.id.btnCancelWorkout);
        tvSelectedCount = findViewById(R.id.tvSelectedCount);
        progressBarExercises = findViewById(R.id.progressBarExercises);
        listViewAvailableExercises = findViewById(R.id.listViewAvailableExercises);

        exerciseList = new ArrayList<>();
        adapter = new ExerciseSelectAdapter(this, exerciseList, new ExerciseSelectAdapter.OnSelectionChangedListener() {
            @Override
            public void onSelectionChanged(int selectedCount) {
                tvSelectedCount.setText(selectedCount + " exercises selected");
            }
        });
        listViewAvailableExercises.setAdapter(adapter);

        listViewAvailableExercises.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                adapter.toggleSelection(position);
            }
        });

        btnCancelWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnCreateWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitWorkoutPlan();
            }
        });

        loadExercisesFromWger();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void loadExercisesFromWger() {
        progressBarExercises.setVisibility(View.VISIBLE);

        RestClient.fetchExercisesFromWger(new RestClient.ExercisesCallback() {
            @Override
            public void onSuccess(List<Exercise> exercises) {
                progressBarExercises.setVisibility(View.GONE);
                exerciseList.clear();
                exerciseList.addAll(exercises);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(String error) {
                progressBarExercises.setVisibility(View.GONE);
                Toast.makeText(CreateWorkoutActivity.this, "Failed to load exercises: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitWorkoutPlan() {
        String name = etWorkoutName.getText().toString().trim();
        if (name.isEmpty()) {
            Toast.makeText(this, "Please enter a workout name.", Toast.LENGTH_SHORT).show();
            return;
        }

        List<Exercise> selectedExercises = adapter.getSelectedExercises();
        if (selectedExercises.isEmpty()) {
            Toast.makeText(this, "Please select at least one exercise.", Toast.LENGTH_SHORT).show();
            return;
        }

        btnCreateWorkout.setEnabled(false);
        Toast.makeText(this, "Saving workout plan...", Toast.LENGTH_SHORT).show();

        String email = SharedPrefsUtils.getEmail(this);
        WorkoutPlan plan = new WorkoutPlan(name, selectedExercises, email);

        String jsonBody = new Gson().toJson(plan);

        RestClient.makeRequest(WORKOUTS_URL, "POST", jsonBody, API_KEY, new RestClient.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                Toast.makeText(CreateWorkoutActivity.this, "Workout created successfully!", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onError(String error) {
                btnCreateWorkout.setEnabled(true);
                Toast.makeText(CreateWorkoutActivity.this, "Failed to save: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
