package app.com.example.android.popularmovies.Utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import app.com.example.android.popularmovies.Database.MovieInfo;
import app.com.example.android.popularmovies.Database.MovieReview;
import app.com.example.android.popularmovies.Database.MovieTrailer;

public final class MovieJSONUtils {

    /*

   EXAMPLES OF NEW TYPES

    VIDEO:
https://api.themoviedb.org/3/movie/<<ID>>/videos?api_key=<<API_KEY>>&language=en-US
 URL: https://www.youtube.com/watch?v=<<key>>  (as returned below)

    {
"id": 335983,
"results": [
{
"id": "5a7c6a35c3a3680f7f01053a",
"iso_639_1": "en",
"iso_3166_1": "US",
"key": "dzxFdtWmjto",
"name": "VENOM - Official Teaser Trailer (HD)",
"site": "YouTube",
"size": 1080,
"type": "Teaser"
},
{
"id": "5b60970d0e0a267ef400031c",
"iso_639_1": "en",
"iso_3166_1": "US",
"key": "xLCn88bfW1o",
"name": "VENOM - Official Trailer 2 (HD)",
"site": "YouTube",
"size": 1080,
"type": "Trailer"
}
]
}

    REVIEW:
 https://api.themoviedb.org/3/movie/<<ID>>/reviews?api_key=<<API_KEY>>&language=en-US

    {
"id": 335983,
"page": 1,
"results": [
{
"author": "Gimly",
"content": "I honestly don't know what everyone's talking about, _Venom_ is **fine**! I mean... It's not great. There were a few moments I found myself thinking \"This bit's pretty bad, why did they do this?\" and a lot of moments I found myself thinking \"This bit's pretty good, but they still probably should have just made a **real** _Venom_ movie with Marvel.\"\r\n\r\nBut it's fine. The absolute best parts are the interactions taking place within Venom himself (between Brock and the Symbiote) and the movie would have been a lot worse if Tom Hardy wasn't capable of pulling that off. Look, a _Venom_ movie probably should have been rated R, and it probably should have taken more notes from current superhero movies than superhero movies from fifteen years ago, but I still had a good time with _Venom_, maybe it's not the best possible version of the story, but I'm still glad we have this one.\r\n\r\n_Final rating:★★★ - I liked it. Would personally recommend you give it a go._",
"id": "5bd28c050e0a2616cf00459a",
"url": "https://www.themoviedb.org/review/5bd28c050e0a2616cf00459a"
},
{
"author": "javajohnny",
"content": "I very much like it if you ask me (;",
"id": "5bd8df3dc3a3683cef000ea5",
"url": "https://www.themoviedb.org/review/5bd8df3dc3a3683cef000ea5"
}
],
"total_pages": 1,
"total_results": 3
}

     */

    private static final String TAG = MovieJSONUtils.class.getSimpleName();

    private static final String JSON_ERROR_STATUS_CODE = "status_code";
    private static final String JSON_MAIN_LIST = "results";
    private static final String JSON_MOVIE_ID = "id";
    private static final String JSON_MOVIE_TITLE = "title";
    private static final String JSON_MOVIE_POSTER = "poster_path";
    private static final String JSON_MOVIE_OVERVIEW = "overview";
    private static final String JSON_MOVIE_RATING = "vote_average";
    private static final String JSON_MOVIE_RELEASE = "release_date";

    private static final String JSON_TRAILER_TITLE = "name";
    private static final String JSON_TRAILER_YOUTUBEID = "key";

    private static final String JSON_REVIEW_AUTHOR = "author";
    private static final String JSON_REVIEW_TEXT = "content";
    private static final String JSON_REVIEW_LINK = "url";

    //
    // MOVIEINFO
    //

    public static MovieInfo parseMovieFromJSON(String json){
        try {
            JSONObject jo = new JSONObject(json);
            if(!checkForJSONError(jo)){
                return createMovieFromJSONObject(jo);
            }
        }
        catch (JSONException jx){
            Log.e(TAG, "Error in parcing individual movie");
            jx.printStackTrace();
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
        catch(JSONException jx){
            Log.e(TAG, "");
            jx.printStackTrace();
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
        movie.setReleaseDate(jo.optString(JSON_MOVIE_RELEASE, "Release Date Missing"));

        return movie;
    }

    //
    // TRAILER
    //

    public static MovieTrailer parseMovieTrailerFromJSON(String json){
        try {
            JSONObject jo = new JSONObject(json);
            if(!checkForJSONError(jo)){
                return createMovieTrailerFromJSONObject(jo);
            }
        }
        catch (JSONException jx){
            Log.e(TAG, "Error in parsing movie review");
            jx.printStackTrace();
        }

        return null;
    }

    public static List<MovieTrailer> parseMovieTrailerListFromJSON(String json){
        try{
            JSONObject jo = new JSONObject(json);
            if(!checkForJSONError(jo)){
                List<MovieTrailer> trailers = new ArrayList<MovieTrailer>();

                JSONArray jsonMovieTrailers = jo.optJSONArray(JSON_MAIN_LIST);

                if(jsonMovieTrailers != null) {
                    for (int index = 0; index < jsonMovieTrailers.length(); index++) {
                        MovieTrailer trailer = createMovieTrailerFromJSONObject(jsonMovieTrailers.optJSONObject(index));
                        if(trailer != null) {
                            trailers.add(trailer);
                        }
                    }

                    return trailers;
                }
            }
        }
        catch(JSONException jx){
            Log.e(TAG, "");
            jx.printStackTrace();
        }

        return null;
    }

    private static MovieTrailer createMovieTrailerFromJSONObject(JSONObject jo){
        // if the initial JSON object is null, we obviously can't proceed
        if(jo == null){
            return null;
        }

        String title = jo.optString(JSON_TRAILER_TITLE, "Title Missing");
        String youtubeId = jo.optString(JSON_TRAILER_YOUTUBEID, "Link Missing");

        MovieTrailer trailer = new MovieTrailer(title, youtubeId);
        return trailer;
    }

    //
    // REVIEW
    //

    public static MovieReview parseMovieReviewFromJSON(String json){
        try {
            JSONObject jo = new JSONObject(json);
            if(!checkForJSONError(jo)){
                return createMovieReviewFromJSONObject(jo);
            }
        }
        catch (JSONException jx){
            Log.e(TAG, "Error in parsing movie review");
            jx.printStackTrace();
        }

        return null;
    }

    public static List<MovieReview> parseMovieReviewListFromJSON(String json){
        try{
            JSONObject jo = new JSONObject(json);
            if(!checkForJSONError(jo)){
                List<MovieReview> reviews = new ArrayList<MovieReview>();

                JSONArray jsonMovieReviews = jo.optJSONArray(JSON_MAIN_LIST);

                if(jsonMovieReviews != null) {
                    for (int index = 0; index < jsonMovieReviews.length(); index++) {
                        MovieReview review = createMovieReviewFromJSONObject(jsonMovieReviews.optJSONObject(index));
                        if(review != null) {
                            reviews.add(review);
                        }
                    }

                    return reviews;
                }
            }
        }
        catch(JSONException jx){
            Log.e(TAG, "");
            jx.printStackTrace();
        }

        return null;
    }

    private static MovieReview createMovieReviewFromJSONObject(JSONObject jo){
        // if the initial JSON object is null, we obviously can't proceed
        if(jo == null){
            return null;
        }

        String author = jo.optString(JSON_REVIEW_AUTHOR, "Author Missing");
        String text = jo.optString(JSON_REVIEW_TEXT, "Review Text Missing");
        String link = jo.optString(JSON_REVIEW_LINK, "Link Missing");

        MovieReview review = new MovieReview(text, author, link);
        return review;
    }

    //
    // ERROR CHECK
    //

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
