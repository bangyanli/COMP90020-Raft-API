package com.handshake.raft.raftServer.proto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 *
 * @author Lingxiao
 */
@Data
@ToString
@AllArgsConstructor
public class RequestVoteResult implements Serializable {

    int term;
    Boolean voteGranted;

}
