package com.example.myapplication.comments;

public class PublicBean {
    private String id;
    private String username;
    private String content;
    private String comment="";//评论内容
    private String time;
    private int count;//点赞数
    private String photo;//图片地址
    private String headurl;

    public void setHeadurl(String headurl) {
        this.headurl = headurl;
    }

    public String getHeadurl() {
        return headurl;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getPhoto() {
        return photo;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getContent() {
        return content;
    }

    public String getComment() {
        return comment;
    }

    public int getCount() {
        return count;
    }

    public String getTime() {
        return time;
    }

    public String getUsername() {
        return username;
    }
}
