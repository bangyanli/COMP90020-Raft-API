package com.handshake.basic.controller;

import com.handshake.basic.common.response.ResponseResult;
import com.handshake.basic.config.LibraryConfig;
import com.handshake.basic.config.NodeConfig;
import com.handshake.basic.dao.BookInfo;
import com.handshake.basic.raftServer.Node;
import com.handshake.basic.raftServer.proto.Impl.CreateBookCommand;
import com.handshake.basic.raftServer.proto.Impl.UploadChapterCommand;
import com.handshake.basic.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * <p>
 *  book controller
 * </p>
 *
 * @author Lingxiao
 * @since 2022-04-25
 */
@CrossOrigin
@RestController
@RequestMapping("/library/book")
public class BookController {

    @Autowired
    public BookService bookService;

    @Autowired
    public Node node;

    @Autowired
    public LibraryConfig libraryConfig;

    @Autowired
    public NodeConfig nodeConfig;

    @GetMapping(value = "/{name}")
    public ResponseResult<Object> getBookInfo(@PathVariable("name") String name){
        BookInfo bookInfo = bookService.getBookInfo(name);
        return ResponseResult.suc("Get info successfully!", bookInfo);
    }
    @PostMapping(value = "/{name}")
    public ResponseResult<Object> postBook(@PathVariable("name") String name,
                                           @RequestParam("author") String author,
                                           @RequestParam("category") String category,
                                           @RequestParam("description") String description){
        bookService.createBook(name,author,category,description);
        CreateBookCommand createBookCommand = CreateBookCommand.builder()
                .name(name)
                .author(author)
                .category(category)
                .description(description)
                .build();
        node.replication(createBookCommand);
        return ResponseResult.suc("Create book successfully!", true);

    }

    @GetMapping(value = "/{name}/index")
    public ResponseResult<Object> getBookIndex(@PathVariable("name") String name){
        String[] bookIndex = bookService.getBookIndex(name);
        return ResponseResult.suc("Create book successfully!", bookIndex);
    }

    @GetMapping(value = "/{name}/{chapter}")
    public ResponseResult<Object> getBookChapter(@PathVariable("name") String name,
                                                             @PathVariable("chapter") String chapter){
        String chapterString = bookService.getChapter(name, chapter);
        return ResponseResult.suc("Get book chapter successfully!", chapterString);
    }

    @PostMapping(value = "/{name}/{chapter}")
    public ResponseResult<Object> uploadBookChapter(@PathVariable("name") String name,
                                                    @PathVariable("chapter") String chapter,
                                                    @RequestPart(name = "file") String file){
        bookService.uploadChapter(name,chapter,file);
        UploadChapterCommand uploadChapterCommand = UploadChapterCommand.builder()
                .name(name)
                .chapter(chapter)
                .file(file)
                .build();
        node.replication(uploadChapterCommand);
        return ResponseResult.suc("Upload chapter successfully!", true);
    }

}
