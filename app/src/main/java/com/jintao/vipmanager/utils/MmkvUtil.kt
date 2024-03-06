package com.jintao.vipmanager.utils

import com.tencent.mmkv.MMKV

object MmkvUtil {

    private var mmkv:MMKV? = null

    private fun getMmkvInstance() {
        if (mmkv==null) {
            mmkv = MMKV.defaultMMKV()
        }
    }

    fun putString(key:String,value:String) {
        getMmkvInstance()
        mmkv!!.encode(key,value)
    }

    fun getString(key: String,defValue: String):String {
        getMmkvInstance()
        val decodeString = mmkv!!.decodeString(key, defValue)
        if (decodeString!=null) {
            return decodeString
        }
        return defValue
    }

    fun putBooble(key:String,value:Boolean) {
        getMmkvInstance()
        mmkv!!.encode(key,value)
    }

    fun getBooble(key: String,defValue: Boolean):Boolean {
        getMmkvInstance()
        return mmkv!!.decodeBool(key,defValue)
    }

    fun putInt(key:String,value:Int) {
        getMmkvInstance()
        mmkv!!.encode(key,value)
    }

    fun getInt(key: String,defValue: Int):Int {
        getMmkvInstance()
        return mmkv!!.decodeInt(key,defValue)
    }

    fun putLong(key:String,value:Long) {
        getMmkvInstance()
        mmkv!!.encode(key,value)
    }

    fun getLong(key: String,defValue: Long):Long {
        getMmkvInstance()
        return mmkv!!.decodeLong(key,defValue)
    }
}