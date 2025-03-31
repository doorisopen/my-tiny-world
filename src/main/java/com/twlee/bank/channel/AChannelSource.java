package com.twlee.bank.channel;

import com.twlee.bank.channel.pool.ChannelPool;

public class AChannelSource implements ChannelSource {
    private final ChannelPool channelPool1;
    private final ChannelPool channelPool2;

    public AChannelSource(ChannelConfig channelConfig1, ChannelConfig channelConfig2) {
        this.channelPool1 = new ChannelPool(channelConfig1);
        this.channelPool2 = new ChannelPool(channelConfig2);
    }

    @Override
    public void send(String data) {
        // TODO: 채널이 여러 곳이면?
        channelPool1.send(data);
    }

    @Override
    public void close() {
        channelPool1.close();
        channelPool2.close();
    }
}
