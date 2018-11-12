package app.com.example.android.popularmovies;

import android.app.Activity;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import app.com.example.android.popularmovies.Database.AppDatabase;
import app.com.example.android.popularmovies.Database.MovieInfo;
import app.com.example.android.popularmovies.Utils.FilterUtils;
import app.com.example.android.popularmovies.Utils.MovieRequestUtils;
import app.com.example.android.popularmovies.ViewModel.MainViewModel;
import app.com.example.android.popularmovies.ViewModel.MovieAPIViewModel;
import app.com.example.android.popularmovies.ViewModel.MovieViewModel;

public class MainActivity extends AppCompatActivity implements MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();

    private RecyclerView mRecyclerView;

    private final int PORTRAIT_NumberOfColumns = 2;
    private final int LANDSCAPE_NumberOfColumns = 4;

    private MovieAdapter mMovieAdapter;

    private TextView mInternetErrorTextView;
    private TextView mErrorMessageTextView;

    private ProgressBar mProgressBar;

    protected FilterUtils.FilterType mFilterToUse;

    private AppDatabase mDB;
    private MainViewModel mViewModel;

    /*
    There's clearly a lot of clean up and documentation left to do, but I'm honestly not entirely
    sure what the endpoint for this project is, exactly...
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FilterUtils.setFilterType(null, this);
        mFilterToUse = FilterUtils.getFilterTypeFromSharedPreferences(this);

        initializeUI();
        showMovieDisplay(savedInstanceState);
    }

    //
    // SAVE INSTANCE
    // we'll refer to this within the onCreate function
    //
    @Override
    public void onSaveInstanceState(Bundle outState){
        String listKey = this.getString(R.string.MovieListParcelableKey);
        outState.putParcelableArrayList(listKey, (ArrayList<MovieInfo>) mMovieAdapter.getMovieList());
        super.onSaveInstanceState(outState);
    }

    protected void initializeUI() {
        mRecyclerView = findViewById(R.id.recyclerview_movie_list);
        mInternetErrorTextView = findViewById(R.id.tv_no_internet_message_display);
        mErrorMessageTextView = findViewById(R.id.tv_error_message_display);
        mProgressBar = findViewById(R.id.pb_loading_indicator);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, PORTRAIT_NumberOfColumns));
        }
        else{
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, LANDSCAPE_NumberOfColumns));
        }

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
        mRecyclerView.setHasFixedSize(true);

        showDataView();
    }

    protected void showDataView(){
        mErrorMessageTextView.setVisibility(View.GONE);
        mInternetErrorTextView.setVisibility(View.GONE);
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    protected void showLoadingIndicator() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    protected void hideLoadingIndicator() {
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    protected void showErrorMessage() {
        mRecyclerView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }

    protected void showInternetError(){
        mRecyclerView.setVisibility(View.INVISIBLE);
        mInternetErrorTextView.setVisibility(View.VISIBLE);
    }

    protected void fillUI(List<MovieInfo> movies) {
        if(movies != null && movies.size() > 0){
            mMovieAdapter.setMovieList(movies);
        }
        else{
            showErrorMessage();
        }
    }

    private void showMovieDisplay(Bundle savedInstanceState){
        // if we already have it, then no need to make a call to the API
        String listKey = this.getString(R.string.MovieListParcelableKey);
        if(savedInstanceState == null || !savedInstanceState.containsKey(listKey)) {
            if(FilterUtils.isFavoriteMode(this)){
                setupViewModel();
            }
            else{
                new InternetCheckThenFetchTask().execute();
            }
        }
        else{
            List<MovieInfo> movies = savedInstanceState.<MovieInfo>getParcelableArrayList(listKey);
            mMovieAdapter.setMovieList(movies);
        }
    }

    private void setupViewModel(){
        mDB = AppDatabase.getInstance(this);
        mViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mViewModel.getMovies().observe(this, new Observer<List<MovieInfo>>() {
            @Override
            public void onChanged(@Nullable List<MovieInfo> movieEntries) {
                Log.d(TAG, "Updating list of tasks from LiveData in ViewModel");
                mMovieAdapter.setMovieList(movieEntries);
            }
        });
    }

    private void switchToIndividualMovieActivity(MovieInfo movie){
        Context context = this;
        Class detailActivityClass = MovieDetailActivity.class;
        Intent movieIntent = new Intent(context, detailActivityClass);
        movieIntent.putExtra(this.getString(R.string.MovieParcelableKey), movie);
        startActivity(movieIntent);
    }

    //
    // CLICK
    //
    @Override
    public void onClick(MovieInfo movie) {
        switchToIndividualMovieActivity(movie);
    }

    //
    // MENU
    //
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.popular_sort) {
            FilterUtils.setFilterType(FilterUtils.FilterType.Popular, this);
            mFilterToUse = FilterUtils.FilterType.Popular;

            disconnectPotentialViewModelObserver();

            showMovieDisplay(null);
            return true;
        }
        else if(id == R.id.top_rated_sort){
            FilterUtils.setFilterType(FilterUtils.FilterType.TopRated, this);
            mFilterToUse = FilterUtils.FilterType.TopRated;

            disconnectPotentialViewModelObserver();

            showMovieDisplay(null);
            return true;
        }
        else if(id == R.id.favorite_sort){
            FilterUtils.setFilterType(FilterUtils.FilterType.Favorite, this);
            mFilterToUse = FilterUtils.FilterType.Favorite;
            showMovieDisplay(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void disconnectPotentialViewModelObserver(){
        if(mViewModel != null && mViewModel.getMovies().hasObservers()){
            mViewModel.getMovies().removeObservers(this);
            mViewModel = null;
        }
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
                // TODO favorite mode
                // if we're in favorites mode, attempt to get the data from the DB
                return false;
            }
        }

        @Override protected void onPostExecute(Boolean internet) {
            if(internet){
                final MovieAPIViewModel model =
                        ViewModelProviders.of(MainActivity.this)
                                .get(
                                        FilterUtils.getCurrentFilterTypeAsString(MainActivity.this)
                                        , MovieAPIViewModel.class);
                model.getData().observe(MainActivity.this, new Observer<List<MovieInfo>>() {
                    @Override
                    public void onChanged(@Nullable List<MovieInfo> movieEntries) {
                        model.getData().removeObservers(MainActivity.this);
                        hideLoadingIndicator();
                        fillUI(movieEntries);
                    }
                });
            }
            else{
                hideLoadingIndicator();
                showInternetError();
            }
        }
    }
}
