package com.handshake.basic.service;

import com.handshake.basic.dao.BookInfo;
import org.springframework.web.multipart.MultipartFile;

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
    public boolean createBook(String name, String author, String category, String description);

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
     * @return string
     */
    public String getChapter(String name, String chapter);

    /**
     * upload chapter
     * @param name the name of the book
     * @param chapter the name of the chapter
     * @param file file to upload
     * @return InputStreamResource of chapter file
     */
    public boolean uploadChapter(String name, String chapter, MultipartFile file);

    /**
     * upload chapter
     * @param name the name of the book
     * @param chapter the name of the chapter
     * @param file file to upload
     * @return InputStreamResource of chapter file
     */
    public boolean uploadChapter(String name, String chapter, String file);
}
