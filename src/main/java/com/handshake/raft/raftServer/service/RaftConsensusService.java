package com.handshake.raft.raftServer.service;

import com.handshake.raft.raftServer.proto.*;

import java.util.ArrayList;

public interface RaftConsensusService {

    AppendEntriesResult appendEntries(AppendEntriesParam param) throws InterruptedException;

    RequestVoteResult requestVote(RequestVoteParam param) throws InterruptedException;

    AddPeerResult addPeer(AddPeerParam addPeerParam) throws InterruptedException;

    RemovePeerResult removePeer(RemovePeerParam removePeerParam) throws InterruptedException;
}
