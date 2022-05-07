package com.handshake.raft.raftServer.proto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.handshake.raft.raftServer.proto.Impl.ChangeConfigurationCommand;
import com.handshake.raft.raftServer.proto.Impl.CreateBookCommand;
import com.handshake.raft.raftServer.proto.Impl.UploadChapterCommand;

import java.io.Serializable;

/**
 * <p>
 *  Command interface that each command can be executed
 * </p>
 *
 * @author Lingxiao
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateBookCommand.class, name = "createBookCommand"),
        @JsonSubTypes.Type(value = UploadChapterCommand.class, name = "uploadChapterCommand"),
        @JsonSubTypes.Type(value = ChangeConfigurationCommand.class, name = "changeConfigurationCommand")})
public interface Command extends Serializable {

    public void execute();

    public String toString();

}
