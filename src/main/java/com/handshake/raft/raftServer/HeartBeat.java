package com.handshake.raft.raftServer;

import com.handshake.raft.dto.AppendEntriesParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

public class HeartBeat implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(HeartBeat.class);

    private Node node;

    public HeartBeat(Node node){
        this.node = node;
    }

    @Override
    public void run() {
        if(node.getNodeStatus() != Status.LEADER){
            logger.warn("Node start heartbeat when status: " + node.getNodeStatus());
            return;
        }
        ArrayList<String> otherServers = node.getNodeConfig().getOtherServers();
        for(String url: otherServers){
//            AppendEntriesParam.builder()
//                    .term()
        }


    }
}
