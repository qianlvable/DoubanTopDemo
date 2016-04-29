package com.lvable.ningjiaqi.doubanrx.movielist;


import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.lvable.ningjiaqi.doubanrx.R;
import com.lvable.ningjiaqi.doubanrx.data.Movie;
import com.lvable.ningjiaqi.doubanrx.movieDetail.MovieDetailActivity;
import com.lvable.ningjiaqi.doubanrx.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends Fragment implements MovieListContract.View {

    @Bind(R.id.list_view)
    RecyclerView recyclerView;
    private MovieListAdapter mAdapter;
    private MovieListContract.Presenter mPresenter;

    private List<Movie> mData = new ArrayList<>();
    private int mLastPos;
    private boolean isLoadingMore;

    public MovieListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_movie_list, container, false);
        ButterKnife.bind(this, root);

        mAdapter = new MovieListAdapter(getContext(),mData);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new MovieListAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, Movie data) {
                ImageView imageView = (ImageView) view;

                int[] screenLocation = new int[2];
                view.getLocationOnScreen(screenLocation);
                Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                intent.putExtra(Constants.KEY_POS_LEFT,screenLocation[0])
                        .putExtra(Constants.KEY_POS_TOP,screenLocation[1])
                        .putExtra(Constants.KEY_HEIGHT,view.getHeight())
                        .putExtra(Constants.KEY_WIDTH,view.getWidth())
                        .putExtra(Constants.KEY_MOVIE,data);

                startActivity(intent);
                getActivity().overridePendingTransition(0,0);
            }
        });

        mPresenter.start();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager lg = (LinearLayoutManager) recyclerView.getLayoutManager();
                mLastPos = lg.findLastVisibleItemPosition();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (!isLoadingMore
                        && mLastPos + 1 >= (recyclerView.getAdapter().getItemCount() - 1)
                        && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    isLoadingMore = true;
                    loadMore();
                }
            }
        });

        return root;
    }

    @Override
    public void showEmptyError() {

    }

    @Override
    public void showMovieList(List<Movie> movies) {
        mData.addAll(movies);
        isLoadingMore = false;
        recyclerView.getAdapter().notifyDataSetChanged();

    }

    @Override
    public void loadCompleted() {

    }

    @Override
    public void loadMoreError() {

    }

    @Override
    public void loadMoreSuccess() {

    }

    @Override
    public void loadMore() {
        mPresenter.getNextPage();
    }

    @Override
    public void noMoreContent() {

    }


    @Override
    public void setPresenter(MovieListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
