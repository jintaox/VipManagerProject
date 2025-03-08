package com.jintao.vipmanager.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.jintao.vipmanager.database.bean.DbConvertGoodsInfo

/** 
 * DAO for table "exchange_goods_info".
*/
@Dao
interface DbConvertGoodsInfoDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoodsInfo(goodsInfo: DbConvertGoodsInfo):Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGoodsList(goodsList: List<DbConvertGoodsInfo>):List<Long>

    @Delete
    suspend fun deleteGoodsInfo(goodsInfo: DbConvertGoodsInfo)

    @Query("DELETE FROM exchange_goods_info WHERE _id = :id")
    suspend fun deleteFormId(id: Long):Int

    @Query("DELETE FROM exchange_goods_info WHERE uid = :uid")
    suspend fun deleteFormUid(uid: Int):Int

    @Query("DELETE FROM exchange_goods_info")
    suspend fun deleteAllGoods():Int

    //Int  1 成功 0失败
    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateGoodsInfo(goodsInfo: DbConvertGoodsInfo):Int

    @Query("SELECT * FROM exchange_goods_info WHERE uid = :uid ORDER BY _id DESC")
    suspend fun queryFormUid(uid:Int): List<DbConvertGoodsInfo>

    @Query("SELECT * FROM exchange_goods_info  ORDER BY _id DESC LIMIT :pageSize OFFSET :offset")
    suspend fun queryPageReverseList(pageSize:Int, offset:Int):List<DbConvertGoodsInfo>
}
