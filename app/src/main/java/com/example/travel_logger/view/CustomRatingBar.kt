package com.example.travel_logger.view

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

class CustomRatingBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var rating: Float = 4.0f
        set(value) {
            field = value.coerceIn(1.0f, 5.0f)
            onRatingChangeListener?.invoke(field)
            invalidate()
        }

    var onRatingChangeListener: ((Float) -> Unit)? = null

    private val numStars = 5
    private val starFilledPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F59E0B") // Amber Gold
        style = Paint.Style.FILL
    }
    private val starUnfilledPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#E2E8F0") // Soft Slate Border
        style = Paint.Style.STROKE
        strokeWidth = 4f
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }
    private val starUnfilledBgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#F8FAFC")
        style = Paint.Style.FILL
    }

    private val starPath = Path()

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val totalWidth = width.toFloat()
        val totalHeight = height.toFloat()
        if (totalWidth <= 0 || totalHeight <= 0) return

        val availableWidth = totalWidth - paddingLeft - paddingRight
        val availableHeight = totalHeight - paddingTop - paddingBottom
        val starSize = min(availableWidth / numStars, availableHeight) * 0.85f
        val starSpacing = (availableWidth - (starSize * numStars)) / (numStars + 1)

        val outerRadius = starSize / 2f
        val innerRadius = outerRadius * 0.42f

        for (i in 0 until numStars) {
            val cx = paddingLeft + starSpacing + (i * (starSize + starSpacing)) + outerRadius
            val cy = paddingTop + (availableHeight / 2f)

            createStarPath(starPath, cx, cy, outerRadius, innerRadius)

            val starFillFraction = (rating - i).coerceIn(0f, 1f)

            // Draw background unfilled star
            canvas.drawPath(starPath, starUnfilledBgPaint)
            canvas.drawPath(starPath, starUnfilledPaint)

            // Draw filled star fraction
            if (starFillFraction > 0f) {
                canvas.save()
                val clipRect = RectF(
                    cx - outerRadius,
                    cy - outerRadius,
                    cx - outerRadius + (outerRadius * 2f * starFillFraction),
                    cy + outerRadius
                )
                canvas.clipRect(clipRect)
                canvas.drawPath(starPath, starFilledPaint)
                canvas.restore()
            }
        }
    }

    private fun createStarPath(path: Path, cx: Float, cy: Float, outerRadius: Float, innerRadius: Float) {
        path.reset()
        val points = 5
        var angle = -Math.PI / 2

        for (i in 0 until points * 2) {
            val r = if (i % 2 == 0) outerRadius else innerRadius
            val x = cx + (r * cos(angle)).toFloat()
            val y = cy + (r * sin(angle)).toFloat()

            if (i == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
            angle += Math.PI / points
        }
        path.close()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                val touchX = event.x - paddingLeft
                val availableWidth = width - paddingLeft - paddingRight
                if (availableWidth > 0) {
                    val rawRating = (touchX / availableWidth) * numStars
                    // Round to nearest 0.5 step for satisfying UX
                    val steppedRating = (Math.round(rawRating * 2) / 2f).coerceIn(1.0f, 5.0f)
                    rating = steppedRating
                }
                return true
            }
        }
        return super.onTouchEvent(event)
    }
}
