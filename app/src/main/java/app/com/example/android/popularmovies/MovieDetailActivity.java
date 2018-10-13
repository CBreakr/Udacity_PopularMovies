package app.com.example.android.popularmovies;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import app.com.example.android.popularmovies.Model.MovieInfo;
import app.com.example.android.popularmovies.Utils.MovieRequestUtils;

public class MovieDetailActivity extends AppCompatActivity {

    private ImageView mPosterImageView;

    private TextView mTitleTextView;
    private TextView mRatingTextView;
    private TextView mReleaseDateTextView;
    private TextView mOverviewTextView;

    private TextView mInternetErrorTextView;

    private TextView mErrorMessageTextView;
    private ProgressBar mProgressBar;

    private ScrollView mMainScrollView;

    private MovieInfo movie;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        movie = getMovieFromIntentOrState(savedInstanceState);

        if(movie != null) {
            initializeUI();
            fillUI(movie);
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

        mInternetErrorTextView = findViewById(R.id.tv_no_internet_message_display);
        mErrorMessageTextView = findViewById(R.id.tv_error_message_display);
        mProgressBar = findViewById(R.id.pb_loading_indicator);

        mMainScrollView = findViewById(R.id.sv_main_view);

        showDataView();
    }

    protected void showDataView(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mInternetErrorTextView.setVisibility(View.INVISIBLE);
        mMainScrollView.setVisibility(View.VISIBLE);
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

    protected void fillUI(MovieInfo movie) {
        if(movie != null){
            Picasso.with(this)
                    .load(movie.getPosterPath())
                    .placeholder(R.mipmap.ic_launcher)
                    .error(R.mipmap.ic_sandwich)
                    .into(mPosterImageView);

            mTitleTextView.setText(movie.getTitle());
            mRatingTextView.setText(movie.getRating());
            mReleaseDateTextView.setText(movie.getRelease());
            mOverviewTextView.setText(movie.getOverview());
        }
        else{
            showErrorMessage();
        }
    }
}
