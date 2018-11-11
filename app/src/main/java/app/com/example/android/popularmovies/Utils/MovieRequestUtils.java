package app.com.example.android.popularmovies.Utils;

import android.content.Context;

import java.net.URL;
import java.util.List;

import app.com.example.android.popularmovies.Database.MovieInfo;
import app.com.example.android.popularmovies.Database.MovieReview;
import app.com.example.android.popularmovies.Database.MovieTrailer;
import app.com.example.android.popularmovies.Database.ReviewListConverter;
import app.com.example.android.popularmovies.Database.TrailerListConverter;
import app.com.example.android.popularmovies.R;

public final class MovieRequestUtils {

    private MovieRequestUtils(){

    }

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

    public static List<MovieTrailer> getListOfTrailersForMovie(Context context, String movieID){
        String api_key = context.getResources().getString(R.string.MovieAPIKey);
        URL url = NetworkUtils.buildMovieTrailersURL(api_key, movieID);
        try{
            String json = NetworkUtils.getResponseFromHttpUrl(url);
            return MovieJSONUtils.parseMovieTrailerListFromJSON(json);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }

    public static List<MovieReview> getListOfReviewsForMovie(Context context, String movieID){
        String api_key = context.getResources().getString(R.string.MovieAPIKey);
        URL url = NetworkUtils.buildMovieReviewsURL(api_key, movieID);
        try{
            String json = NetworkUtils.getResponseFromHttpUrl(url);
            return MovieJSONUtils.parseMovieReviewListFromJSON(json);
        }
        catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
}
