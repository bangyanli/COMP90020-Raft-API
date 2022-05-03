package com.handshake.raft.raftServer;

import com.handshake.raft.common.utils.SpringContextUtil;
import com.handshake.raft.raftServer.ThreadPool.RaftThreadPool;
import com.handshake.raft.raftServer.proto.AppendEntriesParam;
import com.handshake.raft.raftServer.proto.AppendEntriesResult;
import com.handshake.raft.raftServer.proto.LogEntry;
import com.handshake.raft.raftServer.rpc.RpcClient;
import com.handshake.raft.raftServer.service.RaftConsensusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.concurrent.*;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

@Component
public class Heartbeat implements Runnable,LifeCycle{

    private static final Logger logger = LoggerFactory.getLogger(Heartbeat.class);

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
        heartbeatTask = raftThreadPool.getScheduledExecutorService().scheduleWithFixedDelay(
                new Heartbeat(),
                0,
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
        //use to interrupt task
        ConcurrentHashMap<String, Future<?>> RPCTaskMap = new ConcurrentHashMap<>();
        try {
            Node node = SpringContextUtil.getBean(Node.class);
            RaftThreadPool raftThreadPool = SpringContextUtil.getBean(RaftThreadPool.class);
            RpcClient rpcClient = SpringContextUtil.getBean(RpcClient.class);

            if (node.getNodeStatus() != Status.LEADER) {
                logger.warn("Node start heartbeat when status: " + node.getNodeStatus());
                return;
            }

            ArrayList<String> otherServers = node.getNodeConfig().getOtherServers();
            //use to wait result
            CountDownLatch latch = new CountDownLatch(otherServers.size()/2);

            for (String url : otherServers) {
                RaftConsensusService service = rpcClient.connectToService(url);
                if (service != null) {
                    Future<?> RPCTask = raftThreadPool.getExecutorService().submit(() -> {
                        try {
                            int nextIndex = node.getNextIndex().get(url);
                            int prevLogIndex = (nextIndex - 1);
                            int prevLogTerm = node.getLog().read(prevLogIndex).getTerm();
                            ArrayList<LogEntry> logEntries = null;
                            if(nextIndex <= node.getLog().getLast().getIndex()){
                                logEntries = node.getLog().getLogFromIndex(nextIndex);
                            }
                            AppendEntriesParam param = AppendEntriesParam.builder()
                                    .term(node.getCurrentTerm())
                                    .leaderId(node.getNodeConfig().getSelf())
                                    .prevLogIndex(prevLogIndex)
                                    .prevLogTerm(prevLogTerm)
                                    .entries(logEntries)
                                    .leaderCommit(node.getLog().getCommitIndex())
                                    .build();
                            AppendEntriesResult appendEntriesResult = service.appendEntries(param);
                            if (appendEntriesResult.getTerm() > node.getCurrentTerm()) {
                                logger.info("Get requestVoteResult from bigger term!");
                                node.setCurrentTerm(appendEntriesResult.getTerm());
                                //TODO convert to follower
                            }
                            if(appendEntriesResult.getSuccess()){
                                latch.countDown();
                                //node.getNextIndex().put(url,)
                            }

                        } catch (Exception e) {
                            logger.info("Fail to send heartbeat to " + url);
                        } finally {
                            //remove itself from RPCTaskMap
                            RPCTaskMap.remove(url);
                        }
                    });
                    RPCTaskMap.put(url,RPCTask);
                }
            }
            latch.await(3500, MILLISECONDS);
        }
        catch (Exception e){
            logger.warn("HeartBeat is Interrupted!");
            for(Future<?> RPCTask: RPCTaskMap.values()){
                RPCTask.cancel(true);
            }
        }

    }
}
