package com.twlee.bank.channel.pool;

import com.twlee.bank.channel.converter.MessageConverter;
import com.twlee.bank.channel.dto.ChannelRequest;
import com.twlee.bank.channel.dto.ChannelResponse;
import com.twlee.bank.channel.exception.ChannelCommunicationException;
import com.twlee.bank.channel.exception.ChannelConnectionException;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractPoolEntry implements PoolEntry {
    private final ChannelConnection connection;
    protected final MessageConverter messageConverter;
    private volatile boolean active = true;
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final AtomicInteger counter = new AtomicInteger(0);
    private volatile long lastWriteTime;
    private volatile ScheduledFuture<?> pingTask;
    private final long pingIntervalMs;

    public AbstractPoolEntry(ChannelConnection connection, MessageConverter messageConverter, long pingIntervalMs) {
        this.connection = connection;
        this.messageConverter = messageConverter;
        this.pingIntervalMs = pingIntervalMs;
    }

    public void activate() throws ChannelConnectionException {
        connection.connect();
        sendAuth();
        // 주기적인 ping 설정
        startPingScheduler();
    }

    public void deactivate() {
        active = false;
        stopPingScheduler();
        connection.disconnect();
    }

    public boolean isActive() {
        return active && connection.isConnected();
    }

    public <T extends ChannelResponse> T executeRequest(ChannelRequest request, Class<T> responseType) throws ChannelCommunicationException {
        if (!isActive()) {
            throw new ChannelCommunicationException("Channel is not active");
        }

        connection.sendRequest(request);
        return connection.receiveResponse(responseType);
    }

    private void sendAuth() {
        try {
            // 채널별 Auth 요청 생성 및 전송 로직
            ChannelRequest authRequest = createAuthRequest();
            connection.sendRequest(authRequest);
            // 필요시 응답 처리
        } catch (Exception e) {
            // 로깅 처리
            // 연결 상태 확인 및 필요시 재연결
            try {
                connection.connect();
            } catch (ChannelConnectionException ex) {
                // 로깅 처리
            }
        }
    }

    private void startPingScheduler() {
        if (pingIntervalMs > 0) {
            pingTask = scheduler.scheduleAtFixedRate(this::sendPing, pingIntervalMs, pingIntervalMs, TimeUnit.MILLISECONDS);
        }
    }

    private void stopPingScheduler() {
        if (pingTask != null) {
            pingTask.cancel(false);
            pingTask = null;
        }
    }

    private void sendPing() {
        try {
            // 채널별 ping 요청 생성 및 전송 로직
            ChannelRequest pingRequest = createPingRequest();
            connection.sendRequest(pingRequest);
            // 필요시 응답 처리
        } catch (Exception e) {
            // 로깅 처리
            // 연결 상태 확인 및 필요시 재연결
            try {
                connection.connect();
            } catch (ChannelConnectionException ex) {
                // 로깅 처리
            }
        }
    }

    protected abstract ChannelRequest createAuthRequest();
    protected abstract ChannelRequest createPingRequest();
}
