package app.com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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

import app.com.example.android.popularmovies.Model.MovieInfo;
import app.com.example.android.popularmovies.Utils.MovieRequestUtils;

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    // TODO maintain sorting between activities
    // I'd like to avoid the reloading and saved scrolling, but I'm really not sure how to do that
    // just use the sharedPreferences to pass the filter type

    // TODO comment the code

    private RecyclerView mRecyclerView;

    private final int fNumberOfColumns = 2;

    private MovieAdapter mMovieAdapter;

    private TextView mInternetErrorTextView;
    private TextView mErrorMessageTextView;

    private ProgressBar mProgressBar;

    public enum FilterType {
        Popular,
        TopRated
    }

    protected FilterType mFilterToUse;

    /*
    There's clearly a lot of clean up and documentation left to do, but I'm honestly not entirely
    sure what the endpoint for this project is, exactly...
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setFilterType(null);

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

        GridLayoutManager layoutManager = new GridLayoutManager(this, fNumberOfColumns);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(layoutManager);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);
        mRecyclerView.setHasFixedSize(true);

        showDataView();
    }

    protected void showDataView(){
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
        mInternetErrorTextView.setVisibility(View.INVISIBLE);
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
            new InternetCheckThenFetchTask().execute();
        }
        else{
            List<MovieInfo> movies = savedInstanceState.<MovieInfo>getParcelableArrayList(listKey);
            mMovieAdapter.setMovieList(movies);
        }
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
            setFilterType(FilterType.Popular);
            showMovieDisplay(null);
            return true;
        }
        else if(id == R.id.top_rated_sort){
            setFilterType(FilterType.TopRated);
            showMovieDisplay(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
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

        @Override protected Boolean doInBackground(Void... voids) { try {
            Socket sock = new Socket();
            sock.connect(new InetSocketAddress("8.8.8.8", 53), 1500);
            sock.close();
            return true;
        } catch (IOException e) { return false; } }

        @Override protected void onPostExecute(Boolean internet) {
            if(internet){
                new FetchMoviesTask(mFilterToUse).execute();
            }
            else{
                hideLoadingIndicator();
                showInternetError();
            }
        }
    }

    //
    // ASYNC TASK
    //
    // the input type doesn't matter
    class FetchMoviesTask extends AsyncTask<Void, Void, List<MovieInfo>> {

        FilterType filter;

        public FetchMoviesTask(FilterType ft) {
            super();
            filter = ft;
        }

        @Override
        protected List<MovieInfo> doInBackground(Void... voids) {
            switch(filter){
                case Popular:
                    return MovieRequestUtils.requestPopularMovies(MainActivity.this);
                case TopRated:
                    return MovieRequestUtils.requestTopRatedMovies(MainActivity.this);
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(List<MovieInfo> movies) {
            super.onPostExecute(movies);
            hideLoadingIndicator();
            fillUI(movies);
        }
    }

    //
    // SHARED PREFERENCES
    //
    protected void setFilterType(FilterType ft){
        if(ft == null){
            ft = getFilterTypeFromSharedPreferences();
        }

        mFilterToUse = ft;
        writeFilterTypeToSharedPreferences(mFilterToUse);
    }

    protected FilterType getFilterTypeFromSharedPreferences(){
        SharedPreferences pref = this.getPreferences(Context.MODE_PRIVATE);
        String filter =
                pref.getString(this.getString(R.string.Key_FilterTypeSharedPreferences)
                , null);
        return getFilterTypeFromString(filter);
    }

    protected void writeFilterTypeToSharedPreferences(FilterType ft){
        SharedPreferences pref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(
                this.getString(R.string.Key_FilterTypeSharedPreferences)
                , getStringFromFilterType(ft));
        editor.commit();
    }

    //
    // because I can't use an enum within SharedPreferences....
    //
    private String getStringFromFilterType(FilterType ft){
        switch(ft){
            case Popular:
                return this.getString(R.string.FilterTypeForSharedPreferences_Popular);
            case TopRated:
                return this.getString(R.string.FilterTypeForSharedPreferences_TopRated);
        }

        return null;
    }

    private FilterType getFilterTypeFromString(String s){
        if (s == this.getString(R.string.FilterTypeForSharedPreferences_TopRated)){
            return FilterType.TopRated;
        }

        // this will always be the default
        return FilterType.Popular;
    }
}
