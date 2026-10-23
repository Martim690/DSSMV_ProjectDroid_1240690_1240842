package mrgym.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import mrgym.R;
import mrgym.models.Exercise;

public class ExerciseSelectAdapter extends BaseAdapter {

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int selectedCount);
    }

    private Context context;
    private List<Exercise> exerciseList;
    private Set<Integer> selectedIds;
    private OnSelectionChangedListener listener;
    private static final Map<String, Bitmap> imageCache = new HashMap<>();

    public ExerciseSelectAdapter(Context context, List<Exercise> exerciseList, OnSelectionChangedListener listener) {
        this.context = context;
        this.exerciseList = exerciseList;
        this.selectedIds = new HashSet<>();
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return exerciseList.size();
    }

    @Override
    public Object getItem(int position) {
        return exerciseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void toggleSelection(int position) {
        Exercise exercise = exerciseList.get(position);
        if (selectedIds.contains(exercise.getId())) {
            selectedIds.remove(exercise.getId());
        } else {
            selectedIds.add(exercise.getId());
        }
        notifyDataSetChanged();
        if (listener != null) {
            listener.onSelectionChanged(selectedIds.size());
        }
    }

    public List<Exercise> getSelectedExercises() {
        List<Exercise> selected = new ArrayList<>();
        for (Exercise ex : exerciseList) {
            if (selectedIds.contains(ex.getId())) {
                selected.add(ex);
            }
        }
        return selected;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_exercise_select, parent, false);
        }

        ImageView ivPhoto = convertView.findViewById(R.id.ivExercisePhoto);
        TextView tvName = convertView.findViewById(R.id.tvExerciseName);
        TextView tvDescription = convertView.findViewById(R.id.tvExerciseDescription);
        CheckBox cbSelect = convertView.findViewById(R.id.cbExerciseSelect);

        Exercise exercise = exerciseList.get(position);
        tvName.setText(exercise.getName());

        String desc = exercise.getDescription();
        if (desc == null || desc.trim().isEmpty()) {
            desc = "No description provided.";
        }
        tvDescription.setText(desc);

        cbSelect.setChecked(selectedIds.contains(exercise.getId()));

        loadExerciseImage(exercise.getImageUrl(), ivPhoto);

        return convertView;
    }

    private void loadExerciseImage(final String imageUrl, final ImageView imageView) {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            imageView.setImageResource(R.drawable.logo);
            return;
        }

        if (imageCache.containsKey(imageUrl)) {
            imageView.setImageBitmap(imageCache.get(imageUrl));
            return;
        }

        imageView.setImageResource(R.drawable.logo);
        new AsyncTask<String, Void, Bitmap>() {
            @Override
            protected Bitmap doInBackground(String... params) {
                try {
                    InputStream in = new URL(params[0]).openStream();
                    Bitmap bitmap = BitmapFactory.decodeStream(in);
                    if (bitmap != null) {
                        imageCache.put(params[0], bitmap);
                    }
                    return bitmap;
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                if (bitmap != null) {
                    imageView.setImageBitmap(bitmap);
                }
            }
        }.execute(imageUrl);
    }
}
