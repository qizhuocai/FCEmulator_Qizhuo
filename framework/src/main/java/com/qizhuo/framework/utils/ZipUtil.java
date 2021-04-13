package com.qizhuo.framework.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.RequiresApi;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtil  {
    private static final String SD_PATH = Environment.getExternalStorageDirectory().getPath();
   // private static final String SD_PATH = Environment.getDataDirectory().getPath();
    //Environment.getExternalStorageDirectory()
    static  String versionstr="version:1.33";
    public static void Init(Context context) {
        try {
            FileUtils.getInstance(context).copyAssetsToSD("fcgamezip","fcgamezip");
            FileUtils.getInstance(context).
                    setFileOperateCallback(new FileUtils.FileOperateCallback() {
                        @Override
                        public void onSuccess () {
                            FileOutputStream fout = null;
                            try {

                                fout = new FileOutputStream(SD_PATH + "/fcgamezip/flag");
                                byte[] bytes = versionstr.getBytes();
                                fout.write(bytes);
                                fout.close();
                                try {
                                    unzipFileer();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onFailed (String error){

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static String TAG="ZipUtil";
    public static boolean checkInit() {
        FileInputStream fin = null;
        try {
            fin = new FileInputStream(SD_PATH + "/fcgamezip/flag");
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            String res = new String(buffer, StandardCharsets.UTF_8);
            fin.close();
            return versionstr.equals(res);
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * 解压到指定路径
     *
     * @param inputStream
     * @param outPathString
     * @throws Exception
     */
    public static void UnZipFoldera(InputStream inputStream, String outPathString) throws Exception {
        ZipInputStream inZip = new ZipInputStream(inputStream);
        ZipEntry zipEntry;
        String szName = "";
        File root = new File(outPathString);
        if (!root.exists()) {
            root.mkdir();
        }
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                //获取部件的文件夹名
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {
                Log.i("tag", outPathString + File.separator + szName);
                File file = new File(outPathString + File.separator + szName);
                if (!file.exists()) {
                    Log.i("tag", "Create the file:" + outPathString + File.separator + szName);
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                // 获取文件的输出流
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[2048];
                // 读取（字节）字节到缓冲区
                while ((len = inZip.read(buffer)) != -1) {
                    // 从缓冲区（0）位置写入（字节）字节
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }
    /**
     * 解压缩
     * 将zipFile文件解压到folderPath目录下.
     * @param zipFile zip文件
     * @param folderPath 解压到的地址
     * @throws IOException
     */
    private  void upZipFile(File zipFile, String folderPath)  {
        try {
            ZipFile zfile = null;
            try {
                zfile = new ZipFile(zipFile);
                // String fileEncode = EncodeUtil.getEncode(zipFile);
                //      zfile= new ZipFile(new File(zipFile), Charset.forName(fileEncode));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Enumeration zList = zfile.entries();
            ZipEntry ze = null;
            byte[] buf = new byte[1024];
            while (zList.hasMoreElements()) {
                ze = (ZipEntry) zList.nextElement();
                if (ze.isDirectory()) {
                    Log.d(TAG, "ze.getName() = " + ze.getName());
                    String dirstr = folderPath + ze.getName();
                    dirstr = new String(dirstr.getBytes("8859_1"), "GB2312");
                    Log.d(TAG, "str = " + dirstr);
                    File f = new File(dirstr);
                    f.mkdir();
                    continue;
                }
                Log.d(TAG, "ze.getName() = " + ze.getName());
                OutputStream os = new BufferedOutputStream(new FileOutputStream(getRealFileName(folderPath, ze.getName())));
                InputStream is = new BufferedInputStream(zfile.getInputStream(ze));
                int readLen = 0;
                while ((readLen = is.read(buf, 0, 1024)) != -1) {
                    os.write(buf, 0, readLen);
                }
                is.close();
                os.close();
            }
            zfile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /**
     * 给定根目录，返回一个相对路径所对应的实际文件名.
     * @param baseDir     指定根目录
     * @param absFileName 相对路径名，来自于ZipEntry中的name
     * @return java.io.File 实际的文件
     */
    public File getRealFileName(String baseDir, String absFileName) {
        String[] dirs = absFileName.split("/");
        File ret = new File(baseDir);
        String substr = null;
        if (dirs.length > 1) {
            for (int i = 0; i < dirs.length - 1; i++) {
                substr = dirs[i];
                try {
                    substr = new String(substr.getBytes("8859_1"), "GB2312");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                ret = new File(ret, substr);

            }
            Log.d(TAG, "1ret = " + ret);
            if (!ret.exists()) {
                ret.mkdirs();
            }
            substr = dirs[dirs.length - 1];
            try {
                substr = new String(substr.getBytes("8859_1"), "GB2312");
                Log.d(TAG, "substr = " + substr);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            ret = new File(ret, substr);
            Log.d(TAG, "2ret = " + ret);
            return ret;
        }
        return ret;
    }

    public static void unzipFileer()  {
        // 打开压缩文件
        //   InputStream inputStream = new FileInputStream(SD_PATH + "/fcgamezip/Classified.zip");
        try {
            // UnZipFolderzip(SD_PATH + "/fcgamezip/Classified.zip",SD_PATH + "/fcgamedata/");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                UnZipFolderzip(SD_PATH + "/fcgamezip/Classified.zip",SD_PATH + "/fcgamezip/");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // upZipFile(new File(SD_PATH + "/fcgamezip/Classified.zip"),SD_PATH + "/fcgamedata/");
        //   UnZipFoldera(inputStream,SD_PATH + "/fcgamedata/");
    }
    public static void unzipFile(String zipPtath, String outputDirectory)throws IOException {
        /**
         * 解压assets的zip压缩文件到指定目录
         * @param context上下文对象
         * @param assetName压缩文件名
         * @param outputDirectory输出目录
         * @param isReWrite是否覆盖
         * @throws IOException
         */
        Log.i(TAG,"开始解压的文件： "  + zipPtath + "\n" + "解压的目标路径：" + outputDirectory );
        // 创建解压目标目录
        File file = new File(outputDirectory);
        // 如果目标目录不存在，则创建
        if (!file.exists()) {
            file.mkdirs();
        }
        // 打开压缩文件
        InputStream inputStream = new FileInputStream(zipPtath); ;
        ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        // 读取一个进入点
        ZipEntry zipEntry = zipInputStream.getNextEntry();
        // 使用1Mbuffer
        byte[] buffer = new byte[1024 * 1024];
        // 解压时字节计数
        int count = 0;
        // 如果进入点为空说明已经遍历完所有压缩包中文件和目录
        while (zipEntry != null) {
            Log.i(TAG,"解压文件 入口 1： " +zipEntry );
            if (!zipEntry.isDirectory()) {  //如果是一个文件
                // 如果是文件
                String fileName = zipEntry.getName();
                Log.i(TAG,"解压文件 原来 文件的位置： " + fileName);
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);  //截取文件的名字 去掉原文件夹名字
                Log.i(TAG,"解压文件 的名字： " + fileName);
                file = new File(outputDirectory + File.separator + fileName);  //放到新的解压的文件路径

                file.createNewFile();
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                while ((count = zipInputStream.read(buffer)) > 0) {
                    fileOutputStream.write(buffer, 0, count);
                }
                fileOutputStream.close();
            }
            // 定位到下一个文件入口
            zipEntry = zipInputStream.getNextEntry();
            Log.i(TAG,"解压文件 入口 2： " + zipEntry );
        }
        zipInputStream.close();
        Log.i(TAG,"解压完成");

    }
    /**
     * 解压zip到指定的路径
     *
     * @param zipFileString ZIP的名称
     * @param outPathString 要解压缩路径
     * @throws Exception
     */
    public static void UnZipFolder(String zipFileString, String outPathString) throws Exception {
        ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString));
        ZipEntry zipEntry;
        String szName = "";
        while ((zipEntry = inZip.getNextEntry()) != null) {
            szName = zipEntry.getName();
            if (zipEntry.isDirectory()) {
                //获取部件的文件夹名
                szName = szName.substring(0, szName.length() - 1);
                File folder = new File(outPathString + File.separator + szName);
                folder.mkdirs();
            } else {
                Log.e(TAG, outPathString + File.separator + szName);
                File file = new File(outPathString + File.separator + szName);
                if (!file.exists()) {
                    Log.e(TAG, "Create the file:" + outPathString + File.separator + szName);
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                }
                // 获取文件的输出流
                FileOutputStream out = new FileOutputStream(file);
                int len;
                byte[] buffer = new byte[1024];
                // 读取（字节）字节到缓冲区
                while ((len = inZip.read(buffer)) != -1) {
                    // 从缓冲区（0）位置写入（字节）字节
                    out.write(buffer, 0, len);
                    out.flush();
                }
                out.close();
            }
        }
        inZip.close();
    }
    private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte
    /**
     * 解压缩一个文件
     *
     * @param zipFile 压缩文件
     * @param folderPath 解压缩的目标目录
     * @throws IOException 当解压缩过程出错时抛出
     */
    public static void upZipFile2(File zipFile, String folderPath) throws ZipException, IOException {
        File desDir = new File(folderPath);
        if (!desDir.exists()) {
            desDir.mkdirs();
        }
        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
            ZipEntry entry = ((ZipEntry)entries.nextElement());
            InputStream in = zf.getInputStream(entry);
            String str = folderPath + File.separator + entry.getName();
            str = new String(str.getBytes("8859_1"), "GB2312");
            File desFile = new File(str);
            if (!desFile.exists()) {
                File fileParentDir = desFile.getParentFile();
                if (!fileParentDir.exists()) {
                    fileParentDir.mkdirs();
                }
                desFile.createNewFile();
            }
            OutputStream out = new FileOutputStream(desFile);
            byte buffer[] = new byte[BUFF_SIZE];
            int realLength;
            while ((realLength = in.read(buffer)) > 0) {
                out.write(buffer, 0, realLength);
            }
            in.close();
            out.close();
        }
    }
    public static boolean deletefile(){
        try{
            File f=new File(SD_PATH + "/fcgamezip/");//文件绝对路径
            clearFolder(f);
//            if(!f.exists()){
//                return false;
//            }
        }catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }

    public static boolean fileIsExists(){
        try{
            File f=new File(SD_PATH + "/fcgamezip/Classified.zip");//文件绝对路径
            if(!f.exists()){
                return false;
            }
        }catch (Exception e) {
            // TODO: handle exception
            return false;
        }
        return true;
    }

    /**
     * 清空文件夹里面全部子文件
     */
    private static void clearFolder(File file) {
        if(file.isDirectory()){
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                childFiles[i].delete();
            }
            return ;
        }
    }


    /**
     * DeCompress the ZIP to the path
     * @param zipFileString  name of ZIP
     * @param outPathString   path to be unZIP
     * @throws Exception
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void UnZipFolderzip(String zipFileString, String outPathString) throws Exception {
        try {

            String fileEncode = EncodeUtil.getEncode(zipFileString,true);
            ZipInputStream inZip = new ZipInputStream(new FileInputStream(zipFileString),Charset.forName(fileEncode));
            ZipEntry zipEntry;
            String szName = "";
            while ((zipEntry = inZip.getNextEntry()) != null) {
                szName = zipEntry.getName();
                if (zipEntry.isDirectory()) {
                    // get the folder name of the widget
                    szName = szName.substring(0, szName.length() - 1);
                    File folder = new File(outPathString + File.separator + szName);
                    folder.mkdirs();
                } else {

                    File file = new File(outPathString + File.separator + szName);
                    file.createNewFile();
                    // get the output stream of the file
                    FileOutputStream out = new FileOutputStream(file);
                    int len;
                    byte[] buffer = new byte[1024];
                    // read (len) bytes into buffer
                    while ((len = inZip.read(buffer)) != -1) {
                        // write (len) byte from buffer at the position 0
                        out.write(buffer, 0, len);
                        out.flush();
                    }
                    out.close();
                }
            }
            inZip.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }





}
