package com.udacity

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.ContextCompat
import kotlin.properties.Delegates


class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {
    private var widthSize = 0
    private var heightSize = 0
    private var buttonText: String
    private var buttonBackgroundColor = Color.parseColor("#07C2AA") //buttonBackgroundColor
    private var progress: Float = 0f
    private var valueAnimator = ValueAnimator()
    private val textRect = Rect()

    private var buttonState: ButtonState by Delegates.observable(
        ButtonState.Completed
    ) { _, _, newValue ->
        when (newValue) {
            ButtonState.Loading -> {
                setText("We are loading")
                setBgColor("#017E6E")
                valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
                    addUpdateListener {
                        progress = animatedValue as Float
                        invalidate()
                    }
                    duration = 3000
                }
                val animatorListener = object : Animator.AnimatorListener {
                    override fun onAnimationStart(animation: Animator) {
                    }

                    override fun onAnimationEnd(animation: Animator) {
                        setText("Download")
                        setBgColor("#07C2AA")
                        progress = 0f
                    }

                    override fun onAnimationCancel(animation: Animator) {
                    }

                    override fun onAnimationRepeat(animation: Animator) {
                    }
                }
                valueAnimator.addListener(animatorListener)
                valueAnimator.start()
            }
//            ButtonState.Completed -> {}
            else -> {}
        }
    }


    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0
        ).apply {
            try {
                buttonText = getString(R.styleable.LoadingButton_android_text).toString()
                buttonBackgroundColor = ContextCompat.getColor(context, R.color.colorPrimary)
            } finally {
                recycle()
            }
        }
    }

    private val textPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 50.0f
        color = Color.WHITE
    }

    private val backgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#07C2AA")
    }

    private val inProgressBackgroundPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#017E6E")
    }

    private val inProgressArcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.YELLOW
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cornerRadius = 20.0f
        val backgroundWidth = measuredWidth.toFloat()
        val backgroundHeight = measuredHeight.toFloat()

        canvas.drawColor(buttonBackgroundColor)
        textPaint.getTextBounds(buttonText, 0, buttonText.length, textRect)
        canvas.drawRoundRect(
            0f,
            0f,
            backgroundWidth,
            backgroundHeight,
            cornerRadius,
            cornerRadius,
            backgroundPaint
        )

        if (buttonState == ButtonState.Loading) {
            var progressVal = progress * measuredWidth.toFloat()
            canvas.drawRoundRect(
                0f,
                0f,
                progressVal,
                backgroundHeight,
                cornerRadius,
                cornerRadius,
                inProgressBackgroundPaint
            )

            val arcDiameter = cornerRadius * 2
            val arcRectSize = measuredHeight.toFloat() - paddingBottom.toFloat() - arcDiameter

            progressVal = progress * 360f
            canvas.drawArc(
                paddingStart.toFloat() + arcDiameter + 630f,
                paddingTop.toFloat() + arcDiameter,
                arcRectSize + 630f,
                arcRectSize,
                0f,
                progressVal,
                true,
                inProgressArcPaint
            )

        }

        // draw button text
        val centerX = measuredWidth.toFloat() / 2
        val centerY = measuredHeight.toFloat() / 2 - textRect.centerY()
        canvas.drawText(buttonText, centerX, centerY, textPaint)

    } // end of onDraw()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minWidth: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val width: Int = resolveSizeAndState(minWidth, widthMeasureSpec, 1)
        val height: Int = resolveSizeAndState(
            MeasureSpec.getSize(width),
            heightMeasureSpec,
            0
        )
        widthSize = width
        heightSize = height
        setMeasuredDimension(width, height)
    }

    fun setLoadingButtonState(state: ButtonState) {
        buttonState = state
    }

    private fun setText(buttonText: String) {
        this.buttonText = buttonText
        invalidate()
        requestLayout()
    }

    private fun setBgColor(backgroundColor: String) {
        buttonBackgroundColor = Color.parseColor(backgroundColor)
        invalidate()
        requestLayout()
    }

}

