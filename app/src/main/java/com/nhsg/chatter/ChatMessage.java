package com.nhsg.chatter;

/**
 * Created by frank on 2015-10-04.
 */
public class ChatMessage {

    public boolean left;
    public String message;
    public ChatMessage(Boolean left, String message) {
        this.left = left;
        this.message = message;
    }
}
