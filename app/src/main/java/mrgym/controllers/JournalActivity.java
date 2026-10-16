package mrgym.controllers;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import mrgym.R;
import mrgym.views.JournalAdapter;

public class JournalActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int CAMERA_PERMISSION_REQUEST = 100;

    private Toolbar toolbar;
    private Button btnTakePhoto;
    private ListView listViewJournal;
    private TextView tvEmptyJournal;
    private JournalAdapter adapter;
    private List<File> photoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        toolbar = findViewById(R.id.toolbarJournal);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle(R.string.label_journal);
        }

        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        listViewJournal = findViewById(R.id.listViewJournal);
        tvEmptyJournal = findViewById(R.id.tvEmptyJournal);

        photoList = new ArrayList<>();
        adapter = new JournalAdapter(this, photoList, new JournalAdapter.OnDeleteClickListener() {
            @Override
            public void onDelete(int position) {
                deletePhoto(position);
            }
        });
        listViewJournal.setAdapter(adapter);

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        loadPhotos();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadPhotos();
    }

    private void checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST);
        } else {
            openCamera();
        }
    }

    private void openCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } else {
            Toast.makeText(this, getString(R.string.msg_camera_error), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            openCamera();
        } else {
            Toast.makeText(this, getString(R.string.msg_camera_permission_denied), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK && data != null) {
            Bundle extras = data.getExtras();
            Bitmap photo = (Bitmap) extras.get("data");
            if (photo != null) {
                savePhoto(photo);
            }
        }
    }

    private void savePhoto(Bitmap bitmap) {
        try {
            File dir = new File(getFilesDir(), "journal");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            String fileName = "journal_" + System.currentTimeMillis() + ".jpg";
            File file = new File(dir, fileName);

            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            Toast.makeText(this, getString(R.string.msg_photo_saved), Toast.LENGTH_SHORT).show();
            loadPhotos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadPhotos() {
        photoList.clear();

        File dir = new File(getFilesDir(), "journal");
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                Arrays.sort(files, new Comparator<File>() {
                    @Override
                    public int compare(File f1, File f2) {
                        return Long.compare(f1.lastModified(), f2.lastModified());
                    }
                });
                for (File file : files) {
                    if (file.getName().endsWith(".jpg")) {
                        photoList.add(file);
                    }
                }
            }
        }

        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private void deletePhoto(int position) {
        File file = photoList.get(position);
        if (file.delete()) {
            photoList.remove(position);
            adapter.notifyDataSetChanged();
            updateEmptyState();
            Toast.makeText(this, getString(R.string.msg_photo_deleted), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEmptyState() {
        if (photoList.isEmpty()) {
            tvEmptyJournal.setVisibility(View.VISIBLE);
            listViewJournal.setVisibility(View.GONE);
        } else {
            tvEmptyJournal.setVisibility(View.GONE);
            listViewJournal.setVisibility(View.VISIBLE);
        }
    }
}
