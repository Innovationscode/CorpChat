package com.CorpChat.CorpChat.CorpChat;

import java.util.ArrayList;

public class MsgObject {

    String chatMsgId,
            msgSenderId,
            msgText;

    ArrayList<String> imageUrlList;

    public MsgObject(String chatMsgId, String msgSenderId, String msgText, ArrayList<String> imageUrlList){
        this.chatMsgId = chatMsgId;
        this.msgSenderId = msgSenderId;
        this.msgText = msgText;
        this.imageUrlList = imageUrlList;
    }

    public String getChatMsgId() {
        return chatMsgId;
    }
    public String getMsgSenderId() {
        return msgSenderId;
    }
    public String getMsgText() {
        return msgText;
    }
    public ArrayList<String> getImageUrlList() {
        return imageUrlList;
    }
}
