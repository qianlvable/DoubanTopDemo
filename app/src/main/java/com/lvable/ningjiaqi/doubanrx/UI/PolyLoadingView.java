package com.lvable.ningjiaqi.doubanrx.UI;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Interpolator;

import com.lvable.ningjiaqi.doubanrx.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ningjiaqi on 16/4/21.
 */
public class PolyLoadingView extends View {
    private Paint mPaint;
    private int slide = 6;
    private int depth = 3;
    private int cx;
    private int cy;
    private List<PointF> mPoints;
    private float mProgress;
    private Path mPath;
    private List<List<PointF>> mChildren;

    private ValueAnimator mProgressAnimator;
    private int mShapeColor = 0xff554433;
    private int mDuration;
    private boolean mEnableAlpha;
    private int mRoundCorner = 3;
    private int strokeWidth;
    private ValueAnimator.AnimatorUpdateListener mUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            mProgress = (float) animation.getAnimatedValue();
            invalidate();
        }
    };

    public PolyLoadingView(Context context) {
        super(context);
        init();
    }

    public PolyLoadingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray a = context.getTheme().obtainStyledAttributes(
                attrs,
                R.styleable.PolyLoadingView,
                0, 0);

        try {
            slide = a.getInt(R.styleable.PolyLoadingView_slide,6);
            depth = a.getInt(R.styleable.PolyLoadingView_depth,3);
            mEnableAlpha = a.getBoolean(R.styleable.PolyLoadingView_enableAlpha,false);
            mShapeColor = a.getColor(R.styleable.PolyLoadingView_shapeColor,0xff554433);
        } finally {
            a.recycle();
        }
        init();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);

        strokeWidth = 3;
        mPaint.setStrokeWidth(strokeWidth);
        mPaint.setColor(mShapeColor);
        mPaint.setPathEffect(new CornerPathEffect(mRoundCorner));

        // 什么时候需要调用这个api
        //  setLayerType(LAYER_TYPE_SOFTWARE, mPaint);
        mPath = new Path();

        mPoints = new ArrayList<>();
        mChildren = new ArrayList<>();

        mDuration = 1000;
        mProgressAnimator = new ValueAnimator().ofFloat(1,0f).setDuration(mDuration);
        mProgressAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mProgressAnimator.addUpdateListener(mUpdateListener);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (cx == 0) {
            cx = getWidth() / 2;
            cy = getHeight() / 2;
            canvas.rotate(180, cx, cy);
            float radius = getWidth() / 2.8f;
            mPoints = getRegularPoints(cx, cy, slide, radius);
        }

        List<List<PointF>> children = getCurrentShape(mProgress);
        for (int i =0;i < children.size();i++) {
            List<PointF> child = children.get(i);
            getPath(child);
            canvas.drawPath(mPath,mPaint);
        }
    }

    private void getPath(List<PointF> child) {
        mPath.reset();
        for (int j =0;j < child.size();j++){
            PointF point = child.get(j);

            if (j == 0){
                mPath.moveTo(point.x,point.y);
            } else {
                mPath.lineTo(point.x,point.y);
            }
        }
        mPath.close();
    }

    public List<List<PointF>> getCurrentShape(float progress){
        mChildren.clear();
        for (int i =0;i < depth;i++) {
            List<PointF> pre;
            if (i == 0)
                pre = mPoints;
            else
                pre = mChildren.get(i-1);

            List<PointF> child = getInscribedPoints(pre,progress);
            mChildren.add(child);
        }
        return mChildren;
    }

    public void startLoading(){

        mProgressAnimator.start();

    }

    public void stopLoading(){
        mProgressAnimator.end();
    }

    public void enableAlphaEffect(boolean enable){
        mProgressAnimator.end();
        mProgressAnimator.start();
    }

    public void setReverse(boolean backward) {
        if (backward) {
            mProgressAnimator.setRepeatMode(ValueAnimator.REVERSE);
        }else {
            mProgressAnimator.setRepeatMode(ValueAnimator.RESTART);
        }
    }

    public void setInterpolator(Interpolator intepetor) {
        mProgressAnimator.setInterpolator(intepetor);
    }

    public void setDuration(int millisecond) {
        mDuration = millisecond;
        mProgressAnimator.setDuration(mDuration);

    }

    public void setShapeColor(int shapeColor) {
        this.mShapeColor = shapeColor;
        mPaint.setColor(shapeColor);
    }

    public void setSlide(int slide) {
        if (slide > 2) {
            this.slide = slide;
            invalidate();
        }
    }

    public static List<PointF> getRegularPoints(int cx,int cy,int slide , float radius) {
        List<PointF> pts = new ArrayList<>();
        for (int i = 0;i < slide;i++) {
            float x = (float) (radius * Math.sin(i * 2 * Math.PI / slide));
            float y = (float) (radius * Math.cos(i * 2 * Math.PI / slide));
            x += cx;
            y += cy;
            pts.add(new PointF(x,y));
        }
        return pts;
    }

    public static List<PointF> getInscribedPoints(List<PointF> pts, float progress) {
        List<PointF> inscribedPoints = new ArrayList<>();

        for (int i=0;i<pts.size();i++){
            PointF start = pts.get(i);
            PointF end;
            if (i < pts.size()-1) end = pts.get(i+1);
            else end = pts.get(0);

            inscribedPoints.add(getInterpolatedPoint(start,end,progress));
        }
        return inscribedPoints;
    }



    private static PointF getInterpolatedPoint(PointF start, PointF end, float progress) {
        float dx = end.x - start.x;
        float dy = end.y - start.y;

        float newX = start.x + dx * progress;
        float newY = start.y + dy * progress;

        return new PointF(newX,newY);
    }


    public void setFill(boolean filled) {
        if (filled){
            mPaint.setStyle(Paint.Style.FILL);
        }else {
            mPaint.setStyle(Paint.Style.STROKE);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        mProgressAnimator.addUpdateListener(mUpdateListener);
    }

    @Override
    protected void onDetachedFromWindow() {
        mProgressAnimator.removeAllListeners();
        mProgressAnimator.end();
        super.onDetachedFromWindow();
    }
}
