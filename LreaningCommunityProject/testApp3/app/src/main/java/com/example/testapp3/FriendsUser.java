package com.example.testapp3;

public class FriendsUser {
    public String staticId;
    public String isDrivingPassive;
    public String username;
    public String motto;
    public String picture;
    public String newChat;
    public String oldChat;
    public int newChatNumber;

    public FriendsUser(String staticId,String isDrivingPassive,String  username,String motto,String picture){
        this.staticId = staticId;
        this.isDrivingPassive = isDrivingPassive;
        this.username = username;
        this.motto = motto;
        this.picture = picture;
        newChat = "";
        oldChat = "";
        newChatNumber = 0;
    }
}
