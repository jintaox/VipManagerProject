package com.jintao.vipmanager.database.bean

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Author: jintao
 * CreateDate: 2023/8/23 17:23
 * Description:
 */
@Entity(tableName = "consume_record_info")
class DbUserConsumeInfo {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "_id")
    private var id:Long? = 0L

    @ColumnInfo(name = "uid")
    private var uid:Int = 0

    @ColumnInfo(name = "content")
    private var content:String? = ""

    @ColumnInfo(name = "image_path")
    private var imagePath:String? = ""

    @ColumnInfo(name = "consume_time")
    private var consumeTime:String? = ""

    @ColumnInfo(name = "consume_amount")
    private var consumeAmount:Float = 0f

    @ColumnInfo(name = "consume_integral")
    private var consumeIntegral:Float = 0f

    @ColumnInfo(name = "conver_ratio")
    private var converRatio:String? = "1"

    @ColumnInfo(name = "consume_type")
    private var consumeType:Int = 0

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

    fun getImagePath():String {
        if(this.imagePath==null) {
            return ""
        }else {
            return this.imagePath!!
        }
    }

    fun setImagePath(imagePath: String) {
        this.imagePath = imagePath;
    }

    fun getConsumeTime():String {
        if(this.consumeTime==null) {
            return ""
        }else {
            return this.consumeTime!!
        }
    }

    fun setConsumeTime(consumeTime: String) {
        this.consumeTime = consumeTime
    }

    fun getConsumeIntegral():Float {
        return this.consumeIntegral
    }

    fun setConsumeIntegral(consumeIntegral:Float) {
        this.consumeIntegral = consumeIntegral
    }

    fun getConsumeType():Int {
        return this.consumeType
    }

    fun setConsumeType(consumeType:Int) {
        this.consumeType = consumeType
    }

    fun getContent(): String {
        if(this.content==null) {
            return ""
        }else {
            return this.content!!
        }
    }

    fun setContent(content: String) {
        this.content = content
    }

    fun getConsumeAmount():Float {
        return consumeAmount
    }

    fun setConsumeAmount(consumeAmount:Float) {
        this.consumeAmount = consumeAmount
    }

    fun getConverRatio(): String {
        if(this.converRatio==null) {
            return ""
        }else {
            return this.converRatio!!
        }
    }

    fun setConverRatio(converRatio: String) {
        this.converRatio = converRatio
    }
}
