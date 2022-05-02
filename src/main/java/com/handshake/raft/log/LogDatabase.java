package com.handshake.raft.log;

import com.handshake.raft.dto.LogEntry;

import java.util.concurrent.CopyOnWriteArrayList;

public interface LogDatabase {

    public void saveToLocal(CopyOnWriteArrayList<LogEntry> logEntries);

    public CopyOnWriteArrayList<LogEntry> readFromLocal();
}
