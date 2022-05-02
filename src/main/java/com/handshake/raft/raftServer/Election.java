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
        //resolve election conflict
        long currentTime = System.currentTimeMillis();
        long randomElectionTime = node.getElectionTime() + ThreadLocalRandom.current().nextInt(100);
        node.setElectionTime(randomElectionTime);
        if(currentTime - randomElectionTime < node.getLastElectionTime())
            return;

        node.setNodeStatus(Status.CANDIDATE);
        node.setLastElectionTime(System.currentTimeMillis() + ThreadLocalRandom.current().nextInt(100) + 150);
        int currentTerm = node.getCurrentTerm();
        node.setCurrentTerm(currentTerm+1);
        String self = node.getNodeConfig().getSelf();
        node.setVotedFor(self);


    }
}
