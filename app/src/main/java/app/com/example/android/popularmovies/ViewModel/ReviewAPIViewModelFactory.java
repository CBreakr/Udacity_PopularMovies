package app.com.example.android.popularmovies.ViewModel;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

public class ReviewAPIViewModelFactory implements ViewModelProvider.Factory {
    private Application mApplication;
    private String movieID;

    public ReviewAPIViewModelFactory(Application application, String ID) {
        mApplication = application;
        movieID = ID;
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        return (T) new ReviewAPIViewModel(mApplication, movieID);
    }
}
