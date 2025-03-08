package com.jintao.vipmanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.jintao.vipmanager.database.bean.DbConvertGoodsInfo
import com.jintao.vipmanager.database.bean.DbUserConsumeInfo
import com.jintao.vipmanager.database.bean.DbVipUserInfo
import com.jintao.vipmanager.database.dao.DbConvertGoodsInfoDao
import com.jintao.vipmanager.database.dao.DbUserConsumeInfoDao
import com.jintao.vipmanager.database.dao.DbVipUserInfoDao
import com.jintao.vipmanager.utils.AppConfig

@Database(entities = [
    DbVipUserInfo::class,
    DbUserConsumeInfo::class,
    DbConvertGoodsInfo::class
], version = 4, exportSchema = false)
abstract class DatabaseManager : RoomDatabase() {

    companion object {

        @Volatile
        private var INSTANCE : DatabaseManager? = null

        fun getDatabase(context:Context): DatabaseManager {
            if (INSTANCE ==null) {
                synchronized(this) {
                    if (INSTANCE ==null) {
                        INSTANCE = Room.databaseBuilder(context.getApplicationContext(), DatabaseManager::class.java, AppConfig.DATABASE_FILE_NAME)
                            .addCallback(sRoomDatabaseCallback)
//                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
//                            .addMigrations(MIGRATION_4_5)
                            .build()
                    }
                }
            }
            return INSTANCE!!
        }

        private val sRoomDatabaseCallback = object : RoomDatabase.Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
            }
        }

        private val MIGRATION_4_5 = object :Migration(4,5) {
            override fun migrate(db: SupportSQLiteDatabase) {

            }
        }
    }

    abstract fun vipUserInfoDao():DbVipUserInfoDao
    abstract fun userConsumeInfoDao():DbUserConsumeInfoDao
    abstract fun convertGoodsInfoDao():DbConvertGoodsInfoDao

}
