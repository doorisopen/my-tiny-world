package com.twlee.bank.channel.pool;

import com.twlee.bank.channel.dto.ChannelRequest;
import com.twlee.bank.channel.dto.ChannelResponse;
import com.twlee.bank.channel.exception.ChannelCommunicationException;
import com.twlee.bank.channel.exception.ChannelConnectionException;

public interface ChannelConnection {
    void connect() throws ChannelConnectionException;
    void disconnect();
    boolean isConnected();
    void sendRequest(ChannelRequest request) throws ChannelCommunicationException;
    <T extends ChannelResponse> T receiveResponse(Class<T> responseType) throws ChannelCommunicationException;
//    void setReconnectStrategy(ReconnectStrategy strategy);
}
