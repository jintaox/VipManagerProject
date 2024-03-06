package com.jintao.vipmanager.listener

import com.jintao.vipmanager.database.bean.DbVipUserInfo

interface OnVipItemClickListener {
    fun onItemClick(userInfo: DbVipUserInfo, type:Int)
}