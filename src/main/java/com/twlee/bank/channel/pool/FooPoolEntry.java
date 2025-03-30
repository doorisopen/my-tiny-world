package com.twlee.bank.channel.pool;


import com.twlee.bank.channel.util.ConcurrentQueue;

import java.util.concurrent.atomic.AtomicInteger;

public class FooPoolEntry implements PoolEntry {
    private final ChannelPool parentPool;

    private final String id;
    private final String password;
    private final String host;
    private final int port;
    private final Integer tps;
    private final AtomicInteger sendCount;
    private final ConcurrentQueue<String> queue;

    // FIXME
    //  send 후에 바로 receive가 하나의 동작일 수 도 있고
    //  send, receive가 각각 따로 동작할 수 도 있고
    private PoolConnection poolConnection;

    public FooPoolEntry(ChannelPool parentPool, String host, int port, Integer tps) {
        this.id = "";
        this.password = "";
        this.host = host;
        this.port = port;
        this.tps = tps;
        this.sendCount = new AtomicInteger(0);
        this.parentPool = parentPool;
        this.queue = new ConcurrentQueue<>(tps);

        this.poolConnection = PoolConnection.connect(host, port, 1000, queue);
        // TODO TPS 초기화 관리자
        // FIXME 동시성 이슈 점검
        //  AtomicInteger에 수정이 안됨
//        tpsInitManager.scheduleAtFixedRate(() -> currentTps.set(0), 100, 1, java.util.concurrent.TimeUnit.SECONDS);
    }

    /**
     * 메시지 발송 요청
     */
    @Override
    public void send(String data) {
        this.sendCount.incrementAndGet();
        this.queue.put(data);
    }

    /**
     * TPS 초과 여부 확인
     * @return TPS 초과 여부
     */
    @Override
    public boolean isAvailable() {
        // FIXME 동시성 이슈 점검
        return sendCount.get() <= tps && poolConnection.isConnected();
    }

    public void initSendCount() {
        this.sendCount.set(0);
    }

    @Override
    public void close() {
        poolConnection.close();
    }

    @Override
    public String toString() {
        return "[host=" + host + ", port=" + port + ", tps=" + sendCount + "/" + tps + "]";
    }
}