package com.sapicons.deepak.k2psap.Objects;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Created by Deepak Prasad on 30-09-2018.
 */

public class ChatItem implements Serializable {

    String chatId;
    String userIdOne, userIdTwo;
    String postId;
    long lastMsgTimestamp;

    public ChatItem(){}
    public ChatItem(String chatId,String userIdOne,String userIdTwo,String postId){
        this.chatId = chatId;
        this.userIdOne = userIdOne;
        this.userIdTwo = userIdTwo;
        this.postId = postId;
        this.lastMsgTimestamp = 0;
    }

    public long getLastMsgTimestamp() {
        return lastMsgTimestamp;
    }

    public void setLastMsgTimestamp(long lastMsgTimestamp) {
        this.lastMsgTimestamp = lastMsgTimestamp;
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

   public static Comparator<ChatItem> LastActiveChatComparator = new Comparator<ChatItem>() {
       @Override
       public int compare(ChatItem chatItem, ChatItem t1) {
           long timestamp1 = chatItem.getLastMsgTimestamp();
           long timestamp2 = t1.getLastMsgTimestamp();

           // latest chat first
           return (int)(timestamp2 - timestamp1);
       }
   };
}
