package com.handshake.raft.protocals;

import com.handshake.raft.dto.RequestVoteParam;
import com.handshake.raft.dto.RequestVoteResult;

public interface RequestVote {

    RequestVoteResult receiveRequestVote(RequestVoteParam param);

    Boolean sendRequestVote(RequestVoteParam param);

}
