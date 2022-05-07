package com.handshake.basic.common.exceptions;


import com.handshake.basic.common.constants.ExceptionMessageConstants;
import com.handshake.basic.common.response.ResponseResult;

public class BookInfoFailReadException extends BaseException{

    public BookInfoFailReadException() {
        super(ResponseResult.fail(ExceptionMessageConstants.FAIL_TO_READ_BOOK_INFO_EXCEPTION));
    }

}
