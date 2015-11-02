package udacity_project.mymovieapp;

/**
 * Created by Krenare Rexhepi on 11/1/2015.
 * Modified by Krenare Rexhepi on 11/2/2015.
 */
public class Constants {
    private String movieUrl ="https://api.themoviedb.org/3/discover/movie?api_key=";
    public static final String apiKey ="";
    private String sort_popular ="&sort_by=popularity.desc";
    private String sort_hight ="&sort_by=vote_average.desc";
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

    public String getSTATE_MOVIES() {
        return STATE_MOVIES;
    }

    public String getSave_Sorted_value() {
        return save_Sorted_value;
    }

    public void setSave_Sorted_value(String save_Sorted_value) {
        this.save_Sorted_value = save_Sorted_value;
    }
}
