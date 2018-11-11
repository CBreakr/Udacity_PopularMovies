package app.com.example.android.popularmovies;

import android.app.Activity;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.List;

import app.com.example.android.popularmovies.Database.AppDatabase;
import app.com.example.android.popularmovies.Database.MovieInfo;
import app.com.example.android.popularmovies.Database.MovieReview;
import app.com.example.android.popularmovies.Database.MovieTrailer;
import app.com.example.android.popularmovies.Utils.AppExecutors;
import app.com.example.android.popularmovies.Utils.MovieRequestUtils;
import app.com.example.android.popularmovies.ViewModel.MovieAPIViewModel;
import app.com.example.android.popularmovies.ViewModel.MovieViewModel;
import app.com.example.android.popularmovies.ViewModel.MovieViewModelFactory;
import app.com.example.android.popularmovies.ViewModel.ReviewAPIViewModel;
import app.com.example.android.popularmovies.ViewModel.ReviewAPIViewModelFactory;
import app.com.example.android.popularmovies.ViewModel.TrailerAPIViewModel;
import app.com.example.android.popularmovies.ViewModel.TrailerAPIViewModelFactory;

public class MovieDetailActivity extends AppCompatActivity {

    private static final String TAG = MovieDetailActivity.class.getSimpleName();

    private ImageView mPosterImageView;

    private TextView mTitleTextView;
    private TextView mRatingTextView;
    private TextView mReleaseDateTextView;
    private TextView mOverviewTextView;

    private Button mFavoriteButton;

    private TextView mInternetErrorTextView;

    private TextView mErrorMessageTextView;
    private ProgressBar mProgressBar;

    private ScrollView mMainScrollView;

    private LinearLayoutCompat mTrailersLayout;
    private LinearLayoutCompat mReviewsLayout;

    private LayoutInflater mInflater;

    private MovieInfo movie;

    protected boolean mTrailersLoaded = false;
    protected boolean mReviewLoaded = false;

    private AppDatabase mDB;

    /*
    order of events on load for non-favorites view:
    show trailer progress bar
    make trailers async call
    hide trailers progress bar
    show reviews progress bar
    make reviews async call
    hide reviews progress bar
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movie = getMovieFromIntentOrState(savedInstanceState);

        mDB = AppDatabase.getInstance(this);

        if(movie != null) {
            initializeUI();
            checkForMovieInFavoritesDB();
        }
        else{
            showErrorMessage();
        }
    }

    private MovieInfo getMovieFromIntentOrState(Bundle savedInstanceState) {
        String key = this.getString(R.string.MovieParcelableKey);
        if(savedInstanceState == null || !savedInstanceState.containsKey(key)) {
            Intent callingIntent = getIntent();
            if (callingIntent != null && callingIntent.hasExtra(key)) {
                return (MovieInfo) callingIntent.getExtras().getParcelable(key);
            }
        }
        else{
            return savedInstanceState.getParcelable(key);
        }

        return null;
    }

    //
    // SAVE INSTANCE
    // we'll refer to this within the onCreate function
    //
    @Override
    public void onSaveInstanceState(Bundle outState){
        String listKey = this.getString(R.string.MovieParcelableKey);
        outState.putParcelable(listKey, movie);
        super.onSaveInstanceState(outState);
    }

    protected void initializeUI() {
        mPosterImageView = findViewById(R.id.iv_poster_detail);

        mTitleTextView = findViewById(R.id.tv_title);
        mRatingTextView = findViewById(R.id.tv_rating);
        mReleaseDateTextView = findViewById(R.id.tv_release);
        mOverviewTextView = findViewById(R.id.tv_overview);
        mFavoriteButton = findViewById(R.id.favorite_button);

        mInternetErrorTextView = findViewById(R.id.tv_no_internet_message_display);
        mErrorMessageTextView = findViewById(R.id.tv_error_message_display);
        mProgressBar = findViewById(R.id.pb_loading_indicator);

        mMainScrollView = findViewById(R.id.sv_main_view);

        mTrailersLayout = findViewById(R.id.ll_TrailersLayout);
        mReviewsLayout = findViewById(R.id.ll_ReviewsLayout);

        mInflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        setFavoriteButtonClickListener();

        showDataView();
    }

    protected void showDataView(){
        mErrorMessageTextView.setVisibility(View.GONE);
        mInternetErrorTextView.setVisibility(View.GONE);
        mMainScrollView.setVisibility(View.VISIBLE);
    }

    protected void setFavoriteButtonClickListener(){
        mFavoriteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(movie.isFavorite()){
                    movie.setFavorite(false);
                    removeFromFavorites();
                    setFavoriteButtonDisplay();
                }
                else{
                    movie.setFavorite(true);
                    addToFavorites();
                    setFavoriteButtonDisplay();
                }
            }
        });
    }

    private void checkForMovieInFavoritesDB(){
        MovieViewModelFactory factory = new MovieViewModelFactory(mDB, movie.getId());
        final MovieViewModel model = ViewModelProviders.of(this, factory).get(MovieViewModel.class);
        model.getMovie().observe(this, new Observer<MovieInfo>() {
            @Override
            public void onChanged(@Nullable MovieInfo movieEntry) {
                model.getMovie().removeObserver(this);
                if(movieEntry != null){
                    movie = movieEntry;
                }
                fillUI(movie);
            }
        });
    }

    private void removeFromFavorites(){
        final MovieInfo innerMovie = movie;
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDB.movieDao().deleteMovie(innerMovie);
            }
        });
    }

    private void addToFavorites(){
        final MovieInfo innerMovie = movie;
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDB.movieDao().insertMovie(innerMovie);
            }
        });
    }

    protected void setFavoriteButtonDisplay(){
        if(movie.isFavorite()){
            mFavoriteButton.setText(R.string.button_text_UNFAVORITE);
            mFavoriteButton.setBackgroundColor(getResources().getColor(R.color.colorRemove));
        }
        else{
            mFavoriteButton.setText(R.string.button_text_FAVORITE);
            mFavoriteButton.setBackgroundColor(getResources().getColor(R.color.colorAdd));
        }
    }

    protected void showLoadingIndicator() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    protected void hideLoadingIndicator() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    protected void showErrorMessage() {
        mMainScrollView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    protected void fillUI(final MovieInfo movie) {
        if(movie != null){
            Picasso.with(this)
                    .load(movie.getPosterPath())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_sandwich) // I like this ridiculous picture...
                    .into(mPosterImageView);

            mTitleTextView.setText(movie.getTitle());
            mRatingTextView.setText(movie.getRating());
            mReleaseDateTextView.setText(movie.getReleaseDate());
            mOverviewTextView.setText(movie.getOverview());

            setFavoriteButtonDisplay();
            fillTrailersAndReviews();
        }
        else{
            showErrorMessage();
        }
    }

    private void fillTrailersAndReviews(){
        if(movie.getTrailers() != null){
            mTrailersLoaded = true;
            displayTrailers();
        }

        if(movie.getReviews() != null){
            mReviewLoaded = true;
            displayReviews();
        }

        if(!mTrailersLoaded || !mReviewLoaded){
            loadTrailersAndReviews();
        }
    }

    protected void displayTrailers(){
        List<MovieTrailer> trailers = movie.getTrailers();

        for(MovieTrailer trailer : trailers){
            View view = inflateTrailerViewAndFill(trailer);
            if(view != null){
                mTrailersLayout.addView(view);
            }
        }
    }

    protected void displayReviews(){
        List<MovieReview> reviews = movie.getReviews();

        for(MovieReview review : reviews){
            View view = inflateReviewViewAndFill(review);
            if(view != null){
                mReviewsLayout.addView(view);
            }
        }
    }

    private View inflateTrailerViewAndFill(final MovieTrailer trailer){
        View view = mInflater.inflate(R.layout.trailer_list_item, mTrailersLayout, false);

        // title, youtube id
        TextView title = view.findViewById(R.id.tv_trailer_text);
        title.setText(trailer.getTrailerTitle());

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // use the youtube vender type to create the intent
                Intent youtubeAppIntent =
                        new Intent(Intent.ACTION_VIEW
                        , Uri.parse("vnd.youtube:" + trailer.getYoutubeId()));

                Intent youtubeBrowserIntent =
                        new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://www.youtube.com/watch?v=" + trailer.getYoutubeId()));

                // attempt to open the youtube app by default
                // otherwise open in browser
                try {
                    MovieDetailActivity.this.startActivity(youtubeAppIntent);
                }
                catch (Exception ex) {
                    MovieDetailActivity.this.startActivity(youtubeBrowserIntent);
                }
            }
        });

        return view;
    }

    private View inflateReviewViewAndFill(final MovieReview review){
        View view = mInflater.inflate(R.layout.review_list_item, mReviewsLayout, false);

        // text, author, link

        TextView reviewText = view.findViewById(R.id.tv_review_text);
        TextView sourceText = view.findViewById(R.id.tv_review_source);

        reviewText.setText(review.getReviewText());
        sourceText.setText(
                getResources().getString(R.string.review_author)
                + " "  + review.getAuthor()
                + "   " + getResources().getString(R.string.review_external_link)
        );

        sourceText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent =
                        new Intent(Intent.ACTION_VIEW,
                        Uri.parse(review.getLink()));

                MovieDetailActivity.this.startActivity(browserIntent);
            }
        });

        return view;
    }

    //
    // TRAILERS AND REVIEWS
    //

    private void loadTrailersAndReviews(){
        new MovieDetailActivity.InternetCheckThenFetchTask().execute();
    }

    //
    // INTERNET CHECK
    //
    class InternetCheckThenFetchTask extends AsyncTask<Void,Void,Boolean> {

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            showLoadingIndicator();
        }

        @Override protected Boolean doInBackground(Void... voids) {
            try {
                Socket sock = new Socket();
                sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
                sock.close();
                return true;
            }
            catch (IOException e) {
                return false;
            }
        }

        @Override protected void onPostExecute(Boolean internet) {
            if(internet){
                if(!mTrailersLoaded) {
                    trailerViewModelSetup();
                }

                if(!mReviewLoaded){
                    reviewViewModelSetup();
                }
            }
            else {
                showInternetError();
            }
        }
    }

    private void trailerViewModelSetup(){
        final TrailerAPIViewModel model =
                ViewModelProviders
                    .of(this, new TrailerAPIViewModelFactory(this.getApplication(), movie.getId()))
                    .get(TrailerAPIViewModel.class);

        model.getData().observe(MovieDetailActivity.this, new Observer<List<MovieTrailer>>() {
            @Override
            public void onChanged(@Nullable List<MovieTrailer> trailerEntries) {
                model.getData().removeObservers(MovieDetailActivity.this);
                mTrailersLoaded = true;
                movie.setTrailers(trailerEntries);
                displayTrailers();
                if(mReviewLoaded) {
                    hideLoadingIndicator();
                }
            }
        });
    }

    private void reviewViewModelSetup(){
        final ReviewAPIViewModel model =
                ViewModelProviders
                        .of(this, new ReviewAPIViewModelFactory(this.getApplication(), movie.getId()))
                        .get(ReviewAPIViewModel.class);

        model.getData().observe(MovieDetailActivity.this, new Observer<List<MovieReview>>() {
            @Override
            public void onChanged(@Nullable List<MovieReview> reviewEntries) {
                model.getData().removeObservers(MovieDetailActivity.this);
                mReviewLoaded = true;
                movie.setReviews(reviewEntries);
                displayReviews();
                if(mTrailersLoaded) {
                    hideLoadingIndicator();
                }
            }
        });
    }

    protected void showInternetError(){
        mInternetErrorTextView.setVisibility(View.VISIBLE);
    }
}
