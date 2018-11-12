package app.com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import app.com.example.android.popularmovies.Database.MovieInfo;

public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
    public final ImageView mPosterImageView;
    public final TextView mErrorTitleDisplay;

    private final List<MovieInfo> mMovieList;
    private final MovieAdapterOnClickHandler mClickHandler;

    public final Context context;

    public MovieAdapterViewHolder(
            View view,
            Context param_context,
            List<MovieInfo> movieList,
            MovieAdapterOnClickHandler clickHandler
    ){
        super(view);
        mPosterImageView = view.findViewById(R.id.iv_poster_main);
        mErrorTitleDisplay = view.findViewById(R.id.tv_error_title_display);
        context = param_context;
        mMovieList = movieList;
        mClickHandler = clickHandler;
        view.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int position = getAdapterPosition();
        MovieInfo movie = mMovieList.get(position);
        mClickHandler.onClick(movie);
    }
}
