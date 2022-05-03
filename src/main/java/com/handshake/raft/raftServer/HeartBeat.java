package com.handshake.raft.raftServer;

import com.handshake.raft.common.utils.SpringContextUtil;
import com.handshake.raft.raftServer.ThreadPool.RaftThreadPool;
import com.handshake.raft.raftServer.proto.AppendEntriesParam;
import com.handshake.raft.raftServer.rpc.RpcClient;
import com.handshake.raft.raftServer.service.RaftConsensusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Component
public class HeartBeat implements Runnable,LifeCycle{

    private static final Logger logger = LoggerFactory.getLogger(HeartBeat.class);

    @Autowired
    private Node node;

    @Autowired
    private RaftThreadPool raftThreadPool;

    @Override
    public void init() {
        ScheduledFuture heartbeatTask = raftThreadPool.getHeartbeatTask();
        if (heartbeatTask != null && !heartbeatTask.isDone()) {
            heartbeatTask.cancel(true);
        }
        heartbeatTask = raftThreadPool.getScheduledExecutorService().schedule(
                new HeartBeat(),
                node.getNodeConfig().getHeartBeatFrequent(),
                TimeUnit.MILLISECONDS);
        raftThreadPool.setHeartbeatTask(heartbeatTask);
    }

    @Override
    public void stop() {
        ScheduledFuture heartbeatTask = raftThreadPool.getHeartbeatTask();
        if(heartbeatTask != null && !heartbeatTask.isDone()){
            heartbeatTask.cancel(true);
        }
    }

    @Override
    public void run() {

        if(node.getNodeStatus() != Status.LEADER){
            logger.warn("Node start heartbeat when status: " + node.getNodeStatus());
            return;
        }

        ArrayList<String> otherServers = node.getNodeConfig().getOtherServers();

        for(String url: otherServers){
            RpcClient rpcClient = SpringContextUtil.getBean(RpcClient.class);
            RaftConsensusService service = rpcClient.getService(url);
            if(service != null){
                raftThreadPool.getExecutorService().submit(()->{
                    try {
                        Integer prevLogIndex = node.getMatchIndex().get(url);
                        int prevLogTerm = node.getLog().read(prevLogIndex).getTerm();
                        AppendEntriesParam param = AppendEntriesParam.builder()
                                .term(node.getCurrentTerm())
                                .leaderId(node.getNodeConfig().getSelf())
                                .prevLogIndex(prevLogIndex)
                                .prevLogTerm(prevLogTerm)
                                .entries(null)
                                .leaderCommit(node.getLog().getCommitIndex())
                                .build();
                        service.appendEntries(param);
                    }
                    catch (Exception e){
                        logger.info("Fail to send heartbeat to " + url);
                    }
                });
            }
        }

    }
}
