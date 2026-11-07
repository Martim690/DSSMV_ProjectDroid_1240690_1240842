package mrgym.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mrgym.R;
import mrgym.models.Exercise;
import mrgym.utils.ImageLoader;

public class ExerciseSelectAdapter extends BaseAdapter {

    public interface OnSelectionChangedListener {
        void onSelectionChanged(int selectedCount);
    }

    private Context context;
    private List<Exercise> displayedList;
    private List<Exercise> originalList;
    private Set<Integer> selectedIds;
    private OnSelectionChangedListener listener;

    public ExerciseSelectAdapter(Context context, List<Exercise> exerciseList, OnSelectionChangedListener listener) {
        this.context = context;
        this.displayedList = new ArrayList<>(exerciseList);
        this.originalList = new ArrayList<>(exerciseList);
        this.selectedIds = new HashSet<>();
        this.listener = listener;
    }

    public void updateData(List<Exercise> list) {
        this.originalList = new ArrayList<>(list);
        this.displayedList.clear();
        this.displayedList.addAll(list);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        displayedList.clear();
        if (query == null || query.trim().isEmpty()) {
            displayedList.addAll(originalList);
        } else {
            String lower = query.toLowerCase().trim();
            for (Exercise ex : originalList) {
                if (ex.getName() != null && ex.getName().toLowerCase().contains(lower)) {
                    displayedList.add(ex);
                }
            }
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return displayedList.size();
    }

    @Override
    public Object getItem(int position) {
        return displayedList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void toggleSelection(int position) {
        if (position >= 0 && position < displayedList.size()) {
            Exercise exercise = displayedList.get(position);
            toggleExercise(exercise, null);
        }
    }

    private void toggleExercise(Exercise exercise, CheckBox cbSelect) {
        if (selectedIds.contains(exercise.getId())) {
            selectedIds.remove(exercise.getId());
        } else {
            selectedIds.add(exercise.getId());
        }
        if (cbSelect != null) {
            cbSelect.setChecked(selectedIds.contains(exercise.getId()));
        }
        notifyDataSetChanged();
        if (listener != null) {
            listener.onSelectionChanged(selectedIds.size());
        }
    }

    public List<Exercise> getSelectedExercises() {
        List<Exercise> selected = new ArrayList<>();
        if (originalList != null) {
            for (Exercise ex : originalList) {
                if (selectedIds.contains(ex.getId())) {
                    selected.add(ex);
                }
            }
        }
        return selected;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_exercise_select, parent, false);
        }

        ImageView ivPhoto = convertView.findViewById(R.id.ivExercisePhoto);
        TextView tvName = convertView.findViewById(R.id.tvExerciseName);
        TextView tvDescription = convertView.findViewById(R.id.tvExerciseDescription);
        CheckBox cbSelect = convertView.findViewById(R.id.cbExerciseSelect);

        final TextView tvSetsValue = convertView.findViewById(R.id.tvSetsValue);
        Button btnMinusSets = convertView.findViewById(R.id.btnMinusSets);
        Button btnPlusSets = convertView.findViewById(R.id.btnPlusSets);

        final TextView tvRepsValue = convertView.findViewById(R.id.tvRepsValue);
        Button btnMinusReps = convertView.findViewById(R.id.btnMinusReps);
        Button btnPlusReps = convertView.findViewById(R.id.btnPlusReps);

        final EditText etWeightValue = convertView.findViewById(R.id.etWeightValue);

        final Exercise exercise = displayedList.get(position);
        tvName.setText(exercise.getName());

        String desc = exercise.getDescription();
        if (desc == null || desc.trim().isEmpty()) {
            desc = "No description provided.";
        }
        tvDescription.setText(desc);

        cbSelect.setChecked(selectedIds.contains(exercise.getId()));

        // Directly wire the click on the whole row and on the checkbox.
        // This is necessary because child Buttons/EditTexts in a ListView row
        // intercept touch events, preventing onItemClickListener from ever firing.
        View.OnClickListener toggleClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleExercise(exercise, cbSelect);
            }
        };
        convertView.setOnClickListener(toggleClick);
        tvName.setOnClickListener(toggleClick);
        tvDescription.setOnClickListener(toggleClick);
        ivPhoto.setOnClickListener(toggleClick);

        if (exercise.getSets() <= 0) {
            exercise.setSets(3);
        }
        if (exercise.getReps() <= 0) {
            exercise.setReps(10);
        }

        tvSetsValue.setText(String.valueOf(exercise.getSets()));
        tvRepsValue.setText(String.valueOf(exercise.getReps()));

        // Remove old text watcher to avoid recycling collision
        if (etWeightValue.getTag() instanceof TextWatcher) {
            etWeightValue.removeTextChangedListener((TextWatcher) etWeightValue.getTag());
        }

        if (exercise.getWeight() > 0) {
            if (exercise.getWeight() == Math.floor(exercise.getWeight())) {
                etWeightValue.setText(String.valueOf((int) exercise.getWeight()));
            } else {
                etWeightValue.setText(String.valueOf(exercise.getWeight()));
            }
        } else {
            etWeightValue.setText("");
        }

        TextWatcher weightWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String val = s.toString().trim();
                if (!val.isEmpty()) {
                    try {
                        exercise.setWeight(Double.parseDouble(val));
                    } catch (NumberFormatException e) {
                        exercise.setWeight(0.0);
                    }
                } else {
                    exercise.setWeight(0.0);
                }
            }
        };

        etWeightValue.addTextChangedListener(weightWatcher);
        etWeightValue.setTag(weightWatcher);

        ImageLoader.loadImage(exercise.getImageUrl(), ivPhoto);

        btnMinusSets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exercise.getSets() > 1) {
                    exercise.setSets(exercise.getSets() - 1);
                    tvSetsValue.setText(String.valueOf(exercise.getSets()));
                }
            }
        });

        btnPlusSets.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exercise.getSets() < 20) {
                    exercise.setSets(exercise.getSets() + 1);
                    tvSetsValue.setText(String.valueOf(exercise.getSets()));
                }
            }
        });

        btnMinusReps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exercise.getReps() > 1) {
                    exercise.setReps(exercise.getReps() - 1);
                    tvRepsValue.setText(String.valueOf(exercise.getReps()));
                }
            }
        });

        btnPlusReps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (exercise.getReps() < 100) {
                    exercise.setReps(exercise.getReps() + 1);
                    tvRepsValue.setText(String.valueOf(exercise.getReps()));
                }
            }
        });

        return convertView;
    }
}
