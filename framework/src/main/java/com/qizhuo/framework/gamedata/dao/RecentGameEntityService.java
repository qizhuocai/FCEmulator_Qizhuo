package com.qizhuo.framework.gamedata.dao;

import org.greenrobot.greendao.AbstractDao;

import com.qizhuo.framework.gamedata.dao.entity.GameEntity;
import com.qizhuo.framework.gamedata.dao.entity.RecentGameEntity;

import com.qizhuo.framework.gamedata.dao.GameEntityDao;
import com.qizhuo.framework.gamedata.dao.RecentGameEntityDao;
/**
 * Created by qzc
 */
public class RecentGameEntityService extends BaseServiceDao<RecentGameEntity, Long> {
    public RecentGameEntityService(AbstractDao dao) {
        super(dao);
    }
}


