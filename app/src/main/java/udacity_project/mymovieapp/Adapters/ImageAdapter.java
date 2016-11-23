package udacity_project.mymovieapp.Adapters;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import udacity_project.mymovieapp.SquaredImageView;

/**
 * Created by Krenare Rexhepi on 11/18/2015.
 */
public class ImageAdapter extends BaseAdapter {
    private Context mContext;
    private List<Bitmap> mimages;

    public ImageAdapter(Context c, List<Bitmap> images) {
        mContext = c;
        mimages = images;

    }

    public int getCount() {
        return mimages.size();
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public View getView(int position, View convertView, ViewGroup parent) {
        SquaredImageView imageView = (SquaredImageView) convertView;
        if (convertView == null) {
            imageView = new SquaredImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPaddingRelative(2, 2, 2, 2);
        } else {
            imageView = (SquaredImageView) convertView;
        }

        imageView.setImageBitmap(mimages.get(position));
        return imageView;
    }

}
