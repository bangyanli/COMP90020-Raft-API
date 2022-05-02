package com.handshake.raft.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.handshake.raft.service.BookService;
import com.handshake.raft.service.Impl.BookServiceImpl;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateBookCommand implements Command{

    public static BookService bookService = new BookServiceImpl();

    private String name;
    private String author;

    @JsonCreator
    public CreateBookCommand(@JsonProperty("name")String name,
                             @JsonProperty("author")String author) {
        this.name = name;
        this.author = author;
    }

    @Override
    public void excute() {
        bookService.createBook(name,author);
    }

    @Override
    public String toString() {
        return "CreateBookCommand{" +
                "name='" + name + '\'' +
                ", author='" + author + '\'' +
                '}';
    }
}
