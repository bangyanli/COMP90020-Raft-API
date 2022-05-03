package com.handshake.raft.raftServer.service;

import com.handshake.raft.raftServer.proto.AppendEntriesParam;
import com.handshake.raft.raftServer.proto.AppendEntriesResult;
import com.handshake.raft.raftServer.proto.RequestVoteParam;
import com.handshake.raft.raftServer.proto.RequestVoteResult;

public interface RaftConsensusService {

    AppendEntriesResult receiveAppendEntries(AppendEntriesParam param);

    Boolean replication(AppendEntriesParam param);

    RequestVoteResult receiveRequestVote(RequestVoteParam param);

    Boolean startElection(RequestVoteParam param);
}
