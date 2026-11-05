package mrgym.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import mrgym.R;

public class ImageLoader {

    private static final Map<String, Bitmap> imageCache = new HashMap<>();

    public static void loadImage(final String imageUrl, final ImageView imageView) {
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
