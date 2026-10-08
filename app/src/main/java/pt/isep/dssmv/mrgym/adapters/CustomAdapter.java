package pt.isep.dssmv.mrgym.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import java.util.List;
import pt.isep.dssmv.mrgym.models.Exercise;
import pt.isep.dssmv.mrgym.R;

public class CustomAdapter extends BaseAdapter {
    private Context context;
    private List<Exercise> exerciseList;

    public CustomAdapter(Context context, List<Exercise> exerciseList) {
        this.context = context;
        this.exerciseList = exerciseList;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvExerciseName);
        TextView tvDesc = convertView.findViewById(R.id.tvExerciseDesc);

        Exercise exercise = exerciseList.get(position);
        tvName.setText(exercise.getName());
        tvDesc.setText(exercise.getDescription());

        return convertView;
    }
}
