package com.handshake.raft.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;

@Data
@ToString
@Builder
public class AppendEntriesParam {

    Long term;
    String leaderId;
    Long prevLogIndex;
    ArrayList<LogEntry> entries;
    Long leaderCommit;

}
