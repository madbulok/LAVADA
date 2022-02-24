package com.uzlov.dating.lavada.ui.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Drawable
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.util.Log
import android.view.animation.LinearInterpolator
import androidx.annotation.ColorInt
import androidx.annotation.Px
import androidx.core.animation.addListener
import androidx.core.animation.doOnRepeat
import androidx.core.graphics.drawable.toBitmap
import androidx.core.graphics.toRectF
import com.uzlov.dating.lavada.R
import com.uzlov.dating.lavada.app.dpTpPx
import kotlin.math.max
import kotlin.math.truncate

class UserAvatarImageView @JvmOverloads constructor (context: Context,
                                                     attrs: AttributeSet,
                                                     defStyleAttr: Int = 0)
    : androidx.appcompat.widget.AppCompatImageView(context, attrs, defStyleAttr) {

    companion object {
        private const val DEFAULT_SIZE = 40
        private const val DEFAULT_BORDER_WIDTH = 2

        @ColorInt
        private const val DEFAULT_BORDER_COLOR = Color.WHITE

        private val arrayColors = arrayOf(
            Color.parseColor("#6200EE"),
            Color.parseColor("#3700B3"),
            Color.parseColor("#03DAC5"),
            Color.parseColor("#ff8a50"),
            Color.parseColor("#ffc947"),
            Color.parseColor("#ffff6e"),
            Color.parseColor("#52c7b8"),
            Color.parseColor("#62efff"),
            Color.parseColor("#AAAAAA")
        )
    }

    private var hasPremium: Boolean
    private val TAG = javaClass.simpleName
    @Px
    var mBorderWidth: Float = context.dpTpPx(DEFAULT_BORDER_WIDTH)
    @ColorInt
    private var mBorderColor:Int = Color.RED

    @ColorInt
    private var mPremiumColor:Int = Color.RED

    private var mInitials = ""

    private val avatarPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val initialPaint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val premiumBorder = Paint(Paint.ANTI_ALIAS_FLAG)
    private val contentBorder = Paint(Paint.ANTI_ALIAS_FLAG)

    private val mViewRect = Rect()
    private val mPremiumViewRect = Rect()

    private val mBorderRect = Rect()
    private val mPremiumRect = Rect()

    private lateinit var  srcBm : Bitmap
    private var half : Int = 1


    private var size: Int = 0
    private var sizeSource: Int = 0

    // need if isAnimated = true
    private var mValueOfSpeedAnimation: Int = 300
    private var mValueOfIncrease: Int = 10

    // enable / disable animation
    private var isAnimated = true

    // change state after click
    private var isAvatarMode = true

    init {
        val  mTypedArray = context.obtainStyledAttributes(attrs, R.styleable.UserAvatarImageView)
        mBorderWidth = mTypedArray.getDimension(
            R.styleable.UserAvatarImageView_piv_borderWidth,
            context.dpTpPx(DEFAULT_BORDER_WIDTH)
        )

        mBorderColor = mTypedArray.getColor(
            R.styleable.UserAvatarImageView_piv_borderColor,
            DEFAULT_BORDER_COLOR)
        mPremiumColor = mTypedArray.getColor(
            R.styleable.UserAvatarImageView_piv_premiumColor,
            DEFAULT_BORDER_COLOR)
        mInitials = mTypedArray.getString(R.styleable.UserAvatarImageView_piv_initial) ?: "??"
        isAnimated = mTypedArray.getBoolean(R.styleable.UserAvatarImageView_piv_isAnimated, true)
        mValueOfIncrease = mTypedArray.getInt(R.styleable.UserAvatarImageView_piv_valueOfIncrease, 10)
        mValueOfIncrease = mTypedArray.getInt(R.styleable.UserAvatarImageView_piv_valueOfIncrease, 10)
        mValueOfSpeedAnimation = mTypedArray.getInt(R.styleable.UserAvatarImageView_piv_valueOfSpeedAnimation, 300)
        hasPremium = mTypedArray.getBoolean(R.styleable.UserAvatarImageView_piv_premium, false)
        scaleType = ScaleType.CENTER_CROP

        setup()
        mTypedArray.recycle()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val initSize = resolveDefaultSize(widthMeasureSpec)
        sizeSource = initSize
        setMeasuredDimension(max(initSize, size), max(initSize, size))
    }

    private fun resolveDefaultSize(spec: Int) : Int {
        return when(MeasureSpec.getMode(spec)) {
            MeasureSpec.UNSPECIFIED -> context.dpTpPx(DEFAULT_SIZE).toInt()
            MeasureSpec.AT_MOST -> MeasureSpec.getSize(spec)
            MeasureSpec.EXACTLY -> MeasureSpec.getSize(spec)
            else -> MeasureSpec.getSize(spec)
        }
    }

    fun setInitials(letters: String){
        mInitials = letters
    }

    fun setValueOfIncrease(value: Int){
        mValueOfIncrease = value
    }

    fun setValueOfSpeedAnimation(value: Int) {
        mValueOfSpeedAnimation = value
    }

    fun enableAnimation(isEnable: Boolean){
        isAnimated = isEnable
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w == 0) {
            return
        }
        with(mViewRect) {
            left = 0
            top = 0
            right = w
            bottom = h
        }

        with(mPremiumViewRect) {
            left = (0+mBorderWidth).toInt()
            top = (0+mBorderWidth).toInt()
            right = (w-mBorderWidth).toInt()
            bottom = (h-mBorderWidth).toInt()
        }
        prepareShaders(w, h)
    }

    private fun setup() {
        with(premiumBorder) {
            strokeWidth = mBorderWidth
            color = mPremiumColor
            style = Paint.Style.STROKE
        }

        with(contentBorder) {
            strokeWidth = mBorderWidth
            color = mBorderColor
            style = Paint.Style.STROKE
        }

        half = (mBorderWidth / 2).toInt()

        setOnClickListener {
            handleLongClick()
        }
    }

    private fun  prepareShaders(w: Int, h: Int) {
        if (w == 0) return
        if (drawable != null) {
            srcBm = drawable.toBitmap(w, h, Bitmap.Config.ARGB_8888)
            avatarPaint.shader = BitmapShader(srcBm, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)
        }

        mBorderRect.set(mViewRect)
        mBorderRect.inset(half, half)
        mPremiumRect.set(mPremiumViewRect)
        mPremiumRect.inset(half, half)
    }

    override fun setImageBitmap(bm: Bitmap?) {
        super.setImageBitmap(bm)
        if (isAvatarMode) prepareShaders(width, height)
    }

    override fun setImageDrawable(drawable: Drawable?) {
        super.setImageDrawable(drawable)
        if (isAvatarMode) prepareShaders(width, height)
    }

    override fun setImageResource(resId: Int) {
        super.setImageResource(resId)
        if (isAvatarMode) prepareShaders(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        if (drawable != null && isAvatarMode){
            drawAvatar(canvas)
        } else {
            drawInitials(canvas)
        }

        if (hasPremium){
            canvas.drawOval(mPremiumRect.toRectF(), contentBorder)
        }
        canvas.drawOval(mBorderRect.toRectF(), premiumBorder)
    }

    private fun drawAvatar(canvas: Canvas) {
        canvas.drawOval(mViewRect.toRectF(), avatarPaint)
    }

    private fun drawInitials(canvas: Canvas) {
        initialPaint.color = initialsToColor(mInitials)
        canvas.drawOval(mViewRect.toRectF(), initialPaint)
        val d = height / mInitials.length * 0.001

        Log.e(TAG, d.toString())
        with(initialPaint){
            color = Color.WHITE
            textAlign = Paint.Align.CENTER
            textSize = (height * d).toFloat()
        }

        val offset = (initialPaint.descent() + initialPaint.ascent()) / 2
        canvas.drawText(mInitials, mViewRect.exactCenterX(), mViewRect.exactCenterY() - offset, initialPaint)
    }

    private fun initialsToColor(charsInitials: String): Int {
        if (charsInitials.isNotEmpty()){
            val b = charsInitials[0].toByte()
            val length = arrayColors.size
            val d = b/length.toDouble()
            val index = ((d-truncate(d)) * length).toInt()
            return arrayColors[index]
        }

        return arrayColors[0]
    }

    private fun handleLongClick() : Boolean {
        if (isAnimated) {
            animateClick()
        } else {
            isAvatarMode = !isAvatarMode
            invalidate()
        }
        return true
    }

    private fun animateClick() {
//        if (sizeSource <  height) return
//
//        val mValueAnimator = ValueAnimator.ofInt(width, width + mValueOfIncrease).apply {
//            duration = mValueOfSpeedAnimation.toLong()
//            interpolator = LinearInterpolator()
//            repeatMode = ValueAnimator.REVERSE
//            repeatCount = 1
//        }
//
//        mValueAnimator.addUpdateListener {
//            size = it.animatedValue as Int
//            requestLayout()
//        }
//
//        mValueAnimator.doOnRepeat {
//            isAvatarMode = !isAvatarMode
//            invalidate()
//        }
//        mValueAnimator.start()
//        mValueAnimator.addListener { animator ->
//            if (!animator.isRunning) {
//                mValueAnimator.start()
//            }
//        }
    }

    override fun onSaveInstanceState(): Parcelable? {
        val savedState = SavedState(super.onSaveInstanceState())
        savedState.mIsAvatarMode = isAvatarMode
        savedState.mBorderWidth = mBorderWidth
        savedState.mBorderColor = mBorderColor
        savedState.mPremiumColor = mPremiumColor
        savedState.mValueSpeedAnimation = mValueOfSpeedAnimation
        return savedState
    }

    override fun onRestoreInstanceState(state: Parcelable?) {
        if (state is SavedState) {
            super.onRestoreInstanceState(state)
            isAvatarMode = state.mIsAvatarMode
            mBorderWidth = state.mBorderWidth
            mBorderColor = state.mBorderColor
            mPremiumColor = state.mBorderColor
            isAnimated = state.isAnimated
            mValueOfIncrease = state.mValueOfIncrease
            mValueOfSpeedAnimation = state.mValueSpeedAnimation

            with(premiumBorder){
                color = mPremiumColor
                strokeWidth = mBorderWidth
            }
            with(contentBorder){
                color = mBorderColor
                strokeWidth = mBorderWidth
            }

        } else {
            super.onRestoreInstanceState(state)
        }
    }

    class SavedState : BaseSavedState, Parcelable {
        var mIsAvatarMode = true
        var mBorderWidth = 0f
        var mBorderColor = 0
        var mPremiumColor = 0
        var isAnimated = true
        var mValueOfIncrease = 10
        var mValueSpeedAnimation = 300

        constructor(superState: Parcelable?) : super(superState)

        constructor(parcel: Parcel) : super(parcel) {
            //read state
            mIsAvatarMode = parcel.readInt() == 1
            mBorderWidth = parcel.readFloat()
            mBorderColor = parcel.readInt()
            mPremiumColor = parcel.readInt()
            isAnimated = parcel.readInt() == 1
            mValueOfIncrease = parcel.readInt()
            mValueSpeedAnimation = parcel.readInt()
        }

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            //save state
            super.writeToParcel(parcel, flags)
            parcel.writeInt(if (mIsAvatarMode) 1 else 0)
            parcel.writeFloat(mBorderWidth)
            parcel.writeInt(mBorderColor)
            parcel.writeInt(mPremiumColor)
            parcel.writeInt(if (isAnimated) 1 else 0)
            parcel.writeInt(mValueOfIncrease)
            parcel.writeInt(mValueSpeedAnimation)

        }

        override fun describeContents() = 0

        companion object Creator : Parcelable.Creator<SavedState>{
            override fun createFromParcel(source: Parcel) = SavedState(source)
            override fun newArray(size: Int): Array<SavedState?> = arrayOfNulls(size)
        }
    }
}