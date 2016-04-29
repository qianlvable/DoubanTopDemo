package com.lvable.ningjiaqi.doubanrx.movielist;

import android.support.annotation.NonNull;

import com.lvable.ningjiaqi.doubanrx.data.Movie;
import com.lvable.ningjiaqi.doubanrx.data.MovieDataSource;
import com.lvable.ningjiaqi.doubanrx.data.MovieRepository;

import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by ningjiaqi on 16/4/26.
 */
public class MovieListPresenter implements MovieListContract.Presenter,MovieDataSource.LoadMoviesCallback{
    @NonNull
    private final MovieRepository mRepository;

    @NonNull
    private final MovieListContract.View mMainView;

    public MovieListPresenter(@NonNull MovieRepository mRepository
            , @NonNull MovieListContract.View mMainView) {
        this.mRepository = mRepository;
        this.mMainView = mMainView;

        mMainView.setPresenter(this);
    }


    @Override
    public void start() {
        getNextPage();
    }

    @Override
    public void getNextPage() {
        mRepository.getMovies(this);
    }



    @Override
    public void onDataLoaded(List<Movie> movies) {
        mMainView.showMovieList(movies);
    }

    @Override
    public void onLoadError() {
        mMainView.showEmptyError();
    }
}
