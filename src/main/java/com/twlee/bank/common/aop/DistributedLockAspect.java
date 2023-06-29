package com.twlee.bank.common.aop;

import com.twlee.bank.common.annotation.DistributedLock;
import com.twlee.bank.common.application.LockManager;
import com.twlee.bank.common.domain.LockKey;
import com.twlee.bank.common.exception.LockException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;

@Order(0)
@Slf4j
@Aspect
@Component
public class DistributedLockAspect {
    private final LockManager lockManager;

    public DistributedLockAspect(LockManager lockManager) {
        this.lockManager = lockManager;
    }

    @Around("@annotation(com.twlee.bank.common.annotation.DistributedLock)")
    public Object lock(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

        Lock lock = null;
        try {
            LockKey lockKey = LockKey.of(signature.getParameterNames(), joinPoint.getArgs(), distributedLock.key());
            lock = lockManager.getLock(lockKey, distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit());
            return joinPoint.proceed();
        } catch (InterruptedException e) {
            throw new LockException(e);
        } finally {
            if (lock != null) {
                lockManager.releaseLock(lock);
            }
        }
    }
}
