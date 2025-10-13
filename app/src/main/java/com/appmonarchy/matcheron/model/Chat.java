package com.appmonarchy.matcheron.model;

public class Chat {
    String senderId, createdAt, mess, readStt, media;

    public Chat(String senderId, String createdAt, String mess, String readStt, String media) {
        this.senderId = senderId;
        this.createdAt = createdAt;
        this.mess = mess;
        this.readStt = readStt;
        this.media = media;
    }

    public String getMedia() {
        return media;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public String getMess() {
        return mess;
    }

    public String getReadStt() {
        return readStt;
    }
}
