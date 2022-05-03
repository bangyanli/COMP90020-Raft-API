package com.handshake.raft.raftServer.log.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.handshake.raft.common.utils.Json;
import com.handshake.raft.raftServer.log.LogInfo;
import com.handshake.raft.raftServer.proto.LogEntry;
import com.handshake.raft.raftServer.log.LogDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class LogDatabaseImpl implements LogDatabase {

    private static final Logger logger = LoggerFactory.getLogger(LogDatabaseImpl.class);

    public void saveToLocal(LogInfo logInfo, ReentrantReadWriteLock lock){
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            Json.getInstance().writeValue(new File("log.json"),logInfo);
        } catch (IOException e) {
            logger.warn(e.getMessage(),e);
        }
        finally {
            writeLock.unlock();
        }
    }

    public LogInfo readFromLocal(ReentrantReadWriteLock lock){
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        readLock.lock();
        LogInfo logInfo = new LogInfo();
        try {
            logInfo = Json.getInstance().readValue(new File("log.json"),
                    new TypeReference<LogInfo>() {
            });
        } catch (IOException e) {
            logger.warn(e.getMessage(),e);
        }
        finally {
            readLock.unlock();
        }
        return logInfo;
    }

}
