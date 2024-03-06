package com.jintao.vipmanager.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.jintao.vipmanager.database.dao.DaoMaster;
import com.jintao.vipmanager.database.dao.DbUserConsumeInfoDao;
import com.jintao.vipmanager.database.dao.DbVipUserInfoDao;
import org.greenrobot.greendao.database.Database;

/**
 * Author: jintao
 * CreateDate: 2024/1/7 20:43
 * Description:
 */
public class DbUpdateHelper extends DaoMaster.OpenHelper {

    private Context mContext;

    public DbUpdateHelper(Context context, String name) {
        super(context, name);
        this.mContext = context;
    }

    public DbUpdateHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name, factory);
        this.mContext = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
        Log.e("AAAAAA","升级数据库");
        MigrationHelper.migrate(db, new MigrationHelper.ReCreateAllTableListener() {
            @Override
            public void onCreateAllTables(Database db, boolean ifNotExists) {
                DaoMaster.createAllTables(db, ifNotExists);
            }

            @Override
            public void onDropAllTables(Database db, boolean ifExists) {
                DaoMaster.dropAllTables(db, ifExists);
            }
        }, DbVipUserInfoDao.class, DbUserConsumeInfoDao.class);
    }


}
