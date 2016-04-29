package com.lvable.ningjiaqi.doubanrx.data;

import java.util.List;

import rx.Observable;

/**
 * Created by ningjiaqi on 16/4/22.
 */
public interface MovieDataSource {
    Observable<List<Movie>> getMoviesObservable(int start);

    interface LoadMoviesCallback {
        void onDataLoaded(List<Movie> movies);
        void onLoadError();
    }
}
