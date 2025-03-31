package com.twlee.bank.channel.pool;


import com.twlee.bank.channel.converter.MessageConverter;
import com.twlee.bank.channel.dto.ChannelRequest;
import com.twlee.bank.channel.exception.ChannelConnectionException;

import java.util.concurrent.atomic.AtomicInteger;

public class APoolEntry extends AbstractPoolEntry {
    private final String id;
    private final String password;
    private final Integer tps;
    private final AtomicInteger sendCount;
    private final ConnectionWrapper connectionWrapper;

    public APoolEntry(String id, String password, String host, int port, Integer tps, MessageConverter messageConverter) {
        super(new TcpChannelConnection(host, port, 1000, 5000), messageConverter, 3000);
        this.id = id;
        this.password = password;
        this.tps = tps;
        this.sendCount = new AtomicInteger(0);
        this.connectionWrapper = new ConnectionWrapper(host, port, 1000);
        try {
            activate();
        } catch (ChannelConnectionException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected ChannelRequest createAuthRequest() {
        return null;
    }

    @Override
    protected ChannelRequest createPingRequest() {
        return null;
    }

    @Override
    public void send(String data) {
        this.sendCount.incrementAndGet();
    }

    /**
     * TPS 초과 여부 확인
     * @return TPS 초과 여부
     */
    @Override
    public boolean isAvailable() {
        // FIXME 동시성 이슈 점검
        return sendCount.get() <= tps && connectionWrapper.isConnected();
    }

    @Override
    public void close() {
        connectionWrapper.close();
    }

    @Override
    public String toString() {
        return "[" + connectionWrapper + ", tps=" + sendCount + "/" + tps + "]";
    }
}
