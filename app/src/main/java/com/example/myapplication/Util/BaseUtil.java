package com.example.myapplication.Util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;


import java.io.FileInputStream;
import java.io.IOException;

public class BaseUtil {
//将图片以Base64方式编码为字符串
    public String encodeImage(String url){
        byte[] bytes=new byte[0];
        try {
            FileInputStream fileInputStream = new FileInputStream(url);
            bytes = new byte[fileInputStream.available()];
            fileInputStream.read(bytes);
            fileInputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return Base64.encodeToString(bytes, Base64.DEFAULT);

    }


    //将以Base64方式编码的字符串解码为byte数组
    public static byte[] decode(String str) {
        return Base64.decode(str, Base64.DEFAULT);
    }

    // 将数组转化成bitmap
    public Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

}
