package com.handshake.raft.raftServer;

import com.handshake.raft.common.utils.SpringContextUtil;
import com.handshake.raft.config.NodeConfig;
import com.handshake.raft.raftServer.log.LogSystem;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Getter
@Setter
public class Node implements LifeCycle{

    private static final Logger logger = LoggerFactory.getLogger(Node.class);

    @Autowired
    private NodeConfig nodeConfig;
    @Autowired
    private LogSystem log;
    private volatile Status nodeStatus = Status.FOLLOWER;
    private volatile Role role = new follower();
    private volatile long electionTime;
    private volatile int currentTerm;
    private volatile String votedFor;
    private ConcurrentHashMap<String, Integer> nextIndex;
    private ConcurrentHashMap<String, Integer> matchIndex;

    private volatile String leaderId;

    private ElectionTimer electionTimer;
    private Heartbeat heartbeat;

    @Override
    public void init() {
        electionTimer = SpringContextUtil.getBean(ElectionTimer.class);
        electionTimer.init();
        heartbeat = SpringContextUtil.getBean(Heartbeat.class);
    }

    @Override
    public void stop() {
        electionTimer.stop();
        heartbeat.stop();
    }

    public void setNodeStatus(Status nodeStatus) {
        Status prevStatus = this.nodeStatus;
        role.stop();
        if(nodeStatus == Status.FOLLOWER){
            role = new follower();
            role.init();
        }else if(nodeStatus == Status.CANDIDATE) {
            role = new candidate();
            role.init();
        }else if(nodeStatus == Status.LEADER){
            role = new leader();
            role.init();
        }
        logger.info("Node {} become {}", nodeConfig.getSelf(), nodeStatus);
    }

    public class follower implements Role{

        @Override
        public void init() {
            nodeStatus = Status.FOLLOWER;
        }

        @Override
        public void stop() {

        }

        @Override
        public Status getName() {
            return Status.FOLLOWER;
        }
    }

    public class candidate implements Role{

        Thread thread;

        @Override
        public void init() {
            nodeStatus = Status.CANDIDATE;
            //reset electionTimer
            electionTimer.stop();
            electionTimer.init();

            //start election
            thread = new Thread(new Election());
            thread.start();
        }

        @Override
        public void stop() {
            thread.interrupt();
        }

        @Override
        public Status getName() {
            return Status.CANDIDATE;
        }
    }

    public class leader implements Role{

        @Override
        public void init() {
            nodeStatus = Status.LEADER;
            leaderId = nodeConfig.getSelf();
            //set state on leaders
            nextIndex = new ConcurrentHashMap<>();
            matchIndex = new ConcurrentHashMap<>();
            for(String peer: nodeConfig.getOtherServers()){
                nextIndex.put(peer, log.getLastIndex()+1);
                matchIndex.put(peer,0);
            }

            //start heartbeat
            heartbeat.init();
            electionTimer.stop();
        }

        @Override
        public void stop() {
            heartbeat.stop();
            electionTimer.init();
        }

        @Override
        public Status getName() {
            return Status.LEADER;
        }
    }


}
