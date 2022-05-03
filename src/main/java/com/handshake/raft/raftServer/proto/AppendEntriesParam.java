package com.handshake.raft.raftServer.proto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;

@Data
@ToString
@Builder
public class AppendEntriesParam {

    int term;
    String leaderId;
    int prevLogIndex;
    int prevLogTerm;
    ArrayList<LogEntry> entries;
    int leaderCommit;

}
