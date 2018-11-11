package app.com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import app.com.example.android.popularmovies.Database.MovieInfo;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieAdapterViewHolder> {

    private List<MovieInfo> mMovieList;

    private final MovieAdapterOnClickHandler mClickHandler;

    public interface MovieAdapterOnClickHandler{
        void onClick(MovieInfo movie);
    }

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

    public class MovieAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final ImageView mPosterImageView;
        public final TextView mErrorTitleDisplay;

        public final Context context;

        public MovieAdapterViewHolder(View view, Context param_context){
            super(view);
            mPosterImageView = view.findViewById(R.id.iv_poster_main);
            mErrorTitleDisplay = view.findViewById(R.id.tv_error_title_display);
            context = param_context;
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            MovieInfo movie = mMovieList.get(position);
            mClickHandler.onClick(movie);
        }
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
        return new MovieAdapterViewHolder(view, context);
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
