package com.handshake.basic.common.exceptions;


import com.handshake.basic.common.constants.ExceptionMessageConstants;
import com.handshake.basic.common.response.ResponseResult;

public class BookNotExistException extends BaseException{

    public BookNotExistException() {
        super(ResponseResult.fail(ExceptionMessageConstants.BOOK_NOT_EXIST_EXCEPTION));
    }

}
