package com.handshake.raft.raftServer.proto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;


@Data
@ToString
@Builder
public class RequestVoteParam {

    int term;
    String candidateId;
    Integer lastLogIndex;
    Integer lastLogTerm;

}
