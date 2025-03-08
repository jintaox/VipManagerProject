package com.jintao.vipmanager.database

import com.jintao.vipmanager.MyApplication
import com.jintao.vipmanager.bean.UserConvertGoodsInfo
import com.jintao.vipmanager.database.bean.DbConvertGoodsInfo
import com.jintao.vipmanager.database.bean.DbUserConsumeInfo
import com.jintao.vipmanager.database.bean.DbVipUserInfo
import com.jintao.vipmanager.database.dao.DbConvertGoodsInfoDao
import com.jintao.vipmanager.database.dao.DbUserConsumeInfoDao
import com.jintao.vipmanager.database.dao.DbVipUserInfoDao
import com.jintao.vipmanager.utils.GeneralUtils

/**
 * Author: zhanghui
 * CreateDate: 2025/1/23 15:08
 * Description:
 */
class DatabaseRepository {

    private var mVipManagerDb:DatabaseManager
    private var vipUserInfoDao:DbVipUserInfoDao
    private var userConsumeInfoDao:DbUserConsumeInfoDao
    private var convertGoodsInfoDao:DbConvertGoodsInfoDao

    constructor() {
        mVipManagerDb = DatabaseManager.getDatabase(MyApplication.mContext)
        vipUserInfoDao = mVipManagerDb.vipUserInfoDao()
        userConsumeInfoDao = mVipManagerDb.userConsumeInfoDao()
        convertGoodsInfoDao = mVipManagerDb.convertGoodsInfoDao()
    }

    fun getVipUserDao():DbVipUserInfoDao {
        return vipUserInfoDao
    }

    fun getUserConsumeDao():DbUserConsumeInfoDao {
        return userConsumeInfoDao
    }

    fun getConvertGoodsDao():DbConvertGoodsInfoDao {
        return convertGoodsInfoDao
    }

    /**
     * 查询 手机号 是否可用
     */
    suspend fun queryByVipPhoneAvai(phone: String):Boolean {
        var vipUserInfo:DbVipUserInfo = vipUserInfoDao.queryUserInfoFromPhone(phone)
        return vipUserInfo==null
    }

    suspend fun getNewVipUid(inputPhone: String):Int {
        var generateUid: Int
        do {
            generateUid = GeneralUtils.getInstence().generateRandomUid(inputPhone).toInt()
            var vipUserInfo = vipUserInfoDao.queryUserInfoFromUid(generateUid)
        } while (vipUserInfo != null)
        return generateUid
    }

    /**
     * 根据 消费时间 逆序 分页查询 用户列表
     * @param pageSize 一页多少个
     * @param pageNumber 第几页
     * @return
     */
    suspend fun queryPageReverseList(pageSize:Int, pageNumber:Int):List<DbVipUserInfo> {
        var vipUserList:List<DbVipUserInfo> = vipUserInfoDao.queryPageReverseList(pageSize,pageSize * pageNumber)
        return vipUserList
    }

    /**
     * 根据 uid 查询倒数最后几条消费记录
     * @param uid
     * @return
     */
    suspend fun queryByConsumeUidLast(uid:Int, number:Int):List<DbUserConsumeInfo> {
        var userConsumeList:List<DbUserConsumeInfo> = userConsumeInfoDao.queryConsumeByUidLast(uid, number)
        return userConsumeList
    }

    /**
     * 根据 uid 分页查询 消费明细
     */
    suspend fun queryByConsumeUidAll(uid:Int,pageSize:Int, pageNumber:Int):List<DbUserConsumeInfo> {
        var consumeList:List<DbUserConsumeInfo> = userConsumeInfoDao.queryPageReverseList(uid, pageSize,pageSize * pageNumber)
        return consumeList
    }

    /**
     * 根据 uid 和 日期 模糊查询消费明细
     */
    suspend fun queryByConsumeUidDate(uid:Int,date: String):List<DbUserConsumeInfo> {
        var consumeList:List<DbUserConsumeInfo> = userConsumeInfoDao.queryByConsumeUidDate(uid, date+"%")
        return consumeList
    }

    /**
     * 根据日期查询 所有用户 消费记录
     * @param date
     * @return
     */
    suspend fun queryByLikeDate(date: String):List<DbUserConsumeInfo> {
        var consumeList:List<DbUserConsumeInfo> = userConsumeInfoDao.queryByLikeDate(date+"%")
        return consumeList
    }

    /**
     * 根据输入内容模糊查询
     * @param key
     * @return
     */
    suspend fun queryByLikeContent(isNumberic:Boolean, key: String):List<DbVipUserInfo> {
        if (isNumberic) {
            var vipUserList:List<DbVipUserInfo> = vipUserInfoDao.queryUserByLikePhoneNumber("%"+key+"%")
            return vipUserList
        }else {
            var vipUserList:List<DbVipUserInfo> = vipUserInfoDao.queryUserByLikeUserName("%"+key+"%")
            return vipUserList
        }
    }

    private val convertGoodsList:MutableList<UserConvertGoodsInfo> = mutableListOf()

    /**
     * 查询 所有用户 兑换记录
     * @return
     */
    suspend fun queryAllConvertGoodsList(pageSize:Int,pageNumber:Int):List<UserConvertGoodsInfo> {
        convertGoodsList.clear()
        var dbGoodsList:List<DbConvertGoodsInfo> = convertGoodsInfoDao.queryPageReverseList(pageSize,pageSize * pageNumber)
        for (dbGoodsInfo in dbGoodsList) {
            val dbVipUserInfo:DbVipUserInfo = vipUserInfoDao.queryUserInfoFromUid(dbGoodsInfo.getUid())
            var userConvertGoodsInfo = UserConvertGoodsInfo()
            userConvertGoodsInfo.setUserName(dbVipUserInfo.getUserName())
            userConvertGoodsInfo.setPhoneNumber(dbVipUserInfo.getPhoneNumber())
            userConvertGoodsInfo.setUseIntegral(dbGoodsInfo.getUseIntegral())
            userConvertGoodsInfo.setDesc(dbGoodsInfo.getDesc())
            userConvertGoodsInfo.setTime(dbGoodsInfo.getTime())
            convertGoodsList.add(userConvertGoodsInfo)
        }
        return convertGoodsList
    }
}
