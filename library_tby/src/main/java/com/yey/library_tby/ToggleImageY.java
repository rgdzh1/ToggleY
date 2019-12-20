package com.yey.library_tby;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
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

    public ToggleImageY(Context context) {
        this(context, null);
    }

    public ToggleImageY(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToggleImageY(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParame(context, attrs, defStyleAttr);
        initPaint();
        initBitmap();
    }

    @SuppressLint("ResourceAsColor")
    private void initParame(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ToggleColorY, defStyleAttr, 0);
        mOpenBGId = typedArray.getInteger(R.styleable.ToggleColorY_tby_open_bg, R.drawable.tiy_open);
        mCloseBGId = typedArray.getInteger(R.styleable.ToggleColorY_tby_close_bg, R.drawable.tiy_close);
        mTouchId = typedArray.getInteger(R.styleable.ToggleColorY_tby_touch, R.drawable.tiy_touch);
        mIsOpen = typedArray.getBoolean(R.styleable.ToggleColorY_tby_state, false);
        typedArray.recycle();
    }

    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    private void initBitmap() {
        mTouch = drawableToBitmap(mTouchId, getContext());
        mCloseBG = drawableToBitmap(mCloseBGId, getContext());
        mOpenBG = drawableToBitmap(mOpenBGId, getContext());
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
                mSlidingDistance = mOpenBG.getWidth() - mOpenBG.getHeight();
            } else {
                mSlidingDistance = 0;
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
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mCloseBG.getWidth(), mCloseBG.getHeight());
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
                if (mSlidingDistance < 0) {
                    mSlidingDistance = 0;
                }
                if (mSlidingDistance >= mOpenBG.getWidth() - mOpenBG.getHeight()) {
                    mSlidingDistance = mOpenBG.getWidth() - mOpenBG.getHeight();
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
                    if (mSlidingDistance >= mOpenBG.getWidth() / 2) {
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
            mSlidingDistance = mOpenBG.getWidth() - mOpenBG.getHeight();
        } else {
            //关
            mSlidingDistance = 0;
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


    /**
     * drawable转bitmap
     *
     * @param resourceID
     * @param context
     * @return
     */
    public static Bitmap drawableToBitmap(int resourceID, Context context) {
        Drawable drawable = context.getResources().getDrawable(resourceID);
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return bitmap;
    }


}

