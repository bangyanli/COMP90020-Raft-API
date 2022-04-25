package com.handshake.raft;

public class RaftNode {
    public enum NodeState{
        STATE_FOLLOWER,
        STATE_PRE_CANDIDATE,
        STATE_CANDIDATE,
        STATE_LEADER
    }
    // A follower would become a candidate if it doesn't receive any message
    // from the leader in electionTimeoutMs milliseconds
    private int electionTimeoutMilliseconds = 5000;
    // A leader sends RPCs at least this often, even if there is no data to send
    private int heartbeatPeriodMilliseconds = 500;
    private NodeState state = NodeState.STATE_FOLLOWER;
    private long currentTerm;
    private int votedFor;
    private int leaderId;
    private long commitIndex;
    private volatile long lastAppliedIndex;

}
