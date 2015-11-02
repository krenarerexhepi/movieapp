package udacity_project.mymovieapp;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


public class ParseData {

    String data;
    public static ArrayList<Movies> movies = null;

    private static final String RESULTS = "results";
    private static final String ORIGINAL_TITLE = "original_title";
    public static final String POSTER_PATH = "poster_path";
    private static final String OVERVIEW = "overview";
    private static final String VOTE_AVERAGE = "vote_average";
    private static final String RELEASE_DATE = "release_date";
    private static final String ID = "id";

    public ParseData(String data)
    {
        this.data = data;
    }

    public ArrayList<Movies> parseMovies()
    {
        ArrayList<Movies> allMoviesList = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(data);
            JSONArray originalTitleArray = jsonObject.getJSONArray(RESULTS);

            for(int i = 0; i < originalTitleArray.length(); i++)
            {
                JSONObject movieresults = originalTitleArray.getJSONObject(i);

                Movies movies = new Movies(movieresults.getString(ORIGINAL_TITLE),
                        movieresults.getString(OVERVIEW),
                        movieresults.getString(POSTER_PATH), movieresults.getString(VOTE_AVERAGE),
                        movieresults.getString(RELEASE_DATE), movieresults.getString(ID));
                allMoviesList.add(movies);

            }
        } catch (JSONException e) {
            Log.e(String.valueOf(R.string.JSONEXceptionMessage), String.valueOf(R.string.JSONException));
        }

        movies = allMoviesList;
        return allMoviesList;
    }
}

