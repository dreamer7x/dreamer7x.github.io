package com.example.testapp3.resources;

public class AttentionsFansUser {

    public String staticId;
    public String username;
    public String motto;
    public String headPicturePosition;

    public AttentionsFansUser(String staticId){
        this.username = "";
        this.staticId = staticId;
        this.motto = "";
        this.headPicturePosition = "";
    }
}
