package udacity_project.mymovieapp.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import udacity_project.mymovieapp.DetailActivityFragment;
import udacity_project.mymovieapp.R;

/**
 * Created by Krenare Rexhepi on 11/9/2015.
 */
public class ReviewAdapter extends BaseAdapter {

 public    ArrayList<String> author;
    Context context;
 public    ArrayList<String> content;
    private static LayoutInflater inflater = null;

    public ReviewAdapter(DetailActivityFragment mainActivity, ArrayList<String> author, ArrayList<String> content) {
        this.author = author;
        context = mainActivity.getContext();
        this.content = content;
        inflater = (LayoutInflater) context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return author.size();
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
        TextView author;
        TextView content;
    }


    @Override
    public View getView(final int position, final View convertView, ViewGroup parent) {
        Holder holder = new Holder();
        View rowView;

            rowView = inflater.inflate(R.layout.review_list, null);
            holder.author = (TextView) rowView.findViewById(R.id.txtAuthor);
            holder.content = (TextView) rowView.findViewById(R.id.txtContent);
            holder.author.setText(author.get(position));
            holder.content.setText(content.get(position));
            return  rowView;

    }

}