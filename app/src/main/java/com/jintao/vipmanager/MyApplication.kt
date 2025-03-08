package com.jintao.vipmanager

import android.app.Application
import android.content.Context
import android.graphics.Color
import com.hjq.http.EasyConfig
import com.hjq.toast.ToastUtils
import com.jintao.vipmanager.bean.UserOperateRecordInfo
import com.jintao.vipmanager.network.HttpServerUrl
import com.jintao.vipmanager.network.RequestHandler
import com.lxj.xpopup.XPopup
import com.tencent.mmkv.MMKV
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

/**
 * Author: jintao
 * CreateDate: 2023/8/23 16:29
 * Description:
 */
class MyApplication:Application() {

    companion object {
        lateinit var mContext: Context
        var todayConsume = 0f
        var todayCount = 0
        var cacheHomeTime = 0L
        var uploadBackupCount = 0
        var cacheOperateRecordList = mutableListOf<UserOperateRecordInfo>()
    }

    override fun onCreate() {
        super.onCreate()
        mContext = this
        ToastUtils.init(this)
        MMKV.initialize(this)
        XPopup.setShadowBgColor(Color.parseColor("#66000000"))

        initNetWork()
    }

    private fun initNetWork() {
        val httpServer = HttpServerUrl()
        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(6, TimeUnit.SECONDS)
            .writeTimeout(6, TimeUnit.SECONDS)
            .connectTimeout(6, TimeUnit.SECONDS)
            .build()

        EasyConfig.with(okHttpClient)
            .setLogEnabled(true)
            .setServer(httpServer)
            .setHandler(RequestHandler())
            .into()
    }
}