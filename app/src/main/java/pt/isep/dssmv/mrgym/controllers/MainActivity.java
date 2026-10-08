package pt.isep.dssmv.mrgym.controllers;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import pt.isep.dssmv.mrgym.R;
import pt.isep.dssmv.mrgym.adapters.CustomAdapter;
import pt.isep.dssmv.mrgym.models.Exercise;
import pt.isep.dssmv.mrgym.network.RestClient;

public class MainActivity extends AppCompatActivity {

    private ListView listViewExercises;
    private CustomAdapter adapter;
    private List<Exercise> exerciseList;
    private Button btnCamera, btnWorkout;
    private LocationManager locationManager;

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listViewExercises = findViewById(R.id.listViewExercises);
        btnCamera = findViewById(R.id.btnCamera);
        btnWorkout = findViewById(R.id.btnWorkout);
        
        exerciseList = new ArrayList<>();
        adapter = new CustomAdapter(this, exerciseList);
        listViewExercises.setAdapter(adapter);

        btnCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        btnWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, WorkoutActivity.class));
            }
        });

        fetchExercisesFromWger();
        setupLocation();
    }

    private void fetchExercisesFromWger() {
        String url = "https://wger.de/api/v2/exercise/?language=2&limit=10";
        RestClient.makeRequest(url, "GET", null, null, new RestClient.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray resultsArray = jsonObject.getJSONArray("results");
                    for (int i = 0; i < resultsArray.length(); i++) {
                        JSONObject ex = resultsArray.getJSONObject(i);
                        int id = ex.getInt("id");
                        String name = ex.getString("name");
                        String description = ex.getString("description").replaceAll("<[^>]*>", "");
                        exerciseList.add(new Exercise(id, name, description));
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                Toast.makeText(MainActivity.this, "Erro Wger: " + error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void setupLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLocation();
        }
    }

    private void getLocation() {
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, new LocationListener() {
                @Override
                public void onLocationChanged(@NonNull Location location) {
                    Toast.makeText(MainActivity.this, "Lat: " + location.getLatitude() + " Lng: " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLocation();
        }
    }
}
