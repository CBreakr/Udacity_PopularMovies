package app.com.example.android.popularmovies.ViewModel;

/*
APIViewModels:
https://medium.com/androiddevelopers/lifecycle-aware-data-loading-with-android-architecture-components-f95484159de4
*/

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

import app.com.example.android.popularmovies.Database.MovieInfo;
import app.com.example.android.popularmovies.Utils.FilterUtils;
import app.com.example.android.popularmovies.Utils.MovieRequestUtils;

public class MovieAPIViewModel extends AndroidViewModel {
    private final MovieAPILiveData data;

    public MovieAPIViewModel(Application application) {
        super(application);
        data = new MovieAPILiveData(application, FilterUtils.getFilterTypeFromSharedPreferences(application));
    }

    public LiveData<List<MovieInfo>> getData() {
        return data;
    }
}

class MovieAPILiveData extends LiveData<List<MovieInfo>> {
    private final Context fContext;
    private final FilterUtils.FilterType fFilterType;

    public MovieAPILiveData(Context context, FilterUtils.FilterType filterType) {
        fContext = context;
        fFilterType = filterType;
        loadData();
    }

    private void loadData() {
        new AsyncTask<Void,Void,List<MovieInfo>>() {
            @Override
            protected List<MovieInfo> doInBackground(Void...voids) {
                // do the async task
                switch(fFilterType){
                    case Popular:
                        return MovieRequestUtils.requestPopularMovies(fContext);
                    case TopRated:
                        return MovieRequestUtils.requestTopRatedMovies(fContext);
                }

                return null;
            }

            @Override
            protected void onPostExecute(List<MovieInfo> data) {
                setValue(data);
            }
        }.execute();
    }
}
