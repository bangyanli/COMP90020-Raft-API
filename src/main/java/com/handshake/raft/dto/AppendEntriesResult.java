package com.handshake.raft.dto;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class AppendEntriesResult {

    Long term;
    Boolean success;

}
