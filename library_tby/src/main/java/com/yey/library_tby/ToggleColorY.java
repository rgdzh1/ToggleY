package com.yey.library_tby;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

public class ToggleColorY extends View {
    private static final String TAG = ToggleColorY.class.getName();

    private float mSlidingDistance;
    private float mStartX;//开始X值
    private float mLastX;//开始X值
    private boolean mIsEnableClick = true;//用户默认可以点击按钮进行切换
    private boolean mIsOpen;//最开始为关
    private boolean mIsFirst;//是否是第一次画
    private int mOpenBGColor;
    private int mCloseBGColor;
    private int mTouchColor;
    private int mWidth;
    private int mHeight;
    private Paint mTouchPaint;
    private Paint mCloseBGPaint;
    private Paint mOpenBGPaint;
    private RectF mRectFOpen;
    private RectF mRectFClose;
    private OnClick onClick;

    public ToggleColorY(Context context) {
        this(context, null);
    }

    public ToggleColorY(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleColorY(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParame(context, attrs, defStyleAttr);
        initPaint();
    }

    @SuppressLint("ResourceAsColor")
    private void initParame(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ToggleColorY, defStyleAttr, 0);
        mOpenBGColor = typedArray.getColor(R.styleable.ToggleColorY_tby_open_bg, getResources().getColor(R.color.tby_orange));
        mCloseBGColor = typedArray.getColor(R.styleable.ToggleColorY_tby_close_bg, getResources().getColor(R.color.tby_gray));
        mTouchColor = typedArray.getColor(R.styleable.ToggleColorY_tby_touch, getResources().getColor(R.color.tby_read));
        mIsOpen = typedArray.getBoolean(R.styleable.ToggleColorY_tby_state, false);
        typedArray.recycle();
    }

    //    Paint.Style.FILL设置只绘制图形内容
    //    Paint.Style.STROKE设置只绘制图形的边
    //    Paint.Style.FILL_AND_STROKE设置都绘制
    private void initPaint() {
        //开关 开背景
        mOpenBGPaint = new Paint();
        mOpenBGPaint.setAntiAlias(true);
        mOpenBGPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mOpenBGPaint.setColor(mOpenBGColor);
        //开关 关背景
        mCloseBGPaint = new Paint();
        mCloseBGPaint.setAntiAlias(true);
        mCloseBGPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCloseBGPaint.setColor(mCloseBGColor);
        //Touch 圆形
        mTouchPaint = new Paint();
        mTouchPaint.setAntiAlias(true);
        mTouchPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mTouchPaint.setColor(mTouchColor);

        //开 RectF
        mRectFOpen = new RectF();
        //关 RectF
        mRectFClose = new RectF();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
        initView();
    }

    private void initView() {
        mRectFClose.set(0, 0, mWidth, mHeight);
        mRectFOpen.set(0, 0, mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsOpen) {
            canvas.drawRoundRect(mRectFOpen, mHeight / 2, mHeight / 2, mOpenBGPaint);//开
        } else {
            canvas.drawRoundRect(mRectFClose, mHeight / 2, mHeight / 2, mCloseBGPaint);//关
        }
        firstDraw();
        canvas.drawCircle(mSlidingDistance, mHeight / 2, mHeight / 2, mTouchPaint);
    }

    private void firstDraw() {
        if (!mIsFirst) {
            if (mIsOpen) {
                mSlidingDistance = mWidth - mHeight / 2;
            } else {
                mSlidingDistance = mHeight / 2;
            }
            mIsFirst = !mIsFirst;
        }
    }


    public void clickToggle() {
        if (mIsEnableClick) {
            mIsOpen = !mIsOpen;
            drawToggle();
        }
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = mStartX = event.getX();
                //手指刚按下,按钮是可以点击
                mIsEnableClick = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float mEndX = event.getX();
                float distanceX = mEndX - mStartX;
                mSlidingDistance += distanceX;
                if (mSlidingDistance < mHeight / 2) {
                    mSlidingDistance = mHeight / 2;
                }
                if (mSlidingDistance >= mWidth - mHeight / 2) {
                    mSlidingDistance = mWidth - mHeight / 2;
                }
                invalidate();
                mStartX = event.getX();
                float mMinDistanceX = Math.abs(mStartX - mLastX);
                //当手指滑动距离大于5的时候, 表示用户现在处于滑动按钮的状态
                if (mMinDistanceX > 8) {
                    mIsEnableClick = false;
                }
                break;
            case MotionEvent.ACTION_UP:
                if (!mIsEnableClick) {
                    if (mSlidingDistance >= mWidth / 2) {
                        mIsOpen = true;
                    } else {
                        mIsOpen = false;
                    }
                    drawToggle();
                } else {
                    clickToggle();
                }
                break;
        }
        return true;
    }

    /**
     * 绘制touche
     * 为开还是关
     */
    public void drawToggle() {
        if (mIsOpen) {
            //开
            mSlidingDistance = mWidth - mHeight / 2;
        } else {
            //关
            mSlidingDistance = mHeight / 2;
        }
        if (onClick != null) {
            onClick.click(mIsOpen);
        }
        invalidate();
    }

    /**
     * 设置点击事件回调
     *
     * @return
     */
    public void setOnClick(OnClick onClick) {
        this.onClick = onClick;
    }


    //屏幕旋转时候保存必要的数据
    @Nullable
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
}

