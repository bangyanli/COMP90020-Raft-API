package com.handshake.raft.dto;


import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class LogEntry implements Serializable{

    private Long index;

    private Long term;

    private Command command;


}
