package com.x.leo.slidebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

/**
 * @作者:My
 * @创建日期: 2017/6/1 10:45
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class SlideBar extends View {

    private boolean      mDoRound;
    private int          mRoundNum;
    private int          mBottomColor;
    private int          mBarColor;
    private int          mBarLength;
    private int          mMeasuredHeight;
    private int          mMeasuredWidth;
    private RectF        mRect;
    private Paint        mPaint;
    private RectF        mBarRect;
    private CharSequence mText;
    private int          mTextColor;
    private int          mtextSize;
    private OnSlideDrag  mOnSlideDrag;
    private float        touchX;
    private int          mBorderStrong;
    private int          mBorderColor;
    private RectF        mBorderRect;
    private boolean      mAutoBack;

    public SlideBar(Context context) {
        this(context, null);
    }

    public SlideBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public SlideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public SlideBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SlideBar);
        mDoRound = attributes.getBoolean(R.styleable.SlideBar_doRound, false);
        mRoundNum = attributes.getDimensionPixelSize(R.styleable.SlideBar_roundNum, 10);
        mBottomColor = attributes.getColor(R.styleable.SlideBar_bottomColor, Color.parseColor("#9c9c9c"));
        mBarColor = attributes.getColor(R.styleable.SlideBar_barColor, Color.BLACK);
        if (mBarColor == mBottomColor) {
            throw new IllegalArgumentException("barcolor and bottomcolor should be diffent");
        }
        mBarLength = attributes.getDimensionPixelSize(R.styleable.SlideBar_barLength, -1);
        if (mBarLength == -1) {
            throw new IllegalArgumentException("bar length should be set");
        }
        mText = attributes.getText(R.styleable.SlideBar_text);
        mTextColor = attributes.getColor(R.styleable.SlideBar_textColor, Color.BLACK);
        mtextSize = attributes.getDimensionPixelSize(R.styleable.SlideBar_textSize, 15);
        mBorderStrong = attributes.getDimensionPixelSize(R.styleable.SlideBar_borderStrong, 0);
        mBorderColor = attributes.getColor(R.styleable.SlideBar_borderColor, Color.parseColor("#779c9c9c"));
        mAutoBack = attributes.getBoolean(R.styleable.SlideBar_autoBack, false);
        attributes.recycle();
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        mMeasuredHeight = getMeasuredHeight();
        mMeasuredWidth = getMeasuredWidth();
        if (mRect == null) {
            mRect = new RectF(0.0f + getPaddingLeft() + mBorderStrong, 0.0f + getPaddingTop() + mBorderStrong, mMeasuredWidth - getPaddingRight() - mBorderStrong, mMeasuredHeight - getPaddingBottom() - mBorderStrong);
        }
        if (mBarLength >= mMeasuredWidth) {
            throw new IllegalArgumentException("bar length should be smaller than Slide bar length");
        }
        if (mBarRect == null) {
            mBarRect = new RectF(0.0f + getPaddingLeft() + mBorderStrong, 0.0f + getPaddingTop() + mBorderStrong, mBarLength + getPaddingLeft() + mBorderStrong, mMeasuredHeight - getPaddingBottom() - mBorderStrong);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (mBarRect == null || mRect == null) {
            return super.onTouchEvent(event);
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (mBarRect.contains(event.getX(), event.getY())) {
                    if (mOnSlideDrag != null) {
                        mOnSlideDrag.onDragStart(this);
                    }
                    touchX = event.getX();
                    return true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (mRect.contains(event.getX(), event.getY())) {
                    float distance = event.getX() - touchX;
                    touchX = event.getX();
                    float left = mBarRect.left + distance > mMeasuredWidth - mBarLength - getPaddingRight() - mBorderStrong ? mMeasuredWidth - mBarLength - getPaddingRight() - mBorderStrong : mBarRect.left + distance;
                    left = left < getPaddingLeft() + mBorderStrong ? getPaddingLeft() + mBorderStrong : left;
                    if (mOnSlideDrag != null) {
                        mOnSlideDrag.onDraging(this, (int) ((left - getPaddingLeft() - mBorderStrong) * 100 / (mMeasuredWidth - mBarLength - getPaddingLeft() - getPaddingRight() - 2 * mBorderStrong) + 0.5f));
                    }
                    mBarRect.set(left, 0.0f + getPaddingTop() + mBorderStrong, left + mBarLength, mMeasuredHeight - getPaddingBottom() - mBorderStrong);
                    invalidate();
                } else {
                    if (mAutoBack) {
                        mBarRect.set(0.0f + getPaddingLeft() + mBorderStrong, 0.0f + getPaddingTop() + mBorderStrong, mBarLength + getPaddingLeft() + mBorderStrong, mMeasuredHeight - getPaddingBottom() - mBorderStrong);
                        invalidate();
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (mBarRect.left == mMeasuredWidth - mBarLength - getPaddingRight() - mBorderStrong) {
                    if (mOnSlideDrag != null) {
                        mOnSlideDrag.onDragEnd(this, true);
                    }
                } else {
                    if (mOnSlideDrag != null) {
                        mOnSlideDrag.onDragEnd(this, false);
                    }
                    if (mAutoBack) {
                        mBarRect.set(0.0f + getPaddingLeft() + mBorderStrong, 0.0f + getPaddingTop() + mBorderStrong, mBarLength + getPaddingLeft() + mBorderStrong, mMeasuredHeight - getPaddingBottom() - mBorderStrong);
                        invalidate();
                    }
                }
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        //getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mRect == null) {
            return;
        }
        if (mBorderStrong > 0) {
            mPaint.setColor(mBorderColor);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeWidth(mBorderStrong);
            canvas.drawRoundRect(mRect, mRoundNum, mRoundNum, mPaint);
        }
        mPaint.setColor(mBottomColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setStrokeWidth(1);
        if (mDoRound) {
            canvas.drawRoundRect(mRect, mRoundNum, mRoundNum, mPaint);
        } else {
            canvas.drawRect(mRect, mPaint);
        }
        if (mText != null && mText.length() > 0) {
            mPaint.setColor(mTextColor);
            mPaint.setTextSize(mtextSize);
            mPaint.setStyle(Paint.Style.FILL);
            float measuredWidth = mPaint.measureText(mText.toString());
            canvas.drawText(mText, 0, mText.length(), mMeasuredWidth / 2 - measuredWidth / 2, mMeasuredHeight / 2 + mtextSize / 4, mPaint);
        }
        mPaint.setColor(mBarColor);
        mPaint.setStyle(Paint.Style.FILL);
        if (mDoRound) {
            canvas.drawRoundRect(mBarRect, mRoundNum, mRoundNum, mPaint);
        } else {
            canvas.drawRect(mBarRect, mPaint);
        }
    }

    public void setOnSlideDragListener(OnSlideDrag l) {
        mOnSlideDrag = l;
    }
}
