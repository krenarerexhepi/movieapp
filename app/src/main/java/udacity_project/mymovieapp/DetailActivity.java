package udacity_project.mymovieapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import udacity_project.mymovieapp.Adapters.ReviewAdapter;
import udacity_project.mymovieapp.Adapters.TrailerAdapter;
import udacity_project.mymovieapp.ParseData.ParseData;


public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {


    private ArrayList<Movies> moviesList;
    int rotation;
    ImageButton im;
    TextView title, date, raiting, description;
    ImageView img;
    DetailActivityFragment fragment;
    Constants constants = new Constants();
    Bundle ooo;
    String myDataFromActivity;
    ListView listRewiev, listTrailers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null || !savedInstanceState.containsKey(getResources().getString(R.string.movie))) {
            ArrayList<Movies> movies = ParseData.movies;
            moviesList = movies;
            ooo = savedInstanceState;

        } else {
            moviesList = savedInstanceState.getParcelableArrayList(getResources().getString(R.string.movie));
            constants.setSave_Sorted_value(savedInstanceState.getString(constants.getSave_Sorted_value()));
        }
        Display display = ((WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        rotation = display.getRotation();
        if (isTablet(getApplicationContext()) && (rotation == 3 || rotation == 1)) {
            onSaveInstanceState(savedInstanceState);
            myDataFromActivity = getIntent().getStringExtra("INDEXX");
            setContentView(R.layout.activity_main_fragment);
            fragment = new DetailActivityFragment();
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

        } else {
            setContentView(R.layout.activity_detail);
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            fragment = new DetailActivityFragment();
            im = (ImageButton) findViewById(R.id.favoriteButton);
            title = (TextView) findViewById(R.id.detail_title);
            date = (TextView) findViewById(R.id.release_date_detail);
            raiting = (TextView) findViewById(R.id.user_raiting_detail);
            description = (TextView) findViewById(R.id.overview_detail);
            img = (ImageView) findViewById(R.id.detail_imageView);
            listTrailers = (ListView) findViewById(R.id.trailerList);
            listRewiev = (ListView) findViewById(R.id.rewievList);

            im.setOnClickListener(new View.OnClickListener() {
                                      @Override
                                      public void onClick(View view) {
                                          if (isChecked(title.getText().toString())) {

                                              String mSelectionClause = MyProvider.name + " LIKE ?";
                                              String[] mSelectionArgs = {title.getText().toString()};

                                              int mRowsDeleted = 0;

                                              mRowsDeleted = getContentResolver().delete(
                                                      MyProvider.CONTENT_URI,   // the user dictionary content URI
                                                      mSelectionClause,                    // the column to select on
                                                      mSelectionArgs                      // the value to compare to
                                              );
                                         /*     Uri uri = MyProvider.CONTENT_URI;
                                              getApplicationContext().getContentResolver().delete(uri,
                                                      "name" + title.getText().toString(),
                                                      new String[]{title.getText().toString()});
                                              im.setImageResource(R.drawable.star_first);
                                              Toast.makeText(getApplicationContext(), "Record deleted", Toast.LENGTH_LONG)
                                                      .show();*/

                                          } else {
                                              Bitmap image = ((BitmapDrawable) img.getDrawable()).getBitmap();
                                              insertFavoriteData(title.getText().toString(), date.getText().toString(),
                                                      raiting.getText().toString(), description.getText().toString(),
                                                      Constants.getBytes(image), listTrailers, listRewiev);
                                              im.setImageResource(R.drawable.star_pressed);
                                          }


                                      }
                                  }
            );

        }
    }

    public boolean isChecked(String name) {
        Cursor cursorName = getApplicationContext().getContentResolver().query(MyProvider.CONTENT_URI, null, null, null, null);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (outState == null) {
            outState = ooo;
        } else {
            outState.putParcelableArrayList(getResources().getString(R.string.movie), moviesList);
            outState.putString(constants.getSTATE_MOVIES(), constants.getSave_Sorted_value());
            super.onSaveInstanceState(outState);
        }

    }

    public void insertFavoriteData(String title, String date, String rating, String desc, byte[] img,
                                   ListView list_trailer, ListView list_review) {

        ContentValues values = new ContentValues();
        values.put(MyProvider.name, title);
        values.put(MyProvider.releasedate, date);
        values.put(MyProvider.raiting, rating);
        values.put(MyProvider.overview, desc);
        values.put(MyProvider.icon, img);
        Uri uri = getContentResolver().insert(MyProvider.CONTENT_URI, values);

        // we here are geting cursor to know which is last regjistered value
        Cursor c = getContentResolver().query(MyProvider.CONTENT_URI, null, null, null, null);
        c.getCount();

        // inserting trailer in database
        ContentValues valuesT = new ContentValues();
        Adapter a = list_trailer.getAdapter();
        if (a != null) {
            for (int i = 0; i < a.getCount(); i++) {
                String trailerUrl = ((TrailerAdapter) a).trailersUrl.get(i).toString();
                String trailerName = ((TrailerAdapter) a).trailerName.get(i).toString();
                valuesT.put(MyProvider.idT, c.getCount());
                valuesT.put(MyProvider.trailer, trailerUrl);
                valuesT.put(MyProvider.trailerName, trailerName);
                getContentResolver().insert(MyProvider.CONTENT_URI_T, valuesT);
            }

        }
        // inserting trailer in database
        ContentValues valuesR = new ContentValues();
        Adapter b = list_review.getAdapter();
        if (b != null) {
            for (int i = 0; i < b.getCount(); i++) {
                String author = ((ReviewAdapter) b).author.get(i);
                String content = ((ReviewAdapter) b).content.get(i);
                valuesR.put(MyProvider.idR, c.getCount());
                valuesR.put(MyProvider.review, content);
                valuesR.put(MyProvider.author, author);
                getContentResolver().insert(MyProvider.CONTENT_URI_R, valuesR);
            }

        }
        Toast.makeText(getBaseContext(), "New record inserted", Toast.LENGTH_LONG)
                .show();
        c.close();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if (myDataFromActivity != null && !myDataFromActivity.isEmpty()) {
            getMenuInflater().inflate(R.menu.menu_main, menu);
            return true;
        } else {
            super.onCreateOptionsMenu(menu);
            getMenuInflater().inflate(R.menu.menu_share, menu);
            menu.getItem(0).setIcon(R.drawable.share_menu_img);
            return true;

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_most_popular) {
            myDataFromActivity = constants.getSort_popular();
        }
        if (id == R.id.action_higest_rated) {
            myDataFromActivity = constants.getSort_hight();
        }
        if (id == R.id.action_favorites) {
            myDataFromActivity = constants.getSort_favorites();
        }
        return super.onOptionsItemSelected(item);
    }

    public static boolean isTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;

    }


    public String getMyData() {
        return myDataFromActivity;
    }

    CursorLoader cursorLoader;

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        cursorLoader = new CursorLoader(this, MyProvider.CONTENT_URI, null, "id=4", null, null);
        return cursorLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        ArrayList<Movies> m = new ArrayList<>();
        List<Bitmap> img = new ArrayList<>();
        while (data.getCount() > 0) {
            data.moveToFirst();
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
        moviesList = m;
        onSaveInstanceState(ooo);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // getSupportLoaderManager().initLoader(1, null, this);
    }

}
