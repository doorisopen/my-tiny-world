package com.twlee.bank.channel;

public record ConnectInfo(
        Integer poolNo,
        String id,
        String password,
        String host,
        int port,
        int tps) {
}
