package com.twlee.bank.channel;

import java.util.List;

public record ChannelConfig(Channel channel,
                            List<ConnectInfo> connectInfos) {

    public String getChannelName() {
        return channel.getName();
    }
}