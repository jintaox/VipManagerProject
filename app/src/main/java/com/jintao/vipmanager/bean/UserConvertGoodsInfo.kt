package com.jintao.vipmanager.bean

/**
 * Author: zhanghui
 * CreateDate: 2025/1/24 17:20
 * Description:
 */
class UserConvertGoodsInfo {

    private var userName = ""
    private var phoneNumber = ""
    private var useIntegral = 0
    private var desc = ""
    private var time = ""

    fun setUserName(userName:String) {
        this.userName = userName
    }

    fun getUserName():String {
        return userName
    }

    fun setPhoneNumber(phoneNumber:String) {
        this.phoneNumber = phoneNumber
    }

    fun getPhoneNumber():String {
        return phoneNumber
    }

    fun setUseIntegral(useIntegral:Int) {
        this.useIntegral = useIntegral
    }

    fun getUseIntegral():Int {
        return useIntegral
    }

    fun setDesc(desc:String) {
        this.desc = desc
    }

    fun getDesc():String {
        return desc
    }

    fun setTime(time:String) {
        this.time = time
    }

    fun getTime():String {
        return time
    }
}