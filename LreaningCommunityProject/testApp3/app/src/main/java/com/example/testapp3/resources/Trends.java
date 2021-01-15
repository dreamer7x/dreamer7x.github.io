package com.example.testapp3.resources;

import java.util.List;

public class Trends {
    public String trendsId;
    public String staticId;
    public String username;
    public String motto;
    public String headPicturePosition;
    public String title;
    public String text;
    public String picture;
    public boolean isPraise;
    public boolean isDiscuss;
    public int praiseNumber;
    public int discussNumber;
    public String discuss;

    public Trends(String trendsId){
        this.trendsId = trendsId;
        staticId = "";
        username = "";
        motto = "";
        headPicturePosition = "";
        title = "";
        text = "";
        picture = "";
        praiseNumber = 0;
        discussNumber = 0;
        discuss = "";
        isPraise = false;
        isDiscuss = false;
    }
}
