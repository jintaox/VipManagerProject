package com.jintao.vipmanager.database.helper;

import android.content.Context;
import com.jintao.vipmanager.database.DatabaseManager;
import com.jintao.vipmanager.database.bean.DbConvertGoodsInfo;
import com.jintao.vipmanager.database.bean.DbUserConsumeInfo;
import com.jintao.vipmanager.database.bean.DbVipUserInfo;
import com.jintao.vipmanager.database.dao.DaoSession;
import com.jintao.vipmanager.database.dao.DbConvertGoodsInfoDao;
import com.jintao.vipmanager.database.dao.DbUserConsumeInfoDao;
import com.jintao.vipmanager.database.dao.DbVipUserInfoDao;
import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.QueryBuilder;
import java.util.List;

/**
 * 一个数据库所有表都在这操作
 * @param <T>  表Dao类型
 */
public class DbVipUserHelper<T> {

    private DaoSession mDaoSession;
    private Class<T> entityClass;
    private AbstractDao entityDao;

    public DbVipUserHelper(Context context, Class<T> pEntityClass) {
        DatabaseManager mManager = DatabaseManager.getInstance().init(context);
        mDaoSession = mManager.getVipUserDaoSession();
        entityClass = pEntityClass;
    }

    public DbVipUserHelper<T> getVipUserDao() {
        entityDao = mDaoSession.getDbVipUserInfoDao();
        return this;
    }

    public DbVipUserHelper<T> getUserConsumeDao() {
        entityDao = mDaoSession.getDbUserConsumeInfoDao();
        return this;
    }

    public DbVipUserHelper<T> getConvertGoodsDao() {
        entityDao = mDaoSession.getDbConvertGoodsInfoDao();
        return this;
    }

    /**
     * 插入记录，如果表未创建，先创建表
     */
    public boolean insert(T pEntity) {
        return entityDao.insert(pEntity) != -1;
    }

    /**
     * 插入多条数据，在子线程操作
     */
    public boolean insertMultiple(final List<T> pEntityList) {
        try {
            mDaoSession.runInTx(new Runnable() {
                @Override
                public void run() {
                    for (T entity : pEntityList) {
                        mDaoSession.insertOrReplace(entity);
                    }
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 修改一条数据
     */
    public boolean update(T entity) {
        try {
            mDaoSession.update(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    public boolean delete(T entity) {
        try {
            //按照id删除
            mDaoSession.delete(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 删除所有记录
     */
    public boolean deleteAll() {
        try {
            //按照id删除
            mDaoSession.deleteAll(entityClass);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据主键id查询记录
     */
    public T queryById(long key) {
        return mDaoSession.load(entityClass, key);
    }

    /**
     * 查询所有记录
     */
    public List<T> queryAll() {
        return mDaoSession.loadAll(entityClass);
    }

    public long queryVipUserCount() {
        return mDaoSession.getDbVipUserInfoDao().count();
    }

    /**
     * 查询 手机号 是否可用
     */
    public boolean queryByVipPhoneAvai(String phone) {
        DbVipUserInfo vipUserInfo = mDaoSession.getDbVipUserInfoDao().queryBuilder().where(DbVipUserInfoDao.Properties.PhoneNumber.eq(phone)).limit(1).unique();
        return vipUserInfo==null;
    }

    /**
     * 查询 uid 是否存在是否可用
     */
    public boolean queryByVipUidAvai(int uid) {
        DbVipUserInfo vipUserInfo = mDaoSession.getDbVipUserInfoDao().queryBuilder().where(DbVipUserInfoDao.Properties.Uid.eq(uid)).limit(1).unique();
        return vipUserInfo==null;
    }

    /**
     * 根据 消费时间 逆序 分页查询 用户列表
     * @param pageSize
     * @param pageCount
     * @return
     */
    public List<DbVipUserInfo> queryPageReverseList(int pageSize,int pageCount) {
        QueryBuilder<DbVipUserInfo> queryBuilder = mDaoSession.getDbVipUserInfoDao().queryBuilder();
        return queryBuilder.orderDesc(DbVipUserInfoDao.Properties.ConsumeTime).offset(pageSize * pageCount).limit(pageSize).list();
    }

    /**
     * 根据输入内容模糊查询
     * @param key
     * @return
     */
    public List<DbVipUserInfo> queryByLikeContent(boolean isNumberic, String key) {
        QueryBuilder<DbVipUserInfo> dbVipUserInfoQueryBuilder = mDaoSession.getDbVipUserInfoDao().queryBuilder();
        if (isNumberic) {
            return dbVipUserInfoQueryBuilder.orderDesc(DbVipUserInfoDao.Properties.Id).where(DbVipUserInfoDao.Properties.PhoneNumber.like("%"+key+"%")).list();
        }else {
            return dbVipUserInfoQueryBuilder.orderDesc(DbVipUserInfoDao.Properties.Id).where(DbVipUserInfoDao.Properties.UserName.like("%"+key+"%")).list();
        }
    }


    /**
     * 根据uid查询数量
     */
    public long queryConsumeUidCount(int uid) {
        return mDaoSession.getDbUserConsumeInfoDao().queryBuilder().where(DbUserConsumeInfoDao.Properties.Uid.eq(uid)).count();
    }

    /**
     * 根据 uid 查询所有记录
     * @param uid
     * @return
     */
    public List<DbUserConsumeInfo> queryByConsumeUid(int uid) {
        return mDaoSession.getDbUserConsumeInfoDao().queryBuilder().where(DbUserConsumeInfoDao.Properties.Uid.eq(uid)).list();
    }

    /**
     * 根据 uid 查询倒数最后几条消费记录
     * @param uid
     * @return
     */
    public List<DbUserConsumeInfo> queryByConsumeUidLast(int uid,int number) {
        return mDaoSession.getDbUserConsumeInfoDao().queryBuilder().orderDesc(DbUserConsumeInfoDao.Properties.Id).where(DbUserConsumeInfoDao.Properties.Uid.eq(uid)).limit(number).list();
    }

    /**
     * 根据 uid 分页查询 消费明细
     */
    public List<DbUserConsumeInfo> queryByConsumeUidAll(int uid,int pageSize,int pageCount) {
        return mDaoSession.getDbUserConsumeInfoDao().queryBuilder().orderDesc(DbUserConsumeInfoDao.Properties.Id).where(DbUserConsumeInfoDao.Properties.Uid.eq(uid)).offset(pageSize * pageCount).limit(pageSize).list();
    }

    /**
     * 根据 uid 和 日期 查询消费明细
     */
    public List<DbUserConsumeInfo> queryByConsumeUidDate(int uid,String date) {
        return mDaoSession.getDbUserConsumeInfoDao().queryBuilder().orderDesc(DbUserConsumeInfoDao.Properties.Id).where(DbUserConsumeInfoDao.Properties.Uid.eq(uid),DbUserConsumeInfoDao.Properties.ConsumeTime.like(date+"%")).list();
    }

    /**
     * 根据日期查询 所有用户 消费记录
     * @param date
     * @return
     */
    public List<DbUserConsumeInfo> queryByLikeDate(String date) {
        QueryBuilder<DbUserConsumeInfo> queryBuilder = mDaoSession.getDbUserConsumeInfoDao().queryBuilder();
        return queryBuilder.where(DbUserConsumeInfoDao.Properties.ConsumeTime.like(date+"%")).list();
    }

    /**
     * 删除单条记录
     */
    public boolean deleteConsume(DbUserConsumeInfo entity) {
        try {
            //按照id删除
            mDaoSession.getDbUserConsumeInfoDao().delete(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据 uid 删除所有消费记录
     * @param uid
     * @return
     */
    public boolean deleteUidConsumeAll(int uid) {
        try {
            mDaoSession.runInTx(new Runnable() {
                @Override
                public void run() {
                    mDaoSession.getDbUserConsumeInfoDao().queryBuilder().where(DbUserConsumeInfoDao.Properties.Uid.eq(uid)).buildDelete().executeDeleteWithoutDetachingEntities();
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 根据 uid 查询所有兑换记录
     * @param uid
     * @return
     */
    public List<DbConvertGoodsInfo> queryByConvertGoodsUid(int uid) {
        return mDaoSession.getDbConvertGoodsInfoDao().queryBuilder().orderDesc(DbConvertGoodsInfoDao.Properties.Id).where(DbConvertGoodsInfoDao.Properties.Uid.eq(uid)).list();
    }

    /**
     * 根据 uid 删除所有兑换记录
     * @param uid
     * @return
     */
    public boolean deleteUidGoodsAll(int uid) {
        try {
            mDaoSession.runInTx(new Runnable() {
                @Override
                public void run() {
                    mDaoSession.getDbConvertGoodsInfoDao().queryBuilder().where(DbConvertGoodsInfoDao.Properties.Uid.eq(uid)).buildDelete().executeDeleteWithoutDetachingEntities();
                }
            });
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public void close() {
        DatabaseManager.getInstance().closeVipUserConnection();
    }
}