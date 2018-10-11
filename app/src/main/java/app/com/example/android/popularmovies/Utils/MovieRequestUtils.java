package app.com.example.android.popularmovies.Utils;

import android.content.Context;
import android.util.Log;

import java.net.URL;
import java.util.List;

import app.com.example.android.popularmovies.Model.MovieInfo;
import app.com.example.android.popularmovies.R;

public class MovieRequestUtils {

    public static List<MovieInfo> requestPopularMovies(Context context){
        String api_key = context.getResources().getString(R.string.MovieAPIKey);
        URL url = NetworkUtils.buildPopularMoviesRequestURL(api_key);

        return getListOfMovies(url);
    }

    public static List<MovieInfo> requestTopRatedMovies(Context context){
        String api_key = context.getResources().getString(R.string.MovieAPIKey);
        URL url = NetworkUtils.buildTopRatedMoviesRequestURL(api_key);
        return getListOfMovies(url);
    }

    private static List<MovieInfo> getListOfMovies(URL url){
        try{
            String json = NetworkUtils.getResponseFromHttpUrl(url);
            return MovieJSONUtils.parseMovieListFromJSON(json);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
