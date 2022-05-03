package com.handshake.raft.raftServer;

import com.handshake.raft.common.utils.SpringContextUtil;
import com.handshake.raft.config.NodeConfig;
import com.handshake.raft.raftServer.ThreadPool.RaftThreadPool;
import com.handshake.raft.raftServer.proto.AppendEntriesParam;
import com.handshake.raft.raftServer.proto.LogEntry;
import com.handshake.raft.raftServer.proto.RequestVoteParam;
import com.handshake.raft.raftServer.proto.RequestVoteResult;
import com.handshake.raft.raftServer.rpc.RpcClient;
import com.handshake.raft.raftServer.service.RaftConsensusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import static java.util.concurrent.TimeUnit.MILLISECONDS;

/**
 *
 */
public class Election implements Runnable{

    private static final Logger logger = LoggerFactory.getLogger(Election.class);

    private final Node node = SpringContextUtil.getBean(Node.class);
    private final RaftThreadPool raftThreadPool = SpringContextUtil.getBean(RaftThreadPool.class);

    @Override
    public void run() {
        try {
            if (node.getNodeStatus() == Status.LEADER) {
                logger.warn("Node start heartbeat when status: {}", node.getNodeStatus());
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
            LogEntry last = node.getLog().getLast();
            RequestVoteParam requestVoteParam = RequestVoteParam.builder()
                    .term(node.getCurrentTerm())
                    .candidateId(config.getSelf())
                    .lastLogIndex(last.getIndex())
                    .lastLogTerm(last.getTerm())
                    .build();
            ArrayList<String> peers = config.getOtherServers();
            CountDownLatch latch = new CountDownLatch(peers.size());
            for (String peer : peers) {
                RpcClient rpcClient = SpringContextUtil.getBean(RpcClient.class);
                RaftConsensusService service = rpcClient.getService(peer);
                if (service != null) {
                    raftThreadPool.getExecutorService().submit(() -> {
                        try {
                            RequestVoteResult requestVoteResult = service.requestVote(requestVoteParam);
                            latch.countDown();
                            if (requestVoteResult.getTerm() > node.getCurrentTerm()) {
                                logger.info("Get requestVoteResult from bigger term!");
                                node.setCurrentTerm(requestVoteResult.getTerm());
                                //TODO convert to follower
                            }
                            if (requestVoteResult.getVoteGranted()) {
                                voteGet.incrementAndGet();
                            }
                        } catch (Exception e) {
                            logger.info("Fail to send request vote to " + peer);
                        }
                    });
                }
            }

            //wait for response
            latch.await(3500, MILLISECONDS);

            //get more than half vote
            if (voteGet.get() > (config.getServers().size()/2)) {
                logger.info("node {} becomes leader , success count = {} , status : {}",
                        config.getSelf(),
                        voteGet.get(),
                        node.getNodeStatus());
                //TODO become leader
            } else {
                //start election
                node.setVotedFor(null);
            }
        }catch (InterruptedException e){
            logger.warn("Election is Interrupted!");
        }


    }
}
