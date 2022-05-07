package com.handshake.basic.raftServer.service;

import com.handshake.basic.raftServer.proto.AppendEntriesParam;
import com.handshake.basic.raftServer.proto.AppendEntriesResult;

public interface RaftConsensusService {

    AppendEntriesResult appendEntries(AppendEntriesParam param) throws InterruptedException;
}
