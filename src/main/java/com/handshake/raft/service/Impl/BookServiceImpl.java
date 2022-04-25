package com.handshake.raft.service.Impl;

import com.handshake.raft.common.exceptions.*;
import com.handshake.raft.common.utils.Json;
import com.handshake.raft.dao.BookInfo;
import com.handshake.raft.service.BookService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.Date;

/**
 * <p>
 *  service implementation
 *  for get book name, author and index
 * </p>
 *
 * @author Lingxiao
 * @since 2022-04-25
 */
@Service
public class BookServiceImpl implements BookService {

    private static final String infoFile = "info.json";

    /**
     * create a book
     * @param name the name of the book
     * @param author the author of the book
     */
    @Override
    public boolean createBook(String name, String author) {
        File file = new File("library/" + name);
        if(file.exists()){
            throw new BookAlreadyExistException();
        }
        if(!file.mkdir()){
            throw new BookFailCreateException();
        }

        //store basic information for the book
        BookInfo bookInfo = new BookInfo(name, author);
        File bookInfoFile = new File("library/" + name + "/" + infoFile);
        try {
            Json.getInstance().writeValue(bookInfoFile.getAbsoluteFile(), bookInfo);
        } catch (IOException e) {
            file.delete();
            e.printStackTrace();
            throw new BookFailCreateException();
        }
        return true;
    }

    /**
     * get the basic information of a book
     * @param name the name of the book
     */
    @Override
    public BookInfo getBookInfo(String name) {
        File bookInfoFile = new File("library/" + name + "/" + infoFile);
        System.out.println(bookInfoFile.getAbsolutePath());
        if(!bookInfoFile.exists()){
            throw new BookNotExistException();
        }
        BookInfo bookInfo = null;
        try {
            bookInfo = Json.getInstance().readValue(bookInfoFile.getAbsoluteFile(), BookInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
            throw new BookInfoFailReadException();
        }
        return bookInfo;
    }

    /**
     * get the index of a book
     * @param name the name of the book
     */
    @Override
    public String[] getBookIndex(String name) {
        File file = new File("library/" + name);
        if(!file.exists()){
            throw new BookNotExistException();
        }
        return file.list();
    }

    /**
     * get the chapter of a book
     * @param name the name of the book
     * @param chapter the name of the chapter
     * @return response with file
     */
    @Override
    public ResponseEntity<FileSystemResource> getChapter(String name, String chapter) {
        File chapterFile = new File("library/" + name + "/" + chapter);
        if(!chapterFile.exists()){
            throw new ChapterNotExistException();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Content-Disposition", "attachment; filename=" + chapterFile.getName());
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");
        headers.add("Last-Modified", new Date().toString());
        headers.add("ETag", String.valueOf(System.currentTimeMillis()));
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(chapterFile.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(new FileSystemResource(chapterFile));
    }

    /**
     * upload chapter
     * @param name the name of the book
     * @param chapter the name of the chapter
     * @param file file to upload
     * @return InputStreamResource of chapter file
     */
    @Override
    public boolean uploadChapter(String name, String chapter, MultipartFile file) {
        File bookChapterFile = new File("library/" + name + "/" + chapter);
        try {
            file.transferTo(bookChapterFile.getAbsoluteFile());
        } catch (IOException e) {
            e.printStackTrace();
            throw new ChapterFailUploadException();
        }
        return true;
    }
}
