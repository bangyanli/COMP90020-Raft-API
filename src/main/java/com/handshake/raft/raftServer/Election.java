package com.handshake.raft.raftServer;

import com.handshake.raft.config.NodeConfig;
import com.handshake.raft.dto.LogEntry;
import com.handshake.raft.dto.RequestVoteResult;
import com.handshake.raft.log.LogSystem;

import java.util.ArrayList;
import java.util.concurrent.Future;
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
        NodeConfig config = node.getNodeConfig();
        String self = config.getSelf();
        node.setVotedFor(self);

        ArrayList<Future<RequestVoteResult>> futureArrayList = new ArrayList<>();
        ArrayList<String> peers = config.getServers();
        LogSystem logSystem = node.getLog();
        LogEntry logEntry = logSystem.getLast();
        int lastTerm = 0;
        if(logEntry != null)
            lastTerm = logEntry.getTerm();

        for(String peer:peers){
            //TODO:implement raftthreadpool before finishing this part
        }

    }
}
