package com.yey.library_tby;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
@Deprecated
public class TBY1 extends View {
    private static final String TAG = TBY1.class.getName();
    private Paint mPaintBgLeft;
    private Paint mPaintTextLeft;
    private RectF mRectFLeft;
    private Paint mPaintBgRight;
    private Paint mPaintTextRight;
    private RectF mRectFRight;
    private int mWidth;
    private int mHeight;
    private Paint mPaintCircle;
    private boolean mIsFirstDraw = true;
    private float mSlidingDistance;
    private float mStartX;//开始X值
    private float mLastX;//开始X值
    private boolean mIsEnableClick = true;//用户默认可以点击按钮进行切换
    private boolean mIsOpen;

    public TBY1(Context context) {
        this(context, null);
    }

    public TBY1(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TBY1(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    //    Paint.Style.FILL设置只绘制图形内容
    //    Paint.Style.STROKE设置只绘制图形的边
    //    Paint.Style.FILL_AND_STROKE设置都绘制
    private void initPaint() {
        //左侧背景画笔
        mPaintBgLeft = new Paint();
        mPaintBgLeft.setAntiAlias(true);
        mPaintBgLeft.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintBgLeft.setColor(Color.RED);
        //右侧背景画笔
        mPaintBgRight = new Paint();
        mPaintBgRight.setAntiAlias(true);
        mPaintBgRight.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintBgRight.setColor(Color.YELLOW);
        //左侧文字画笔
        mPaintTextLeft = new Paint();
        mPaintTextLeft.setAntiAlias(true);
        mPaintTextLeft.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintTextLeft.setColor(Color.BLACK);
        //字体大小
//        mPaintTextLeft.setTextSize(120);
        //画笔粗细
        mPaintTextLeft.setStrokeWidth(10.0f);

        //右侧文字画笔
        mPaintTextRight = new Paint();
        mPaintTextRight.setAntiAlias(true);
        mPaintTextRight.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintTextRight.setColor(Color.DKGRAY);
        //字体大小
//        mPaintTextRight.setTextSize(120);
        //画笔粗细
        mPaintTextRight.setStrokeWidth(10.0f);

        //Touch 圆的画笔
        mPaintCircle = new Paint();
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaintCircle.setColor(Color.BLACK);

        //左侧背景
        mRectFLeft = new RectF();
        //右侧背景
        mRectFRight = new RectF();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
        mRectFLeft.set(0, 0, mWidth / 2, mHeight);
        mRectFRight.set(mWidth / 2, 0, mWidth, mHeight);

        mPaintTextLeft.setTextSize(mHeight / 2);
        mPaintTextRight.setTextSize(mHeight / 2);
        mSlidingDistance = mHeight / 2;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        initDrawa(canvas);
        canvas.drawCircle(mSlidingDistance, mHeight / 2, mHeight / 2, mPaintCircle);
    }

    private void initDrawa(Canvas canvas) {
        if (!mIsFirstDraw) return;//是否是第一次画
        canvas.drawRoundRect(mRectFLeft, 0, 0, mPaintBgLeft);
        canvas.drawRoundRect(mRectFRight, 0, 0, mPaintBgRight);
        canvas.drawText("关", mWidth * 0.25f - mHeight / 4, mHeight * 0.70f, mPaintTextLeft);
        canvas.drawText("开", mWidth * 0.75f - mHeight / 4, mHeight * 0.70f, mPaintTextRight);
    }


    public void clickToggle() {
        Log.e(TAG, "点击事件执行");
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
                if (mMinDistanceX > 5) {
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

        invalidate();
    }
}
