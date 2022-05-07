package com.handshake.raft.raftServer.proto;

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
public class RemovePeerParam {

    String peerIp;
    String peerSpringAddress;

}
