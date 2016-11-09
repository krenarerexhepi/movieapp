package udacity_project.mymovieapp.ParseData;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import udacity_project.mymovieapp.MovieReviews;
import udacity_project.mymovieapp.MovieTrailer;
import udacity_project.mymovieapp.R;

/**
 * Created by Krenare Rexhepi on 11/5/2015.
 */
public class ParseTrailer {
    String data;
    public static ArrayList<MovieTrailer> movies = null;

    private static final String RESULTS = "results";
    private static final String TYPE = "type";
    private static final String KEY = "key";
    private static final String NAME = "name";
    private static final String ID = "id";
    private static final String AUTHOR="author";
    private static final String CONTENT="content";

    public ParseTrailer(String data)
    {
        this.data = data;
    }

    public ArrayList<MovieTrailer> parseTrailer()
    {
        ArrayList<MovieTrailer> allMoviesList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray originalTitleArray = jsonObject.getJSONArray(RESULTS);

            for(int i = 0; i < originalTitleArray.length(); i++)
            {
                JSONObject movieresults = originalTitleArray.getJSONObject(i);

                MovieTrailer movies = new MovieTrailer(movieresults.getString(TYPE),
                        movieresults.getString(KEY),
                        movieresults.getString(NAME), movieresults.getString(ID));
                allMoviesList.add(movies);

            }
        } catch (JSONException e) {
            Log.e(String.valueOf(R.string.JSONEXceptionMessage), String.valueOf(R.string.JSONException));
        }

        movies = allMoviesList;
        return allMoviesList;
    }
    public ArrayList<MovieReviews> parseReview()
    {
        ArrayList<MovieReviews> allMoviesList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray originalTitleArray = jsonObject.getJSONArray(RESULTS);

            for(int i = 0; i < originalTitleArray.length(); i++)
            {
                JSONObject movieresults = originalTitleArray.getJSONObject(i);

                MovieReviews movies = new MovieReviews("Author: " + movieresults.getString(AUTHOR),movieresults.getString(CONTENT)
                       , movieresults.getString(ID));
                allMoviesList.add(movies);

            }
        } catch (JSONException e) {
            Log.e(String.valueOf(R.string.JSONEXceptionMessage), String.valueOf(R.string.JSONException));
        }

       return allMoviesList;
    }
}

