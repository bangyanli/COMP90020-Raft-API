package com.handshake.raft.raftServer;

import com.handshake.raft.common.utils.SpringContextUtil;
import com.handshake.raft.config.NodeConfig;
import com.handshake.raft.raftServer.ThreadPool.RaftThreadPool;
import com.handshake.raft.raftServer.proto.RequestVoteParam;
import com.handshake.raft.raftServer.proto.RequestVoteResult;
import com.handshake.raft.raftServer.rpc.RpcClient;
import com.handshake.raft.raftServer.service.RaftConsensusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 *
 */
public class Election implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(Election.class);

    private final Node node = SpringContextUtil.getBean(Node.class);
    private final RaftThreadPool raftThreadPool = SpringContextUtil.getBean(RaftThreadPool.class);
    private final RpcClient rpcClient = SpringContextUtil.getBean(RpcClient.class);

    @Override
    public void run() {
        if(node.getNodeConfig().isNewServer()){
            return;
        }
        if(node.getNodeConfig().isShuttingDown()){
            return;
        }
        //use to interrupt task
        ConcurrentHashMap<String, Future<?>> RPCTaskMap = new ConcurrentHashMap<>();
        try {
            if (node.getNodeStatus() == Status.LEADER) {
                logger.warn("Node start Election when status: {}", node.getNodeStatus());
                return;
            }

            //increment currentTerm
            int currentTerm = node.getCurrentTerm();
            node.setCurrentTerm(currentTerm + 1);

            //vote for self
            NodeConfig config = node.getNodeConfig();
            String self = config.getSelf();
            node.setVotedFor(self);

            AtomicInteger voteGet = new AtomicInteger(1);

            //send RequestVote RPC
            RequestVoteParam requestVoteParam = RequestVoteParam.builder()
                    .term(node.getCurrentTerm())
                    .candidateId(config.getSelf())
                    .lastLogIndex(node.getLog().getLastIndex())
                    .lastLogTerm(node.getLog().getLastTerm())
                    .build();
            ArrayList<String> peers = config.getOtherServers();
            //use to wait result
            CountDownLatch latch = new CountDownLatch(peers.size());
            for (String peer : peers) {
                RaftConsensusService service = rpcClient.connectToService(peer);
                if (service != null) {
                    Future<?> RPCTask = raftThreadPool.getExecutorService().submit(() -> {
                        try {
                            RequestVoteResult requestVoteResult = service.requestVote(requestVoteParam);
                            latch.countDown();
                            if (requestVoteResult.getTerm() > node.getCurrentTerm()) {
                                logger.debug("Get requestVoteResult from bigger term!");
                                node.setCurrentTerm(requestVoteResult.getTerm());
                                node.setVotedFor(null);
                                //convert to follower
                                node.setNodeStatus(Status.FOLLOWER);
                            }
                            if (requestVoteResult.getVoteGranted()) {
                                voteGet.incrementAndGet();
                            }
                        } catch (Exception e) {
                            //logger.info(e.getMessage(),e);
                            logger.warn("Fail to send request vote to " + peer);
                        }
                        finally {
                            //remove itself from RPCTaskMap
                            RPCTaskMap.remove(peer);
                        }
                    });
                    RPCTaskMap.put(peer,RPCTask);
                }
            }

            //wait for response
            latch.await(3500, MILLISECONDS);

            //get more than half vote
            if (voteGet.get() > (config.getServers().size()/2)) {
                //become leader
                node.setNodeStatus(Status.LEADER);
                logger.info("node {} becomes leader , success count = {} , status : {}",
                        config.getSelf(),
                        voteGet.get(),
                        node.getNodeStatus());
            } else {
                //start election
                node.setVotedFor(null);
                node.setNodeStatus(Status.FOLLOWER);
            }
        }catch (InterruptedException e){
            logger.debug("Election is Interrupted!");
            for(Future<?> RPCTask: RPCTaskMap.values()){
                RPCTask.cancel(true);
            }
        }


    }
}
