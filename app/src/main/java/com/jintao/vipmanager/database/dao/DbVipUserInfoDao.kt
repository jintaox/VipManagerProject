package com.jintao.vipmanager.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jintao.vipmanager.database.bean.DbVipUserInfo

/**
 * DAO for table "vip_user_info".
 */
@Dao
interface DbVipUserInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserInfo(vipUserInfo: DbVipUserInfo): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserList(vipUserList: List<DbVipUserInfo>): List<Long>

    @Delete
    suspend fun deleteUserInfo(vipUserInfo: DbVipUserInfo)

    @Query("DELETE FROM vip_user_info WHERE _id = :id")
    suspend fun deleteFormId(id: Long): Int

    @Query("DELETE FROM vip_user_info")
    suspend fun deleteAllUser(): Int

    //Int  1 成功 0失败
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateUserInfo(vipUserInfo: DbVipUserInfo): Int

    @Query("SELECT COUNT(*) FROM vip_user_info")
    suspend fun getUserCount(): Long

    @Query("SELECT * FROM vip_user_info WHERE _id = :id LIMIT 1")
    suspend fun queryUserInfoFromId(id: Long): DbVipUserInfo

    @Query("SELECT * FROM vip_user_info WHERE phone_number = :phoneNumber LIMIT 1")
    suspend fun queryUserInfoFromPhone(phoneNumber: String): DbVipUserInfo

    @Query("SELECT * FROM vip_user_info WHERE uid = :uid LIMIT 1")
    suspend fun queryUserInfoFromUid(uid: Int): DbVipUserInfo

    @Query("SELECT * FROM vip_user_info ORDER BY consume_time DESC LIMIT :pageSize OFFSET :offset")
    suspend fun queryPageReverseList(pageSize: Int, offset: Int): List<DbVipUserInfo>

    @Query("SELECT * FROM vip_user_info WHERE phone_number LIKE :key ORDER BY _id DESC")
    suspend fun queryUserByLikePhoneNumber(key: String): List<DbVipUserInfo>

    @Query("SELECT * FROM vip_user_info WHERE user_name LIKE :key ORDER BY _id DESC")
    suspend fun queryUserByLikeUserName(key: String): List<DbVipUserInfo>
}
