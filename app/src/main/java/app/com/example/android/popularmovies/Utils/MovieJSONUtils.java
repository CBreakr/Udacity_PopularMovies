package app.com.example.android.popularmovies.Utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.com.example.android.popularmovies.Model.MovieInfo;

public class MovieJSONUtils {

    private static final String JSON_ERROR_STATUS_CODE = "status_code";
    private static final String JSON_MAIN_LIST = "results";
    private static final String JSON_MOVIE_ID = "id";
    private static final String JSON_MOVIE_TITLE = "title";
    private static final String JSON_MOVIE_POSTER = "poster_path";
    private static final String JSON_MOVIE_OVERVIEW = "overview";
    private static final String JSON_MOVIE_RATING = "vote_average";
    private static final String JSON_MOVIE_RELEASE = "release_date";

    public static MovieInfo parseMovieFromJSON(String json){
        try {
            JSONObject jo = new JSONObject(json);
            if(!checkForJSONError(jo)){
                return createMovieFromJSONObject(jo);
            }
        }
        catch (Exception ex){

        }

        return null;
    }

    public static List<MovieInfo> parseMovieListFromJSON(String json){

        try{
            JSONObject jo = new JSONObject(json);
            if(!checkForJSONError(jo)){
                List<MovieInfo> movies = new ArrayList<MovieInfo>();

                JSONArray jsonMovies = jo.optJSONArray(JSON_MAIN_LIST);

                if(jsonMovies != null) {
                    for (int index = 0; index < jsonMovies.length(); index++) {
                        MovieInfo movie = createMovieFromJSONObject(jsonMovies.optJSONObject(index));
                        if(movie != null) {
                            movies.add(movie);
                        }
                    }

                    return movies;
                }
            }
        }
        catch(Exception ex){

        }

        return null;
    }

    private static MovieInfo createMovieFromJSONObject(JSONObject jo){
        // if the initial JSON object is null, we obviously can't proceed
        if(jo == null){
            return null;
        }

        MovieInfo movie = new MovieInfo();

        // id and poster photo are always required
        movie.setId(jo.optString(JSON_MOVIE_ID, null));
        movie.setPosterPath((jo.optString(JSON_MOVIE_POSTER, null)));

        // if we didn't get both of these, we can't use this movie
        if(movie.getId() == null || movie.getPosterPath() == null){
            return null;
        }

        movie.setTitle(jo.optString(JSON_MOVIE_TITLE, "Title Missing"));
        movie.setOverview(jo.optString(JSON_MOVIE_OVERVIEW, "Overview Missing"));
        movie.setRating(jo.optString(JSON_MOVIE_RATING, "Rating Missing"));
        movie.setRelease(jo.optString(JSON_MOVIE_RELEASE, "Release Date Missing"));

        return movie;
    }

    private static boolean checkForJSONError(JSONObject jo){
        if(
            jo == null
            || jo.has(JSON_ERROR_STATUS_CODE)
            || !(jo.has(JSON_MAIN_LIST) || jo.has(JSON_MOVIE_ID))
            )
        {
            return true;
        }
        return false;
    }
}
