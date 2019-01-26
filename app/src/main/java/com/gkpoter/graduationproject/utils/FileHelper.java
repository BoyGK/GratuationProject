package com.gkpoter.graduationproject.utils;

/**
 * Created by nullpointexception0 on 2017/7/31.
 */

import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;

public class FileHelper {

    /*
     * 定义文件保存的方法，写入到文件中，所以是输出流
     * */
    public void save(String name, String content){

        try {
            // 创建指定路径的文件
            File file = new File(Environment.getExternalStorageDirectory(), name);
            if (file.exists()&&!content.equals("]")) {
                content = "," + content.substring(1, content.length());
            }
            // 获取文件的输出流对象
            FileOutputStream outStream = new FileOutputStream(file, true);
            // 获取字符串对象的byte数组并写入文件流
            outStream.write(content.getBytes());
            // 最后关闭文件输出流
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /*
     * 定义文件读取的方法
     * */
    public File read(String filename) {
        File file = new File(Environment.getExternalStorageDirectory(), filename);
        return file;
    }

    public void clear(String filename){
        File file = new File(Environment.getExternalStorageDirectory(), filename);
        file.delete();
    }

}
