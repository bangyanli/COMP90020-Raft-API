package com.handshake.raft.service.Impl;

import com.handshake.raft.common.exceptions.*;
import com.handshake.raft.common.utils.Json;
import com.handshake.raft.common.utils.SpringContextUtil;
import com.handshake.raft.config.LibraryConfig;
import com.handshake.raft.dao.BookInfo;
import com.handshake.raft.service.BookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
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

    @Autowired
    LibraryConfig libraryConfig;

    /**
     * create a book
     * @param name the name of the book
     * @param author the author of the book
     */
    @Override
    public boolean createBook(String name, String author, String category, String description) {
        LibraryConfig libraryConfig = SpringContextUtil.getBean(LibraryConfig.class);
        File file = new File(libraryConfig.getAddress() + "/" + name);
        if(file.exists()){
            throw new BookAlreadyExistException();
        }
        if(!file.mkdir()){
            throw new BookFailCreateException();
        }

        //store basic information for the book
        BookInfo bookInfo = new BookInfo(name, author, category, description);
        File bookInfoFile = new File(libraryConfig.getAddress() + "/" + name + "/" + infoFile);

        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            Json.getInstance().writeValue(bookInfoFile.getAbsoluteFile(), bookInfo);
        } catch (IOException e) {
            bookInfoFile.delete();
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
        File bookInfoFile = new File(libraryConfig.getAddress() + "/" + name + "/" + infoFile);
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
        File file = new File(libraryConfig.getAddress() + "/" + name);
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
     * @return String
     */
    @Override
    public String getChapter(String name, String chapter) {
        //sanity check
        if(!new File(libraryConfig.getAddress() + "/" + name).exists()){
            throw new BookNotExistException();
        }
        File chapterFile = new File(libraryConfig.getAddress() + "/" + name + "/" + chapter);
        if(!chapterFile.exists()){
            throw new ChapterNotExistException();
        }

        ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
        readLock.lock();
        String str = null;
        try {
            str = Files.readString(chapterFile.toPath());
        } catch (IOException e) {
            throw new ChapterFailReadException();
        }
        readLock.unlock();
        logger.info("successfully download chapter " + chapter + " of " + name);
        return str;
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
        LibraryConfig libraryConfig = SpringContextUtil.getBean(LibraryConfig.class);
        //sanity check
        if(!new File(libraryConfig.getAddress() + "/" + name).exists()){
            throw new BookNotExistException();
        }

        File bookChapterFile = new File(libraryConfig.getAddress() + "/" + name + "/" + chapter);

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

    /**
     * upload chapter
     * @param name the name of the book
     * @param chapter the name of the chapter
     * @param file file to upload
     * @return InputStreamResource of chapter file
     */
    @Override
    public boolean uploadChapter(String name, String chapter, String file) {
        LibraryConfig libraryConfig = SpringContextUtil.getBean(LibraryConfig.class);
        //sanity check
        if(!new File(libraryConfig.getAddress() + "/" + name).exists()){
            throw new BookNotExistException();
        }

        File bookChapterFile = new File(libraryConfig.getAddress() + "/" + name + "/" + chapter);

        ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();
        writeLock.lock();
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(bookChapterFile));
            writer.write(file);
            writer.close();
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
