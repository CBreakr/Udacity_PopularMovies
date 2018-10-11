package app.com.example.android.popularmovies.Utils;

import android.net.Uri;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String API_BASEURL_INDIVIDUAL = "https://api.themoviedb.org/3/movie/";
    private static final String API_BASEURL_LIST = "https://api.themoviedb.org/3/discover/movie";

    private static final String API_KEY = "api_key";
    private static final String API_LANGUAGE = "language";
    private static final String API_SORT_BY = "sort_by";
    private static final String API_INCLUDE_ADULT = "include_adult";
    private static final String API_INCLUDE_VIDEO = "include_video";
    private static final String API_PAGE = "page";

    private static final String LANGUAGE_QUERY_VALUE_ENGLISH = "en-US";
    private static final String SORT_BY_QUERY_VALUE_POPULARITY = "popularity.desc";
    private static final String SORT_BY_QUERY_VALUE_TOP_RATED = "vote_average.desc";
    private static final String PAGE_QUERY_VALUE_DEFAULT = "1";

    /*
    INDIVIDUAL:
    https://api.themoviedb.org/3/movie/
    {movie_id}
    ?api_key=<<api_key>>
    &language=en-US
    */
    public static URL buildIndividualMovieRequestURL(String movieId, String key){
        Uri builtUri = Uri.parse(API_BASEURL_INDIVIDUAL + movieId).buildUpon()
                .appendQueryParameter(API_KEY, key)
                .appendQueryParameter(API_LANGUAGE, LANGUAGE_QUERY_VALUE_ENGLISH)
                .build();

        return createURLFromUri(builtUri);
    }

    /*
    LIST:
    https://api.themoviedb.org/3/discover/movie
    ?api_key=25c4095aa59cd1e69e38e9933640ce6b
    &language=en-US
    &sort_by=popularity.desc
    &include_adult=false
    &include_video=false
    &page=1
    */
    public static URL buildPopularMoviesRequestURL(String key){
        Uri builtUri = Uri.parse(API_BASEURL_LIST).buildUpon()
                .appendQueryParameter(API_KEY, key)
                .appendQueryParameter(API_LANGUAGE, LANGUAGE_QUERY_VALUE_ENGLISH)
                .appendQueryParameter(API_SORT_BY, SORT_BY_QUERY_VALUE_POPULARITY)
                .appendQueryParameter(API_INCLUDE_ADULT, "false")
                .appendQueryParameter(API_INCLUDE_VIDEO, "false")
                .appendQueryParameter(API_PAGE, PAGE_QUERY_VALUE_DEFAULT)
                .build();

        return createURLFromUri(builtUri);
    }

    public static URL buildTopRatedMoviesRequestURL(String key){
        Uri builtUri = Uri.parse(API_BASEURL_LIST).buildUpon()
                .appendQueryParameter(API_KEY, key)
                .appendQueryParameter(API_LANGUAGE, LANGUAGE_QUERY_VALUE_ENGLISH)
                .appendQueryParameter(API_SORT_BY, SORT_BY_QUERY_VALUE_TOP_RATED)
                .appendQueryParameter(API_INCLUDE_ADULT, "false")
                .appendQueryParameter(API_INCLUDE_VIDEO, "false")
                .appendQueryParameter(API_PAGE, PAGE_QUERY_VALUE_DEFAULT)
                .build();

        return createURLFromUri(builtUri);
    }

    public static URL createURLFromUri(Uri uri){
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();

            urlConnection.setConnectTimeout(15000);
            urlConnection.setReadTimeout(15000);
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);

            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        }
        catch(Exception ex){
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            ex.printStackTrace(pw);

            Log.d("NetworkUtils", "error in HTTP request: " + sw.toString());
            return null;
        }
        finally {
            if(urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
