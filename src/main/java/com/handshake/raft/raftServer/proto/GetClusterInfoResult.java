package com.handshake.raft.raftServer.proto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 *
 * @author Lingxiao
 */
@Data
@ToString
@Builder
@AllArgsConstructor
public class GetClusterInfoResult {

    boolean success;
    ServerInfo serverInfo;
    String leaderId;
}
