package com.handshake.basic.raftServer.proto.Impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.handshake.basic.common.exceptions.BaseException;
import com.handshake.basic.raftServer.proto.Command;
import com.handshake.basic.service.BookService;
import com.handshake.basic.service.Impl.BookServiceImpl;
import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Data
@Builder
public class UploadChapterCommand implements Command {

    private static final Logger logger = LoggerFactory.getLogger(UploadChapterCommand.class);

    private String name;
    private String chapter;
    private String file;

    public static BookService bookService = new BookServiceImpl();

    @JsonCreator
    public UploadChapterCommand(@JsonProperty("name")String name,
                                @JsonProperty("chapter")String chapter,
                                @JsonProperty("file")String file) {
        this.name = name;
        this.chapter = chapter;
        this.file = file;
    }

    public UploadChapterCommand(String name, String chapter, MultipartFile file) {
        this.name = name;
        this.chapter = chapter;
        try {
            this.file = new String(file.getBytes());
        } catch (IOException e) {
            logger.warn(e.getMessage(),e);
        }
        ;
    }

    @Override
    public void execute() {
        try {
            bookService.uploadChapter(name,chapter,file);
        }
        catch (BaseException e){

        }
    }

    @Override
    public String toString() {
        return "UploadChapterCommand{" +
                "name='" + name + '\'' +
                ", chapter='" + chapter + '\'' +
                '}';
    }
}
