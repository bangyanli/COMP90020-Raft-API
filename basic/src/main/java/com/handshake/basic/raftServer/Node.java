package com.handshake.basic.raftServer;

import com.handshake.basic.common.utils.SpringContextUtil;
import com.handshake.basic.config.NodeConfig;
import com.handshake.basic.raftServer.ThreadPool.RaftThreadPool;
import com.handshake.basic.raftServer.proto.AppendEntriesParam;
import com.handshake.basic.raftServer.proto.AppendEntriesResult;
import com.handshake.basic.raftServer.proto.Command;
import com.handshake.basic.raftServer.proto.LogEntry;
import com.handshake.basic.raftServer.rpc.RpcClient;
import com.handshake.basic.raftServer.service.RaftConsensusService;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.concurrent.*;

@Service
@Getter
@Setter
public class Node implements LifeCycle {

    private static final Logger logger = LoggerFactory.getLogger(Node.class);
    @Autowired
    private NodeConfig nodeConfig;

    @Override
    public void init() {

    }

    @Override
    public void stop() {

    }


    /**
     * replication
     * update log and reset the Heartbeat
     * return whether success or not
     */
    public boolean replication(Command command){
        if(command == null){
            logger.warn("Node {} try to replicant a null command {}", getNodeConfig().getSelf(), command.toString());
            return false;
        }
        logger.info("Node {} start replication for command {}", getNodeConfig().getSelf(), command);
        //create log entry
        LogEntry logEntry = LogEntry.builder()
                .index(0)
                .term(0)
                .command(command)
                .build();

        RaftThreadPool raftThreadPool = SpringContextUtil.getBean(RaftThreadPool.class);
        RpcClient rpcClient = SpringContextUtil.getBean(RpcClient.class);
        ArrayList<String> otherServers = nodeConfig.getOtherServers();
        for (String url : otherServers) {
            RaftConsensusService service = rpcClient.connectToService(url);
            if (service != null) {
                raftThreadPool.getExecutorService().submit(() -> {
                    try {
                        ArrayList<LogEntry> logEntries = new ArrayList<>();
                        logEntries.add(logEntry);
                        AppendEntriesParam param = AppendEntriesParam.builder()
                                .term(0)
                                .leaderId(nodeConfig.getSelf())
                                .prevLogIndex(0)
                                .prevLogTerm(0)
                                .entries(logEntries)
                                .leaderCommit(0)
                                .build();
                        service.appendEntries(param);

                    } catch (Exception e) {
                        //logger.info(e.getMessage(),e);
                        logger.info("Fail to send command to " + url);
                    }
                });
            }
        }
        return true;
    }


}
