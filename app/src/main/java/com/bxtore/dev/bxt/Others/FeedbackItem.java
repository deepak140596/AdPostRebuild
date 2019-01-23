package com.bxtore.dev.bxt.Others;

/**
 * Created by Deepak Prasad on 22-01-2018.
 */

public class FeedbackItem {
    String id,body;
    public FeedbackItem(){}
    public FeedbackItem(String id, String body){
        this.body=body;
        this.id=id;
    }
    public String getId() {
        return id;
    }
}
