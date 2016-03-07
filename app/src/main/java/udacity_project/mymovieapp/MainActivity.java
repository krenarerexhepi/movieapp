package udacity_project.mymovieapp;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.facebook.stetho.Stetho;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import udacity_project.mymovieapp.Adapters.GridViewAdapter;
import udacity_project.mymovieapp.Adapters.ImageAdapter;
import udacity_project.mymovieapp.ParseData.ParseData;

import static android.widget.Toast.LENGTH_LONG;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    GridView movie_grid;
    Constants constants = new Constants();
    private ArrayList<Movies> moviesList;
    int rotation;
    ArrayList<Movies> movies;
    Bundle sss;
    String selectedCriter = "";
    boolean mTwoPanel;
    boolean getFavoriteClicked = false;
    CursorLoader cursorLoader;
    boolean display_optionsMenu = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            constants.setSave_Sorted_value(savedInstanceState.getString(constants.getSTATE_MOVIES()));
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            movie_grid = (GridView) findViewById(R.id.movie_grid);
            new JSONTask().execute(constants.getMovieUrl() + Constants.apiKey + constants.getSave_Sorted_value());
            moviesList = savedInstanceState.getParcelableArrayList(getResources().getString(R.string.movie));
            sss = savedInstanceState;
        } else {
            setContentView(R.layout.activity_main);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            movie_grid = (GridView) findViewById(R.id.movie_grid);
            GetPopularMovies();
            ArrayList<Movies> movies = ParseData.movies;
            moviesList = movies;
        }

        if (isTablet(getApplicationContext())) {
            mTwoPanel = true;
        }
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(
                                Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(
                                Stetho.defaultInspectorModulesProvider(this))
                        .build());
        Display display = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        rotation = display.getRotation();

        if ((rotation == 1 || rotation == 3) && mTwoPanel) {
            if (constants.getSave_Sorted_value().equals("fav")) {
                LoadFavInRotate();
            }
        }
    }

    private void LoadFavInRotate() {
        movie_grid = (GridView) findViewById(R.id.movie_grid);
        GetFavoriteMovies();
        movie_grid.onSaveInstanceState();
        setContentView(R.layout.activity_main_fragment);
        constants.setSave_Sorted_value(constants.getSort_favorites());
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }


    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        display_optionsMenu = true;
        int id = item.getItemId();
        if (id == R.id.refresh) {
            GetPopularMovies();
        }
        if (id == R.id.action_most_popular) {
            GetPopularMovies();

        }
        if (id == R.id.action_higest_rated) {
            GetHighestRatedMovie();
            movie_grid.onSaveInstanceState();
        }
        if (id == R.id.action_favorites) {
            if ((rotation == 1 || rotation == 3) && mTwoPanel) {
                //  we load here new intent for favorites
                selectedCriter = constants.getSort_favorites();
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                intent.putExtra("INDEX", 0);
                intent.putExtra("INDEXX", selectedCriter);
                startActivity(intent);

            } else {
                GetFavoriteMovies();
                movie_grid.onSaveInstanceState();
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressLint("ShowToast")
    private void GetFavoriteMovies() {
        getFavoriteClicked = true;
        getSupportLoaderManager().initLoader(1, null, this);
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
                StringBuilder buffer = new StringBuilder();
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
            ParseData json;

            try {
                json = new ParseData(s);
                movies = json.parseMovies();

            } catch (Exception ex) {
                if (moviesList != null) {
                    if (moviesList.size() != 0 && constants.getSave_Sorted_value().equals("fav")) {
                        GetFavoriteMovies();

                    } else {
                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.NoNetwork), LENGTH_LONG).show();
                        movie_grid.setAdapter(null);
                    }
                } else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.NoNetwork), LENGTH_LONG).show();
                }

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
            if ((rotation == 1 || rotation == 3) && mTwoPanel) {
                if (!display_optionsMenu) {
                    setContentView(R.layout.activity_main_fragment);
                }
                Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
                setSupportActionBar(toolbar);
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);

            }
            selectedCriter = constants.getSave_Sorted_value();
            movie_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra("INDEX", position);
                    intent.putExtra("INDEXX", selectedCriter);
                    startActivity(intent);

                }
            });
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (moviesList == null) {
            moviesList = movies;
        }
        outState.putParcelableArrayList(getResources().getString(R.string.movie), moviesList);
        outState.putString(constants.getSTATE_MOVIES(), constants.getSave_Sorted_value());
        super.onSaveInstanceState(outState);

    }

    public void GetPopularMovies() {
        new JSONTask().execute(constants.getMovieUrl() + Constants.apiKey + constants.getSort_popular());
        constants.setSave_Sorted_value(constants.getSort_popular());
    }

    public void GetHighestRatedMovie() {
        new JSONTask().execute(constants.getMovieUrl() + Constants.apiKey + constants.getSort_hight());
        constants.setSave_Sorted_value(constants.getSort_hight());

    }

    public String getMyData() {
        return constants.getSave_Sorted_value();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        cursorLoader = new CursorLoader(this, MyProvider.CONTENT_URI, null, null, null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        ArrayList<Movies> m = new ArrayList<>();
        List<Bitmap> img = new ArrayList<>();
        data.moveToFirst();
        while (data.getCount() > 0) {
            String id = data.getString(data.getColumnIndex("id"));
            String title = data.getString(data.getColumnIndex("name"));
            String overview = data.getString(data.getColumnIndex("overview"));
            String raiting = data.getString(data.getColumnIndex("raiting"));
            String releasedate = data.getString(data.getColumnIndex("releasedate"));

            byte[] image = data.getBlob(data.getColumnIndex("icon"));
            img.add(Constants.getImage(image));
            m.add(new Movies(title, overview, "", raiting, releasedate, id));
            if (data.isLast()) {
                break;
            } else {
                data.moveToNext();
            }
        }
        if (constants.getSave_Sorted_value().equals("fav")) {

            movie_grid = (GridView) findViewById(R.id.movie_grid);
            movie_grid.setAdapter(new ImageAdapter(this, img));
            selectedCriter = constants.getSort_favorites();
            movie_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra("INDEX", position);
                    intent.putExtra("INDEXX", selectedCriter);
                    startActivity(intent);

                }
            });

        } else {

            moviesList = m;
            constants.setSave_Sorted_value(constants.getSort_favorites());
            selectedCriter = getMyData();
            movie_grid.setAdapter(new ImageAdapter(this, img));
            movie_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                    intent.putExtra("INDEX", position);
                    intent.putExtra("INDEXX", selectedCriter);
                    startActivity(intent);

                }
            });
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

}
