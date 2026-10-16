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
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import mrgym.R;

public class JournalAdapter extends BaseAdapter {
    private Context context;
    private List<File> photoList;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDelete(int position);
    }

    public JournalAdapter(Context context, List<File> photoList, OnDeleteClickListener deleteListener) {
        this.context = context;
        this.photoList = photoList;
        this.deleteListener = deleteListener;
    }

    @Override
    public int getCount() {
        return photoList.size();
    }

    @Override
    public Object getItem(int position) {
        return photoList.get(position);
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
        Button btnDelete = convertView.findViewById(R.id.btnDeletePhoto);

        File photoFile = photoList.get(position);

        Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
        if (bitmap != null) {
            ivPhoto.setImageBitmap(bitmap);
        }

        long timestamp = photoFile.lastModified();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy  HH:mm", Locale.getDefault());
        tvDate.setText(sdf.format(new Date(timestamp)));

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteListener.onDelete(position);
            }
        });

        return convertView;
    }
}
