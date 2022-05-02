package com.handshake.raft.raftServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HeartBeat implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(HeartBeat.class);

    private Node node;

    public HeartBeat(Node node){
        this.node = node;
    }

    @Override
    public void run() {
        if(node.getNodeStatus() != Status.LEADER){
            return;
        }

    }
}
