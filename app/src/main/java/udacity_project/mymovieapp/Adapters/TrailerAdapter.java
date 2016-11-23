package udacity_project.mymovieapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import udacity_project.mymovieapp.DetailActivityFragment;
import udacity_project.mymovieapp.R;

/**
 * Created by Krenare Rexhepi on 11/4/2015.
 */
public class TrailerAdapter extends BaseAdapter {

  public  ArrayList<String> trailerName;
    Context context;
    ArrayList<Integer> imageId;
   public ArrayList<String> trailersUrl;
    private static LayoutInflater inflater = null;


    public TrailerAdapter(DetailActivityFragment mainActivity, ArrayList<String> prgmNameList, ArrayList<Integer> prgmImages ,ArrayList<String> trailers) {
        trailerName = prgmNameList;
        context = mainActivity.getContext();
        imageId = prgmImages;
        trailersUrl=trailers;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

     @Override
    public int getCount() {
        return trailerName.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder {
        TextView trailerName;
        ImageView img;
    }

    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
         Holder holder = new Holder();
        View rowView;

            rowView = inflater.inflate(R.layout.trailer_list, null);
            holder.trailerName = (TextView) rowView.findViewById(R.id.txtTrailer);
            holder.img = (ImageView) rowView.findViewById(R.id.image_trailer);
            holder.trailerName.setText(trailerName.get(position));
            holder.img.setImageResource(imageId.get(position));
            return rowView;



    }

}