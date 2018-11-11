package app.com.example.android.popularmovies.Database;

public class MovieReview {

    private String mReviewText;
    private String mAuthor;
    private String mLink;

    public MovieReview(String reviewText, String author, String link){
        mReviewText = reviewText;
        mAuthor = author;
        mLink = link;
    }

    public String getReviewText() {
        return mReviewText;
    }

    public void setReviewText(String reviewText) {
        this.mReviewText = reviewText;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public void setAuthor(String author) {
        this.mAuthor = author;
    }

    public String getLink() {
        return mLink;
    }

    public void setLink(String link) {
        this.mLink = link;
    }
}
