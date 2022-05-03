package com.handshake.raft.raftServer.log;

import com.handshake.raft.raftServer.proto.LogEntry;

import java.util.concurrent.CopyOnWriteArrayList;

public interface LogDatabase {

    public void saveToLocal(CopyOnWriteArrayList<LogEntry> logEntries);

    public CopyOnWriteArrayList<LogEntry> readFromLocal();
}
