package com.handshake.raft.raftServer.log;

import com.handshake.raft.raftServer.LifeCycle;
import com.handshake.raft.raftServer.proto.LogEntry;

import java.util.ArrayList;

/**
 * <p>
 *  LogSystem
 * </p>
 *
 * @author Lingxiao
 */
public interface LogSystem extends LifeCycle {

    public int getCommitIndex();

    public void setCommitIndex(int commitIndex);

    public int getLastApplied();

    public void write(LogEntry logEntry);

    public LogEntry read(int index);

    /**
     * remove from index(include the index itself)
     */
    public void removeFromIndex(int index);

    /**
     * get all log above given index(include the index itself)
     */
    public ArrayList<LogEntry> getLogFromIndex(int index);

    /**
     * get last by the size of map
     * need change when snapshot installed
     * @return logEntry with biggest entry
     */
    public LogEntry getLast();

    public int getLastIndex();

    public int getLastTerm();

    public void applyLog();

    public void store();

    public int getCurrentTerm();

    public String getVotedFor();
}
