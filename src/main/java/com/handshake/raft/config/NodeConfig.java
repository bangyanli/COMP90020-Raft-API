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
    private ArrayList<String> serversSpringAddress;
    private String self;
    private volatile long electionTimeout;
    private long heartBeatFrequent;
    private String log;

    private int webSocketServer;
    public ArrayList<String> getOtherServers(){
        ArrayList<String> otherServers = new ArrayList<>(servers);
        otherServers.remove(self);
        return otherServers;
    }

    public String getSpringAddress(String peer){
        int index = servers.indexOf(peer);
        //if not exist, return the first
        if(index == -1){
            return serversSpringAddress.get(0);
        }
        return serversSpringAddress.get(index);
    }

}
