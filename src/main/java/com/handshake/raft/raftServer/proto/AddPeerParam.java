package com.handshake.raft.raftServer.proto;

import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
@Builder
public class AddPeerParam {

    String peerIp;
    String peerSpringAddress;

}
