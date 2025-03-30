package com.twlee.bank.channel;

public record ConnectInfo(
        String id,
        String password,
        String host,
        int port,
        int tps) {
}