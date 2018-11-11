package app.com.example.android.popularmovies.Database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

@Entity(tableName="FavoriteMovie")
public class MovieInfo implements Parcelable {

    @Ignore
    private static final String mImageBasePath = "https://image.tmdb.org/t/p/w185";

    @PrimaryKey @NonNull
    private String id;
    private String title;
    private String posterPath;
    private String overview;
    private String rating;
    private String releaseDate;
    private boolean isFavorite;

    // use TypeConverters for these
    private List<MovieTrailer> trailers;
    private List<MovieReview> reviews;

    public MovieInfo(){
        isFavorite = false;

/*
        trailers = new ArrayList<MovieTrailer>();
        reviews = new ArrayList<MovieReview>();

        // test data for looking at the display

        // title, youtube id
        MovieTrailer trailer1 = new MovieTrailer("VENOM - Official Teaser Trailer (HD)", "dzxFdtWmjto");
        MovieTrailer trailer2 = new MovieTrailer("VENOM - Official Trailer 2 (HD)", "xLCn88bfW1o");
        trailers.add(trailer1);
        trailers.add(trailer2);

        // text, author, link
        MovieReview review1 = new MovieReview(
                "I honestly don't know what everyone's talking about, _Venom_ is **fine**! I mean... It's not great. There were a few moments I found myself thinking \\\"This bit's pretty bad, why did they do this?\\\""
                , "Gimly"
                , "https://www.themoviedb.org/review/5bd28c050e0a2616cf00459a"
        );
        MovieReview review2 = new MovieReview(
                "I very much like it if you ask me (;"
                , "javajohnny"
                , "https://www.themoviedb.org/review/5bd8df3dc3a3683cef000ea5"
        );
        reviews.add(review1);
        reviews.add(review2);
*/
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
        if(param_poster.startsWith(mImageBasePath)) {
            posterPath = param_poster;
        }
        else{
            posterPath = mImageBasePath + param_poster;
        }
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
    public String getReleaseDate(){
        return releaseDate;
    }

    public void setReleaseDate(String param_release){
        releaseDate = param_release;
    }

    // FAVORITE
    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    // TRAILERS
    public void addTrailer(MovieTrailer trailer){
        trailers.add(trailer);
    }

    public void setTrailers(List<MovieTrailer> trailersList){
        trailers = trailersList;
    }

    public List<MovieTrailer> getTrailers(){
        return trailers;
    }

    // REVIEWS
    public void addReviewLink(MovieReview review){
        reviews.add(review);
    }

    public void setReviews(List<MovieReview> reviewsList){
        reviews = reviewsList;
    }

    public List<MovieReview> getReviews(){
        return reviews;
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
        int fav = in.readInt();
        if(fav == 1){
            this.isFavorite = true;
        }
        else{
            this.isFavorite = false;
        }
        this.trailers = TrailerListConverter.fromString(in.readString());
        this.reviews = ReviewListConverter.fromString(in.readString());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(posterPath);
        dest.writeString(overview);
        dest.writeString(rating);
        dest.writeString(releaseDate);
        int fav;
        if(isFavorite){
            fav = 1;
        }
        else{
            fav = 0;
        }
        dest.writeInt(fav);
        dest.writeString(TrailerListConverter.toString(trailers));
        dest.writeString(ReviewListConverter.toString(reviews));
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
