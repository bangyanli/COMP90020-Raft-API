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
    //use to interrupt task
    private ConcurrentHashMap<String, Future<?>> RPCTaskMap = new ConcurrentHashMap<>();

    @Autowired
    private Node node;

    @Autowired
    private RaftThreadPool raftThreadPool;

    @Override
    public void init() {
        init(0L);
    }

    /**
     * init with initial delay
     */
    public void init(Long initialDelay) {
        ScheduledFuture heartbeatTask = raftThreadPool.getHeartbeatTask();
        if (heartbeatTask != null && !heartbeatTask.isDone()) {
            heartbeatTask.cancel(true);
        }
        heartbeatTask = raftThreadPool.getScheduledExecutorService().scheduleWithFixedDelay(
                new Heartbeat(),
                initialDelay,
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
        for(Future<?> task: RPCTaskMap.values()){
            task.cancel(true);
        }
    }

    @Override
    public void run() {
        try {
            Node node = SpringContextUtil.getBean(Node.class);
            RaftThreadPool raftThreadPool = SpringContextUtil.getBean(RaftThreadPool.class);
            RpcClient rpcClient = SpringContextUtil.getBean(RpcClient.class);

            if (node.getNodeStatus() != Status.LEADER) {
                logger.warn("Node {}  start heartbeat when status: {}",
                        node.getNodeConfig().getSelf(),
                        node.getNodeStatus());
                return;
            }
            logger.info("Node {} start heartbeat", node.getNodeConfig().getSelf());
            ArrayList<String> otherServers = node.getNodeConfig().getOtherServers();
            //use to wait result
            CountDownLatch latch = new CountDownLatch(otherServers.size()/2);

            for (String url : otherServers) {
                //check whether last AppendEntries RPC is done
                Future<?> future = RPCTaskMap.get(url);
                //last AppendEntries RPC has not finished yet
                if(future != null && !future.isDone()){
                    continue;
                }

                RaftConsensusService service = rpcClient.connectToService(url);
                if (service != null) {
                    Future<?> RPCTask = raftThreadPool.getExecutorService().submit(() -> {
                        try {
                            int nextIndex = node.getNextIndex().get(url);
                            //init when log is empty
                            int prevLogIndex = 0;
                            int prevLogTerm = 0;
                            if(node.getLog().getLastIndex() > 0 && nextIndex > 1){
                                prevLogIndex = (nextIndex - 1);
                                prevLogTerm = node.getLog().read(prevLogIndex).getTerm();
                            }
                            ArrayList<LogEntry> logEntries = null;
                            int lastIndex = node.getLog().getLastIndex();
                            if(nextIndex <= lastIndex){
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
                            // server rule 1
                            if (appendEntriesResult.getTerm() > node.getCurrentTerm()) {
                                logger.info("Get requestVoteResult from bigger term!");
                                node.setCurrentTerm(appendEntriesResult.getTerm());
                                node.setVotedFor(null);
                                //convert to follower
                                node.setNodeStatus(Status.FOLLOWER);
                            }
                            //success
                            if(appendEntriesResult.getSuccess()){
                                if(lastIndex == node.getLog().getLastIndex()){
                                    latch.countDown();
                                }
                                //update the nextIndex and matchIndex
                                node.getNextIndex().put(url,lastIndex + 1);
                                node.getMatchIndex().put(url,lastIndex);
                            }else {
                                //fail decrement nextIndex
                                //cannot get -1
                                logger.info("Get fail for AppendEntries from {}", url);
                                node.getNextIndex().put(url, Math.max(1,nextIndex - 1));
                                findNextIndex(url,service);
                            }

                        } catch (Exception e) {
                            logger.info(e.getMessage(),e);
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
            //update N such that N > commitIndex
            node.setNForMatchIndex();
            //apply log
            node.getLog().applyLog();
            logger.info("Node {} finished heartbeat", node.getNodeConfig().getSelf());
        }
        catch (Exception e){
            logger.debug("Heartbeat is Interrupted!");
            for(Future<?> RPCTask: RPCTaskMap.values()){
                RPCTask.cancel(true);
            }
        }
    }

    /**
     * find nextIndex of a peer fast
     */
    public void findNextIndex(String peer, RaftConsensusService service){
        Node node = SpringContextUtil.getBean(Node.class);
        while (true){
            int nextIndex = node.getNextIndex().get(peer);
            //init when log is empty
            int prevLogIndex = 0;
            int prevLogTerm = 0;
            if(node.getLog().getLastIndex() > 0 && nextIndex > 1){
                prevLogIndex = (nextIndex - 1);
                prevLogTerm = node.getLog().read(prevLogIndex).getTerm();
            }

            AppendEntriesParam param = AppendEntriesParam.builder()
                    .term(node.getCurrentTerm())
                    .leaderId(node.getNodeConfig().getSelf())
                    .prevLogIndex(prevLogIndex)
                    .prevLogTerm(prevLogTerm)
                    .entries(null)
                    .leaderCommit(node.getLog().getCommitIndex())
                    .build();
            AppendEntriesResult appendEntriesResult = service.appendEntries(param);
            // server rule 1
            if (appendEntriesResult.getTerm() > node.getCurrentTerm()) {
                logger.info("Get requestVoteResult from bigger term!");
                node.setCurrentTerm(appendEntriesResult.getTerm());
                node.setVotedFor(null);
                //convert to follower
                node.setNodeStatus(Status.FOLLOWER);
            }
            //fail decrement nextIndex
            //cannot get -1
            if(!appendEntriesResult.getSuccess()){
                node.getNextIndex().put(peer, Math.max(1,nextIndex - 1));
            }else {
                break;
            }
        }
    }

}
