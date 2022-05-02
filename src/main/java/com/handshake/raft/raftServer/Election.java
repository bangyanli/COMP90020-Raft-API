package com.handshake.raft.raftServer;

import java.util.concurrent.ThreadLocalRandom;

/**
 *
 */
public class Election implements Runnable{
    private Node node;

    public Election(Node node){
        this.node = node;
    }
    @Override
    public void run() {
        if(node.getNodeStatus()== Status.LEADER){
            return;
        }
        long currentTime = System.currentTimeMillis();
        node.setElectionTime(node.getElectionTime() + ThreadLocalRandom.current().nextInt(100));
    }
}
