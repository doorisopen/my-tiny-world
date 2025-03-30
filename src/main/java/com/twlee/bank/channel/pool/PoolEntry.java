package com.twlee.bank.channel.pool;

public interface PoolEntry {
    void send(String data);
    boolean isAvailable();
    void close();
}