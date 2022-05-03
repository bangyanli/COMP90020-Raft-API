package com.handshake.raft.raftServer.proto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AppendEntriesResult {

    int term;
    Boolean success;

}
