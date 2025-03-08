package com.jintao.vipmanager.database.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Author: jintao
 * CreateDate: 2023/7/14 23:08
 * Description:兑换记录，兑换时间，消费记录，消费积分，剩余积分
 */
@Entity(tableName = "vip_user_info")
class DbVipUserInfo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private var id:Long? = 0L

    @ColumnInfo(name = "user_name")
    private var userName:String? = null

    @ColumnInfo(name = "user_sex")
    private var userSex:String? = null

    @ColumnInfo(name = "phone_number")
    private var phoneNumber:String? = null

    @ColumnInfo(name = "uid")
    private var uid:Int = 0

    //消费次数
    @ColumnInfo(name = "consume_number")
    private var consumeNumber:Int = 0

    //兑换次数
    @ColumnInfo(name = "exchange_number")
    private var exchangeNumber:Float = 0f

    //总积分
    @ColumnInfo(name = "total_integral")
    private var totalIntegral:Float = 0f

    //可用积分
    @ColumnInfo(name = "user_integral")
    private var userIntegral:Float = 0f

    //总消费金额
    @ColumnInfo(name = "total_amount")
    private var totalAmount:Float = 0f

    //扣除消费积分后，剩余积分对应的消费金额
    @ColumnInfo(name = "current_amount")
    private var currentAmount:Float = 0f

    //最近消费时间
    @ColumnInfo(name = "consume_time")
    private var consumeTime:Long = 0L

    @ColumnInfo(name = "register_time")
    private var registerTime:String? = null

    fun getId():Long {
        return this.id!!
    }

    fun setId(id:Long) {
        this.id = id
    }

    fun getUserName():String {
        if(this.userName==null) {
            return ""
        }else {
            return this.userName!!
        }
    }

    fun setUserName(userName:String) {
        this.userName = userName
    }

    fun getPhoneNumber():String {
        if(this.phoneNumber==null) {
            return ""
        }else {
            return this.phoneNumber!!
        }
    }

    fun setPhoneNumber(phoneNumber:String) {
        this.phoneNumber = phoneNumber
    }

    fun getUserIntegral():Float {
        return this.userIntegral
    }

    fun setUserIntegral(userIntegral:Float) {
        this.userIntegral = userIntegral
    }

    fun getUid():Int {
        return this.uid
    }

    fun setUid(uid:Int) {
        this.uid = uid
    }

    fun getUserSex():String {
        if(this.userSex==null) {
            return ""
        }else {
            return this.userSex!!
        }
    }

    fun setUserSex(userSex:String) {
        this.userSex = userSex
    }

    fun getConsumeNumber():Int {
        return consumeNumber
    }

    fun setConsumeNumber(consumeNumber:Int) {
        this.consumeNumber = consumeNumber
    }

    fun getExchangeNumber():Float {
        return exchangeNumber
    }

    fun setExchangeNumber(exchangeNumber:Float) {
        this.exchangeNumber = exchangeNumber
    }

    fun getTotalIntegral():Float {
        return totalIntegral
    }

    fun setTotalIntegral(totalIntegral:Float) {
        this.totalIntegral = totalIntegral
    }

    fun getTotalAmount():Float {
        return totalAmount
    }

    fun setTotalAmount(totalAmount:Float) {
        this.totalAmount = totalAmount
    }

    fun getCurrentAmount():Float {
        return currentAmount
    }

    fun setCurrentAmount(currentAmount:Float) {
        this.currentAmount = currentAmount
    }

    fun getConsumeTime():Long {
        return consumeTime
    }

    fun setConsumeTime(consumeTime:Long) {
        this.consumeTime = consumeTime
    }

    fun getRegisterTime():String {
        if(this.registerTime==null) {
            return ""
        }else {
            return this.registerTime!!
        }
    }

    fun setRegisterTime(registerTime:String) {
        this.registerTime = registerTime
    }
}