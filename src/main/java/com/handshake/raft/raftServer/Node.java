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
    private volatile Role role = new follower();
    private volatile long electionTime;
    private volatile int currentTerm;
    private volatile String votedFor;
    private ConcurrentHashMap<String, Integer> nextIndex;
    private ConcurrentHashMap<String, Integer> matchIndex;

<<<<<<< HEAD
=======
    private volatile String leaderId;

>>>>>>> 92329ba130de1293715bf07df81b0b26aab3afb3
    //@Autowired
    private ElectionTimer electionTimer;

    //@Autowired
<<<<<<< HEAD
    private HeartBeat heartBeat;
=======
    private Heartbeat heartBeat;
>>>>>>> 92329ba130de1293715bf07df81b0b26aab3afb3

    @Override
    public void init() {

    }

    @Override
    public void stop() {

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
                nextIndex.put(peer, log.getLast().getIndex());
                matchIndex.put(peer,0);
            }

            //start heartbeat
            heartBeat.init();
            electionTimer.stop();
        }

        @Override
        public void stop() {
            heartBeat.stop();
            electionTimer.init();
        }

        @Override
        public Status getName() {
            return Status.LEADER;
        }
    }


}
