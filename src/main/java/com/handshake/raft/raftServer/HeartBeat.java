package com.handshake.raft.raftServer;

import com.handshake.raft.dto.AppendEntriesParam;
import com.handshake.raft.rpc.Request;
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
            AppendEntriesParam param = AppendEntriesParam.builder()
                    .term(node.getCurrentTerm())
                    .leaderId(node.getNodeConfig().getSelf())
                    .prevLogIndex(null)
                    .entries(null)
                    .leaderCommit(node.getCommitIndex())
                    .build();

            Request.builder()
                    .cmd(Request.A_ENTRIES)
                    .obj(param)
                    .url(url)
                    .build();
        }


    }
}
