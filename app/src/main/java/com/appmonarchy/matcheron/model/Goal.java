package com.appmonarchy.matcheron.model;

public class Goal {
    String id, name;

    public Goal(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
