package com.example.justbootupffs.Service;

import com.example.justbootupffs.Entity.Message;

public class MessageService {
    private Message message;

    private final static String set = "1";

    public MessageService(){
    }

    public MessageService(Message message) {
        this.message = message;
    }

    public void addRecipient(String id) {
        this.message.recipients.put(id, set);
    }

    public void setText(String text) {
        this.message.text = text;
    }

    public void setSender(String sender) {
        this.message.sender = sender;
    }

    public String getText() {
        return this.message.text;
    }

    public String getSender() {
        return this.message.sender;
    }
}
