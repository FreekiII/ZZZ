package com.example.justbootupffs.Entity;

import java.util.HashMap;

//POJO Class for messages
public class Message {
    public String text, sender;
    public HashMap<String, String> recipients;

    public Message() {
    }

    public Message(String text, String sender) {
        this.text = text;
        this.sender = sender;
    }
}
