package mrgym.controllers;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import mrgym.R;

public class WorkoutActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ListView listViewWorkouts;
    private TextView tvEmptyWorkouts;
    private Button btnAddWorkout;

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

        btnAddWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
