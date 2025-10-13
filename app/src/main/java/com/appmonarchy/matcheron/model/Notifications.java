package com.appmonarchy.matcheron.model;

public class Notifications {
    String id, des, stt, time, sId;

    public Notifications(String id, String des, String stt, String time, String sId) {
        this.id = id;
        this.des = des;
        this.stt = stt;
        this.time = time;
        this.sId = sId;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDes(String des) {
        this.des = des;
    }

    public void setStt(String stt) {
        this.stt = stt;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getId() {
        return id;
    }

    public String getsId() {
        return sId;
    }

    public String getDes() {
        return des;
    }

    public String getStt() {
        return stt;
    }

    public String getTime() {
        return time;
    }
}
