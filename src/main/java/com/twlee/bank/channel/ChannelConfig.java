package com.twlee.bank.channel;

import com.twlee.bank.channel.converter.AMessageConverter;
import com.twlee.bank.channel.pool.APoolEntry;
import com.twlee.bank.channel.pool.PoolEntry;

import java.util.List;

public record ChannelConfig(Channel channel,
                            List<ConnectInfo> connectInfos) {

    public String getChannelName() {
        return channel.getName();
    }

    public PoolEntry createPoolEntry(ConnectInfo connectInfo) {
        return switch (channel) {
            case A -> new APoolEntry(connectInfo.id(), connectInfo.password(), connectInfo.host(), connectInfo.port(), connectInfo.tps(), new AMessageConverter());
            default -> throw new IllegalArgumentException("Unsupported channel: " + channel);
        };
    }
}
