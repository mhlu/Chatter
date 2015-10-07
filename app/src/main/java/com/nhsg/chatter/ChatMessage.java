package com.nhsg.chatter;

/**
 * Created by frank on 2015-10-04.
 */
public class ChatMessage {

    public boolean left;
    public String message;
    public String dateTime;
    public ChatMessage(Boolean left, String message, String dateTime) {
        this.left = left;
        this.message = message;
        this.dateTime = dateTime;
    }
}
