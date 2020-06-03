package com.CorpChat.CorpChat.CorpChat;

import com.CorpChat.CorpChat.AboutUser.CorpUserObject;

import java.io.Serializable;
import java.util.ArrayList;

public class ChatObject implements Serializable {
    private String chatKey;

    private ArrayList<CorpUserObject> corpUserObjectArrayList = new ArrayList<>();

    public ChatObject(String chatId){
        this.chatKey = chatId;
    }

    public String getChatKey() {
        return chatKey;
    }
    public ArrayList<CorpUserObject> getCorpUserObjectArrayList() {
        return corpUserObjectArrayList;
    }




    public void addUserToArrayList(CorpUserObject mUser){
        corpUserObjectArrayList.add(mUser);
    }
}
