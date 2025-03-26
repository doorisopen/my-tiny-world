package com.twlee.bank.common.infra;

import com.twlee.bank.common.application.LockManager;
import com.twlee.bank.common.domain.LockKey;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@Component
public class RedissonLockManager implements LockManager {
    private final RedissonClient redissonClient;

    public RedissonLockManager(RedissonClient redissonClient) {
        this.redissonClient = redissonClient;
    }

    @Override
    public Lock getLock(LockKey lockKey, long waitTime, long leaseTime, TimeUnit timeUnit) throws InterruptedException {
        RLock lock = redissonClient.getLock(lockKey.value());
        boolean available = lock.tryLock(waitTime, leaseTime, timeUnit);
        if (!available) {
            return null;
        }
        return lock;
    }

    @Override
    public void releaseLock(Lock lock) {
        try {
            lock.unlock();
        } catch (IllegalMonitorStateException e) {
            log.info("Redisson Lock Release Fail");
        }
    }
}
