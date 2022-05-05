package com.handshake.raft.raftServer.service.Impl;

import com.handshake.raft.common.utils.SpringContextUtil;
import com.handshake.raft.raftServer.ElectionTimer;
import com.handshake.raft.raftServer.Node;
import com.handshake.raft.raftServer.Status;
import com.handshake.raft.raftServer.log.Impl.LogSystemImpl;
import com.handshake.raft.raftServer.log.LogSystem;
import com.handshake.raft.raftServer.proto.*;
import com.handshake.raft.raftServer.proto.Impl.ChangeConfigurationCommand;
import com.handshake.raft.raftServer.service.RaftConsensusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class RaftConsensusServiceImpl implements RaftConsensusService {

    private static final Logger logger = LoggerFactory.getLogger(LogSystemImpl.class);
    public static final ReentrantLock voteLock = new ReentrantLock();
    public static final ReentrantLock appendLock = new ReentrantLock();
    public static final ReentrantLock addPeerLock = new ReentrantLock();
    public static final ReentrantLock removePeerLock = new ReentrantLock();
    //set latency
    public static final ConcurrentHashMap<String,Integer> latencyMap = new ConcurrentHashMap<>();

    private final Node node = SpringContextUtil.getBean(Node.class);
    private final LogSystem logSystem = SpringContextUtil.getBean(LogSystem.class);
    private final ElectionTimer electionTimer = SpringContextUtil.getBean(ElectionTimer.class);

    private void latency(String ip) throws InterruptedException{
        //simulate latency
        Integer latency = latencyMap.getOrDefault(ip, 0);
        if(latency < 0){
            //sleep very long
            Thread.sleep(1000*1000);
        }else {
            Thread.sleep(latency);
        }
    }

    @Override
    public AppendEntriesResult appendEntries(AppendEntriesParam param) throws InterruptedException{
        latency(param.getLeaderId());
        try {
            appendLock.lock();
            if (node.getNodeStatus() == Status.LEADER) {
                logger.warn("Receive append entries when leader!");
            }

            //step 1
            if (param.getTerm() < node.getCurrentTerm()) {
                logger.info("Get appendEntries from smaller term!");
                return new AppendEntriesResult(node.getCurrentTerm(), false);
            }

            if(param.getTerm() > node.getCurrentTerm()){
                logger.debug("Get appendEntries from bigger term!");
                node.setCurrentTerm(param.getTerm());
                node.setVotedFor(null);
                //convert to follower
                node.setNodeStatus(Status.FOLLOWER);
            }

            //set leader
            node.setLeaderId(param.getLeaderId());
            //reset election timeout
            electionTimer.init();

            //check whether is init(no log at all)
            if(param.getPrevLogIndex() != 0){
                //step 2
                LogEntry logEntry = logSystem.read(param.getPrevLogIndex());
                if (logEntry == null || logEntry.getTerm() != param.getPrevLogTerm()) {
                    return new AppendEntriesResult(node.getCurrentTerm(), false);
                }
            }

            logger.info("param {}", param.toString());

            //heartbeat
            if (param.getEntries() == null || param.getEntries().size() == 0) {
                //step 5
                if (param.getLeaderCommit() > logSystem.getCommitIndex()) {
                    logSystem.setCommitIndex(Math.min(param.getLeaderCommit(),logSystem.getLastIndex()));
                }
                logSystem.applyLog();
                return new AppendEntriesResult(node.getCurrentTerm(), true);
            }

            for (LogEntry leaderEntry : param.getEntries()) {
                //step 3
                LogEntry logEntry = logSystem.read(leaderEntry.getIndex());
                if (logEntry != null && logEntry.getTerm() != leaderEntry.getTerm()) {
                    logger.info("Delete log with index " + logEntry.getIndex());
                    logSystem.removeFromIndex(leaderEntry.getIndex());
                }
                //step 4
                if(logEntry == null){
                    //execute change configuration command immediately
                    if(leaderEntry.getCommand() != null && leaderEntry.getCommand() instanceof ChangeConfigurationCommand){
                        //create new command because execute will change status of command
                        ChangeConfigurationCommand clientCommand = (ChangeConfigurationCommand)leaderEntry.getCommand();
                        ChangeConfigurationCommand newCommand = ChangeConfigurationCommand.builder()
                                .servers(clientCommand.getServers())
                                .serversSpringAddress(clientCommand.getServersSpringAddress())
                                .executed(false)
                                .build();
                        newCommand.execute();

                        //copy the log entry because execute command will change status of command
                        LogEntry newLogEntry = LogEntry.builder()
                                .index(leaderEntry.getIndex())
                                .term(leaderEntry.getTerm())
                                .command(newCommand)
                                .build();
                        leaderEntry = newLogEntry;
                    }
                    logSystem.write(leaderEntry);
                }

            }

            //step 5
            if (param.getLeaderCommit() > logSystem.getCommitIndex()) {
                logSystem.setCommitIndex(Math.min(param.getLeaderCommit(),logSystem.getLastIndex()));
            }

            logSystem.applyLog();

            //success
            return new AppendEntriesResult(node.getCurrentTerm(), true);
        }
        finally {
            logSystem.store();
            appendLock.unlock();
        }

    }


    @Override
    public RequestVoteResult requestVote(RequestVoteParam param) throws InterruptedException{
        latency(param.getCandidateId());
        try{
            voteLock.lock();
            //step 1
            if(param.getTerm() < node.getCurrentTerm()){
                return new RequestVoteResult(node.getCurrentTerm(),false);
            }
            if(param.getTerm() > node.getCurrentTerm()){
                node.setCurrentTerm(param.getTerm());
                node.setVotedFor(null);
                //convert to follower
                node.setNodeStatus(Status.FOLLOWER);
            }
            //step 2
            if(node.getVotedFor() == null || node.getVotedFor().equals(param.getCandidateId())){
                if(param.getLastLogIndex() >= logSystem.getLastIndex() && param.getLastLogTerm() >= logSystem.getLastTerm()){
                    node.setVotedFor(param.getCandidateId());
                    return new RequestVoteResult(node.getCurrentTerm(),true);
                }
            }
            return new RequestVoteResult(node.getCurrentTerm(),false);
        }
        finally {
            voteLock.unlock();
        }
    }

    @Override
    public AddPeerResult addPeer(AddPeerParam addPeerParam) throws InterruptedException {
        latency(addPeerParam.getPeerIp());
        try {
            addPeerLock.lock();

            //Not leader
            if(node.getNodeStatus() != Status.LEADER){
                return new AddPeerResult(false,null,node.getLeaderId());
            }

            ServerInfo oldConfiguration = node.getNodeConfig().getOldConfiguration();
            //check whether peer is added before
            if(node.getNodeConfig().getServers().contains(addPeerParam.getPeerIp())){
                //server already in cluster
                if(oldConfiguration == null
                        || !oldConfiguration.getServers().contains(addPeerParam.getPeerIp())){
                    return new AddPeerResult(true,null,node.getLeaderId());
                }
                else {
                    return new AddPeerResult(false,null,node.getLeaderId());
                }
            }

            //still trying to process last change configuration process
            if(oldConfiguration != null){
                return new AddPeerResult(false,null,node.getLeaderId());
            }

            //new configuration
            ArrayList<String> newServers = new ArrayList<>(node.getNodeConfig().getServers());
            newServers.add(addPeerParam.getPeerIp());
            ArrayList<String> newServersSpringAddress = new ArrayList<>(node.getNodeConfig().getServersSpringAddress());
            newServersSpringAddress.add(addPeerParam.getPeerSpringAddress());

            //create command
            ChangeConfigurationCommand changeConfigurationCommand = ChangeConfigurationCommand.builder()
                    .servers(newServers)
                    .serversSpringAddress(newServersSpringAddress)
                    .executed(false)
                    .build();

            //use new configuration when it comes
            changeConfigurationCommand.execute();

            //create log entry
            LogEntry logEntry = LogEntry.builder()
                    .index(node.getLog().getLastIndex() + 1)
                    .term(node.getCurrentTerm())
                    .command(changeConfigurationCommand)
                    .build();

            //save to log system first
            node.getLog().write(logEntry);
            //use heartbeat to update new configuration, not here
            return new AddPeerResult(false,null,node.getLeaderId());
        }
        finally {
            addPeerLock.unlock();
        }

    }

    @Override
    public RemovePeerResult removePeer(RemovePeerParam removePeerParam) throws InterruptedException {
        latency(removePeerParam.getPeerIp());
        try {
            removePeerLock.lock();

            //Not leader
            if(node.getNodeStatus() != Status.LEADER){
                return new RemovePeerResult(false,null,node.getLeaderId());
            }

            ServerInfo oldConfiguration = node.getNodeConfig().getOldConfiguration();
            //check whether peer is removed before
            if(!node.getNodeConfig().getServers().contains(removePeerParam.getPeerIp())){
                //server already removed
                if(oldConfiguration == null
                        || !oldConfiguration.getServers().contains(removePeerParam.getPeerIp())){
                    return new RemovePeerResult(true,null,node.getLeaderId());
                }
                else {
                    return new RemovePeerResult(false,null,node.getLeaderId());
                }
            }

            //still trying to process last change configuration process
            if(oldConfiguration != null){
                return new RemovePeerResult(false,null,node.getLeaderId());
            }

            //new configuration
            ArrayList<String> newServers = new ArrayList<>(node.getNodeConfig().getServers());
            newServers.remove(removePeerParam.getPeerIp());
            ArrayList<String> newServersSpringAddress = new ArrayList<>(node.getNodeConfig().getServersSpringAddress());
            newServersSpringAddress.remove(removePeerParam.getPeerSpringAddress());

            //create command
            ChangeConfigurationCommand changeConfigurationCommand = ChangeConfigurationCommand.builder()
                    .servers(newServers)
                    .serversSpringAddress(newServersSpringAddress)
                    .executed(false)
                    .build();

            //use new configuration when it comes
            changeConfigurationCommand.execute();

            //create log entry
            LogEntry logEntry = LogEntry.builder()
                    .index(node.getLog().getLastIndex() + 1)
                    .term(node.getCurrentTerm())
                    .command(changeConfigurationCommand)
                    .build();

            //save to log system first
            node.getLog().write(logEntry);
            //use heartbeat to update new configuration, not here
            return new RemovePeerResult(false,null,node.getLeaderId());
        }
        finally {
            removePeerLock.unlock();
        }
    }
}
