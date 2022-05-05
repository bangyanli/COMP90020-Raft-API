package com.handshake.raft.raftServer.proto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class RemovePeerParam {

    String peerIp;
    String peerSpringAddress;

}
