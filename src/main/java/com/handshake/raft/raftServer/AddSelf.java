package com.handshake.raft.raftServer;

import com.handshake.raft.raftServer.ThreadPool.RaftThreadPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Component
public class AddSelf implements LifeCycle{

    private static final Logger logger = LoggerFactory.getLogger(AddSelf.class);
    private static ScheduledFuture addSelfSchedule;

    @Autowired
    private RaftThreadPool raftThreadPool;

    @Autowired
    private Node node;

    @Override
    public void init() {
        logger.debug("Add itself");
        if (addSelfSchedule != null && !addSelfSchedule.isDone()) {
            addSelfSchedule.cancel(true);
        }
        if(node.getNodeConfig().isNewServer()){
            addSelfSchedule = raftThreadPool.getScheduledExecutorService().scheduleWithFixedDelay(
                    ()->{
                        node.addItself();
                        if(!node.getNodeConfig().isNewServer()){
                            stop();
                        }
                    },
                    0,
                    3000,
                    TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public void stop() {
        if(addSelfSchedule != null && !addSelfSchedule.isDone()){
            addSelfSchedule.cancel(true);
        }
    }
}
