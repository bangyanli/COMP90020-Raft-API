package com.handshake.raft.raftServer;

/**
 * <p>
 *  Status of raft node
 * </p>
 *
 * @author Lingxiao
 */
public enum Status {
    FOLLOWER,
    CANDIDATE,
    LEADER
}
