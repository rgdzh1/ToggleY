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
        //开关初始状态
        if (mIsOpen) {
            canvas.drawRoundRect(mRectFOpen, mHeight / 2, mHeight / 2, mOpenBGPaint);//开
        } else {
            canvas.drawRoundRect(mRectFClose, mHeight / 2, mHeight / 2, mCloseBGPaint);//关
        }
        firstDraw();
        //绘制滑动按钮
        canvas.drawCircle(mSlidingDistance, mHeight / 2, mHeight / 2, mTouchPaint);
    }
    /**
     * 根据开关初始状态设置滑动按钮位置
     */
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //mLastX 是记录手指按下的点X坐标值
                //mStartX 表示的是当前滑动的起始点X坐标值
                mLastX = mStartX = event.getX();
                //2种情况
                //1, Down->Up,若是这个顺序,手指抬起时候,按照点击逻辑切换开关
                //2, Down->Move->Up,若是这个顺序, 手指抬起时候, 就按照滑动逻辑切换开关
                mIsEnableClick = true;
                break;
            case MotionEvent.ACTION_MOVE:
                float mEndX = event.getX();
                //滑动起始坐标与滑动后坐标值相减,得到手指移动距离
                float distanceX = mEndX - mStartX;
                //对手指移动距离进行累加,这个距离是圆心X轴坐标
                mSlidingDistance += distanceX;
                //判断左右两个临界值,不能超出左右侧边值
                if (mSlidingDistance < mHeight / 2) {
                    mSlidingDistance = mHeight / 2;
                }
                if (mSlidingDistance >= mWidth - mHeight / 2) {
                    mSlidingDistance = mWidth - mHeight / 2;
                }
                //重绘,到这一步,圆就随手指开始移动了
                invalidate();
                //手指按下坐标与滑动最后坐标差值
                float mMinDistanceX = Math.abs(mEndX - mLastX);
                //判断差值
                //1,如果差值大于8, 则认为是滑动, 如果用户松开按钮则按照滑动条件判断
                //1,如果差值小于8, 则认为是点击, 如果用户此时松开按钮则按照点击条件判断
                if (mMinDistanceX > 8) {
                    mIsEnableClick = false;
                } else {
                    mIsEnableClick = true;
                }
                //更新滑动X轴起始坐标,为下一次Move事件滑动做准备
                mStartX = event.getX();
                break;
            case MotionEvent.ACTION_UP:
                if (!mIsEnableClick) {
                    //当判定为滑动时, 首先判断这次滑动累加的距离, 如果大于一半则开关取反
                    if (mSlidingDistance >= mWidth / 2) {
                        mIsOpen = true;
                    } else {
                        mIsOpen = false;
                    }
                    //设置好开关Flag,执行替换背景
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
     * 替换开关逻辑
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

