package com.twlee.bank.channel.pool;

import com.twlee.bank.channel.util.ConcurrentQueue;
import com.twlee.bank.channel.exception.SocketReadException;
import com.twlee.bank.channel.util.SocketUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TODO socket buffer에 차있을때 close 하면 어떻게 되는지? graceful 하게 종료되는지 점검
 */
public class PoolConnection {
    private static final Logger log = LoggerFactory.getLogger(PoolConnection.class);

    private final Socket socket;
    private final ExecutorService sendExecutor = Executors.newSingleThreadExecutor();
    private final ExecutorService receiveExecutor = Executors.newSingleThreadExecutor();

    private PoolConnection(String host, int port, int timeout, ConcurrentQueue<?> queue) {
        try {
            // Socket 생성
            this.socket = new Socket();
            this.socket.connect(new InetSocketAddress(host, port), timeout);
            if (isConnected()) {
                log.info("Create channel connection host={}, port={}", host, port);
                sendExecutor.execute(() -> new Sender(this.socket, queue, this));
                receiveExecutor.execute(() -> new Receiver(this.socket, this, 1024, Charset.defaultCharset()));
            }
        } catch (IOException e) {
            // TODO 소켓 연결 실패 시 재시도나 방안
            log.error("Failed to create socket client host={}, port={}", host, port);
            throw new RuntimeException("Failed to create socket client host=" + host + ", port=" + port, e);
        }
    }

    public static PoolConnection connect(String host, int port, int timeout, ConcurrentQueue<?> queue) {
        return new PoolConnection(host, port, timeout, queue);
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    private void receive(String data) {
        log.info("Received data: {}", data);
        // TODO receive data process and receive ACK send
//        channelReceiveProxy.handle(data);
//        send("ACK");
    }

    public void close() {
        try {
            receiveExecutor.shutdown();
            sendExecutor.shutdown();
            socket.close();
        } catch (Exception e) {
            log.error("Fail to close socket");
        }
    }

    /**
     * Socket Sender
     */
    private record Sender(Socket socket,
                          ConcurrentQueue<?> queue,
                          PoolConnection parent) implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                String message = "";
                try {
                    log.info("Sent message: {}", message);
                    message = (String) queue.take();
                    SocketUtil.write(socket, message.getBytes());
                } catch (SocketException e) {
                    // TODO 소켓 연결 끊김 시 방안
                    log.error("Failed to write message(broken pipe)", e);
                    throw new RuntimeException("Failed to write message(broken pipe)", e);
                } catch (IOException e) {
                    // TODO 발송 실패 시 방안
                    parent.receive(message + ":FAIL");
                    log.error("Failed to take message(io)", e);
                }
            }
        }
    }

    /**
     * Socket Receiver
     */
    private record Receiver(Socket socket,
                            PoolConnection parent,
                            int length,
                            Charset charset) implements Runnable {

        @Override
        public void run() {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    byte[] read = SocketUtil.read(socket, length);
                    String data = new String(read, charset);
                    parent.receive(data);
                } catch (SocketReadException e) {
                    log.error("Disconnected from server", e);
                    throw new RuntimeException("Disconnected from server", e);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to receive message", e);
                }
            }
        }
    }
}
