package udacity_project.mymovieapp;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GridViewAdapter extends BaseAdapter {

    private final Context context;
    private final List<String> urls = new ArrayList<>();

    public GridViewAdapter(Context context, ArrayList<String> u) {
        this.context = context;
       Collections.addAll(urls, getDataUrls(u));

  }

    public String[] getDataUrls(ArrayList<String> urls)
    {
        String[] u = new String[urls.size()];
        for(int i = 0; i < u.length; i++)
        {
            u[i] = urls.get(i);
        }

        return u;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I have get this code from picasa page
       SquaredImageView view = (SquaredImageView) convertView;
        if (view == null) {
            view = new SquaredImageView(context);
            view.setScaleType(ImageView.ScaleType.CENTER_CROP);

        }
        String url = getItem(position);
        //based in instructions
        Picasso.with(context).load(url).into(view);
        return view;
    }

    @Override public int getCount() {
        return urls.size();
    }

    @Override public String getItem(int position) {
        return urls.get(position);
    }

    @Override public long getItemId(int position) {
        return position;
    }
}
