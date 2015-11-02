package udacity_project.mymovieapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity {
    GridView movie_grid;
    Constants constants = new Constants();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
            if (savedInstanceState != null) {
            constants.setSave_Sorted_value(savedInstanceState.getString(constants.getSTATE_MOVIES()));
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            movie_grid=(GridView) findViewById(R.id.movie_grid);
            new JSONTask().execute(constants.getMovieUrl() + constants.apiKey + constants.getSave_Sorted_value());
        }
        else
        {
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            movie_grid=(GridView) findViewById(R.id.movie_grid);
       GetPopularMovies();   }


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
public void GetPopularMovies()
{     new JSONTask().execute(constants.getMovieUrl() + constants.apiKey + constants.getSort_popular());
    constants.setSave_Sorted_value(constants.getSort_popular());
}
    public void  GetHighestRatedMovie(){
        new JSONTask().execute(constants.getMovieUrl() + constants.apiKey + constants.getSort_hight());
        constants.setSave_Sorted_value(constants.getSort_hight());

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.refresh) {
       GetPopularMovies();
        }
        if (id == R.id.action_most_popular) {
          GetPopularMovies();;
        }
        if (id == R.id.action_higest_rated) {
            GetHighestRatedMovie();

        }

        return super.onOptionsItemSelected(item);
    }

    public class JSONTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.connect();
                InputStream stream = urlConnection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                return buffer.toString();

            } catch (MalformedURLException e) {
                Log.e(getResources().getString(R.string.MalformedURL), getResources().getString(R.string.MalformedURLMessage));
            } catch (IOException e) {
                Log.e(getResources().getString(R.string.IOExceptionURL), getResources().getString(R.string.IOExceptionMessageURL));
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    Log.e(getResources().getString(R.string.IOException), getResources().getString(R.string.IOExceptionMessage));
                }
            }

            return null;
        }
        GridViewAdapter sg;
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ParseData json ;
            final ArrayList<Movies> movies;
try {
     json = new ParseData(s);
    movies = json.parseMovies();

}
catch (Exception ex)
{
    Toast.makeText(getApplicationContext(), getResources().getString(R.string.NoNetwork), LENGTH_LONG).show();

    return;
}

            ArrayList<String> urlsPoster = new ArrayList<>();
            for (int i = 0; i < movies.size(); i++) {
                Movies thumbnail = movies.get(i);
                String posterPath = getResources().getString(R.string.image_url) + thumbnail.getPoster();
                urlsPoster.add(posterPath);
            }

            sg = new GridViewAdapter(MainActivity.this, urlsPoster);
            movie_grid.setAdapter(sg);

            movie_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra("INDEX", position);
                    startActivity(intent);

                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(constants.getSTATE_MOVIES(),constants.getSave_Sorted_value());
    }
}
