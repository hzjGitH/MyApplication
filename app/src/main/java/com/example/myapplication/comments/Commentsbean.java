package com.example.myapplication.comments;

public class Commentsbean {
    private String content;
    private String replyuser;//回复人
    private  String commentsuser;//评论人

    public String getCommentsuser() {
        return commentsuser;
    }

    public String getContent() {
        return content;
    }

    public String getReplyuser() {
        return replyuser;
    }

    public void setCommentsuser(String commentsuser) {
        this.commentsuser = commentsuser;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setReplyuser(String replyuser) {
        this.replyuser = replyuser;
    }
}
