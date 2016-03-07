package udacity_project.mymovieapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import udacity_project.mymovieapp.Adapters.MovieAdapter;
import udacity_project.mymovieapp.Adapters.ReviewAdapter;
import udacity_project.mymovieapp.Adapters.TrailerAdapter;
import udacity_project.mymovieapp.ParseData.ParseData;
import udacity_project.mymovieapp.ParseData.ParseTrailer;

import static android.widget.Toast.LENGTH_LONG;

public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public DetailActivityFragment() {
    }

    private ArrayList<Movies> moviesList;
    ListView listTrailers, listRewiev;
    GridView movie_grid;
    MovieAdapter movieAdapter;
    GridViewAdapter gmovieAdapter;
    Movies selectedMovie;
    TextView title, relase_date, raiting, overwi;
    ImageView img;
    int selectedMovieValue = 0;
    int rotation;
    Constants constants = new Constants();
    ImageButton favButton;
    CursorLoader cursorLoader;
    ArrayList<Movies> moviess;
    Bitmap bitmapIcon;

    @Override
    public void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        if (saveInstanceState == null || !saveInstanceState.containsKey(getResources().getString(R.string.movie))) {
            ArrayList<Movies> movies = ParseData.movies;
            moviesList = movies;
        } else {
            moviesList = saveInstanceState.getParcelableArrayList(getResources().getString(R.string.movie));
            constants.setSave_Sorted_value(saveInstanceState.getString(constants.getSTATE_MOVIES()));

        }
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(getResources().getString(R.string.movie), moviesList);
        outState.putString(constants.getSTATE_MOVIES(), constants.getSave_Sorted_value());
        super.onSaveInstanceState(outState);
    }

    boolean inRotate;
    boolean inRotateMain;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view;
        view = inflater.inflate(R.layout.fragment_detail, container, false);
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
        }
        movieAdapter = new MovieAdapter(getActivity(), moviesList);
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        rotation = display.getRotation();
        if (isTablet(getContext()) && (rotation == 3 || rotation == 1)) {
            if ((getActivity().getClass() == MainActivity.class)) {
                MainActivity activity = (MainActivity) getActivity();
                String myDataFromActivity = activity.getMyData();
                if (myDataFromActivity.equals("fav")) {
                    moviess = getFavMoviesList();
                    movie_grid = activity.movie_grid;
                    movieAdapter = new MovieAdapter(getActivity(), moviess);
                    constants.setSave_Sorted_value(constants.getSort_favorites());
                    inRotateMain = true;
                } else {
                    new JSONTaskGetTrailer().execute(constants.getMovieUrl() + constants.apiKey + myDataFromActivity);
                }
            } else {
                DetailActivity activity = (DetailActivity) getActivity();
                String myDataFromActivity = activity.getMyData();
                if (myDataFromActivity.equals("fav")) {
                    moviess = getFavMoviesList();
                    moviesList = getFavMoviesList();
                    movieAdapter = new MovieAdapter(getActivity(), moviess);
                    inRotate = true;

                } else {
                    new JSONTaskGetTrailer().execute(constants.getMovieUrl() + constants.apiKey + myDataFromActivity);
                }
            }
        }
        Intent intent = getActivity().getIntent();
        selectedMovieValue = intent.getIntExtra("INDEX", 0);
        String vlera = intent.getStringExtra("INDEXX");
        if (vlera != null) {
            if (vlera.equals("fav")) {
                moviess = getFavMoviesList();
                movieAdapter = new MovieAdapter(getActivity(), moviess);
            }
        }
        if (selectedMovieValue == 0) {
            if (moviess != null) {
                selectedMovie = moviess.get(0);
            }
            selectedMovie = movieAdapter.getItem(0);
        } else {
            if (moviess != null) {
                selectedMovie = moviess.get(selectedMovieValue);
            } else {

                if (movieAdapter != null) {
                    selectedMovie = movieAdapter.getItem(selectedMovieValue);
                }
            }
        }
        title = (TextView) view.findViewById(R.id.detail_title);
        relase_date = (TextView) view.findViewById(R.id.release_date_detail);
        raiting = (TextView) view.findViewById(R.id.user_raiting_detail);
        overwi = (TextView) view.findViewById(R.id.overview_detail);
        img = (ImageView) view.findViewById(R.id.detail_imageView);
        listTrailers = (ListView) view.findViewById(R.id.trailerList);
        listRewiev = (ListView) view.findViewById(R.id.rewievList);
        favButton = (ImageButton) view.findViewById(R.id.favoriteButton);


        favButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

              String name = title.getText().toString();
                if (isChecked(title.getText().toString())) {
                    String mSelectionClause = MyProvider.name + " LIKE ?";
                    String[] mSelectionArgs = {name};

                    getContext().getContentResolver().delete(
                            MyProvider.CONTENT_URI,   // the user dictionary content URI
                            mSelectionClause,                    // the column to select on
                            mSelectionArgs                      // the value to compare to
                    );

                    Cursor c = getContext().getContentResolver().query(MyProvider.CONTENT_URI, mSelectionArgs, null, null, null);
                    c.getCount();
                    c.getColumnIndex("idT");
                    Toast.makeText(getContext(), "Data  deleted", Toast.LENGTH_LONG)
                            .show();

                } else {
                    favButton.setImageResource(R.drawable.star_pressed);

                    Bitmap image = ((BitmapDrawable) img.getDrawable()).getBitmap();
                    ContentValues values = new ContentValues();
                    values.put(MyProvider.name, title.getText().toString());
                    values.put(MyProvider.releasedate, relase_date.getText().toString());
                    values.put(MyProvider.raiting, raiting.getText().toString());
                    values.put(MyProvider.overview, overwi.getText().toString());
                    values.put(MyProvider.icon, Constants.getBytes(image));

                    Uri uri = getContext().getContentResolver().insert(MyProvider.CONTENT_URI, values);
                    Toast.makeText(getContext(), "New record inserted", Toast.LENGTH_LONG)
                            .show();

                    Cursor c = getContext().getContentResolver().query(MyProvider.CONTENT_URI, null, null, null, null);
                    c.getCount();

                    ContentValues valuesT = new ContentValues();
                    Adapter a = listTrailers.getAdapter();
                    if (a != null) {
                        for (int i = 0; i < a.getCount(); i++) {
                            String trailerUrl = ((TrailerAdapter) a).trailersUrl.get(i).toString();
                            String trailerName = ((TrailerAdapter) a).trailerName.get(i).toString();
                            valuesT.put(MyProvider.idT, c.getCount());
                            valuesT.put(MyProvider.trailer, trailerUrl);
                            valuesT.put(MyProvider.trailerName, trailerName);
                            getContext().getContentResolver().insert(MyProvider.CONTENT_URI_T, valuesT);
                        }
                    }
                    // inserting trailer in database
                    ContentValues valuesR = new ContentValues();
                    Adapter b = listRewiev.getAdapter();
                    if (b != null) {
                        for (int i = 0; i < b.getCount(); i++) {
                            String author = ((ReviewAdapter) b).author.get(i);
                            String content = ((ReviewAdapter) b).content.get(i);
                            valuesR.put(MyProvider.idR, c.getCount());
                            valuesR.put(MyProvider.review, content);
                            valuesR.put(MyProvider.author, author);
                            getContext().getContentResolver().insert(MyProvider.CONTENT_URI_R, valuesR);

                        }
                    }
                }
                ;
            }
        });
        if (vlera != null) {
            if (vlera.equals("fav")) {
                getActivity().getSupportLoaderManager().initLoader(1, null, this);
                //  selectedMovieValue = selectedMovieValue + 1;
                LoadTrailersAndReview(selectedMovieValue);
                favButton.setImageResource(R.drawable.star_pressed);

            } else {
                SetMovieDetails(selectedMovie);

            }
        }
        if (inRotateMain) {
            GetFavoriteMovies();
            favButton.setImageResource(R.drawable.star_pressed);

        }
        if (isChecked(title.getText().toString())) {
            favButton.setImageResource(R.drawable.star_pressed);
        }
        setHasOptionsMenu(true);
        return view;

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == 1) {
            selectedMovieValue = selectedMovieValue + 1;
            cursorLoader = new CursorLoader(getContext(), MyProvider.CONTENT_URI, null, "id=" + selectedMovieValue, null, null);
            return cursorLoader;
        } else if (id == 2) {
            cursorLoader = new CursorLoader(getContext(), MyProvider.CONTENT_URI, null, null, null, null);
            return cursorLoader;
        } else {
            return cursorLoader;
        }

    }

    private void SetMovieDetails(Movies selectedMovie) {

        title.setText(selectedMovie.getTitle());
        // if (isChecked(selectedMovie.getTitle())) {
        //     favButton.setImageResource(R.drawable.star_pressed);
        //  }
        overwi.setText(selectedMovie.getOverview());
        relase_date.setText(selectedMovie.getDate());
        raiting.setText(selectedMovie.getRating());
        String image = selectedMovie.getPoster();
        String ImgUrl = getResources().getString(R.string.image_url) + image;
        Picasso.with((getActivity()).getApplicationContext())
                .load(ImgUrl).placeholder(R.drawable.userplaceholder)
                .error(R.drawable.user_placeholder_error)
                .into(img);
        GetVideos(selectedMovie.getId());
        GetReview(selectedMovie.getId());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.refresh) {
            GetPopularMovies();

        }
        if (id == R.id.action_most_popular) {
            GetPopularMovies();

        }
        if (id == R.id.action_higest_rated) {
            GetHightMovies();

        }
        if (id == R.id.action_favorites) {
            GetFavoriteMovies();

        }
        if (id == R.id.menu_item_share) {
            // dispaly share Intent
            ShareActionProvider mShare = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");

            if (trailers != null) {
                if (trailers.size() > 0) {
                    shareIntent.putExtra(Intent.EXTRA_TEXT, trailers.get(0).toString());
                    mShare.setShareIntent(shareIntent);
                }
            } else if (urltrailer != null) {
                if (urltrailer.size() > 0) {
                    shareIntent.putExtra(Intent.EXTRA_TEXT, urltrailer.get(0).toString());
                    mShare.setShareIntent(shareIntent);

                }

            } else {
                Toast.makeText(getContext(), "No trailer to share ", LENGTH_LONG).show();
                return super.onOptionsItemSelected(item);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void GetFavoriteMovies() {

        Cursor data = getContext().getContentResolver().query(MyProvider.CONTENT_URI, null, null, null, null);
        List<Movies> m = new ArrayList<>();
        List<Bitmap> images = new ArrayList<>();
        assert data != null;
        data.moveToFirst();
        while (data.getCount() > 0) {
            String id = data.getString(data.getColumnIndex("id"));
            String titleName = data.getString(data.getColumnIndex("name"));

            String overview = data.getString(data.getColumnIndex("overview"));
            String raitingScore = data.getString(data.getColumnIndex("raiting"));
            String releasedate = data.getString(data.getColumnIndex("releasedate"));
            byte[] image = data.getBlob(data.getColumnIndex("icon"));
            bitmapIcon = Constants.getImage(image);
            images.add(bitmapIcon);
            m.add(new Movies(titleName, overview, bitmapIcon, raitingScore, releasedate, id));
            if (data.isLast()) {
                break;
            } else {
                data.moveToNext();
            }
        }
        data.close();
        ImageAdapter image2 = new ImageAdapter(getContext(), images);
        if (movie_grid == null) {
            GridView activity = (GridView) getActivity().findViewById(R.id.movie_grid);
            this.movie_grid = activity;

        }
        movie_grid.setAdapter(image2);
        final MovieAdapter FavMovieAdapter = new MovieAdapter(getActivity(), m);
        if (GetMovieForFavoriteByPosition(0, FavMovieAdapter)) {
            this.movie_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GetMovieForFavoriteByPosition(position, FavMovieAdapter);

                }
            });
        } else {
            return;

        }
    }

    private boolean GetMovieForFavoriteByPosition(int position, MovieAdapter FavMovieAdapter) {
        if (FavMovieAdapter.getCount() != 0) {
            selectedMovie = FavMovieAdapter.getItem(position);
            title.setText(selectedMovie.getTitle());
            //  if (isChecked(selectedMovie.getTitle())) {
            //       favButton.setImageResource(R.drawable.star_pressed);
            //   }
            overwi.setText(selectedMovie.getOverview());
            relase_date.setText(selectedMovie.getDate());
            raiting.setText(selectedMovie.getRating());
            img.setImageBitmap(selectedMovie.getPosterB());
            ListLoad(position + 1);
            return true;
        } else {
            Toast.makeText(getContext(), "No data in favorite", Toast.LENGTH_LONG).show();
            return false;
        }
    }

    public void GetVideos(String id) {
        new JSONTaskGetTrailer().execute("http://api.themoviedb.org/3/movie/" + id + "/videos?api_key=" + Constants.apiKey);

    }

    public void GetReview(String id) {
        new JSONTaskGetTrailer().execute("http://api.themoviedb.org/3/movie/" + id + "/reviews?api_key=" + Constants.apiKey);
    }

    public void GetPopularMovies() {
        new JSONTaskGetTrailer().execute(constants.getMovieUrl() + constants.apiKey + constants.getSort_popular());
    }

    public void GetHightMovies() {
        new JSONTaskGetTrailer().execute(constants.getMovieUrl() + constants.apiKey + constants.getSort_hight());
    }


    @Override
    public void onViewCreated(View view, Bundle savedataInstance) {
        // super.onViewCreated(view,savedataInstance);
    }


    public class JSONTaskGetTrailer
            extends AsyncTask<String, String, String> {

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

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            ParseTrailer json;
            ArrayList<MovieTrailer> movies = new ArrayList<>();
            ParseData jsons = new ParseData(s);
            moviess = new ArrayList<>();

            json = new ParseTrailer(s);
            if (s != null) {
                if (s.contains("YouTube")) {
                    movies = json.parseTrailer();
                    LoadTrailerList(movies);
                } else if (s.contains("poster_path")) {
                    moviess = jsons.parseMovies();
                    ArrayList<String> urlsPoster = new ArrayList<>();
                    for (int i = 0; i < moviess.size(); i++) {
                        Movies thumbnail = moviess.get(i);
                        String posterPath = getResources().getString(R.string.image_url) + thumbnail.getPoster();
                        urlsPoster.add(posterPath);

                    }
                    gmovieAdapter = new GridViewAdapter(getActivity(), urlsPoster);
                    Fragment movie_grida = getFragmentManager().findFragmentById(R.id.fragment_main);
                    movie_grid = (GridView) movie_grida.getActivity().findViewById(R.id.movie_grid);

                    SetMovieDetails(moviess.get(selectedMovieValue));
                    movie_grid.setAdapter(gmovieAdapter);
                    movieAdapter = new MovieAdapter(getActivity(), moviess);

                    movie_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                            SetMovieDetails(movieAdapter.getItem(position));

                        }
                    });
                } else {
                    LoadReviewList(json);
                }

            }
        }

        private void LoadReviewList(ParseTrailer json) {
            ArrayList<MovieReviews> moviesReview;
            moviesReview = json.parseReview();
            ArrayList<String> reviewsContent = new ArrayList<>();
            ArrayList<String> reviewsAuthor = new ArrayList<>();

            for (int i = 0; i < moviesReview.size(); i++) {
                MovieReviews thumbnail = moviesReview.get(i);
                String author = thumbnail.getAuthor();
                String content = thumbnail.getContent();
                reviewsAuthor.add(author);
                reviewsContent.add(content);
            }
            ReviewAdapter adapter = new ReviewAdapter(DetailActivityFragment.this, reviewsAuthor, reviewsContent);
            listRewiev.setAdapter(adapter);
        }

        private void LoadTrailerList(ArrayList<MovieTrailer> movies) {
            trailers = new ArrayList<>();
            ArrayList<String> trailerName = new ArrayList<>();
            ArrayList<Integer> prgmImages = new ArrayList<>();

            for (int i = 0; i < movies.size(); i++) {
                MovieTrailer thumbnail = movies.get(i);
                String posterPath = "https://www.youtube.com/watch?v=" + thumbnail.getKey();
                String name = thumbnail.getName();
                trailers.add(posterPath);
                trailerName.add(name);
                prgmImages.add(R.drawable.play_trailer_image);
            }

            TrailerAdapter adapter = new TrailerAdapter(DetailActivityFragment.this, trailerName, prgmImages, trailers);
            listTrailers.setAdapter(adapter);
            listTrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = null, chooser = null;
                    intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(trailers.get(position)));
                    chooser = Intent.createChooser(intent, "Launch video");

                    startActivity(chooser);


                }
            });
        }

    }

    ArrayList<String> trailers;

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        ArrayList<Movies> m = new ArrayList<>();
        List<Bitmap> images = new ArrayList<>();
        data.moveToFirst();
        while (data.getCount() > 0) {
            String id = data.getString(data.getColumnIndex("id"));
            String titleName = data.getString(data.getColumnIndex("name"));
            String overview = data.getString(data.getColumnIndex("overview"));
            String raitingScore = data.getString(data.getColumnIndex("raiting"));
            String releasedate = data.getString(data.getColumnIndex("releasedate"));
            byte[] image = data.getBlob(data.getColumnIndex("icon"));
            bitmapIcon = Constants.getImage(image);

            title.setText(titleName);
            overwi.setText(overview);
            relase_date.setText(releasedate);
            raiting.setText(raitingScore);
            img.setImageBitmap(bitmapIcon);

            images.add(bitmapIcon);
            m.add(new Movies(titleName, overview, bitmapIcon, raitingScore, releasedate, id));
            if (data.isLast()) {
                break;
            } else {
                data.moveToNext();
            }
        }
        data.close();
        moviesList = getFavMoviesList();

        ListLoad(selectedMovieValue);

    }

    public ArrayList<Movies> getFavMoviesList() {
        Cursor data = getContext().getContentResolver().query(MyProvider.CONTENT_URI, null, null, null, null);
        ArrayList<Movies> m = new ArrayList<>();
        data.moveToFirst();
        while (data.getCount() > 0) {
            String id = data.getString(data.getColumnIndex("id"));
            String titleName = data.getString(data.getColumnIndex("name"));
            String overview = data.getString(data.getColumnIndex("overview"));
            String raitingScore = data.getString(data.getColumnIndex("raiting"));
            String releasedate = data.getString(data.getColumnIndex("releasedate"));
            byte[] image = data.getBlob(data.getColumnIndex("icon"));
            m.add(new Movies(titleName, overview, "", raitingScore, releasedate, id));
            if (data.isLast()) {
                break;
            } else {
                data.moveToNext();
            }
        }
        data.close();
        return m;
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // getSupportLoaderManager().initLoader(1, null, this);
    }

    ArrayList<String> urltrailer;

    public void ListLoad(int selectedMovieValue) {
        LoadTrailersAndReview(selectedMovieValue);
        LoadGrid();

    }

    private void LoadTrailersAndReview(int selectedMovieValue) {
        ArrayList<String> name;
        ArrayList<Integer> prgmImages;
        // Load trailer from databese with cursor for selected movie
        Cursor cursor = getContext().getContentResolver().query(MyProvider.CONTENT_URI_T, null, "idT=" + selectedMovieValue, null, null);
        name = new ArrayList<>();
        urltrailer = new ArrayList<>();
        prgmImages = new ArrayList<>();
        cursor.moveToFirst();

        while (cursor.getCount() > 0) {
            String trailer = cursor.getString(cursor.getColumnIndex("trailer"));
            String trailerName = cursor.getString(cursor.getColumnIndex("trailerName"));
            name.add(trailerName);
            urltrailer.add(trailer);
            prgmImages.add(R.drawable.play_trailer_image);
            if (cursor.isLast()) {
                break;
            } else {
                cursor.moveToNext();
            }
        }
        cursor.close();
        TrailerAdapter a = new TrailerAdapter(DetailActivityFragment.this, name, prgmImages, urltrailer);
        this.listTrailers.setAdapter(a);
        listTrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intent = null, chooser = null;
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(urltrailer.get(position)));
                chooser = Intent.createChooser(intent, "Launch video");

                startActivity(chooser);


            }
        });

        Cursor cursorReview = getContext().getContentResolver().query(MyProvider.CONTENT_URI_R, null, "idR=" + selectedMovieValue, null, null);
        ArrayList<String> author = new ArrayList<>();
        ArrayList<String> content = new ArrayList<>();
        cursorReview.moveToFirst();
        while (cursorReview.getCount() > 0) {
            String auth = cursorReview.getString(cursorReview.getColumnIndex("author"));
            String cont = cursorReview.getString(cursorReview.getColumnIndex("review"));
            author.add(auth);
            content.add(cont);
            if (cursorReview.isLast()) {
                break;
            } else {
                cursorReview.moveToNext();
            }
        }
        cursorReview.close();
        ReviewAdapter reviewAdapter = new ReviewAdapter(DetailActivityFragment.this, author, content);
        this.listRewiev.setAdapter(reviewAdapter);
    }

    public void LoadGrid() {
        final ArrayList<Movies> adapterForFavorite;
        if (inRotate) {

            final Cursor cursorImage = getContext().getContentResolver().query(MyProvider.CONTENT_URI, null, null, null, null);
            adapterForFavorite = new ArrayList<>();
            List<Bitmap> img = new ArrayList<>();
            cursorImage.moveToFirst();
            while (cursorImage.getCount() > 0) {
                String id = cursorImage.getString(cursorImage.getColumnIndex("id"));
                String title = cursorImage.getString(cursorImage.getColumnIndex("name"));
                String overview = cursorImage.getString(cursorImage.getColumnIndex("overview"));
                String raiting = cursorImage.getString(cursorImage.getColumnIndex("raiting"));
                String releasedate = cursorImage.getString(cursorImage.getColumnIndex("releasedate"));

                byte[] image = cursorImage.getBlob(cursorImage.getColumnIndex("icon"));
                img.add(Constants.getImage(image));
                adapterForFavorite.add(new Movies(title, overview, Constants.getImage(image), raiting, releasedate, id));
                if (cursorImage.isLast()) {
                    break;
                } else {
                    cursorImage.moveToNext();
                }
            }
            cursorImage.close();
            Fragment movie_grida = getFragmentManager().findFragmentById(R.id.fragment_main);
            movie_grid = (GridView) movie_grida.getActivity().findViewById(R.id.movie_grid);
            movie_grid.setAdapter(new ImageAdapter(getActivity(), img));
            movie_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    GetMovieForFavoriteByPosition(position, new MovieAdapter((Activity) getContext(), adapterForFavorite));
                }
            });
        }


    }

    public boolean isChecked(String name) {
        Cursor cursorName = getContext().getContentResolver().query(MyProvider.CONTENT_URI, null, null, null, null);
        ArrayList<String> Name = new ArrayList<>();
        cursorName.moveToFirst();
        while (cursorName.getCount() > 0) {
            String namee = cursorName.getString(cursorName.getColumnIndex("name"));
            if (namee.equals(name)) {
                return true;
            }
            if (cursorName.isLast()) {
                break;
            } else {
                cursorName.moveToNext();
            }
        }
        cursorName.close();
        return false;
    }

}
