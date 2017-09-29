package com.lvable.ningjiaqi.doubanrx.movielist;

import com.lvable.ningjiaqi.doubanrx.BasePresneter;
import com.lvable.ningjiaqi.doubanrx.BaseView;
import com.lvable.ningjiaqi.doubanrx.data.Movie;

import java.util.List;

/**
 * Created by ningjiaqi on 16/4/22.
 */
public interface MovieListContract {
    interface View extends BaseView<Presenter>{

        void showEmptyError();

        void showMovieList(List<Movie> movies);

        void loadCompleted();

        void loadMoreError();

        void loadMoreSuccess();

        void loadMore();

        void noMoreContent();
    }

    interface Presenter extends BasePresneter {

        void getNextPage();

        int getCurrentRequestPos();
    }
}
