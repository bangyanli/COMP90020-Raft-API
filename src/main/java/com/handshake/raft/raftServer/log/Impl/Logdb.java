package com.handshake.raft.raftServer.log.Impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.handshake.raft.common.utils.Json;
import com.handshake.raft.raftServer.proto.LogEntry;
import com.handshake.raft.raftServer.log.LogDatabase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Repository
public class Logdb implements LogDatabase {

    private static final Logger logger = LoggerFactory.getLogger(Logdb.class);

    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void saveToLocal(CopyOnWriteArrayList<LogEntry> logEntries){
        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            Json.getInstance().writeValue(new File("log.json"),logEntries);
        } catch (IOException e) {
            logger.warn(e.getMessage(),e);
        }
        finally {
            writeLock.unlock();
        }
    }

    public CopyOnWriteArrayList<LogEntry> readFromLocal(){
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        readLock.lock();
        CopyOnWriteArrayList<LogEntry> logEntries = new CopyOnWriteArrayList<>();
        try {
            logEntries = Json.getInstance().readValue(new File("log.json"),
                    new TypeReference<CopyOnWriteArrayList<LogEntry>>() {
            });
        } catch (IOException e) {
            logger.warn(e.getMessage(),e);
        }
        finally {
            readLock.unlock();
        }
        return logEntries;
    }
}
