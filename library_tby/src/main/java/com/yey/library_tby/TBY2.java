package com.yey.library_tby;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;
@Deprecated
public class TBY2 extends View {
    private static final String TAG = TBY2.class.getName();

    private float mSlidingDistance;
    private float mStartX;//开始X值
    private float mLastX;//开始X值
    private boolean mIsEnableClick = true;//用户默认可以点击按钮进行切换
    private boolean mIsOpen;
    //================================================
    private Bitmap mOpenBG;
    private Bitmap mCloseBG;
    private Bitmap mTouch;
    private int mOpenBGId;
    private int mCloseBGId;
    private int mTouchId;
    private Paint mPaint;
    private int mWidth;
    private int mHeight;

    public TBY2(Context context) {
        this(context, null);
    }

    public TBY2(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TBY2(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initParame(context, attrs, defStyleAttr);
        initPaint();
        initBitmap(context);
    }

    private void initBitmap(Context context) {
        mTouch = drawableToBitmap(mTouchId, context);
        mCloseBG = drawableToBitmap(mCloseBGId, context);
        mOpenBG = drawableToBitmap(mOpenBGId, context);
    }


    private void initPaint() {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    private void initParame(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.ToggleColorY, defStyleAttr, 0);
        mOpenBGId = typedArray.getInteger(R.styleable.ToggleColorY_tby_open_bg, R.drawable.tiy_open);
        mCloseBGId = typedArray.getInteger(R.styleable.ToggleColorY_tby_close_bg, R.drawable.tiy_close);
        mTouchId = typedArray.getInteger(R.styleable.ToggleColorY_tby_touch, R.drawable.tiy_touch);
        typedArray.recycle();
    }


    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        mWidth = getWidth();
        mHeight = getHeight();
        mOpenBG.setWidth(mWidth);
        mOpenBG.setHeight(mHeight);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mIsOpen) {
            canvas.drawBitmap(mOpenBG, 0, 0, mPaint);//开
        } else {
            canvas.drawBitmap(mCloseBG, 0, 0, mPaint);//关
        }
        canvas.drawBitmap(mTouch, mSlidingDistance, (mHeight - mTouch.getHeight()) / 2, mPaint);
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
                if (mSlidingDistance < 0) {
                    mSlidingDistance = 0;
                }
                if (mSlidingDistance >= mWidth - mTouch.getWidth()) {
                    mSlidingDistance = mWidth - mTouch.getWidth();
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
            mSlidingDistance = mWidth - mTouch.getWidth();
        } else {
            //关
            mSlidingDistance = 0;
        }

        invalidate();
    }

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

