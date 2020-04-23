package com.example.myapplication.Bean;

public class Localmusic {
    private String songname;//歌名
    private String singer;//歌手
    private String path;//路径
    private int duration;//歌曲时长
    private long   albumid;//图片id
    private int id;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setAlbumid(long albumid) {
        this.albumid = albumid;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getSongname() {
        return songname;
    }

    public String getSinger() {
        return singer;
    }

    public String getPath() {
        return path;
    }

    public int getDuration() {
        return duration;
    }

    public long getAlbumid() {
        return albumid;
    }

    @Override
    public String toString() {
        return "localmusic:"+id+","+songname+","+ singer+","+ path+ ","+duration+ ","+albumid;
    }
}
