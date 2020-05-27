package com.example.myapplication.Util;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import com.example.myapplication.Bean.DownloadBean;
import com.example.myapplication.DownManagerFragment;
import com.example.myapplication.MainActivity;

import java.io.File;


public class AndroidDownloadManger {
   private Context context;
  private   String url;
  public static DownloadManager downloadManager;
  public static long downloadid;//下载的id
    public static  String path;//存放的绝对路径
    public static String name;
    public static  String singer;
    public static String songname;
    public static  DownloadBean downloadBean;

    public AndroidDownloadManger(Context context, String url)
    {
    this.context=context;
    this.url=url;
    name=getFileNameByUrl(url);
    }
    public void download(){
    DownloadManager.Request request=new DownloadManager.Request(Uri.parse(url));
    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE);
    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
    request.setTitle(name);
    request.setDescription("正在下载中.....");

        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), name);

        request.setDestinationUri(Uri.fromFile(file));
        path=file.getAbsolutePath();
        System.out.println("下载的路径为："+path);
        if (downloadManager==null){
        downloadManager=(DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }
    if (downloadManager!=null){
       downloadid= downloadManager.enqueue(request);
         downloadBean=new DownloadBean();
        downloadBean.setDownloadid(downloadid);
        downloadBean.setDownloadname(name);
        downloadBean.setPath(path);
        downloadBean.setUrl(url);
        MainActivity.downloadBeanList.add(downloadBean);
        if (DownManagerFragment.addDownloadAdapter!=null)
            DownManagerFragment.addDownloadAdapter.notifyDataSetChanged();
    }
   // context.registerReceiver(receiver,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }


    private static  String getFileNameByUrl(String url) {
        String filename = url.substring(url.lastIndexOf("/") + 1);
        filename = filename.substring(0, filename.indexOf("?") == -1 ? filename.length() : filename.indexOf("?"));

        return filename;
    }
public static int getDownloadPercent(long downloadid){
        DownloadManager.Query query=new DownloadManager.Query().setFilterById(downloadid);
        Cursor cursor= downloadManager.query(query);
        if (cursor.moveToFirst()){
            int downloadBytesIdx=cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR);
            int totalBytestIdx=cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES);
            long  downloadBytes=cursor.getLong(downloadBytesIdx);
            long totalBytes=cursor.getLong(totalBytestIdx);
            cursor.close();
            return (int) (downloadBytes * 100 / totalBytes);
        }
    cursor.close();
        return 0;
}

}
