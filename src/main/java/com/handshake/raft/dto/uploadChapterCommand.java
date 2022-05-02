package com.handshake.raft.dto;

import com.handshake.raft.service.BookService;
import com.handshake.raft.service.Impl.BookServiceImpl;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@Builder
public class uploadChapterCommand implements Command{

    private String name;
    private String chapter;
    private MultipartFile file;

    public static BookService bookService = new BookServiceImpl();

    @Override
    public void excute() {
        bookService.uploadChapter(name,chapter,file);
    }

    @Override
    public String toString() {
        return "uploadChapterCommand{" +
                "name='" + name + '\'' +
                ", chapter='" + chapter + '\'' +
                ", file=" + file +
                '}';
    }
}
