package com.example.myapplication.Util;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.util.Log;

import com.example.myapplication.Bean.Localmusic;
import java.util.ArrayList;
import java.util.List;
//获取本地音乐
public class LoaclMusicUtil {
    static   List<Localmusic> localmusics;
     static String name;
     static  String songname;
    public static List<Localmusic> getmusic(Context context){

        localmusics=new ArrayList<>();
        Cursor cursor=context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                null,null,null, MediaStore.Audio.AudioColumns.IS_MUSIC);
        if (cursor!=null){
        while (cursor.moveToNext()){
            Localmusic localmusic=new Localmusic();
            name=cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME));
            if (name.contains("-")) {
                String[] str = name.split("-");
                songname = str[1];
                localmusic.setSinger(str[0]);
                int index=songname.indexOf(".");
                songname=songname.substring(0,index);
            } else {
                songname=name;
                localmusic.setSinger(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)));
            }
            localmusic.setSongname(songname);
            localmusic.setId(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)));
            localmusic.setPath(cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)));
            localmusic.setDuration(cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)));
            localmusic.setAlbumid(cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)));
            localmusics.add(localmusic);
        }
    }
        if (cursor!=null)
        cursor.close();
        Log.i("LocalMusicutil",localmusics.toString());
        return localmusics;
    }


}
