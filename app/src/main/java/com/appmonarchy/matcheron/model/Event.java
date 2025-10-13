package com.appmonarchy.matcheron.model;

public class Event {
    String id, photo, name;

    public Event(String id, String photo, String name) {
        this.id = id;
        this.photo = photo;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getPhoto() {
        return photo;
    }

    public String getName() {
        return name;
    }
}
