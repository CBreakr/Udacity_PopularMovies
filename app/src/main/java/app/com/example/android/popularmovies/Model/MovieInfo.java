package app.com.example.android.popularmovies.Model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import app.com.example.android.popularmovies.R;

public class MovieInfo implements Parcelable {

    private final String mImageBasePath = "https://image.tmdb.org/t/p/w185";

    private String id;
    private String title;
    private String posterPath;
    private String overview;
    private String rating;
    private String releaseDate;

    public MovieInfo(){

    }

    // ID
    public String getId(){
        return id;
    }

    public void setId(String param_id){
        id = param_id;
    }

    // TITLE
    public String getTitle(){
        return title;
    }

    public void setTitle(String param_title){
        title = param_title;
    }

    // POSTER
    public String getPosterPath(){
        return posterPath;
    }

    //
    // this one is going to compose this into the proper full string
    //
    public void setPosterPath(String param_poster){
        posterPath = mImageBasePath + param_poster;
    }

    // OVERVIEW
    public String getOverview(){
        return overview;
    }

    public void setOverview(String param_overview){
        overview = param_overview;
    }

    // RATING
    public String getRating(){
        return rating;
    }

    public void setRating(String param_rating){
        rating = param_rating;
    }

    // RELEASE
    public String getRelease(){
        return releaseDate;
    }

    public void setRelease(String param_release){
        releaseDate = param_release;
    }

    //
    // PARCELABLE
    //

    MovieInfo (Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.posterPath = in.readString();
        this.overview = in.readString();
        this.rating = in.readString();
        this.releaseDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(rating);
        dest.writeString(releaseDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<MovieInfo> CREATOR
            = new Parcelable.Creator<MovieInfo>() {

        public MovieInfo createFromParcel(Parcel in) {
            return new MovieInfo(in);
        }

        public MovieInfo[] newArray(int size) {
            return new MovieInfo[size];
        }
    };
}
