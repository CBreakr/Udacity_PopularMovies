package app.com.example.android.popularmovies.Database;

public class MovieTrailer {

    private String mTitle;
    private String mYoutubeId;

    public MovieTrailer(String title, String youtubeID) {
        this.mTitle = title;
        this.mYoutubeId = youtubeID;
    }

    public String getTrailerTitle() {
        return mTitle;
    }

    public void setTrailerTitle(String mTrailerTitle) {
        this.mTitle = mTrailerTitle;
    }

    public String getYoutubeId() {
        return mYoutubeId;
    }

    public void setYoutubeId(String youtubeID) {
        this.mYoutubeId = youtubeID;
    }
}
