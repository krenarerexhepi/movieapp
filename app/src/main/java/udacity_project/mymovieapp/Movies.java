package udacity_project.mymovieapp;

import android.os.Parcel;
import android.os.Parcelable;

public class Movies implements Parcelable {
    String title, description, poster, rating, date, id;

    public Movies(String title, String description, String poster, String rating, String date, String id) {
        this.title = title;
        this.description = description;
        this.poster = poster;
        this.rating = rating;
        this.date = date;
        this.id = id;
    }

    protected Movies(Parcel in) {
        title = in.readString();
        description = in.readString();
        poster = in.readString();
        rating = in.readString();
        date = in.readString();
        id = in.readString();
    }

    public static final Creator<Movies> CREATOR = new Creator<Movies>() {
        @Override
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        @Override
        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return description;
    }


    public String getPoster() {
        return poster;
    }


    public String getRating() {
        return rating;
    }

    public String getDate() {
        return date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(description);
        dest.writeString(poster);
        dest.writeString(rating);
        dest.writeString(date);
        dest.writeString(id);
    }
}

