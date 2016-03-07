package udacity_project.mymovieapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;

/**
 * Created by Krenare Rexhepi on 11/1/2015.
 * Modified by Krenare Rexhepi on 11/2/2015.
 */
public class Constants {
    private String movieUrl ="https://api.themoviedb.org/3/discover/movie?api_key=";
    public static final String apiKey ="c64c93b167ede97449fbac5121afe208";
    private String sort_popular ="&sort_by=popularity.desc";
    private String sort_hight ="&sort_by=vote_average.desc";
    private String sort_favorites="fav";
    private  String STATE_MOVIES = "state";
    private String save_Sorted_value = "save";

    public String getMovieUrl() {
        return movieUrl;
    }

    public String getSort_popular() {
        return sort_popular;
    }
    public String getSort_hight() {
        return sort_hight;
    }
    public String getSort_favorites() {
        return sort_favorites;
    }
    public String getSTATE_MOVIES() {
        return STATE_MOVIES;
    }

    public String getSave_Sorted_value() {
        return save_Sorted_value;
    }

    public void setSave_Sorted_value(String save_Sorted_value) {
        this.save_Sorted_value = save_Sorted_value;
    }
    public static Bitmap getImage(byte[] image) {
        Bitmap bm= null;
        try {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.outHeight = 50;
            options.outWidth = 50;
            options.inSampleSize = 2;

            bm = BitmapFactory.decodeByteArray(image, 0, image.length,options);



     }
        catch (Exception ex)
        {
            bm.recycle();
           System.gc();
        }
        return bm;
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,0, stream);
        return stream.toByteArray();
    }



}


