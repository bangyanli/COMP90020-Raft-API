package com.handshake.basic.raftServer.service.Impl;

import com.handshake.basic.common.utils.SpringContextUtil;
import com.handshake.basic.raftServer.Node;
import com.handshake.basic.raftServer.proto.*;
import com.handshake.basic.raftServer.service.RaftConsensusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public class RaftConsensusServiceImpl implements RaftConsensusService {

    private static final Logger logger = LoggerFactory.getLogger(RaftConsensusServiceImpl.class);
    public static final ReentrantLock appendLock = new ReentrantLock();
    //set latency
    public static final ConcurrentHashMap<String,Integer> latencyMap = new ConcurrentHashMap<>();

    private final Node node = SpringContextUtil.getBean(Node.class);

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
            logger.info("Get command from {}",param.getLeaderId());
            appendLock.lock();
            if(param.getEntries() != null && !param.getEntries().isEmpty()){
                for(LogEntry logEntry:param.getEntries()){
                    if(logEntry.getCommand() != null){
                        logEntry.getCommand().execute();
                    }
                }
            }

            //success
            return new AppendEntriesResult(0, true);
        }
        finally {
            appendLock.unlock();
        }

    }
}
