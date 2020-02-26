package com.balladie.soundtherapy.view

import android.app.ActionBar
import android.os.Build
import android.view.View
import android.view.Window
import android.view.WindowManager

abstract class ThrottledOnClickListener(private val throttleInMillis: Long = 500L) :
    View.OnClickListener {
    private var lastClickedAt = 0L

    override fun onClick(v: View) {
        System.currentTimeMillis().let { currentMillis ->
            if (currentMillis - lastClickedAt < throttleInMillis) return

            lastClickedAt = currentMillis
            onThrottledClick(v)
        }
    }

    abstract fun onThrottledClick(v: View)
}

fun View.setThrottledOnClickListener(
    throttleInMillis: Long = 500L,
    clickAction: (View) -> Unit
) {
    setOnClickListener(object: ThrottledOnClickListener(throttleInMillis) {
        override fun onThrottledClick(v: View) = clickAction(v)
    })
}

fun setWindowFullScreen(window: Window, actionBar: ActionBar?) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        window.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
    }
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_FULLSCREEN
        actionBar?.hide()
    }
}