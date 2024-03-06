package com.jintao.vipmanager.dialog

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.widget.ImageView
import com.lxj.xpopup.core.CenterPopupView
import android.view.animation.LinearInterpolator
import android.widget.TextView
import com.jintao.vipmanager.R

class LoadingDialog(mContext: Context,var text:String):CenterPopupView(mContext) {

    override fun getImplLayoutId(): Int {
        return R.layout.didlog_loading
    }

    override fun onCreate() {
        super.onCreate()
        setBackgroundColor(Color.TRANSPARENT)
        val mIvLoading = findViewById<ImageView>(R.id.iv_loading)
        findViewById<TextView>(R.id.tv_loading_text).setText(text)
        val animator3 = ObjectAnimator.ofFloat(mIvLoading, "rotation", 0f, 360f)
        animator3.interpolator = LinearInterpolator()
        animator3.repeatCount = ValueAnimator.INFINITE
        animator3.duration = 1300
        animator3.start()
    }
}