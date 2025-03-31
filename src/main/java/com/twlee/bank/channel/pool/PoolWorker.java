package com.twlee.bank.channel.pool;

import com.twlee.bank.channel.exception.SocketReadException;
import com.twlee.bank.channel.proxy.ChannelReceiveProxy;
import com.twlee.bank.channel.util.SocketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;

public class PoolWorker {
    private static final Logger log = LoggerFactory.getLogger(PoolWorker.class);

    private final ChannelReceiveProxy channelReceiveProxy;

    public PoolWorker(ChannelReceiveProxy channelReceiveProxy) {
        this.channelReceiveProxy = channelReceiveProxy;
    }

    public void write(ConnectionWrapper connection, String data) {
        try {
            log.info("Sent data: {}", data);
            Socket socket = connection.get();
            SocketUtil.write(socket, data.getBytes());
        } catch (SocketException e) {
            // 소켓 연결 끊김 시 재처리
            channelReceiveProxy.receive(data);
            log.error("Failed to write message(broken pipe)", e);
        } catch (IOException e) {
            // 발송 실패 시 방안 재처리
            channelReceiveProxy.receive(data);
            log.error("Failed to take message(io)", e);
        }
    }

    public void read(ConnectionWrapper connection, int length, Charset charset) {
        try {
            Socket socket = connection.get();
            byte[] read = SocketUtil.read(socket, length);
            String data = new String(read, charset);
            channelReceiveProxy.receive(data);
            // TODO ACK
        } catch (SocketReadException e) {
            log.error("Disconnected from server", e);
        } catch (IOException e) {
            log.error("Failed to receive message", e);
        }
    }

    /**
     * Socket Sender
     */
    private record Sender(PoolWorker parent) implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {

            }
        }
    }

    /**
     * Socket Receiver
     */
    private record Receiver(Socket socket,
                            PoolWorker parent,
                            int length,
                            Charset charset) implements Runnable {

        @Override
        public void run() {

        }
    }
}
