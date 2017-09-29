package com.lvable.ningjiaqi.doubanrx.movielist;

import android.os.Debug;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.lvable.ningjiaqi.doubanrx.OnBackListener;
import com.lvable.ningjiaqi.doubanrx.R;
import com.lvable.ningjiaqi.doubanrx.data.MovieRepository;
import com.lvable.ningjiaqi.doubanrx.data.local.LocalDataSource;
import com.lvable.ningjiaqi.doubanrx.data.remote.RemoteDataSource;

public class MovieListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        setTitle("DoubanRx top 250");

//        Debug.startMethodTracing("Rxdouban");
        MovieListFragment listFragment = (MovieListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.content_frame);
        if (listFragment == null) {
            listFragment = new MovieListFragment();

            listFragment.setPresenter(new MovieListPresenter(MovieRepository
                    .getInstance(LocalDataSource.getInstance(getApplicationContext())
                            ,RemoteDataSource.getInstance())
                    ,listFragment));
            getSupportFragmentManager().beginTransaction().add(R.id.content_frame
                    ,listFragment).commit();
            return;
        }

        listFragment.setPresenter(new MovieListPresenter(MovieRepository
                .getInstance(LocalDataSource.getInstance(getApplicationContext())
                        ,RemoteDataSource.getInstance()),listFragment));


    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager()
                .findFragmentById(R.id.content_frame);
        if (fragment instanceof OnBackListener){
            if(((OnBackListener) fragment).onBackPress()){
                return;
            }
        }
        super.onBackPressed();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MovieRepository.getInstance(LocalDataSource.getInstance(getApplicationContext())
                ,RemoteDataSource.getInstance()).resetReqStart();
    }
}
