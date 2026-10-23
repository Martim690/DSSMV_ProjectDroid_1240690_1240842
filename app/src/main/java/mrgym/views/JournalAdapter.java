package mrgym.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import mrgym.R;
import mrgym.models.JournalEntry;

public class JournalAdapter extends BaseAdapter {

    private Context context;
    private List<JournalEntry> entryList;
    private OnDeleteClickListener deleteListener;
    private OnPhotoClickListener photoClickListener;

    public interface OnDeleteClickListener {
        void onDelete(int position);
    }

    public interface OnPhotoClickListener {
        void onPhotoClick(JournalEntry entry);
    }

    public JournalAdapter(Context context, List<JournalEntry> entryList, OnDeleteClickListener deleteListener, OnPhotoClickListener photoClickListener) {
        this.context = context;
        this.entryList = entryList;
        this.deleteListener = deleteListener;
        this.photoClickListener = photoClickListener;
    }

    @Override
    public int getCount() {
        return entryList.size();
    }

    @Override
    public Object getItem(int position) {
        return entryList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_journal, parent, false);
        }

        ImageView ivPhoto = convertView.findViewById(R.id.ivJournalPhoto);
        TextView tvDate = convertView.findViewById(R.id.tvJournalDate);
        TextView tvMetrics = convertView.findViewById(R.id.tvJournalMetrics);
        Button btnDelete = convertView.findViewById(R.id.btnDeletePhoto);

        final JournalEntry entry = entryList.get(position);

        if (entry.getPhotoPath() != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(entry.getPhotoPath());
            if (bitmap != null) {
                ivPhoto.setImageBitmap(bitmap);
            }
        }

        ivPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (photoClickListener != null) {
                    photoClickListener.onPhotoClick(entry);
                }
            }
        });

        long timestamp = entry.getTimestamp();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvDate.setText(sdf.format(new Date(timestamp)));

        StringBuilder metricsBuilder = new StringBuilder();
        if (entry.getWeight() > 0) {
            metricsBuilder.append("Weight: ").append(entry.getWeight()).append(" kg");
        }
        if (entry.getHeight() > 0) {
            if (metricsBuilder.length() > 0) {
                metricsBuilder.append("  •  ");
            }
            metricsBuilder.append("Height: ").append(entry.getHeight()).append(" cm");
        }

        if (metricsBuilder.length() > 0) {
            tvMetrics.setText(metricsBuilder.toString());
            tvMetrics.setVisibility(View.VISIBLE);
        } else {
            tvMetrics.setVisibility(View.GONE);
        }

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteListener.onDelete(position);
            }
        });

        return convertView;
    }
}
