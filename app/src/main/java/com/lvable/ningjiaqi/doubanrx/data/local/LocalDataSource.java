package com.lvable.ningjiaqi.doubanrx.data.local;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lvable.ningjiaqi.doubanrx.data.Movie;
import com.lvable.ningjiaqi.doubanrx.data.MovieDataSource;
import com.lvable.ningjiaqi.doubanrx.utils.Constants;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by ningjiaqi on 16/4/22.
 */
public class LocalDataSource implements MovieDataSource {
    private static LocalDataSource INSTANCE;
    private MovieDbHelper mDbHelper;
    private Context mContext;
    private SharedPreferences mSp;
    private LocalDataSource(Context context){
        mContext = context;
        mDbHelper = new MovieDbHelper(context);
        mSp = mContext.getSharedPreferences(Constants.SP_NAME,Context.MODE_APPEND);
        mStart = mSp.getInt(Constants.SP_SAVED_COUNT,0);
    }
    private int mStart = -1;
    public static LocalDataSource getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new LocalDataSource(context);
        }
        return INSTANCE;
    }

    public void saveData(List<Movie> data) {
        // TODO: 16/4/26 能不能变出主动任务不是注册了才发生
        Observable.create(new Observable.OnSubscribe<Object>() {
            @Override
            public void call(Subscriber<? super Object> subscriber) {
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                for (Movie movie : data) {
                    ContentValues values = new ContentValues();
                    values.put(MoviePersistenceContract.MovieEntry.COL_MOVIE_ID, movie.id);
                    values.put(MoviePersistenceContract.MovieEntry.COL_MOVIE_DIRECTOR,movie.directors);
                    values.put(MoviePersistenceContract.MovieEntry.COL_MOVIE_RATING,movie.rating);
                    values.put(MoviePersistenceContract.MovieEntry.COL_MOVIE_TITLE,movie.title);
                    values.put(MoviePersistenceContract.MovieEntry.COL_POSTER_URL,movie.imgUrl);
                    values.put(MoviePersistenceContract.MovieEntry.COL_MOVIE_YEAR,movie.year);
                    values.put(MoviePersistenceContract.MovieEntry.COL_MOVIE_ORIGN_TITLE,movie.orignTitle);
                    db.insert(MoviePersistenceContract.MovieEntry.TABLE_NAME, null, values);
                }
                mStart += data.size();
                db.close();
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Object>() {

            @Override
            public void onCompleted() {
                mSp.edit().putInt(Constants.SP_SAVED_COUNT,mStart).apply();
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Object o) {

            }
        });

    }
    @Override
    public Observable<List<Movie>> getMoviesObservable(int reqStart) {
        return Observable.create(new Observable.OnSubscribe<List<Movie>>() {

            @Override
            public void call(Subscriber<? super List<Movie>> subscriber) {

                List<Movie> movies = new ArrayList<>();
                if (reqStart <= mStart) {
                    SQLiteDatabase db = mDbHelper.getReadableDatabase();

                    String[] projection = {
                            MoviePersistenceContract.MovieEntry._ID,
                            MoviePersistenceContract.MovieEntry.COL_MOVIE_ID,
                            MoviePersistenceContract.MovieEntry.COL_MOVIE_TITLE,
                            MoviePersistenceContract.MovieEntry.COL_POSTER_URL,
                            MoviePersistenceContract.MovieEntry.COL_MOVIE_RATING,
                            MoviePersistenceContract.MovieEntry.COL_MOVIE_DIRECTOR,
                            MoviePersistenceContract.MovieEntry.COL_MOVIE_YEAR,
                            MoviePersistenceContract.MovieEntry.COL_MOVIE_ORIGN_TITLE

                    };

                    String selection = MoviePersistenceContract.MovieEntry._ID + " > ? AND "+
                            MoviePersistenceContract.MovieEntry._ID + " < ?";
                    int end = reqStart + Constants.COUNT_PER_PAGE;
                    String[] selectionArg = {Integer.toString(reqStart),Integer.toString(end)};

                    Cursor c = db.query(
                            MoviePersistenceContract.MovieEntry.TABLE_NAME
                            , projection, selection, selectionArg, null, null, null);
                    if (c != null && c.getCount() > 0) {
                        c.moveToFirst();
                        for (int i = 0; i < c.getCount(); i++) {
                            Movie movie = new Movie();
                            movie.id = c.getString(c.getColumnIndex(MoviePersistenceContract
                                    .MovieEntry.COL_MOVIE_ID));
                            movie.title = c.getString(c.getColumnIndex(MoviePersistenceContract
                                    .MovieEntry.COL_MOVIE_TITLE));
                            movie.imgUrl = c.getString(c.getColumnIndex(MoviePersistenceContract.MovieEntry
                                    .COL_POSTER_URL));
                            movie.rating = c.getFloat(c.getColumnIndex(MoviePersistenceContract.MovieEntry
                                    .COL_MOVIE_RATING));
                            movie.directors = c.getString(c.getColumnIndex(MoviePersistenceContract.MovieEntry
                                    .COL_MOVIE_DIRECTOR));
                            movie.year = c.getInt(c.getColumnIndex(MoviePersistenceContract.MovieEntry.COL_MOVIE_YEAR));
                            movie.orignTitle = c.getString(c.getColumnIndex(MoviePersistenceContract.MovieEntry.COL_MOVIE_ORIGN_TITLE));
                            movies.add(movie);
                            c.moveToNext();
                        }
                    }
                    if (c != null) {
                        c.close();
                    }
                    db.close();
                }
                if (movies.size() == 0) {
                    subscriber.onError(new SQLException("not item"));
                }else {
                    subscriber.onNext(movies);
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());


    }
}
