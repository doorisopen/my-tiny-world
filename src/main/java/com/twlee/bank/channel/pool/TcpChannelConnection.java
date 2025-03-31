package com.twlee.bank.channel.pool;

import com.twlee.bank.channel.dto.ChannelRequest;
import com.twlee.bank.channel.dto.ChannelResponse;
import com.twlee.bank.channel.exception.ChannelCommunicationException;
import com.twlee.bank.channel.exception.ChannelConnectionException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// 4. TCP 기반 채널 연결 구현
public class TcpChannelConnection implements ChannelConnection {
    private final String host;
    private final int port;
    private final int readTimeout;
    private final int connectionTimeout;
    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
//    private ReconnectStrategy reconnectStrategy;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final Lock connectionLock = new ReentrantLock();
    
    public TcpChannelConnection(String host, int port, int connectionTimeout, int readTimeout) {
        this.host = host;
        this.port = port;
        this.connectionTimeout = connectionTimeout;
        this.readTimeout = readTimeout;
//        this.reconnectStrategy = new DefaultReconnectStrategy();
    }
    
    @Override
    public void connect() throws ChannelConnectionException {
        connectionLock.lock();
        try {
            if (isConnected()) {
                return;
            }
            
            try {
                socket = new Socket();
                socket.connect(new InetSocketAddress(host, port), connectionTimeout);
                socket.setSoTimeout(readTimeout);
                outputStream = socket.getOutputStream();
                inputStream = socket.getInputStream();
                connected.set(true);
            } catch (IOException e) {
                throw new ChannelConnectionException("Failed to connect to " + host + ":" + port, e);
            }
        } finally {
            connectionLock.unlock();
        }
    }
    
    @Override
    public void disconnect() {
        connectionLock.lock();
        try {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException e) {
                    // 로깅 처리
                } finally {
                    socket = null;
                    outputStream = null;
                    inputStream = null;
                    connected.set(false);
                }
            }
        } finally {
            connectionLock.unlock();
        }
    }
    
    @Override
    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected() && connected.get();
    }
    
    @Override
    public void sendRequest(ChannelRequest request) throws ChannelCommunicationException {
//        ensureConnected();
        
        try {
            outputStream.write(request.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            connected.set(false);
            throw new ChannelCommunicationException("Failed to send request", e);
        }
    }
    
    @Override
    public <T extends ChannelResponse> T receiveResponse(Class<T> responseType) throws ChannelCommunicationException {
//        ensureConnected();
        
        try {
            byte[] buffer = new byte[8192]; // 적절한 버퍼 크기 설정 필요
            int bytesRead = inputStream.read(buffer);
            
            if (bytesRead == -1) {
                connected.set(false);
                throw new ChannelCommunicationException("Connection closed by server");
            }
            
            byte[] responseData = Arrays.copyOf(buffer, bytesRead);
            T response = responseType.getDeclaredConstructor().newInstance();
            response.parse(responseData);
            return response;
        } catch (IOException e) {
            connected.set(false);
            throw new ChannelCommunicationException("Failed to receive response", e);
        } catch (ReflectiveOperationException e) {
            throw new ChannelCommunicationException("Failed to create response instance", e);
        }
    }
    
//    @Override
//    public void setReconnectStrategy(ReconnectStrategy strategy) {
//        this.reconnectStrategy = strategy;
//    }
    
//    private void ensureConnected() throws ChannelCommunicationException {
//        if (!isConnected()) {
//            try {
//                reconnectStrategy.reconnect(this);
//            } catch (ChannelConnectionException e) {
//                throw new ChannelCommunicationException("Failed to reconnect", e);
//            }
//        }
//    }
}
