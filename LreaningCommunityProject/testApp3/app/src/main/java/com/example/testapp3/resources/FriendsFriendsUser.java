package com.example.testapp3.resources;

public class FriendsFriendsUser {
    public String staticId;
    public String username;
    public String motto;
    public String headPicturePosition;
    public boolean isFriend;
    public String introduce;

    public FriendsFriendsUser(String staticId,boolean isFriend){
        this.staticId = staticId;
        this.username = "";
        this.motto = "";
        this.headPicturePosition = "";
        this.isFriend = isFriend;
        this.introduce = "";
    }
}
