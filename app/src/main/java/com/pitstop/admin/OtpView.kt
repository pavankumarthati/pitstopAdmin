package com.pitstop.admin


import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Typeface
import android.text.Editable
import android.text.InputFilter
import android.text.InputType
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import io.reactivex.Observable
import com.pitstop.admin.R
import io.reactivex.subjects.PublishSubject
import kotlin.math.roundToInt


private fun AppCompatEditText.onTextChanged(childViews: Array<AppCompatEditText>, publishSubject: PublishSubject<String>) {
    setOnKeyListener { v, keyCode, event ->
        if (keyCode == KeyEvent.KEYCODE_DEL && text?.length == 0 && event.action == KeyEvent.ACTION_DOWN) {
            val index = v?.tag as? Int
            if (index != null && index > 0) {
                childViews[index - 1].requestFocus()
                childViews[index - 1].setText("")
                return@setOnKeyListener true
            }
        }
        return@setOnKeyListener false
    }

    addTextChangedListener(object: TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            println("afterTextChanged called with ${s.toString()}")

            if (s?.length == 1) {
                val index = tag as Int
                if (index < childViews.size - 1) {
                    childViews[index + 1].requestFocus()
                }
            }
            publishSubject.onNext(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }

    })
}

class OtpView @kotlin.jvm.JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0, defStyleRes: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr, defStyleRes) {

    private var ems : Int = EMS
    private var familySize : Int = FAMILY_SIZE
    private var childBackground : Int? = null
    private var marginAround = resources.getDimension(R.dimen.otp_view_margin)
    private var marginBetween = resources.getDimension(R.dimen.otp_view_margin)
    private var childTextStyle = R.style.TextAppearance_OtpView
    private var childTypeFace: Typeface? = ResourcesCompat.getFont(context, R.font.proxima_nova_regular)
    private var childTextSize : Float = 0F
    private var childInputType : String? = null
    private val textStream = PublishSubject.create<String>()

    private lateinit var childEditTexts : Array<AppCompatEditText>
    init {
        setupAttrs(context, attrs)
        init()
    }

    @SuppressLint("CheckResult")
    private fun init() {
        childEditTexts = Array(familySize, ::initChild)
        childEditTexts.forEach {
            it.onTextChanged(childEditTexts, textStream)
        }

    }

    private fun initChild(index: Int) : AppCompatEditText {
        val child = LayoutInflater.from(context).inflate(R.layout.otp_digit_view, this, false) as AppCompatEditText

        child.tag = index
        child.inputType = when(childInputType) {
            "phone" -> InputType.TYPE_CLASS_PHONE
            else -> InputType.TYPE_CLASS_TEXT
        }
        child.gravity = Gravity.CENTER_HORIZONTAL.or(Gravity.BOTTOM)

        child.filters = arrayOf(InputFilter.LengthFilter(ems))

        val layoutParams = child.layoutParams as LinearLayout.LayoutParams
        if (orientation == LinearLayout.VERTICAL) {
            if (marginAround != 0F) {
                layoutParams.topMargin = marginAround.roundToInt()
                if (index == familySize - 1) { // last child
                    layoutParams.bottomMargin = marginAround.roundToInt()
                }
            } else {
                if (index != 0) {
                    layoutParams.topMargin = marginBetween.roundToInt()
                }
            }
        } else {
            if (marginAround != 0F) {
                layoutParams.marginStart = marginAround.roundToInt()
                if (index == familySize - 1) { // last child
                    layoutParams.marginEnd = marginAround.roundToInt()
                }
            } else {
                if (index != 0) {
                    layoutParams.marginStart = marginBetween.roundToInt()
                }
            }
        }
        addView(child, index, layoutParams)
        return child
    }

    private fun setupAttrs(context: Context, attrs: AttributeSet?) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.OtpView, 0, 0)

        ems = typedArray.getInt(R.styleable.OtpView_ems, EMS)
        familySize = typedArray.getInt(R.styleable.OtpView_familySize, FAMILY_SIZE)
        childBackground = typedArray.getColor(R.styleable.OtpView_childBackground, 0)
        marginAround = typedArray.getDimension(R.styleable.OtpView_childMarginAround, marginAround)
        marginBetween = typedArray.getDimension(R.styleable.OtpView_childMarginBetween, marginBetween)
        childTextStyle = typedArray.getResourceId(R.styleable.OtpView_childTextStyle, childTextStyle)
        childInputType = typedArray.getNonResourceString(R.styleable.OtpView_childInputType)

        val editTextTypedArray = context.obtainStyledAttributes(childTextStyle, R.styleable.TextAppearance)
        if (editTextTypedArray.hasValue(R.styleable.TextAppearance_android_fontFamily)) {
            val fontId = editTextTypedArray.getResourceId(R.styleable.TextAppearance_android_fontFamily, -1)
            childTypeFace = ResourcesCompat.getFont(context, fontId) ?: childTypeFace
        }
        childTextSize = editTextTypedArray.getDimension(R.styleable.TextAppearance_android_textSize, 0F)

        editTextTypedArray.recycle()

        typedArray.recycle()
    }

    fun getText(): String {
        val resultBuilder = StringBuilder()
        resultBuilder.apply {
            childEditTexts.forEach {
                this.append(it.text)
            }
        }
        return resultBuilder.toString()
    }

    fun getFamilySize(): Int {
        return familySize
    }

    fun setText(text: String?) {
        if (text.isNullOrEmpty()) {
            childEditTexts.forEach {
                it.setText("")
            }
        } else {
            text.forEachIndexed { index, c ->
                childEditTexts[index].setText(c.toString())
            }
        }
    }

    fun isValid(): Boolean {
        val resultBuilder = StringBuilder()
        resultBuilder.apply {
            childEditTexts.forEach {
                this.append(it.text)
            }
        }
        return resultBuilder.toString().length == familySize
    }

    fun getTextStream(): Observable<String> {
        return textStream
    }

    companion object {
        private const val EMS = 1
        private const val FAMILY_SIZE = 10
    }

}