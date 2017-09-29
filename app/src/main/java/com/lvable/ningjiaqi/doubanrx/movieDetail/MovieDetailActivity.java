package com.lvable.ningjiaqi.doubanrx.movieDetail;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.common.references.CloseableReference;
import com.facebook.datasource.DataSource;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.image.CloseableBitmap;
import com.facebook.imagepipeline.image.CloseableImage;
import com.facebook.imagepipeline.request.ImageRequest;
import com.lvable.ningjiaqi.doubanrx.R;
import com.lvable.ningjiaqi.doubanrx.UI.ElasticDragDismissFrameLayout;
import com.lvable.ningjiaqi.doubanrx.UI.ElasticDragDismissFrameLayout.ElasticDragDismissCallback;
import com.lvable.ningjiaqi.doubanrx.UI.NotifyingScrollView;
import com.lvable.ningjiaqi.doubanrx.UI.PolyLoadingView;
import com.lvable.ningjiaqi.doubanrx.data.Actor;
import com.lvable.ningjiaqi.doubanrx.data.Movie;
import com.lvable.ningjiaqi.doubanrx.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MovieDetailActivity extends AppCompatActivity {
    public static final int ANIM_DURATION = 300;
    @BindView(R.id.iv_main_poster)
    SimpleDraweeView mIvPoster;
    @BindView(R.id.tv_movie_title)
    TextView mTvTitle;
    @BindView(R.id.tv_origin_title)
    TextView mOrignTtitle;
    @BindView(R.id.title_area)
    LinearLayout mTitleArea;
    @BindView(R.id.tv_summary)
    TextView mSummaryArea;
    @BindView(R.id.info_panel)
    LinearLayout mInfoPanel;
    @BindView(R.id.fab_collect)
    FloatingActionButton mFabCollect;
    @BindView(R.id.poly_loading)
    PolyLoadingView mPolyLoadingView;
    @BindView(R.id.loading_area)
    LinearLayout mLoadingArea;
    @BindView(R.id.iv_actor1)
    SimpleDraweeView ivActor1;
    @BindView(R.id.iv_actor2)
    SimpleDraweeView ivActor2;
    @BindView(R.id.iv_actor3)
    SimpleDraweeView ivActor3;
    @BindView(R.id.iv_actor4)
    SimpleDraweeView ivActor4;
    @BindView(R.id.actor_area)
    LinearLayout actorArea;
    @BindView(R.id.scollview)
    NotifyingScrollView mScrollView;
    @BindView(R.id.dismiss_root)
    ElasticDragDismissFrameLayout dismissRoot;


    private int mLeftDelta;
    private int mTopDelta;
    private float mWidthScale;
    private float mHeightScale;

    private int thumbnailTop;
    private int thumbnailLeft;
    private int thumbnailWidth;
    private int thumbnailHeight;
    private Movie movie;

    private Bitmap mPosterBitmap;
    private int mScrollOffset;
    private Animation fabOpenAnim;
    private Animation fabCloseAnim;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);
        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        if (bundle == null) {
            return;
        }
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);

        thumbnailHeight = bundle.getInt(Constants.KEY_HEIGHT);
        thumbnailWidth = bundle.getInt(Constants.KEY_WIDTH);
        thumbnailLeft = bundle.getInt(Constants.KEY_POS_LEFT);
        thumbnailTop = bundle.getInt(Constants.KEY_POS_TOP);
        movie = bundle.getParcelable(Constants.KEY_MOVIE);
        mTvTitle.setText(movie.title + " (" + movie.year + ")");
        mOrignTtitle.setText(movie.orignTitle);

        fabOpenAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabCloseAnim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        ImageRequest imageRequest = ImageRequest.fromUri(movie.imgUrl);
        DataSource<CloseableReference<CloseableImage>> dataSource =
                Fresco.getImagePipeline().fetchImageFromBitmapCache(imageRequest, this);
        CloseableReference<CloseableImage> imageReference;
        try {
            imageReference = dataSource.getResult();
            if (imageReference != null) {
                try {
                    CloseableBitmap image = (CloseableBitmap) imageReference.get();
                    mPosterBitmap = image.getUnderlyingBitmap();//.copy(Bitmap.Config.ARGB_8888,true);
                    mIvPoster.setImageBitmap(mPosterBitmap);
                } finally {
                    CloseableReference.closeSafely(imageReference);
                }
            } else {
                // cache miss
                // TODO: 16/4/29 request this image
            }
        } finally {
            dataSource.close();
        }

        ViewTreeObserver obs = mIvPoster.getViewTreeObserver();
        obs.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mIvPoster.getViewTreeObserver().removeOnPreDrawListener(this);

                int[] screenLocation = new int[2];
                mIvPoster.getLocationOnScreen(screenLocation);
                mLeftDelta = thumbnailLeft - screenLocation[0];
                mTopDelta = thumbnailTop - screenLocation[1];
                mWidthScale = (float) thumbnailWidth / mIvPoster.getWidth();
                mHeightScale = (float) thumbnailHeight / mIvPoster.getHeight();

                runEnterAnimation();
                return true;
            }
        });

        if (mPosterBitmap != null) {
            Palette.from(mPosterBitmap).generate(palette -> {
                mTitleArea.setBackgroundColor(palette.getDarkVibrantColor(0xff5f767d));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    Window window = getWindow();
                    window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                    window.setStatusBarColor(palette.getDarkMutedColor(0xff5f767d));
                }
            });
        }

        mScrollView.setOnScrollChangedListener(new NotifyingScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(NestedScrollView who, int l, int t, int oldl, int oldt) {
                int coverHeight = mIvPoster.getHeight();
                if (coverHeight != 0 && l < coverHeight) {
                    final float ratio = (float) Math.min(Math.max(t, 0), coverHeight) / coverHeight;
                    mIvPoster.setTranslationY(ratio * coverHeight * 0.3f);
                    mScrollOffset = t;
                }
            }
        });

        dismissRoot.addListener(new ElasticDragDismissCallback() {

            @Override
            public void onDrag(float elasticOffset, float elasticOffsetPixels, float rawOffset, float rawOffsetPixels) {

            }

            @Override
            public void onDragDismissed(int oritention) {
                if (oritention == ElasticDragDismissCallback.DISMISS_DOWN) {
                    swipeDownExit();
                } else {
                    swipeUpExit();
                }

            }
        });
    }

    private void swipeDownExit() {
        ObjectAnimator animY = ObjectAnimator
                .ofFloat(dismissRoot, "translationY", dismissRoot.getHeight())
                .setDuration(500);
        animY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animY.start();
    }

    private void swipeUpExit() {
        ObjectAnimator animY = ObjectAnimator
                .ofFloat(dismissRoot, "translationY", -dismissRoot.getHeight())
                .setDuration(500);
        animY.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animY.start();
    }

    private void getDetailInfo() {
        Observable.create(new Observable.OnSubscribe<Response>() {
            @Override
            public void call(Subscriber<? super Response> subscriber) {
                OkHttpClient client = new OkHttpClient();

                HttpUrl url = HttpUrl.parse(Constants.MOIVE_DETAIL_URL + movie.id);
                Request request = new Request.Builder().url(url).build();
                try {
                    Response response = client.newCall(request).execute();
                    subscriber.onNext(response);
                } catch (IOException e) {
                    e.printStackTrace();
                    subscriber.onError(e);
                }
            }
        }).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Response>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Response response) {
                try {
                    String jsonStr = response.body().string();
                    JSONObject json = new JSONObject(jsonStr);
                    String summary = "     " + json.getString("summary");
                    ArrayList<Actor> actors = new ArrayList<Actor>();
                    JSONArray actorsJson = json.getJSONArray("casts");
                    for (int i = 0; i < actorsJson.length(); i++) {
                        JSONObject item = actorsJson.getJSONObject(i);
                        JSONObject avatarsUrl = item.optJSONObject("avatars");
                        if (avatarsUrl == null)
                            continue;
                        actors.add(new Actor(item.getString("name"), avatarsUrl.getString("small")));
                    }
                    movie.actors = actors;

                    mPolyLoadingView.stopLoading();
                    mLoadingArea.setVisibility(View.GONE);
                    mSummaryArea.setText(summary);
                    for (int i = 0; i < Math.min(4, actors.size()); i++) {
                        Actor ac = actors.get(i);
                        if (i == 0) {
                            ivActor1.setImageURI(Uri.parse(ac.avatarUrl));
                        } else if (i == 1) {
                            ivActor2.setImageURI(Uri.parse(ac.avatarUrl));
                        } else if (i == 2) {
                            ivActor3.setImageURI(Uri.parse(ac.avatarUrl));
                        } else {
                            ivActor4.setImageURI(Uri.parse(ac.avatarUrl));
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void runEnterAnimation() {
        mIvPoster.setPivotX(0);
        mIvPoster.setPivotY(0);
        mIvPoster.setScaleX(mWidthScale);
        mIvPoster.setScaleY(mHeightScale * 1.2f);
        mIvPoster.setTranslationX(mLeftDelta);
        mIvPoster.setTranslationY(mTopDelta * .9f);

        mInfoPanel.setTranslationY(mInfoPanel.getHeight());

        mIvPoster.animate().setDuration(ANIM_DURATION).
                scaleX(1).scaleY(1).
                translationX(0).translationY(0).
                setInterpolator(new AccelerateDecelerateInterpolator()).start();

        ObjectAnimator animInfo = ObjectAnimator.ofFloat(mInfoPanel, "translationY", 0)
                .setDuration(ANIM_DURATION);
        animInfo.setInterpolator(new AccelerateDecelerateInterpolator());
        animInfo.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mFabCollect.setVisibility(View.VISIBLE);
                mFabCollect.startAnimation(fabOpenAnim);
                mLoadingArea.setVisibility(View.VISIBLE);
                mPolyLoadingView.startLoading();
                getDetailInfo();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animInfo.start();

    }

    @Override
    public void onBackPressed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(0xffFFD600);
        }
        mFabCollect.startAnimation(fabCloseAnim);
        mPolyLoadingView.stopLoading();
        mLoadingArea.setVisibility(View.INVISIBLE);
        runExitAnimation();
    }

    private void runExitAnimation() {
        mTopDelta += (mScrollOffset);
        int time = (int) (mIvPoster.getScaleX() * ANIM_DURATION);
        actorArea.setVisibility(View.INVISIBLE);
        int[] screenLocation = new int[2];
        mIvPoster.getLocationOnScreen(screenLocation);
        mLeftDelta = thumbnailLeft - screenLocation[0];
        mTopDelta = thumbnailTop - screenLocation[1];

        ObjectAnimator animScaleX = ObjectAnimator
                .ofFloat(mIvPoster, "scaleX", mIvPoster.getScaleX(), mWidthScale).setDuration(time);
        ObjectAnimator animScaleY = ObjectAnimator
                .ofFloat(mIvPoster, "scaleY", mIvPoster.getScaleY(), mHeightScale).setDuration(time);
        ObjectAnimator animX = ObjectAnimator
                .ofFloat(mIvPoster, "translationX", mLeftDelta).setDuration(time);
        ObjectAnimator animY = ObjectAnimator
                .ofFloat(mIvPoster, "translationY", mTopDelta).setDuration(time);
        ObjectAnimator alpha = ObjectAnimator
                .ofFloat(mIvPoster, "alpha", mIvPoster.getAlpha(), 0).setDuration((long) (time * 0.35));
        alpha.setStartDelay((long) (time * 0.65));
        AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(animScaleX, animScaleY, animX, animY, alpha);
        animSet.start();
        animSet.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                finish();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mInfoPanel.animate().setDuration(ANIM_DURATION)
                .translationY(mInfoPanel.getHeight()).start();


    }

    @Override
    public void finish() {
        super.finish();
        // override transitions to skip the standard window animations
        overridePendingTransition(0, 0);
    }

}
