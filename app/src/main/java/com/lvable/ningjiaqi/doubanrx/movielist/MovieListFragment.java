package com.lvable.ningjiaqi.doubanrx.movielist;


import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.Debug;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;

import com.lvable.ningjiaqi.doubanrx.OnBackListener;
import com.lvable.ningjiaqi.doubanrx.R;
import com.lvable.ningjiaqi.doubanrx.UI.itemanimator.ItemAnimatorFactory;
import com.lvable.ningjiaqi.doubanrx.data.Movie;
import com.lvable.ningjiaqi.doubanrx.movieDetail.MovieDetailActivity;
import com.lvable.ningjiaqi.doubanrx.utils.AnimatorPath;
import com.lvable.ningjiaqi.doubanrx.utils.Constants;
import com.lvable.ningjiaqi.doubanrx.utils.PathEvaluator;
import com.lvable.ningjiaqi.doubanrx.utils.PathPoint;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.codetail.animation.SupportAnimator;
import io.codetail.animation.ViewAnimationUtils;
import io.codetail.widget.RevealFrameLayout;

/**
 * A simple {@link Fragment} subclass.
 */
public class MovieListFragment extends Fragment implements MovieListContract.View, OnBackListener {

    public static final int ANIM_DURATION = 300;
    @BindView(R.id.list_view)
    RecyclerView mRecyclerView;
    @BindView(R.id.fab_filter)
    FloatingActionButton mFab;
    @BindView(R.id.filter_panel)
    LinearLayout mFilterPanelRoot;
    @BindView(R.id.reveal_panel)
    RevealFrameLayout mRevealPanel;


    private MovieListAdapter mAdapter;
    private MovieListContract.Presenter mPresenter;

    private List<Movie> mData = new ArrayList<>();
    private int mLastPos;
    private boolean isLoadingMore;

    private long lastClickTime = 0;
    private int screenWidth;
    private int screenHeight;
    SupportAnimator animator;
    AnimatorPath openPath;
    AnimatorPath closePath;
    ObjectAnimator closeAnim;

    public MovieListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_movie_list, container, false);
        ButterKnife.bind(this, root);

        getActivity().getWindow().getDecorView().post(new Runnable() {
            @Override
            public void run() {
                mPresenter.start();
            }
        });

        mAdapter = new MovieListAdapter(getContext(), mData);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(ItemAnimatorFactory.slidein());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(new MovieListAdapter.OnItemClickListener() {
            @Override
            public void onClick(View view, Movie data) {
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime > ANIM_DURATION) {
                    int[] screenLocation = new int[2];
                    view.getLocationOnScreen(screenLocation);
                    Intent intent = new Intent(getActivity(), MovieDetailActivity.class);
                    intent.putExtra(Constants.KEY_POS_LEFT, screenLocation[0])
                            .putExtra(Constants.KEY_POS_TOP, screenLocation[1])
                            .putExtra(Constants.KEY_HEIGHT, view.getHeight())
                            .putExtra(Constants.KEY_WIDTH, view.getWidth())
                            .putExtra(Constants.KEY_MOVIE, data);

                    startActivity(intent);
                    getActivity().overridePendingTransition(0, 0);
                }
                lastClickTime = clickTime;

            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
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

                    if (mPresenter.getCurrentRequestPos() == 250) {
                        mAdapter.setFooterStatus(MovieListAdapter.STATUS_NO_MORE);
                    } else {
                        isLoadingMore = true;
                        mAdapter.setFooterStatus(MovieListAdapter.STATUS_LOADING);
                        loadMore();
                    }
                }
            }
        });

        DisplayMetrics metrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        screenHeight = metrics.heightPixels;
        screenWidth = metrics.widthPixels;

        openPath = new AnimatorPath();
        openPath.moveTo(screenWidth * 0.95f, screenHeight * 0.97f);
        int c1X = (int) (screenWidth * 0.65);
        int c1Y = (int) (screenHeight);
        int c2X = c1X;
        int c2Y = c1Y;
        int endX = (int) (screenWidth / 1.6);
        int endY = (int) (screenHeight * .85f);
        openPath.curveTo(c1X, c1Y, c2X, c2Y, endX, endY);

        ObjectAnimator revealAnim = ObjectAnimator.ofObject(this, "fabLoc",
                new PathEvaluator(), openPath.getPoints().toArray());
        revealAnim.setDuration(ANIM_DURATION);
        revealAnim.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // get the center for the clipping circle
                mFab.setVisibility(View.GONE);
                int cx = (mFilterPanelRoot.getLeft() + mFilterPanelRoot.getRight()) / 2;
                int cy = (int) ((mFilterPanelRoot.getTop() + mFilterPanelRoot.getBottom()) / 2.2);

                float finalRadius = screenWidth / 2;

                animator = ViewAnimationUtils.createCircularReveal(mFilterPanelRoot, cx, cy, 0, finalRadius);
                animator.setInterpolator(new AccelerateInterpolator());
                animator.setDuration(ANIM_DURATION);
                mFilterPanelRoot.setVisibility(View.VISIBLE);
                animator.start();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        closePath = new AnimatorPath();
        closePath.moveTo(endX, endY);

        closePath.curveTo(c2X, c2Y, c1X, c1Y, screenWidth, screenHeight);
        closeAnim = ObjectAnimator.ofObject(this, "fabLoc",
                new PathEvaluator(), closePath.getPoints().toArray());
        closeAnim.setDuration(ANIM_DURATION);


        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                revealAnim.start();
            }
        });
        Debug.stopMethodTracing();
        return root;
    }

    public void setFabLoc(PathPoint newLoc) {
        mFab.setTranslationX(newLoc.mX - screenWidth);
        mFab.setTranslationY(newLoc.mY - screenHeight);
    }

    @Override
    public void showEmptyError() {

    }

    @Override
    public void showMovieList(List<Movie> movies) {
        boolean isFirstLoading = mData.size() == 0;
        mData.addAll(movies);
        isLoadingMore = false;
        if (isFirstLoading)
            mRecyclerView.getAdapter()
                    .notifyItemRangeInserted(mRecyclerView.getAdapter().getItemCount(), movies.size() + 1);
        else
            mRecyclerView.getAdapter().notifyDataSetChanged();
        //       Debug.stopMethodTracing();
    }

    @Override
    public void loadCompleted() {

    }

    @Override
    public void loadMoreError() {
        isLoadingMore = false;
        mAdapter.setFooterStatus(MovieListAdapter.STATUS_ERROR);
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
        mAdapter.setFooterStatus(MovieListAdapter.STATUS_NO_MORE);
    }


    @Override
    public void setPresenter(MovieListContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onBackPress() {
        if (mFilterPanelRoot.getVisibility() == View.VISIBLE) {
            animator = animator.reverse();
            if (animator == null)
                return false;
            animator.addListener(new SupportAnimator.AnimatorListener() {
                @Override
                public void onAnimationStart() {
                    mFab.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd() {
                    mFilterPanelRoot.setVisibility(View.INVISIBLE);
                    closeAnim.start();
                }

                @Override
                public void onAnimationCancel() {

                }

                @Override
                public void onAnimationRepeat() {

                }
            });
            animator.start();
            return true;
        } else {
            return false;
        }
    }
}
