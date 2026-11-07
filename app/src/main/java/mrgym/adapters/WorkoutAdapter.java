package mrgym.adapters;

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
import mrgym.models.WorkoutPlan;
import mrgym.utils.ImageLoader;

public class WorkoutAdapter extends BaseAdapter {
    private Context context;
    private List<WorkoutPlan> workoutPlanList;

    public WorkoutAdapter(Context context, List<WorkoutPlan> workoutPlanList) {
        this.context = context;
        this.workoutPlanList = workoutPlanList;
    }

    @Override
    public int getCount() {
        return workoutPlanList.size();
    }

    @Override
    public Object getItem(int position) {
        return workoutPlanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private String formatWeight(double weight) {
        if (weight <= 0) {
            return "";
        }
        if (weight == Math.floor(weight)) {
            return " @ " + (int) weight + "kg";
        }
        return String.format(java.util.Locale.US, " @ %.1fkg", weight);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_workout, parent, false);
        }

        ImageView ivThumbnail = convertView.findViewById(R.id.ivWorkoutThumbnail);
        TextView tvName = convertView.findViewById(R.id.tvWorkoutName);
        TextView tvExercises = convertView.findViewById(R.id.tvWorkoutExercises);

        WorkoutPlan plan = workoutPlanList.get(position);
        tvName.setText(plan.getName());

        List<Exercise> exercises = plan.getExercises();
        if (exercises != null && !exercises.isEmpty()) {
            String firstImageUrl = null;
            for (Exercise ex : exercises) {
                if (ex.getImageUrl() != null && !ex.getImageUrl().trim().isEmpty()) {
                    firstImageUrl = ex.getImageUrl();
                    break;
                }
            }
            ImageLoader.loadImage(firstImageUrl, ivThumbnail);

            StringBuilder sb = new StringBuilder();
            sb.append(exercises.size()).append(exercises.size() == 1 ? " exercise:\n" : " exercises:\n");
            for (int i = 0; i < exercises.size(); i++) {
                Exercise ex = exercises.get(i);
                int sets = ex.getSets() > 0 ? ex.getSets() : 3;
                int reps = ex.getReps() > 0 ? ex.getReps() : 10;
                String weightStr = formatWeight(ex.getWeight());
                sb.append("• ").append(ex.getName()).append(" (").append(sets).append("×").append(reps).append(weightStr).append(")");
                if (i < exercises.size() - 1) {
                    sb.append("\n");
                }
            }
            tvExercises.setText(sb.toString());
        } else {
            ivThumbnail.setImageResource(R.drawable.logo);
            tvExercises.setText("0 exercises");
        }

        return convertView;
    }
}
