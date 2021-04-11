package com.qizhuo.framework.gamedata.dao;

import java.util.Map;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.qizhuo.framework.gamedata.dao.entity.GameEntity;
import com.qizhuo.framework.gamedata.dao.entity.RecentGameEntity;

import com.qizhuo.framework.gamedata.dao.GameEntityDao;
import com.qizhuo.framework.gamedata.dao.RecentGameEntityDao;

/**
 * Created by qzc
 * GreenDao（数据库）框架-核心管理类
 *     //注意：只要数据表中的字段做了修改，或添加，就应该在（build.gradle中）schemaVersion版本的基础上加1，并重新编译，保证数据库升级
 */
public class DbManager {

      public static  String DEFAULT_DB_NAME = "MY_DB";
      private static DaoMaster daoMaster;
      private static DaoSession daoSession;
      private static DbManager mDbManager;
      private  DaoMaster.DevOpenHelper mDevOpenHelper;
      private DbManager() {
      }
      public static synchronized  void init(Context context) {
        init(context, DEFAULT_DB_NAME);
      }

    public static synchronized void init(Context context, String dbName) {
        try {
             DEFAULT_DB_NAME=dbName;
            getInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static DbManager getInstance(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Db init need a context, Context can't be null");
        }
        if (null == mDbManager) {
            synchronized (DbManager.class) {
                if (null == mDbManager) {
                    mDbManager = new DbManager(context);
                }
            }
        }
        return mDbManager;
    }
    private DbManager(Context context) {
        // 初始化数据库信息
        mDevOpenHelper = new DaoMaster.DevOpenHelper(context, DEFAULT_DB_NAME);
        getDaoMaster(context);
        getDaoSession();
    }
    /**
     * 获取可读数据库
     */
    public SQLiteDatabase getReadableDatabase(Context context) {
        if (null == mDevOpenHelper) {
            getInstance(context);
        }
        return mDevOpenHelper.getReadableDatabase();
    }

    /**
     * 获取可写数据库
     */
    public SQLiteDatabase getWritableDatabase(Context context) {
        if (null == mDevOpenHelper) {
            getInstance(context);
        }
        return mDevOpenHelper.getWritableDatabase();
    }
    /**
     * 获取DaoMaster
     * 判断是否存在数据库，如果没有则创建数据库
     */
    private  static synchronized DaoMaster getDaoMaster(Context context) {
        if (null == daoMaster) {
            synchronized (DbManager.class) {
                if (null == daoMaster) {
                    /**
                     * 自定义升级辅助类
                     */
                    MySQLiteOpenHelper helper = new MySQLiteOpenHelper(context,DEFAULT_DB_NAME,null);
                    daoMaster  = new DaoMaster(helper.getWritableDatabase());
                    daoSession = daoMaster.newSession();
                }
            }
        }
        return daoMaster;
    }
    /**
     * 获取DaoSession
     */
    public  static synchronized DaoSession getDaoSession() {
        if (daoSession == null) {
            synchronized (DbManager.class){
                if (daoMaster!=null) {
                    daoSession = daoMaster.newSession();
                }
            }
        }
        return daoSession;
    }


}
