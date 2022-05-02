package com.handshake.raft.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class RequestVoteResult {

    int term;
    Boolean voteGranted;

}
