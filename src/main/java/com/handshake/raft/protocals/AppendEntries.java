package com.handshake.raft.protocals;

import com.handshake.raft.dto.AppendEntriesParam;
import com.handshake.raft.dto.AppendEntriesResult;

public interface AppendEntries {

    AppendEntriesResult receiveAppendEntries(AppendEntriesParam param);

    Boolean sendAppendEntries(AppendEntriesParam param);

}
