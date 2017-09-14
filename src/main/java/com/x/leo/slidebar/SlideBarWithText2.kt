package com.x.leo.slidebar

import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Handler
import android.os.Looper
import android.text.TextUtils
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

/**
 * @作者:XLEO
 * @创建日期: 2017/9/13 10:05
 * @描述:${TODO}
 *
 * @更新者:${Author}$
 * @更新时间:${Date}$
 * @更新描述:${TODO}
 * @下一步：
 */
class SlideBarWithText2(ctx: Context, attrs: AttributeSet?) : View(ctx, attrs) {
    private val NORES: Int = -1
    var tipText: CharSequence? = null
    var leftText: CharSequence? = null
    var leftTextSize: Int = 30
    var rightTextSize: Int = 30
    var tipTextSize: Int = 30
    var leftTextColor: Int = Color.RED
    var rightTextColor: Int = Color.RED
    var tipTextColor: Int = Color.YELLOW
    var rightText: CharSequence? = null
    var progressBarBag: Int = Color.BLACK
    var progressColor: Int = Color.GREEN
    var barDrawable: Int = NORES
    var tipBag: Int = NORES
    var leftTextBag: Int = NORES
    var rightTextBag: Int = NORES
    var localPaint: Paint


    var progressBagRect: RectF? = null
    var progressBottomPadding: Int = 10
    var progressTipsPadding: Int = 10
    var progressRound: Int = 30
    private var location: Float = 0f
    var progressRect: RectF? = null
    private var tipsHeight: Float = 30f
    private var bottomHeight: Float = 50f
    private var textHorPadding: Int = 50
    private var textVerticalPadding: Int = 30
    private var progressBarHeight: Int = 60
    private var legalTouchArea: RectF? = null
    private var barWidth: Int = 100
    private var barHeight: Int = 60
    private var dockSize: Int = barWidth

    private var angleHeight = 20
    private var angleWidth = 30
    private var fillColor: Int = Color.GREEN
    private var strokeColor: Int = Color.RED
    private var strokeSize: Int = 10

    init {
        if (attrs != null) {
            val attr = ctx.obtainStyledAttributes(attrs, R.styleable.SlideBarWithText2)
            tipBag = attr.getResourceId(R.styleable.SlideBarWithText2_tipsBag, NORES)
            tipText = attr.getText(R.styleable.SlideBarWithText2_tipsDefText)
            tipTextColor = attr.getColor(R.styleable.SlideBarWithText2_tipsTextColor, Color.BLACK)
            tipTextSize = attr.getDimensionPixelSize(R.styleable.SlideBarWithText2_tipsTextSize, 24)
            progressBarBag = attr.getColor(R.styleable.SlideBarWithText2_progressBagColor, Color.BLACK)
            progressColor = attr.getColor(R.styleable.SlideBarWithText2_progressColor, Color.GREEN)
            barDrawable = attr.getResourceId(R.styleable.SlideBarWithText2_bar, NORES)
            leftText = attr.getText(R.styleable.SlideBarWithText2_leftText)
            rightText = attr.getText(R.styleable.SlideBarWithText2_rightText)
            leftTextColor = attr.getColor(R.styleable.SlideBarWithText2_leftTextColor, Color.BLACK)
            leftTextSize = attr.getDimensionPixelSize(R.styleable.SlideBarWithText2_leftTextSize, 24)
            rightTextColor = attr.getColor(R.styleable.SlideBarWithText2_rightTextColor, Color.BLACK)
            rightTextSize = attr.getDimensionPixelSize(R.styleable.SlideBarWithText2_rightTextSize, 24)
            leftTextBag = attr.getResourceId(R.styleable.SlideBarWithText2_leftTextBag, NORES)
            rightTextBag = attr.getResourceId(R.styleable.SlideBarWithText2_rightTextBag, NORES)

            fillColor = attr.getColor(R.styleable.SlideBarWithText2_tipFillColor, Color.BLACK)
            strokeColor = attr.getColor(R.styleable.SlideBarWithText2_tipStrokeColor, Color.YELLOW)
            strokeSize = attr.getDimensionPixelSize(R.styleable.SlideBarWithText2_tipStrokeWidth, 10)
            progressBottomPadding = attr.getDimensionPixelSize(R.styleable.SlideBarWithText2_progressBottomDistance, 10)
            progressTipsPadding = attr.getDimensionPixelSize(R.styleable.SlideBarWithText2_progressTipDistance, 10)
            progressBarHeight = attr.getDimensionPixelSize(R.styleable.SlideBarWithText2_progressbarHeight, 60)
            attr.recycle()
        }
        localPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    }

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        parent?.requestDisallowInterceptTouchEvent(true)
        return super.dispatchTouchEvent(event)
    }
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val mode = MeasureSpec.getMode(heightMeasureSpec)
        getTipsHeight()
        getBottomHeight()
        if (mode != MeasureSpec.EXACTLY) {
            val height = tipsHeight + bottomHeight + progressBarHeight + paddingTop + paddingBottom + progressBottomPadding + progressTipsPadding + 1f
            setMeasuredDimension(getDefaultSize(suggestedMinimumWidth, widthMeasureSpec),
                    height.toInt())
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
        initSizes()
    }


    private fun initSizes() {
        if (tipsHeight + bottomHeight + progressBarHeight + paddingTop + paddingBottom + progressBottomPadding + progressTipsPadding > measuredHeight) {
            throw IllegalArgumentException("the height is too small to content this view")
        }
        val middleHight = measuredHeight / 2
        progressBagRect = RectF(paddingLeft.toFloat(),
                middleHight.toFloat() - progressBarHeight / 2, measuredWidth.toFloat() - paddingRight, middleHight.toFloat() + progressBarHeight / 2)
        legalTouchArea = RectF(0f,
                0f, measuredWidth.toFloat(), measuredHeight.toFloat())

        var barDrawableT: Drawable? = null
        if (barDrawable == NORES) {
            barDrawableT = resources.getDrawable(R.drawable.seekbar_thumb)
        } else {
            barDrawableT = resources.getDrawable(barDrawable)
        }
        barHeight = if (barDrawableT!!.intrinsicHeight > progressBarHeight) barDrawableT!!.intrinsicHeight else progressBarHeight
        barWidth = barDrawableT!!.intrinsicWidth / barDrawableT!!.intrinsicHeight * barHeight
        dockSize = barWidth
        if (location == 0f) {
            getRealLocation(location)
        }
    }

    private fun getBottomHeight() {
        localPaint.textSize = leftTextSize.toFloat()
        val fontMetrics = localPaint.getFontMetrics()
        val leftHeight = fontMetrics.bottom - fontMetrics.top
        localPaint.textSize = rightTextSize.toFloat()
        val rightMetrics = localPaint.getFontMetrics()
        val rightHeight = rightMetrics.bottom - rightMetrics.top
        bottomHeight = if (rightHeight > leftHeight) rightHeight + textVerticalPadding * 2 else leftHeight + textVerticalPadding * 2
    }

    private fun getTipsHeight() {
        localPaint.textSize = tipTextSize.toFloat()
        val fontMetrics = localPaint.getFontMetrics()
        tipsHeight = fontMetrics.bottom - fontMetrics.top + textVerticalPadding * 2
    }

    private val localHandler: Handler by lazy {
        Handler(Looper.myLooper())
    }
    var onDragCallBack:OnSlideDrag? = null
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null) {
            return false
        }
        val obtain = MotionEvent.obtain(event)
        when (obtain.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                if (isTouchAreaLegal(obtain)) {
                    handleNewLocation(obtain)
                }
            }
            MotionEvent.ACTION_MOVE -> {
                if (isTouchAreaLegal(obtain)) {
                    handleNewLocation(obtain)
                }
            }
            MotionEvent.ACTION_UP -> {

            }
            else -> {
            }
        }
        obtain.recycle()
        return true
    }

    private fun handleNewLocation(obtain: MotionEvent) {
        localHandler.post {
            getRealLocation(obtain.x)
            obtainTipText()
            if (onDragCallBack != null && localValueConvertor != null) {
                onDragCallBack!!.onDraging(this,localValueConvertor!!.valueToProgress(getValue()))
            }
            postInvalidate()
        }
    }

    private fun obtainTipText() {
        if (localValueConvertor != null) {
            tipText = localValueConvertor!!.valueToTipText(getValue())

        } else {
            tipText = "wo de progress" + locationToProgress(location)
        }
    }

    private fun getRealLocation(x: Float) {
        if (x < progressBagRect!!.left + dockSize / 2) {
            location = progressBagRect!!.left + dockSize / 2
        } else if (x > progressBagRect!!.right - dockSize / 2) {
            location = progressBagRect!!.right - dockSize / 2
        } else {
            location = x
        }
    }

    private fun isTouchAreaLegal(obtain: MotionEvent): Boolean {
        if (legalTouchArea != null) {
            return legalTouchArea!!.contains(obtain.x, obtain.y)
        }
        return false
    }

    var localValueConvertor: ValueConvertor? = null
    fun setValue(value: Double) {
        if (localValueConvertor == null) {
            throw IllegalArgumentException("please set the valueConvertor first")
        }
        setProgress(localValueConvertor!!.valueToProgress(value))
    }

    fun getValue(): Double {
        if (localValueConvertor == null) {
            throw IllegalArgumentException("please set the valueConvertor first")
        }
        return localValueConvertor!!.progressToValue(getProgress())
    }

    fun setProgress(progress: Int) {
        post {
            location = progressToLocation(progress)
            obtainTipText()
            invalidate()
        }
    }

    fun getProgress(): Int {
        return locationToProgress(location)
    }

    fun setLeftText(text: String) {
        post {
            leftText = text
            invalidate()
        }
    }

    fun setRightText(text: String) {
        post {
            rightText = text
            invalidate()
        }
    }

    override fun onDraw(canvas: Canvas?) {
        if (canvas == null)
            return
        background?.draw(canvas)
        localPaint.color = progressBarBag
        localPaint.style = Paint.Style.FILL
        canvas.drawRoundRect(progressBagRect, progressRound.toFloat(), progressRound.toFloat(), localPaint)
        drawProgress(canvas)
        drawBar(canvas)
        drawBottomTexts(canvas)
        drawTipsText(canvas)

    }


    private fun drawBar(canvas: Canvas) {
        val barBounds = Rect(
                location.toInt() - barWidth / 2,
                progressBagRect!!.centerY().toInt() - barHeight / 2,
                location.toInt() + barWidth / 2,
                progressBagRect!!.centerY().toInt() + barHeight / 2
        )
        if (barDrawable != -1) {
            val barDraw = resources.getDrawable(barDrawable)
            barDraw.setBounds(barBounds)
            barDraw.draw(canvas)
        } else {
            val bardraw = resources.getDrawable(R.drawable.seekbar_thumb)
            bardraw.setBounds(barBounds)
            bardraw.draw(canvas)
        }
    }

    private fun drawTipsText(canvas: Canvas) {
        if (!TextUtils.isEmpty(tipText)) {
            localPaint.textSize = tipTextSize.toFloat()

            val tipsBounds = Rect()
            localPaint.getTextBounds(tipText.toString(), 0, tipText!!.length, tipsBounds)
            val tipDrawableBounds = Rect(
                    location.toInt() - tipsBounds
                            .width() / 2 - textHorPadding,
                    progressBagRect!!.top.toInt() - progressTipsPadding.toInt() - 2 * textVerticalPadding - tipsBounds.height(),
                    location.toInt() + tipsBounds
                            .width() / 2 + textHorPadding,
                    progressBagRect!!.top.toInt() - progressTipsPadding.toInt()
            )
            var diff = 0
            if (tipDrawableBounds.left < 0) {
                diff = 0 - tipDrawableBounds.left
                tipDrawableBounds.offset(diff, 0)
            }
            if (tipDrawableBounds.right > measuredWidth) {
                diff = measuredWidth - tipDrawableBounds.right
                tipDrawableBounds.offset(diff, 0)
            }
            if (tipBag != NORES) {
                val tipDrawable = resources.getDrawable(leftTextBag)
                tipDrawable.setBounds(tipDrawableBounds)
                tipDrawable.draw(canvas)
            } else {
                drawDefaultTipDrawable(canvas, tipDrawableBounds)
            }
            localPaint.color = tipTextColor
            localPaint.style = Paint.Style.FILL_AND_STROKE
            localPaint.strokeWidth = 1f
            canvas.drawText(tipText.toString(), location - tipsBounds
                    .width() / 2 + diff, (tipDrawableBounds.bottom.toFloat() + tipDrawableBounds.top) / 2, localPaint)
        }
    }

    private fun drawDefaultTipDrawable(canvas: Canvas, tipDrawableBounds: Rect) {
        val path = Path()
        val bottom = progressBagRect!!.top - progressTipsPadding
        path.moveTo(location, bottom)
        path.lineTo(location + angleWidth / 2, bottom - angleHeight)
        path.lineTo(tipDrawableBounds.right.toFloat(), bottom - angleHeight)
        path.lineTo(tipDrawableBounds.right.toFloat(), tipDrawableBounds.top.toFloat())
        path.lineTo(tipDrawableBounds.left.toFloat(), tipDrawableBounds.top.toFloat())
        path.lineTo(tipDrawableBounds.left.toFloat(), bottom - angleHeight)
        path.lineTo(location - angleWidth / 2, bottom - angleHeight)
        path.lineTo(location, bottom)
        path.close()
        localPaint.color = fillColor
        localPaint.strokeWidth = strokeSize.toFloat()
        localPaint.style = Paint.Style.FILL
        canvas.drawPath(path, localPaint)
        localPaint.color = strokeColor
        localPaint.style = Paint.Style.STROKE
        canvas.drawPath(path, localPaint)
    }


    private fun drawBottomTexts(canvas: Canvas) {
        if (!TextUtils.isEmpty(leftText)) {
            localPaint.textSize = leftTextSize.toFloat()
            localPaint.color = leftTextColor
            val leftTextBounds = Rect()
            localPaint.getTextBounds(leftText.toString(), 0, leftText!!.length, leftTextBounds)
            if (leftTextBag != NORES) {
                val leftTextDrawable = resources.getDrawable(leftTextBag)
                leftTextDrawable.setBounds(paddingLeft,
                        progressBagRect!!.bottom.toInt() + progressBottomPadding.toInt(),
                        paddingLeft + leftTextBounds.width() + textHorPadding * 2,
                        progressBagRect!!.bottom.toInt() + 2 * textVerticalPadding + progressBottomPadding.toInt() + leftTextBounds.height())
                leftTextDrawable.draw(canvas)
            }
            canvas.drawText(leftText.toString(), if (leftTextBag == NORES) paddingLeft.toFloat() else paddingLeft + textHorPadding.toFloat(), progressBagRect!!.bottom + progressBottomPadding + leftTextBounds.height() + if (leftTextBag == NORES) 0 else textVerticalPadding, localPaint)
        }

        if (!TextUtils.isEmpty(rightText)) {
            localPaint.textSize = rightTextSize.toFloat()
            localPaint.color = rightTextColor
            val rightTextBounds = Rect()
            localPaint.getTextBounds(rightText.toString(), 0, rightText!!.length, rightTextBounds)
            if (rightTextBag != NORES) {
                val rightDrawable = resources.getDrawable(rightTextBag)
                rightDrawable.setBounds(
                        measuredWidth - paddingRight - 2 * textHorPadding - rightTextBounds.width(),
                        progressBagRect!!.bottom.toInt() + progressBottomPadding.toInt(),
                        measuredWidth - paddingRight,
                        progressBagRect!!.bottom.toInt() + progressBottomPadding.toInt() + 2 * textVerticalPadding.toInt() + rightTextBounds.height()
                )
                rightDrawable.draw(canvas)
            }
            canvas.drawText(rightText.toString(), measuredWidth.toFloat() - paddingRight - rightTextBounds.width() - if (rightTextBag == NORES) 0 else textHorPadding, progressBagRect!!.bottom + progressBottomPadding + rightTextBounds.height() + if (rightTextBag == NORES) 0 else textVerticalPadding, localPaint)
        }
    }

    private fun drawProgress(canvas: Canvas) {
        localPaint.color = progressColor
        if (location > progressBagRect!!.left + dockSize / 2 && location <= progressBagRect!!.right) {
            progressRect = RectF(progressBagRect!!.left, progressBagRect!!.top, location, progressBagRect!!.bottom)
            canvas.drawRoundRect(progressRect, progressRound.toFloat(), progressRound.toFloat(), localPaint)
        } else if (location <= progressBagRect!!.left + dockSize / 2) {
        } else {
            progressRect = RectF(progressBagRect!!.left, progressBagRect!!.top, progressBagRect!!.right, progressBagRect!!.bottom)
            canvas.drawRoundRect(progressRect, progressRound.toFloat(), progressRound.toFloat(), localPaint)
        }
    }

    private fun progressToLocation(progress: Int): Float {
        return (progressBagRect!!.width() - dockSize) * progress / 100 + progressBagRect!!.left + dockSize / 2
    }

    private fun locationToProgress(location: Float): Int {
        val toInt = ((location - progressBagRect!!.left - dockSize / 2) / (progressBagRect!!.width() - dockSize) * 100).toInt()
        return if (toInt > 100) 100 else if (toInt < 0) 0 else toInt
    }
}

interface ValueConvertor {
    fun valueToProgress(value: Double): Int
    fun progressToValue(progress: Int): Double
    fun valueToTipText(value: Double): String
}