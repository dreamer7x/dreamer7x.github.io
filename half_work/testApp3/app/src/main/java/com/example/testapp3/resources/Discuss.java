package com.example.testapp3.resources;

import com.example.testapp3.TrendActivity;

import java.util.ArrayList;
import java.util.List;

public class Discuss {
    public String staticId;
    public String username;
    public String motto;
    public String headPicture;
    public String discuss;
    public int praiseNumber;
    public int replyNumber;
    public boolean isPraise;
    public boolean isReply;
    public List<Reply> reply;

    public Discuss(String staticId,String discuss,
                   int praiseNumber,int replyNumber,boolean isPraise,boolean isReply,
                   List<Reply> reply){
        this.staticId = staticId;
        this.discuss = discuss;
        this.headPicture = "";
        this.username = "";
        this.motto = "";
        this.praiseNumber = praiseNumber;
        this.replyNumber = replyNumber;
        this.isPraise = isPraise;
        this.isReply = isReply;
        this.reply = reply;
    }

    public Discuss(String staticId,String discuss,
                   int praiseNumber,int replyNumber ,boolean isPraise,boolean isReply){
        this.staticId = staticId;
        this.discuss = discuss;
        this.headPicture = "";
        this.username = "";
        this.motto = "";
        this.isPraise = isPraise;
        this.isReply = isReply;
        this.praiseNumber = praiseNumber;
        this.replyNumber = replyNumber;
        this.reply = new ArrayList<>();
    }
}
