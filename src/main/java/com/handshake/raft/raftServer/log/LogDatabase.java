package com.handshake.raft.raftServer.log;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * <p>
 *  LogDatabase
 * </p>
 *
 * @author Lingxiao
 */
public interface LogDatabase {

    public void saveToLocal(LogInfo logInfo, ReentrantReadWriteLock lock);

    public LogInfo readFromLocal(ReentrantReadWriteLock lock);
}
