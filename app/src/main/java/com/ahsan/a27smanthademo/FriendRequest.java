package com.ahsan.a27smanthademo;

//We had Users table in Backendless and we had the object type BackendlessUser.Class
//Now we need to create a new table in Backendless and define our class here
public class FriendRequest {

    private String toUser;
    private String fromUser;
    private boolean accepted;

    //Constructor with variables initialization
    public FriendRequest(){
        toUser = "";
        fromUser = "";
        accepted = false;
    }

    //Getters and setters for toUser
    public String getToUser(){
        return toUser;
    }
    public void setToUser(String user){
        toUser = user;
    }

    //Getters and setters for fromUser
    public String getFromUser(){
        return fromUser;
    }
    public void setFromUser(String user){
        fromUser = user;
    }

    //Getters and setters for accepted
    public boolean isAccepted(){
        return accepted;
    }
    public void setAccepted(boolean isAccepted){
        accepted = isAccepted;
    }

}



















