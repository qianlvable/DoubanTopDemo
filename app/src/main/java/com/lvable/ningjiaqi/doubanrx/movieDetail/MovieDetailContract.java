package com.lvable.ningjiaqi.doubanrx.movieDetail;

import com.lvable.ningjiaqi.doubanrx.BasePresneter;
import com.lvable.ningjiaqi.doubanrx.BaseView;

/**
 * Created by ningjiaqi on 16/5/2.
 */
public class MovieDetailContract {
    interface View extends BaseView<Presenter>{
        void emptyError();
    }

    interface Presenter extends BasePresneter{

    }
}
