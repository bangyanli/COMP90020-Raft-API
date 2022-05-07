package com.handshake.basic.raftServer.proto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.handshake.basic.raftServer.proto.Impl.CreateBookCommand;
import com.handshake.basic.raftServer.proto.Impl.UploadChapterCommand;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateBookCommand.class, name = "createBookCommand"),
        @JsonSubTypes.Type(value = UploadChapterCommand.class, name = "uploadChapterCommand")})
public interface Command extends Serializable {

    public void execute();

    public String toString();

}
