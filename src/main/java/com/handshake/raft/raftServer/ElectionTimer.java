package com.handshake.raft.raftServer;

import com.handshake.raft.common.utils.SpringContextUtil;
import com.handshake.raft.raftServer.ThreadPool.RaftThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Component
public class ElectionTimer implements Runnable,LifeCycle{

    private static final Logger logger = LoggerFactory.getLogger(ElectionTimer.class);

    @Autowired
    private RaftThreadPool raftThreadPool;

    @Autowired
    private Node node;

    @Override
    public void init() {
        ScheduledFuture electionTimer = raftThreadPool.getElectionTimer();
        if (electionTimer != null && !electionTimer.isDone()) {
            electionTimer.cancel(true);
        }
        electionTimer = raftThreadPool.getScheduledExecutorService().scheduleWithFixedDelay(
                new ElectionTimer(),
                node.getNodeConfig().getElectionTimeout() + ThreadLocalRandom.current().nextInt(100),
                node.getNodeConfig().getElectionTimeout() + ThreadLocalRandom.current().nextInt(100),
                TimeUnit.MILLISECONDS);
        raftThreadPool.setElectionTimer(electionTimer);
    }

    @Override
    public void stop() {
        ScheduledFuture electionTimer = raftThreadPool.getElectionTimer();
        if(electionTimer != null && !electionTimer.isDone()){
            electionTimer.cancel(true);
        }
    }

    @Override
    public void run() {
        Node node = SpringContextUtil.getBean(Node.class);
        logger.info("Change to candidate in Term" + node.getCurrentTerm());
        //TODO change to candidate
    }
}