package com.handshake.raft.config;

import com.handshake.raft.raftServer.proto.ServerInfo;
import lombok.Data;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Data
@ToString
@Configuration
@ConfigurationProperties(prefix = "raft")
public class NodeConfig {

    private static final Logger logger = LoggerFactory.getLogger(NodeConfig.class);

    private static final ReentrantReadWriteLock configurationLock = new ReentrantReadWriteLock();

    private ArrayList<String> servers;
    private ArrayList<String> serversSpringAddress;
    private ServerInfo oldConfiguration;
    private String self;
    private volatile long electionTimeout;
    private long heartBeatFrequent;
    private long clientTimeout;
    private String log;
    //whether this server is added to cluster
    private boolean newServer;
    //whether the server try to delete itself from cluster
    private boolean shuttingDown = false;

    public ArrayList<String> getServers() {
        ReentrantReadWriteLock.ReadLock readLock = configurationLock.readLock();
        try {
            readLock.lock();
            return servers;
        }finally {
            readLock.unlock();
        }
    }

    public void setServers(ArrayList<String> servers) {
        ReentrantReadWriteLock.WriteLock writeLock = configurationLock.writeLock();
        try {
            writeLock.lock();
            this.servers = servers;
        }finally {
            writeLock.unlock();
        }
    }

    public ArrayList<String> getServersSpringAddress() {
        ReentrantReadWriteLock.ReadLock readLock = configurationLock.readLock();
        try {
            readLock.lock();
            return serversSpringAddress;
        }finally {
            readLock.unlock();
        }
    }

    public void setServersSpringAddress(ArrayList<String> serversSpringAddress) {
        ReentrantReadWriteLock.WriteLock writeLock = configurationLock.writeLock();
        try {
            writeLock.lock();
            this.serversSpringAddress = serversSpringAddress;
        }finally {
            writeLock.unlock();
        }
    }

    public ServerInfo getServerInfo(){
        ReentrantReadWriteLock.ReadLock readLock = configurationLock.readLock();
        try {
            readLock.lock();
            return new ServerInfo(servers,serversSpringAddress);
        }finally {
            readLock.unlock();
        }
    }

    public void setServerInfo(ServerInfo serverInfo){
        ReentrantReadWriteLock.WriteLock writeLock = configurationLock.writeLock();
        try {
            writeLock.lock();
            this.servers = serverInfo.getServers();
            this.serversSpringAddress = serverInfo.getServersSpringAddress();
        }finally {
            writeLock.unlock();
        }
    }

    public ArrayList<String> getOtherServers(){
        ArrayList<String> otherServers = new ArrayList<>(servers);
        otherServers.remove(self);
        return otherServers;
    }

    public String getSpringAddress(String peer){
        int index = servers.indexOf(peer);
        //if not exist, return the first
        if(index == -1){
            logger.warn("getSpringAddress address {} not exist!",peer);
            return serversSpringAddress.get(0);
        }
        return serversSpringAddress.get(index);
    }

}
