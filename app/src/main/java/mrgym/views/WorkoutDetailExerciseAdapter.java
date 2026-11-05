package mrgym.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.List;
import mrgym.R;
import mrgym.models.Exercise;
import mrgym.utils.ImageLoader;

public class WorkoutDetailExerciseAdapter extends BaseAdapter {

    private Context context;
    private List<Exercise> exerciseList;

    public WorkoutDetailExerciseAdapter(Context context, List<Exercise> exerciseList) {
        this.context = context;
        this.exerciseList = exerciseList;
    }

    @Override
    public int getCount() {
        return exerciseList != null ? exerciseList.size() : 0;
    }

    @Override
    public Object getItem(int position) {
        return exerciseList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private String formatTarget(int sets, int reps, double weight) {
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
        return sb.toString();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_workout_detail_exercise, parent, false);
        }

        ImageView ivPhoto = convertView.findViewById(R.id.ivDetailExercisePhoto);
        TextView tvName = convertView.findViewById(R.id.tvDetailExerciseName);
        TextView tvSetsReps = convertView.findViewById(R.id.tvDetailExerciseSetsReps);
        TextView tvDesc = convertView.findViewById(R.id.tvDetailExerciseDesc);

        Exercise exercise = exerciseList.get(position);
        tvName.setText(exercise.getName());

        int sets = exercise.getSets() > 0 ? exercise.getSets() : 3;
        int reps = exercise.getReps() > 0 ? exercise.getReps() : 10;
        tvSetsReps.setText(formatTarget(sets, reps, exercise.getWeight()));

        String desc = exercise.getDescription();
        if (desc == null || desc.trim().isEmpty()) {
            desc = "No description provided.";
        }
        tvDesc.setText(desc);

        ImageLoader.loadImage(exercise.getImageUrl(), ivPhoto);

        return convertView;
    }
}
