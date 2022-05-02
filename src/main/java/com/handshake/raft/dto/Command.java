package com.handshake.raft.dto;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.io.Serializable;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = CreateBookCommand.class, name = "createBookCommand"),
        @JsonSubTypes.Type(value = UploadChapterCommand.class, name = "uploadChapterCommand") })
public interface Command extends Serializable {

    public void excute();

    public String toString();

}
