package com.handshake.raft.raftServer.service.Impl;

import com.handshake.raft.raftServer.proto.AppendEntriesParam;
import com.handshake.raft.raftServer.proto.AppendEntriesResult;
import com.handshake.raft.raftServer.proto.RequestVoteParam;
import com.handshake.raft.raftServer.proto.RequestVoteResult;
import com.handshake.raft.raftServer.service.RaftConsensusService;

public class RaftConsensusServiceImpl implements RaftConsensusService {

    @Override
    public AppendEntriesResult appendEntries(AppendEntriesParam param) {
        return null;
    }


    @Override
    public RequestVoteResult requestVote(RequestVoteParam param) {
        return null;
    }

}
