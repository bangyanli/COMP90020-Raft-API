package com.handshake.raft.raftServer.log;

import com.handshake.raft.raftServer.proto.LogEntry;

public interface LogSystem {

    public void add();

    public LogEntry read(Long index);

    public void removeLastIndex();

    public LogEntry getLast();

    public void applyLog();
}
