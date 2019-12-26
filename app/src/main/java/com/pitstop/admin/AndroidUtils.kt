package com.carserviceapp.ext

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.util.DisplayMetrics
import android.util.TypedValue
import androidx.annotation.AttrRes
import androidx.annotation.ColorRes
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.pitstop.admin.CustomTypefaceSpan
import com.pitstop.admin.R
import java.text.SimpleDateFormat
import java.util.*


fun getWidth(activity: Activity): Int {
    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    return displayMetrics.widthPixels
}

fun getHeight(activity: Activity, vararg subtractions: Int): Int {
    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)
    var height = displayMetrics.heightPixels
    subtractions.forEach {
       height -= it
    }
    return height
}

fun getActionBarHeightFromTheme(context: Context): Int {
    val tv = TypedValue()
    context.theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)
    return context.resources.getDimensionPixelSize(tv.resourceId)
}

fun Activity.setStatusBarColor(@ColorRes colorRes: Int) {
    window.statusBarColor = resources.getColor(colorRes)
}

fun Activity.getThemedResId(@AttrRes attr: Int): Int {
    val a: TypedArray = theme.obtainStyledAttributes(intArrayOf(attr))
    val resId = a.getResourceId(0, 0)
    a.recycle()
    return resId
}


fun longToDateFormat(epoch: Long): String {
    val date = Date(epoch)
    val df2 = SimpleDateFormat("dd-MMM-yy HH:MM a")
    return df2.format(date)
}

fun vehicleRegistrationNumberFormat(regNo: String): String {
    return regNo.substring(0, 2) + " " + regNo.substring(2, 4) + " " +
            regNo.substring(4, 6) + " " + regNo.substring(6)
}


fun wordCapitalize(input: String?) : String? {
    val strBuilder = StringBuilder()
    val words = input?.split(" ")
    words?.forEach {
        strBuilder.append(it.toLowerCase().capitalize())
    }
    return strBuilder.toString()
}

class BaseViewModelFactory<T>(val creator: () ->T): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return creator() as T
    }

}

inline fun <reified T: ViewModel> Fragment.getViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null) {
        ViewModelProviders.of(this).get(T::class.java)
    } else {
        ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
    }
}

inline fun <reified T: ViewModel> FragmentActivity.getViewModel(noinline creator: (() -> T)? = null): T {
    return if (creator == null) {
        ViewModelProviders.of(this).get(T::class.java)
    } else {
        ViewModelProviders.of(this, BaseViewModelFactory(creator)).get(T::class.java)
    }
}


fun Context.formatPhoneVerifySubLabel(text: String, fontId: Int, _relativeSize: Float? = null, @ColorRes _textColor: Int = R.color.black): SpannableString {
    val font = ResourcesCompat.getFont(this, fontId)
    val spannableSalePriceString = SpannableString(text)

    var relativeSize = _relativeSize
    if (relativeSize == null) {
        val typedValue = TypedValue()
        resources.getValue(R.integer.mobile_text_rel_size, typedValue, true)
        relativeSize = typedValue.float
    }

    spannableSalePriceString.setSpan(
        RelativeSizeSpan(relativeSize),
        spannableSalePriceString.indexOf('+'),
        spannableSalePriceString.indexOf(','),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    val textColor = ResourcesCompat.getColor(resources, _textColor, null)

    spannableSalePriceString.setSpan(
        ForegroundColorSpan(textColor),
        spannableSalePriceString.indexOf('+'),
        spannableSalePriceString.indexOf(','),
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    spannableSalePriceString.setSpan(
        CustomTypefaceSpan("", font!!),spannableSalePriceString.indexOf('+'),
        spannableSalePriceString.indexOf(',') - 1,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    return spannableSalePriceString
}

fun decodeSampledBitmapFromResource(
    res: Resources,
    resId: Int,
    reqWidth: Int,
    reqHeight: Int
): Bitmap {
    // First decode with inJustDecodeBounds=true to check dimensions
    return BitmapFactory.Options().run {
        inJustDecodeBounds = true
        BitmapFactory.decodeResource(res, resId, this)

        // Calculate inSampleSize
        inSampleSize = calculateInSampleSize(this, reqWidth, reqHeight)

        // Decode bitmap with inSampleSize set
        inJustDecodeBounds = false

        BitmapFactory.decodeResource(res, resId, this)
    }
}

fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
    // Raw height and width of image
    val (height: Int, width: Int) = options.run { outHeight to outWidth }
    var inSampleSize = 1

    if (height > reqHeight || width > reqWidth) {

        val halfHeight: Int = height / 2
        val halfWidth: Int = width / 2

        // Calculate the largest inSampleSize value that is a power of 2 and keeps both
        // height and width larger than the requested height and width.
        while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
            inSampleSize *= 2
        }
    }

    return inSampleSize
}