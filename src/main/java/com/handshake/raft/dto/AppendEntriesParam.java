package com.handshake.raft.dto;

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
    Long prevLogIndex;
    ArrayList<LogEntry> entries;
    int leaderCommit;

}
