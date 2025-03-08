package com.jintao.vipmanager.activity

import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.os.Message
import com.gyf.immersionbar.BarHide
import com.gyf.immersionbar.ImmersionBar
import com.jintao.vipmanager.base.BaseActivity
import com.jintao.vipmanager.databinding.ActivitySplashBinding
import com.jintao.vipmanager.utils.LoginManager

class SplashActivity : BaseActivity<ActivitySplashBinding>() {

    private var MESSAGE_CODE = 0
    private val HANDLE_MESSAGE_LOGIN = 11
    private val HANDLE_MESSAGE_HOME = 22

    private val mHandler = Handler(Looper.getMainLooper(),object : Handler.Callback{
        override fun handleMessage(msg: Message): Boolean {
            when (msg.what) {
                HANDLE_MESSAGE_LOGIN -> {
                    val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                HANDLE_MESSAGE_HOME -> {
                    val intent = Intent(this@SplashActivity, HomeActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
            return false
        }
    })

    override fun initData() {
        val loginInfo = LoginManager.getInstence().getLoginInfo()
        if (loginInfo.isLogin) {
            MESSAGE_CODE = HANDLE_MESSAGE_HOME
            mHandler.sendEmptyMessageDelayed(HANDLE_MESSAGE_HOME,1000)
        }else {
            MESSAGE_CODE = HANDLE_MESSAGE_LOGIN
            mHandler.sendEmptyMessageDelayed(HANDLE_MESSAGE_LOGIN,1000)
        }
    }

    override fun initListener() {

    }

    override fun initObserve() {

    }

    override fun onDestroy() {
        super.onDestroy()
        if (MESSAGE_CODE != 0) {
            mHandler.removeMessages(MESSAGE_CODE)
        }
    }

    override fun getViewBinding(): ActivitySplashBinding {
        return ActivitySplashBinding.inflate(layoutInflater)
    }

    override fun setStatusBarConfig(): ImmersionBar {
        return ImmersionBar.with(this)
            .transparentStatusBar()
            .hideBar(BarHide.FLAG_HIDE_NAVIGATION_BAR)
    }
}