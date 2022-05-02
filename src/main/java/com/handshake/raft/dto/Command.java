package com.handshake.raft.dto;

import java.io.Serializable;

public interface Command extends Serializable {

    public void excute();

    public String toString();

}
