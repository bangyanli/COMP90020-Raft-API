package com.handshake.raft.raftServer;

/**
 * <p>
 *  current role of node(same as status)
 * </p>
 *
 * @author Lingxiao
 */
public interface Role extends LifeCycle{

    public Status getName();
}
