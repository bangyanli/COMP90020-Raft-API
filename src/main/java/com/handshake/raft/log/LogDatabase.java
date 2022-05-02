package com.handshake.raft.log;

import com.handshake.raft.dto.LogEntry;

import java.util.ArrayList;

public interface LogDatabase {

    public void saveToLocal(ArrayList<LogEntry> logEntries);

    public ArrayList<LogEntry> readFromLocal();
}
