package app.com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import app.com.example.android.popularmovies.Database.MovieInfo;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapterViewHolder> {

    private List<MovieInfo> mMovieList;

    private final MovieAdapterOnClickHandler mClickHandler;

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler){
        mClickHandler = clickHandler;
    }

    public void setMovieList(List<MovieInfo> movies){
        mMovieList = movies;
        notifyDataSetChanged();
    }

    public List<MovieInfo> getMovieList(){
        return mMovieList;
    }

    //
    // RECYCLERVIEW ADAPTER
    //

    @Override
    public MovieAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        int layoutIdToInflate = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachToParentImmediately = false;

        View view = inflater.inflate(layoutIdToInflate, viewGroup, attachToParentImmediately);
        return new MovieAdapterViewHolder(view, context, mMovieList, mClickHandler);
    }

    @Override
    public void onBindViewHolder(final MovieAdapterViewHolder movieAdapterViewHolder, int position) {
        final MovieInfo movie = mMovieList.get(position);
        Picasso.with(movieAdapterViewHolder.context)
                .load(movie.getPosterPath())
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_sandwich)
                .into(movieAdapterViewHolder.mPosterImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        movieAdapterViewHolder.mPosterImageView.setVisibility(View.VISIBLE);
                        movieAdapterViewHolder.mErrorTitleDisplay.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        movieAdapterViewHolder.mPosterImageView.setVisibility(View.GONE);
                        movieAdapterViewHolder.mErrorTitleDisplay.setVisibility(View.VISIBLE);
                        movieAdapterViewHolder.mErrorTitleDisplay.setText(movie.getTitle());
                    }
                });
    }

    @Override
    public int getItemCount() {
        if(mMovieList != null) {
            return mMovieList.size();
        }
        return 0;
    }
}
