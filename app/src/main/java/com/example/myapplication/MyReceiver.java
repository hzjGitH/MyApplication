package com.example.myapplication;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.myapplication.Adapter.AddDownloadAdapter;
import com.example.myapplication.Util.AndroidDownloadManger;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        DownloadManager.Query query=new DownloadManager.Query();
        query.setFilterById(AndroidDownloadManger.downloadid);
        Cursor cursor=AndroidDownloadManger.downloadManager.query(query);
        if (cursor.moveToFirst()){
            int status =cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
            switch (status) {
                //下载暂停
                case DownloadManager.STATUS_PAUSED:
                    Toast.makeText(context,"下载暂停",Toast.LENGTH_SHORT).show();
                    break;
                //下载延迟
                case DownloadManager.STATUS_PENDING:
                    break;
                //正在下载
                case DownloadManager.STATUS_RUNNING:
                    Toast.makeText(context,"正在下载",Toast.LENGTH_SHORT).show();

                    break;
                //下载完成
                case DownloadManager.STATUS_SUCCESSFUL:
                    cursor.close();
                    Toast.makeText(context,"下载完成",Toast.LENGTH_SHORT).show();
                    if (AndroidDownloadManger.name.contains("-")) {
                        String[] str = AndroidDownloadManger.name.split("-");
                        AndroidDownloadManger.singer = str[0];
                        AndroidDownloadManger.songname = str[1];
                    } else {
                        AndroidDownloadManger.songname=AndroidDownloadManger.name;
                    }

                    ContentValues values=new ContentValues();
                    values.put(MediaStore.Audio.Media.DATA,AndroidDownloadManger.path);
                    values.put(MediaStore.Audio.Media.ARTIST,AndroidDownloadManger.singer);
                    values.put(MediaStore.Audio.Media.DISPLAY_NAME,AndroidDownloadManger.songname);
                    context.getContentResolver().insert(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,values);
                    MainActivity.finishdownloadlist.add(AndroidDownloadManger.downloadBean);
                    MainActivity.downloadBeanList.remove(AndroidDownloadManger.downloadBean);
                    DownManagerFragment.addDownloadAdapter.notifyDataSetChanged();
                    FinishDownloadFragment.finishdownloadAdapter.notifyDataSetChanged();
                    AddDownloadAdapter.downloadtimer.cancel();
                    break;
                //下载失败
                case DownloadManager.STATUS_FAILED:
                    Toast.makeText(context,"下载失败",Toast.LENGTH_SHORT).show();
                    cursor.close();

                    break;
            }
        }

    }
}
