package udacity_project.mymovieapp;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment {

    public  DetailActivityFragment(){}
    private ArrayList<Movies> moviesList;

    @Override
    public void onCreate(Bundle saveInstanceState)
    {
        super.onCreate(saveInstanceState);
        if(saveInstanceState == null || !saveInstanceState.containsKey(getResources().getString(R.string.movie)))
        {  ArrayList<Movies> movies = ParseData.movies;
            moviesList = movies;
        }
        else
        {
            moviesList = saveInstanceState.getParcelableArrayList(getResources().getString(R.string.movie));
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(getResources().getString(R.string.movie), moviesList);
        super.onSaveInstanceState(outState);
    }

    MovieAdapter movieAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        movieAdapter = new MovieAdapter(getActivity(),moviesList);
        Intent intent = getActivity().getIntent();
        int selectedMovieValue= intent.getIntExtra("INDEX", 0);
        Movies selectedMovie =  movieAdapter.getItem(selectedMovieValue);

        TextView title = (TextView)view.findViewById(R.id.detail_title);

        TextView relase_date = (TextView)view.findViewById(R.id.release_date_detail);
        TextView raiting = (TextView)view.findViewById(R.id.user_raiting_detail);
        TextView overwi = (TextView)view.findViewById(R.id.overview_detail);
        ImageView img = (ImageView) view.findViewById(R.id.detail_imageView);

        title.setText(selectedMovie.getTitle());
        overwi.setText(selectedMovie.getOverview());
        
        relase_date.setText(selectedMovie.getDate());
        raiting.setText(selectedMovie.getRating());


        String image = selectedMovie.getPoster();
        String ImgUrl= getResources().getString(R.string.image_url)+image;
        Picasso.with((getActivity()).getApplicationContext())
              .load(ImgUrl)
            .into(img);
        return   view;

    }
}
