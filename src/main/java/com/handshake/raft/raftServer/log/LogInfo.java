package com.handshake.raft.raftServer.log;

import com.handshake.raft.raftServer.proto.LogEntry;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogInfo {

    private int currentTerm;
    private String votedFor;
    private int commitIndex = 0;
    private int lastApplied = 0;
    private HashMap<Integer,LogEntry> logEntries = new HashMap<>();

}
