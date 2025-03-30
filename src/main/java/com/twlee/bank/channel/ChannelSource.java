package com.twlee.bank.channel;

public interface ChannelSource extends AutoCloseable {
    void send(String data);
}