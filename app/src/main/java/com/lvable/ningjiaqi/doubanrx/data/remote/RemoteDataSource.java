package com.lvable.ningjiaqi.doubanrx.data.remote;

import com.lvable.ningjiaqi.doubanrx.utils.Constants;
import com.lvable.ningjiaqi.doubanrx.utils.JsonParser;
import com.lvable.ningjiaqi.doubanrx.data.Movie;
import com.lvable.ningjiaqi.doubanrx.data.MovieDataSource;

import java.io.IOException;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by ningjiaqi on 16/4/22.
 */
public class RemoteDataSource implements MovieDataSource {

    private static RemoteDataSource INSTANCE;

    private RemoteDataSource(){}

    public static RemoteDataSource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new RemoteDataSource();
        }
        return INSTANCE;
    }

    @Override
    public Observable<List<Movie>> getMoviesObservable(int start) {
        Observable<List<Movie>> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                OkHttpClient client = new OkHttpClient();

                HttpUrl url = HttpUrl.parse(Constants.TOP_250_URL)
                        .newBuilder()
                        .addQueryParameter(Constants.QUERY_PAGE,Integer.toString(start)).build();
                Request request = new Request.Builder().url(url).build();
                try {
                    Response response = client.newCall(request).execute();
                    String jsonStr = response.body().string();
                    subscriber.onNext(jsonStr);
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
        .flatMap(new Func1<String, Observable<List<Movie>>>() {
            @Override
            public Observable<List<Movie>> call(String jsonStr) {
                return Observable.create(new Observable.OnSubscribe<List<Movie>>() {
                    @Override
                    public void call(Subscriber<? super List<Movie>> subscriber) {
                            subscriber.onNext(JsonParser.parseMovieList(jsonStr));
                    }
                });
            }
        });

        return observable;
    }
}
