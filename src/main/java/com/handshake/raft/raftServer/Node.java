package com.handshake.raft.raftServer;

import com.handshake.raft.common.utils.SpringContextUtil;
import com.handshake.raft.config.NodeConfig;
import com.handshake.raft.raftServer.ThreadPool.RaftThreadPool;
import com.handshake.raft.raftServer.log.LogSystem;
import com.handshake.raft.raftServer.proto.Command;
import com.handshake.raft.raftServer.proto.LogEntry;
import com.handshake.raft.service.Impl.WebSocketServer;
import org.springframework.web.socket.WebSocketSession;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

@Service
@Getter
@Setter
public class Node implements LifeCycle{

    private static final Logger logger = LoggerFactory.getLogger(Node.class);
    @Autowired
    private WebSocketServer webSocketServer;
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
        //get persistent state
        setCurrentTerm(log.getCurrentTerm());
        setVotedFor(log.getVotedFor());
    }

    @Override
    public void stop() {
        logger.debug("Stopping {}", Node.class);
        electionTimer.stop();
        heartbeat.stop();
        role.stop();
        logger.debug("Stopped {}", Node.class);
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
                nextIndex.put(peer, log.getLastIndex());
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
            //clean the data
            nextIndex = new ConcurrentHashMap<>();
            matchIndex = new ConcurrentHashMap<>();
            for(String peer: nodeConfig.getOtherServers()){
                nextIndex.put(peer, log.getLastIndex());
                matchIndex.put(peer,0);
            }
        }

        @Override
        public Status getName() {
            return Status.LEADER;
        }
    }

    /**
     * leader action only
     *
     */
    public void setNForMatchIndex(){
        if(nodeStatus != Status.LEADER){
            logger.warn("Find N when status {}", nodeStatus);
            return;
        }
        //for 3 servers, 2 other servers, majorityNumber is 1
        //for 4 servers, 3 other servers, majorityNumber is 2
        int otherServerSize = nodeConfig.getOtherServers().size();
        int majorityNumber = otherServerSize/2 + otherServerSize%2;
        HashMap<Integer,Integer> countMap = new HashMap<>();
        for(Integer matchLog : matchIndex.values()){
            Integer count = countMap.getOrDefault(matchLog, 0) + 1;
            countMap.put(matchLog, count);
        }
        for (Map.Entry<Integer,Integer> entry: countMap.entrySet()){
            if(entry.getValue() >= majorityNumber){
                if(entry.getKey() > log.getCommitIndex()){
                    logger.info("New commit Index: {}", entry.getKey());
                    log.setCommitIndex(entry.getKey());
                }
                //can have equal value e.g. one 28, one 29 , should choose 29
            }
        }
    }

    /**
     * replication
     * update log and reset the Heartbeat
     * return whether success or not
     */
    public boolean replication(Command command){
        if(nodeStatus != Status.LEADER){
            //RETURN LEADER
            return false;
        }
        if(command == null){
            logger.warn("Node {} try to replicant a null command {}", getNodeConfig().getSelf(), command.toString());
            return false;
        }
        logger.info("Node {} start replication for command {}", getNodeConfig().getSelf(), command);
        //create log entry
        LogEntry logEntry = LogEntry.builder()
                .index(log.getLastIndex() + 1)
                .term(getCurrentTerm())
                .command(command)
                .build();

        //save to log system first
        log.write(logEntry);


        //restart heartbeat
        heartbeat.stop();
        RaftThreadPool raftThreadPool = SpringContextUtil.getBean(RaftThreadPool.class);
        Future<?> task = raftThreadPool.getExecutorService().submit(heartbeat);
        try {
            task.get(10000, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            logger.warn("Replication in Interrupted when replicate {}", logEntry.toString());
            return false;
        } catch (ExecutionException e) {
            logger.warn("Replication has ExecutionException when replicate {}", logEntry.toString());
            return false;
        } catch (TimeoutException e) {
            logger.warn("Replication timeout when replicate {}", logEntry.toString());
            return false;
        }finally {
            heartbeat.init(nodeConfig.getHeartBeatFrequent());
        }
        logger.info("log.getCommitIndex() == logEntry.getIndex() {}", log.getCommitIndex() == logEntry.getIndex());
        return log.getCommitIndex() == logEntry.getIndex();
    }

}
