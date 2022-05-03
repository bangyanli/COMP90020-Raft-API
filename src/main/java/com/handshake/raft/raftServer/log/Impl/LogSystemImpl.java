package com.handshake.raft.raftServer.log.Impl;

import com.handshake.raft.raftServer.log.LogDatabase;
import com.handshake.raft.raftServer.log.LogInfo;
import com.handshake.raft.raftServer.log.LogSystem;
import com.handshake.raft.raftServer.proto.LogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class LogSystemImpl implements LogSystem {

    private static Logger logger = LoggerFactory.getLogger(LogSystemImpl.class);

    public final static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private HashMap<Integer,LogEntry> logEntries = null;

    private volatile int commitIndex = 0;
    private volatile int lastApplied = 0;

    @Autowired
    private LogDatabase logDatabase;

    @Override
    public int getCommitIndex() {
        return commitIndex;
    }

    @Override
    public void setCommitIndex(int commitIndex) {
        this.commitIndex = commitIndex;
    }

    @Override
    public int getLastApplied() {
        return lastApplied;
    }

    @Override
    public void init() {
        LogInfo logInfo = logDatabase.readFromLocal(lock);
        logEntries = logInfo.getLogEntries();
        commitIndex = logInfo.getCommitIndex();
        lastApplied = logInfo.getLastApplied();
    }

    @Override
    public void stop() {
        logDatabase.saveToLocal(new LogInfo(commitIndex,lastApplied,logEntries), lock);
    }

    @Override
    public void write(LogEntry logEntry) {

        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();

        if(logEntries.get(logEntry.getIndex()) != null){
            logger.warn("Write illegal index: " + logEntry.getIndex() + " when index already exist!");
        }
        logEntries.put(logEntry.getIndex(),logEntry);

        writeLock.unlock();

    }

    @Override
    public LogEntry read(int index) {
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        readLock.lock();

        LogEntry logEntry = logEntries.get(index);
        if(logEntry == null){
            logger.warn("Read illegal index: " + index + " when index do not exist!");
        }
        readLock.unlock();
        return logEntry;
    }

    @Override
    public void removeFromIndex(int index) {

        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        for (int i = index;i<logEntries.size();i++){
            LogEntry remove = logEntries.remove(i);
            if(remove == null){
                logger.warn("log entry with commit Index do not exist!");
            }
        }
        writeLock.unlock();

    }


    @Override
    public LogEntry getLast() {
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        readLock.lock();

        LogEntry logEntry = logEntries.get(logEntries.size());
        if(logEntry == null){
            logger.warn("log entry with commit Index do not exist!");
        }
        readLock.unlock();
        return logEntry;
    }

    @Override
    public void applyLog(int index) {

        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        readLock.lock();
        for(int i = lastApplied+1; i <= index; i++){
            LogEntry logEntry = logEntries.get(i);
            if(logEntry != null){
                if(logEntry.getCommand() != null) {
                    logEntry.getCommand().execute();
                }
                else {
                    logger.warn("When apply log, the command of log " + logEntry.getIndex() + " is null!");
                }
            }
            else {
                logger.warn("When apply log, log " + logEntry.getIndex() + " is null!");
            }
        }
        readLock.unlock();

    }
}
