package com.qizhuo.framework.utils;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.blankj.utilcode.util.ViewUtils.runOnUiThread;

public class DownloadFileUtil {
    /**
     * 下载指定路径的文件，并写入到指定的位置
     *
     * @param dirName
     * @param fileName
     * @param urlStr
     * @return 返回0表示下载成功，返回1表示下载出错
     */
    public int downloadFile(String dirName, String fileName, String urlStr) {
        OutputStream output = null;
        try {
            //将字符串形式的path,转换成一个url
            URL url = new URL(urlStr);
            //得到url之后，将要开始连接网络，以为是连接网络的具体代码
            //首先，实例化一个HTTP连接对象conn
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            //定义请求方式为GET，其中GET的大小写不要搞错了。
            conn.setRequestMethod("GET");
            //定义请求时间，在ANDROID中最好是不好超过10秒。否则将被系统回收。
            conn.setConnectTimeout(6 * 1000);
            //请求成功之后，服务器会返回一个响应码。如果是GET方式请求，服务器返回的响应码是200，post请求服务器返回的响应码是206（貌似）。
            if (conn.getResponseCode() == 200) {
                //返回码为真
                //从服务器传递过来数据，是一个输入的动作。定义一个输入流，获取从服务器返回的数据
                InputStream input = conn.getInputStream();
                File file = createFile(dirName + fileName);
                output = new FileOutputStream(file);
                //读取大文件
                byte[] buffer = new byte[1024];
                //记录读取内容
                int n = input.read(buffer);
                //写入文件
                output.write(buffer, 0, n);

                n = input.read(buffer);
            }
            output.flush();
            output.close();
        }
     catch (MalformedURLException e) {
        e.printStackTrace();
    } catch (Exception e) {
        e.printStackTrace();
    } finally {
        try {
            output.close();
            System.out.println("success");
            return 0;
        } catch (IOException e) {
            System.out.println("fail");
            e.printStackTrace();
        }
    }
        return 1;
}


    /**
     * 在SD卡的指定目录上创建文件
     *
     * @param fileName
     */
    public File createFile(String fileName) {
        File file = new File(fileName);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }






}
