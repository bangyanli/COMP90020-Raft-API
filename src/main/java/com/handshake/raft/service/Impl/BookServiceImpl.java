package com.handshake.raft.service.Impl;

import com.handshake.raft.common.exceptions.*;
import com.handshake.raft.common.utils.Json;
import com.handshake.raft.dao.BookInfo;
import com.handshake.raft.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.locks.ReentrantReadWriteLock;

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

    private static final Logger logger = LoggerFactory.getLogger(BookServiceImpl.class);

    private static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

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

        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            Json.getInstance().writeValue(bookInfoFile.getAbsoluteFile(), bookInfo);
        } catch (IOException e) {
            file.delete();
            e.printStackTrace();
            writeLock.unlock();
            throw new BookFailCreateException();
        }
        writeLock.unlock();

        logger.info("successfully create book: " + name + " with author " + author);
        return true;
    }

    /**
     * get the basic information of a book
     * @param name the name of the book
     */
    @Override
    public BookInfo getBookInfo(String name) {
        File bookInfoFile = new File("library/" + name + "/" + infoFile);
        if(!bookInfoFile.exists()){
            throw new BookNotExistException();
        }
        BookInfo bookInfo = null;
        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        readLock.lock();
        try {
            bookInfo = Json.getInstance().readValue(bookInfoFile.getAbsoluteFile(), BookInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
            readLock.unlock();
            throw new BookInfoFailReadException();
        }
        readLock.unlock();
        logger.info("successfully get book info: " + bookInfo);
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
        logger.info("successfully get index of " + name);
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
        //sanity check
        if(!new File("library/" + name).exists()){
            throw new BookNotExistException();
        }
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

        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        readLock.lock();
        FileSystemResource resource = new FileSystemResource(chapterFile);
        readLock.unlock();
        logger.info("successfully download chapter " + chapter + " of " + name);
        return ResponseEntity
                .ok()
                .headers(headers)
                .contentLength(chapterFile.length())
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
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
        //sanity check
        if(!new File("library/" + name).exists()){
            throw new BookNotExistException();
        }

        File bookChapterFile = new File("library/" + name + "/" + chapter);

        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            file.transferTo(bookChapterFile.getAbsoluteFile());
        } catch (IOException e) {
            //e.printStackTrace();
            writeLock.unlock();
            throw new ChapterFailUploadException();
        }
        writeLock.unlock();
        logger.info("successfully upload chapter " + chapter + " of " + name);
        return true;
    }
}
