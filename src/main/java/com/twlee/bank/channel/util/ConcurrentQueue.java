package com.twlee.bank.channel.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class ConcurrentQueue<T> {
    private static final Logger log = LoggerFactory.getLogger(ConcurrentQueue.class);

    private final BlockingQueue<T> queue;

    public ConcurrentQueue(int tps) {
        this.queue = new ArrayBlockingQueue<>(tps);
    }

    public void put(T data) {
        try {
            queue.put(data);
        } catch (InterruptedException e) {
            log.info("Failed to put message(interrupted)", e);
        }
    }

    public T take() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            log.info("Failed to take message(interrupted)", e);
        }
        return null;
    }
}
