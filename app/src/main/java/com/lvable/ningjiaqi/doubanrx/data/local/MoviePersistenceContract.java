package com.lvable.ningjiaqi.doubanrx.data.local;

import android.provider.BaseColumns;

/**
 * Created by ningjiaqi on 16/4/26.
 */
public class MoviePersistenceContract {
    // TODO: 16/4/26 To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public MoviePersistenceContract() {
    }

    public static class MovieEntry implements BaseColumns {
        public static final String TABLE_NAME = "douban_top250";
        public static final String COL_MOVIE_ID = "movie_id";
        public static final String COL_MOVIE_TITLE = "title";
        public static final String COL_MOVIE_DIRECTOR = "director";
        public static final String COL_MOVIE_RATING = "rating";
        public static final String COL_POSTER_URL = "image_url";
    }
}
