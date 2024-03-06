package com.jintao.vipmanager.utils

import android.text.TextUtils
import com.hjq.gson.factory.GsonFactory
import com.jintao.vipmanager.bean.UserInfo
import java.io.File

class LoginManager private constructor() {

    companion object {
        private var mInstance : LoginManager? = null
        @Synchronized
        fun getInstence(): LoginManager {
            if (mInstance ==null) {
                mInstance = LoginManager()
            }
            return mInstance!!
        }
    }

    fun saveLoginInfo(userInfo: UserInfo) {
        val contentValue = GsonFactory.getSingletonGson().toJson(userInfo)
        MmkvUtil.putString(AppConstant.LOGIN_USER_INFO, contentValue)
        StreamUtils.writeFile(StreamUtils.getSdcardUserInfoPath(),contentValue)
    }

    fun getLoginInfo(): UserInfo {
        val contentValue = MmkvUtil.getString(AppConstant.LOGIN_USER_INFO,"")
        if (!TextUtils.isEmpty(contentValue)) {
            val loginInfo =
                GsonFactory.getSingletonGson().fromJson(contentValue, UserInfo::class.java)
            return loginInfo
        }else {
            val file = File(StreamUtils.getSdcardUserInfoPath())
            if (file!=null&&file.exists()) {
                val readerContent = StreamUtils.readerFile(StreamUtils.getSdcardUserInfoPath())
                if (!TextUtils.isEmpty(readerContent)) {
                    val loginInfo =
                        GsonFactory.getSingletonGson().fromJson(readerContent, UserInfo::class.java)
                    MmkvUtil.putString(AppConstant.LOGIN_USER_INFO, readerContent)
                    return loginInfo
                }else {
                    return UserInfo()
                }
            }else {
                return UserInfo()
            }
        }
    }
}