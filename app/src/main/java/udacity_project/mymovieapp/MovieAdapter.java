package udacity_project.mymovieapp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;
//* I get this example from ArrayExample and modif for my project  /
public class MovieAdapter extends ArrayAdapter<Movies> {

    public MovieAdapter(Activity context, List<Movies> androidFlavors) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // the second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for two TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, androidFlavors);
    }

    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The AdapterView position that is requesting a view
     * @param convertView The recycled view to populate.
     *                    (search online for "android view recycling" to learn more)
     * @param parent The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
        Movies movies = getItem(position);

        // Adapters recycle views to AdapterViews.
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_detail, parent, false);
        }
        ImageView iconView = (ImageView) convertView.findViewById(R.id.detail_imageView);

        Picasso.with(getContext())
               .load(movies.getPoster())
             .into(iconView);

        TextView versionNameView = (TextView) convertView.findViewById(R.id.detail_title);
        versionNameView.setText(movies.getTitle());

        TextView versionNumberView = (TextView) convertView.findViewById(R.id.overview_detail);
        versionNumberView.setText(movies.getOverview());
        return convertView;
    }
}
