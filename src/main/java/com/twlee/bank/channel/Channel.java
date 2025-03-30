package com.twlee.bank.channel;

public enum Channel {
    A("A"),
    B("B"),
    C("C");

    private final String name;

    Channel(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}