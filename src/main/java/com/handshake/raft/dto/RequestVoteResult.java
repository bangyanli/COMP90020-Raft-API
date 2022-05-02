package com.handshake.raft.dto;

import com.handshake.raft.rpc.Request;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

@Data
@ToString
@Builder
public class RequestVoteResult implements Serializable {

    Long term;
    Boolean voteGranted;

    RequestVoteResult(Long term,Boolean voteGranted){
        this.term = term;
        this.voteGranted = voteGranted;
    }

}
