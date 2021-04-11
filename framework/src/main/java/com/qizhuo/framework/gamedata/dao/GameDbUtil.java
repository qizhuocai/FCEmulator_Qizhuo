package com.qizhuo.framework.gamedata.dao;

import com.qizhuo.framework.gamedata.dao.entity.GameEntity;
import com.qizhuo.framework.gamedata.dao.entity.RecentGameEntity;

import java.util.List;

/**
 * Created by qzc
 * 数据保存和获取工具类
 */
public class GameDbUtil {

  private static GameDbUtil dbUtil;
  private static String TAG="DbUtil";
  private GameDbUtil() {}

   public static synchronized GameDbUtil getInstance() {
        if (dbUtil == null) {
            synchronized (GameDbUtil.class) {
                if (dbUtil == null) {
                    dbUtil = new GameDbUtil();
                }
            }
        }
        return dbUtil;
    }

     //region   GameEntityDao  管理 操作
    private GameEntityService GameEntity_Service;

    private static  GameEntityDao GetGameEntityDao() {
            return DbManager.getDaoSession().getGameEntityDao();
        }
      public GameEntityService GetGameEntityService() {
          if (GameEntity_Service == null) {
              GameEntity_Service = new GameEntityService(GetGameEntityDao());
          }
          return  GameEntity_Service;
      }

    /**
     * GameEntity保存单个dev
     */
    public void setGameEntity(GameEntity item) {
        if (null == item) {
            return;
        }
        try {
            GameEntity  GameEntity_entity =GetGameEntityService().queryBuilder().where( GameEntityDao.Properties.Name.eq(item.getName())).unique();
            if (GameEntity_entity != null) {
               //替换主键
                item.setId(GameEntity_entity.getId());
                //更新
               GetGameEntityService().update(item);
             } else {
               GetGameEntityService().saveOrUpdate(item);
            }
         } catch (Exception e) {
                   e.printStackTrace();
               }
    }
    /**
     * GameEntity批量保存 dev
     */
    public void setGameEntityList(List<GameEntity> list) {
        if (null == list || list.size() <= 0) {
            return;
        }
        try {
            for (GameEntity item : list) {
             GameEntity  GameEntity_entity =GetGameEntityService().queryBuilder().where( GameEntityDao.Properties.Id.eq(item.getId())).unique();
                        if (GameEntity_entity != null) {
                            //替换主键
                            item.setId(GameEntity_entity.getId());
                            //更新
                            GetGameEntityService().update(item);
                         } else {
                           GetGameEntityService().saveOrUpdate(item);
                        }
            }
         } catch (Exception e) {
                   e.printStackTrace();
               }
    }
    /**
     * GameEntity删除单个dev
     */
    public void deleteGameEntity (GameEntity  item) {
        if (null == item) {
            return;
        }
        try {
            GetGameEntityService().delete(item);
        } catch (Exception e) {
                  e.printStackTrace();
              }
    }
    /**
     * GameEntity删除单个dev
     */
    public void deleteGameEntityBy(long item) {
        try {
             GetGameEntityService().deleteByKey(item);
          } catch (Exception e) {
                    e.printStackTrace();
                }
    }
    /**
     * GameEntity获取保存的dev数据集合
     */
    public List<GameEntity> GetGameEntityList() {
        List<GameEntity> listGameEntity = null;
        try {
             listGameEntity  =GetGameEntityDao() .queryBuilder().list();
         } catch (Exception e)
         {
            e.printStackTrace();
          }
        return  listGameEntity ;
    }
    //endregion

     //region   RecentGameEntityDao  管理 操作
    private RecentGameEntityService RecentGameEntity_Service;

    private static  RecentGameEntityDao GetRecentGameEntityDao() {
            return DbManager.getDaoSession().getRecentGameEntityDao();
        }
      public RecentGameEntityService GetRecentGameEntityService() {
          if (RecentGameEntity_Service == null) {
              RecentGameEntity_Service = new RecentGameEntityService(GetRecentGameEntityDao());
          }
          return  RecentGameEntity_Service;
      }

    /**
     * RecentGameEntity保存单个dev
     */
    public void setRecentGameEntity(RecentGameEntity item) {
        if (null == item) {
            return;
        }
        try {
            RecentGameEntity  RecentGameEntity_entity =GetRecentGameEntityService().queryBuilder().where( RecentGameEntityDao.Properties.Id.eq(item.getId())).unique();
            if (RecentGameEntity_entity != null) {
               //替换主键
                item.setId(RecentGameEntity_entity.getId());
                //更新
               GetRecentGameEntityService().update(item);
             } else {
               GetRecentGameEntityService().saveOrUpdate(item);
            }
         } catch (Exception e) {
                   e.printStackTrace();
               }
    }
    /**
     * RecentGameEntity批量保存 dev
     */
    public void setRecentGameEntityList(List<RecentGameEntity> list) {
        if (null == list || list.size() <= 0) {
            return;
        }
        try {
            for (RecentGameEntity item : list) {
             RecentGameEntity  RecentGameEntity_entity =GetRecentGameEntityService().queryBuilder().where( RecentGameEntityDao.Properties.Id.eq(item.getId())).unique();
                        if (RecentGameEntity_entity != null) {
                            //替换主键
                            item.setId(RecentGameEntity_entity.getId());
                            //更新
                            GetRecentGameEntityService().update(item);
                         } else {
                           GetRecentGameEntityService().saveOrUpdate(item);
                        }
            }
         } catch (Exception e) {
                   e.printStackTrace();
               }
    }
    /**
     * RecentGameEntity删除单个dev
     */
    public void deleteRecentGameEntity (RecentGameEntity  item) {
        if (null == item) {
            return;
        }
        try {
            GetRecentGameEntityService().delete(item);
        } catch (Exception e) {
                  e.printStackTrace();
              }
    }
    /**
     * RecentGameEntity删除单个dev
     */
    public void deleteRecentGameEntityBy(long item) {
        try {
             GetRecentGameEntityService().deleteByKey(item);
          } catch (Exception e) {
                    e.printStackTrace();
                }
    }
    /**
     * RecentGameEntity获取保存的dev数据集合
     */
    public List<RecentGameEntity> GetRecentGameEntityList() {
        List<RecentGameEntity> listRecentGameEntity = null;
        try {
             listRecentGameEntity  =GetRecentGameEntityDao() .queryBuilder().list();
         } catch (Exception e)
         {
            e.printStackTrace();
          }
        return  listRecentGameEntity ;
    }
    //endregion



}