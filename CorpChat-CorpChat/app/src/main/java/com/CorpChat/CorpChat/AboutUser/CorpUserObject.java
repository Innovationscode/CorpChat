package com.CorpChat.CorpChat.AboutUser;

import java.io.Serializable;

public class CorpUserObject implements Serializable {

    private String userID,
            userName,
            phoneNumber,
                    notificationID;

    private Boolean selected = false;

    public CorpUserObject(String uid){
        this.userID = uid;
    }
    public CorpUserObject(String uid, String name, String phone){
        this.userID = uid;
        this.userName = name;
        this.phoneNumber = phone;
    }

    public String getUserID() {
        return userID;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
    public String getUserName() {
        return userName;
    }
    public String getNotificationID() {
        return notificationID;
    }
    public Boolean getSelected() {
        return selected;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }
    public void setSelected(Boolean selected) {
        this.selected = selected;
    }
}
