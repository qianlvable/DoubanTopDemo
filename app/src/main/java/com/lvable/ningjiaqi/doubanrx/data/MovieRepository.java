package com.lvable.ningjiaqi.doubanrx.data;

import com.lvable.ningjiaqi.doubanrx.data.local.LocalDataSource;

import java.util.List;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by ningjiaqi on 16/4/22.
 */
public class MovieRepository {
    private static MovieRepository INSTANCE = null;

    private final LocalDataSource mLocalDatasource;
    private final MovieDataSource mRemoteDatasource;
    private List<Movie> mCacheData;
    private int mStart = 1;

    private MovieRepository(LocalDataSource mLocalDatasource, MovieDataSource mRemoteDatasource) {
        this.mLocalDatasource = mLocalDatasource;
        this.mRemoteDatasource = mRemoteDatasource;
    }

    public static MovieRepository getInstance(LocalDataSource localDatasource ,
                                              MovieDataSource remoteDatasource){
        if (INSTANCE == null){
            INSTANCE = new MovieRepository(localDatasource,remoteDatasource);
        }
        return INSTANCE;
    }



    public void getMovies(MovieDataSource.LoadMoviesCallback callback) {
        if (callback != null){
            if (false) {
                callback.onDataLoaded(mCacheData);
            }else {
                mLocalDatasource.getMoviesObservable(mStart)
                        .subscribe(new Subscriber<List<Movie>>() {
                            @Override
                            public void onCompleted() {

                            }

                            @Override
                            public void onError(Throwable e) {
                                mRemoteDatasource.getMoviesObservable(mStart)
                                        .subscribe(new Subscriber<List<Movie>>() {
                                            @Override
                                            public void onCompleted() {
                                            }

                                            @Override
                                            public void onError(Throwable e) {
                                                callback.onLoadError();
                                            }

                                            @Override
                                            public void onNext(List<Movie> movies) {
                                                mStart += movies.size();
                                                mLocalDatasource.saveData(movies);
                                                callback.onDataLoaded(movies);
                                            }
                                        });
                            }

                            @Override
                            public void onNext(List<Movie> movies) {
                                mStart += movies.size();
                                callback.onDataLoaded(movies);
                            }
                        });
            }
        }
    }
}
