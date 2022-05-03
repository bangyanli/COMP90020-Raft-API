package com.handshake.raft.raftServer;

import com.handshake.raft.config.NodeConfig;
import com.handshake.raft.raftServer.log.LogSystem;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
@Getter
@Setter
public class Node implements LifeCycle{

    @Autowired
    private NodeConfig nodeConfig;
    private LogSystem log;
    private volatile Status nodeStatus = Status.FOLLOWER;
    private volatile long electionTime;
    private volatile int currentTerm;
    private volatile String votedFor;
    private ConcurrentHashMap<String, Integer> nextIndex;
    private ConcurrentHashMap<String, Integer> matchIndex;

    @Override
    public void init() {
        
    }

    @Override
    public void stop() {

    }

    public class follower implements LifeCycle{

        @Override
        public void init() {

        }

        @Override
        public void stop() {

        }
    }

    public class candidate implements LifeCycle{

        @Override
        public void init() {

        }

        @Override
        public void stop() {

        }
    }

    public class leader implements LifeCycle{

        @Override
        public void init() {

        }

        @Override
        public void stop() {

        }
    }


}
