package com.handshake.raft.service;

import com.handshake.raft.dao.BookInfo;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * <p>
 *  service
 *  for get book name, author and index
 * </p>
 *
 * @author Lingxiao
 * @since 2022-04-25
 */
public interface BookService {

    /**
     * create a book
     * @param name the name of the book
     * @param author the author of the book
     */
    public boolean createBook(String name, String author);

    /**
     * get the basic information of a book
     * @param name the name of the book
     */
    public BookInfo getBookInfo(String name);

    /**
     * get the index of a book
     * @param name the name of the book
     */
    public String[] getBookIndex(String name);

    /**
     * get the chapter of a book
     * @param name the name of the book
     * @param chapter the name of the chapter
     * @return response with file
     */
    public ResponseEntity<FileSystemResource> getChapter(String name, String chapter);

    /**
     * upload chapter
     * @param name the name of the book
     * @param chapter the name of the chapter
     * @param file file to upload
     * @return InputStreamResource of chapter file
     */
    public boolean uploadChapter(String name, String chapter, MultipartFile file);
}
