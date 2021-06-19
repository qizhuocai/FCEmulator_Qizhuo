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
import java.util.regex.Pattern;
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
    }

    private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte

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

    private static Pattern FilePattern = Pattern.compile("[\\\\/:*?\"<>|]");

    /**
     * 路径遍历 漏洞修复
     * @param str
     * @return
     */
    public static String filenameFilter(String str) {
        return str==null?null:FilePattern.matcher(str).replaceAll("");
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
               String strfile=outPathString  ;
               // if (zipEntry.isDirectory()) {
                 File f = new File( strfile);
                String canonicalPath = f.getCanonicalPath();
                String strfiless=canonicalPath+ File.separator;
          //   if (filenameFilter(szName)==null) {
                  if (!strfiless.equals(strfile)) {
                    // get the folder name of the widget
              //    szName = szName.substring(0, szName.length() - 1);
                //    szName = szName.substring(0, szName.length());
                    File folder = new File(outPathString + File.separator + szName);
                    //File folder = new File(outPathString + szName);
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
