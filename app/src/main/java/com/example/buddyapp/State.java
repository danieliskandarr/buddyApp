package com.example.buddyapp;

public class State {
    private String name;
    private int count;

    public State(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public int getCount() {
        return count;
    }
}