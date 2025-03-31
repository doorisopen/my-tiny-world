package com.twlee.bank.channel;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ChannelInitializer {
    // TODO Redis로 부터 채널 정보 동기화
//    private final RedistTemplate redistTemplate;

    @Bean
    public ChannelSource sktChannelSource() {
        List<ConnectInfo> connectInfos = loadCid();
        List<ConnectInfo> pool1 = new ArrayList<>();
        List<ConnectInfo> pool2 = new ArrayList<>();
        for (ConnectInfo connectInfo : connectInfos) {
            if (connectInfo.poolNo() == 1) {
                pool1.add(connectInfo);
            } else {
                pool2.add(connectInfo);
            }
        }
        return new AChannelSource(
                new ChannelConfig(Channel.A, pool1),
                new ChannelConfig(Channel.A, pool2));
    }

    private List<ConnectInfo> loadCid() {
        ConnectInfo connectInfo1 = createCid(1, "id1", "pwd1", "127.0.0.1", "8088", "10");
        ConnectInfo connectInfo2 = createCid(1, "id2", "pwd2", "127.0.0.1", "8089", "10");
        return List.of(connectInfo1, connectInfo2);
    }

    private ConnectInfo createCid(Integer poolNo, String id, String pwd, String host, String port, String tps) {
        return new ConnectInfo(poolNo, id, pwd, host, Integer.parseInt(port), Integer.parseInt(tps));
    }
}
