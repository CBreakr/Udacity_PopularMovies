package app.com.example.android.popularmovies.ViewModel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import app.com.example.android.popularmovies.Database.MovieReview;
import app.com.example.android.popularmovies.Database.MovieTrailer;
import app.com.example.android.popularmovies.Utils.MovieRequestUtils;

public class TrailerAPIViewModel extends AndroidViewModel {
    private final TrailerAPILiveData data;

    public TrailerAPIViewModel(Application application, String ID) {
        super(application);
        data = new TrailerAPILiveData(application, ID);
    }

    public LiveData<List<MovieTrailer>> getData() {
        return data;
    }
}

class TrailerAPILiveData extends LiveData<List<MovieTrailer>> {
    private final Context context;
    private final String movieID;

    public TrailerAPILiveData(Context context, String ID) {
        this.context = context;
        movieID = ID;
        loadData();
    }

    private void loadData() {
        new AsyncTask<Void,Void,List<MovieTrailer>>() {
            @Override
            protected List<MovieTrailer> doInBackground(Void...voids) {
                // do the async task
                return MovieRequestUtils.getListOfTrailersForMovie(context, movieID);
            }

            @Override
            protected void onPostExecute(List<MovieTrailer> data) {
                setValue(data);
            }
        }.execute();
    }
}
