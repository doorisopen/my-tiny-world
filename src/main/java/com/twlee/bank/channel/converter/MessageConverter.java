package com.twlee.bank.channel.converter;

public interface MessageConverter {
    void auth();
    void ping();
    void send();
    void ack();
}
