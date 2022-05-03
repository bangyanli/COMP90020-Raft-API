package com.handshake.raft.raftServer;

import com.handshake.raft.common.utils.SpringContextUtil;
import com.handshake.raft.raftServer.proto.AppendEntriesParam;
import com.handshake.raft.raftServer.rpc.RpcClient;
import com.handshake.raft.raftServer.service.RaftConsensusService;
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

            RpcClient rpcClient = SpringContextUtil.getBean(RpcClient.class);
            RaftConsensusService service = rpcClient.getService(url);
            if(service != null){

            }
        }


    }
}
