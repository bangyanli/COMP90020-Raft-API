package com.handshake.raft.raftServer.log.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.handshake.raft.common.utils.Json;
import com.handshake.raft.common.utils.SpringContextUtil;
import com.handshake.raft.config.NodeConfig;
import com.handshake.raft.raftServer.log.LogDatabase;
import com.handshake.raft.raftServer.log.LogInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class LogDatabaseImpl implements LogDatabase {

    private static final Logger logger = LoggerFactory.getLogger(LogDatabaseImpl.class);

    public void saveToLocal(LogInfo logInfo, ReentrantReadWriteLock lock){
        NodeConfig nodeConfig = SpringContextUtil.getBean(NodeConfig.class);
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        try {
            writeLock.lock();
            Json.getInstance().writeValue(new File(nodeConfig.getLog()),logInfo);
        } catch (IOException e) {
            logger.warn(e.getMessage(),e);
        }
        finally {
            writeLock.unlock();
        }
    }

    public LogInfo readFromLocal(ReentrantReadWriteLock lock){
        NodeConfig nodeConfig = SpringContextUtil.getBean(NodeConfig.class);
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        LogInfo logInfo = new LogInfo();
        try {
            readLock.lock();
            logInfo = Json.getInstance().readValue(new File(nodeConfig.getLog()),
                    new TypeReference<LogInfo>() {
            });
        } catch (IOException e) {
            //logger.warn(e.getMessage(),e);
            logger.warn("Cannot read log from file!");
        }
        finally {
            readLock.unlock();
        }
        return logInfo;
    }

}
