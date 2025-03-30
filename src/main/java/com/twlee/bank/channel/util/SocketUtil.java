package com.twlee.bank.channel.util;

import com.daou.msggw.smsgw.message.sender.channel.exception.SocketReadException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketException;

public class SocketUtil {

    public static void connect(Socket socket, String host, int port, int timeout) throws Exception {
        socket.connect(new InetSocketAddress(host, port), timeout);
    }

    public static void write(Socket socket, byte[] data) throws SocketException, IOException {
        OutputStream out = socket.getOutputStream();
        out.write(data);
        out.flush();
    }

    public static byte[] read(Socket socket, int length) throws SocketReadException, IOException {
        InputStream in = socket.getInputStream();
        byte[] buffer = new byte[length];
        int read = in.read(buffer);
        if (read == -1) {
            throw new SocketReadException("Disconnected from server");
        }
        return buffer;
    }
}