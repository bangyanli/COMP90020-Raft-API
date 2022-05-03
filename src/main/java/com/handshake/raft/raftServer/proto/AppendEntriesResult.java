package com.handshake.raft.raftServer.proto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
public class AppendEntriesResult {

    int term;
    Boolean success;

}
