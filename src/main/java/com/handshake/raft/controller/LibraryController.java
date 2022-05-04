package com.handshake.raft.controller;

import com.handshake.raft.common.response.ResponseResult;
import com.handshake.raft.service.LibraryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  Library controller
 * </p>
 *
 * @author Lingxiao
 * @since 2022-04-25
 */
@CrossOrigin
@RestController
@RequestMapping("/library")
public class LibraryController {

    @Autowired
    public LibraryService libraryService;

    @GetMapping
    public ResponseResult<Object> getLibraryCatalog() {
        String[] libraryCatalog = libraryService.getLibraryCatalog();
        return ResponseResult.suc("Get library index success", libraryCatalog);
    }


}
