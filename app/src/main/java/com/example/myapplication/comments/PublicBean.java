package com.example.myapplication.comments;

public class PublicBean {
    private String id;
    private String username;
    private String content;
    private String comment=null;//评论内容
    private String time;
    private int count;//点赞数
    private String photo;//图片地址

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
