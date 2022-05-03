package com.handshake.raft.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

@Data
@ToString
@Configuration
@ConfigurationProperties(prefix = "raft")
public class NodeConfig {

    private ArrayList<String> servers;
    private String self;
    private volatile long electionTimeout;
    private long heartBeatFrequent;
    private String log;

    public ArrayList<String> getOtherServers(){
        ArrayList<String> otherServers = new ArrayList<>(servers);
        otherServers.remove(self);
        return otherServers;
    }

}
