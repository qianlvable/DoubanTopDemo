package com.lvable.ningjiaqi.doubanrx.movielist;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.lvable.ningjiaqi.doubanrx.R;
import com.lvable.ningjiaqi.doubanrx.data.MovieRepository;
import com.lvable.ningjiaqi.doubanrx.data.local.LocalDataSource;
import com.lvable.ningjiaqi.doubanrx.data.remote.RemoteDataSource;

public class MovieListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitle("DoubanRx top 250");
        MovieListFragment listFragment = (MovieListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content_frame);
        if (listFragment == null) {
            listFragment = new MovieListFragment();

            listFragment.setPresenter(new MovieListPresenter(MovieRepository
                    .getInstance(LocalDataSource.getInstance(this),RemoteDataSource.getInstance())
                    ,listFragment));
            getSupportFragmentManager().beginTransaction().add(R.id.content_frame
                    ,listFragment).commit();
            return;
        }

        listFragment.setPresenter(new MovieListPresenter(MovieRepository
                .getInstance(LocalDataSource.getInstance(this),RemoteDataSource.getInstance()),listFragment));


    }
}
