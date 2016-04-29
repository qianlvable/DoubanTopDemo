package com.lvable.ningjiaqi.doubanrx.movieDetail;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.lvable.ningjiaqi.doubanrx.R;
import com.lvable.ningjiaqi.doubanrx.data.Movie;
import com.lvable.ningjiaqi.doubanrx.utils.Constants;

import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MovieDetailActivity extends AppCompatActivity {
    @Bind(R.id.iv_main_poster)
    ImageView mIvPoster;
    @Bind(R.id.info_panel)
    View mInfoPanel;

    private int mLeftDelta;
    private int mTopDelta;
    private float mWidthScale;
    private float mHeightScale;

    private int thumbnailTop;
    private int thumbnailLeft;
    private int thumbnailWidth;
    private int thumbnailHeight;
    private Movie movie;


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

        Bitmap b = BitmapFactory.decodeByteArray(
                getIntent().getByteArrayExtra("test"),0,getIntent().getByteArrayExtra("test").length);
        mIvPoster.setImageBitmap(b);

        ViewTreeObserver obs = mIvPoster.getViewTreeObserver();
        obs.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                mIvPoster.getViewTreeObserver().removeOnPreDrawListener(this);

                int[] screenLocation = new int[2];
                mIvPoster.getLocationOnScreen(screenLocation);
                mLeftDelta = thumbnailLeft - screenLocation[0];
                mTopDelta = thumbnailTop - screenLocation[1];
                Log.d("wtf","thumbanailLeft: " + thumbnailLeft + " thumbnailTop : "
                        + thumbnailTop + "x: "+screenLocation[0] + "y :"+screenLocation[1]);
                mWidthScale = (float) thumbnailWidth / mIvPoster.getWidth();
                mHeightScale = (float) thumbnailHeight / mIvPoster.getHeight();

                runEnterAnimation();
                return true;
            }
        });

    }

    private void runEnterAnimation() {

        mIvPoster.setPivotX(0);
        mIvPoster.setPivotY(0);
        mIvPoster.setScaleX(mWidthScale);
        mIvPoster.setScaleY(mHeightScale);
        mIvPoster.setTranslationX(mLeftDelta);
        mIvPoster.setTranslationY(mTopDelta);

        mInfoPanel.setTranslationY(mInfoPanel.getHeight());

        mIvPoster.animate().setDuration(250).
                scaleX(1).scaleY(1).
                translationX(0).translationY(0).
                setInterpolator(new AccelerateDecelerateInterpolator()).start();

        mInfoPanel.animate().setDuration(220)
                .translationY(0).setInterpolator(new AccelerateDecelerateInterpolator()).start();
    }

    @Override
    public void onBackPressed() {
        runExitAnimation(new Runnable() {
            public void run() {
                finish();
                mIvPoster.setAlpha(0f);
                mInfoPanel.setAlpha(0f);
            }
        });
    }
    private void runExitAnimation(Runnable endAction) {
        mIvPoster.animate().setDuration(250).
                scaleX(mWidthScale).scaleY(mHeightScale).
                translationX(mLeftDelta).translationY(mTopDelta).
                withEndAction(endAction).start();

        mInfoPanel.animate().setDuration(250)
                .translationY(mInfoPanel.getHeight()).start();



    }
    @Override
    public void finish() {
        super.finish();

        // override transitions to skip the standard window animations
        overridePendingTransition(0, 0);
    }
}
