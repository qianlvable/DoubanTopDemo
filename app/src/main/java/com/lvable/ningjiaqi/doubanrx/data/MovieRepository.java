package com.lvable.ningjiaqi.doubanrx.data;

import android.util.Log;

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
    private int mStart = 0;

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
                                            if (movies.size() == 0) {
                                                if (mStart < 250) {
                                                    callback.onLoadError();
                                                    Log.d("wtf","json parse error");
                                                }
                                            } else {
                                                mStart += movies.size();
                                                mLocalDatasource.saveData(movies);
                                                callback.onDataLoaded(movies);
                                            }
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

    public void resetReqStart() {
        this.mStart = 0;
    }

    public int getRequestStartPos() {
        return mStart;
    }
}
