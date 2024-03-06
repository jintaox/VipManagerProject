package com.jintao.vipmanager.database;

import android.content.Context;
import com.jintao.vipmanager.BuildConfig;
import com.jintao.vipmanager.database.dao.DaoMaster;
import com.jintao.vipmanager.database.dao.DaoSession;
import com.jintao.vipmanager.utils.AppConfig;

import org.greenrobot.greendao.query.QueryBuilder;

/**
 * 所有数据库都在这儿打开关闭
 */
public class DatabaseManager {

    private Context context;

    //多线程中要被共享的使用volatile关键字修饰
    private volatile static DatabaseManager manager = new DatabaseManager();

    private static DbUpdateHelper mVipUserHelper;
    private static DaoMaster mVipUserDaoMaster;
    private static DaoSession mVipUserDaoSession;

    public static DatabaseManager getInstance() {
        return manager;
    }

    private DatabaseManager() {
        setDebug();
    }

    public DatabaseManager init(Context context) {
        this.context = context;
        return this;
    }

    public DaoMaster getVipUserDaoMaster() {
        if (mVipUserDaoMaster == null) {
            mVipUserHelper = new DbUpdateHelper(context, AppConfig.DATABASE_FILE_NAME, null);
            mVipUserDaoMaster = new DaoMaster(mVipUserHelper.getWritableDatabase());
        }
        return mVipUserDaoMaster;
    }

    public DaoSession getVipUserDaoSession() {
        if (mVipUserDaoSession == null) {
            if (mVipUserDaoMaster == null) {
                mVipUserDaoMaster = getVipUserDaoMaster();
            }
            mVipUserDaoSession = mVipUserDaoMaster.newSession();
        }
        return mVipUserDaoSession;
    }

    public void closeVipUserConnection() {
        if (mVipUserDaoSession != null) {
            mVipUserDaoSession.clear();
            mVipUserDaoSession = null;
        }
        if (mVipUserHelper != null) {
            mVipUserHelper.close();
            mVipUserHelper = null;
        }
    }

    public void setDebug() {
        if (BuildConfig.DEBUG) {
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
        }
    }
}
