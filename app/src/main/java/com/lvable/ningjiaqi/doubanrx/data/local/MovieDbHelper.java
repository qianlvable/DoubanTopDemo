package com.lvable.ningjiaqi.doubanrx.data.local;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by ningjiaqi on 16/4/26.
 */
public class MovieDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 3;

    public static final String DATABASE_NAME = "Tasks.db";

    private static final String TEXT_TYPE = " TEXT";

    private static final String INTEGER_TYPE = " INTEGER";

    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + MoviePersistenceContract.MovieEntry.TABLE_NAME + " (" +
                    MoviePersistenceContract.MovieEntry._ID + INTEGER_TYPE + " PRIMARY KEY AUTOINCREMENT," +
                    MoviePersistenceContract.MovieEntry.COL_MOVIE_ID + TEXT_TYPE + COMMA_SEP +
                    MoviePersistenceContract.MovieEntry.COL_MOVIE_DIRECTOR + TEXT_TYPE + COMMA_SEP +
                    MoviePersistenceContract.MovieEntry.COL_MOVIE_RATING + " FLOAT" + COMMA_SEP +
                    MoviePersistenceContract.MovieEntry.COL_MOVIE_TITLE + TEXT_TYPE + COMMA_SEP +
                    MoviePersistenceContract.MovieEntry.COL_POSTER_URL + TEXT_TYPE
                    + " );";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+DATABASE_NAME + ";");
        onCreate(db);
    }
}
