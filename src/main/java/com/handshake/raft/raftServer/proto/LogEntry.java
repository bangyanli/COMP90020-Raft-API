package com.handshake.raft.raftServer.proto;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 *
 * @author Lingxiao
 */
@Data
@Builder
public class LogEntry implements Serializable{

    private int index;

    private int term;

    private Command command;

    @JsonCreator
    public LogEntry(@JsonProperty("index")int index,
                    @JsonProperty("term")int term,
                    @JsonProperty("command")Command command) {
        this.index = index;
        this.term = term;
        this.command = command;
    }
}
