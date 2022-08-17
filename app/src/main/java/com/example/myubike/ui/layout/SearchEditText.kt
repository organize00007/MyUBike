package com.example.myubike.ui.layout

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.content.res.ResourcesCompat.getDrawable
import com.example.myubike.R
import com.example.myubike.ui.UBikeFragment

@SuppressLint("AppCompatCustomView")
class SearchEditText(context: Context, attrs: AttributeSet) : EditText(context, attrs) {
    var uBikeFragment: UBikeFragment? = null
    private val iconSearch = getDrawable(resources, R.drawable.ic_search, null)
        .apply {
            this?.setBounds(0, 0, this.intrinsicWidth, this.intrinsicHeight)
        }
    private val iconLocate = getDrawable(resources, R.drawable.ic_locate_2, null)
        .apply {
            this?.setBounds(0, 0, this.intrinsicWidth, this.intrinsicHeight)
        }

    init {
        this.background = ContextCompat.getDrawable(context, R.drawable.shape_edit_text)
        setIcon()
    }

    private fun setIcon() {
        setCompoundDrawables(iconSearch, null, iconLocate, null)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_UP) {
            val x = event.x
            val y = event.y
            val isInnerWidth = x > width - totalPaddingRight && x < width - paddingRight
            val rect = iconLocate!!.bounds
            val rectHeight = rect.height()
            val distance = (height - rectHeight) / 2
            val isInnerHeight = (y > distance) && (y < (distance + rectHeight))
            if (isInnerHeight && isInnerWidth) {
                uBikeFragment?.moveToMyLocation()
            }
        }
        return super.onTouchEvent(event)
    }
}