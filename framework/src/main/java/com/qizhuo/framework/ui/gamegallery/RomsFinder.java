package com.qizhuo.framework.ui.gamegallery;

import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.qizhuo.framework.gamedata.dao.GameDbUtil;
import com.qizhuo.framework.gamedata.dao.GameEntityDao;
import com.qizhuo.framework.gamedata.dao.entity.GameEntity;
//import com.qizhuo.framework.utils.DatabaseHelper;
import com.qizhuo.framework.utils.EmuUtils;
import com.qizhuo.framework.utils.NLog;
import com.qizhuo.framework.utils.SDCardUtil;

import static com.qizhuo.framework.ui.gamegallery.GalleryActivity.finalStringListstrlist;

public class RomsFinder extends Thread {
    private static final String TAG = "RomsFinder";
    private FilenameExtFilter filenameExtFilter;
    private FilenameExtFilter inZipFileNameExtFilter;
    private String androidAppDataFolder = "";
    private HashMap<String, GameEntity> oldGames = new HashMap<>();
//    private DatabaseHelper dbHelper;
    private ArrayList<GameEntity> games = new ArrayList<>();
    private OnRomsFinderListener listener;
    private BaseGameGalleryActivity activity;
    private boolean searchNew = true;
    private File selectedFolder;
    private AtomicBoolean running = new AtomicBoolean(false);

    public RomsFinder(Set<String> exts, Set<String> inZipExts, BaseGameGalleryActivity activity,
                      OnRomsFinderListener listener, boolean searchNew, File selectedFolder) {
        this.listener = listener;
        this.activity = activity;
        this.searchNew = searchNew;
        this.selectedFolder = selectedFolder;
        filenameExtFilter = new FilenameExtFilter(exts, true, false);
        inZipFileNameExtFilter = new FilenameExtFilter(inZipExts, true, false);
        androidAppDataFolder = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android";
//        dbHelper = new DatabaseHelper(activity);
    }

//    public static ArrayList<GameEntity> getAllGames(DatabaseHelper helper) {
//        return helper.selectObjsFromDb(GameEntity.class, false, "GROUP BY checksum", null);
//    }

    private void getRomAndPackedFiles(File root, List<File> result, HashSet<String> usedPaths) {
        String dirPath = null;
        Stack<DirInfo> dirStack = new Stack<>();
        dirStack.removeAllElements();
        dirStack.add(new DirInfo(root, 0));
        final int MAX_LEVEL = 12;

        while (running.get() && !dirStack.empty()) {
            DirInfo dir = dirStack.remove(0);
            try {
                dirPath = dir.file.getCanonicalPath();
            } catch (IOException e1) {
                NLog.e(TAG, "search error", e1);
            }

            if (dirPath != null && !usedPaths.contains(dirPath) && dir.level <= MAX_LEVEL) {
                usedPaths.add(dirPath);
                File[] files = dir.file.listFiles(filenameExtFilter);
                if (files != null) {
                    for (File file : files) {
                        if (file.isDirectory()) {
                            String canonicalPath = null;
                            try {
                                canonicalPath = file.getCanonicalPath();
                            } catch (IOException e) {
                                NLog.e(TAG, "search error", e);
                            }
                            if (canonicalPath != null
                                    && (!usedPaths.contains(canonicalPath))) {
                                if (canonicalPath.equals(androidAppDataFolder)) {
                                    NLog.i(TAG, "ignore " + androidAppDataFolder);
                                } else {
                                    dirStack.add(new DirInfo(file, dir.level + 1));
                                }
                            } else {
                                NLog.i(TAG, "cesta " + canonicalPath + " jiz byla prohledana");
                            }
                        } else {
                            result.add(file);
                        }
                    }
                }
            } else {
                NLog.i(TAG, "cesta " + dirPath + " jiz byla prohledana");
            }
        }
    }

    @Override
    public void run() {
        android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_BACKGROUND);
        running.set(true);
        NLog.i(TAG, "start");
        activity.runOnUiThread(() -> listener.onRomsFinderStart(searchNew));

//        ArrayList<GameEntity> oldRoms = getAllGames(dbHelper);
        ArrayList<GameEntity> oldRoms =new ArrayList<>();
        List<GameEntity>   oldRomslist =  GameDbUtil.getInstance().GetGameEntityList();
        if(oldRomslist!=null) {
            oldRoms.addAll(oldRomslist);
        }
//        oldRoms = removeNonExistRoms(oldRoms);
        final ArrayList<GameEntity> roms = oldRoms;
        NLog.i(TAG, "old games " + oldRoms.size());
        if (oldRoms!=null&&oldRoms.size()>0) {
            finalStringListstrlist = oldRoms;
            activity.runOnUiThread(() -> listener.onRomsFinderFoundGamesInCache(roms));
        }
        if (searchNew) {
            try {
                //全盘查找
                startFileSystemMode(oldRoms);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            activity.runOnUiThread(() -> listener.onRomsFinderEnd(true));
        }
    }

    private void checkZip(File zipFile) {
        File externalCache = activity.getExternalCacheDir();
       // GameEntity games =   GameDbUtil.getInstance(). GetGameEntityService().queryBuilder().where( GameEntityDao.Properties.Zipfile_id.eq( game.zipfile_id)).unique();
          ArrayList<GameEntity> gameslis = new ArrayList<>();
        if (externalCache != null) {
           // String cacheDir = externalCache.getAbsolutePath();
            String cacheDir = Environment.getExternalStorageDirectory().getPath();
            NLog.i(TAG, "check zip" + zipFile.getAbsolutePath());
            String hash = ZipRomFile.computeZipHash(zipFile);
//            ZipRomFile zipRomFile = dbHelper.selectObjFromDb(ZipRomFile.class,
//                    "WHERE hash=\"" + hash + "\"");
            GameEntity zipRomFile =   GameDbUtil.getInstance(). GetGameEntityService().queryBuilder().where( GameEntityDao.Properties.Hash.eq(hash)).unique();

            ZipFile zip = null;
            if (zipRomFile == null) {
                zipRomFile = new GameEntity();
                zipRomFile.setPath(zipFile.getAbsolutePath());
                zipRomFile.setHash(hash);
                zipRomFile.setLastGameTime(System.currentTimeMillis());
                zipRomFile.setId(System.currentTimeMillis());
                zipRomFile.set_id(System.currentTimeMillis());
                try {
                    ZipEntry ze;
                    File dir = new File(cacheDir);
                    int counterRoms = 0;
                    int counterEntry = 0;
                    zip = new ZipFile(zipFile);
                    int max = zip.size();
                    Enumeration<? extends ZipEntry> entries = zip.entries();
                    while (entries.hasMoreElements()) {
                        try {
                            ze = entries.nextElement();
                            counterEntry++;
                            if (running.get() && (!ze.isDirectory())) {
                                String filename = ze.getName();
                                if (inZipFileNameExtFilter.accept(dir, filename)) {
                                    counterRoms++;
                                    InputStream is = zip.getInputStream(ze);
                                    String checksum = EmuUtils.getMD5Checksum(is);
                                    try {
                                        if (is != null) {
                                            is.close();
                                        }
                                    } catch (Exception ignored) {
                                    }
                                    NLog.i(TAG, "dir name:"+ze.getName()+"    :" );

                                    GameEntity game = new GameEntity(ze.getName(), zipFile.getPath(), checksum);
                                    game.inserTime = System.currentTimeMillis();
//                                     zipRomFile.games.add(game);
                                   // zipRomFile.setInserTime(System.currentTimeMillis());
                                    gameslis.add(zipRomFile);
                                    GameDbUtil.getInstance().setGameEntityer(game);
                                    games.add(game);
                                   // zipRomFile.setChecksum(checksum);
                                }
                            }

                            if (counterEntry > 20 && counterRoms == 0) {
                            //    listener.onRomsFinderFoundZipEntry(zipFile.getName() + "\n" + ze.getName(), max - 20 - 1);
                                NLog.i(TAG, "Predcasne ukonceni prohledavani zipu. V prvnich 20 zaznamech v zipu neni ani jeden rom"+zipFile.getName()+"   :"+ ze.getName());
                                break;
                            } else {
                                String name = ze.getName();
                                int idx = name.lastIndexOf('/');
                                if (idx != -1) {
                                    name = name.substring(idx + 1);
                                }
                                if (name.length() > 20) {
                                    name = name.substring(0, 20);
                                }
                                zipRomFile.setName(name);
                           //     listener.onRomsFinderFoundZipEntry(zipFile.getName() + "\n" + name, 0);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (running.get()) {
                        GameDbUtil.getInstance().setGameEntity(zipRomFile);
                        if (!finalStringListstrlist.contains(zipRomFile)) {
                            finalStringListstrlist.add(zipRomFile);
                        }
                              //  dbHelper.insertObjToDb(zipRomFile);
                    }
                } catch (Exception e) {
                    NLog.e(TAG, "", e);
                } finally {
                    try {
                        if (zip != null) {
                            zip.close();
                        }
                    } catch (IOException e) {
                        NLog.e(TAG, "", e);
                    }
                }
            } else {
                games.addAll(gameslis);
             //   listener.onRomsFinderFoundZipEntry(zipFile.getName(),gameslis.size());
                NLog.i(TAG, "found zip in cache " + gameslis.size());
            }
        } else {
            NLog.e(TAG, "external cache dir is null");
            activity.showSDCardFailed();
        }
    }

    private void startFileSystemMode(ArrayList<GameEntity> oldRoms) {

        try {
            HashSet<File> roots = new HashSet<>();
            //
            if (selectedFolder == null) {
                roots = SDCardUtil.getAllStorageLocations();
            } else {
                roots.add(selectedFolder);
            }
            ArrayList<File> result = new ArrayList<>();
            long startTime = System.currentTimeMillis();
            NLog.i(TAG, "start searching in file system");
            HashSet<String> usedPaths = new HashSet<>();

            for (File root : roots) {
                NLog.i(TAG, "exploring " + root.getAbsolutePath());
                getRomAndPackedFiles(root, result, usedPaths);
            }

            NLog.i(TAG, "found " + result.size() + " files");
            NLog.i(TAG, "compute checksum");
            int zipEntriesCount = 0;
            ArrayList<File> zips = new ArrayList<>();

            for (File file : result) {

                try {
                    String path = file.getAbsolutePath();
                    if (running.get()) {
                        String ext = EmuUtils.getExt(path).toLowerCase();
                        if ("zip".equals(ext)) {
                            zips.add(file);
                            try {
                                ZipFile zzFile = new ZipFile(file);
                                zipEntriesCount += zzFile.size();
                            } catch (Exception e) {
                                NLog.e(TAG, "", e);
                            }
                            continue;
                        }
                        GameEntity game;
                        if (oldGames.containsKey(path)) {
                            game = oldGames.get(path);
                        } else {
                            game = new GameEntity(file);
                            game.inserTime = System.currentTimeMillis();
                            game._id = System.currentTimeMillis();
                            game.setId(System.currentTimeMillis());
                            GameDbUtil.getInstance().setGameEntity( game);
                         //   dbHelper.insertObjToDb(game);
                            listener.onRomsFinderFoundFile(game.getName());
                        }
                        if (!finalStringListstrlist.contains(game)) {
                            finalStringListstrlist.add(game);
                        }
                        games.add(game);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
//if(zips!=null) {
//    for (File zip : zips) {
//        if (running.get()) {
//            listener.onRomsFinderZipPartStart(zipEntriesCount);
//            //  checkZip(zip);
//        }
//    }
//}

            if (running.get()) {
                NLog.i(TAG, "found games: " + games.size());
                games = removeNonExistRoms(games);
            }

            NLog.i(TAG, "compute checksum- done");

            if (running.get()) {
                activity.runOnUiThread(() -> {
                    if (games!=null)
                    listener.onRomsFinderNewGames(games);
                    listener.onRomsFinderEnd(true);
                });
            }
            NLog.i(TAG, "time:" + ((System.currentTimeMillis() - startTime) / 1000));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void stopSearch() {
        if (running.get()) {
            listener.onRomsFinderCancel(true);
        }

        running.set(false);
        NLog.i(TAG, "cancel search");
    }

    private ArrayList<GameEntity> removeNonExistRoms(ArrayList<GameEntity> roms) {

        HashSet<String> hashs = new HashSet<>();
        ArrayList<GameEntity> newRoms = new ArrayList<>(roms.size());
        Map<Long, GameEntity> zipsMap = new HashMap<>();

        try {
            List< GameEntity> games =   GameDbUtil.getInstance().GetGameEntityList();
            //      dbHelper.selectObjsFromDb(ZipRomFile.class, false, null, null)
            if (games!=null&&games.size()>0) {
                for (GameEntity zip :games ) {
                    File zipFile = new File(zip.path);
                    if (zipFile.exists()) {
                        zipsMap.put(zip._id, zip);
                    } else {
                        try {
                            GameEntity gas =   GameDbUtil.getInstance(). GetGameEntityService().queryBuilder().where( GameEntityDao.Properties.Zipfile_id.eq(zip._id)).unique();
                            // dbHelper.deleteObjFromDb(zip);
                            // dbHelper.deleteObjsFromDb(GameEntity.class, "where zipfile_id=" + zip._id);
                            GameDbUtil.getInstance().deleteGameEntity(gas);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            for (GameEntity game : roms) {
                if (!game.isInArchive()) {
                    File path = new File(game.path);

                    if (path.exists()) {
                        if (!hashs.contains(game.checksum)) {
                            newRoms.add(game);
                            hashs.add(game.checksum);
                        }

                    } else {
                      //  dbHelper.deleteObjFromDb(game);
                        GameDbUtil.getInstance().deleteGameEntity(game);
                    }

                } else {
                    GameEntity zip = zipsMap.get(game.zipfile_id);

                    if (zip != null) {
                        if (!hashs.contains(game.checksum)) {
                            newRoms.add(game);
                            hashs.add(game.checksum);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newRoms;
    }

    public interface OnRomsFinderListener {

        void onRomsFinderStart(boolean searchNew);

        void onRomsFinderFoundGamesInCache(ArrayList<GameEntity> oldRoms);

        void onRomsFinderFoundFile(String name);

       // void onRomsFinderZipPartStart(int countEntries);

     //   void onRomsFinderFoundZipEntry(String message, int skipEntries);

        void onRomsFinderNewGames(ArrayList<GameEntity> roms);

        void onRomsFinderEnd(boolean searchNew);

        void onRomsFinderCancel(boolean searchNew);
    }

    private class DirInfo {
        public File file;
        public int level;

        public DirInfo(File f, int level) {
            this.level = level;
            this.file = f;
        }
    }

}
