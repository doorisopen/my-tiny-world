package com.twlee.bank.common.application;

import com.twlee.bank.common.domain.LockKey;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public interface LockManager {
    Lock getLock(LockKey lockKey, long waitTime, long releaseTime, TimeUnit timeUnit) throws InterruptedException;

    void releaseLock(Lock key);
}
