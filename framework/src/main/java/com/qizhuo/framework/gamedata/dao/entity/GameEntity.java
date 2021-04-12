package com.qizhuo.framework.gamedata.dao.entity;



import androidx.annotation.NonNull;

import com.qizhuo.framework.utils.EmuUtils;
import com.qizhuo.framework.utils.annotations.Column;
import com.qizhuo.framework.utils.annotations.ObjectFromOtherTable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

@Entity(nameInDb = "rom", createInDb = true)
public class GameEntity  implements Serializable {
    private static final long serialVersionUID = -416689653487858374L;

    @Property(nameInDb = "id")
    @Id(autoincrement = true)
    private Long id;

    @Property(nameInDb = "name")
    private String name;
    @Property(nameInDb = "enname")
    private String enname;

    @Property(nameInDb = "chname")
    private String chname;

    @Property(nameInDb = "pinyin")
    private String pinyin;

    @Property(nameInDb = "visiable")
    private int visiable;

    @Property(nameInDb = "type")
    private String type;

    @Property(nameInDb = "path")
    public String path = "";
    @Property(nameInDb = "checksum")
    public String checksum = "";
    @Property(nameInDb = "_id")
    public long _id;
    @Property(nameInDb = "zipfile_id")
    public long zipfile_id = -1;
    @Property(nameInDb = "inserTime")
    public long inserTime = 0;
    @Property(nameInDb = "lastGameTime")
    public long lastGameTime = 0;
    @Property(nameInDb = "runCount")
    public int runCount = 0;
    @Property(nameInDb = "cleanNameCache")
    private String cleanNameCache = null;

    @Property(nameInDb = "sortNameCache")
    private String sortNameCache = null;

    @Property(nameInDb = "hash")
    private String hash = "";




    @Generated(hash = 344939995)
    public GameEntity(Long id, String name, String enname, String chname,
            String pinyin, int visiable, String type, String path, String checksum,
            long _id, long zipfile_id, long inserTime, long lastGameTime,
            int runCount, String cleanNameCache, String sortNameCache,
            String hash) {
        this.id = id;
        this.name = name;
        this.enname = enname;
        this.chname = chname;
        this.pinyin = pinyin;
        this.visiable = visiable;
        this.type = type;
        this.path = path;
        this.checksum = checksum;
        this._id = _id;
        this.zipfile_id = zipfile_id;
        this.inserTime = inserTime;
        this.lastGameTime = lastGameTime;
        this.runCount = runCount;
        this.cleanNameCache = cleanNameCache;
        this.sortNameCache = sortNameCache;
        this.hash = hash;
    }

    @Generated(hash = 854974668)
    public GameEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnname() {
        return this.enname;
    }

    public void setEnname(String enname) {
        this.enname = enname;
    }

    public String getChname() {
        return this.chname;
    }

    public void setChname(String chname) {
        this.chname = chname;
    }

    public String getPinyin() {
        return this.pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public int getVisiable() {
        return this.visiable;
    }

    public void setVisiable(int visiable) {
        this.visiable = visiable;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getChecksum() {
        return this.checksum;
    }

    public void setChecksum(String checksum) {
        this.checksum = checksum;
    }

    public long get_id() {
        return this._id;
    }

    public void set_id(long _id) {
        this._id = _id;
    }

    public long getZipfile_id() {
        return this.zipfile_id;
    }

    public void setZipfile_id(long zipfile_id) {
        this.zipfile_id = zipfile_id;
    }

    public long getInserTime() {
        return this.inserTime;
    }

    public void setInserTime(long inserTime) {
        this.inserTime = inserTime;
    }

    public long getLastGameTime() {
        return this.lastGameTime;
    }

    public void setLastGameTime(long lastGameTime) {
        this.lastGameTime = lastGameTime;
    }

    public int getRunCount() {
        return this.runCount;
    }

    public void setRunCount(int runCount) {
        this.runCount = runCount;
    }

    public String getCleanNameCache() {
        return this.cleanNameCache;
    }

    public void setCleanNameCache(String cleanNameCache) {
        this.cleanNameCache = cleanNameCache;
    }

    public String getSortNameCache() {
        return this.sortNameCache;
    }

    public void setSortNameCache(String sortNameCache) {
        this.sortNameCache = sortNameCache;
    }


    public GameEntity(File file) {
        this(file, "");
        checksum = EmuUtils.getMD5Checksum(file);
    }

    public GameEntity(File file, String checksum) {
        name = file.getName();
        path = file.getAbsolutePath();
        this.checksum = checksum;
    }

  //  public ArrayList<GameEntity> games = new ArrayList<>();

    public GameEntity(String name, String path, String checksum) {
        this.name = name;
        this.path = path;
        this.checksum = checksum;
    }

    @Override
    public String toString() {
        return name + " " + checksum + " zipId:" + zipfile_id;
    }

    public boolean isInArchive() {
        return zipfile_id != -1;
    }

    public String getCleanName() {
        if (cleanNameCache == null) {
            String name = EmuUtils.removeExt(this.name);
            int idx = name.lastIndexOf('/');
            if (idx != -1) {
                cleanNameCache = name.substring(idx + 1);
            } else {
                cleanNameCache = name;
            }
        }
        return cleanNameCache;
    }

    public String getSortName() {
        if (sortNameCache == null) {
            sortNameCache = getCleanName().toLowerCase();
        }
        return sortNameCache;
    }

//    @Override
//    public int compareTo(@NonNull GameEntity another) {
//        return checksum.compareTo(another.checksum);
//    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof GameEntity)) {
            return false;
        } else {
            GameEntity gd = (GameEntity) o;
            return gd.checksum != null && checksum.equals(gd.checksum);
        }
    }

    public String getHash() {
        return this.hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }
}
