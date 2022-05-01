package com.handshake.raft.controller;

import com.handshake.raft.common.response.ResponseResult;
import com.handshake.raft.dao.BookInfo;
import com.handshake.raft.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *  book controller
 * </p>
 *
 * @author Lingxiao
 * @since 2022-04-25
 */
@RestController
@RequestMapping("/library/book")
public class BookController {

    @Autowired
    public BookService bookService;

    @GetMapping(value = "/{name}")
    public ResponseResult<Object> getBookInfo(@PathVariable("name") String name){
        BookInfo bookInfo = bookService.getBookInfo(name);
        return ResponseResult.suc("Get info successfully!", bookInfo);
    }

    @PostMapping(value = "/{name}")
    public ResponseResult<Object> postBook(@PathVariable("name") String name,
                                           @RequestParam("author") String author){
        boolean book = bookService.createBook(name, author);
        return ResponseResult.suc("Create book successfully!", book);
    }

    @GetMapping(value = "/{name}/index")
    public ResponseResult<Object> getBookIndex(@PathVariable("name") String name){
        String[] bookIndex = bookService.getBookIndex(name);
        return ResponseResult.suc("Create book successfully!", bookIndex);
    }

    @GetMapping(value = "/{name}/{chapter}")
    public ResponseEntity<FileSystemResource> getBookChapter(@PathVariable("name") String name,
                                                             @PathVariable("chapter") String chapter){
        return bookService.getChapter(name,chapter);
    }

    @PostMapping(value = "/{name}/{chapter}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseResult<Object> uploadBookChapter(@PathVariable("name") String name,
                                                    @PathVariable("chapter") String chapter,
                                                    @RequestPart(name = "file" ,required = true) MultipartFile file){
        bookService.uploadChapter(name,chapter,file);
        return ResponseResult.suc("Upload chapter successfully!", null);
    }

}
