package com.bxtore.dev.bxt.Objects;

import java.io.Serializable;

/**
 * Created by Deepak Prasad on 30-09-2018.
 */

public class MsgItem implements Serializable {
    String to;
    String from;
    String msgId;
    String msg;
    public MsgItem(){}

    public MsgItem(String msgId, String to, String from, String msg){
        this.msgId = msgId;
        this.from = from;
        this.to = to;
        this.msg = msg;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
