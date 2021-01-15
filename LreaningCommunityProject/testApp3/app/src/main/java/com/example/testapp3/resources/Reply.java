package com.example.testapp3.resources;

public class Reply {
    public String staticId;
    public String username;
    public String replyString;
    public boolean isPraise;
    public int praiseNumber;

    public Reply(String staticId,String replyString,int praiseNumber,boolean isPraise){
        this.staticId = staticId;
        this.username = "";
        this.replyString = replyString;
        this.praiseNumber = praiseNumber;
        this.isPraise = isPraise;
    }
}
