package mrgym.controllers;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import mrgym.R;
import mrgym.models.JournalEntry;
import mrgym.network.RestClient;
import mrgym.utils.SharedPrefsUtils;
import mrgym.adapters.JournalAdapter;

public class JournalActivity extends AppCompatActivity {

    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_PICK_GALLERY = 2;
    private static final int CAMERA_PERMISSION_REQUEST = 100;
    private static final int PAGE_SIZE = 6;
    private static final String API_KEY = "a9a7646274509cf090183d26fba41bb423130";
    private static final String JOURNAL_URL = "https://mrgym-dbdf.restdb.io/rest/journalentries";

    private Toolbar toolbar;
    private Button btnTakePhoto, btnPickGallery;
    private GridView gridViewJournal;
    private TextView tvEmptyJournal;
    private JournalAdapter adapter;
    private List<JournalEntry> allEntries;
    private List<JournalEntry> displayedEntries;
    private int currentPage = 0;
    private boolean isLoading = false;
    private Gson gson;

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
        btnPickGallery = findViewById(R.id.btnPickGallery);
        gridViewJournal = findViewById(R.id.gridViewJournal);
        tvEmptyJournal = findViewById(R.id.tvEmptyJournal);

        allEntries = new ArrayList<>();
        displayedEntries = new ArrayList<>();
        gson = new Gson();

        adapter = new JournalAdapter(this, displayedEntries, new JournalAdapter.OnDeleteClickListener() {
            @Override
            public void onDelete(int position) {
                confirmDeleteEntry(position);
            }
        }, new JournalAdapter.OnPhotoClickListener() {
            @Override
            public void onPhotoClick(JournalEntry entry) {
                showPhotoDetailDialog(entry);
            }
        });
        gridViewJournal.setAdapter(adapter);

        gridViewJournal.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (!isLoading && (firstVisibleItem + visibleItemCount >= totalItemCount - 1) && displayedEntries.size() < allEntries.size()) {
                    loadNextPage();
                }
            }
        });

        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkCameraPermission();
            }
        });

        btnPickGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        loadEntries();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadEntries();
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

    private void openGallery() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(pickIntent, REQUEST_PICK_GALLERY);
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
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {
                Bundle extras = data.getExtras();
                if (extras != null) {
                    Bitmap photo = (Bitmap) extras.get("data");
                    if (photo != null) {
                        showEntryDetailsDialog(photo);
                    }
                }
            } else if (requestCode == REQUEST_PICK_GALLERY) {
                Uri imageUri = data.getData();
                if (imageUri != null) {
                    try {
                        InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                        if (imageStream != null) {
                            imageStream.close();
                        }
                        if (selectedImage != null) {
                            showEntryDetailsDialog(selectedImage);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void showEntryDetailsDialog(final Bitmap photoBitmap) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_journal_entry, null);
        ImageView ivPreview = dialogView.findViewById(R.id.ivPreviewPhoto);
        final EditText etWeight = dialogView.findViewById(R.id.etJournalWeight);
        final EditText etHeight = dialogView.findViewById(R.id.etJournalHeight);

        ivPreview.setImageBitmap(photoBitmap);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.title_new_journal_entry)
                .setView(dialogView)
                .setPositiveButton(R.string.btn_save_entry, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        double weight = 0;
                        double height = 0;
                        try {
                            String weightStr = etWeight.getText().toString().trim();
                            if (!weightStr.isEmpty()) {
                                weight = Double.parseDouble(weightStr);
                            }
                        } catch (NumberFormatException ignored) {
                        }
                        try {
                            String heightStr = etHeight.getText().toString().trim();
                            if (!heightStr.isEmpty()) {
                                height = Double.parseDouble(heightStr);
                            }
                        } catch (NumberFormatException ignored) {
                        }

                        saveJournalEntry(photoBitmap, weight, height);
                    }
                })
                .setNegativeButton(R.string.btn_cancel, null)
                .create();
        dialog.show();
    }

    private void saveJournalEntry(Bitmap bitmap, double weight, double height) {
        try {
            String currentEmail = SharedPrefsUtils.getEmail(this);
            String folderName = "journal";
            if (currentEmail != null && !currentEmail.isEmpty()) {
                folderName = "journal_" + currentEmail.replaceAll("[^a-zA-Z0-9]", "_");
            }
            File dir = new File(getFilesDir(), folderName);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            long timestamp = System.currentTimeMillis();
            String fileName = "journal_" + timestamp + ".jpg";
            File photoFile = new File(dir, fileName);

            FileOutputStream fos = new FileOutputStream(photoFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.close();

            String id = String.valueOf(timestamp);
            final JournalEntry newEntry = new JournalEntry(id, photoFile.getAbsolutePath(), timestamp, weight, height, currentEmail);

            allEntries.add(0, newEntry);
            displayedEntries.add(0, newEntry);
            persistAllEntries();

            adapter.notifyDataSetChanged();
            updateEmptyState();
            gridViewJournal.smoothScrollToPosition(0);
            Toast.makeText(this, getString(R.string.msg_entry_saved), Toast.LENGTH_SHORT).show();

            String jsonBody = gson.toJson(newEntry);
            RestClient.makeRequest(JOURNAL_URL, "POST", jsonBody, API_KEY, new RestClient.VolleyCallback() {
                @Override
                public void onSuccess(String result) {
                    try {
                        JournalEntry created = gson.fromJson(result, JournalEntry.class);
                        if (created != null && created.getId() != null) {
                            newEntry.setId(created.getId());
                            persistAllEntries();
                        }
                    } catch (Exception ignored) {
                    }
                }

                @Override
                public void onError(String error) {
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void persistAllEntries() {
        try {
            File dir = new File(getFilesDir(), "journal_data");
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File jsonFile = new File(dir, "entries.json");
            List<JournalEntry> totalList = new ArrayList<>();
            if (jsonFile.exists()) {
                try {
                    FileReader reader = new FileReader(jsonFile);
                    Type type = new TypeToken<List<JournalEntry>>() {}.getType();
                    List<JournalEntry> existing = gson.fromJson(reader, type);
                    reader.close();
                    if (existing != null) {
                        String currentEmail = SharedPrefsUtils.getEmail(this);
                        for (JournalEntry item : existing) {
                            if (currentEmail != null && !currentEmail.equals(item.getUserEmail())) {
                                totalList.add(item);
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }
            totalList.addAll(allEntries);
            FileWriter writer = new FileWriter(jsonFile);
            gson.toJson(totalList, writer);
            writer.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadEntries() {
        allEntries.clear();
        displayedEntries.clear();
        currentPage = 0;

        final String currentEmail = SharedPrefsUtils.getEmail(this);

        File dir = new File(getFilesDir(), "journal_data");
        File jsonFile = new File(dir, "entries.json");
        if (!jsonFile.exists()) {
            jsonFile = new File(new File(getFilesDir(), "journal"), "entries.json");
        }

        if (jsonFile.exists()) {
            try {
                FileReader reader = new FileReader(jsonFile);
                Type type = new TypeToken<List<JournalEntry>>() {}.getType();
                List<JournalEntry> loaded = gson.fromJson(reader, type);
                reader.close();
                if (loaded != null) {
                    for (JournalEntry e : loaded) {
                        if (e.getUserEmail() == null || currentEmail == null || e.getUserEmail().equals(currentEmail)) {
                            allEntries.add(e);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        Collections.sort(allEntries, new Comparator<JournalEntry>() {
            @Override
            public int compare(JournalEntry e1, JournalEntry e2) {
                return Long.compare(e2.getTimestamp(), e1.getTimestamp());
            }
        });

        loadNextPage();
        updateEmptyState();

        loadEntriesFromRestDB(currentEmail);
    }

    private void loadEntriesFromRestDB(final String currentEmail) {
        RestClient.makeRequest(JOURNAL_URL, "GET", null, API_KEY, new RestClient.VolleyCallback() {
            @Override
            public void onSuccess(String result) {
                try {
                    JournalEntry[] remoteEntries = gson.fromJson(result, JournalEntry[].class);
                    if (remoteEntries != null && remoteEntries.length > 0) {
                        boolean changed = false;
                        for (JournalEntry remote : remoteEntries) {
                            if (remote.getUserEmail() == null || currentEmail == null || remote.getUserEmail().equals(currentEmail)) {
                                boolean exists = false;
                                for (JournalEntry local : allEntries) {
                                    if (local.getId() != null && local.getId().equals(remote.getId())) {
                                        exists = true;
                                        break;
                                    }
                                }
                                if (!exists) {
                                    allEntries.add(remote);
                                    changed = true;
                                }
                            }
                        }
                        if (changed) {
                            Collections.sort(allEntries, new Comparator<JournalEntry>() {
                                @Override
                                public int compare(JournalEntry e1, JournalEntry e2) {
                                    return Long.compare(e2.getTimestamp(), e1.getTimestamp());
                                }
                            });
                            displayedEntries.clear();
                            currentPage = 0;
                            loadNextPage();
                            updateEmptyState();
                            persistAllEntries();
                        }
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void onError(String error) {
            }
        });
    }

    private void loadNextPage() {
        if (displayedEntries.size() >= allEntries.size()) {
            return;
        }
        isLoading = true;
        int startIndex = currentPage * PAGE_SIZE;
        int endIndex = Math.min(startIndex + PAGE_SIZE, allEntries.size());
        for (int i = startIndex; i < endIndex; i++) {
            displayedEntries.add(allEntries.get(i));
        }
        currentPage++;
        adapter.notifyDataSetChanged();
        isLoading = false;
    }

    private void showPhotoDetailDialog(JournalEntry entry) {
        if (entry.getPhotoPath() == null) {
            return;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(entry.getPhotoPath());
        if (bitmap == null) {
            return;
        }
        ImageView iv = new ImageView(this);
        iv.setImageBitmap(bitmap);
        iv.setAdjustViewBounds(true);
        iv.setScaleType(ImageView.ScaleType.FIT_CENTER);
        iv.setPadding(16, 16, 16, 16);

        new AlertDialog.Builder(this)
                .setView(iv)
                .setPositiveButton(R.string.btn_back, null)
                .show();
    }

    private void confirmDeleteEntry(final int position) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.btn_delete)
                .setMessage(R.string.msg_confirm_delete_journal)
                .setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteEntry(position);
                    }
                })
                .setNegativeButton(R.string.btn_no, null)
                .show();
    }

    private void deleteEntry(int position) {
        if (position >= 0 && position < displayedEntries.size()) {
            JournalEntry entry = displayedEntries.get(position);
            if (entry.getPhotoPath() != null) {
                File photoFile = new File(entry.getPhotoPath());
                if (photoFile.exists()) {
                    photoFile.delete();
                }
            }
            allEntries.remove(entry);
            displayedEntries.remove(position);
            persistAllEntries();
            adapter.notifyDataSetChanged();
            updateEmptyState();

            if (entry.getId() != null && !entry.getId().isEmpty()) {
                String deleteUrl = JOURNAL_URL + "/" + entry.getId();
                RestClient.makeRequest(deleteUrl, "DELETE", null, API_KEY, new RestClient.VolleyCallback() {
                    @Override
                    public void onSuccess(String result) {
                    }

                    @Override
                    public void onError(String error) {
                    }
                });
            }

            Toast.makeText(this, getString(R.string.msg_entry_deleted), Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEmptyState() {
        if (allEntries.isEmpty()) {
            tvEmptyJournal.setVisibility(View.VISIBLE);
            gridViewJournal.setVisibility(View.GONE);
        } else {
            tvEmptyJournal.setVisibility(View.GONE);
            gridViewJournal.setVisibility(View.VISIBLE);
        }
    }
}
