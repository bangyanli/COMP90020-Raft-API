package com.handshake.raft.protocals;

import com.handshake.raft.dto.LogEntry;

public interface LogSystem {

    public void add();

    public LogEntry read(Long index);

    public void removeLastIndex();

    public LogEntry getLast();

    public void applyLog();
}
