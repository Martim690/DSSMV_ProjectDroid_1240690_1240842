package mrgym.controllers;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import mrgym.utils.SharedPrefsUtils;
import mrgym.adapters.ExerciseSelectAdapter;

public class CreateWorkoutActivity extends AppCompatActivity {

    private static final String API_KEY = "a9a7646274509cf090183d26fba41bb423130";
    private static final String WORKOUTS_URL = "https://mrgym-dbdf.restdb.io/rest/workoutplans";

    private Toolbar toolbar;
    private EditText etSearchExercise;
    private Button btnCreateWorkout;
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

        etSearchExercise = findViewById(R.id.etSearchExercise);
        btnCreateWorkout = findViewById(R.id.btnCreateWorkout);
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

        etSearchExercise.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        btnCreateWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Exercise> selectedExercises = adapter.getSelectedExercises();
                if (selectedExercises.isEmpty()) {
                    Toast.makeText(CreateWorkoutActivity.this, "Please select at least one exercise.", Toast.LENGTH_SHORT).show();
                    return;
                }
                promptWorkoutNameAndSave(selectedExercises);
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
                adapter.updateData(exercises);
            }

            @Override
            public void onError(String error) {
                progressBarExercises.setVisibility(View.GONE);
                Toast.makeText(CreateWorkoutActivity.this, "Failed to load exercises: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void promptWorkoutNameAndSave(final List<Exercise> selectedExercises) {
        final EditText etNameInput = new EditText(this);
        etNameInput.setHint(R.string.hint_workout_name);
        etNameInput.setSingleLine(true);

        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        int paddingPx = (int) (20 * getResources().getDisplayMetrics().density);
        container.setPadding(paddingPx, paddingPx / 2, paddingPx, paddingPx / 2);
        container.addView(etNameInput);

        new AlertDialog.Builder(this)
                .setTitle(R.string.dialog_title_save_workout)
                .setMessage(R.string.dialog_msg_workout_name)
                .setView(container)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = etNameInput.getText().toString().trim();
                        if (name.isEmpty()) {
                            Toast.makeText(CreateWorkoutActivity.this, "Please enter a workout name.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        saveWorkoutPlan(name, selectedExercises);
                    }
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .show();
    }

    private void saveWorkoutPlan(String name, List<Exercise> selectedExercises) {
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
