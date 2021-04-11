package com.qizhuo.framework.utils;



        import android.os.Environment;

        import java.io.File;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.OutputStream;

/**
 * Created by Administrator on 2016/9/14.
 */
public class DownFileUtils {

    private String SDCardRoot;

    public DownFileUtils(){
        //得到当前外部存储设备的目录
        SDCardRoot= Environment.getExternalStorageDirectory()+File.separator;
        //File.separator为文件分隔符”/“,方便之后在目录下创建文件
    }

    //在SD卡上创建文件
    public File createFileInSDCard(String fileName,String dir) throws IOException {
        File file=new File(SDCardRoot+dir+File.separator+fileName);
        file.createNewFile();
        return file;
    }

    //在SD卡上创建目录
    public File createSDDir(String dir)throws IOException{
        File dirFile=new File(SDCardRoot+dir);
        dirFile.mkdir();//mkdir()只能创建一层文件目录，mkdirs()可以创建多层文件目录
        return dirFile;
    }

    //判断文件是否存在
    public boolean isFileExist(String fileName,String dir){
        File file=new File(SDCardRoot+dir+File.separator+fileName);
        return file.exists();
    }

    //将一个InoutStream里面的数据写入到SD卡中
    public File write2SDFromInput(String fileName,String dir,InputStream input){
        File file=null;
        OutputStream output=null;
        try {
            //创建目录
            createSDDir(dir);
            //创建文件
            file=createFileInSDCard(fileName,dir);
            //写数据流
            output=new FileOutputStream(file);
            byte buffer[]=new byte[4*1024];//每次存4K
            int temp;
            //写入数据
            while((temp=input.read(buffer))!=-1){
                output.write(buffer,0,temp);
            }
            output.flush();
        } catch (Exception e) {
            System.out.println("写数据异常："+e);
        }
        finally{
            try {
                output.close();
            } catch (Exception e2) {
                System.out.println(e2);
            }
        }
        return file;
    }

}