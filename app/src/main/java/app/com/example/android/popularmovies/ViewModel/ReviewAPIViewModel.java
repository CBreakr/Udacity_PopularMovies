package app.com.example.android.popularmovies.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import app.com.example.android.popularmovies.Database.MovieReview;
import app.com.example.android.popularmovies.MovieDetailActivity;
import app.com.example.android.popularmovies.Utils.MovieRequestUtils;

public class ReviewAPIViewModel extends AndroidViewModel {
    private final ReviewAPILiveData data;

    public ReviewAPIViewModel(Application application, String ID) {
        super(application);
        data = new ReviewAPILiveData(application, ID);
    }

    public LiveData<List<MovieReview>> getData() {
        return data;
    }
}

class ReviewAPILiveData extends LiveData<List<MovieReview>> {
    private final Context context;
    private final String movieID;

    public ReviewAPILiveData(Context context, String ID) {
        this.context = context;
        movieID = ID;
        loadData();
    }

    private void loadData() {
        new AsyncTask<Void,Void,List<MovieReview>>() {
            @Override
            protected List<MovieReview> doInBackground(Void...voids) {
                // do the async task
                return MovieRequestUtils.getListOfReviewsForMovie(context, movieID);
            }

            @Override
            protected void onPostExecute(List<MovieReview> data) {
                setValue(data);
            }
        }.execute();
    }
}
