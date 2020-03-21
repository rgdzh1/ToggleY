package com.yey.library_tby;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class ToggleImageY extends View {
    private static final String TAG = ToggleImageY.class.getName();
    private float mSlidingDistance;
    private float mStartX;//开始X值
    private float mLastX;//开始X值
    private boolean mIsEnableClick = true;//用户默认可以点击按钮进行切换
    private boolean mIsOpen;//最开始为关
    private boolean mIsFirst;//是否是第一次画

    private OnClick onClick;
    private int mOpenBGId;
    private int mCloseBGId;
    private int mTouchId;
    private Paint mPaint;
    private Bitmap mOpenBG;
    private Bitmap mCloseBG;
    private Bitmap mTouch;
    private int mWidth;
    private int mHeight;

    //屏幕旋转时候保存必要的数据
    @Override
    protected Parcelable onSaveInstanceState() {
        Log.e(TAG, "onSaveInstanceState");
        Bundle bundle = new Bundle();
        //保存系统其他原有的状态信息
        bundle.putParcelable("instance", super.onSaveInstanceState());
        //保存当前的一些状态
        bundle.putBoolean("mIsOpen", mIsOpen);
        bundle.putFloat("mSlidingDistance", mSlidingDistance);
        bundle.putBoolean("mIsFirst", mIsFirst);
        return bundle;
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        //判断state的类型是否为bundle,若是则从bundle中取数据
        if (state instanceof Bundle) {
            Log.e(TAG, "onRestoreInstanceState");
            Bundle bundle = (Bundle) state;
            mIsOpen = bundle.getBoolean("mIsOpen");
            mIsFirst = bundle.getBoolean("mIsFirst");
            mSlidingDistance = bundle.getFloat("mSlidingDistance");
            super.onRestoreInstanceState(bundle.getParcelable("instance"));
            return;
        }
        super.onRestoreInstanceState(state);
    }

    public ToggleImageY(Context context) {
        this(context, null);
    }

    public ToggleImageY(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleImageY(Context context,  AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParame(context, attrs, defStyleAttr);
        initPaint();
        initBitmap();
    }

    @SuppressLint("ResourceAsColor")
    private void initParame(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ToggleColorY, defStyleAttr, 0);
        mOpenBGId = typedArray.getResourceId(R.styleable.ToggleColorY_tby_open_bg, R.drawable.tiy_open);
        mCloseBGId = typedArray.getResourceId(R.styleable.ToggleColorY_tby_close_bg, R.drawable.tiy_close);
        mTouchId = typedArray.getResourceId(R.styleable.ToggleColorY_tby_touch, R.drawable.tiy_touch);
        mIsOpen = typedArray.getBoolean(R.styleable.ToggleColorY_tby_state, false);
        typedArray.recycle();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    private void initBitmap() {
        mTouch = BitmapUtils.resourceIDToBitmap(mTouchId, getContext());
        mCloseBG = BitmapUtils.resourceIDToBitmap(mCloseBGId, getContext());
        mOpenBG = BitmapUtils.resourceIDToBitmap(mOpenBGId, getContext());
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
        mTouch = BitmapUtils.scaleBitmap(mTouch, mHeight, mHeight);
        mCloseBG = BitmapUtils.scaleBitmap(mCloseBG, mWidth, mHeight);
        mOpenBG = BitmapUtils.scaleBitmap(mOpenBG, mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsOpen) {
            canvas.drawBitmap(mOpenBG, 0, 0, mPaint);//开
        } else {
            canvas.drawBitmap(mCloseBG, 0, 0, mPaint);//关
        }
        firstDraw();
        canvas.drawBitmap(mTouch, mSlidingDistance, 0, mPaint);
    }

    private void firstDraw() {
        if (!mIsFirst) {
            if (mIsOpen) {
                mSlidingDistance = mWidth - mHeight;
            } else {
                mSlidingDistance = 0;
            }
            mIsFirst = !mIsFirst;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = mStartX = event.getX();
                mIsEnableClick = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float mEndX = event.getX();
                float distanceX = mEndX - mStartX;
                mSlidingDistance += distanceX;
                if (mSlidingDistance < 0) {
                    mSlidingDistance = 0;
                }
                if (mSlidingDistance >= mWidth - mHeight) {
                    mSlidingDistance = mWidth - mHeight;
                }
                invalidate();
                float mMinDistanceX = Math.abs(mEndX - mLastX);
                if (mMinDistanceX > 8) {
                    mIsEnableClick = false;
                } else {
                    mIsEnableClick = true;
                }
                mStartX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                if (!mIsEnableClick) {
                    if (mSlidingDistance >= (mWidth-mHeight) / 2) {
                        mIsOpen = true;
                    } else {
                        mIsOpen = false;
                    }
                    drawToggle();
                } else {
                    mIsOpen = !mIsOpen;
                    drawToggle();
                }
                break;
        }
        return true;
    }

    /**
     * 绘制touche
     * 并回调click
     */
    public void drawToggle() {
        if (mIsOpen) {
            mSlidingDistance = mWidth - mHeight;
        } else {
            mSlidingDistance = 0;
        }
        if (onClick != null) {
            onClick.click(mIsOpen);
        }
        invalidate();
    }

    /**
     * @return 设置点击事件回调
     */
    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }
}

