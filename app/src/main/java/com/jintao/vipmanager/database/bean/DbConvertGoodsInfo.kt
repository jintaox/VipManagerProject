package com.jintao.vipmanager.database.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Author: jintao
 * CreateDate: 2024/1/3 16:45
 * Description:兑换记录表
 */
@Entity(tableName = "exchange_goods_info")
class DbConvertGoodsInfo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private var id:Long? = 0L

    @ColumnInfo(name = "uid")
    private var uid:Int = 0

    @ColumnInfo(name = "goods_name")
    private var goodsName:String? = ""

    @ColumnInfo(name = "use_integral")
    private var useIntegral:Int = 0

    @ColumnInfo(name = "desc")
    private var desc:String? = ""

    @ColumnInfo(name = "time")
    private var time:String? = ""

    fun getId():Long {
        return id!!
    }

    fun setId(id:Long) {
        this.id = id
    }

    fun getUid():Int {
        return uid
    }

    fun setUid(uid:Int) {
        this.uid = uid
    }

    fun getGoodsName():String {
        if(this.goodsName==null) {
            return ""
        }else {
            return this.goodsName!!
        }
    }

    fun setGoodsName(goodsName:String) {
        this.goodsName = goodsName;
    }

    fun getDesc():String {
        if(this.desc==null) {
            return ""
        }else {
            return this.desc!!
        }
    }

    fun setDesc(desc:String) {
        this.desc = desc
    }

    fun getUseIntegral():Int {
        return useIntegral
    }

    fun setUseIntegral(useIntegral:Int) {
        this.useIntegral = useIntegral;
    }

    fun getTime():String {
        if(this.time==null) {
            return ""
        }else {
            return this.time!!
        }
    }

    fun setTime(time:String) {
        this.time = time
    }
}
