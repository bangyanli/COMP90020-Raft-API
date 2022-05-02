package com.handshake.raft.raftServer;

import com.handshake.raft.config.NodeConfig;
import com.handshake.raft.log.LogSystem;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Getter
@Setter
public class Node {

    @Autowired
    private NodeConfig nodeConfig;
    private LogSystem log;
    private volatile Status nodeStatus;
    private volatile long electionTime;
    private volatile long lastElectionTime;
    private volatile int term;
}
