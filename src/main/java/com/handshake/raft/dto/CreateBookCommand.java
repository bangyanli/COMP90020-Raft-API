package com.handshake.raft.dto;

import com.handshake.raft.service.BookService;
import com.handshake.raft.service.Impl.BookServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class CreateBookCommand implements Command{

    public static BookService bookService = new BookServiceImpl();

    private String name;
    private String author;

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
