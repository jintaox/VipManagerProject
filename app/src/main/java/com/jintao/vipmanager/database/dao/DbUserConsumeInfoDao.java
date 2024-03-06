package com.jintao.vipmanager.database.dao;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.jintao.vipmanager.database.bean.DbUserConsumeInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "consume_record_info".
*/
public class DbUserConsumeInfoDao extends AbstractDao<DbUserConsumeInfo, Long> {

    public static final String TABLENAME = "consume_record_info";

    /**
     * Properties of entity DbUserConsumeInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, Long.class, "id", true, "_id");
        public final static Property Uid = new Property(1, int.class, "uid", false, "uid");
        public final static Property Content = new Property(2, String.class, "content", false, "content");
        public final static Property ImagePath = new Property(3, String.class, "imagePath", false, "image_path");
        public final static Property ConsumeTime = new Property(4, String.class, "consumeTime", false, "consume_time");
        public final static Property ConsumeAmount = new Property(5, float.class, "consumeAmount", false, "consume_amount");
        public final static Property ConsumeIntegral = new Property(6, float.class, "consumeIntegral", false, "consume_integral");
        public final static Property ConverRatio = new Property(7, String.class, "converRatio", false, "conver_ratio");
        public final static Property ConsumeType = new Property(8, boolean.class, "consumeType", false, "consume_type");
    }


    public DbUserConsumeInfoDao(DaoConfig config) {
        super(config);
    }
    
    public DbUserConsumeInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"consume_record_info\" (" + //
                "\"_id\" INTEGER PRIMARY KEY AUTOINCREMENT ," + // 0: id
                "\"uid\" INTEGER NOT NULL ," + // 1: uid
                "\"content\" TEXT," + // 2: content
                "\"image_path\" TEXT," + // 3: imagePath
                "\"consume_time\" TEXT," + // 4: consumeTime
                "\"consume_amount\" REAL NOT NULL ," + // 5: consumeAmount
                "\"consume_integral\" REAL NOT NULL ," + // 6: consumeIntegral
                "\"conver_ratio\" TEXT," + // 7: converRatio
                "\"consume_type\" INTEGER NOT NULL );"); // 8: consumeType
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"consume_record_info\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, DbUserConsumeInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getUid());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(3, content);
        }
 
        String imagePath = entity.getImagePath();
        if (imagePath != null) {
            stmt.bindString(4, imagePath);
        }
 
        String consumeTime = entity.getConsumeTime();
        if (consumeTime != null) {
            stmt.bindString(5, consumeTime);
        }
        stmt.bindDouble(6, entity.getConsumeAmount());
        stmt.bindDouble(7, entity.getConsumeIntegral());
 
        String converRatio = entity.getConverRatio();
        if (converRatio != null) {
            stmt.bindString(8, converRatio);
        }
        stmt.bindLong(9, entity.getConsumeType() ? 1L: 0L);
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, DbUserConsumeInfo entity) {
        stmt.clearBindings();
 
        Long id = entity.getId();
        if (id != null) {
            stmt.bindLong(1, id);
        }
        stmt.bindLong(2, entity.getUid());
 
        String content = entity.getContent();
        if (content != null) {
            stmt.bindString(3, content);
        }
 
        String imagePath = entity.getImagePath();
        if (imagePath != null) {
            stmt.bindString(4, imagePath);
        }
 
        String consumeTime = entity.getConsumeTime();
        if (consumeTime != null) {
            stmt.bindString(5, consumeTime);
        }
        stmt.bindDouble(6, entity.getConsumeAmount());
        stmt.bindDouble(7, entity.getConsumeIntegral());
 
        String converRatio = entity.getConverRatio();
        if (converRatio != null) {
            stmt.bindString(8, converRatio);
        }
        stmt.bindLong(9, entity.getConsumeType() ? 1L: 0L);
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0);
    }    

    @Override
    public DbUserConsumeInfo readEntity(Cursor cursor, int offset) {
        DbUserConsumeInfo entity = new DbUserConsumeInfo( //
            cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0), // id
            cursor.getInt(offset + 1), // uid
            cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2), // content
            cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3), // imagePath
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // consumeTime
            cursor.getFloat(offset + 5), // consumeAmount
            cursor.getFloat(offset + 6), // consumeIntegral
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7), // converRatio
            cursor.getShort(offset + 8) != 0 // consumeType
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, DbUserConsumeInfo entity, int offset) {
        entity.setId(cursor.isNull(offset + 0) ? null : cursor.getLong(offset + 0));
        entity.setUid(cursor.getInt(offset + 1));
        entity.setContent(cursor.isNull(offset + 2) ? null : cursor.getString(offset + 2));
        entity.setImagePath(cursor.isNull(offset + 3) ? null : cursor.getString(offset + 3));
        entity.setConsumeTime(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setConsumeAmount(cursor.getFloat(offset + 5));
        entity.setConsumeIntegral(cursor.getFloat(offset + 6));
        entity.setConverRatio(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
        entity.setConsumeType(cursor.getShort(offset + 8) != 0);
     }
    
    @Override
    protected final Long updateKeyAfterInsert(DbUserConsumeInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(DbUserConsumeInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(DbUserConsumeInfo entity) {
        return entity.getId() != null;
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}