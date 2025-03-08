package com.jintao.vipmanager.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

import com.jintao.vipmanager.database.bean.DbUserConsumeInfo

/**
 * DAO for table "consume_record_info".
*/
@Dao
interface DbUserConsumeInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsumeInfo(consumeInfo:DbUserConsumeInfo):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConsumeList(consumeList: List<DbUserConsumeInfo>):List<Long>

    @Delete
    suspend fun deleteConsumeInfo(consumeInfo: DbUserConsumeInfo)

    @Query("DELETE FROM consume_record_info WHERE _id = :id")
    suspend fun deleteFormId(id: Long):Int

    @Query("DELETE FROM consume_record_info WHERE uid = :uid")
    suspend fun deleteFormUid(uid:Int):Int

    @Query("DELETE FROM consume_record_info")
    suspend fun deleteAllConsume():Int

    //Int  1 成功 0失败
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateConsumeInfo(consumeInfo: DbUserConsumeInfo):Int

    @Query("SELECT * FROM consume_record_info WHERE uid = :uid  ORDER BY _id DESC LIMIT :number")
    suspend fun queryConsumeByUidLast(uid:Int, number:Int):List<DbUserConsumeInfo>

    @Query("SELECT * FROM consume_record_info WHERE uid = :uid ORDER BY _id DESC LIMIT :pageSize OFFSET :offset")
    suspend fun queryPageReverseList(uid: Int, pageSize:Int, offset:Int):List<DbUserConsumeInfo>

    @Query("SELECT * FROM consume_record_info WHERE uid = :uid AND consume_time LIKE :date ORDER BY _id DESC")
    suspend fun queryByConsumeUidDate(uid: Int, date:String):List<DbUserConsumeInfo>

    @Query("SELECT * FROM consume_record_info WHERE consume_time LIKE :date ORDER BY _id DESC")
    suspend fun queryByLikeDate(date:String):List<DbUserConsumeInfo>
}
