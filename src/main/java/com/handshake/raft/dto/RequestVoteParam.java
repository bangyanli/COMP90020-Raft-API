package com.handshake.raft.dto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.ArrayList;

@Data
@ToString
@Builder
public class RequestVoteParam {

    Long term;
    String candidateId;
    Long lastLogIndex;
    Long lastLogTerm;

}
