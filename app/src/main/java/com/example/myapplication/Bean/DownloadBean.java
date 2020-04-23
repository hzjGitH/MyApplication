package com.example.myapplication.Bean;

import android.widget.SeekBar;

public class DownloadBean {
    private long downloadid;//下载的id
    private  String path;//存放的绝对路径
    private String downloadname;//下载的歌曲名字
    private String url;//下载路径

    public void setPath(String path) {
        this.path = path;
    }

    public void setDownloadid(long downloadid) {
        this.downloadid = downloadid;
    }

    public void setDownloadname(String downloadname) {
        this.downloadname = downloadname;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getPath() {
        return path;
    }

    public long getDownloadid() {
        return downloadid;
    }

    public String getDownloadname() {
        return downloadname;
    }
}
