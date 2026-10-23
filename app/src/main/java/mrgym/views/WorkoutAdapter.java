package mrgym.views;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import mrgym.R;
import mrgym.models.Exercise;
import mrgym.models.WorkoutPlan;

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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_workout, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvWorkoutName);
        TextView tvExercises = convertView.findViewById(R.id.tvWorkoutExercises);

        WorkoutPlan plan = workoutPlanList.get(position);
        tvName.setText(plan.getName());

        List<Exercise> exercises = plan.getExercises();
        if (exercises != null && !exercises.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(exercises.size()).append(" exercises: ");
            for (int i = 0; i < exercises.size(); i++) {
                sb.append(exercises.get(i).getName());
                if (i < exercises.size() - 1) {
                    sb.append(", ");
                }
            }
            tvExercises.setText(sb.toString());
        } else {
            tvExercises.setText("0 exercises");
        }

        return convertView;
    }
}
