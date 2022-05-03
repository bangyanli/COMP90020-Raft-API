package com.handshake.raft.raftServer.proto;

import com.handshake.raft.rpc.Request;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@Builder
public class RequestVoteResult implements Serializable {

    int term;
    Boolean voteGranted;

    RequestVoteResult(Long term,Boolean voteGranted){
        this.term = term;
        this.voteGranted = voteGranted;
    }

}
