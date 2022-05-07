package com.handshake.basic.common.constants;

/**
 * <p>
 *  Exception Message Constants
 * </p>
 * @author Lingxiao Li
 * @since 2021-09-18
 */
public final class ExceptionMessageConstants {

    //exception messages
    public static final String BOOK_ALREADY_EXIST_EXCEPTION = "Book already exist.";
    public static final String BOOK_NOT_EXIST_EXCEPTION = "Book does not exist.";
    public static final String CHAPTER_NOT_EXIST_EXCEPTION = "This chapter does not exist.";

    //IO exception
    public static final String FAIL_TO_CREATE_BOOK_EXCEPTION = "Fail to add this book!";
    public static final String FAIL_TO_READ_BOOK_INFO_EXCEPTION = "Fail to read the information of this book!";
    public static final String FAIL_TO_READ_CHAPTER_EXCEPTION = "Fail to read the chapter!";
    public static final String FAIL_TO_UPLOAD_CHAPTER_EXCEPTION = "Fail to upload the chapter!";

    private ExceptionMessageConstants() {
    }
}
