package com.appmonarchy.matcheron.model;

import java.io.Serializable;

public class Messages implements Serializable {
    String sId, rId, userName, msg, pic, readStt, time, createdAt;

    public Messages(String sId, String rId, String userName, String msg, String pic, String readStt, String time, String createdAt) {
        this.sId = sId;
        this.rId = rId;
        this.userName = userName;
        this.msg = msg;
        this.pic = pic;
        this.readStt = readStt;
        this.time = time;
        this.createdAt = createdAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setReadStt(String readStt) {
        this.readStt = readStt;
    }

    public String getsId() {
        return sId;
    }

    public String getrId() {
        return rId;
    }

    public String getUserName() {
        return userName;
    }

    public String getMsg() {
        return msg;
    }

    public String getPic() {
        return pic;
    }

    public String getReadStt() {
        return readStt;
    }

    public String getTime() {
        return time;
    }
}
