package com.sapicons.deepak.k2psap.Objects;

import java.io.Serializable;

/**
 * Created by Deepak Prasad on 30-09-2018.
 */

public class ChatItem implements Serializable {

    String chatId;
    String userIdOne, userIdTwo;
    String postId;

    public ChatItem(){}
    public ChatItem(String chatId,String userIdOne,String userIdTwo,String postId){
        this.chatId = chatId;
        this.userIdOne = userIdOne;
        this.userIdTwo = userIdTwo;
        this.postId = postId;
    }

    public String getChatId() {
        return chatId;
    }

    public void setChatId(String chatId) {
        this.chatId = chatId;
    }

    public String getUserIdOne() {
        return userIdOne;
    }

    public void setUserIdOne(String userIdOne) {
        this.userIdOne = userIdOne;
    }

    public String getUserIdTwo() {
        return userIdTwo;
    }

    public void setUserIdTwo(String userIdTwo) {
        this.userIdTwo = userIdTwo;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }
}
