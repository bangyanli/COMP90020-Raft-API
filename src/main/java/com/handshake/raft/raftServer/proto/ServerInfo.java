package com.handshake.raft.raftServer.proto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * use to add peer and remove peer
 * @author lingxiao
 */
@Data
@AllArgsConstructor
public class ServerInfo implements Serializable {

    private ArrayList<String> servers;
    private ArrayList<String> serversSpringAddress;

}
