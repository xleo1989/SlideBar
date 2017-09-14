package com.x.leo.slidebar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

/**
 * @作者:My
 * @创建日期: 2017/7/12 15:21
 * @描述:${TODO}
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 */

public class SlideBarWithText extends LinearLayout implements SlideBarApi {
    private double minimum = 0;
    private double maximum = 100;
    private OnSlideDrag mOnSlideDrag;
    private int         mBarHandlerResourceId;
    private boolean     doRound;
    private int         mStrokeWidth;
    private int         mRoundNum;


    private float          mSolidTextSize;
    private float          mTipsTextSize;
    private int            mBorderColor;
    private int            mBottomColor;
    private int            mForeColor;
    private int            mSolidColor;
    private int            mTipsColor;
    private int            mTipsResId;
    private TextView       mTips;
    private TextView       mSolidMini;
    private TextView       mSolidMaxi;
    private SeekBar        mSeekBar;
    private boolean        minimumSetUped;
    private boolean        maxSetuped;
    private int            mMeasuredWidth;
    private int            mMeasuredHeight;
    private View           mView;
    private RelativeLayout mRelativeLayout;
    private String         mMinText;
    private String         mMaxText;
    private ValueConvertor mConvertor;
    private int mHorizPadding;
    private int mPaddingHorizWithTips;
    private boolean mDragableState = true;

    public SlideBarWithText(Context context) {
        this(context, null);
    }

    public SlideBarWithText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public SlideBarWithText(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        this(context, attrs);
    }

    public SlideBarWithText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        this(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs) {
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.SlideBarWithText);
        mBarHandlerResourceId = attributes.getResourceId(R.styleable.SlideBarWithText_barresid, R.drawable.barhandler);
        doRound = attributes.getBoolean(R.styleable.SlideBarWithText_doround, true);
        mStrokeWidth = attributes.getDimensionPixelSize(R.styleable.SlideBarWithText_borderstrong, 0);
        mRoundNum = attributes.getDimensionPixelSize(R.styleable.SlideBarWithText_roundnum, (int) Utils.dp2px(context, 5));
        mSolidTextSize = attributes.getDimension(R.styleable.SlideBarWithText_solidtextsize, Utils.sp2px(context, 14));
        mTipsTextSize = attributes.getDimension(R.styleable.SlideBarWithText_tipstextsize, Utils.sp2px(context, 14));
        mBorderColor = attributes.getColor(R.styleable.SlideBarWithText_bordercolor, Color.TRANSPARENT);
        mBottomColor = attributes.getColor(R.styleable.SlideBarWithText_bottomcolor, Color.BLACK);
        mForeColor = attributes.getInt(R.styleable.SlideBarWithText_forecolore, 0);
        mSolidColor = attributes.getColor(R.styleable.SlideBarWithText_solidtextcolor, Color.BLACK);
        mTipsColor = attributes.getColor(R.styleable.SlideBarWithText_tipstextcolor, Color.RED);
        mTipsResId = attributes.getResourceId(R.styleable.SlideBarWithText_tipsresid, R.drawable.tips_blue);
        mMinText = attributes.getString(R.styleable.SlideBarWithText_minitext);
        mMaxText = attributes.getString(R.styleable.SlideBarWithText_maxitext);
        mPaddingHorizWithTips = attributes.getDimensionPixelSize(R.styleable.SlideBarWithText_paddingHor, 0);
        attributes.recycle();
        mTips = new TextView(context);
        mSolidMini = new TextView(context);
        mSolidMaxi = new TextView(context);
        mRelativeLayout = new RelativeLayout(context);
        mSeekBar = new SeekBar(context, null, R.style.style_seekBar);
        setupSeekBar();
        setupTextViews();
        addToParent();
    }

    private void addToParent() {
        setOrientation(LinearLayout.VERTICAL);
        LayoutParams tipsLayoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mTips, tipsLayoutParams);
        LayoutParams seekBarLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mSeekBar, seekBarLayoutParams);
        seekBarLayoutParams.topMargin = (int) Utils.dp2px(getContext(), 6);
        seekBarLayoutParams.leftMargin = mPaddingHorizWithTips;
        seekBarLayoutParams.rightMargin = mPaddingHorizWithTips;
        LayoutParams relativeLayoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeLayoutParams.topMargin = (int) Utils.dp2px(getContext(), 5);
        relativeLayoutParams.leftMargin = mPaddingHorizWithTips;
        relativeLayoutParams.rightMargin = mPaddingHorizWithTips;
        addView(mRelativeLayout, relativeLayoutParams);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
//        mMeasuredWidth = getMeasuredWidth();
//        mMeasuredHeight = getMeasuredHeight();
//        if (mMeasuredWidth < Utils.dp2px(getContext(), 100)) {
//            throw new RuntimeException("to smalle space for width");
//        }
//        switch (MeasureSpec.getMode(heightMeasureSpec)) {
//            case MeasureSpec.UNSPECIFIED:
//            case MeasureSpec.AT_MOST:
//                mMeasuredHeight = (int) (Utils.dp2px(getContext(), 200) + 0.5f);
//                break;
//        }
//        setMeasuredDimension(MeasureSpec.makeMeasureSpec(mMeasuredWidth, MeasureSpec.EXACTLY)
//                , MeasureSpec.makeMeasureSpec(mMeasuredHeight, MeasureSpec.EXACTLY));
    }


    public void setPercentProgress(int percent) {
        if (percent >= 0 && percent <= 100) {
            mSeekBar.setProgress(percent);
            updateTipsPostion(percent);
        }
    }

    public void setValueProgress(double value) {
        if (value >= minimum && value < maximum) {
            int progress = (int) ((value - minimum) * 100 / (maximum - minimum) + 0.5f);
            setPercentProgress(progress);
        }
    }

    private void updateTipsPostion(final int progress) {
        if (mSeekBar.getWidth() <= 0) {
            mSeekBar.post(new Runnable() {
                @Override
                public void run() {
                    translateTipsX(progress);
                }
            });
        }else {
            translateTipsX(progress);
        }
    }

    private void translateTipsX(int progress) {
        int width = mSeekBar.getWidth() - mHorizPadding * 2;
        if (width == 0) {
            mSeekBar.measure(0, 0);
            width = mSeekBar.getMeasuredWidth() - mHorizPadding * 2;
        }
        int i = mPaddingHorizWithTips + width / 100 * progress   + mSeekBar.getThumb().getIntrinsicWidth() / 2 + mHorizPadding/2 - mTips.getWidth() / 2;

        int result = i < 0 ? 0 : ((i + mTips.getWidth()) > getWidth() ? (getWidth() - mTips.getWidth()) : i);
        mTips.setTranslationX(result);
    }

    private void setupSeekBar() {
        switch (mForeColor) {
            case 0:
                mSeekBar.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_layerlist_blue));
                mSeekBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.seekbar_layerlist_blue));
                break;
            case 1:
                mSeekBar.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_layerlist));
                mSeekBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.seekbar_layerlist));
                break;
            default:
                mSeekBar.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_layerlist));
                mSeekBar.setIndeterminateDrawable(getResources().getDrawable(R.drawable.seekbar_layerlist));
        }
        mSeekBar.setIndeterminate(false);
        mSeekBar.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb));
        mHorizPadding = mSeekBar.getThumb().getIntrinsicWidth()/4;
        mSeekBar.setThumbOffset(mHorizPadding);
        mSeekBar.setFocusable(true);
        mSeekBar.setClickable(false);
        mSeekBar.setPadding(mHorizPadding, 0, mHorizPadding, 0);
        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                updateTipsPostion(progress);
                if (mOnSlideDrag != null) {
                    mOnSlideDrag.onDraging(mTips, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                if (mOnSlideDrag != null) {
                    mOnSlideDrag.onDragStart(mTips);
                }
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (mOnSlideDrag != null) {
                    mOnSlideDrag.onDragEnd(mTips, mSeekBar.getProgress() == 100);
                }
            }
        });
    }

    private void setupTextViews() {
        mTips.setBackgroundResource(mTipsResId);
        mTips.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTipsTextSize);
        mTips.setTextColor(mTipsColor);
        TextPaint paint = mTips.getPaint();
        paint.setFakeBoldText(true);
        mTips.setGravity(Gravity.CENTER);
        mTips.setText("");
        mTips.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        mSolidMaxi.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSolidTextSize);
        mSolidMaxi.setTextColor(mSolidColor);
        mSolidMaxi.getPaint().setFakeBoldText(true);
        mSolidMaxi.setGravity(Gravity.CENTER);
        if (!TextUtils.isEmpty(mMaxText) && TextUtils.isEmpty(mSolidMaxi.getText())) {
            mSolidMaxi.setText(mMaxText);
            Log.d(TAG, "maxTextViews: " + mMaxText);
        }
        RelativeLayout.LayoutParams mSolidMaxiLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mSolidMaxi.setLayoutParams(mSolidMaxiLayoutParams);

        mSolidMini.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSolidTextSize);
        mSolidMini.getPaint().setFakeBoldText(true);
        mSolidMini.setTextColor(mSolidColor);
        mSolidMini.setGravity(Gravity.CENTER);
        if (!TextUtils.isEmpty(mMinText) && TextUtils.isEmpty(mSolidMini.getText())) {
            mSolidMini.setText(mMinText);
            Log.d(TAG, "minTextViews: " + mMinText);
        }
        RelativeLayout.LayoutParams mSolidMiniLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mSolidMini.setLayoutParams(mSolidMiniLayoutParams);
        RelativeLayout.LayoutParams miniLayoutParmas = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        miniLayoutParmas.leftMargin = mHorizPadding;
        mRelativeLayout.addView(mSolidMini, miniLayoutParmas);
        RelativeLayout.LayoutParams maxiLayoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        maxiLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        maxiLayoutParams.rightMargin = mHorizPadding;
        mRelativeLayout.addView(mSolidMaxi, maxiLayoutParams);

    }


    public void setValueConvertor(ValueConvertor convertor) {
        mConvertor = convertor;
    }

    public static final String TAG = "SlideBarWithText";

    private void updateView() {
        if (mConvertor != null) {
            if (minimumSetUped) {
                mSolidMini.setText(mConvertor.convertorValue(minimum));
                Log.d(TAG, "updateView: " + mConvertor.convertorValue(minimum));
            }
            if (maxSetuped) {
                mSolidMaxi.setText(mConvertor.convertorValue(maximum));
                Log.d(TAG, "updateView: " + mConvertor.convertorValue(maximum));
            }
            if (minimumSetUped && maxSetuped) {
                mTips.setText(mConvertor.convertorValue(getCurrentValue()));
            }
        } else {
            if (minimumSetUped) {
                mSolidMini.setText(minimum + "");
            }
            if (maxSetuped) {
                mSolidMaxi.setText(maximum + "");
            }
            if (minimumSetUped && maxSetuped) {
                mTips.setText(getCurrentValue() + "");
            }
        }
    }


    @Override
    public void setMinimum(double minimum) {
        if (maxSetuped && minimum > this.maximum) {
            throw new IllegalArgumentException("maximum can't be smaller than minimum");
        }
        minimumSetUped = true;
        this.minimum = minimum;
        updateView();
    }

    @Override
    public void setMaximum(double maxmun) {
        if (minimumSetUped && maxmun < this.minimum) {
            throw new IllegalArgumentException("maximum can't be smaller than minimum");
        }
        maxSetuped = true;
        this.maximum = maxmun;
        updateView();
    }
//TODO
    public void setDragableState(boolean dragableState){
        mDragableState = dragableState;
    }
    @Override
    public double getMaximum() {
        return maximum;
    }

    @Override
    public double getMinimum() {
        return minimum;
    }

    @Override
    public float getCurrentPerception() {
        return mSeekBar.getProgress();
    }

    @Override
    public double getCurrentValue() {
        if (mConvertor == null) {
            return mSeekBar.getProgress() * (maximum - minimum) / 100 + minimum;
        } else {
            return mConvertor.ValueFromPercent(mSeekBar.getProgress());
        }
    }

    @Override
    public void setOnDragCallBack(OnSlideDrag l) {
        mOnSlideDrag = l;
    }

    public float getTipsTextSize() {
        return mTipsTextSize;
    }

    public void setTipsTextSize(float tipsTextSize) {
        mTipsTextSize = tipsTextSize;
    }

    public String getMinText() {
        return mMinText;
    }

    public void setMinText(String minText) {
        mMinText = minText;
    }

    public String getMaxText() {
        return mMaxText;
    }

    public void setMaxText(String maxText) {
        mMaxText = maxText;
    }

    public interface ValueConvertor {
        String convertorValue(double value);

        double ValueFromPercent(int progress);
    }
}
