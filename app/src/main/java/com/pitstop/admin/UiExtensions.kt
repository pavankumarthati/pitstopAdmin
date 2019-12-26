package com.carserviceapp.ext

import android.text.Editable
import android.text.TextWatcher
import androidx.viewpager.widget.ViewPager

fun ViewPager.addOnPageChanged(_onPageScrollStateChanged: ((Int) -> Unit)? = null,
                               _onPageScrolled: ((position: Int, positionOffset: Float, positionOffsetPixels: Int) -> Unit)? = null,
                               _onPageSelected: ((position: Int) -> Unit)? = null) {
    addOnPageChangeListener(object: ViewPager.OnPageChangeListener {
        override fun onPageScrollStateChanged(state: Int) {
            _onPageScrollStateChanged?.invoke(state)
        }

        override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
            _onPageScrolled?.invoke(position, positionOffset, positionOffsetPixels)
        }

        override fun onPageSelected(position: Int) {
            _onPageSelected?.invoke(position)
        }

    })

}

open class SimpleTextWatcher: TextWatcher {
    override fun afterTextChanged(s: Editable?) {

    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

    }

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

    }


}

