package com.handshake.basic.raftServer.proto.Impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.handshake.basic.common.exceptions.BaseException;
import com.handshake.basic.raftServer.proto.Command;
import com.handshake.basic.service.BookService;
import com.handshake.basic.service.Impl.BookServiceImpl;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateBookCommand implements Command {

    public static BookService bookService = new BookServiceImpl();

    private String name;
    private String author;
    private String category;
    private String description;

    @JsonCreator
    public CreateBookCommand(@JsonProperty("name")String name,
                             @JsonProperty("author")String author,
                             @JsonProperty("category")String category,
                             @JsonProperty("description")String description) {
        this.name = name;
        this.author = author;
        this.category = category;
        this.description = description;
    }

    @Override
    public void execute() {
        try{
            bookService.createBook(name,author,category,description);
        }
        catch (BaseException e){

        }
    }

    @Override
    public String toString() {
        return "CreateBookCommand{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}
