package com.twlee.bank.channel.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ConnectionWrapper {
    private static final Logger log = LoggerFactory.getLogger(ConnectionWrapper.class);

    private final String host;
    private final int port;
    private final int timeout;

    private Socket socket;

    public ConnectionWrapper(String host, int port, int timeout) {
        this.host = host;
        this.port = port;
        this.timeout = timeout;

        int connectCount = 3;
        boolean isConnected;
        do {
            isConnected = connect();
            connectCount--;
        } while (!isConnected && connectCount > 0);
    }

    public boolean connect() {
        // if socket is already connected
        close();
        try {
            // Socket 생성
            this.socket = new Socket();
            this.socket.connect(new InetSocketAddress(host, port), timeout);
            if (isConnected()) {
                log.info("Create channel connection host={}, port={}", host, port);
                return true;
            }
        } catch (IOException e) {
            // TODO 소켓 연결 실패 시 재시도나 방안
            log.error("Failed to create socket client host={}, port={}", host, port, e);
        }
        return false;
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected();
    }

    public Socket get() {
        if (socket == null) {
            throw new IllegalStateException("Channel is not connected");
        }
        if (!socket.isConnected()) {
            throw new IllegalStateException("Channel connection was already closed");
        }
        return socket;
    }

    public void close() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String toString() {
        return "host=" + host + ", port=" + port;
    }
}
