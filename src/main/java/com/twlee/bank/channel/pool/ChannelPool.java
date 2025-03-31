package com.twlee.bank.channel.pool;

import com.twlee.bank.channel.ChannelConfig;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.*;

import static com.twlee.bank.channel.util.ThreadUtil.sleep;

public class ChannelPool implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger(ChannelPool.class);

    private final CopyOnWriteArrayList<PoolEntry> poolEntries; // CopyOnWriteArrayList TODO 동시성 점검 -> currentBag
    private final ScheduledThreadPoolExecutor poolKeepingExecutorService;

    public ChannelPool(ChannelConfig channelConfig) {
        ThreadFactory threadFactory = new DefaultThreadFactory("Channel poolKeeper (pool " + channelConfig.getChannelName() + ")", true);
        this.poolKeepingExecutorService = new ScheduledThreadPoolExecutor(1, threadFactory, new ThreadPoolExecutor.DiscardPolicy());
        this.poolKeepingExecutorService.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        this.poolKeepingExecutorService.setRemoveOnCancelPolicy(true);
        this.poolKeepingExecutorService.scheduleAtFixedRate(new PoolKeeper(), 3000, 10_000, TimeUnit.MILLISECONDS);

        // create pool entries
        this.poolEntries = channelConfig.connectInfos()
                .stream()
                .map(channelConfig::createPoolEntry)
                .collect(CopyOnWriteArrayList::new, CopyOnWriteArrayList::add, CopyOnWriteArrayList::addAll);
    }

    /**
     * API
     */
    public void send(String data) {
        // Search available pool entry(check connection and tps)
        for (PoolEntry poolEntry : poolEntries) {
            if (poolEntry.isAvailable()) {
                poolEntry.send(data);
                return;
            }
        }

        // TODO: entries의 TPS가 전부 초과된 경우
        log.info("PoolEntries is not available. out of tps sleep 1s");
        for (PoolEntry poolEntry : poolEntries) {
            // TODO sendCount 초기화
            //  poolEntry.initSendCount();
        }
        sleep(1000L);
    }

    /**
     * TODO graceful shutdown 검토
     */
    @Override
    public void close() {
        for (PoolEntry poolEntry : poolEntries) {
            poolEntry.close();
        }
        poolKeepingExecutorService.shutdown();
    }

    /**
     * TODO 풀 커넥션 PING 체크
     * TODO 풀 커넥션 재연결
     */
    private class PoolKeeper implements Runnable {

        @Override
        public void run() {
            for (PoolEntry poolEntry : poolEntries) {
                if (!poolEntry.isAvailable()) {
//                    log.info("PoolEntry is not available. host={}, port={}", poolEntry.getHost(), poolEntry.getPort());
                }
            }

        }
    }

    /**
     * TODO 풀 동기화
     * TODO TPS 초기화
     *
     *  - ip, port, cid 정보로 찾아서 close
     * TODO add entry
     *  - 커넥션 추가
     */
}
