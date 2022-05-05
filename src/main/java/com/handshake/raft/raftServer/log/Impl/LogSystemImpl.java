package com.handshake.raft.raftServer.log.Impl;

import com.handshake.raft.common.utils.SpringContextUtil;
import com.handshake.raft.raftServer.Node;
import com.handshake.raft.raftServer.log.LogDatabase;
import com.handshake.raft.raftServer.log.LogInfo;
import com.handshake.raft.raftServer.log.LogSystem;
import com.handshake.raft.raftServer.proto.LogEntry;
import com.handshake.raft.service.Impl.WebSocketServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class LogSystemImpl implements LogSystem {

    private static Logger logger = LoggerFactory.getLogger(LogSystemImpl.class);

    public final static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private HashMap<Integer,LogEntry> logEntries = null;

    private volatile int commitIndex = 0;
    private volatile int lastApplied = 0;

    //use for store persistent state
    private int currentTerm;
    private String votedFor;

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
        currentTerm = logInfo.getCurrentTerm();
        votedFor = logInfo.getVotedFor();
        logEntries = logInfo.getLogEntries();
        commitIndex = logInfo.getCommitIndex();
        lastApplied = logInfo.getLastApplied();
    }

    @Override
    public void stop() {
        logger.debug("Stopping {}", LogSystemImpl.class);
        store();
        logger.debug("Stopped {}", LogSystemImpl.class);
    }

    @Override
    public void write(LogEntry logEntry) {

        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        try{
            writeLock.lock();

            if(logEntries.get(logEntry.getIndex()) != null){
                logger.warn("Write illegal index: " + logEntry.getIndex() + " when index already exist!");
            }
            logEntries.put(logEntry.getIndex(),logEntry);
        }
        finally {
            writeLock.unlock();
        }
    }

    @Override
    public LogEntry read(int index) {
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        LogEntry logEntry = null;
        try{
            readLock.lock();
            if(index == 0){
                return null;
            }
            logEntry = logEntries.get(index);
            if(logEntry == null){
                logger.debug("Read illegal index: " + index + " when index do not exist!");
            }
        }
        finally {
            readLock.unlock();
        }
        return logEntry;
    }

    @Override
    public void removeFromIndex(int index) {

        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        try{
            writeLock.lock();
            for (int i = index;i<=logEntries.size();i++){
                LogEntry remove = logEntries.remove(i);
                if(remove == null){
                    logger.warn("log entry with commit Index do not exist!");
                }
            }
        }
        finally {
            writeLock.unlock();
        }
    }

    @Override
    public ArrayList<LogEntry> getLogFromIndex(int index) {
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        ArrayList<LogEntry> logEntryArrayList = new ArrayList<>();
        try{
            readLock.lock();
            for(int i=index;i<=logEntries.size();i++){
                LogEntry logEntry = logEntries.get(i);
                if(logEntry == null){
                    logger.warn("Read illegal index: " + i + " when index do not exist! {}", "getLogFromIndex");
                }
                else {
                    logEntryArrayList.add(logEntry);
                }
            }
        }
        finally {
            readLock.unlock();
        }
        return logEntryArrayList;
    }

    @Override
    public LogEntry getLast() {
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        LogEntry logEntry = null;
        try {
            readLock.lock();
            logEntry = logEntries.get(logEntries.size());
            if(logEntry == null){
                logger.warn("log entry with commit Index do not exist!");
            }
        }
        finally {
            readLock.unlock();
        }
        return logEntry;
    }

    @Override
    public int getLastIndex() {
        if(logEntries.size() != 0){
            LogEntry last = getLast();
            return last.getIndex();
        }else {
            return 0;
        }
    }

    @Override
    public int getLastTerm() {
        if(logEntries.size() != 0){
            LogEntry last = getLast();
            return last.getTerm();
        }else {
            return 0;
        }
    }

    @Override
    public void applyLog() {
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        try{
            readLock.lock();
            WebSocketServer webSocketServer = SpringContextUtil.getBean(WebSocketServer.class);
            for(int i = lastApplied+1; i <= commitIndex; i++){
                LogEntry logEntry = logEntries.get(i);
                if(logEntry != null){
                    if(logEntry.getCommand() != null) {
                        logger.info("Apply {}",logEntry.toString());
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
            lastApplied = commitIndex;
        }
        finally {
            readLock.unlock();
        }
    }

    @Override
    public void store() {
        Node node = SpringContextUtil.getBean(Node.class);
        logDatabase.saveToLocal(new LogInfo(node.getCurrentTerm(),node.getVotedFor(),commitIndex,lastApplied,logEntries), lock);
    }

    @Override
    public int getCurrentTerm() {
        return currentTerm;
    }

    @Override
    public String getVotedFor() {
        return votedFor;
    }
}
