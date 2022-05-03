package com.handshake.raft.raftServer.log;

import com.handshake.raft.raftServer.log.Impl.LogDatabaseImpl;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public interface LogDatabase {

    public void saveToLocal(LogInfo logInfo, ReentrantReadWriteLock lock);

    public LogInfo readFromLocal(ReentrantReadWriteLock lock);
}
