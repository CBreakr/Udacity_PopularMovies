package app.com.example.android.popularmovies.ViewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import app.com.example.android.popularmovies.Database.AppDatabase;

public class MovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final AppDatabase mDB;
    private final String mMovieID;

    public MovieViewModelFactory(AppDatabase DB, String id){
        mDB = DB;
        mMovieID = id;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        //noinspection unchecked
        return (T) new MovieViewModel(mDB, mMovieID);
    }
}
