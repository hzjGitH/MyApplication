package com.example.myapplication.Bean;

public class Music {
 private    String singer;//歌手
private   String songname;//歌名
 private   String path;//路径
private String musictype;//音乐类型
private String comments;//音乐评论

    public String getPath() {
        return path;
    }

    public String getSinger() {
        return singer;
    }

    public String getSongname() {
        return songname;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }

    public void setSongname(String songname) {
        this.songname = songname;
    }

    public void setMusictype(String musictype) {
        this.musictype = musictype;
    }


    public String getMusictype() {
        return musictype;
    }



    @Override
    public String toString() {
        return "singer:"+singer+"songname:"+songname+"path:"+path+"musictype:"+musictype;
    }

    @Override
    public boolean equals(Object obj) {
        Music music=(Music) obj;
        return songname.equals(music.songname)&&singer.equals(music.singer);
    }

    @Override
    public int hashCode() {
        String in=songname;
        return in.hashCode();
    }

}
