package com.ahsan.a27smanthademo;


public class SentPicture {

    private boolean viewed;
    private String fromUser;
    private String toUser;
    private String imageLocation;

    public SentPicture(){

        //Initializing variables in constructor
        viewed = false;
        fromUser = "";
        toUser = "";
        imageLocation = "";
    }



    public boolean isViewed(){
        return viewed;
    }
    public void setViewed(boolean wasViewed){
        viewed = wasViewed;
    }




    public String getToUser(){
        return toUser;
    }
    public void setToUser(String user){
        toUser = user;
    }




    public String getFromUser(){
        return fromUser;
    }
    public void setFromUser(String user){
        fromUser = user;
    }





    public String getImageLocation(){
        return imageLocation;
    }
    public void setImageLocation(String location){
        imageLocation = location;
    }

}
