package com.handshake.raft.controller;

import com.handshake.raft.common.exceptions.BookAlreadyExistException;
import com.handshake.raft.common.exceptions.BookFailCreateException;
import com.handshake.raft.common.exceptions.BookNotExistException;
import com.handshake.raft.common.response.ResponseResult;
import com.handshake.raft.common.utils.SpringContextUtil;
import com.handshake.raft.config.LibraryConfig;
import com.handshake.raft.config.NodeConfig;
import com.handshake.raft.dao.BookInfo;
import com.handshake.raft.raftServer.Node;
import com.handshake.raft.raftServer.Status;
import com.handshake.raft.raftServer.proto.Command;
import com.handshake.raft.raftServer.proto.Impl.CreateBookCommand;
import com.handshake.raft.raftServer.proto.Impl.UploadChapterCommand;
import com.handshake.raft.service.BookService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

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
        //sanity check
        File file = new File(libraryConfig.getAddress() + "/" + name);
        if(file.exists()){
            throw new BookAlreadyExistException();
        }
        if(node.getNodeStatus() != Status.LEADER){
            //RETURN LEADER
            return ResponseResult.fail(nodeConfig.getSpringAddress(node.getLeaderId()), HttpStatus.SEE_OTHER);
        }
        CreateBookCommand createBookCommand = CreateBookCommand.builder()
                .name(name)
                .author(author)
                .category(category)
                .description(description)
                .build();
        if(node.replication(createBookCommand)){
            return ResponseResult.suc("Create book successfully!", true);
        }else {
            return ResponseResult.fail("Fail to replicate to other server! time out!");
        }

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
        //sanity check
        if(!new File(libraryConfig.getAddress() + "/" + name).exists()){
            throw new BookNotExistException();
        }
        if(node.getNodeStatus() != Status.LEADER){
            //RETURN LEADER
            return ResponseResult.fail(nodeConfig.getSpringAddress(node.getLeaderId()), HttpStatus.SEE_OTHER);
        }
        UploadChapterCommand uploadChapterCommand = UploadChapterCommand.builder()
                .name(name)
                .chapter(chapter)
                .file(file)
                .build();
        if(node.replication(uploadChapterCommand)){
            return ResponseResult.suc("Upload chapter successfully!", true);
        }else {
            return ResponseResult.fail("Fail to replicate to other server! time out!");
        }
    }

}
