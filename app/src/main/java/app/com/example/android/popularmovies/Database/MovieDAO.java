package app.com.example.android.popularmovies.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MovieDAO {
    /*
    This won't be using the Update method
     */

    @Query("SELECT * FROM FavoriteMovie")
    LiveData<List<MovieInfo>> loadAllMovies();

    @Insert
    void insertMovie(MovieInfo taskEntry);

    @Delete
    void deleteMovie(MovieInfo taskEntry);

    @Query("SELECT * FROM FavoriteMovie WHERE id = :id")
    LiveData<MovieInfo> loadMovieById(String id);
}
