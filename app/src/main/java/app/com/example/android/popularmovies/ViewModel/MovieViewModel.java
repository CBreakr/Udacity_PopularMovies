package app.com.example.android.popularmovies.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import app.com.example.android.popularmovies.Database.AppDatabase;
import app.com.example.android.popularmovies.Database.MovieInfo;

public class MovieViewModel extends ViewModel {

    private LiveData<MovieInfo> movie;

    public MovieViewModel(AppDatabase DB, String id){
        movie = DB.movieDao().loadMovieById(id);
    }

    public LiveData<MovieInfo> getMovie() {
        return movie;
    }
}
